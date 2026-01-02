using CleanArchitecture.Core.Enums;

namespace CleanArchitecture.Core.Entities;

public class EspProduct: AuditableBaseEntity
{
    public string EspProductName { get; set; }
    public string EspProductTemperature { get; set; } = "empty 100C";
    public string EspProductHumidity { get; set; } = "empty 100%";
    public string EspProductLastDataTime { get; set; } = "empty";
    public string ProductStatus { get; set; } = "Available"; //EspProductStatus.Available;
    public bool EspErrorState { get; set; } = false;
    public int? GreenhouseId { get; set; } // Nullable Foreign Key
}