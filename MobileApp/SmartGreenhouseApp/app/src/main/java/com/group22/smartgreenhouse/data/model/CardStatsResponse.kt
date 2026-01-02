package com.group22.smartgreenhouse.data.model

data class CardStatsResponse(
    val totalCount: Int,
    val availableCount: Int,
    val unavailableCount: Int,
    val errorCount: Int
)