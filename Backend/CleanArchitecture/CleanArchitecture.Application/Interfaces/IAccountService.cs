using CleanArchitecture.Core.DTOs.Account;
using CleanArchitecture.Core.DTOs.Email;
using CleanArchitecture.Core.Wrappers;
using System.Threading.Tasks;
using CleanArchitecture.Core.DTOs.Users;

namespace CleanArchitecture.Core.Interfaces
{
    public interface IAccountService
    {

        Task<AuthenticationResponse> AuthenticateAsync(AuthenticationRequest request, string ipAddress);
        Task<string> RegisterAsync(RegisterRequest request, string origin);
        Task<string> ConfirmEmailAsync(string userId, string code);
        Task<EmailRequest> ForgotPassword(ForgotPasswordRequest model, string origin);
        bool ValidateResetCode(string email, string code);
        Task<string> ResetPasswordWithCode(ResetPasswordWithCodeRequest.ResetPasswordWithCodeRequestDto model);
        Task<string> ResetPassword(ResetPasswordRequest model);
        Task<string> ChangePasswordAsync(string userId, string currentPassword, string newPassword, string confirmNewPassword);
        Task<bool> UpdateUserNameAsync(string userId, string newUserName);
    
    }
}
