using System.Collections.Generic;
using CleanArchitecture.Core.Exceptions;
using CleanArchitecture.Core.Interfaces.Repositories;
using CleanArchitecture.Core.Entities;
using MediatR;
using System.Threading;
using System.Threading.Tasks;

namespace CleanArchitecture.Core.Features.UserSupportMessages.Queries.GetMessageByUserId
{
    public class GetMessageByUserIdQuery : IRequest<List<UserSupportMessage>>
    {
        public string UserId { get; set; }
    }
    
    public class GetMessageByUserIdQueryHandler : IRequestHandler<GetMessageByUserIdQuery, List<UserSupportMessage>>
    {
        private readonly IUserSupportMessageRepositoryAsync _userSupportMessageRepository;

        public GetMessageByUserIdQueryHandler(IUserSupportMessageRepositoryAsync userSupportMessageRepository)
        {
            _userSupportMessageRepository = userSupportMessageRepository;
        }

        public async Task<List<UserSupportMessage>> Handle(GetMessageByUserIdQuery request, CancellationToken cancellationToken)
        {
            return await _userSupportMessageRepository.GetMessageByUserIdAsync(request.UserId);
        }
    }
    
}