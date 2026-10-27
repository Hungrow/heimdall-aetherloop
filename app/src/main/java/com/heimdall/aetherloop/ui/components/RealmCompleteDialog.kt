package com.heimdall.aetherloop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.heimdall.aetherloop.game.model.FleetWave
import com.heimdall.aetherloop.ui.theme.*

/**
 * Wave Victory Celebration Dialog with 3-Star Rating and High Scores.
 */
@Composable
fun RealmCompleteDialog(
    wave: FleetWave,
    score: Int,
    stars: Int,
    isNewHighScore: Boolean,
    onNextLevel: () -> Unit,
    onReplay: () -> Unit,
    onReturnToMenu: () -> Unit
) {
    Dialog(onDismissRequest = {}) {
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = CosmosSurface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .border(1.5.dp, HeimdallGold, RoundedCornerShape(26.dp))
        ) {
            Column(
                modifier = Modifier
                    .padding(22.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(HeimdallGold, BifrostCyan)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = CosmosDark,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Text(
                    text = "¡VICTORIA EN EL FIORDO!",
                    color = HeimdallGold,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )

                // 3 Stars Rating
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 1..3) {
                        val isLit = i <= stars
                        Icon(
                            imageVector = if (isLit) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = if (isLit) HeimdallGold else TextMuted,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Text(
                    text = "Has destruido la flota invasora y asegurado el umbral del fiordo.",
                    color = TextPrimary,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(CosmosCard)
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("PUNTUACIÓN OBTENIDA", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("$score", color = BifrostCyan, fontSize = 22.sp, fontWeight = FontWeight.Black)

                    if (isNewHighScore) {
                        Text(
                            text = "★ ¡NUEVO RÉCORD DE BATALLA! ★",
                            color = HeimdallGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onReplay,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reintentar", color = TextSecondary, fontSize = 11.sp)
                    }

                    Button(
                        onClick = onNextLevel,
                        colors = ButtonDefaults.buttonColors(containerColor = HeimdallGold),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Siguiente", color = CosmosDark, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = CosmosDark, modifier = Modifier.size(16.dp))
                    }
                }

                TextButton(
                    onClick = onReturnToMenu,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Map, contentDescription = null, tint = BifrostCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("VOLVER AL MAPA DEL RÍO GJÖLL", color = BifrostCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}
