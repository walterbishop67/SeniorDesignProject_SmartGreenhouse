using CleanArchitecture.Core.Exceptions;
using CleanArchitecture.Core.Interfaces.Repositories;
using CleanArchitecture.Core.Entities;
using MediatR;
using System.Threading;
using System.Threading.Tasks;

namespace CleanArchitecture.Core.Features.Municipality.Queries.GetMunicipalityById
{
    public class GetMunicipalityByIdQuery : IRequest<Entities.Municipality>
    {
        public int Id { get; set; }
        
        public class GetMunicipalityByIdQueryHandler : IRequestHandler<GetMunicipalityByIdQuery, Entities.Municipality>
        {
            private readonly IMunicipalityRepositoryAsync _municipalityRepository;
            public GetMunicipalityByIdQueryHandler(IMunicipalityRepositoryAsync municipalityRepository)
            {
                _municipalityRepository = municipalityRepository;
            }
            public async Task<Entities.Municipality> Handle(GetMunicipalityByIdQuery query, CancellationToken cancellationToken)
            {
                var municipality = await _municipalityRepository.GetByIdAsync(query.Id);
                if (municipality == null) throw new ApiException($"Municipality Not Found.");
                return municipality;
            }
        }
    }
}