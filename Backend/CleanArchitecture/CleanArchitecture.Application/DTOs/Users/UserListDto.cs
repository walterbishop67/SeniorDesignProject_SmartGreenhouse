using System.Collections.Generic;

namespace CleanArchitecture.Core.DTOs.Users;

public class UserListDto
{
    public List<UserDto> Users { get; set; }
    public int TotalCount { get; set; }
    public int PageSize { get; set; }
    public int CurrentPage { get; set; }
    public int TotalPages { get; set; }
}