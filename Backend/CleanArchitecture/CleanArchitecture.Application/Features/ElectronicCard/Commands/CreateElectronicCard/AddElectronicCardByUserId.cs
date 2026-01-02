using System;
using System.ComponentModel.DataAnnotations;
using System.Threading;
using System.Threading.Tasks;
using CleanArchitecture.Core.Interfaces.Repositories;
using MediatR;

namespace CleanArchitecture.Core.Features.ElectronicCard.Commands.CreateElectronicCard;

public class AddElectronicCardByUserId
{
    // Command: Kullanıcı ID'ye göre kart ekle
    public class AddElectronicCardByUserIdCommand : IRequest<Entities.ElectronicCard>
    {
        [Required]
        public string UserId { get; set; }
    }

    // Handler: Kullanıcı ID'ye göre kart ekleme işlemi
    public class AddElectronicCardByUserIdCommandHandler : IRequestHandler<AddElectronicCardByUserIdCommand, Entities.ElectronicCard>
    {
        private readonly IElectronicCardRepositoryAsync _electronicCardRepository;

        public AddElectronicCardByUserIdCommandHandler(IElectronicCardRepositoryAsync electronicCardRepository)
        {
            _electronicCardRepository = electronicCardRepository;
        }

        public async Task<Entities.ElectronicCard> Handle(AddElectronicCardByUserIdCommand request, CancellationToken cancellationToken)
        {
            try
            {
                // Kullanıcı ID'sine göre kartı ekle
                return await _electronicCardRepository.AddCardByUserIdAsync(request.UserId);
            }
            catch (Exception ex)
            {
                // Loglama işlemi burada yapılabilir
                throw new Exception("Error while adding electronic card by UserId", ex);
            }
        }
    }
}