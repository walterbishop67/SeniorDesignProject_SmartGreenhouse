using System.Threading;
using System.Threading.Tasks;
using CleanArchitecture.Core.Entities;
using CleanArchitecture.Core.Interfaces.Repositories;
using MediatR;

namespace CleanArchitecture.Core.Features.UserSupportMessages.Commands.CreateMessage
{
    public class CreateMessageCommand: IRequest<int>
    {
        public string Subject { get; set; }
        public string MessageContent { get; set; }
        public string SentAt { get; set; }
    }

    public class CreateMessageCommandHandler : IRequestHandler<CreateMessageCommand, int>
    {
        private readonly IUserSupportMessageRepositoryAsync _userSupportMessageRepositoryAsync;
        
        public CreateMessageCommandHandler(IUserSupportMessageRepositoryAsync userSupportMessageRepositoryAsync)
        {
            _userSupportMessageRepositoryAsync = userSupportMessageRepositoryAsync;
        }

        public async Task<int> Handle(CreateMessageCommand request, CancellationToken cancellationToken)
        {
            var newUserSupportMessage = new UserSupportMessage
            {
                Subject = request.Subject,
                MessageContent = request.MessageContent,
                SentAt = request.SentAt
            };

            await _userSupportMessageRepositoryAsync.AddAsync(newUserSupportMessage);
            
            return newUserSupportMessage.Id;
        }
    }
}