using CleanArchitecture.Core.Filters;

namespace CleanArchitecture.Core.Features.UserSupportMessages.Queries.GetAllMessages;

public class GetAllMessagesParameter: RequestParameter
{
    public bool OnlyUnopened { get; set; } = false;
}