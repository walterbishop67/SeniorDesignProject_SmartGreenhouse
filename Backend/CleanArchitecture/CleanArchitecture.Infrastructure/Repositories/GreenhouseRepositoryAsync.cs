using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using CleanArchitecture.Core.Entities;
using CleanArchitecture.Core.Interfaces.Repositories;
using CleanArchitecture.Infrastructure.Contexts;
using CleanArchitecture.Infrastructure.Repository;
using Microsoft.EntityFrameworkCore;

namespace CleanArchitecture.Infrastructure.Repositories
{
    public class GreenhouseRepositoryAsync: GenericRepositoryAsync<Greenhouse>, IGreenhouseRepositoryAsync
    {
        private readonly DbSet<Greenhouse> _greenhouses;
        
        public GreenhouseRepositoryAsync(ApplicationDbContext dbContext) : base(dbContext)
        {
            _greenhouses = dbContext.Set<Greenhouse>();
        }
        
        public async Task<List<Greenhouse>> GetByUserIdAsync(string userId)
        {
            return await _greenhouses
                .Where(g => g.CreatedBy == userId)
                .ToListAsync();
        }

        public async Task<Greenhouse> GetGreenhouseByIdAsync(int greenhouseId)
        {
            return await _greenhouses
                .Where(g => g.Id == greenhouseId)
                .FirstOrDefaultAsync();
        }


    }
}

