package com.group22.smartgreenhouse.data.model

// DTOs
data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String,
    val confirmNewPassword: String
)

data class ChangePasswordResponse(val message: String)
