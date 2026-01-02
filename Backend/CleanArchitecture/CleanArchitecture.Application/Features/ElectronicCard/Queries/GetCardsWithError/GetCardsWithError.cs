using System.Threading;
using System.Threading.Tasks;
using CleanArchitecture.Core.DTOs.ElectronicCard;
using CleanArchitecture.Core.Interfaces.Repositories;
using MediatR;

namespace CleanArchitecture.Core.Features.ElectronicCard.Queries.GetCardsWithError;

// Updated request to include pagination parameters
public class GetCardsWithError : IRequest<ElectronicCardListDto> 
{
    public int PageNumber { get; set; } = 1;
    public int PageSize { get; set; } = 10;
}

public class GetElectronicCardsWithErrorHandler : IRequestHandler<GetCardsWithError, ElectronicCardListDto>
{
    private readonly IElectronicCardRepositoryAsync _repository;

    public GetElectronicCardsWithErrorHandler(IElectronicCardRepositoryAsync repository)
    {
        _repository = repository;
    }

    public async Task<ElectronicCardListDto> Handle(GetCardsWithError request, CancellationToken cancellationToken)
    {
        // Use the paginated version of the repository method
        return await _repository.GetCardsWithErrorAsync(request.PageNumber, request.PageSize);
    }
}