
using System.Collections.Generic;
using System.Threading;
using System.Threading.Tasks;
using CleanArchitecture.Core.Interfaces.Repositories;
using MediatR;

namespace CleanArchitecture.Core.Features.ElectronicCard.Queries.GetCardByUserName
{
    public class GetActiveElectronicCardsByUserIdQuery : IRequest<List<Entities.ElectronicCard>>
    {
        // User ID passed into the query
        public string UserName { get; set; }
    }
    public class GetActiveElectronicCardsByUserIdQueryHandler : IRequestHandler<GetActiveElectronicCardsByUserIdQuery, List<Entities.ElectronicCard>>
    {
        private readonly IElectronicCardRepositoryAsync _electronicCardRepository;

        // Constructor injection to get the repository
        public GetActiveElectronicCardsByUserIdQueryHandler(IElectronicCardRepositoryAsync electronicCardRepository)
        {
            _electronicCardRepository = electronicCardRepository;
        }

        // Handle method: Executes the query and returns "active" cards for the user
        public async Task<List<Entities.ElectronicCard>> Handle(GetActiveElectronicCardsByUserIdQuery request, CancellationToken cancellationToken)
        {
            // Calling the repository to fetch "active" cards by UserId
            return await _electronicCardRepository.GetCardsByUserNameAsync(request.UserName);
        }
    }
}
