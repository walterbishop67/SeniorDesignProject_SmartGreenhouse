using System.Threading;
using System.Threading.Tasks;
using CleanArchitecture.Core.Interfaces.Repositories;
using MediatR;

namespace CleanArchitecture.Core.Features.ElectronicCard.Commands.UpdateProduct;

public class UpdateElectronicCardStatus2: IRequest<int>
{
    public int CardId { get; set; }

    public class Handler : IRequestHandler<UpdateElectronicCardStatus2, int>
    {
        private readonly IElectronicCardRepositoryAsync _repository;

        public Handler(IElectronicCardRepositoryAsync repository)
        {
            _repository = repository;
        }

        public async Task<int> Handle(UpdateElectronicCardStatus2 command, CancellationToken cancellationToken)
        {
            var card = await _repository.GetByIdAsync(command.CardId);

            if (card == null)
                return 0;

            card.Status = "Available";
            
            await _repository.UpdateAsync(card);

            return card.Id;
        }
    }
    
}