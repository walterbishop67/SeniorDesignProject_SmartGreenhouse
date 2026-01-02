using System.Threading;
using System.Threading.Tasks;
using CleanArchitecture.Core.Interfaces;
using CleanArchitecture.Core.Interfaces.Repositories;
using MediatR;

namespace CleanArchitecture.Core.Features.ElectronicCard.Commands.CreateElectronicCard
{
    
    public class CreateElectronicCardCommand : IRequest<int>
    {
        public string ProductName { get; set; }
        public int? GreenHouseId { get; set; }
        public string LastDataTime { get; set; }
    }
    public class CreateElectronicCardCommandHandler : IRequestHandler<CreateElectronicCardCommand, int>
    {
        private readonly IElectronicCardRepositoryAsync _electronicCardRepositoryAsync;

        public CreateElectronicCardCommandHandler(IElectronicCardRepositoryAsync electronicCardRepositoryAsync)
        {
            _electronicCardRepositoryAsync = electronicCardRepositoryAsync;
        }

        public async Task<int> Handle(CreateElectronicCardCommand request, CancellationToken cancellationToken)
        {
            var newElectronicCard = new Entities.ElectronicCard
            {
                ProductName = request.ProductName,
                GreenHouseId = request.GreenHouseId,
                LastDataTime = request.LastDataTime,
                Temperature = "waiting for temperature data",
                Humidity = "waiting for humidity data",
                ErrorState = null,
                Status = "Available"
            };

            await _electronicCardRepositoryAsync.AddAsync(newElectronicCard);

            return newElectronicCard.Id;
        }
    }
}