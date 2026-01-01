package com.darvi.filmhunter.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.darvi.filmhunter.presentation.core.components.FilmHunterPrimaryButton
import com.darvi.filmhunter.presentation.core.components.FilmHunterSecondaryButton
import com.darvi.filmhunter.presentation.core.components.FilmHunterText

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    var showLogoutDialog by remember { mutableStateOf(false) }
    
    val optionGroups = remember {
        getDefaultProfileOptions { showLogoutDialog = true }
    }

    // Show logout dialog when requested
    if (showLogoutDialog) {
        LogoutConfirmationDialog(
            onConfirm = {
                showLogoutDialog = false
                viewModel.onSignOut()
            },
            onDismiss = {
                showLogoutDialog = false
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // User Header Section
            item {
                UserHeaderSection(
                    userName = currentUser?.name ?: "Usuario",
                    email = currentUser?.email ?: ""
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Profile Options Groups
            items(optionGroups) { group ->
                ProfileOptionGroupSection(
                    group = group,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Add spacing between groups
                if (optionGroups.last() != group) {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun UserHeaderSection(
    userName: String,
    email: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // User Icon
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape),
//                    .background(MaterialTheme.colorScheme.onSurface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "User Avatar",
                    modifier = Modifier.size(48.dp),
                )
            }

            // User Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                FilmHunterText(
                    text = userName,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                FilmHunterText(
                    text = email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun ProfileOptionGroupSection(
    group: ProfileOptionGroup,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Group Title (if provided)
        if (group.title != null) {
            FilmHunterText(
                text = group.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Options Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column {
                group.options.forEachIndexed { index, option ->
                    ProfileOptionItem(
                        option = option,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    // Add divider between items (except last)
                    if (index < group.options.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileOptionItem(
    option: ProfileOption,
    modifier: Modifier = Modifier
) {
    val iconColor = if (option.usePrimaryColor) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.primary
    }
    
    val textColor = if (option.usePrimaryColor) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    
    Row(
        modifier = modifier
            .clickable(onClick = option.onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = option.icon,
            contentDescription = option.title,
            modifier = Modifier.size(24.dp),
            tint = iconColor
        )

        FilmHunterText(
            text = option.title,
            style = MaterialTheme.typography.bodyLarge,
            color = textColor,
            modifier = Modifier.weight(1f)
        )

        if (!option.usePrimaryColor) {
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun LogoutConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            FilmHunterText(
                text = "Cerrar sesión",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            FilmHunterText(
                text = "¿Estás seguro de que quieres cerrar sesión?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        confirmButton = {
            FilmHunterSecondaryButton(
                text = "Cerrar sesión",
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth()
            )
        },
        dismissButton = {
            FilmHunterPrimaryButton(
                text = "Cancelar",
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            )
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(24.dp)
    )
}

/**
 * Default profile options - easily customizable and extensible
 * To add new options, simply add them to the appropriate group or create a new group
 */
private fun getDefaultProfileOptions(
    onShowLogoutDialog: () -> Unit
): List<ProfileOptionGroup> {
    return listOf(
        ProfileOptionGroup(
            title = "Cuenta",
            options = listOf(
                ProfileOption(
                    id = "edit_profile",
                    title = "Editar perfil",
                    icon = Icons.Filled.Edit,
                    onClick = { /* TODO: Navigate to edit profile */ }
                ),
                ProfileOption(
                    id = "notifications",
                    title = "Notificaciones",
                    icon = Icons.Filled.Notifications,
                    onClick = { /* TODO: Navigate to notifications */ }
                ),
                ProfileOption(
                    id = "privacy",
                    title = "Privacidad",
                    icon = Icons.Filled.Lock,
                    onClick = { /* TODO: Navigate to privacy */ }
                ),
            )
        ),
        ProfileOptionGroup(
            title = "General",
            options = listOf(
                ProfileOption(
                    id = "help",
                    title = "Ayuda y soporte",
                    icon = Icons.AutoMirrored.Filled.Help,
                    onClick = { /* TODO: Navigate to help */ }
                ),
                ProfileOption(
                    id = "about",
                    title = "Acerca de",
                    icon = Icons.Filled.Info,
                    onClick = { /* TODO: Navigate to about */ }
                )
            )
        ),
        ProfileOptionGroup(
            title = "Ajustes",
            options = listOf(
                ProfileOption(
                    id = "settings",
                    title = "Configuración",
                    icon = Icons.Filled.Settings,
                    onClick = { /* TODO: Navigate to settings */ }
                )
            )
        ),
        ProfileOptionGroup(
            title = null,
            options = listOf(
                ProfileOption(
                    id = "logout",
                    title = "Cerrar sesión",
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    onClick = onShowLogoutDialog,
                    usePrimaryColor = true
                )
            )
        )
    )
}
