using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using CleanArchitecture.Core.DTOs.Users;
using CleanArchitecture.Core.Interfaces.Repositories;
using CleanArchitecture.Infrastructure.Contexts;
using Microsoft.EntityFrameworkCore;
using AutoMapper;
using CleanArchitecture.Core.Interfaces;
using CleanArchitecture.Infrastructure.Models;
using CleanArchitecture.Infrastructure.Repository;
using Microsoft.AspNetCore.Identity;

namespace CleanArchitecture.Infrastructure.Repositories
{
    public class UserRepositoryAsync : GenericRepositoryAsync<User>, IUserRepositoryAsync
    {
        private readonly DbSet<User> _users;
        private readonly UserManager<ApplicationUser> _userManager;
        private readonly ApplicationDbContext _dbContext;

        public UserRepositoryAsync(
            ApplicationDbContext dbContext,
            UserManager<ApplicationUser> userManager) : base(dbContext)
        {
            _users = dbContext.Set<User>();
            _userManager = userManager;
            _dbContext = dbContext;
        }

        public async Task<User> GetByUserNameAsync(string userName)
        {
            // Identity kullanarak kullanıcı bilgisini al
            var appUser = await _userManager.FindByNameAsync(userName);
            if (appUser == null)
                return null;

            // ApplicationUser'ı User entity'sine dönüştür
            return new User
            {
                Id = appUser.Id,
                UserName = appUser.UserName,
                Email = appUser.Email,
                FirstName = appUser.FirstName,
                LastName = appUser.LastName,
                PhoneNumber = appUser.PhoneNumber
                // Diğer özellikler buraya eklenebilir
            };
        }

        public async Task<IReadOnlyList<User>> GetUsersByRoleAsync(string roleName)
        {
            var usersInRole = await _userManager.GetUsersInRoleAsync(roleName);

            return usersInRole.Select(appUser => new User
            {
                Id = appUser.Id,
                UserName = appUser.UserName,
                Email = appUser.Email,
                FirstName = appUser.FirstName,
                LastName = appUser.LastName,
                PhoneNumber = appUser.PhoneNumber
                // Diğer özellikler buraya eklenebilir
            }).ToList();
        }

        public async Task<UserListDto> GetUserListAsync(int pageNumber, int pageSize)
        {
            var totalCount = await _userManager.Users.CountAsync();
            var totalPages = (int)System.Math.Ceiling(totalCount / (double)pageSize);

            var appUsers = await _userManager.Users
                .Skip((pageNumber - 1) * pageSize)
                .Take(pageSize)
                .ToListAsync();

            var userDtos = new List<UserDto>();

            foreach (var appUser in appUsers)
            {
                var roles = await _userManager.GetRolesAsync(appUser);

                userDtos.Add(new UserDto
                {
                    Id = appUser.Id,
                    UserName = appUser.UserName,
                    Email = appUser.Email,
                    FirstName = appUser.FirstName,
                    LastName = appUser.LastName,
                    PhoneNumber = appUser.PhoneNumber,
                    Roles = roles.ToList()
                });
            }

            return new UserListDto
            {
                Users = userDtos,
                TotalCount = totalCount,
                PageSize = pageSize,
                CurrentPage = pageNumber,
                TotalPages = totalPages
            };
        }

        public async Task<bool> IsUserExistAsync(string userName)
        {
            return await _userManager.Users.AnyAsync(u => u.UserName == userName);
        }

        public async Task<bool> UpdateUserNameAsync(string userId, string newUserName)
        {
            var user = await _userManager.FindByIdAsync(userId);
            if (user == null) return false;

            user.UserName = newUserName;
            user.NormalizedUserName = newUserName.ToUpper(); // Identity için önemli

            var result = await _userManager.UpdateAsync(user);
            return result.Succeeded;
        }

        public async Task<int> GetTotalUserCountAsync()
        {
            return await _userManager.Users.CountAsync();
        }

        public async Task<Dictionary<string, int>> GetUserCountGroupedByRolesAsync()
        {
            var roles = new[] { "SuperAdmin", "Basic" };
            var result = new Dictionary<string, int>();

            // SuperAdmin sayısını al
            var superAdminUsers = await _userManager.GetUsersInRoleAsync("SuperAdmin");
            var superAdminCount = superAdminUsers.Count;

            // Basic sayısını al
            var basicUsers = await _userManager.GetUsersInRoleAsync("Basic");
            var basicCount = basicUsers.Count;

            // Basic sayısından SuperAdmin sayısını çıkar
            var basicMinusSuperAdminCount = basicCount - superAdminCount;

            // Sonuçları dictionary'ye ekle
            result["SuperAdmin"] = superAdminCount;
            result["Basic"] = basicMinusSuperAdminCount;

            return result;
        }
        
        public async Task<UserInfoDto> GetUserBasicInfoByIdAsync(string userId)
        {
            var appUser = await _userManager.FindByIdAsync(userId);
            if (appUser == null)
                return null;

            return new UserInfoDto
            {
                UserName = appUser.UserName,
                FirstName = appUser.FirstName,
                LastName = appUser.LastName,
                Email = appUser.Email
            };
        }

    }
}