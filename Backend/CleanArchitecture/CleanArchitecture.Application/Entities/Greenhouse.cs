namespace CleanArchitecture.Core.Entities;

public class Greenhouse: AuditableBaseEntity
{
    public string ProductName { get; set; }
    public string ProductType { get; set; }
    public string ProductArea { get; set; }
    public string ProductCode { get; set; }
}