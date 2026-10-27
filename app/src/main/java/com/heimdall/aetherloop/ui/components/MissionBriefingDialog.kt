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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.heimdall.aetherloop.game.model.FleetWave
import com.heimdall.aetherloop.ui.theme.*

/**
 * Mission Briefing Dialog for Viking Arkanoid Combat.
 */
@Composable
fun MissionBriefingDialog(
    wave: FleetWave,
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
                    text = "COMBATE NAVAL VIKINGO",
                    color = HeimdallGold,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )

                Text(
                    text = "OLEADA ${wave.waveNumber}: ${wave.title.uppercase()}",
                    color = BifrostCyan,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

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
                        text = "La flota enemiga desciende por las aguas del fiordo protegida por escudos de roble."
                    )
                    InstructionRow(
                        num = "2",
                        text = "Arrastra tu barco guardián inferior para desviar las esferas de energía y hacer pedazos los barcos."
                    )
                    InstructionRow(
                        num = "3",
                        text = "Recoge las runas mágicas que caen (Rayo de Mjölnir, Multibola y Balistas)."
                    )
                    InstructionRow(
                        num = "4",
                        text = "¡Abordaje Vikingo! Guerreros Einherjar saltan al asalto de las cubiertas enemigas."
                    )
                    InstructionRow(
                        num = "5",
                        text = "¡Evita que las esferas caigan al abismo! Cuentas con 3 vidas de Valquiria."
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
                        text = "¡A LA BATALLA NAVAL!",
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
