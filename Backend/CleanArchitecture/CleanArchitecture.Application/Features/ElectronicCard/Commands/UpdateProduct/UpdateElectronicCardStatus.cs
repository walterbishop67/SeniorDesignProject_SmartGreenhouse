using System.Threading;
using System.Threading.Tasks;
using CleanArchitecture.Core.Interfaces.Repositories;
using MediatR;

namespace CleanArchitecture.Core.Features.ElectronicCard.Commands.UpdateProduct;

public class UpdateElectronicCardStatus: IRequest<int>
{
    public int CardId { get; set; }
    public int GreenHouseId { get; set; }

    public class Handler : IRequestHandler<UpdateElectronicCardStatus, int>
    {
        private readonly IElectronicCardRepositoryAsync _repository;

        public Handler(IElectronicCardRepositoryAsync repository)
        {
            _repository = repository;
        }

        public async Task<int> Handle(UpdateElectronicCardStatus command, CancellationToken cancellationToken)
        {
            var card = await _repository.GetByIdAsync(command.CardId);

            if (card == null)
                return 0;

            card.Status = "Unavailable";
            card.GreenHouseId = command.GreenHouseId;

            await _repository.UpdateAsync(card);

            return card.Id;
        }
    }
    
}