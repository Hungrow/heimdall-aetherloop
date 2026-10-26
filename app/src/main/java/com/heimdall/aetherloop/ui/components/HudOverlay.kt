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
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.heimdall.aetherloop.game.engine.GameLoopEngine
import com.heimdall.aetherloop.game.model.AetherColor
import com.heimdall.aetherloop.ui.theme.*

/**
 * Modern Heimdall Glassmorphic HUD.
 *
 * Displays realm objectives, upcoming launch queue, speed modifiers, and launch triggers.
 */
@Composable
fun HudOverlay(
    engine: GameLoopEngine,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val level = engine.currentLevel
    val completedConduits = engine.conduits.count { it.isHarvestComplete }
    val totalConduits = engine.conduits.size

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // TOP STATUS BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(CosmosSurface.copy(alpha = 0.85f))
                .border(1.dp, CosmosBorder, RoundedCornerShape(18.dp))
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "HEIMDALL",
                        color = HeimdallGold,
                        fontSize = 11.sp,
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
                    text = level.subtitle,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Sealed Conduits Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(CosmosCard)
                        .border(1.dp, BifrostCyan.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "$completedConduits/$totalConduits SELLADOS",
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Settings Button
                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(CosmosCard)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Ajustes",
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // BOTTOM CONTROLS & DISPATCH BAR
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(CosmosSurface.copy(alpha = 0.92f))
                .border(1.dp, CosmosBorder, RoundedCornerShape(24.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Queue & Tactician Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Next Pods Queue Preview
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
                    // Hold / Swap Slot
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(CosmosCard)
                                .border(
                                    1.dp,
                                    engine.heldVesselColor?.color ?: CosmosBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { engine.swapHeldColor() },
                            contentAlignment = Alignment.Center
                        ) {
                            if (engine.heldVesselColor != null) {
                                Text(
                                    text = engine.heldVesselColor!!.runeSymbol,
                                    color = engine.heldVesselColor!!.color,
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

                    // Warp Speed Toggle
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (engine.isWarpSpeed) BifrostCyan.copy(alpha = 0.25f) else CosmosCard)
                                .border(
                                    1.dp,
                                    if (engine.isWarpSpeed) BifrostCyan else CosmosBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { engine.toggleWarpSpeed() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FastForward,
                                contentDescription = "Velocidad",
                                tint = if (engine.isWarpSpeed) BifrostCyan else TextSecondary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Text(
                            text = if (engine.isWarpSpeed) "1.6X WARP" else "1.0X VEL",
                            color = if (engine.isWarpSpeed) BifrostCyan else TextMuted,
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
                    animation = tween(900, easing = FastOutSlowInEasing),
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
                                    nextColor.color.copy(alpha = 0.8f)
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
