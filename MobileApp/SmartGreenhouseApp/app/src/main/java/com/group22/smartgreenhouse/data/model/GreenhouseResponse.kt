package com.group22.smartgreenhouse.data.model

data class GreenhouseResponse(
    val productName: String,
    val productType: String,
    val productArea: String,
    val productCode: String,
    val id: Int,
    val createdBy: String,
    val created: String
)

data class DeleteResponse(
    val deleteResult: Int,
    val statusUpdateResult: Int,
    val message: String
)