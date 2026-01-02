package com.group22.smartgreenhouse.data.model

data class AgriProductPrice(
    val agriProductName: String,
    val unit: Int,
    val agriProductPrice: Double,
    val municipalityId: Int,
    val id: Int,
    val createdBy: String,
    val created: String,
    val lastModifiedBy: String?,
    val lastModified: String?
)

data class AgriProductPriceResponse(
    val pageNumber: Int,
    val pageSize: Int,
    val data: List<AgriProductPrice>
)