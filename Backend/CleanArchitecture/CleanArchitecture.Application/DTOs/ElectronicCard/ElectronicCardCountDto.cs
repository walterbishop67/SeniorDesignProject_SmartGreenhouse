namespace CleanArchitecture.Core.DTOs.ElectronicCard
{
    public class ElectronicCardCountDto
    {
        public int TotalCount { get; set; }
        public int AvailableCount { get; set; }
        public int UnavailableCount { get; set; }
        public int ErrorCount { get; set; }
    }
}