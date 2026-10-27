package com.heimdall.aetherloop.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.heimdall.aetherloop.R
import com.heimdall.aetherloop.game.model.FleetWave
import com.heimdall.aetherloop.ui.theme.*

/**
 * Odin's Mythical Lore & Tactical Counsel Dialog.
 *
 * Odin, the All-Father, introduces the saga lore and tactical wisdom
 * regarding ocean swell barriers, god runes, and Loki's curses before battle begins.
 */
@Composable
fun MissionBriefingDialog(
    wave: FleetWave,
    onStart: () -> Unit
) {
    val isBoss = wave.isBossLevel && wave.bossType != null

    val loreText = when {
        isBoss -> "«¡Guardián de Asgard! El temible buque insignia ${wave.bossType?.title ?: "Naglfar"} avanza al frente de la armada enemiga. Sus cañones vomitan fuego negro y sus maderas claman venganza. ¡Apunta con la sagrada esfera rúnica al corazón de su quilla!»"
        wave.waveNumber == 1 -> "«¡Oye mi llamado, guerrero! Las aguas sagradas del fiordo están siendo invadidas por clanes renegados de Helheim. Desvía la esfera rúnica con tu barco guardián y haz pedazos su flota invasora antes de que toquen nuestras costas.»"
        wave.waveNumber in 2..24 -> "«El mar de Vigrid se agita. Recuerda que las grandes olas oceánicas de Aegir bloquearán tanto los proyectiles enemigos como nuestras balistas durante su cresta. ¡Aprovecha la corriente y recoge las runas de poder!»"
        wave.waveNumber in 26..49 -> "«Nos adentramos en las aguas incandescentes de Muspelheim. Fuego y azufre caen del cielo. Desconfía de los barriles volátiles y los maleficios de Loki; ¡busca la Escarcha de Niflheim para congelar a los agresores!»"
        wave.waveNumber in 51..74 -> "«Las brumas de Niflheim hielan los remos. Los témpanos flotantes pueden cambiar el rumbo de tus disparos, pero la sagrada Lanza de Gungnir perforará columnas enteras de navíos enemigos.»"
        else -> "«¡La batalla final por el destino de Yggdrasil ha comenzado! La flota del Ragnarök se extiende hasta donde alcanza la vista. ¡Que el trueno de Thor y el canto de las Valquirias guíen tu timón!»"
    }

    Dialog(onDismissRequest = onStart) {
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
                // Odin Portrait with Golden Runic Frame
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .border(3.dp, HeimdallGold, CircleShape)
                        .background(CosmosCard),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.avatar_odin),
                        contentDescription = "Odín, Padre de Todo",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "CONSEJO DEL PADRE DE TODO",
                        color = HeimdallGold,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = "ODÍN • SEÑOR DE VALHALLA",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                // Lore Dialogue Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(CosmosCard)
                        .border(1.dp, CosmosBorder, RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Text(
                        text = loreText,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }

                // Tactical Wave Summary
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(CosmosCard)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "OLEADA ${wave.waveNumber}: ${wave.title.uppercase()}",
                            color = BifrostCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isBoss) "💀 JEFE" else "⚔️ FLOTA",
                            color = if (isBoss) Color(0xFFFF1744) else HeimdallGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    HorizontalDivider(color = CosmosBorder)

                    Text(
                        text = "🌊 Las olas oceánicas bloquean proyectiles al crestar.",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "⚡ Destruye drakkars para obtener runas; esquiva maldiciones de Loki.",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                // Action Launch Button
                Button(
                    onClick = onStart,
                    colors = ButtonDefaults.buttonColors(containerColor = HeimdallGold),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = CosmosDark)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "¡POR ASGARD! ZARPAR",
                        color = CosmosDark,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}
