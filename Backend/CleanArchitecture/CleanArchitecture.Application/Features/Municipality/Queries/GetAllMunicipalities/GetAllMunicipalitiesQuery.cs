using System.Collections.Generic;
using System.Threading;
using System.Threading.Tasks;
using CleanArchitecture.Core.Entities;
using CleanArchitecture.Core.Features.Greenhouses.Queries.GetGreenhouseById;
using CleanArchitecture.Core.Interfaces.Repositories;
using MediatR;

namespace CleanArchitecture.Core.Features.Municipality.Queries.GetAllMunicipalities
{
    public class GetAllMunicipalitiesQuery: IRequest<IReadOnlyList<Entities.Municipality>> { }
    
    public class GetAllMunicipalitiesQueryHandler : IRequestHandler<GetAllMunicipalitiesQuery, IReadOnlyList<Entities.Municipality>>
    {
        private readonly IMunicipalityRepositoryAsync _municipalityRepository;

        public GetAllMunicipalitiesQueryHandler(IMunicipalityRepositoryAsync municipalityRepository)
        {
            _municipalityRepository = municipalityRepository;
        }

        public async Task<IReadOnlyList<Entities.Municipality>> Handle(GetAllMunicipalitiesQuery request, CancellationToken cancellationToken)
        {
            return await _municipalityRepository.GetAllAsync();
        }
    }
}

