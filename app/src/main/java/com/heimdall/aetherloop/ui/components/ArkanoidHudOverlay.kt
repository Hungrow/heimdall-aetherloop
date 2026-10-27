package com.heimdall.aetherloop.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
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
import com.heimdall.aetherloop.game.engine.VikingArkanoidEngine
import com.heimdall.aetherloop.ui.theme.*

/**
 * Modern AetherLoop Arkanoid HUD.
 *
 * Displays lives, score, wave progress, active relic buffs, and guaranteed-accessible settings button.
 */
@Composable
fun ArkanoidHudOverlay(
    engine: VikingArkanoidEngine,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val wave = engine.currentWave.value
    val lives = engine.lives.intValue
    val score = engine.score.intValue
    val isBallAttached = engine.isBallAttached.value
    val hasBallistas = engine.paddle.hasBallistas

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // TOP STATUS BAR (Guaranteed safe from camera notch)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(CosmosSurface.copy(alpha = 0.94f))
                .border(1.dp, CosmosBorder, RoundedCornerShape(18.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Wave & Title
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "AETHERLOOP",
                        color = HeimdallGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• OLEADA ${wave.waveNumber}: ${engine.battleMap.value.runeSymbol} ${engine.battleMap.value.realmName.uppercase()}",
                        color = BifrostCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Puntos: $score",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black
                )
            }

            // Lives & Safe Settings Button
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Valkyrie Lives Counter
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(CosmosCard)
                        .border(1.dp, Color(0xFFFF4081).copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = Color(0xFFFF4081),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "x$lives",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Prominent Safe Settings Button (Large touch target 44dp)
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(CosmosCard)
                        .border(1.dp, HeimdallGold.copy(alpha = 0.6f), CircleShape)
                        .clickable(onClick = onOpenSettings),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Ajustes",
                        tint = HeimdallGold,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // COMBO MULTIPLIER BANNER
        if (engine.comboStreak.intValue >= 2) {
            val comboCount = engine.comboStreak.intValue
            val mult = engine.comboMultiplier
            Row(
                modifier = Modifier
                    .padding(top = 6.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF261000).copy(alpha = 0.90f))
                    .border(1.5.dp, Color(0xFFFF9100), RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🔥 COMBO $comboCount • x$mult FURIA VIKINGA",
                    color = HeimdallGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }
        }

        // BOTTOM CONTROLS & LAUNCH PROMPT
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Ballistas active indicator
            if (hasBallistas) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(ValkyrieViolet.copy(alpha = 0.85f))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "⚡ BALISTAS GEMELAS ACTIVAS • TOCA PARA DISPARAR",
                        color = CosmosDark,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            // Launch prompt if ball is waiting on paddle
            if (isBallAttached) {
                val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                val pulseScale by infiniteTransition.animateFloat(
                    initialValue = 1.0f,
                    targetValue = 1.04f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(750, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "scale"
                )

                Button(
                    onClick = { engine.launchBall() },
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(54.dp)
                        .scale(pulseScale),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(HeimdallGold, BifrostCyan)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = CosmosDark)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "¡TOCA PARA LANZAR LA ESFERA RÚNICA!",
                                color = CosmosDark,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            } else {
                Text(
                    text = "Arrastra el barco para devolver la esfera • Destruye la flota enemiga",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
