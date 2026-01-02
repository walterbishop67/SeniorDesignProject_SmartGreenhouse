using System.Threading;
using System.Threading.Tasks;
using CleanArchitecture.Core.Interfaces.Repositories;
using MediatR;

namespace CleanArchitecture.Core.Features.Municipality.Commands.CreateMunicipality
{
    public class CreateMunicipalityCommand: IRequest<int>
    {
        public string MunicipalityName { get; set; }
    }

    public class CreateMunicipalityCommandHandler : IRequestHandler<CreateMunicipalityCommand, int>
    {
        private readonly IMunicipalityRepositoryAsync _municipalityRepositoryAsync;
        
        public CreateMunicipalityCommandHandler(IMunicipalityRepositoryAsync municipalityRepositoryAsync)
        {
            _municipalityRepositoryAsync = municipalityRepositoryAsync;
        }

        public async Task<int> Handle(CreateMunicipalityCommand request, CancellationToken cancellationToken)
        {
            var newMunicipality = new Entities.Municipality
            {
                MunicipalityName = request.MunicipalityName,
            };

            await _municipalityRepositoryAsync.AddAsync(newMunicipality);
            
            return newMunicipality.Id;
        }
    }
}