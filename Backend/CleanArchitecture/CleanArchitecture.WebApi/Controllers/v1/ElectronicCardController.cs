﻿﻿using System.Security.Claims;
using System.Threading.Tasks;
using CleanArchitecture.Core.Features.ElectronicCard.Commands.CreateElectronicCard;
using CleanArchitecture.Core.Features.ElectronicCard.Commands.UpdateProduct;
using CleanArchitecture.Core.Features.ElectronicCard.Queries.GetAllCards_available;
using CleanArchitecture.Core.Features.ElectronicCard.Queries.GetAllCards_unavailable;
using CleanArchitecture.Core.Features.ElectronicCard.Queries.GetCardsWithError;
using CleanArchitecture.Core.Features.ElectronicCard.Queries.GetElectronicCardById;
using CleanArchitecture.Core.Features.ElectronicCard.Queries.GetElectronicCardsByGreenhouseId;
using CleanArchitecture.Core.Features.ElectronicCards.Queries.GetElectronicCardById;
using CleanArchitecture.Core.Features.Greenhouses.Queries.GetGreenhouseById;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace CleanArchitecture.WebApi.Controllers.v1
{
    [ApiVersion("1.0")]
    [Authorize]
    public class ElectronicCardController : BaseApiController
    {
        
        /*
        [HttpPost("create-electronic-card")]
        public async Task<IActionResult> Post(CreateElectronicCardCommand command)
        {
            return Ok(await Mediator.Send(command));
        }

        // Dilersen kullanıcıya göre filtreli Get ekleyebilirim (örneğin greenhouse id veya user id’ye göre).
        
        
        // GET metodu (UserId'ye göre ElectronicCard'ları al)
        [HttpGet("get-electronic-card-by-user-id")]
        public async Task<IActionResult> Get()
        {
            // UserId'yi otomatik olarak al
            var userId = User.FindFirstValue("uid");

            // GetElectronicCardsByUserIdQuery ile sorguyu gönder
            var query = new GetElectronicCardById.GetElectronicCardsByUserIdQuery { UserId = userId };
            var result = await Mediator.Send(query);

            return Ok(result);
        }
        */

        // İlerleyen zamanlarda GreenhouseId'ye göre filtre eklenebilir.
        // [HttpGet("by-greenhouse/{greenhouseId}")]
        // public async Task<IActionResult> GetByGreenhouseId(int greenhouseId)
        // {
        //     var query = new GetElectronicCardsByGreenhouseIdQuery { GreenhouseId = greenhouseId };
        //     var result = await Mediator.Send(query);
        //     return Ok(result);
        // }
        
        [HttpPut("update-data")]
        public async Task<IActionResult> UpdateData([FromBody] UpdateElectronicCardDataCommand command)
        {
            var result = await Mediator.Send(command);
            if (result == 0)
                return NotFound("ElectronicCard not found");

            return Ok("ElectronicCard updated successfully");
        }
        
        [HttpPost("update-status")]
        public async Task<IActionResult> UpdateCardStatus([FromBody] UpdateElectronicCardStatus command)
        {
            if (command == null || command.CardId <= 0 || command.GreenHouseId <= 0)
            {
                return BadRequest("Invalid input data.");
            }

            var result = await Mediator.Send(command);

            if (result == 0)
                return NotFound("Card not found.");

            return Ok(new { Message = "Electronic card status updated successfully.", CardId = result });
        }
        
        [HttpPost("update-status2")]
        public async Task<IActionResult> UpdateCardStatus2([FromBody] UpdateElectronicCardStatus2 command)
        {
            if (command == null || command.CardId <= 0)
            {
                return BadRequest("Invalid input data.");
            }

            var result = await Mediator.Send(command);

            if (result == 0)
                return NotFound("Card not found.");

            return Ok(new { Message = "Electronic card status updated successfully.", CardId = result });
        }
        // GET metodu (UserId'ye göre ElectronicCard'ları al)
        [HttpGet("get-unavailable-electronic-card-by-user-id")]
        public async Task<IActionResult> GetUnAvailableCards()
        {
            // UserId'yi otomatik olarak al
            var userId = User.FindFirstValue("uid");

            // GetElectronicCardsByUserIdQuery ile sorguyu gönder
            var query = new GetUnavailableCardById.GetUnElectronicCardsByUserIdQuery{ UserId = userId };
            var result = await Mediator.Send(query);

            return Ok(result);
        }
        
        [HttpGet("electronic-card/get-available-electronic-card-by-user-id")]
        public async Task<IActionResult> GetAvailableCards()
        {
            // UserId'yi otomatik olarak al
            var userId = User.FindFirstValue("uid");

            // GetElectronicCardsByUserIdQuery ile sorguyu gönder
            var query = new GetByUserIdAvailableAsync.GetElectronicCardsByUserIdQuery{ UserId = userId };
            var result = await Mediator.Send(query);

            return Ok(result);
        }
        
        [HttpGet("by-greenhouse/{greenhouseId}")]
        public async Task<IActionResult> GetByGreenhouseId(int greenhouseId)
        {
            var userId = User.FindFirstValue("uid");

            var query = new GetElectronicCardsByGreenhouseIdQuery { GreenhouseId = greenhouseId, UserId = userId };
            var result = await Mediator.Send(query);
            return Ok(result);
        }

    }
}