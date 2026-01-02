using System.Security.Claims;
using System.Threading.Tasks;
using CleanArchitecture.Core.Features.Greenhouses.Queries.GetGreenhouseById;
using CleanArchitecture.Core.Features.UserSupportMessages.Commands.CreateMessage;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using CleanArchitecture.Core.Features.UserSupportMessages.Queries.GetAllMessages;
using CleanArchitecture.Core.Features.UserSupportMessages.Queries.GetMessageById;
using CleanArchitecture.Core.Features.UserSupportMessages.Commands.DeleteMessageById;
using CleanArchitecture.Core.Features.UserSupportMessages.Queries.GetMessageByUserId;
using CleanArchitecture.Core.Features.UserSupportMessages.Commands.UpdateMessage;
using CleanArchitecture.Core.Wrappers;
using Microsoft.AspNetCore.Http;


namespace CleanArchitecture.WebApi.Controllers.v1
{
    [ApiVersion("1.0")]
    [Authorize]
    public class UserSupportMessageController : BaseApiController
    {
        [HttpPost]
        public async Task<IActionResult> Post(CreateMessageCommand command)
        {
            return Ok(await Mediator.Send(command));
        }
        
        [HttpGet("all")]
        [ProducesResponseType(StatusCodes.Status200OK, Type = typeof(PagedResponse<GetAllMessagesViewModel>))]
        public async Task<PagedResponse<GetAllMessagesViewModel>> Get([FromQuery] GetAllMessagesParameter filter)
        {
            return await Mediator.Send(new GetAllMessagesQuery() 
            { 
                PageSize = filter.PageSize, 
                PageNumber = filter.PageNumber,
                OnlyUnopened = filter.OnlyUnopened // Pass this parameter
            });
        }
        
        [HttpGet("user")]
        public async Task<IActionResult> Get()
        {
            var userId = User.FindFirstValue("uid"); //"a9c55f47-2725-4bf5-a544-1d319068cfeb"; //User.FindFirstValue(ClaimTypes.NameIdentifier);
            var result = await Mediator.Send(new GetMessageByUserIdQuery { UserId = userId });
            return Ok(result);
        }
            
        [HttpGet("{id}")]
        public async Task<IActionResult> Get(int id)
        {
            return Ok(await Mediator.Send(new GetMessageByIdQuery { Id = id }));
        }
        
        [HttpDelete("{id}")]
        public async Task<IActionResult> Delete(int id)
        {
            return Ok(await Mediator.Send(new DeleteMessageByIdCommand { Id = id }));
        }
        
        // PUT api/<controller>/5
        [HttpPut("{id}")]
        public async Task<IActionResult> Put(int id, UpdateMessageCommand command)
        {
            if (id != command.Id)
            {
                return BadRequest();
            }
            return Ok(await Mediator.Send(command));
        }
        
    }
}