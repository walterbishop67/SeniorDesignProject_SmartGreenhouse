using System.Threading;
using System.Threading.Tasks;
using CleanArchitecture.Core.Entities;
using CleanArchitecture.Core.Features.Categories.Commands.CreateCategory;
using CleanArchitecture.Core.Interfaces.Repositories;
using MediatR;

namespace CleanArchitecture.Core.Features.Greenhouses.Command.CreateGreenhouse
{
    public class CreateGreenhouseCommand: IRequest<int>
    {
        public string Name { get; set; }
        public string Type { get; set; }
        public string Area { get; set; }
        public string Code { get; set; }
    }

    public class CreateGreenhouseCommandHandler : IRequestHandler<CreateGreenhouseCommand, int>
    {
        private readonly IGreenhouseRepositoryAsync _greenhouseRepositoryAsync;
        
        public CreateGreenhouseCommandHandler(IGreenhouseRepositoryAsync greenhouseRepositoryAsync)
        {
            _greenhouseRepositoryAsync = greenhouseRepositoryAsync;
        }

        public async Task<int> Handle(CreateGreenhouseCommand request, CancellationToken cancellationToken)
        {
            var newGreenhouse = new Greenhouse
            {
                ProductName = request.Name,
                ProductType = request.Type,
                ProductArea = request.Area,
                ProductCode = request.Code
            };

            await _greenhouseRepositoryAsync.AddAsync(newGreenhouse);
            
            return newGreenhouse.Id;
        }
    }
}

