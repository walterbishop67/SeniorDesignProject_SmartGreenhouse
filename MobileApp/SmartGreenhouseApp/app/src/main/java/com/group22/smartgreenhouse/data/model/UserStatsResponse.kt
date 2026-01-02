package com.group22.smartgreenhouse.data.model

data class UserStatsResponse(
    val totalUserCount: Int,
    val roleCounts: Map<String, Int>
)
