using CleanArchitecture.Core.Exceptions;
using CleanArchitecture.Core.Interfaces.Repositories;
using CleanArchitecture.Core.Wrappers;
using MediatR;
using System.Threading;
using System.Threading.Tasks;

namespace CleanArchitecture.Core.Features.UserSupportMessages.Commands.DeleteMessageById
{
    public class DeleteMessageByIdCommand : IRequest<int>
    {
        public int Id { get; set; }
        public class DeleteMessageByIdCommandHandler : IRequestHandler<DeleteMessageByIdCommand, int>
        {
            private readonly IUserSupportMessageRepositoryAsync _userSupportMessageRepository;
            public DeleteMessageByIdCommandHandler(IUserSupportMessageRepositoryAsync userSupportMessageRepository)
            {
                _userSupportMessageRepository = userSupportMessageRepository;
            }
            public async Task<int> Handle(DeleteMessageByIdCommand command, CancellationToken cancellationToken)
            {
                var userSupportMessage = await _userSupportMessageRepository.GetByIdAsync(command.Id);
                if (userSupportMessage == null) throw new ApiException($"Support Message Not Found.");
                await _userSupportMessageRepository.DeleteAsync(userSupportMessage);
                return userSupportMessage.Id;
            }
        }
    }
}