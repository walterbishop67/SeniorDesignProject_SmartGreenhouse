using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using CleanArchitecture.Core.DTOs.ElectronicCard;
using CleanArchitecture.Core.Interfaces.Repositories;
using MediatR;

namespace CleanArchitecture.Core.Features.ElectronicCard.Queries.GetElectronicCardsByGreenhouseId
{
    // Query to get cards by greenhouseId and userId
    public class GetElectronicCardsByGreenhouseIdQuery : IRequest<List<ElectronicCardStatusDto>>
    {
        public int GreenhouseId { get; set; }
        public string UserId { get; set; }
    }

    // Query Handler to get the electronic cards
    public class GetElectronicCardsByGreenhouseIdQueryHandler : IRequestHandler<GetElectronicCardsByGreenhouseIdQuery, List<ElectronicCardStatusDto>>
    {
        private readonly IElectronicCardRepositoryAsync _electronicCardRepository;

        public GetElectronicCardsByGreenhouseIdQueryHandler(IElectronicCardRepositoryAsync electronicCardRepository)
        {
            _electronicCardRepository = electronicCardRepository;
        }

        // Handle method to return DTO with filtered data (Temperature, Humidity, and ErrorState)
        public async Task<List<ElectronicCardStatusDto>> Handle(GetElectronicCardsByGreenhouseIdQuery request, CancellationToken cancellationToken)
        {
            // Get the electronic cards from the repository based on greenhouseId and userId
            var electronicCards = await _electronicCardRepository.GetCardsByGreenhouseIdAsync(request.GreenhouseId, request.UserId);

            // Map to ElectronicCardStatusDto and return only relevant properties (Temperature, Humidity, and ErrorState)
            var result = electronicCards.Select(ec => new ElectronicCardStatusDto
            {
                Id = ec.Id,
                Temperature = ec.Temperature,
                Humidity = ec.Humidity,
                ErrorState = ec.ErrorState
            }).ToList();

            return result;
        }
    }
}