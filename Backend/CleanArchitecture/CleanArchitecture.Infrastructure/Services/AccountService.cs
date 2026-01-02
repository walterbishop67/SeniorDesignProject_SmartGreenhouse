using CleanArchitecture.Core.DTOs.Account;
using CleanArchitecture.Core.DTOs.Email;
using CleanArchitecture.Core.Enums;
using CleanArchitecture.Core.Exceptions;
using CleanArchitecture.Core.Interfaces;
using CleanArchitecture.Core.Settings;
using CleanArchitecture.Core.Wrappers;
using CleanArchitecture.Infrastructure.Helpers;
using CleanArchitecture.Infrastructure.Models;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.WebUtilities;
using Microsoft.Extensions.Options;
using Microsoft.IdentityModel.Tokens;
using System;
using System.Collections.Generic;
using System.IdentityModel.Tokens.Jwt;
using System.Linq;
using System.Security.Claims;
using System.Security.Cryptography;
using System.Text;
using System.Threading.Tasks;
using Microsoft.Extensions.Caching.Memory;

namespace CleanArchitecture.Infrastructure.Services
{
    public class AccountService : IAccountService
    {
        private readonly UserManager<ApplicationUser> _userManager;
        private readonly RoleManager<IdentityRole> _roleManager;
        private readonly SignInManager<ApplicationUser> _signInManager;
        private readonly IEmailService _emailService;
        private readonly JWTSettings _jwtSettings;
        private readonly IDateTimeService _dateTimeService;
        private readonly IMemoryCache _memoryCache;

        public AccountService(UserManager<ApplicationUser> userManager,
            RoleManager<IdentityRole> roleManager,
            IOptions<JWTSettings> jwtSettings,
            IMemoryCache memoryCache,
            IDateTimeService dateTimeService,
            SignInManager<ApplicationUser> signInManager,
            IEmailService emailService)
        {
            _userManager = userManager;
            _roleManager = roleManager;
            _jwtSettings = jwtSettings.Value;
            _dateTimeService = dateTimeService;
            _signInManager = signInManager;
            _memoryCache = memoryCache;
            _emailService = emailService;
        }

        public async Task<AuthenticationResponse> AuthenticateAsync(AuthenticationRequest request, string ipAddress)
        {
            var user = await _userManager.FindByEmailAsync(request.Email);
            if (user == null)
            {
                throw new ApiException($"No Accounts Registered with {request.Email}.");
            }
            var result = await _signInManager.PasswordSignInAsync(user.UserName, request.Password, false, lockoutOnFailure: false);
            if (!result.Succeeded)
            {
                throw new ApiException($"Invalid Credentials for '{request.Email}'.");
            }
            if (!user.EmailConfirmed)
            {
                throw new ApiException($"Account Not Confirmed for '{request.Email}'.");
            }
            JwtSecurityToken jwtSecurityToken = await GenerateJWToken(user);
            AuthenticationResponse response = new AuthenticationResponse();
            response.Id = user.Id;
            response.JWToken = new JwtSecurityTokenHandler().WriteToken(jwtSecurityToken);
            response.Email = user.Email;
            response.UserName = user.UserName;
            var rolesList = await _userManager.GetRolesAsync(user).ConfigureAwait(false);
            response.Roles = rolesList.ToList();
            response.IsVerified = user.EmailConfirmed;
            var refreshToken = GenerateRefreshToken(ipAddress);
            response.RefreshToken = refreshToken.Token;
            return response;
        }

        public async Task<string> RegisterAsync(RegisterRequest request, string origin)
        {
            var userWithSameUserName = await _userManager.FindByNameAsync(request.UserName);
            if (userWithSameUserName != null)
            {
                throw new ApiException($"Username '{request.UserName}' is already taken.");
            }

            var user = new ApplicationUser
            {
                Email = request.Email,
                FirstName = request.FirstName,
                LastName = request.LastName,
                UserName = request.UserName
            };

            var userWithSameEmail = await _userManager.FindByEmailAsync(request.Email);
            if (userWithSameEmail == null)
            {
                var result = await _userManager.CreateAsync(user, request.Password);
                if (result.Succeeded)
                {
                    await _userManager.AddToRoleAsync(user, Roles.Basic.ToString());
                    var verificationUri = await SendVerificationEmail(user, origin);
                    return $"User Registered. Please confirm your account by visiting this URL {verificationUri}";
                }
                else
                {
                    var errors = result.Errors.Select(e => $"{e.Code}: {e.Description}").ToList();
                    throw new ApiException($"User registration failed: {string.Join(", ", errors)}");
                }
            }
            else
            {
                throw new ApiException($"Email {request.Email} is already registered.");
            }
        }

