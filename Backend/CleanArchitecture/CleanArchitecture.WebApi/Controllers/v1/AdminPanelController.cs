using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using CleanArchitecture.Core.DTOs.ElectronicCard;
using CleanArchitecture.Core.DTOs.Users;
using CleanArchitecture.Core.Entities;
using CleanArchitecture.Core.Features.AdminPanel.GetBasicUsers;
using CleanArchitecture.Core.Features.AdminPanel.GetUserCountByRoles;
using CleanArchitecture.Core.Features.AgriProductsPrices.Commands.CreatePrice;
using CleanArchitecture.Core.Features.ElectronicCard.Commands.CreateElectronicCard;
using CleanArchitecture.Core.Features.ElectronicCard.Queries.GetAllCards_available;
using CleanArchitecture.Core.Features.ElectronicCard.Queries.GetAllCards_unavailable;
using CleanArchitecture.Core.Features.ElectronicCard.Queries.GetAllProducts;
using CleanArchitecture.Core.Features.ElectronicCard.Queries.GetCardByUserName;
using CleanArchitecture.Core.Features.ElectronicCard.Queries.GetCardsWithError;
using CleanArchitecture.Core.Features.ElectronicCard.Queries.GetElectronicCardById;
using CleanArchitecture.Core.Features.ElectronicCard.Queries.GetElectronicCardCounts;
using CleanArchitecture.Core.Features.User.GetUserInfoById;
using CleanArchitecture.Core.Interfaces.Repositories;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace CleanArchitecture.WebApi.Controllers.v1
{
    [ApiController]
    [ApiVersion("1.0")]
    [Route("api/v{version:apiVersion}/[controller]")]
    [Authorize(Roles = "Admin")] // Controller seviyesinde sadece admin'e izin verildi
    public class AdminPanelController : BaseApiController
    {
        private readonly IElectronicCardRepositoryAsync _electronicCardRepository;

        // Inject the repository into the constructor
        public AdminPanelController(IElectronicCardRepositoryAsync electronicCardRepository)
        {
            _electronicCardRepository = electronicCardRepository;
        }
        [HttpGet("electronic-card/get-all-electronic-cards")]
        public async Task<ActionResult<ElectronicCardListDto>> GetAllCards(int pageNumber = 1, int pageSize = 10)
        {
            try
            {
                // Sayfalama işlemi için methodu çağırıyoruz
                var result = await _electronicCardRepository.GetAllCardsAsync(pageNumber, pageSize);
        
                if (result.Cards == null || !result.Cards.Any())
                {
                    return NotFound(new { message = "No electronic cards found." });
                }

                return Ok(result);
            }
            catch (Exception ex)
            {
                return BadRequest(new { message = ex.Message });
            }
        }


        [HttpGet("electronic-card/available")]
        public async Task<ActionResult<ElectronicCardListDto>> GetAvailableCards(int pageNumber = 1, int pageSize = 10)
        {
            try
            {
                var result = await _electronicCardRepository.GetAvailableCardsAsync(pageNumber, pageSize);
                
                if (result.Cards == null || !result.Cards.Any())
                {
                    return NotFound(new { message = "No available electronic cards found." });
                }

                return Ok(result);
            }
            catch (Exception ex)
            {
                return BadRequest(new { message = ex.Message });
            }
        }

        [HttpGet("electronic-card/unavailable")]
        public async Task<ActionResult<ElectronicCardListDto>> GetUnavailableCards(int pageNumber = 1, int pageSize = 10)
        {
            try
            {
                var result = await _electronicCardRepository.GetUnavailableCardsAsync(pageNumber, pageSize);
                
                if (result.Cards == null || !result.Cards.Any())
                {
                    return NotFound(new { message = "No unavailable electronic cards found." });
                }

                return Ok(result);
            }
            catch (Exception ex)
            {
                return BadRequest(new { message = ex.Message });
            }
        }

        [HttpGet("electronic-card/with-error")]
        public async Task<ActionResult<ElectronicCardListDto>> GetCardsWithError(int pageNumber = 1, int pageSize = 10)
        {
            try
            {
                var result = await _electronicCardRepository.GetCardsWithErrorAsync(pageNumber, pageSize);
                
                if (result.Cards == null || !result.Cards.Any())
                {
                    return NotFound(new { message = "No electronic cards with error found." });
                }

                return Ok(result);
            }
            catch (Exception ex)
            {
                return BadRequest(new { message = ex.Message });
            }
        }

        [HttpGet ("users/get-all-users")]
        public async Task<ActionResult<UserListDto>> GetAllUsers([FromQuery] int pageNumber = 1, [FromQuery] int pageSize = 10)
        {
            var query = new GetAllUsers.GetAllUsersQuery
            {
                PageNumber = pageNumber,
                PageSize = pageSize
            };

            var result = await Mediator.Send(query);
            return Ok(result);
        }

        [HttpGet("users/user-stats-count")]
        public async Task<IActionResult> GetUserStats()
        {
            var result = await Mediator.Send(new GetUserCountByRoles());
            return Ok(result);
        }

        [HttpGet("electronic-card/counts")]
        public async Task<IActionResult> GetCardCounts()
        {
            var result = await Mediator.Send(new GetElectronicCardCounts());
            return Ok(result);
        }
        
        [HttpPost("electronic-card/add-card-by-admin")]
        public async Task<IActionResult> AddCardByAdmin([FromBody] AddElectronicCardByAdmin.AddElectronicCardByAdminCommand command)
        {
            try
            {
                var result = await Mediator.Send(command);
                return StatusCode(201, result);
            }
            catch (KeyNotFoundException ex)
            {
                return NotFound(new { message = ex.Message });
            }
            catch (System.Exception ex)
            {
                return BadRequest(new { message = ex.Message });
            }
        }
        /*
        [HttpGet("electronic-card/by-username")]
        public async Task<IActionResult> GetCardsByUserName([FromQuery] string userName)
        {
            try
            {
                
                var query = new GetActiveElectronicCardsByUserIdQuery { UserName = userName };
                var cards = await Mediator.Send(query);

                if (cards == null || !cards.Any())
                {
                    return NotFound(new { message = $"No cards found for user with username '{userName}'" });
                }

                return Ok(cards);
            }
            catch (KeyNotFoundException ex)
            {
                return NotFound(new { message = ex.Message });
            }
            catch (System.Exception ex)
            {
                return BadRequest(new { message = ex.Message });
            }
        }
        */
        [HttpGet("electronic-card/by-user-id/all")]
        public async Task<IActionResult> GetAllCardsByUserId([FromQuery] string userId)
        {
            try
            {
                var query = new GetAllElectronicCardsByUserId.GetAllElectronicCardsByUserIdQuery { UserId = userId };
                var cards = await Mediator.Send(query);

                if (cards == null || !cards.Any())
                {
                    return NotFound(new { message = $"No cards found for user with ID '{userId}'" });
                }

                return Ok(cards);
            }
            catch (Exception ex)
            {
                return BadRequest(new { message = ex.Message });
            }
        }
        
        [HttpPost("electronic-card/add-card-by-user-id")]
        public async Task<IActionResult> AddCardByUserId([FromQuery] AddElectronicCardByUserId.AddElectronicCardByUserIdCommand command)
        {
            try
            {
                var result = await Mediator.Send(command);
                return StatusCode(201, result); // Başarıyla eklenen kart döndürülüyor
            }
            catch (KeyNotFoundException ex)
            {
                return NotFound(new { message = ex.Message }); // Kullanıcı bulunamazsa hata mesajı
            }
            catch (System.Exception ex)
            {
                return BadRequest(new { message = ex.Message }); // Diğer hatalar
            }
        }
        
        [HttpGet("get-user-basic-info")]
        public async Task<IActionResult> GetUserBasicInfo()
        {
            var userId = User.FindFirst("uid")?.Value;
            if (string.IsNullOrEmpty(userId))
                return Unauthorized("Giriş yapmış kullanıcı bulunamadı.");

            var query = new GetUserBasicInfoByIdQuery(userId);
            var userInfo = await Mediator.Send(query);

            if (userInfo == null)
                return NotFound("Kullanıcı bulunamadı.");

            return Ok(userInfo);
        }
        
        [HttpPost("AgriProductsPrices/create")]
        public async Task<IActionResult> Post(CreatePriceCommand command)
        {
            return Ok(await Mediator.Send(command));
        }
        

    }
}