using System.Collections.Generic;

namespace CleanArchitecture.Core.DTOs.ElectronicCard
{
    public class ElectronicCardListDto
    {
        public List<Entities.ElectronicCard> Cards { get; set; }
        public int TotalCount { get; set; }
        public int PageSize { get; set; }
        public int CurrentPage { get; set; }
        public int TotalPages { get; set; }
    }
}
