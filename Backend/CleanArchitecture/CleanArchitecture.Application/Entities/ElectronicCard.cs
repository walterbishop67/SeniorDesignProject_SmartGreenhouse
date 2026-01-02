namespace CleanArchitecture.Core.Entities;

public class ElectronicCard : AuditableBaseEntity
{
    public string ProductName { get; set; }
    public int? GreenHouseId { get; set; }
    public string Status { get; set; }
    public string LastDataTime { get; set; }
    public string Temperature { get; set; }
    public string Humidity { get; set; }
    public string ErrorState { get; set; }
    public string UserId { get; set; }
}