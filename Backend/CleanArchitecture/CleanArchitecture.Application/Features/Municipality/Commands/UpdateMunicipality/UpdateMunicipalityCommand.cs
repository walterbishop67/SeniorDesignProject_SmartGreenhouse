using CleanArchitecture.Core.Exceptions;
using CleanArchitecture.Core.Interfaces.Repositories;
using CleanArchitecture.Core.Wrappers;
using MediatR;
using System.Threading;
using System.Threading.Tasks;

namespace CleanArchitecture.Core.Features.Municipality.Commands.UpdateMunicipality
{
    public class UpdateMunicipalityCommand : IRequest<int>
    {
        public int Id { get; set; }
        public string MunicipalityName { get; set; } 
        
        public class UpdateMunicipalityCommandHandler : IRequestHandler<UpdateMunicipalityCommand, int>
        {
            private readonly IMunicipalityRepositoryAsync _municipalityRepository;
            public UpdateMunicipalityCommandHandler(IMunicipalityRepositoryAsync municipalityRepository)
            {
                _municipalityRepository = municipalityRepository;
            }
            public async Task<int> Handle(UpdateMunicipalityCommand command, CancellationToken cancellationToken)
            {
                var municipality = await _municipalityRepository.GetByIdAsync(command.Id);

                if (municipality == null) throw new EntityNotFoundException("municipality", command.Id);
                
                municipality.MunicipalityName = command.MunicipalityName;
                await _municipalityRepository.UpdateAsync(municipality);
                return municipality.Id;
            }
        }
    }
}