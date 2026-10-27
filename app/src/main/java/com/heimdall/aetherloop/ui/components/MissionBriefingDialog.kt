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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.heimdall.aetherloop.R
import com.heimdall.aetherloop.core.PlayerProgressManager
import com.heimdall.aetherloop.game.model.FleetWave
import com.heimdall.aetherloop.ui.theme.*

/**
 * Dynamic Saga Counsel of Odin, the All-Father.
 *
 * Provides contextual tactical guidance, authentic Hávamál proverbs,
 * and personalized lore tailored to the player's name and campaign level.
 */
@Composable
fun MissionBriefingDialog(
    wave: FleetWave,
    progressManager: PlayerProgressManager,
    onStart: () -> Unit
) {
    val isBoss = wave.isBossLevel && wave.bossType != null
    val playerName = progressManager.playerName
    val lvl = wave.waveNumber

    // Dynamic Odin lore generator
    val loreData = remember(lvl, playerName) {
        val salutations = listOf(
            "«¡Oye el llamado de Asgard, $playerName!",
            "«¡Escucha con atención, valiente $playerName!",
            "«¡Alza la mirada con orgullo, $playerName!",
            "«¡Los cuervos Hugin y Munin cantan tu nombre, $playerName!",
            "«¡Mantén firme la mano en el timón, $playerName!"
        )
        val salutation = salutations[lvl % salutations.size]

        val havamalProverbs = listOf(
            "«El ganado muere, los parientes mueren, uno mismo ha de morir; pero sé de una cosa que jamás perece: la fama de cada muerto que obró con honor.» (Hávamál 76)",
            "«El necio piensa que vivirá eternamente si esquiva las lanzas; pero la vejez no le dará tregua aunque escape de las armas enemigas.» (Hávamál 16)",
            "«El fuego es el más preciado tesoro de los hombres, y la vista del sol; gozar de buena salud y vivir sin vergüenza.» (Hávamál 68)",
            "«No hay mejor fardo para llevar en el viaje que un juicio claro y despierto; en tierras extrañas vale más que las arcas de oro.» (Hávamál 10)",
            "«Aquel que deba partir al alba no duerma hasta tarde; el lobo que acecha atrapa la presa, el dormilón pierde la batalla.» (Hávamál 58)",
            "«Sé leal y generoso con el amigo; responde al regalo con regalo, a la risa con franqueza y a la traición con sagacidad.» (Hávamál 42)",
            "«Ningún hombre es tan sabio que no pueda aprender de los errores del imprudente en la mar brava.» (Hávamál 24)"
        )
        val proverb = havamalProverbs[lvl % havamalProverbs.size]

        val mainNarrative = when {
            lvl == 25 -> "La Serpiente del Océano, Jörmungandr, emerge de las profundidades abisales. Su buque insignia destila veneno negro sobre las aguas de Vigrid. ¡Desvía la sagrada esfera rúnica con fuerza hacia su quilla y no permitas que sus fauces alcancen tu nave!"
            lvl == 50 -> "¡Las aguas hierven en llamaradas! Surtr, el titán de fuego de Muspelheim, lidera su flota de guerra. Sus cañones de azufre buscarán calcinar tus defensas. ¡Consigue la runa de Escarcha para helar su avance y golpea con determinación!"
            lvl == 75 -> "Níðhöggr, la bestia de las raíces de Yggdrasil, comanda el Acorazado Glacial. Su armadura de hielo ancestral desviará golpes dispersos. ¡Desata la Lanza divina de Gungnir para partir sus defensas de un solo rayo certero!"
            lvl == 100 -> "¡HA LLEGADO LA HORA SUPREMA DEL RAGNARÖK! Naglfar, la siniestra nave forjada con las uñas de los caídos, navega al frente de la horda del fin del mundo. Si tu barco zozobra hoy, los Nueve Reinos colapsarán en las sombras. ¡Por Odín, por Valhalla, por la eternidad!"
            lvl == 1 -> "Las aguas sagradas del fiordo están siendo mancilladas por invasores renegados. Haz rebotar la esfera bendecida con tu drakkar guardián y limpia nuestras costas antes de que desembarquen sus guerreros."
            lvl in 2..24 -> "Navegamos las corrientes de Vigrid. Recuerda que las grandes olas oceánicas de Aegir bloquearán los disparos en su cresta más alta. Desliza el drakkar con velocidad lateral para imprimir efecto y curvar la esfera hacia sus filas."
            lvl in 26..49 -> "El frío de Niflheim azota la costa. Cuidado con los maleficios de Loki y los témpanos flotantes; recoge las runas bendecidas de Mjolnir y las balistas para mantener a raya a sus esquifes rápidos."
            lvl in 51..74 -> "El río de Muspelheim nos rodea de ceniza volcánica. Los enemigos son más rápidos y agresivos. Mantén el ritmo de combate: cada golpe consecutivo activará la FURIA VIKINGA multiplicando tu puntuación."
            else -> "Estamos en las aguas prohibidas del mar de Helheim. La niebla es densa y los proyectiles enemigos llueven sin cesar. ¡Que la bendición de las Valquirias guíe cada uno de tus impactos!"
        }

        Triple(salutation, mainNarrative, proverb)
    }

    Dialog(onDismissRequest = onStart) {
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = CosmosSurface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .border(1.5.dp, HeimdallGold, RoundedCornerShape(26.dp))
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Odin Portrait with Golden Runic Ring
                Box(
                    modifier = Modifier
                        .size(76.dp)
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
                        fontSize = 14.sp,
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
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "${loreData.first} ${loreData.second}",
                            color = TextPrimary,
                            fontSize = 12.5.sp,
                            lineHeight = 18.sp
                        )

                        Text(
                            text = loreData.third,
                            color = HeimdallGold.copy(alpha = 0.9f),
                            fontSize = 11.5.sp,
                            lineHeight = 16.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
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
                            text = if (isBoss) "💀 GRAN JEFE" else "⚔️ FLOTA ENEMIGA",
                            color = if (isBoss) Color(0xFFFF1744) else HeimdallGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    HorizontalDivider(color = CosmosBorder)

                    Text(
                        text = "🌊 Desliza el barco rápido para dar efecto y curvar la esfera sagrada.",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "⚡ Encadena impactos consecutivos para desatar la Furia Vikinga (Multiplicador x2 a x8).",
                        color = HeimdallGold,
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
                        .height(48.dp)
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
