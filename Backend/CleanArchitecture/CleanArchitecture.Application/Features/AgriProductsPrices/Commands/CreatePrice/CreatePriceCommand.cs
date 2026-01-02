using System.Threading;
using System.Threading.Tasks;
using CleanArchitecture.Core.Entities;
using CleanArchitecture.Core.Features.UserSupportMessages.Commands.CreateMessage;
using CleanArchitecture.Core.Interfaces.Repositories;
using MediatR;

namespace CleanArchitecture.Core.Features.AgriProductsPrices.Commands.CreatePrice
{
    public class CreatePriceCommand: IRequest<int>
    {
        public string AgriProductName { get; set; }
        public int? MunicipalityId { get; set; }
        
    }

    public class CreatePriceCommandHandler : IRequestHandler<CreatePriceCommand, int>
    {
        private readonly IAgriProductsPricesRepositoryAsync _agriProductsPricesRepositoryAsync;
        
        public CreatePriceCommandHandler(IAgriProductsPricesRepositoryAsync agriProductsPricesRepositoryAsync)
        {
            _agriProductsPricesRepositoryAsync = agriProductsPricesRepositoryAsync;
        }

        public async Task<int> Handle(CreatePriceCommand request, CancellationToken cancellationToken)
        {
            var newAgriProductPrice = new Entities.AgriProductsPrices
            {
                AgriProductName = request.AgriProductName,
                MunicipalityId = request.MunicipalityId
            };
            
            await _agriProductsPricesRepositoryAsync.AddAsync(newAgriProductPrice);
            
            return newAgriProductPrice.Id;
        }
    }
}

