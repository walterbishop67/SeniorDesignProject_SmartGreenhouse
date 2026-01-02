﻿using System.Collections.Generic;
using System.Linq;
using System.Security.Claims;
using CleanArchitecture.Core.Features.Municipality.Commands;
using CleanArchitecture.Core.Features.Municipality.Commands.UpdateMunicipality;
using CleanArchitecture.Core.Wrappers;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using System.Threading.Tasks;
using CleanArchitecture.Core.Entities;
using CleanArchitecture.Core.Features.Municipality.Commands.CreateMunicipality;
using CleanArchitecture.Core.Features.Municipality.Commands.DeleteMunicipalityById;
using CleanArchitecture.Core.Features.Municipality.Queries.GetAllMunicipalities;
using CleanArchitecture.Core.Features.Municipality.Queries.GetMunicipalityById;
using MediatR;

namespace CleanArchitecture.WebApi.Controllers.v1
{
    [ApiVersion("1.0")]
    [Authorize]
    [Route("api/[controller]")]
    public class MunicipalityController : BaseApiController
    {
        [HttpPost]
        public async Task<IActionResult> Post(CreateMunicipalityCommand command)
        {
            return Ok(await Mediator.Send(command));
        }

        [HttpGet]
        public async Task<IActionResult> Get()
        {
            return Ok(await Mediator.Send(new GetAllMunicipalitiesQuery{}));
        }
        
        [HttpGet("{id}")]
        public async Task<IActionResult> Get(int id)
        {
            return Ok(await Mediator.Send(new GetMunicipalityByIdQuery { Id = id }));
        }
        
        [HttpPut("{id}")]
        public async Task<IActionResult> Put(int id, UpdateMunicipalityCommand command)
        {
            if (id != command.Id)
            {
                return BadRequest();
            }
            return Ok(await Mediator.Send(command));
        }
        
        [HttpDelete("{id}")]
        public async Task<IActionResult> Delete(int id)
        {
            return Ok(await Mediator.Send(new DeleteMunicipalityByIdCommand { Id = id }));
        }
        
    }
}