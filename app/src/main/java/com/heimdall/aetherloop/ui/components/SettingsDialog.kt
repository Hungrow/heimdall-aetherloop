package com.heimdall.aetherloop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.heimdall.aetherloop.core.HeimdallEcosystem
import com.heimdall.aetherloop.game.engine.GameLoopEngine
import com.heimdall.aetherloop.game.model.RealmLevel
import com.heimdall.aetherloop.ui.theme.*

/**
 * Settings & Ecosystem Dialog.
 */
@Composable
fun SettingsDialog(
    engine: GameLoopEngine,
    onDismiss: () -> Unit,
    onCheckUpdate: () -> Unit
) {
    var soundEnabled by remember { mutableStateOf(!engine.soundSynthesizer.isMuted) }
    var hapticsEnabled by remember { mutableStateOf(engine.hapticManager.isHapticsEnabled) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CosmosSurface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .border(1.dp, CosmosBorder, RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ajustes del Sistema",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = TextSecondary)
                    }
                }

                // Audio & Haptics Toggles
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(CosmosCard)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VolumeUp, contentDescription = null, tint = BifrostCyan)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Sonido ASMR Pentatónico", color = TextPrimary, fontSize = 13.sp)
                        }
                        Switch(
                            checked = soundEnabled,
                            onCheckedChange = {
                                soundEnabled = it
                                engine.soundSynthesizer.isMuted = !it
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = CosmosDark,
                                checkedTrackColor = BifrostCyan
                            )
                        )
                    }

                    Divider(color = CosmosBorder)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Vibration, contentDescription = null, tint = HeimdallGold)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Retroalimentación Háptica", color = TextPrimary, fontSize = 13.sp)
                        }
                        Switch(
                            checked = hapticsEnabled,
                            onCheckedChange = {
                                hapticsEnabled = it
                                engine.hapticManager.isHapticsEnabled = it
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = CosmosDark,
                                checkedTrackColor = HeimdallGold
                            )
                        )
                    }
                }

                // Level Selector
                Text(
                    text = "SELECCIÓN DE REINO",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    for (i in RealmLevel.ALL_LEVELS.indices) {
                        val isSelected = engine.levelIndex.value == i
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) BifrostCyan else CosmosCard)
                                .clickable {
                                    engine.loadLevel(i)
                                    onDismiss()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${i + 1}",
                                color = if (isSelected) CosmosDark else TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // Restart current level
                OutlinedButton(
                    onClick = {
                        engine.restartCurrentLevel()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = TextSecondary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reiniciar Nivel Actual", color = TextSecondary)
                }

                // In-Device GitHub Update Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(CosmosCard)
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Ecosistema Heimdall", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Versión instalada: v${HeimdallEcosystem.VERSION_NAME}", color = TextSecondary, fontSize = 11.sp)
                        }
                        IconButton(
                            onClick = onCheckUpdate,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(BifrostCyan.copy(alpha = 0.2f))
                        ) {
                            Icon(Icons.Default.SystemUpdate, contentDescription = "Actualizar", tint = BifrostCyan)
                        }
                    }

                    Button(
                        onClick = onCheckUpdate,
                        colors = ButtonDefaults.buttonColors(containerColor = BifrostCyan),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Buscar Actualizaciones en GitHub", color = CosmosDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
