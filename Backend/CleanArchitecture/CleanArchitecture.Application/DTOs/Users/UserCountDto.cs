using System.Collections.Generic;

namespace CleanArchitecture.Core.DTOs.Users;

public class UserCountDto
{
    public int TotalUserCount { get; set; }
    public Dictionary<string, int> RoleCounts { get; set; }
}