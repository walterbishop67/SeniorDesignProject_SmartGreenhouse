using CleanArchitecture.Core.Exceptions;
using CleanArchitecture.Core.Interfaces.Repositories;
using CleanArchitecture.Core.Wrappers;
using MediatR;
using System.Threading;
using System.Threading.Tasks;

namespace CleanArchitecture.Core.Features.Greenhouses.Command.DeleteGreenhouseById
{
    public class DeleteGreenhouseByIdCommand : IRequest<int>
    {
        public int Id { get; set; }

        public class DeleteGreenhouseByIdCommandHandler : IRequestHandler<DeleteGreenhouseByIdCommand, int>
        {
            private readonly IGreenhouseRepositoryAsync _greenhouseRepository;

            public DeleteGreenhouseByIdCommandHandler(IGreenhouseRepositoryAsync greenhouseRepository)
            {
                _greenhouseRepository = greenhouseRepository;
            }

            public async Task<int> Handle(DeleteGreenhouseByIdCommand command, CancellationToken cancellationToken)
            {
                var greenhouse = await _greenhouseRepository.GetByIdAsync(command.Id);
                if (greenhouse == null)
                    throw new ApiException($"Greenhouse Not Found.");

                await _greenhouseRepository.DeleteAsync(greenhouse);
                return greenhouse.Id;
            }
        }
    }
}