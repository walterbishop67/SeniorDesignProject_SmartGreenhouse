using CleanArchitecture.Core.Exceptions;
using CleanArchitecture.Core.Interfaces.Repositories;
using CleanArchitecture.Core.Wrappers;
using MediatR;
using System.Threading;
using System.Threading.Tasks;

namespace CleanArchitecture.Core.Features.UserSupportMessages.Commands.UpdateMessage
{
    public class UpdateMessageCommand : IRequest<int>
    {
        public int Id { get; set; }
        public string MessageResponse { get; set; } 
        
        public class UpdateMessageCommandHandler : IRequestHandler<UpdateMessageCommand, int>
        {
            private readonly IUserSupportMessageRepositoryAsync _userSupportMessageRepository;
            public UpdateMessageCommandHandler(IUserSupportMessageRepositoryAsync userSupportMessageRepository)
            {
                _userSupportMessageRepository = userSupportMessageRepository;
            }
            public async Task<int> Handle(UpdateMessageCommand command, CancellationToken cancellationToken)
            {
                var userSupportMessage = await _userSupportMessageRepository.GetByIdAsync(command.Id);

                if (userSupportMessage == null) throw new EntityNotFoundException("support message", command.Id);

                userSupportMessage.MessageResponse = command.MessageResponse;
                userSupportMessage.isResponsed = true;
                await _userSupportMessageRepository.UpdateAsync(userSupportMessage);
                return userSupportMessage.Id;
            }
        }
    }
}