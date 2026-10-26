package com.heimdall.aetherloop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
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
import com.heimdall.aetherloop.game.model.RealmLevel
import com.heimdall.aetherloop.ui.theme.*

/**
 * Realm Victory Celebration Dialog with 3-Star Rating and High Scores.
 */
@Composable
fun RealmCompleteDialog(
    level: RealmLevel,
    score: Int,
    stars: Int,
    isNewHighScore: Boolean,
    onNextLevel: () -> Unit,
    onReplay: () -> Unit
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
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Celebration Icon
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(18.dp))
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
                    text = "¡REINO ESTABILIZADO!",
                    color = HeimdallGold,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )

                // 3 STARS ROW
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 1..3) {
                        val isAchieved = i <= stars
                        Icon(
                            imageVector = if (isAchieved) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = if (isAchieved) HeimdallGold else TextMuted,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Text(
                    text = "Has disuelto el mosaico sagrado de ${level.realmName} y sellado el portal.",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )

                // Score Panel
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(CosmosCard)
                        .padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("PUNTUACIÓN OBTENIDA", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("$score", color = BifrostCyan, fontSize = 24.sp, fontWeight = FontWeight.Black)

                    if (isNewHighScore) {
                        Text(
                            text = "★ ¡NUEVO RÉCORD DEL REINO! ★",
                            color = HeimdallGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onReplay,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = TextSecondary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reintentar", color = TextSecondary)
                    }

                    Button(
                        onClick = onNextLevel,
                        colors = ButtonDefaults.buttonColors(containerColor = HeimdallGold),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Siguiente", color = CosmosDark, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = CosmosDark)
                    }
                }
            }
        }
    }
}
