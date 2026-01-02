package com.group22.smartgreenhouse.data.model

data class SupportMessageRequest(
    val subject: String,
    val messageContent: String,
    val sentAt: String
)
