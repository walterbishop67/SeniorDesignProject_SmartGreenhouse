using System.Threading;
using System.Threading.Tasks;
using CleanArchitecture.Core.DTOs.Users;
using MediatR;

namespace CleanArchitecture.Core.Features.AdminPanel.GetBasicUsers;

public class GetAllUsers
{
    // Tüm kullanıcıları getirecek sorgu (sayfalama ile)
    public class GetAllUsersQuery : IRequest<UserListDto>
    {
        public int PageNumber { get; set; } = 1;
        public int PageSize { get; set; } = 10;
    }

    // Handler: Tüm kullanıcıları getirecek işleyici
    public class GetAllUsersQueryHandler : IRequestHandler<GetAllUsersQuery, UserListDto>
    {
        private readonly IUserRepositoryAsync _userRepository;

        public GetAllUsersQueryHandler(IUserRepositoryAsync userRepository)
        {
            _userRepository = userRepository;
        }

        public async Task<UserListDto> Handle(GetAllUsersQuery request, CancellationToken cancellationToken)
        {
            // Kullanıcıları sayfalanmış şekilde getiren repository metodunu çağırıyoruz
            return await _userRepository.GetUserListAsync(request.PageNumber, request.PageSize);
        }
    }
}