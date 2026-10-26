package com.heimdall.aetherloop.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.heimdall.aetherloop.game.engine.GameLoopEngine
import com.heimdall.aetherloop.game.model.AetherColor
import com.heimdall.aetherloop.game.model.AetherPower
import com.heimdall.aetherloop.ui.theme.*

/**
 * Modern AetherLoop Glassmorphic HUD.
 *
 * Displays realm objectives, combo counters, relic powers, queue preview,
 * and dispatch triggers.
 */
@Composable
fun HudOverlay(
    engine: GameLoopEngine,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val level = engine.currentLevel
    val clearedPercent = (engine.clearedProgress.floatValue * 100f).toInt()
    val combo = engine.comboMultiplier.intValue

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // TOP STATUS BAR & COMBO
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(CosmosSurface.copy(alpha = 0.90f))
                .border(1.dp, CosmosBorder, RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "AETHERLOOP",
                            color = HeimdallGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "• ${level.realmName.uppercase()}",
                            color = BifrostCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "Puntos: ${engine.currentScore.intValue}",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Combo Badge
                    if (combo > 1) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Brush.horizontalGradient(listOf(HeimdallGold, ValkyrieViolet)))
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "x$combo COMBO",
                                color = CosmosDark,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    // Cleared Progress Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(CosmosCard)
                            .border(1.dp, BifrostCyan.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "$clearedPercent% DISUELTO",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(CosmosCard)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Ajustes",
                            tint = TextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Linear Progress Bar for Artwork Deconstruction
            LinearProgressIndicator(
                progress = { engine.clearedProgress.floatValue },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = HeimdallGold,
                trackColor = CosmosCard
            )
        }

        // RELIC POWERS & BOTTOM CONTROLS
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // RELIC POWERS ROW (Chrono Stasis & Gjallarhorn)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Chrono Stasis Button
                RelicPowerButton(
                    power = AetherPower.CHRONO_STASIS,
                    chargeProgress = engine.chronoCharge.floatValue,
                    isActive = engine.isChronoActive.value,
                    activeColor = BifrostCyan,
                    onClick = { engine.activateChronoStasis() }
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Gjallarhorn Surge Button
                RelicPowerButton(
                    power = AetherPower.GJALLARHORN_SURGE,
                    chargeProgress = engine.gjallarhornCharge.floatValue,
                    isActive = engine.isGjallarhornActive.value,
                    activeColor = HeimdallGold,
                    onClick = { engine.activateGjallarhorn() }
                )
            }

            // BOTTOM CONTROLS DOCK
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(CosmosSurface.copy(alpha = 0.94f))
                    .border(1.dp, CosmosBorder, RoundedCornerShape(24.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Queue & Tactical Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "SIGUIENTES EN COLA",
                            color = TextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            val previewList = engine.vesselQueue.take(3)
                            for (color in previewList) {
                                QueuePodChip(color = color)
                            }
                        }
                    }

                    // Tactical Actions: Swap & Warp Speed
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Hold Slot
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CosmosCard)
                                    .border(
                                        1.dp,
                                        engine.heldVesselColor.value?.color ?: CosmosBorder,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { engine.swapHeldColor() },
                                contentAlignment = Alignment.Center
                            ) {
                                val held = engine.heldVesselColor.value
                                if (held != null) {
                                    Text(
                                        text = held.runeSymbol,
                                        color = held.color,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.SwapHoriz,
                                        contentDescription = "Cambiar",
                                        tint = TextMuted,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Text(
                                text = "CAMBIAR",
                                color = TextMuted,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Warp Speed
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val isWarp = engine.isWarpSpeed.value
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isWarp) BifrostCyan.copy(alpha = 0.25f) else CosmosCard)
                                    .border(
                                        1.dp,
                                        if (isWarp) BifrostCyan else CosmosBorder,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { engine.toggleWarpSpeed() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FastForward,
                                    contentDescription = "Velocidad",
                                    tint = if (isWarp) BifrostCyan else TextSecondary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Text(
                                text = if (isWarp) "1.6X WARP" else "1.0X VEL",
                                color = if (isWarp) BifrostCyan else TextMuted,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // PRIMARY LAUNCH BUTTON
                val nextColor = engine.vesselQueue.firstOrNull() ?: AetherColor.SOLAR_GOLD

                val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                val pulseScale by infiniteTransition.animateFloat(
                    initialValue = 1.0f,
                    targetValue = 1.02f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(850, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "scale"
                )

                Button(
                    onClick = { engine.dispatchNextVessel() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .scale(pulseScale),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    nextColor.color,
                                    nextColor.color.copy(alpha = 0.85f)
                                )
                            )
                        ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = CosmosDark,
                                modifier = Modifier.size(26.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "DESPACHAR VASIJA ${nextColor.displayName.uppercase()}",
                                color = CosmosDark,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RelicPowerButton(
    power: AetherPower,
    chargeProgress: Float,
    isActive: Boolean,
    activeColor: Color,
    onClick: () -> Unit
) {
    val isReady = chargeProgress >= 1.0f || isActive

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(if (isActive) activeColor else CosmosCard)
                .border(
                    2.dp,
                    if (isReady) activeColor else CosmosBorder,
                    CircleShape
                )
                .clickable(enabled = isReady, onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            if (!isReady) {
                CircularProgressIndicator(
                    progress = { chargeProgress },
                    color = activeColor,
                    trackColor = Color.Transparent,
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 3.dp
                )
            }
            Text(
                text = power.runeSymbol,
                color = if (isActive) CosmosDark else if (isReady) activeColor else TextMuted,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
        }
        Text(
            text = if (isActive) "ACTIVO" else if (isReady) "¡LISTO!" else "${(chargeProgress * 100).toInt()}%",
            color = if (isReady) activeColor else TextMuted,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun QueuePodChip(color: AetherColor) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(CosmosCard)
            .border(1.5.dp, color.color, RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = color.runeSymbol,
            color = color.color,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
