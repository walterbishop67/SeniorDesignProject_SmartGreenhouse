using System.Collections.Generic;
using System.Threading.Tasks;
using CleanArchitecture.Core.Entities;

namespace CleanArchitecture.Core.Interfaces.Repositories;

public interface IUserSupportMessageRepositoryAsync: IGenericRepositoryAsync<UserSupportMessage>
{
    Task<List<UserSupportMessage>> GetMessageByUserIdAsync(string userId);
    Task<List<UserSupportMessage>> GetAllUnOpenedMessages();
}