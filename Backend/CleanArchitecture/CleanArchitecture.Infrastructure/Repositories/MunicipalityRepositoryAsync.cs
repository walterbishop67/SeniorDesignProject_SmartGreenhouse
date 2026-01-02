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
    public class MunicipalityRepositoryAsync : GenericRepositoryAsync<Municipality>, IMunicipalityRepositoryAsync
    {

        private readonly DbSet<Municipality> _municipalities;

        public MunicipalityRepositoryAsync(ApplicationDbContext dbContext) : base(dbContext)
        {
            _municipalities = dbContext.Set<Municipality>();
        }
        
    }
}