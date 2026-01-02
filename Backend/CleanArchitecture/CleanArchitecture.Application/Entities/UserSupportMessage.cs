namespace CleanArchitecture.Core.Entities;

public class UserSupportMessage: AuditableBaseEntity
{
    public string Subject { get; set; }
    public string MessageContent { get; set; }
    public string SentAt { get; set; }
    public string MessageResponse { get; set; } = "";
    public bool isResponsed { get; set; } = true;
}