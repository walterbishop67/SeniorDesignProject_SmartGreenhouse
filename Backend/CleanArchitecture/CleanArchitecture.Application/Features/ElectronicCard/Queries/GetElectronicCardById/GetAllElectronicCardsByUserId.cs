using System.Collections.Generic;
using System.Threading;
using System.Threading.Tasks;
using CleanArchitecture.Core.Interfaces.Repositories;
using MediatR;

namespace CleanArchitecture.Core.Features.ElectronicCard.Queries.GetElectronicCardById;

public class GetAllElectronicCardsByUserId
{
    // Sorgu: Kullanıcı ID'sine göre tüm kartları getir
    public class GetAllElectronicCardsByUserIdQuery : IRequest<List<Entities.ElectronicCard>>
    {
        public string UserId { get; set; }
    }

    // Handler: Repository üzerinden tüm kartları getiren handler
    public class GetAllElectronicCardsByUserIdQueryHandler : IRequestHandler<GetAllElectronicCardsByUserIdQuery, List<Entities.ElectronicCard>>
    {
        private readonly IElectronicCardRepositoryAsync _electronicCardRepository;

        public GetAllElectronicCardsByUserIdQueryHandler(IElectronicCardRepositoryAsync electronicCardRepository)
        {
            _electronicCardRepository = electronicCardRepository;
        }

        public async Task<List<Entities.ElectronicCard>> Handle(GetAllElectronicCardsByUserIdQuery request, CancellationToken cancellationToken)
        {
            return await _electronicCardRepository.GetAllCardsByUserIdAsync(request.UserId);
        }
    }
}