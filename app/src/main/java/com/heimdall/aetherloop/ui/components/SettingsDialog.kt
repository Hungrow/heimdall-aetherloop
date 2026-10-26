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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.automirrored.filled.VolumeUp
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
import com.heimdall.aetherloop.game.engine.GameLoopEngine
import com.heimdall.aetherloop.game.model.RealmLevel
import com.heimdall.aetherloop.ui.theme.*

/**
 * Settings, Level Selector & Progress Dialog.
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
                        text = "Ajustes de AetherLoop",
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
                            Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = BifrostCyan)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Sonido ASMR Pentatónico", color = TextPrimary, fontSize = 13.sp)
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
                                engine.progressManager.setHapticsEnabled(it)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = CosmosDark,
                                checkedTrackColor = HeimdallGold
                            )
                        )
                    }
                }

                // Level Selector with Stars & Unlock check
                Text(
                    text = "MAPA DE REINOS Y ESTRELLAS",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val highestUnlocked = engine.progressManager.highestUnlockedRealm

                    for (i in RealmLevel.ALL_LEVELS.indices) {
                        val level = RealmLevel.ALL_LEVELS[i]
                        val isUnlocked = i <= highestUnlocked || i == 0
                        val isCurrent = engine.levelIndex.value == i
                        val stars = engine.progressManager.getRealmStars(i)
                        val highScore = engine.progressManager.getRealmHighScore(i)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isCurrent) BifrostCyan.copy(alpha = 0.2f) else CosmosCard)
                                .border(
                                    1.dp,
                                    if (isCurrent) BifrostCyan else CosmosBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable(enabled = isUnlocked) {
                                    engine.loadLevel(i)
                                    onDismiss()
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (!isUnlocked) {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                                Column {
                                    Text(
                                        text = "${i + 1}. ${level.realmName}",
                                        color = if (isUnlocked) TextPrimary else TextMuted,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    if (highScore > 0) {
                                        Text(
                                            text = "Récord: $highScore pts",
                                            color = TextSecondary,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                for (s in 1..3) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = if (s <= stars) HeimdallGold else Color(0x33FFFFFF),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
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
                            Text("AetherLoop • Ecosistema Heimdall", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
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
