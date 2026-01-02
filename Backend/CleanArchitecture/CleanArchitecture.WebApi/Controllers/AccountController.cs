using System;
using CleanArchitecture.Core.DTOs.Account;
using CleanArchitecture.Core.Interfaces;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using System.Linq;
using System.Threading.Tasks;
using CleanArchitecture.Core.DTOs.Users;
using CleanArchitecture.Core.Features.User.GetUserInfoById;
using MediatR;

namespace CleanArchitecture.WebApi.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class AccountController : ControllerBase
    {
        private readonly IAccountService _accountService;
        private readonly IMediator _mediator;

        public AccountController(IAccountService accountService, IMediator mediator)
        {
            _accountService = accountService;
            _mediator = mediator;
        }

        [HttpPost("authenticate")]
        public async Task<IActionResult> AuthenticateAsync(AuthenticationRequest request)
        {
            return Ok(await _accountService.AuthenticateAsync(request, GenerateIPAddress()));
        }

        [HttpPost("register")]
        public async Task<IActionResult> RegisterAsync(RegisterRequest request)
        {
            var origin = Request.Headers["origin"];
            return Ok(await _accountService.RegisterAsync(request, origin));
        }

        [HttpGet("confirm-email")]
        public async Task<IActionResult> ConfirmEmailAsync([FromQuery] string userId, [FromQuery] string code)
        {
            var origin = Request.Headers["origin"].FirstOrDefault() ?? "https://localhost:9001";
            return Ok(await _accountService.ConfirmEmailAsync(userId, code));
        }

        [HttpPost("forgot-password")]
        public async Task<IActionResult> ForgotPassword(ForgotPasswordRequest model)
        {
            var origin = Request.Headers["origin"].FirstOrDefault() ?? "https://localhost:9001";
            var result = await _accountService.ForgotPassword(model, origin);
            return Ok(new { message = "Şifre sıfırlama kodu e-posta adresinize gönderildi", emailSent = result != null });
        }
/*
        [HttpPost("validate-reset-code")]
        public IActionResult ValidateResetCode([FromBody] ValidateResetCodeRequest model)
        {
            var isValid = _accountService.ValidateResetCode(model.Email, model.Code);
            return Ok(new { isValid });
        }
*/
        [HttpPost("reset-password")]
        public async Task<IActionResult> ResetPasswordWithCode([FromBody] ResetPasswordWithCodeRequest.ResetPasswordWithCodeRequestDto model)
        {
            try
            {
                var result = await _accountService.ResetPasswordWithCode(model);
                return Ok(new { success = true, message = result });
            }
            catch (Exception ex)
            {
                return BadRequest(new { success = false, message = ex.Message });
            }
        }
/*
        [HttpPost("reset-password")]
        public async Task<IActionResult> ResetPassword(ResetPasswordRequest model)
        {
            return Ok(await _accountService.ResetPassword(model));
        }
*/
        [HttpPost("change-password")]
        public async Task<IActionResult> ChangePassword([FromBody] ChangePasswordRequest model)
        {
            var userId = User.FindFirst("uid")?.Value;
            if (userId == null) return Unauthorized();

            var result = await _accountService.ChangePasswordAsync(userId, model.CurrentPassword, model.NewPassword, model.ConfirmNewPassword);

            if (result == "Şifre başarıyla değiştirildi.")
            {
                return Ok(new { message = result });
            }

            return BadRequest(new { error = result });
        }

        [HttpPut("update-username")]
        public async Task<IActionResult> UpdateUserName([FromBody] string newUserName)
        {
            var userId = User.FindFirst("uid")?.Value;
            if (string.IsNullOrEmpty(userId))
                return Unauthorized();

            var result = await _accountService.UpdateUserNameAsync(userId, newUserName);

            if (!result)
                return BadRequest("Kullanıcı adı güncellenemedi veya kullanıcı bulunamadı.");

            return Ok("Kullanıcı adı başarıyla güncellendi.");
        }
        
        [HttpGet("get-user-basic-info")]
        public async Task<IActionResult> GetUserBasicInfo()
        {
            var userId = User.FindFirst("uid")?.Value;
            if (string.IsNullOrEmpty(userId))
                return Unauthorized("Giriş yapmış kullanıcı bulunamadı.");

            var query = new GetUserBasicInfoByIdQuery(userId);
            var userInfo = await _mediator.Send(query);

            if (userInfo == null)
                return NotFound("Kullanıcı bulunamadı.");

            return Ok(userInfo);
        }

        private string GenerateIPAddress()
        {
            if (Request.Headers.ContainsKey("X-Forwarded-For"))
                return Request.Headers["X-Forwarded-For"];
            else
                return HttpContext.Connection.RemoteIpAddress.MapToIPv4().ToString();
        }
    }

    public class ValidateResetCodeRequest
    {
        public string Email { get; set; }
        public string Code { get; set; }
    }

    public class ChangePasswordRequest
    {
        public string CurrentPassword { get; set; }
        public string NewPassword { get; set; }
        public string ConfirmNewPassword { get; set; }
    }
}