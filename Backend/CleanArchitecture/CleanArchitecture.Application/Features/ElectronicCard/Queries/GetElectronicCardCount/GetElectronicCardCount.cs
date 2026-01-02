using System.Threading;
using System.Threading.Tasks;
using CleanArchitecture.Core.DTOs;
using CleanArchitecture.Core.DTOs.ElectronicCard;
using CleanArchitecture.Core.Interfaces.Repositories;
using MediatR;

namespace CleanArchitecture.Core.Features.ElectronicCard.Queries.GetElectronicCardCounts
{
    public class GetElectronicCardCounts : IRequest<ElectronicCardCountDto>
    {
    }

    public class GetElectronicCardCountsQueryHandler : IRequestHandler<GetElectronicCardCounts, ElectronicCardCountDto>
    {
        private readonly IElectronicCardRepositoryAsync _electronicCardRepository;

        public GetElectronicCardCountsQueryHandler(IElectronicCardRepositoryAsync electronicCardRepository)
        {
            _electronicCardRepository = electronicCardRepository;
        }

        public async Task<ElectronicCardCountDto> Handle(GetElectronicCardCounts request, CancellationToken cancellationToken)
        {
            return await _electronicCardRepository.GetAllTypesCardsCountAsync();
        }
    }
}