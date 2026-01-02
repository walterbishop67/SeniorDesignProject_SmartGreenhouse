using System.Collections.Generic;
using System.Threading.Tasks;
using CleanArchitecture.Core.Entities;

namespace CleanArchitecture.Core.Interfaces.Repositories;

public interface IAgriProductsPricesRepositoryAsync: IGenericRepositoryAsync<AgriProductsPrices>
{
    Task<List<AgriProductsPrices>> GetPagedPricesByMunicipalityIdAsync(int municipalityId, int pageNumber, int pageSize);

}