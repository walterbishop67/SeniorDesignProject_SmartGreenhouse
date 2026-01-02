using System.Collections.Generic;
using System.Threading.Tasks;
using CleanArchitecture.Core.DTOs.ElectronicCard;
using CleanArchitecture.Core.Entities;

namespace CleanArchitecture.Core.Interfaces.Repositories
{
    public interface IElectronicCardRepositoryAsync : IGenericRepositoryAsync<ElectronicCard>
    {

        Task<List<ElectronicCard>> GetByUserIdAsync(string userId);
        Task<ElectronicCardListDto> GetAvailableCardsAsync(int pageNumber, int pageSize);
        Task<ElectronicCardListDto> GetUnavailableCardsAsync(int pageNumber, int pageSize);
        Task<ElectronicCardListDto> GetCardsWithErrorAsync(int pageNumber, int pageSize);
        Task<ElectronicCardListDto> GetAllCardsAsync(int pageNumber, int pageSize);
        Task<ElectronicCardCountDto> GetAllTypesCardsCountAsync();
        Task<List<ElectronicCard>> GetByUserIdUnAvailableAsync(string userId);
        Task<List<ElectronicCard>> GetByUserIdAvailableAsync(string userId);


        Task<ElectronicCard> AddCardAsync_Admin(string username);

        Task<List<ElectronicCard>> GetCardsByUserNameAsync(string userName);
        Task<List<ElectronicCard>> GetAllCardsByUserIdAsync(string userId);
        Task<ElectronicCard> AddCardByUserIdAsync(string userId);
        Task<List<ElectronicCardStatusDto>> GetCardsByGreenhouseIdAsync(int greenhouseId, string userId);


    }
}