        private async Task<JwtSecurityToken> GenerateJWToken(ApplicationUser user)
        {
            var userClaims = await _userManager.GetClaimsAsync(user);
            var roles = await _userManager.GetRolesAsync(user);

            var roleClaims = new List<Claim>();

            for (int i = 0; i < roles.Count; i++)
            {
                roleClaims.Add(new Claim("roles", roles[i]));
            }

            string ipAddress = IpHelper.GetIpAddress();

            var claims = new[]
            {
                new Claim(JwtRegisteredClaimNames.Sub, user.UserName),
                new Claim(JwtRegisteredClaimNames.Jti, Guid.NewGuid().ToString()),
                new Claim(JwtRegisteredClaimNames.Email, user.Email),
                new Claim("uid", user.Id),
                new Claim("ip", ipAddress)
            }
            .Union(userClaims)
            .Union(roleClaims);

            var symmetricSecurityKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(_jwtSettings.Key));
            var signingCredentials = new SigningCredentials(symmetricSecurityKey, SecurityAlgorithms.HmacSha256);

            var jwtSecurityToken = new JwtSecurityToken(
                issuer: _jwtSettings.Issuer,
                audience: _jwtSettings.Audience,
                claims: claims,
                expires: DateTime.UtcNow.AddMinutes(_jwtSettings.DurationInMinutes),
                signingCredentials: signingCredentials);
            return jwtSecurityToken;
        }

        private string RandomTokenString()
        {
            using var rngCryptoServiceProvider = new RNGCryptoServiceProvider();
            var randomBytes = new byte[40];
            rngCryptoServiceProvider.GetBytes(randomBytes);
            // convert random bytes to hex string
            return BitConverter.ToString(randomBytes).Replace("-", "");
        }

        private async Task<string> SendVerificationEmail(ApplicationUser user, string origin)
        {
            origin = "https://localhost:9001";
            var code = await _userManager.GenerateEmailConfirmationTokenAsync(user);
            code = WebEncoders.Base64UrlEncode(Encoding.UTF8.GetBytes(code));
    
            var route = "api/account/confirm-email/";
            var endpointUri = new Uri(string.Concat($"{origin}/", route));
            var verificationUri = QueryHelpers.AddQueryString(endpointUri.ToString(), "userId", user.Id);
            verificationUri = QueryHelpers.AddQueryString(verificationUri, "code", code);

            // 📧 HTML body with button
            var body = $@"
        <p>Welcome {user.FirstName},</p>
        <p>Please confirm your account by clicking the button below:</p>
        <a href='{verificationUri}' style='
            display: inline-block;
            padding: 10px 20px;
            font-size: 16px;
            color: white;
            background-color: #28a745;
            border-radius: 5px;
            text-decoration: none;
        '>Confirm Email</a>
    ";

            var emailRequest = new EmailRequest
            {
                To = user.Email,
                Subject = "Please Confirm Your Email Address",
                Body = body
            };

            await _emailService.SendAsync(emailRequest);
            return verificationUri;
        }

        public async Task<string> ConfirmEmailAsync(string userId, string code)
        {
            var user = await _userManager.FindByIdAsync(userId);
            code = Encoding.UTF8.GetString(WebEncoders.Base64UrlDecode(code));
            var result = await _userManager.ConfirmEmailAsync(user, code);
            if (result.Succeeded)
            {
                return $"Account Confirmed for {user.Email}. You can now use the /api/Account/authenticate endpoint.";
            }
            else
            {
                throw new ApiException($"An error occured while confirming {user.Email}.");
            }
        }

        private RefreshToken GenerateRefreshToken(string ipAddress)
        {
            return new RefreshToken
            {
                Token = RandomTokenString(),
                Expires = DateTime.UtcNow.AddDays(7),
                Created = DateTime.UtcNow,
                CreatedByIp = ipAddress
            };
        }
        
        private static string GenerateSixDigitCode()
        {
            var random = new Random();
            return random.Next(100000, 999999).ToString(); // 6 haneli sayı
        }

