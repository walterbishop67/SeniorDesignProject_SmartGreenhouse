using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using CleanArchitecture.Core.DTOs.ElectronicCard;
using CleanArchitecture.Core.Entities;
using CleanArchitecture.Core.Interfaces.Repositories;
using CleanArchitecture.Infrastructure.Contexts;
using CleanArchitecture.Infrastructure.Models;
using CleanArchitecture.Infrastructure.Repository;
using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;

namespace CleanArchitecture.Infrastructure.Repositories
{
    public class ElectronicCardRepositoryAsync : GenericRepositoryAsync<ElectronicCard>, IElectronicCardRepositoryAsync
    {
        private readonly DbSet<ElectronicCard> _electronicCards;
        private readonly UserManager<ApplicationUser> _userManager;
        private readonly ApplicationDbContext _dbContext;

        public ElectronicCardRepositoryAsync(
            ApplicationDbContext dbContext,
            UserManager<ApplicationUser> userManager) : base(dbContext)
        {
            _electronicCards = dbContext.Set<ElectronicCard>();
            _userManager = userManager;
            _dbContext = dbContext;
        }
        
        // Belirli bir Greenhouse'a ait kartları getir
        public async Task<List<ElectronicCard>> GetByGreenhouseIdAsync(int greenhouseId)
        {
            return await _electronicCards
                .Where(e => e.GreenHouseId == greenhouseId && e.Status == "available")  // "available" status filtresi ekledik
                .ToListAsync();
        }

        // Belirli bir kullanıcıya ait ve status'u "available" olan kartları getir
        public async Task<List<ElectronicCard>> GetByUserIdAsync(string userId)
        {
            return await _electronicCards
                .Where(e => e.UserId == userId && e.Status == "available")  // "available" status filtresi ekledik
                .ToListAsync();
        }
        
        public async Task<List<ElectronicCard>> GetAvailableCardsAsync()
        {
            return await _electronicCards
                .Where(ec => ec.Status == "Available")
                .ToListAsync();
        }

        public async Task<List<ElectronicCard>> GetUnavailableCardsAsync()
        {
            return await _electronicCards
                .Where(ec => ec.Status == "Unavailable")
                .ToListAsync();
        }
        
        public async Task<List<ElectronicCard>> GetCardsWithErrorAsync()
        {
            return await _electronicCards
                .Where(ec => !string.IsNullOrEmpty(ec.ErrorState))
                .ToListAsync();
        }
        
        public async Task<ElectronicCardListDto> GetAllCardsAsync(int pageNumber, int pageSize)
        {
            var totalCount = await _electronicCards.CountAsync();
            var totalPages = (int)Math.Ceiling(totalCount / (double)pageSize);

            var cards = await _electronicCards
                .OrderBy(e => e.Id) // İstersen başka bir sıralama da verebilirsin
                .Skip((pageNumber - 1) * pageSize)
                .Take(pageSize)
                .ToListAsync();

            return new ElectronicCardListDto
            {
                Cards = cards,
                TotalCount = totalCount,
                PageSize = pageSize,
                CurrentPage = pageNumber,
                TotalPages = totalPages
            };
        }

        // Available kartlar için sayfalama
        public async Task<ElectronicCardListDto> GetAvailableCardsAsync(int pageNumber, int pageSize)
        {
            var totalCount = await _electronicCards.CountAsync(ec => ec.Status == "Available");
            var totalPages = (int)Math.Ceiling(totalCount / (double)pageSize);

            var cards = await _electronicCards
                .Where(ec => ec.Status == "Available")
                .OrderBy(e => e.Id)
                .Skip((pageNumber - 1) * pageSize)
                .Take(pageSize)
                .ToListAsync();

            return new ElectronicCardListDto
            {
                Cards = cards,
                TotalCount = totalCount,
                PageSize = pageSize,
                CurrentPage = pageNumber,
                TotalPages = totalPages
            };
        }

        // Unavailable kartlar için sayfalama
        public async Task<ElectronicCardListDto> GetUnavailableCardsAsync(int pageNumber, int pageSize)
        {
            var totalCount = await _electronicCards.CountAsync(ec => ec.Status == "Unavailable");
            var totalPages = (int)Math.Ceiling(totalCount / (double)pageSize);

            var cards = await _electronicCards
                .Where(ec => ec.Status == "Unavailable")
                .OrderBy(e => e.Id)
                .Skip((pageNumber - 1) * pageSize)
                .Take(pageSize)
                .ToListAsync();

            return new ElectronicCardListDto
            {
                Cards = cards,
                TotalCount = totalCount,
                PageSize = pageSize,
                CurrentPage = pageNumber,
                TotalPages = totalPages
            };
        }

