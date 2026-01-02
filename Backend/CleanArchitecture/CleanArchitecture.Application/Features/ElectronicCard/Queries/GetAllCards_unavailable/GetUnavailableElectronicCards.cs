using System.Threading;
using System.Threading.Tasks;
using CleanArchitecture.Core.DTOs.ElectronicCard;
using CleanArchitecture.Core.Interfaces.Repositories;
using MediatR;

namespace CleanArchitecture.Core.Features.ElectronicCard.Queries.GetAllCards_unavailable;

// Updated request to include pagination parameters
public class GetUnavailableElectronicCards : IRequest<ElectronicCardListDto> 
{
    public int PageNumber { get; set; } = 1;
    public int PageSize { get; set; } = 10;
}

public class GetUnavailableElectronicCardsQueryHandler : IRequestHandler<GetUnavailableElectronicCards, ElectronicCardListDto>
{
    private readonly IElectronicCardRepositoryAsync _repository;

    public GetUnavailableElectronicCardsQueryHandler(IElectronicCardRepositoryAsync repository)
    {
        _repository = repository;
    }

    public async Task<ElectronicCardListDto> Handle(GetUnavailableElectronicCards request, CancellationToken cancellationToken)
    {
        // Use the paginated version of the repository method
        return await _repository.GetUnavailableCardsAsync(request.PageNumber, request.PageSize);
    }
}