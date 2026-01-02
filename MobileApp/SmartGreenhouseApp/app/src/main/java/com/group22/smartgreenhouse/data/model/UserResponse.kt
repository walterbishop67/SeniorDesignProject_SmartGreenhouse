package com.group22.smartgreenhouse.data.model

data class UserResponse(
    val users: List<User>,
    val totalCount: Int,
    val pageSize: Int,
    val currentPage: Int,
    val totalPages: Int
)

data class User(
    val id: String,
    val userName: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String?,
    val roles: List<String>
)
