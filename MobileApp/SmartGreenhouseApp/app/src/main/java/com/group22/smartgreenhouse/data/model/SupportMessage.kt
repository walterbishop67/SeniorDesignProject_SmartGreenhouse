package com.group22.smartgreenhouse.data.model

data class SupportMessage(
    val id: Int,
    val subject: String,
    val messageContent: String,
    val createdBy: String,
    val isOpened: Boolean = false,
    val isResponsed: Boolean = false,
    val messageResponse: String? = null,
    val created: String? = null,
    val lastModified: String? = null
)

data class SupportMessageResponse(
    val pageNumber: Int,
    val pageSize: Int,
    val data: List<SupportMessage>
)

data class SupportMessageResponseRequest(
    val id: Int,
    val messageResponse: String
)