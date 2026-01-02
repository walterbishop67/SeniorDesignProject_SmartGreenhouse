namespace CleanArchitecture.Core.Entities;

public class AgriProductsPrices: AuditableBaseEntity
{
    public string AgriProductName { get; set; }
    public int Unit { get; set; } = 1;
    public int AgriProductPrice { get; set; } = 0;
    public int? MunicipalityId { get; set; }
}