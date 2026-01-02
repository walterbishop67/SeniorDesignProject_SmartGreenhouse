package com.group22.smartgreenhouse.data.model

data class ResetPasswordRequest(
    val email: String,
    val token: String,
    val password: String,
    val confirmPassword: String
)

data class ResetPasswordResponse(
    val success: Boolean,
    val message: String
)