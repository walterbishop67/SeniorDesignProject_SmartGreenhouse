using System.Threading;
using System.Threading.Tasks;
using CleanArchitecture.Core.DTOs.Users;
using MediatR;

namespace CleanArchitecture.Core.Features.User.GetUserInfoById
{
    // Query
    public class GetUserBasicInfoByIdQuery : IRequest<UserInfoDto>
    {
        public string UserId { get; set; }

        public GetUserBasicInfoByIdQuery(string userId)
        {
            UserId = userId;
        }
    }

    // Handler
    public class GetUserBasicInfoByIdQueryHandler : IRequestHandler<GetUserBasicInfoByIdQuery, UserInfoDto>
    {
        private readonly IUserRepositoryAsync _userRepository;

        public GetUserBasicInfoByIdQueryHandler(IUserRepositoryAsync userRepository)
        {
            _userRepository = userRepository;
        }

        public async Task<UserInfoDto> Handle(GetUserBasicInfoByIdQuery request, CancellationToken cancellationToken)
        {
            return await _userRepository.GetUserBasicInfoByIdAsync(request.UserId);
        }
    }
}
