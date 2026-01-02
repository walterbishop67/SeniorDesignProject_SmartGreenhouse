using System.Collections.Generic;
using System.Security.Claims;
using System.Threading.Tasks;
using CleanArchitecture.Core.Entities;
using CleanArchitecture.Core.Features.AgriProductsPrices.Commands.CreatePrice;
using CleanArchitecture.Core.Features.AgriProductsPrices.Queries.GetPriceByMunicipalityId;
using CleanArchitecture.Core.Features.UserSupportMessages.Queries.GetMessageByUserId;
using CleanArchitecture.Core.Interfaces.Repositories;
using CleanArchitecture.Core.Wrappers;
using MediatR;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;

namespace CleanArchitecture.WebApi.Controllers.v1
{
    [ApiVersion("1.0")]
    [Authorize()]
    public class AgriProductsPricesController: BaseApiController
    {
        private readonly IMediator _mediator;

        public AgriProductsPricesController(IMediator mediator)
        {
            _mediator = mediator;
        }
        
        [HttpGet("prices")]
        [ProducesResponseType(StatusCodes.Status200OK, Type = typeof(PagedResponse<AgriProductsPrices>))]
        public async Task<PagedResponse<AgriProductsPrices>> GetPricesByMunicipality(
            [FromQuery] int municipalityId, 
            [FromQuery] int pageNumber = 1, 
            [FromQuery] int pageSize = 10)
        {
            var query = new GetPriceByMunicipalityIdQuery
            {
                MunicipalityId = municipalityId,
                PageNumber = pageNumber,
                PageSize = pageSize
            };

            return await Mediator.Send(query);
        }

    }
}

