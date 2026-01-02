using System.Collections.Generic;
using System.Threading.Tasks;
using CleanArchitecture.Core.DTOs.Users;
using CleanArchitecture.Core.Interfaces;

public interface IUserRepositoryAsync : IGenericRepositoryAsync<User>
{
    Task<User> GetByUserNameAsync(string userName);
    Task<IReadOnlyList<User>> GetUsersByRoleAsync(string roleName);
    Task<UserListDto> GetUserListAsync(int pageNumber, int pageSize);
    Task<bool> IsUserExistAsync(string userName);
    
    Task<bool> UpdateUserNameAsync(string userId, string newUserName);
    Task<int> GetTotalUserCountAsync();
    Task<Dictionary<string, int>> GetUserCountGroupedByRolesAsync();
    Task<UserInfoDto> GetUserBasicInfoByIdAsync(string userId);

}