package com.heimdall.aetherloop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.heimdall.aetherloop.core.HeimdallEcosystem
import com.heimdall.aetherloop.game.engine.VikingArkanoidEngine
import com.heimdall.aetherloop.ui.theme.*

/**
 * Settings & Fleet Wave Selector Dialog with Audio, Return to Menu, and OTA Updater.
 */
@Composable
fun SettingsDialog(
    engine: VikingArkanoidEngine,
    onDismiss: () -> Unit,
    onReturnToMenu: () -> Unit,
    onCheckUpdate: () -> Unit
) {
    var soundEnabled by remember { mutableStateOf(!engine.soundSynthesizer.isMuted) }
    var musicEnabled by remember { mutableStateOf(!engine.soundSynthesizer.isMusicMuted) }
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
                    Column {
                        Text(
                            text = "PAUSA Y AJUSTES",
                            color = HeimdallGold,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Partida en pausa",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = TextSecondary)
                    }
                }

                // Resume Game Button
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = HeimdallGold),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = CosmosDark)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("CONTINUAR COMBATE", color = CosmosDark, fontWeight = FontWeight.Black, fontSize = 13.sp)
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
                    // Sound SFX
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = BifrostCyan)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Efectos de Sonido", color = TextPrimary, fontSize = 13.sp)
                        }
                        Switch(
                            checked = soundEnabled,
                            onCheckedChange = {
                                soundEnabled = it
                                engine.soundSynthesizer.isMuted = !it
                                engine.progressManager.setSoundEnabled(it)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = CosmosDark,
                                checkedTrackColor = BifrostCyan
                            )
                        )
                    }

                    HorizontalDivider(color = CosmosBorder)

                    // Background Music (BGM)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MusicNote, contentDescription = null, tint = HeimdallGold)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Música de Fondo (BGM)", color = TextPrimary, fontSize = 13.sp)
                        }
                        Switch(
                            checked = musicEnabled,
                            onCheckedChange = {
                                musicEnabled = it
                                engine.soundSynthesizer.isMusicMuted = !it
                                engine.progressManager.setMusicEnabled(it)
                                if (it) engine.soundSynthesizer.startBattleMusic() else engine.soundSynthesizer.stopMusic()
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = CosmosDark,
                                checkedTrackColor = HeimdallGold
                            )
                        )
                    }

                    HorizontalDivider(color = CosmosBorder)

                    // Haptics
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Vibration, contentDescription = null, tint = ValkyrieViolet)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Retroalimentación Háptica", color = TextPrimary, fontSize = 13.sp)
                        }
                        Switch(
                            checked = hapticsEnabled,
                            onCheckedChange = {
                                hapticsEnabled = it
                                engine.hapticManager.isHapticsEnabled = it
                                engine.progressManager.setHapticsEnabled(it)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = CosmosDark,
                                checkedTrackColor = ValkyrieViolet
                            )
                        )
                    }
                }

                // Return to Main Menu / River Map Button
                Button(
                    onClick = {
                        onDismiss()
                        onReturnToMenu()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CosmosCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BifrostCyan),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Map, contentDescription = null, tint = BifrostCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("VOLVER AL MAPA DEL RÍO GJÖLL", color = BifrostCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                // Restart current level
                OutlinedButton(
                    onClick = {
                        engine.restartCurrentWave()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = TextSecondary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reiniciar Batalla Actual", color = TextSecondary)
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
                            Text("AetherLoop: Viking Combat", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
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
