using CleanArchitecture.Core.Exceptions;
using CleanArchitecture.Core.Interfaces.Repositories;
using CleanArchitecture.Core.Wrappers;
using MediatR;
using System.Threading;
using System.Threading.Tasks;

namespace CleanArchitecture.Core.Features.Municipality.Commands.DeleteMunicipalityById
{
    public class DeleteMunicipalityByIdCommand : IRequest<int>
    {
        public int Id { get; set; }
        public class DeleteMunicipalityByIdCommandHandler : IRequestHandler<DeleteMunicipalityByIdCommand, int>
        {
            private readonly IMunicipalityRepositoryAsync _municipalityRepository;
            public DeleteMunicipalityByIdCommandHandler(IMunicipalityRepositoryAsync municipalityRepository)
            {
                _municipalityRepository = municipalityRepository;
            }
            public async Task<int> Handle(DeleteMunicipalityByIdCommand command, CancellationToken cancellationToken)
            {
                var municipality = await _municipalityRepository.GetByIdAsync(command.Id);
                if (municipality == null) throw new ApiException($"Municipality Not Found.");
                await _municipalityRepository.DeleteAsync(municipality);
                return municipality.Id;
            }
        }
    }
}