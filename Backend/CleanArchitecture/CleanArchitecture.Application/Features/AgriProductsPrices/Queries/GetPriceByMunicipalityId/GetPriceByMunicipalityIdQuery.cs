using System.Collections.Generic;
using CleanArchitecture.Core.Exceptions;
using CleanArchitecture.Core.Interfaces.Repositories;
using CleanArchitecture.Core.Entities;
using MediatR;
using System.Threading;
using System.Threading.Tasks;
using CleanArchitecture.Core.Features.UserSupportMessages.Queries.GetMessageByUserId;
using CleanArchitecture.Core.Wrappers;

namespace CleanArchitecture.Core.Features.AgriProductsPrices.Queries.GetPriceByMunicipalityId
{
    public class GetPriceByMunicipalityIdQuery : IRequest<PagedResponse<Entities.AgriProductsPrices>>
    {
        public int MunicipalityId { get; set; }
        public int PageNumber { get; set; }
        public int PageSize { get; set; }
    }
    
    public class GetPriceByMunicipalityIdQueryHandler : IRequestHandler<GetPriceByMunicipalityIdQuery, PagedResponse<Entities.AgriProductsPrices>>
    {
        private readonly IAgriProductsPricesRepositoryAsync _agriProductsPricesRepository;

        public GetPriceByMunicipalityIdQueryHandler(IAgriProductsPricesRepositoryAsync agriProductsPricesRepository)
        {
            _agriProductsPricesRepository = agriProductsPricesRepository;
        }

        public async Task<PagedResponse<Entities.AgriProductsPrices>> Handle(GetPriceByMunicipalityIdQuery request, CancellationToken cancellationToken)
        {
            var pagedList = await _agriProductsPricesRepository
                .GetPagedPricesByMunicipalityIdAsync(request.MunicipalityId, request.PageNumber, request.PageSize);

            return new PagedResponse<Entities.AgriProductsPrices>(pagedList, request.PageNumber, request.PageSize);
        }
    }

    
}