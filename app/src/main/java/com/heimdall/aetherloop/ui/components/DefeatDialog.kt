package com.heimdall.aetherloop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dangerous
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
import com.heimdall.aetherloop.game.model.RealmLevel
import com.heimdall.aetherloop.ui.theme.*

/**
 * Defeat Dialog when Shield reaches 0%.
 */
@Composable
fun DefeatDialog(
    level: RealmLevel,
    onRetry: () -> Unit
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
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
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
                    text = "¡ESCUDO COLAPSADO!",
                    color = MuspelCrimson,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )

                Text(
                    text = "Demasiado éter cayó en el vacío cósmico. El puente Bifröst en ${level.realmName} ha colapsado.",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )

                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(containerColor = MuspelCrimson),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = TextPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "REINTENTAR REINO",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