        // Hata durumu olan kartlar için sayfalama
        public async Task<ElectronicCardListDto> GetCardsWithErrorAsync(int pageNumber, int pageSize)
        {
            var totalCount = await _electronicCards.CountAsync(ec => !string.IsNullOrEmpty(ec.ErrorState));
            var totalPages = (int)Math.Ceiling(totalCount / (double)pageSize);

            var cards = await _electronicCards
                .Where(ec => !string.IsNullOrEmpty(ec.ErrorState))
                .OrderBy(e => e.Id)
                .Skip((pageNumber - 1) * pageSize)
                .Take(pageSize)
                .ToListAsync();

            return new ElectronicCardListDto
            {
                Cards = cards,
                TotalCount = totalCount,
                PageSize = pageSize,
                CurrentPage = pageNumber,
                TotalPages = totalPages
            };
        }

        public async Task<ElectronicCardCountDto> GetAllTypesCardsCountAsync()
        {
            var total = await _electronicCards.CountAsync();
            var available = await _electronicCards.CountAsync(e => e.Status == "Available");
            var unavailable = await _electronicCards.CountAsync(e => e.Status == "Unavailable");
            var error = await _electronicCards.CountAsync(e => !string.IsNullOrEmpty(e.ErrorState));

            return new ElectronicCardCountDto
            {
                TotalCount = total,
                AvailableCount = available,
                UnavailableCount = unavailable,
                ErrorCount = error
            };
        }
        
        public async Task<List<ElectronicCard>> GetByUserIdUnAvailableAsync(string userId)
        {
            return await _electronicCards
                .Where(e => e.UserId == userId && e.Status == "Unavailable")  // "available" status filtresi ekledik
                .ToListAsync();
        }
        public async Task<List<ElectronicCard>> GetByUserIdAvailableAsync(string userId)
        {
            return await _electronicCards
                .Where(e => e.UserId == userId && e.Status == "Available")  // "available" status filtresi ekledik
                .ToListAsync();
        }

        // Kullanıcıya ait available kartlar için sayfalama
        public async Task<ElectronicCardListDto> GetByUserIdAvailableAsync(string userId, int pageNumber, int pageSize)
        {
            var totalCount = await _electronicCards.CountAsync(e => e.UserId == userId && e.Status == "Available");
            var totalPages = (int)Math.Ceiling(totalCount / (double)pageSize);

            var cards = await _electronicCards
                .Where(e => e.UserId == userId && e.Status == "Available")
                .OrderBy(e => e.Id)
                .Skip((pageNumber - 1) * pageSize)
                .Take(pageSize)
                .ToListAsync();

            return new ElectronicCardListDto
            {
                Cards = cards,
                TotalCount = totalCount,
                PageSize = pageSize,
                CurrentPage = pageNumber,
                TotalPages = totalPages
            };
        }

        // Kullanıcıya ait unavailable kartlar için sayfalama
        public async Task<ElectronicCardListDto> GetByUserIdUnavailableAsync(string userId, int pageNumber, int pageSize)
        {
            var totalCount = await _electronicCards.CountAsync(e => e.UserId == userId && e.Status == "Unavailable");
            var totalPages = (int)Math.Ceiling(totalCount / (double)pageSize);

            var cards = await _electronicCards
                .Where(e => e.UserId == userId && e.Status == "Unavailable")
                .OrderBy(e => e.Id)
                .Skip((pageNumber - 1) * pageSize)
                .Take(pageSize)
                .ToListAsync();

            return new ElectronicCardListDto
            {
                Cards = cards,
                TotalCount = totalCount,
                PageSize = pageSize,
                CurrentPage = pageNumber,
                TotalPages = totalPages
            };
        }

        // Kullanıcıya ait hata durumu olan kartlar için sayfalama
        public async Task<ElectronicCardListDto> GetByUserIdWithErrorAsync(string userId, int pageNumber, int pageSize)
        {
            var totalCount = await _electronicCards.CountAsync(e => e.UserId == userId && !string.IsNullOrEmpty(e.ErrorState));
            var totalPages = (int)Math.Ceiling(totalCount / (double)pageSize);

            var cards = await _electronicCards
                .Where(e => e.UserId == userId && !string.IsNullOrEmpty(e.ErrorState))
                .OrderBy(e => e.Id)
                .Skip((pageNumber - 1) * pageSize)
                .Take(pageSize)
                .ToListAsync();

            return new ElectronicCardListDto
            {
                Cards = cards,
                TotalCount = totalCount,
                PageSize = pageSize,
                CurrentPage = pageNumber,
                TotalPages = totalPages
            };
        }

