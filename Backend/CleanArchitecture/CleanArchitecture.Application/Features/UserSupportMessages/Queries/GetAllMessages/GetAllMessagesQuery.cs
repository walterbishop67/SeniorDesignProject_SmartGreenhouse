using AutoMapper;
using CleanArchitecture.Core.Interfaces.Repositories;
using CleanArchitecture.Core.Wrappers;
using MediatR;
using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using CleanArchitecture.Core.Entities;

namespace CleanArchitecture.Core.Features.UserSupportMessages.Queries.GetAllMessages
{
    // You can add a boolean flag for unopened messages
    public class GetAllMessagesQuery : IRequest<PagedResponse<GetAllMessagesViewModel>>
    {
        public int PageNumber { get; set; }
        public int PageSize { get; set; }
        public bool OnlyUnopened { get; set; } // Add this property to filter unopened messages
    }
    
    public class GetAllMessagesQueryHandler : IRequestHandler<GetAllMessagesQuery, PagedResponse<GetAllMessagesViewModel>>
    {
        private readonly IUserSupportMessageRepositoryAsync _userSupportMessageRepository;
        private readonly IMapper _mapper;
        
        public GetAllMessagesQueryHandler(
            IUserSupportMessageRepositoryAsync userSupportMessageRepository, 
            IMapper mapper)
        {
            _userSupportMessageRepository = userSupportMessageRepository;
            _mapper = mapper;
        }

        public async Task<PagedResponse<GetAllMessagesViewModel>> Handle(GetAllMessagesQuery request, CancellationToken cancellationToken)
        {
            // Use different repository methods based on the filter
            IEnumerable<UserSupportMessage> userSupportMessages;
            
            if (request.OnlyUnopened)
            {
                // Use your existing method for unopened messages
                userSupportMessages = await _userSupportMessageRepository.GetAllUnOpenedMessages();
                
                // Apply pagination manually since your method doesn't have paging
                userSupportMessages = userSupportMessages
                    .Skip((request.PageNumber - 1) * request.PageSize)
                    .Take(request.PageSize);
            }
            else
            {
                // Use the standard paged method for all messages
                var validFilter = _mapper.Map<GetAllMessagesParameter>(request);
                userSupportMessages = await _userSupportMessageRepository.GetPagedReponseAsync(
                    validFilter.PageNumber, 
                    validFilter.PageSize);
            }
            
            var userSupportMessageViewModel = _mapper.Map<List<GetAllMessagesViewModel>>(userSupportMessages);
            return new PagedResponse<GetAllMessagesViewModel>(
                userSupportMessageViewModel, 
                request.PageNumber, 
                request.PageSize);
        }
    }
}