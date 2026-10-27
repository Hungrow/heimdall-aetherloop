package com.heimdall.aetherloop.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.heimdall.aetherloop.core.AvatarCatalog
import com.heimdall.aetherloop.core.PlayerProgressManager
import com.heimdall.aetherloop.ui.theme.*
import kotlin.math.sin

/**
 * Authentic Norse Saga Cartography Map: The Sacred River Gjöll (100 Levels).
 *
 * Features serpentine river currents, stone riverbanks, 4 distinct saga realms
 * (Vigrid, Niflheim, Muspelheim, Ragnarök), milestone fortress gates, and player avatar locator.
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
    val totalStars = remember(highestUnlocked) {
        (1..100).sumOf { progressManager.getLevelStars(it) }
    }

    val listState = rememberLazyListState()

    // Auto-scroll to player's current position on load
    LaunchedEffect(currentLevel) {
        val targetIndex = (currentLevel - 1).coerceIn(0, totalLevels - 1)
        listState.scrollToItem((targetIndex - 2).coerceAtLeast(0))
    }

    val levelsList = remember { (1..totalLevels).toList() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF02070F),
                        Color(0xFF041220),
                        Color(0xFF071B2F),
                        Color(0xFF030D18)
                    )
                )
            )
    ) {
        // Map Top Header Bar: Vegvísir Compass & Star Progress
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(CosmosSurface.copy(alpha = 0.92f))
                .border(1.dp, CosmosBorder, RoundedCornerShape(14.dp))
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "🧭",
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "RÍO SAGRADO GJÖLL",
                        color = HeimdallGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Saga de los Nueve Reinos",
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }
            }

            // Total Stars & Level Progress Chips
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(CosmosCard)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = HeimdallGold,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "$totalStars/300",
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(BifrostCyan.copy(alpha = 0.15f))
                        .border(1.dp, BifrostCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "NV. $currentLevel",
                        color = BifrostCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        // Serpentine River Path List
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 10.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            itemsIndexed(levelsList) { index, lvl ->
                val isUnlocked = lvl <= highestUnlocked
                val isCurrent = lvl == currentLevel
                val isBoss = (lvl % 25 == 0)
                val stars = progressManager.getLevelStars(lvl)

                // Realm demarkation headers
                when (lvl) {
                    1 -> RealmDemarcationBanner(
                        title = "REINO I: VIGRID • EL FIORDO DE LOS HÉROES",
                        subtitle = "Aguas calmas y esquifes invasores • Niveles 1 - 24",
                        accentColor = BifrostCyan,
                        rune = "ᚠ"
                    )
                    26 -> RealmDemarcationBanner(
                        title = "REINO II: NIFLHEIM • EL ESTRECHO GLACIAL",
                        subtitle = "Brumas boreales y témpanos flotantes • Niveles 26 - 49",
                        accentColor = Color(0xFF80DEEA),
                        rune = "ᛁ"
                    )
                    51 -> RealmDemarcationBanner(
                        title = "REINO III: MUSPELHEIM • EL RÍO DE LAVA",
                        subtitle = "Aguas incandescentes y lluvia de azufre • Niveles 51 - 74",
                        accentColor = Color(0xFFFF5722),
                        rune = "ᛋ"
                    )
                    76 -> RealmDemarcationBanner(
                        title = "REINO IV: HELHEIM & RAGNARÖK • EL JUICIO FINAL",
                        subtitle = "Tormenta divina y el ocaso de los mundos • Niveles 76 - 99",
                        accentColor = ValkyrieViolet,
                        rune = "ᛏ"
                    )
                }

                // River zig-zag curve offset
                val riverOffsetNormalized = sin(index * 0.42f)
                val bias = 0.5f + (riverOffsetNormalized * 0.36f)

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
                        BossMilestoneNode(
                            level = lvl,
                            isUnlocked = isUnlocked,
                            isCurrent = isCurrent,
                            stars = stars,
                            playerAvatarResId = playerAvatar.drawableResId,
                            onClick = { if (isUnlocked) onSelectLevel(lvl) }
                        )
                    } else {
                        StandardRiverNode(
                            level = lvl,
                            isUnlocked = isUnlocked,
                            isCurrent = isCurrent,
                            stars = stars,
                            playerAvatarResId = playerAvatar.drawableResId,
                            onClick = { if (isUnlocked) onSelectLevel(lvl) }
                        )
                    }

                    // Water current connector wave between nodes
                    RiverConnectorStream(bias = bias)
                }
            }
        }
    }
}

/**
 * Realm demarcation banner marking the transition into a new Norse realm.
 */