        public async Task<ElectronicCard> AddCardAsync_Admin(string userName)
        {
            // UserManager üzerinden kullanıcıyı bul
            var user = await _userManager.FindByNameAsync(userName);
    
            if (user == null)
            {
                throw new KeyNotFoundException($"User with username '{userName}' not found.");
            }
            var newGuid = Guid.NewGuid(); 

            var newCard = new ElectronicCard
            {
                UserId = user.Id,
                Temperature = "waiting for temperature data",
                Humidity = "waiting for humidity data",
                ErrorState = null,
                Status = "Available"
            };
    
            // Add the card to the database
            await _electronicCards.AddAsync(newCard);
            await _dbContext.SaveChangesAsync();
            return newCard;
        }
        
        public async Task<List<ElectronicCard>> GetCardsByUserNameAsync(string userName)
        {
            // UserManager üzerinden kullanıcıyı bul
            var user = await _userManager.FindByNameAsync(userName);
    
            if (user == null)
            {
                throw new KeyNotFoundException($"User with username '{userName}' not found.");
            }

            // Kullanıcıya ait kartları veritabanından al
            var cards = await _dbContext.ElectronicCards
                .Where(card => card.UserId == user.Id)
                .ToListAsync();

            return cards;
        }
        
        // for admin part
        public async Task<List<ElectronicCard>> GetAllCardsByUserIdAsync(string userId)
        {
            return await _electronicCards
                .Where(e => e.UserId == userId)
                .ToListAsync();
        }

        // Kullanıcıya ait tüm kartlar için sayfalama
        public async Task<ElectronicCardListDto> GetAllCardsByUserIdAsync(string userId, int pageNumber, int pageSize)
        {
            var totalCount = await _electronicCards.CountAsync(e => e.UserId == userId);
            var totalPages = (int)Math.Ceiling(totalCount / (double)pageSize);

            var cards = await _electronicCards
                .Where(e => e.UserId == userId)
                .OrderBy(e => e.Id)
                .Skip((pageNumber - 1) * pageSize)
                .Take(pageSize)
                .ToListAsync();

            return new ElectronicCardListDto
            {
                Cards = cards,
                TotalCount = totalCount,
                PageSize = pageSize,
                CurrentPage = pageNumber,
                TotalPages = totalPages
            };
        }

        public async Task<ElectronicCard> AddCardByUserIdAsync(string userId)
        {
            // Kullanıcıyı veritabanında kontrol et
            var user = await _userManager.FindByIdAsync(userId);
            if (user == null)
            {
                throw new KeyNotFoundException($"User with ID '{userId}' not found.");
            }

            var newCard = new ElectronicCard
            {
                UserId = userId,
                Temperature = "waiting for temperature data",
                Humidity = "waiting for humidity data",
                ErrorState = null,
                Status = "Available",
                // Eğer ProductId gibi otomatik oluşturulacak başka alanlar varsa burada ayarlanabilir
            };

            await _electronicCards.AddAsync(newCard);
            await _dbContext.SaveChangesAsync();

            return newCard;
        }
        
        public async Task<List<ElectronicCardStatusDto>> GetCardsByGreenhouseIdAsync(int greenhouseId, string userId)
        {
            return await _electronicCards
                .Where(ec => ec.GreenHouseId == greenhouseId && ec.UserId == userId)
                .Select(ec => new ElectronicCardStatusDto
                {
                    Id = ec.Id,
                    Temperature = ec.Temperature,
                    Humidity = ec.Humidity,
                    ErrorState = ec.ErrorState
                })
                .ToListAsync();
        }

        // Sera'ya ait kartlar için sayfalama
        public async Task<ElectronicCardListDto> GetCardsByGreenhouseIdPaginatedAsync(int greenhouseId, int pageNumber, int pageSize)
        {
            var totalCount = await _electronicCards.CountAsync(ec => ec.GreenHouseId == greenhouseId);
            var totalPages = (int)Math.Ceiling(totalCount / (double)pageSize);

            var cards = await _electronicCards
                .Where(ec => ec.GreenHouseId == greenhouseId)
                .OrderBy(e => e.Id)
                .Skip((pageNumber - 1) * pageSize)
                .Take(pageSize)
                .ToListAsync();

            return new ElectronicCardListDto
            {
                Cards = cards,
                TotalCount = totalCount,
                PageSize = pageSize,
                CurrentPage = pageNumber,
                TotalPages = totalPages
            };
        }
    }
}