        public async Task<EmailRequest> ForgotPassword(ForgotPasswordRequest model, string origin)
        {
            var account = await _userManager.FindByEmailAsync(model.Email);
            if (account == null)
                throw new ApiException("Kullanıcı bulunamadı");

            var code = GenerateSixDigitCode();

            // 1 dakika süreyle cache'e sakla
            _memoryCache.Set($"ResetCode_{account.Email}", code, TimeSpan.FromMinutes(1));

            // HTML body with code styled as a blue box
            var emailBody = $@"
        <div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>
            <h2 style='color: #333;'>Şifre Sıfırlama</h2>
            <p>Sayın {account.FirstName} {account.LastName},</p>
            <p>Hesabınız için şifre sıfırlama talebinde bulundunuz. Aşağıdaki kodu kullanarak şifrenizi sıfırlayabilirsiniz:</p>
            
            <div style='
                background-color: #e9f7fe;
                border-left: 4px solid #0078d4;
                color: #333;
                font-size: 24px;
                font-weight: bold;
                letter-spacing: 5px;
                margin: 20px 0;
                padding: 15px;
                text-align: center;
            '>{code}</div>
            
            <p><strong>Bu kod 1 dakika içinde geçerliliğini yitirecektir.</strong></p>
            <p>Eğer bu talebi siz yapmadıysanız, lütfen bu e-postayı dikkate almayınız.</p>
            <p>Saygılarımızla,<br>Uygulama Ekibi</p>
        </div>";

            var emailRequest = new EmailRequest
            {
                To = model.Email,
                Subject = "Şifre Sıfırlama Kodu",
                Body = emailBody
            };

            await _emailService.SendAsync(emailRequest);
            return emailRequest;
        }

        public bool ValidateResetCode(string email, string inputCode)
        {
            if (_memoryCache.TryGetValue($"ResetCode_{email}", out string actualCode))
            {
                return actualCode == inputCode;
            }
            return false;
        }

        public async Task<string> ResetPasswordWithCode(ResetPasswordWithCodeRequest.ResetPasswordWithCodeRequestDto model)
        {
            // Kodun geçerli olup olmadığını kontrol et
            if (!ValidateResetCode(model.Email, model.Token))
            {
                throw new ApiException("Geçersiz veya süresi dolmuş kod.");
            }

            var user = await _userManager.FindByEmailAsync(model.Email);
            if (user == null)
            {
                throw new ApiException($"{model.Email} adresiyle kayıtlı bir hesap bulunamadı.");
            }

            // Şifre değiştirme token'ı oluştur
            var resetToken = await _userManager.GeneratePasswordResetTokenAsync(user);
            
            // Şifreyi sıfırla
            var result = await _userManager.ResetPasswordAsync(user, resetToken, model.Password);
            
            if (result.Succeeded)
            {
                // Kullanılmış kodu önbellekten temizle
                _memoryCache.Remove($"ResetCode_{model.Email}");
                return "Şifreniz başarıyla değiştirildi.";
            }
            else
            {
                var errors = result.Errors.Select(e => e.Description).ToList();
                throw new ApiException($"Şifre sıfırlama başarısız: {string.Join(", ", errors)}");
            }
        }
        
        public async Task<string> ResetPassword(ResetPasswordRequest model)
        {
            var account = await _userManager.FindByEmailAsync(model.Email);
            if (account == null) throw new ApiException($"No Accounts Registered with {model.Email}.");
            var result = await _userManager.ResetPasswordAsync(account, model.Token, model.Password);
            if (result.Succeeded)
            {
                return $"Password Resetted.";
            }
            else
            {
                throw new ApiException($"Error occured while reseting the password.");
            }
        }
        
        public async Task<string> ChangePasswordAsync(string userId, string currentPassword, string newPassword, string confirmNewPassword)
        {
            var user = await _userManager.FindByIdAsync(userId);
            if (user == null)
            {
                return "Kullanıcı bulunamadı.";
            }

            if (newPassword != confirmNewPassword)
            {
                return "Yeni şifre ve tekrarı uyuşmuyor.";
            }

            if (currentPassword == newPassword)
            {
                return "Yeni şifre eski şifreyle aynı olamaz.";
            }

            var result = await _userManager.ChangePasswordAsync(user, currentPassword, newPassword);

            if (!result.Succeeded)
            {
                return "Şifre değiştirme başarısız: " + string.Join(", ", result.Errors.Select(e => e.Description));
            }

            return "Şifre başarıyla değiştirildi.";
        }
        
        public async Task<bool> UpdateUserNameAsync(string userId, string newUserName)
        {
            var user = await _userManager.FindByIdAsync(userId);
            if (user == null) return false;

            user.UserName = newUserName;
            user.NormalizedUserName = newUserName.ToUpper();

            var result = await _userManager.UpdateAsync(user);
            return result.Succeeded;
        }
    }
}