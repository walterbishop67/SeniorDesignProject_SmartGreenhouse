package com.group22.smartgreenhouse.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ErrorResponse(
    val type: String? = null,
    val title: String? = null,
    val status: Int? = null,
    val errors: Map<String, List<String>>? = null,
    val detail: String? = null,  // Added detail field
    val traceId: String? = null
) {
    fun getUserFriendlyMessage(): String {
        return when {
            !errors.isNullOrEmpty() -> errors.values.firstOrNull()?.firstOrNull() ?: title ?: "Validation error"
            !detail.isNullOrEmpty() -> detail
            else -> title ?: "An error occurred"
        }
    }
}