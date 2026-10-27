package com.heimdall.aetherloop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.heimdall.aetherloop.game.model.FleetWave
import com.heimdall.aetherloop.ui.theme.*

/**
 * Defeat Dialog when all lives are lost.
 */
@Composable
fun DefeatDialog(
    wave: FleetWave,
    onRetry: () -> Unit,
    onReturnToMenu: () -> Unit
) {
    Dialog(onDismissRequest = {}) {
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = CosmosSurface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .border(1.5.dp, MuspelCrimson, RoundedCornerShape(26.dp))
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
                        .background(MuspelCrimson.copy(alpha = 0.2f))
                        .border(1.dp, MuspelCrimson, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Dangerous,
                        contentDescription = null,
                        tint = MuspelCrimson,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Text(
                    text = "¡FLOTA DESTRUIDA!",
                    color = MuspelCrimson,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )

                Text(
                    text = "El casco de tu nave ha sucumbido y la flota enemiga ha cruzado el fiordo en ${wave.title}.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )

                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(containerColor = MuspelCrimson),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = TextPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "REINTENTAR BATALLA",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
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
