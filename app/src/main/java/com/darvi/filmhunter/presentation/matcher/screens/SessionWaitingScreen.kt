package com.darvi.filmhunter.presentation.matcher.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.dropUnlessResumed
import com.darvi.filmhunter.presentation.core.components.FilmHunterPrimaryButton
import com.darvi.filmhunter.presentation.core.components.FilmHunterSecondaryButton
import com.darvi.filmhunter.presentation.core.components.FilmHunterText

@Composable
fun SessionWaitingScreen(
    sessionCode: String,
    isHost: Boolean = true,
    hasOtherUserJoined: Boolean = false,
    onCancelSession: (() -> Unit)? = null,
    onInitiateSession: (() -> Unit)? = null,
) {
    val context = LocalContext.current
    var showCancelDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            FilmHunterText(
                text = if (isHost) "Sala creada" else "Unido a la sala",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

            FilmHunterText(
                text = if (isHost) {
                    "Comparte este código con tu amigo para que se una a la sesión"
                } else {
                    "Te has unido a la sesión correctamente"
                },
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Session Code Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilmHunterText(
                        text = sessionCode,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )

                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Session Code", sessionCode)
                            clipboard.setPrimaryClip(clip)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copiar código",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Waiting indicator or success indicator
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (hasOtherUserJoined && isHost) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Usuario unido",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                } else {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 4.dp
                    )
                }

                FilmHunterText(
                    text = when {
                        hasOtherUserJoined && isHost -> "¡Un usuario se ha unido! Listo para iniciar la sesión"
                        isHost -> "Esperando a que se una otro usuario..."
                        else -> "Esperando a que el anfitrión inicie la sesión..."
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Initiate Session Button (when user has joined) or Cancel Session Button
            if (isHost) {
                if (hasOtherUserJoined && onInitiateSession != null) {
                    Column(Modifier.fillMaxWidth()) {
                        FilmHunterSecondaryButton(
                            text = "Cancelar Sesión",
                            onClick = dropUnlessResumed {
                                showCancelDialog = true
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        FilmHunterPrimaryButton(
                            text = "Empezar Sesión",
                            onClick = dropUnlessResumed {
                                onInitiateSession()
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                } else if (onCancelSession != null) {
                    FilmHunterSecondaryButton(
                        text = "Cancelar Sesión",
                        onClick = dropUnlessResumed {
                            showCancelDialog = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Cancel Confirmation Dialog (only for host)
        if (isHost && showCancelDialog && onCancelSession != null) {
            AlertDialog(
                onDismissRequest = { showCancelDialog = false },
                title = {
                    FilmHunterText(
                        text = "Cancelar Sesión",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                text = {
                    FilmHunterText(
                        text = "¿Estás seguro de que quieres cancelar esta sesión? Esta acción no se puede deshacer.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                confirmButton = {
                    FilmHunterSecondaryButton(
                        text = "Cancelar Sesión",
                        onClick = {
                            showCancelDialog = false
                            onCancelSession()
                        }
                    )
                },
                dismissButton = {
                    FilmHunterPrimaryButton(
                        text = "Mantener Sesión",
                        onClick = { showCancelDialog = false }
                    )
                },
                containerColor = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

