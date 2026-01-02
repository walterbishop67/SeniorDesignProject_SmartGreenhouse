namespace CleanArchitecture.Core.Features.UserSupportMessages.Queries.GetAllMessages;

public class GetAllMessagesViewModel
{
    public int Id { get; set; }
    public string Subject { get; set; }
    public string CreatedBy { get; set; }
    public bool isResponsed { get; set; }
    //public string MessageContent { get; set; }
    //public string SentAt { get; set; }
}