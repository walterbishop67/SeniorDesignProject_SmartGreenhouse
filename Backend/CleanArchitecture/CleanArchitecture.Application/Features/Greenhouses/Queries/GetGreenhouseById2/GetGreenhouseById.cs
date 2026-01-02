using System.Collections.Generic;
using System.Threading;
using System.Threading.Tasks;
using CleanArchitecture.Core.Entities;
using CleanArchitecture.Core.Features.Greenhouses.Queries.GetGreenhouseById;
using CleanArchitecture.Core.Interfaces.Repositories;
using MediatR;

namespace CleanArchitecture.Core.Features.Greenhouses.Queries.GetGreenhouseById2
{
    public class GetGreenhouseById: IRequest<Greenhouse>
    {
        public string GreenhouseId { get; set; }
    }
    
    public class GetGreenhouseByIdHandler : IRequestHandler<GetGreenhouseById, Greenhouse>
    {
        private readonly IGreenhouseRepositoryAsync _greenhouseRepository;

        public GetGreenhouseByIdHandler(IGreenhouseRepositoryAsync greenhouseRepository)
        {
            _greenhouseRepository = greenhouseRepository;
        }

        public async Task<Greenhouse> Handle(GetGreenhouseById request, CancellationToken cancellationToken)
        {
            return await _greenhouseRepository.GetByIdAsync(int.Parse(request.GreenhouseId));
        }
    }
}

