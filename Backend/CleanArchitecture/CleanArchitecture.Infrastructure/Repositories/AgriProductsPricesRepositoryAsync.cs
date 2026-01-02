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
    public class AgriProductsPricesRepositoryAsync : GenericRepositoryAsync<AgriProductsPrices>, IAgriProductsPricesRepositoryAsync
    {

        private readonly DbSet<AgriProductsPrices> _agriProductsPrices;

        public AgriProductsPricesRepositoryAsync(ApplicationDbContext dbContext) : base(dbContext)
        {
            _agriProductsPrices = dbContext.Set<AgriProductsPrices>();
        }

        public async Task<List<AgriProductsPrices>> GetPagedPricesByMunicipalityIdAsync(int municipalityId, int pageNumber, int pageSize)
        {
            return await _agriProductsPrices
                .Where(p => p.MunicipalityId == municipalityId)
                .Skip((pageNumber - 1) * pageSize)
                .Take(pageSize)
                .ToListAsync();
        }

    }
}