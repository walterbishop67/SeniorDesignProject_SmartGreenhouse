package com.group22.smartgreenhouse.data.model

data class ForgotPasswordRequest(
    val email: String
)

data class ForgotPasswordResponse(
    val message: String,
    val emailSent: Boolean
)