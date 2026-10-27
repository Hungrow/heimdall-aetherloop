package com.heimdall.aetherloop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shield
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
 * Mission Briefing Dialog.
 *
 * Explains the core gameplay rules, objectives, and stakes at the start of each level.
 */
@Composable
fun MissionBriefingDialog(
    level: RealmLevel,
    onStart: () -> Unit
) {
    Dialog(onDismissRequest = onStart) {
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = CosmosSurface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .border(1.5.dp, BifrostCyan, RoundedCornerShape(26.dp))
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Icon
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(BifrostCyan, ValkyrieViolet)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = CosmosDark,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Text(
                    text = "DEFENSA DE ASGARD",
                    color = HeimdallGold,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )

                Text(
                    text = "${level.realmName.uppercase()} • ${level.subtitle}",
                    color = BifrostCyan,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                // Instructions Box
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(CosmosCard)
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    InstructionRow(
                        num = "1",
                        text = "El éter estelar cae en cascada continua hacia el vacío."
                    )
                    InstructionRow(
                        num = "2",
                        text = "Toca la pantalla para despachar vasijas del color coincidente con cada corriente."
                    )
                    InstructionRow(
                        num = "3",
                        text = "Cada grano que cae al vacío daña el Escudo de Asgard. ¡Si llega al 0%, serás derrotado!"
                    )
                    InstructionRow(
                        num = "4",
                        text = "Llena las vasijas al 100% para disolver el mosaico rúnico y restaurar el escudo."
                    )
                }

                Button(
                    onClick = onStart,
                    colors = ButtonDefaults.buttonColors(containerColor = HeimdallGold),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = CosmosDark)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "¡INICIAR RESONANCIA!",
                        color = CosmosDark,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun InstructionRow(num: String, text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(BifrostCyan),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = num,
                color = CosmosDark,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            color = TextPrimary,
            fontSize = 12.sp,
            lineHeight = 16.sp
        )
    }
}
