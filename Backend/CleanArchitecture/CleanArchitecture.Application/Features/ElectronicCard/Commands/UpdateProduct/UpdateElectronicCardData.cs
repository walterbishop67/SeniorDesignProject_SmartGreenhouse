using System.Threading;
using System.Threading.Tasks;
using CleanArchitecture.Core.Exceptions;
using CleanArchitecture.Core.Features.ElectronicCard.Commands.CreateElectronicCard;
using CleanArchitecture.Core.Interfaces.Repositories;
using MediatR;

namespace CleanArchitecture.Core.Features.ElectronicCard.Commands.UpdateProduct;

public class UpdateElectronicCardDataCommand : IRequest<int>
{
    public int Id { get; set; }
    public string Temperature { get; set; }
    public string Humidity { get; set; }
    public string ErrorState { get; set; }

    public class Handler : IRequestHandler<UpdateElectronicCardDataCommand, int>
    {
        private readonly IElectronicCardRepositoryAsync _repository;

        public Handler(IElectronicCardRepositoryAsync repository)
        {
            _repository = repository;
        }

        public async Task<int> Handle(UpdateElectronicCardDataCommand command, CancellationToken cancellationToken)
        {
            var card = await _repository.GetByIdAsync(command.Id);

            if (card == null)
                return 0;

            card.Temperature = command.Temperature;
            card.Humidity = command.Humidity;
            card.ErrorState = command.ErrorState;

            await _repository.UpdateAsync(card);

            return card.Id;
        }
    }
}