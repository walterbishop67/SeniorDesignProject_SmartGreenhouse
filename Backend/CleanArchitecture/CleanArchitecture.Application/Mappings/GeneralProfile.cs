using AutoMapper;
using CleanArchitecture.Core.DTOs.Users;
using CleanArchitecture.Core.Entities;
using CleanArchitecture.Core.Features.Categories.Queries.GetAllCategories;
using CleanArchitecture.Core.Features.Greenhouses.Command.CreateGreenhouse;
using CleanArchitecture.Core.Features.Products.Commands.CreateProduct;
using CleanArchitecture.Core.Features.Products.Queries.GetAllProducts;
using CleanArchitecture.Core.Features.ElectronicCard.Commands.CreateElectronicCard;
using CleanArchitecture.Core.Features.UserSupportMessages.Commands.CreateMessage;
using CleanArchitecture.Core.Features.Municipality.Commands.CreateMunicipality;
using CleanArchitecture.Core.Features.UserSupportMessages.Queries.GetAllMessages;
using CleanArchitecture.Core.Features.AgriProductsPrices.Commands.CreatePrice;
using CleanArchitecture.Core.Features.AgriProductsPrices.Queries.GetPriceByMunicipalityId;


namespace CleanArchitecture.Core.Mappings
{
    public class GeneralProfile : Profile
    {
        public GeneralProfile()
        {
            CreateMap<Product, GetAllProductsViewModel>().ReverseMap();
            CreateMap<CreateProductCommand, Product>();
            CreateMap<GetAllProductsQuery, GetAllProductsParameter>();
            CreateMap<GetAllCategoriesQuery, GetAllCategoriesParameter>();
            CreateMap<Category, GetAllCategoriesViewModel>().ReverseMap();
            CreateMap<CreateGreenhouseCommand, Greenhouse>();
            CreateMap<CreateElectronicCardCommand, ElectronicCard>();
            CreateMap<CreateMessageCommand,UserSupportMessage>();
            CreateMap<UserSupportMessage, GetAllMessagesViewModel>().ReverseMap();
            CreateMap<GetAllMessagesQuery, GetAllMessagesParameter>();
            CreateMap<CreateMunicipalityCommand, Municipality>();
            CreateMap<CreatePriceCommand,AgriProductsPrices>();
            CreateMap<AgriProductsPrices, GetPriceByMunicipalityIdViewModel>().ReverseMap();
            CreateMap<GetPriceByMunicipalityIdQuery, GetPriceByMunicipalityIdParameter>();

        }
    }
}
