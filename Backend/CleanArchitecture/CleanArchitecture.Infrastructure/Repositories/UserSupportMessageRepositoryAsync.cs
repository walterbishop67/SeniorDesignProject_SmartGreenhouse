using CleanArchitecture.Core.Entities;
using CleanArchitecture.Core.Interfaces.Repositories;
using CleanArchitecture.Infrastructure.Contexts;
using CleanArchitecture.Infrastructure.Repository;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace CleanArchitecture.Infrastructure.Repositories
{
    public class UserSupportMessageRepositoryAsync : GenericRepositoryAsync<UserSupportMessage>, IUserSupportMessageRepositoryAsync
    {

        private readonly DbSet<UserSupportMessage> _userSupportMessages;

        public UserSupportMessageRepositoryAsync(ApplicationDbContext dbContext) : base(dbContext)
        {
            _userSupportMessages = dbContext.Set<UserSupportMessage>();
        }

        public async Task<List<UserSupportMessage>> GetMessageByUserIdAsync(string userId)
        {
            return await _userSupportMessages
                .Where(g => g.CreatedBy == userId)
                .ToListAsync();
        }

        public async Task<List<UserSupportMessage>> GetAllUnOpenedMessages()
        {
            return await _userSupportMessages
                .Where(g => g.isResponsed == false)
                .ToListAsync();
        }
    }
}