package com.group22.smartgreenhouse.data

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.vector.ImageVector

data class NavItem(
    val label: String,
    @DrawableRes val icon: Int,
    val route: String
)
