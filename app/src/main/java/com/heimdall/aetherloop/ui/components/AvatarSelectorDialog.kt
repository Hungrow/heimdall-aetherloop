package com.heimdall.aetherloop.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.heimdall.aetherloop.core.AvatarCatalog
import com.heimdall.aetherloop.core.AvatarProfile
import com.heimdall.aetherloop.core.PlayerProgressManager
import com.heimdall.aetherloop.ui.theme.*

/**
 * Avatar & Player Name Customization Dialog.
 */
@Composable
fun AvatarSelectorDialog(
    progressManager: PlayerProgressManager,
    onDismiss: () -> Unit
) {
    var playerNameInput by remember { mutableStateOf(progressManager.playerName) }
    var selectedAvatarId by remember { mutableStateOf(progressManager.avatarId) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CosmosSurface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .border(1.5.dp, HeimdallGold, RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "PERFIL DEL GUERRERO",
                    color = HeimdallGold,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )

                // Editable Name TextField
                OutlinedTextField(
                    value = playerNameInput,
                    onValueChange = { playerNameInput = it.take(20) },
                    label = { Text("Nombre del Guerrero / Jarl", color = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = BifrostCyan,
                        unfocusedBorderColor = CosmosBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Selecciona tu Retrato Artístico:",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                // Grid of 6 Artistic Avatars
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    items(AvatarCatalog.ALL) { avatar ->
                        val isSelected = (avatar.id == selectedAvatarId)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) BifrostCyan.copy(alpha = 0.2f) else Color.Transparent)
                                .clickable { selectedAvatarId = avatar.id }
                                .padding(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .border(
                                        width = if (isSelected) 3.dp else 1.5.dp,
                                        color = if (isSelected) HeimdallGold else CosmosBorder,
                                        shape = CircleShape
                                    )
                            ) {
                                Image(
                                    painter = painterResource(id = avatar.drawableResId),
                                    contentDescription = avatar.name,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = avatar.name,
                                color = if (isSelected) HeimdallGold else TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Save button
                Button(
                    onClick = {
                        progressManager.playerName = playerNameInput.ifBlank { "Jarl Ragnar" }
                        progressManager.avatarId = selectedAvatarId
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HeimdallGold),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = "GUARDAR PERFIL",
                        color = CosmosDark,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
