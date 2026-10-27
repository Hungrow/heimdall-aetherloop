package com.heimdall.aetherloop.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
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
import com.heimdall.aetherloop.ui.theme.*
import kotlin.math.sin

/**
 * Saga-style Winding Norse River Level Map (El Río Sagrado Gjöll).
 *
 * Displays levels 1 to 100 along a serpentine river path, milestone bosses
 * every 25 levels, earned stars, and the player's artistic avatar frame on the current node.
 */
@Composable
fun RiverLevelMap(
    progressManager: PlayerProgressManager,
    onSelectLevel: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalLevels = 100
    val highestUnlocked = progressManager.highestCampaignLevel
    val currentLevel = progressManager.currentCampaignLevel
    val playerAvatar = AvatarCatalog.find(progressManager.avatarId)

    val listState = rememberLazyListState()

    // Auto-scroll to current level on first render
    LaunchedEffect(currentLevel) {
        val targetIndex = (currentLevel - 1).coerceIn(0, totalLevels - 1)
        listState.scrollToItem((targetIndex - 2).coerceAtLeast(0))
    }

    val levelsList = remember { (1..totalLevels).toList() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF030A14),
                        Color(0xFF071B2D),
                        Color(0xFF0C2B47),
                        Color(0xFF061828)
                    )
                )
            )
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(levelsList) { index, lvl ->
                val isUnlocked = lvl <= highestUnlocked
                val isCurrent = lvl == currentLevel
                val isBoss = (lvl % 25 == 0)
                val stars = progressManager.getLevelStars(lvl)

                // Serpentine sine wave offset (horizontal zig-zag river path)
                val riverOffsetNormalized = sin(index * 0.45f) // -1.0 to 1.0
                val bias = 0.5f + (riverOffsetNormalized * 0.35f) // 0.15 to 0.85

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = when {
                        bias < 0.35f -> Alignment.Start
                        bias > 0.65f -> Alignment.End
                        else -> Alignment.CenterHorizontally
                    }
                ) {
                    if (isBoss) {
                        // Milestone Boss Node
                        BossMilestoneNode(
                            level = lvl,
                            isUnlocked = isUnlocked,
                            isCurrent = isCurrent,
                            stars = stars,
                            playerAvatarResId = playerAvatar.drawableResId,
                            onClick = { if (isUnlocked) onSelectLevel(lvl) }
                        )
                    } else {
                        // Standard River Node
                        StandardRiverNode(
                            level = lvl,
                            isUnlocked = isUnlocked,
                            isCurrent = isCurrent,
                            stars = stars,
                            playerAvatarResId = playerAvatar.drawableResId,
                            onClick = { if (isUnlocked) onSelectLevel(lvl) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StandardRiverNode(
    level: Int,
    isUnlocked: Boolean,
    isCurrent: Boolean,
    stars: Int,
    playerAvatarResId: Int,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(enabled = isUnlocked, onClick = onClick)
    ) {
        // Player Avatar portrait on current node!
        if (isCurrent) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(CosmosCard)
                    .border(2.5.dp, HeimdallGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = playerAvatarResId),
                    contentDescription = "Tu Posición",
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        // Circular Node Stone
        Box(
            modifier = Modifier
                .size(if (isCurrent) 48.dp else 42.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isCurrent -> HeimdallGold
                        isUnlocked -> BifrostCyan
                        else -> Color(0xFF263238)
                    }
                )
                .border(
                    width = 2.dp,
                    color = if (isUnlocked) Color.White.copy(alpha = 0.8f) else Color(0xFF455A64),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isUnlocked) {
                Text(
                    text = level.toString(),
                    color = CosmosDark,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color(0xFF78909C),
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Star rating underneath (★ ★ ★)
        if (isUnlocked && stars > 0) {
            Row(
                modifier = Modifier.padding(top = 3.dp),
                horizontalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                for (s in 1..3) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = if (s <= stars) HeimdallGold else Color(0xFF37474F),
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun BossMilestoneNode(
    level: Int,
    isUnlocked: Boolean,
    isCurrent: Boolean,
    stars: Int,
    playerAvatarResId: Int,
    onClick: () -> Unit
) {
    val bossName = when (level) {
        25 -> "🐉 JEFE: JÖRMUNGANDR"
        50 -> "🔥 JEFE: SURTR"
        75 -> "❄️ JEFE: NÍÐHÖGGR"
        100 -> "💀 JEFE FINAL: NAGLFAR"
        else -> "⚔️ GRAN JEFE"
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1A0A26).copy(alpha = 0.9f))
            .border(2.dp, ValkyrieViolet, RoundedCornerShape(16.dp))
            .clickable(enabled = isUnlocked, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        // Boss Title Banner
        Text(
            text = bossName,
            color = if (isUnlocked) ValkyrieViolet else Color.Gray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Avatar frame if current
        if (isCurrent) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .border(2.5.dp, HeimdallGold, CircleShape)
            ) {
                Image(
                    painter = painterResource(id = playerAvatarResId),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        // Boss Node Badge
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(if (isUnlocked) Color(0xFFD500F9) else Color(0xFF37474F))
                .border(2.dp, HeimdallGold, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = level.toString(),
                color = if (isUnlocked) Color.White else Color.LightGray,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black
            )
        }

        if (isUnlocked && stars > 0) {
            Row(modifier = Modifier.padding(top = 4.dp)) {
                for (s in 1..3) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = if (s <= stars) HeimdallGold else Color(0xFF37474F),
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }
    }
}
