package com.group22.smartgreenhouse.data.model

data class AuthResponse(
    val id: String,
    val userName: String,
    val email: String,
    val roles: List<String>,
    val isVerified: Boolean,
    val jwToken: String
)

data class UserBasicInfo(
    val firstName: String,
    val lastName: String,
    val email: String,
    val userName: String
)
