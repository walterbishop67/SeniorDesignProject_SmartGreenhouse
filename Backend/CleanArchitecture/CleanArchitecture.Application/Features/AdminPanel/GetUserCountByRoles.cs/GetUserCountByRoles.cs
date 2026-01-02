using System.Collections.Generic;
using System.Threading;
using System.Threading.Tasks;
using CleanArchitecture.Core.DTOs.Users;
using MediatR;

namespace CleanArchitecture.Core.Features.AdminPanel.GetUserCountByRoles
{
    public class GetUserCountByRoles : IRequest<UserCountDto> { }
    
    public class GetUserCountByRolesQueryHandler : IRequestHandler<GetUserCountByRoles, UserCountDto>
    {
        private readonly IUserRepositoryAsync _userRepository;

        public GetUserCountByRolesQueryHandler(IUserRepositoryAsync userRepository)
        {
            _userRepository = userRepository;
        }

        public async Task<UserCountDto> Handle(GetUserCountByRoles request, CancellationToken cancellationToken)
        {
            var roleCounts = await _userRepository.GetUserCountGroupedByRolesAsync();
            var totalCount = await _userRepository.GetTotalUserCountAsync();

            return new UserCountDto
            {
                TotalUserCount = totalCount,
                RoleCounts = roleCounts
            };
        }
    }


}

