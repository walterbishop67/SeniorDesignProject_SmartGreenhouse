namespace CleanArchitecture.Core.DTOs.ElectronicCard;

public class ElectronicCardStatusDto
{
    public int Id { get; set; }

    public string Temperature { get; set; }
    public string Humidity { get; set; }
    public string ErrorState { get; set; }
}