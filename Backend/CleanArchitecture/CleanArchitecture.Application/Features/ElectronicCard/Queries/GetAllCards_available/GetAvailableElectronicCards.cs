using System.Threading;
using System.Threading.Tasks;
using CleanArchitecture.Core.DTOs.ElectronicCard;
using CleanArchitecture.Core.Interfaces.Repositories;
using MediatR;

namespace CleanArchitecture.Core.Features.ElectronicCard.Queries.GetAllCards_available;

// Updated request to include pagination parameters
public class GetAvailableElectronicCards : IRequest<ElectronicCardListDto> 
{
    public int PageNumber { get; set; } = 1;
    public int PageSize { get; set; } = 10;
}

public class GetAvailableElectronicCardsQueryHandler : IRequestHandler<GetAvailableElectronicCards, ElectronicCardListDto>
{
    private readonly IElectronicCardRepositoryAsync _repository;

    public GetAvailableElectronicCardsQueryHandler(IElectronicCardRepositoryAsync repository)
    {
        _repository = repository;
    }

    public async Task<ElectronicCardListDto> Handle(GetAvailableElectronicCards request, CancellationToken cancellationToken)
    {
        // Use the paginated version of the repository method
        return await _repository.GetAvailableCardsAsync(request.PageNumber, request.PageSize);
    }
}