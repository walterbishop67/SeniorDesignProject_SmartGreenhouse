using System;
using System.Security.Claims;
using System.Threading.Tasks;
using CleanArchitecture.Core.Features.ElectronicCard.Commands.UpdateProduct;
using CleanArchitecture.Core.Features.Greenhouses.Command.CreateGreenhouse;
using CleanArchitecture.Core.Features.Greenhouses.Command.DeleteGreenhouseById;
using CleanArchitecture.Core.Features.Greenhouses.Queries;
using CleanArchitecture.Core.Features.Greenhouses.Queries.GetGreenhouseById;
using CleanArchitecture.Core.Features.Greenhouses.Queries.GetGreenhouseById2;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace CleanArchitecture.WebApi.Controllers.v1
{
    [ApiVersion("1.0")]
    [Authorize]
    public class GreenhouseController : BaseApiController
    {   
        /*
        [HttpPost]
        public async Task<IActionResult> Post(CreateGreenhouseCommand command)
        {
            return Ok(await Mediator.Send(command));
        }
        */
        [HttpPost]
        public async Task<IActionResult> Post(CreateGreenhouseCommand command)
        {
            // Sera oluşturma komutunu çalıştır
            var greenhouseResult = await Mediator.Send(command);
    
            // Eğer sera başarıyla oluşturulduysa ve bir ID döndüyse
            if (greenhouseResult != null && greenhouseResult is int greenhouseId && greenhouseId > 0)
            {
                // Kart durumunu güncellemek için komutu hazırla
                var updateStatusCommand = new UpdateElectronicCardStatus
                {
                    GreenHouseId = greenhouseId,
                    CardId = int.Parse(command.Code), // CardId'yi CreateGreenhouseCommand'dan almanız gerekecek
                    // Diğer gerekli parametreler
                };
        
                try
                {
                    // Kart durumunu güncelle
                    var statusUpdateResult = await Mediator.Send(updateStatusCommand);
            
                    // İşlem sonucunu birleştirerek dön
                    return Ok(new 
                    {
                        GreenhouseId = greenhouseId,
                        CardStatus = statusUpdateResult > 0 ? "Updated" : "Not Updated"
                    });
                }
                catch (Exception ex)
                {
                    // Hata durumunu ele al
                    // Sera oluştu ancak kart güncellemesi başarısız oldu
                }
            }
    
            // Sadece sera oluşturma sonucunu dön
            return Ok(greenhouseResult);
        }

        [HttpGet]
        public async Task<IActionResult> Get()
        {
            var userId = User.FindFirstValue("uid"); //"a9c55f47-2725-4bf5-a544-1d319068cfeb"; //User.FindFirstValue(ClaimTypes.NameIdentifier);
            var result = await Mediator.Send(new GetGreenhousesByUserIdQuery { UserId = userId });
            return Ok(result);
        }
        /*
        // DELETE api/<controller>/5
        [HttpDelete("{id}")]
        public async Task<IActionResult> Delete(int id)
        {
            return Ok(await Mediator.Send(new DeleteGreenhouseByIdCommand { Id = id }));
        }
        */
        [HttpDelete("{id}")]
        public async Task<IActionResult> Delete([FromRoute] int id)
        {
            // Önce sera bilgisini al
            var forid = new GetGreenhouseById
            {
                GreenhouseId = id.ToString()
            };

            var greenhouse = await Mediator.Send(forid);

            if (greenhouse == null)
            {
                return NotFound(new { Message = "Greenhouse not found." });
            }

            // Silme işlemini gerçekleştir
            var deleteResult = await Mediator.Send(new DeleteGreenhouseByIdCommand { Id = id });

            if (deleteResult > 0)
            {
                // Kart güncelleme komutunu oluştur
                var updateStatusCommand = new UpdateElectronicCardStatus2
                {
                    CardId = int.Parse(greenhouse.ProductCode), // Code burada ProductCode oluyor
                    // Diğer gerekli alanları da doldur
                };

                try
                {
                    var statusUpdateResult = await Mediator.Send(updateStatusCommand);

                    return Ok(new
                    {
                        DeleteResult = deleteResult,
                        StatusUpdateResult = statusUpdateResult,
                        Message = "Greenhouse deleted and card status updated successfully."
                    });
                }
                catch (Exception ex)
                {
                    return Ok(new
                    {
                        DeleteResult = deleteResult,
                        Message = "Greenhouse deleted but card status update failed: " + ex.Message
                    });
                }
            }

            return Ok(new { DeleteResult = deleteResult });
        }

    }
}