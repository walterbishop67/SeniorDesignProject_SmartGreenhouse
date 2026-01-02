using System.Collections.Generic;
using System.Threading;
using System.Threading.Tasks;
using CleanArchitecture.Core.DTOs.ElectronicCard;
using CleanArchitecture.Core.Interfaces.Repositories;
using MediatR;

namespace CleanArchitecture.Core.Features.ElectronicCard.Queries.GetAllProducts;


public class GetAllElectronicCards : IRequest<ElectronicCardListDto>
{
    public int PageNumber { get; set; } = 1;
    public int PageSize { get; set; } = 10;
}

public class GetAllElectronicCardsQueryHandler : IRequestHandler<GetAllElectronicCards, ElectronicCardListDto>
{
    private readonly IElectronicCardRepositoryAsync _electronicCardRepository;

    public GetAllElectronicCardsQueryHandler(IElectronicCardRepositoryAsync electronicCardRepository)
    {
        _electronicCardRepository = electronicCardRepository;
    }

    public async Task<ElectronicCardListDto> Handle(GetAllElectronicCards request, CancellationToken cancellationToken)
    {
        return await _electronicCardRepository.GetAllCardsAsync(request.PageNumber, request.PageSize); // GenericRepository'den geliyor
    }
}