@Composable
private fun RealmDemarcationBanner(
    title: String,
    subtitle: String,
    accentColor: Color,
    rune: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        accentColor.copy(alpha = 0.25f),
                        CosmosSurface.copy(alpha = 0.95f),
                        accentColor.copy(alpha = 0.25f)
                    )
                )
            )
            .border(1.5.dp, accentColor.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = accentColor,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = subtitle,
                    color = TextSecondary,
                    fontSize = 10.sp
                )
            }
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.2f))
                    .border(1.dp, accentColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = rune,
                    color = accentColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )
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
        targetValue = 1.10f,
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
        // Player Avatar portrait on current node with golden Viking shield frame
        if (isCurrent) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(CosmosCard)
                    .border(2.8.dp, HeimdallGold, CircleShape),
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

        // Circular Carved Rune Stone
        Box(
            modifier = Modifier
                .size(if (isCurrent) 50.dp else 44.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isCurrent -> HeimdallGold
                        isUnlocked -> BifrostCyan
                        else -> Color(0xFF1E2830)
                    }
                )
                .border(
                    width = 2.dp,
                    color = if (isUnlocked) Color.White.copy(alpha = 0.85f) else Color(0xFF37474F),
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

/**
 * Milestone Boss Fortress Gateway (Levels 25, 50, 75, 100).
 */
@Composable
private fun BossMilestoneNode(
    level: Int,
    isUnlocked: Boolean,
    isCurrent: Boolean,
    stars: Int,
    playerAvatarResId: Int,
    onClick: () -> Unit
) {
    val bossInfo = when (level) {
        25 -> Pair("🐉 JEFE: JÖRMUNGANDR", "La Serpiente del Océano")
        50 -> Pair("🔥 JEFE: SURTR", "El Coloso de Muspelheim")
        75 -> Pair("❄️ JEFE: NÍÐHÖGGR", "El Dragón Glacial")
        100 -> Pair("👑 JEFE FINAL: NAGLFAR", "La Nave del Ragnarök")
        else -> Pair("⚔️ JEFE DE GUERRA", "Armada Suprema")
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF160824).copy(alpha = 0.94f))
            .border(2.dp, ValkyrieViolet, RoundedCornerShape(18.dp))
            .clickable(enabled = isUnlocked, onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 12.dp)
    ) {
        // Boss Title & Subtitle
        Text(
            text = bossInfo.first,
            color = if (isUnlocked) ValkyrieViolet else Color.Gray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        )
        Text(
            text = bossInfo.second,
            color = TextSecondary,
            fontSize = 10.sp
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

        // Fortress Rune Gate
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(if (isUnlocked) Color(0xFFD500F9) else Color(0xFF263238))
                .border(2.5.dp, HeimdallGold, CircleShape),
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

/**
 * Winding river water stream connecting consecutive map nodes.
 */
@Composable
private fun RiverConnectorStream(bias: Float) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(18.dp)
    ) {
        val w = size.width
        val h = size.height
        val startX = w * bias

        // Flowing water stream line
        val path = Path()
        path.moveTo(startX, 0f)
        path.quadraticTo(w * 0.5f, h * 0.5f, startX, h)

        drawPath(
            path = path,
            color = Color(0x5500E5FF),
            style = Stroke(width = 3.5f)
        )
        drawPath(
            path = path,
            color = Color(0x33FFFFFF),
            style = Stroke(width = 1.5f)
        )
    }
}
