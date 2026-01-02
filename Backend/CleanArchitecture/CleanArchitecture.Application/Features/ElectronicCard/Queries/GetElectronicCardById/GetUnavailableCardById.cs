using System.Collections.Generic;
using System.Threading;
using System.Threading.Tasks;
using CleanArchitecture.Core.Interfaces.Repositories;
using MediatR;

namespace CleanArchitecture.Core.Features.ElectronicCard.Queries.GetElectronicCardById;

public class GetUnavailableCardById
{
    // Kullanıcı ID'sine göre "available" olan kartları getirecek sorgu
    public class GetUnElectronicCardsByUserIdQuery : IRequest<List<Entities.ElectronicCard>>
    {
        public string UserId { get; set; }
    }

    // Handler: Kullanıcıya ait "available" kartları getirecek işleyici
    public class GetUnElectronicCardsByUserIdQueryHandler : IRequestHandler<GetUnElectronicCardsByUserIdQuery, List<Entities.ElectronicCard>>
    {
        private readonly IElectronicCardRepositoryAsync _electronicCardRepository;

        public GetUnElectronicCardsByUserIdQueryHandler(IElectronicCardRepositoryAsync electronicCardRepository)
        {
            _electronicCardRepository = electronicCardRepository;
        }

        public async Task<List<Entities.ElectronicCard>> Handle(GetUnElectronicCardsByUserIdQuery request, CancellationToken cancellationToken)
        {
            return await _electronicCardRepository.GetByUserIdUnAvailableAsync(request.UserId);
        }
    }
}