package com.group22.smartgreenhouse.data.model

data class RegisterRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val userName: String,
    val password: String,
    val confirmPassword: String
)
