using CleanArchitecture.Core.Exceptions;
using CleanArchitecture.Core.Interfaces.Repositories;
using CleanArchitecture.Core.Entities;
using MediatR;
using System.Threading;
using System.Threading.Tasks;

namespace CleanArchitecture.Core.Features.UserSupportMessages.Queries.GetMessageById
{
    public class GetMessageByIdQuery : IRequest<UserSupportMessage>
    {
        public int Id { get; set; }
        public class GetMessageByIdQueryHandler : IRequestHandler<GetMessageByIdQuery, UserSupportMessage>
        {
            private readonly IUserSupportMessageRepositoryAsync _userSupportMessageRepository;
            public GetMessageByIdQueryHandler(IUserSupportMessageRepositoryAsync userSupportMessageRepository)
            {
                _userSupportMessageRepository = userSupportMessageRepository;
            }
            public async Task<UserSupportMessage> Handle(GetMessageByIdQuery query, CancellationToken cancellationToken)
            {
                var userSupportMessage = await _userSupportMessageRepository.GetByIdAsync(query.Id);
                if (userSupportMessage == null) throw new ApiException($"Support Message Not Found.");
                return userSupportMessage;
            }
        }
    }
}