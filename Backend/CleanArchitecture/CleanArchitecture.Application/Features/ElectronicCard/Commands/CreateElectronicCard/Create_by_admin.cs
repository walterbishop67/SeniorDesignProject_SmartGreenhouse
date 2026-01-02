using System;
using System.Collections.Generic;
using System.Threading;
using System.Threading.Tasks;
using System.ComponentModel.DataAnnotations;
using CleanArchitecture.Core.Interfaces.Repositories;
using MediatR;

namespace CleanArchitecture.Core.Features.ElectronicCard.Commands.CreateElectronicCard;

public class AddElectronicCardByAdmin
{
    // Command: Admin'in kullanıcı adına göre kart eklemesi için komut
    public class AddElectronicCardByAdminCommand : IRequest<Entities.ElectronicCard>
    {
        [Required]
        public string UserName { get; set; }
    }

    // Handler: Admin'in kullanıcı adına göre kart eklemesi için işleyici
    public class AddElectronicCardByAdminCommandHandler : IRequestHandler<AddElectronicCardByAdminCommand, Entities.ElectronicCard>
    {
        private readonly IElectronicCardRepositoryAsync _electronicCardRepository;
        private readonly IUserRepositoryAsync _userRepository;

        public AddElectronicCardByAdminCommandHandler(
            IElectronicCardRepositoryAsync electronicCardRepository,
            IUserRepositoryAsync userRepository)
        {
            _electronicCardRepository = electronicCardRepository;
            _userRepository = userRepository;
        }

        public async Task<Entities.ElectronicCard> Handle(AddElectronicCardByAdminCommand request, CancellationToken cancellationToken)
        {
            // Önce kullanıcının var olup olmadığını kontrol et
            var userExists = await _userRepository.IsUserExistAsync(request.UserName);
            if (!userExists)
            {
                throw new KeyNotFoundException($"User with username '{request.UserName}' not found.");
            }

            try
            {
                return await _electronicCardRepository.AddCardAsync_Admin(request.UserName);
            }
            catch (Exception ex)
            {
                // Loglama işlemi burada yapılabilir
                throw;
            }
        }
    }
}