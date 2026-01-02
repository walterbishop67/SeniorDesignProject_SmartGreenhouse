using AutoMapper;
using CleanArchitecture.Core.Interfaces.Repositories;
using CleanArchitecture.Core.Wrappers;
using MediatR;
using System.Collections.Generic;
using System.Threading;
using System.Threading.Tasks;
using System.Linq;

namespace CleanArchitecture.Core.Features.UserSupportMessages.Queries.GetAllUnOpenedMessages
{
    public class GetAllUnOpenedMessagesQuery : IRequest<PagedResponse<GetAllUnOpenedMessagesViewModel>>
    {
        public int PageNumber { get; set; }
        public int PageSize { get; set; }
        public bool? IsResponsed { get; set; } // Add this property to filter by response status
    }
    
    public class GetAllUnOpenedMessagesQueryHandler : IRequestHandler<GetAllUnOpenedMessagesQuery, PagedResponse<GetAllUnOpenedMessagesViewModel>>
    {
        private readonly IUserSupportMessageRepositoryAsync _userSupportMessageRepository;
        private readonly IMapper _mapper;
        
        public GetAllUnOpenedMessagesQueryHandler(
            IUserSupportMessageRepositoryAsync userSupportMessageRepository, 
            IMapper mapper)
        {
            _userSupportMessageRepository = userSupportMessageRepository;
            _mapper = mapper;
        }

        public async Task<PagedResponse<GetAllUnOpenedMessagesViewModel>> Handle(GetAllUnOpenedMessagesQuery request, CancellationToken cancellationToken)
        {
            var validFilter = _mapper.Map<GetAllUnOpenedMessagesParameter>(request);
            
            // Get all messages first
            var userSupportMessages = await _userSupportMessageRepository.GetPagedReponseAsync(validFilter.PageNumber, validFilter.PageSize);
            
            // Apply the filter if IsResponsed is provided
            if (request.IsResponsed.HasValue)
            {
                userSupportMessages = userSupportMessages.Where(m => m.isResponsed == request.IsResponsed.Value).ToList();
            }
            
            var userSupportMessageViewModel = _mapper.Map<List<GetAllUnOpenedMessagesViewModel>>(userSupportMessages);
            return new PagedResponse<GetAllUnOpenedMessagesViewModel>(userSupportMessageViewModel, validFilter.PageNumber, validFilter.PageSize);
        }
    }
}