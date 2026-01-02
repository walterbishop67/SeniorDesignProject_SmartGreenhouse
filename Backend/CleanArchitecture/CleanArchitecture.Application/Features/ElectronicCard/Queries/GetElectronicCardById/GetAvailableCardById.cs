using System.Collections.Generic;
using System.Threading;
using System.Threading.Tasks;
using CleanArchitecture.Core.Interfaces.Repositories;
using MediatR;

namespace CleanArchitecture.Core.Features.ElectronicCard.Queries.GetElectronicCardById;

public class GetByUserIdAvailableAsync
{
    // Kullanıcı ID'sine göre "available" olan kartları getirecek sorgu
    public class GetElectronicCardsByUserIdQuery : IRequest<List<Entities.ElectronicCard>>
    {
        public string UserId { get; set; }
    }

    // Handler: Kullanıcıya ait "available" kartları getirecek işleyici
    public class GetElectronicCardsByUserIdQueryHandler : IRequestHandler<GetElectronicCardsByUserIdQuery, List<Entities.ElectronicCard>>
    {
        private readonly IElectronicCardRepositoryAsync _electronicCardRepository;

        public GetElectronicCardsByUserIdQueryHandler(IElectronicCardRepositoryAsync electronicCardRepository)
        {
            _electronicCardRepository = electronicCardRepository;
        }

        public async Task<List<Entities.ElectronicCard>> Handle(GetElectronicCardsByUserIdQuery request, CancellationToken cancellationToken)
        {
            return await _electronicCardRepository.GetByUserIdAvailableAsync(request.UserId);
        }
    }
}