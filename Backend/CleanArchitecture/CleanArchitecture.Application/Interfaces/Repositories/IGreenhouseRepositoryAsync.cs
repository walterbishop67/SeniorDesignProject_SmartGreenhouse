using System.Collections.Generic;
using System.Threading.Tasks;
using CleanArchitecture.Core.Entities;

namespace CleanArchitecture.Core.Interfaces.Repositories;

public interface IGreenhouseRepositoryAsync: IGenericRepositoryAsync<Greenhouse>
{
    Task<List<Greenhouse>> GetByUserIdAsync(string userId);
    Task<Greenhouse> GetGreenhouseByIdAsync(int id);
    
}