package com.heimdall.aetherloop.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.heimdall.aetherloop.core.AvatarCatalog
import com.heimdall.aetherloop.core.PlayerProgressManager
import com.heimdall.aetherloop.core.SoundSynthesizer
import com.heimdall.aetherloop.game.model.GameMode
import com.heimdall.aetherloop.ui.theme.*

/**
 * Cinematic Main Menu Screen of AetherLoop: Valhalla Combat.
 *
 * Displays Norse runic header, warrior profile card, saga progression stars,
 * sound controls, game mode switch, and the Sacred River Gjöll Map.
 */
@Composable
fun MainMenuScreen(
    progressManager: PlayerProgressManager,
    soundSynthesizer: SoundSynthesizer,
    onStartCampaignLevel: (Int) -> Unit,
    onStartEndlessMode: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(GameMode.STORY_CAMPAIGN) }
    var showAvatarDialog by remember { mutableStateOf(false) }

    val currentLevel = progressManager.currentCampaignLevel
    val playerAvatar = AvatarCatalog.find(progressManager.avatarId)
    val totalStars = remember(progressManager.highestCampaignLevel) {
        (1..100).sumOf { progressManager.getLevelStars(it) }
    }

    // Dynamic Norse Warrior Title based on highest level
    val warriorTitle = when {
        currentLevel >= 100 -> "👑 HÉROE DEL RAGNARÖK"
        currentLevel >= 75 -> "⚔️ SEÑOR DE LA GUERRA"
        currentLevel >= 50 -> "🔥 BERSERKER DE FUEGO"
        currentLevel >= 25 -> "❄️ GUARDIÁN DEL HIELO"
        else -> "🌲 JARL DE LOS FIORDOS"
    }

    var isSoundOn by remember { mutableStateOf(progressManager.isSoundEnabled()) }
    var isMusicOn by remember { mutableStateOf(progressManager.isMusicEnabled()) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CosmosDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // 1. CINEMATIC NORSE RUNIC HEADER
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ᚫ ᛖ ᚦ ᛖ ᚱ ᛚ ᛟ ᛟ ᛈ",
                    color = HeimdallGold.copy(alpha = 0.85f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "• AETHERLOOP •",
                    color = BifrostCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp
                )
            }

            // 2. TOP PLAYER PROFILE & AUDIO TOOLBAR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(CosmosSurface.copy(alpha = 0.96f))
                    .border(1.2.dp, HeimdallGold.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Profile (Avatar + Name + Dynamic Title)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { showAvatarDialog = true }
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .border(2.dp, HeimdallGold, CircleShape)
                    ) {
                        Image(
                            painter = painterResource(id = playerAvatar.drawableResId),
                            contentDescription = "Avatar",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = progressManager.playerName,
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar",
                                tint = BifrostCyan,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                        Text(
                            text = warriorTitle,
                            color = HeimdallGold,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Sound Toggles & Settings Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Music Toggle
                    IconButton(
                        onClick = {
                            val newState = !isMusicOn
                            isMusicOn = newState
                            progressManager.setMusicEnabled(newState)
                            soundSynthesizer.isMusicMuted = !newState
                            if (newState) soundSynthesizer.startMenuMusic() else soundSynthesizer.stopMusic()
                        },
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = if (isMusicOn) Icons.Default.MusicNote else Icons.Default.MusicOff,
                            contentDescription = "Música",
                            tint = if (isMusicOn) BifrostCyan else Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // SFX Toggle
                    IconButton(
                        onClick = {
                            val newState = !isSoundOn
                            isSoundOn = newState
                            progressManager.setSoundEnabled(newState)
                            soundSynthesizer.isMuted = !newState
                        },
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = if (isSoundOn) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                            contentDescription = "Sonido",
                            tint = if (isSoundOn) HeimdallGold else Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Settings Button
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(CosmosCard)
                            .border(1.dp, HeimdallGold, CircleShape)
                            .clickable(onClick = onOpenSettings),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Ajustes",
                            tint = HeimdallGold,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }
            }

            // 3. GAME MODE SELECTOR TABS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(CosmosCard)
                    .border(1.dp, CosmosBorder, RoundedCornerShape(14.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Campaign Tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(11.dp))
                        .background(if (selectedTab == GameMode.STORY_CAMPAIGN) BifrostCyan else Color.Transparent)
                        .clickable { selectedTab = GameMode.STORY_CAMPAIGN }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⚔️ RÍO GJÖLL (CAMPAÑA)",
                        color = if (selectedTab == GameMode.STORY_CAMPAIGN) CosmosDark else TextPrimary,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                // Endless Tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(11.dp))
                        .background(if (selectedTab == GameMode.ENDLESS_SURVIVAL) ValkyrieViolet else Color.Transparent)
                        .clickable { selectedTab = GameMode.ENDLESS_SURVIVAL }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "∞ OLEADAS INFINITAS",
                        color = if (selectedTab == GameMode.ENDLESS_SURVIVAL) CosmosDark else TextPrimary,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            // 4. MAIN CONTENT: RIVER LEVEL MAP OR ENDLESS SURVIVAL
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (selectedTab == GameMode.STORY_CAMPAIGN) {
                    // Winding Norse River Level Map (100 levels)
                    RiverLevelMap(
                        progressManager = progressManager,
                        onSelectLevel = { lvl -> onStartCampaignLevel(lvl) }
                    )
                } else {
                    // Endless Mode Preview Card
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Card(
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(containerColor = CosmosSurface),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.5.dp, ValkyrieViolet, RoundedCornerShape(22.dp))
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Text(
                                    text = "SUPERVIVENCIA DEL VALHALLA",
                                    color = ValkyrieViolet,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "Enfrenta oleadas infinitas de armadas vikingas. Los barcos avanzan continuamente hacia tu posición y disparan andanadas de cañones. ¿Cuántas oleadas podrás resistir?",
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Récord Oleada", color = TextMuted, fontSize = 11.sp)
                                        Text("${progressManager.endlessHighestWave}", color = HeimdallGold, fontSize = 18.sp, fontWeight = FontWeight.Black)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Puntuación Máx.", color = TextMuted, fontSize = 11.sp)
                                        Text("${progressManager.endlessHighScore}", color = BifrostCyan, fontSize = 18.sp, fontWeight = FontWeight.Black)
                                    }
                                }

                                Button(
                                    onClick = onStartEndlessMode,
                                    colors = ButtonDefaults.buttonColors(containerColor = ValkyrieViolet),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                ) {
                                    Text(
                                        text = "¡INICIAR SUPERVIVENCIA!",
                                        color = CosmosDark,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 5. BOTTOM LAUNCH BAR
            if (selectedTab == GameMode.STORY_CAMPAIGN) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Button(
                        onClick = { onStartCampaignLevel(currentLevel) },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
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
                                    text = "¡JUGAR NIVEL $currentLevel!",
                                    color = CosmosDark,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Avatar customizer modal
        if (showAvatarDialog) {
            AvatarSelectorDialog(
                progressManager = progressManager,
                onDismiss = { showAvatarDialog = false }
            )
        }
    }
}
