package com.darvi.filmhunter.presentation.profile

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Represents a single profile option item
 */
data class ProfileOption(
    val id: String,
    val title: String,
    val icon: ImageVector = Icons.Filled.AccountCircle,
    val onClick: () -> Unit,
    val usePrimaryColor: Boolean = false // If true, text and icon use primary color
)

/**
 * Represents a group of profile options with an optional title
 */
data class ProfileOptionGroup(
    val title: String? = null,
    val options: List<ProfileOption>
)

