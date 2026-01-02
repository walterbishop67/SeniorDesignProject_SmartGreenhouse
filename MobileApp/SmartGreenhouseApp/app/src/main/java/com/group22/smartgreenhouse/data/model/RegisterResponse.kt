package com.group22.smartgreenhouse.data.model

data class RegisterResponse(
    val message: String?,
    val errors: List<String>?,
    val confirmationUrl: String? = null
) {
    fun requiresConfirmation(): Boolean {
        return message?.contains("confirm your account") == true
    }
}