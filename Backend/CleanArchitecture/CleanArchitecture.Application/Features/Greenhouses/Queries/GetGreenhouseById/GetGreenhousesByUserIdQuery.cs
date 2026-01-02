using System.Collections.Generic;
using CleanArchitecture.Core.Exceptions;
using CleanArchitecture.Core.Interfaces.Repositories;
using CleanArchitecture.Core.Entities;
using MediatR;
using System.Threading;
using System.Threading.Tasks;

namespace CleanArchitecture.Core.Features.Greenhouses.Queries.GetGreenhouseById
{
    public class GetGreenhousesByUserIdQuery : IRequest<List<Greenhouse>>
    {
        public string UserId { get; set; }
    }
    
    public class GetGreenhousesByUserIdQueryHandler : IRequestHandler<GetGreenhousesByUserIdQuery, List<Greenhouse>>
    {
        private readonly IGreenhouseRepositoryAsync _greenhouseRepository;

        public GetGreenhousesByUserIdQueryHandler(IGreenhouseRepositoryAsync greenhouseRepository)
        {
            _greenhouseRepository = greenhouseRepository;
        }

        public async Task<List<Greenhouse>> Handle(GetGreenhousesByUserIdQuery request, CancellationToken cancellationToken)
        {
            return await _greenhouseRepository.GetByUserIdAsync(request.UserId);
        }
    }
    
}