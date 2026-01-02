package com.group22.smartgreenhouse.data.model

// Device.kt
data class ElectronicCard(
    val id: Int,
    val productName: String?,
    val greenHouseId: Int?,
    val status: String, // "Available", "Unavailable", "Error"
    val lastDataTime: String?,
    val temperature: String,
    val humidity: String,
    val errorState: String?,
    val userId: String,
    val createdBy: String,
    val created: String,
    val lastModifiedBy: String?,
    val lastModified: String?
)

data class DeviceListResponse(
    val cards: List<ElectronicCard>,
    val totalCount: Int,
    val pageSize: Int,
    val currentPage: Int,
    val totalPages: Int
)

