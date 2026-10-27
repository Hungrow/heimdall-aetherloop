package com.heimdall.aetherloop.game.engine

import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.heimdall.aetherloop.core.HapticManager
import com.heimdall.aetherloop.core.PlayerProgressManager
import com.heimdall.aetherloop.core.SoundSynthesizer
import com.heimdall.aetherloop.game.model.*
import com.heimdall.aetherloop.game.physics.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicLong
import kotlin.random.Random

/**
 * Master Game Loop Engine for Viking Arkanoid Naval Combat.
 */
class VikingArkanoidEngine(
    val soundSynthesizer: SoundSynthesizer,
    val hapticManager: HapticManager,
    val progressManager: PlayerProgressManager
) {
    val collisionEngine = CollisionEngine(soundSynthesizer, hapticManager)

    private val shipIdGen = AtomicLong(1L)
    private val warriorIdGen = AtomicLong(1L)

    val gameMode = mutableStateOf(GameMode.STORY_CAMPAIGN)

    val paddle = Paddle()
    val activeBalls = mutableStateListOf<Ball>()
    val enemyShips = mutableStateListOf<EnemyShip>()
    val powerUps = mutableStateListOf<PowerUpDrop>()
    val projectiles = mutableStateListOf<Projectile>()
    val cannonBalls = mutableStateListOf<CannonBall>()
    val vikingWarriors = mutableStateListOf<VikingWarrior>()
    val oceanSwells = mutableStateListOf<OceanSwell>()

    val ambientParticles = mutableListOf<AmbientParticle>()
    val floatingScenery = mutableListOf<FloatingScenery>()

    val battleMap = mutableStateOf(MapBiome.VIGRID_FJORD)

    val lives = mutableIntStateOf(3)
    val score = mutableIntStateOf(0)
    val isBallAttached = mutableStateOf(true)
    val isPaused = mutableStateOf(false)

    var frostNovaTimer = 0f

    val currentWave = mutableStateOf(FleetWave.forLevel(1))
    val levelNumber = mutableIntStateOf(1)

    val earnedStars = mutableIntStateOf(0)
    val isNewHighScore = mutableStateOf(false)

    val jarlCheerAnim = mutableFloatStateOf(0f)

    private val _gameState = MutableStateFlow(GameState.MISSION_BRIEFING)
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private var screenWidth = 1080f
    private var screenHeight = 1920f

    init {
        loadCampaignLevel(progressManager.currentCampaignLevel)
    }

    fun startWaveAction() {
        _gameState.value = GameState.PLAYING
        soundSynthesizer.startBattleMusic()
        launchBall()
    }

    fun loadCampaignLevel(lvl: Int) {
        gameMode.value = GameMode.STORY_CAMPAIGN
        val safeLvl = lvl.coerceIn(1, 100)
        levelNumber.intValue = safeLvl
        progressManager.currentCampaignLevel = safeLvl
        currentWave.value = FleetWave.forLevel(safeLvl)
        battleMap.value = MapBiome.forWave(safeLvl / 20)

        resetFieldAndBuildFormation(safeLvl)
    }

    fun loadEndlessWave(wave: Int) {
        gameMode.value = GameMode.ENDLESS_SURVIVAL
        levelNumber.intValue = wave
        currentWave.value = FleetWave.forEndless(wave)
        battleMap.value = MapBiome.forWave(wave % 5)

        resetFieldAndBuildFormation(wave)
    }

    private fun resetFieldAndBuildFormation(level: Int) {
        activeBalls.clear()
        enemyShips.clear()
        powerUps.clear()
        projectiles.clear()
        cannonBalls.clear()
        vikingWarriors.clear()
        ambientParticles.clear()
        floatingScenery.clear()
        collisionEngine.splinters.clear()
        collisionEngine.floatingTexts.clear()
        collisionEngine.waterRipples.clear()
        collisionEngine.screenShakeAmount = 0f

        lives.intValue = 3
        isBallAttached.value = true
        earnedStars.intValue = 0
        isNewHighScore.value = false
        jarlCheerAnim.floatValue = 0f

        paddle.tier = 2 // Start at standard Guardian Drakkar
        paddle.applyTierDimensions()
        paddle.isLokiCursed = false
        paddle.isKeelFrozen = false
        paddle.isMagnetActive = false
        frostNovaTimer = 0f

        oceanSwells.clear()
        oceanSwells.add(OceanSwell(id = 1L, y = screenHeight * 0.52f, height = 34f, vy = 18f, surgeTimer = 0f, period = 16.0f))

        initAmbientParticles(screenWidth, screenHeight)
        initFloatingScenery(screenWidth, screenHeight)
        buildFleetFormation(level, screenWidth, screenHeight)
        spawnAttachedBall()

        _gameState.value = GameState.MISSION_BRIEFING
    }

    fun restartCurrentWave() {
        if (gameMode.value == GameMode.STORY_CAMPAIGN) {
            loadCampaignLevel(levelNumber.intValue)
        } else {
            loadEndlessWave(levelNumber.intValue)
        }
    }

    fun nextWave() {
        if (gameMode.value == GameMode.STORY_CAMPAIGN) {
            if (levelNumber.intValue < 100) {
                loadCampaignLevel(levelNumber.intValue + 1)
            } else {
                loadCampaignLevel(1)
            }
        } else {
            loadEndlessWave(levelNumber.intValue + 1)
        }
    }

    private fun initAmbientParticles(w: Float, h: Float) {
        val biome = battleMap.value
        val count = 35
        for (i in 0 until count) {
            val speedY = when (biome.ambientParticleType) {
                AmbientParticleType.SNOWFLAKE -> Random.nextFloat() * 60f + 40f
                AmbientParticleType.FIRE_EMBER -> -(Random.nextFloat() * 80f + 30f)
                AmbientParticleType.SPIRIT_WISP -> -(Random.nextFloat() * 40f + 20f)
                AmbientParticleType.SEA_SPRAY -> Random.nextFloat() * 70f + 20f
                AmbientParticleType.STARDUST -> Random.nextFloat() * 30f + 10f
            }
            val speedX = (Random.nextFloat() - 0.5f) * 40f
            ambientParticles.add(
                AmbientParticle(
                    x = Random.nextFloat() * w,
                    y = Random.nextFloat() * h,
                    vx = speedX,
                    vy = speedY,
                    size = Random.nextFloat() * 3f + 1.5f,
                    alpha = Random.nextFloat() * 0.7f + 0.2f,
                    maxAlpha = 0.8f,
                    type = biome.ambientParticleType,
                    color = biome.ambientColor
                )
            )
        }
    }

    private fun initFloatingScenery(w: Float, h: Float) {
        val biome = battleMap.value
        val type = when (biome) {
            MapBiome.NIFLHEIM_ICE -> SceneryType.ICEBERG
            MapBiome.MUSPELHEIM_MAGMA -> SceneryType.VOLCANIC_ROCK
            MapBiome.HELHEIM_ABYSS -> SceneryType.GHOST_WRECK
            else -> SceneryType.RUNIC_BUOY
        }

        for (i in 0 until 3) {
            floatingScenery.add(
                FloatingScenery(
                    id = shipIdGen.incrementAndGet(),
                    x = Random.nextFloat() * (w - 100f) + 50f,
                    y = h * (0.25f + i * 0.22f),
                    width = 65f,
                    height = 45f,
                    vy = 12f + i * 4f,
                    type = type,
                    rotation = (Random.nextFloat() - 0.5f) * 20f
                )
            )
        }
    }

    private fun spawnAttachedBall() {
        activeBalls.clear()
        val ball = Ball(
            x = paddle.centerX,
            y = paddle.top - 12f,
            vx = 0f,
            vy = 0f,
            isAttachedToPaddle = true
        )
        activeBalls.add(ball)
        isBallAttached.value = true
    }

    fun launchBall() {
        if (isBallAttached.value && activeBalls.isNotEmpty()) {
            val ball = activeBalls.first()
            ball.isAttachedToPaddle = false
            val launchAngle = (Random.nextFloat() - 0.5f) * 0.5f
            val baseSpeed = 750f
            ball.vx = baseSpeed * launchAngle
            ball.vy = -baseSpeed
            isBallAttached.value = false
            jarlCheerAnim.floatValue = 1.0f
            soundSynthesizer.playLaunchWhoosh()
        }
    }

    fun dragPaddle(dragDeltaX: Float) {
        val effectiveDelta = if (paddle.isLokiCursed) -dragDeltaX else dragDeltaX
        val speedFactor = if (paddle.isKeelFrozen) 0.5f else 1.0f
        val newX = paddle.centerX + effectiveDelta * speedFactor
        val halfW = paddle.width / 2f
        paddle.centerX = newX.coerceIn(halfW, screenWidth - halfW)

        if (isBallAttached.value && activeBalls.isNotEmpty()) {
            val ball = activeBalls.first()
            ball.x = paddle.centerX
            ball.y = paddle.top - ball.radius - 1f
        }
    }

    fun fireBallistas() {
        if (paddle.hasBallistas && _gameState.value == GameState.PLAYING) {
            projectiles.add(Projectile(x = paddle.left + 14f, y = paddle.top))
            projectiles.add(Projectile(x = paddle.right - 14f, y = paddle.top))
            soundSynthesizer.playLaserShot()
            hapticManager.onParticleCaptured()
        }
    }

    fun launchVikingBoardingParty(targetShip: EnemyShip) {
        val warrior = VikingWarrior(
            id = warriorIdGen.incrementAndGet(),
            x = paddle.centerX + (Random.nextFloat() - 0.5f) * 40f,
            y = paddle.top - 10f,
            vx = (targetShip.x - paddle.centerX) * 1.5f,
            vy = -750f,
            targetShipId = targetShip.id,
            state = WarriorState.LEAPING
        )
        vikingWarriors.add(warrior)
        jarlCheerAnim.floatValue = 1.0f
        soundSynthesizer.playPaddleHit()
    }

    fun handleScreenTap(offset: Offset) {
        if (_gameState.value != GameState.PLAYING) return

        if (isBallAttached.value) {
            launchBall()
        } else if (paddle.hasBallistas) {
            fireBallistas()
        }
    }

    private fun buildFleetFormation(level: Int, w: Float, h: Float) {
        val safeW = if (w > 100f) w else 1080f
        val safeH = if (h > 100f) h else 1920f

        val startY = safeH * 0.16f
        val shipW = (safeW * 0.20f).coerceIn(68f, 96f)
        val shipH = (safeH * 0.052f).coerceIn(40f, 60f)

        val wave = currentWave.value
        val isBossWave = wave.isBossLevel && wave.bossType != null

        if (isBossWave) {
            // Milestone Boss Fleet (Level 25, 50, 75, 100)
            val bossType = wave.bossType ?: ShipType.BOSS_NAGLFAR
            val bossW = safeW * 0.72f
            val bossH = shipH * 2.3f

            enemyShips.add(
                EnemyShip(
                    id = shipIdGen.incrementAndGet(),
                    x = safeW * 0.5f,
                    y = startY + bossH * 0.5f,
                    width = bossW,
                    height = bossH,
                    type = bossType,
                    powerUpType = PowerUpType.MJOLNIR,
                    vx = 45f,
                    descentSpeed = 1.0f,
                    canShoot = true,
                    cannonCooldown = 1.8f
                )
            )

            // Flanking escorts
            val escortsY = startY + bossH + 28f
            val cols = 4
            val spacingX = safeW * 0.82f / cols
            val startX = safeW * 0.09f + spacingX * 0.5f
            for (c in 0 until cols) {
                val drop = if (c == 0) PowerUpType.BALLISTAS else if (c == 3) PowerUpType.MULTIBALL else null
                val escortType = when (bossType) {
                    ShipType.BOSS_JORMUNGANDR -> ShipType.SCOUT
                    ShipType.BOSS_SURTR -> ShipType.FIRE_DREADNOUGHT
                    ShipType.BOSS_NIDHOGGR -> ShipType.FROST_BRIG
                    else -> ShipType.ARMORED_DRAKKAR
                }
                enemyShips.add(
                    EnemyShip(
                        id = shipIdGen.incrementAndGet(),
                        x = startX + c * spacingX,
                        y = escortsY,
                        width = shipW,
                        height = shipH,
                        type = escortType,
                        powerUpType = drop,
                        vx = (if (c % 2 == 0) 30f else -30f),
                        descentSpeed = 2.0f,
                        canShoot = true,
                        cannonCooldown = Random.nextFloat() * 4f + 3f
                    )
                )
            }
        } else {
            // Standard / Scaled Fleet Formations (Levels 1 to 99)
            val rows = (3 + (level % 5).coerceAtMost(2)).coerceIn(3, 5)
            val cols = (4 + (if (level > 20) 1 else 0)).coerceIn(4, 5)
            val spacingX = safeW * 0.82f / cols
            val startX = safeW * 0.09f + spacingX * 0.5f

            val speedMultiplier = 1.0f + (level * 0.015f)
            val canShootRatio = (level * 0.02f).coerceIn(0.15f, 0.65f)

            for (r in 0 until rows) {
                for (c in 0 until cols) {
                    val shipX = startX + c * spacingX
                    val shipY = startY + r * (shipH + 16f)

                    val shipType = when {
                        level in 1..24 -> if (r == 0) ShipType.ARMORED_DRAKKAR else ShipType.SCOUT
                        level in 26..49 -> if (r <= 1) ShipType.FIRE_DREADNOUGHT else ShipType.ARMORED_DRAKKAR
                        level in 51..74 -> if (r == 0) ShipType.FROST_BRIG else if (r % 2 == 1) ShipType.ARMORED_DRAKKAR else ShipType.SCOUT
                        else -> when (r) {
                            0 -> ShipType.FIRE_DREADNOUGHT
                            1 -> ShipType.FROST_BRIG
                            else -> ShipType.ARMORED_DRAKKAR
                        }
                    }

                    val drop = when {
                        r == 0 && c == 1 -> PowerUpType.MULTIBALL
                        r == 1 && c == 2 -> PowerUpType.MJOLNIR
                        r == 2 && c == 0 -> PowerUpType.BALLISTAS
                        r == 2 && c == 3 -> PowerUpType.SHIELD_EXPAND
                        else -> null
                    }

                    val shouldShoot = Random.nextFloat() < canShootRatio
                    val patrolVx = (if (r % 2 == 0) 25f else -25f) * speedMultiplier

                    enemyShips.add(
                        EnemyShip(
                            id = shipIdGen.incrementAndGet(),
                            x = shipX,
                            y = shipY,
                            width = shipW,
                            height = shipH,
                            type = shipType,
                            powerUpType = drop,
                            vx = patrolVx,
                            descentSpeed = 1.8f * speedMultiplier,
                            canShoot = shouldShoot,
                            cannonCooldown = Random.nextFloat() * 5f + 3f
                        )
                    )
                }
            }
        }
    }

    private fun handleShipDestroyed(ship: EnemyShip) {
        score.intValue += ship.type.scoreValue
        jarlCheerAnim.floatValue = 1.0f

        val dropType = ship.powerUpType ?: if (Random.nextFloat() < 0.32f) {
            val beneficial = listOf(
                PowerUpType.MJOLNIR,
                PowerUpType.MULTIBALL,
                PowerUpType.SURTR_FIRE,
                PowerUpType.BALLISTAS,
                PowerUpType.SHIELD_EXPAND,
                PowerUpType.REPAIR_LIFE,
                PowerUpType.FROST_NOVA,
                PowerUpType.GUNGNIR_SPEAR,
                PowerUpType.VORTEX_MAGNET
            )
            val curses = listOf(
                PowerUpType.LOKI_CURSE,
                PowerUpType.FROZEN_KEEL,
                PowerUpType.POWDER_KEG
            )
            if (Random.nextFloat() < 0.22f) curses.random() else beneficial.random()
        } else null

        if (dropType != null) {
            powerUps.add(
                PowerUpDrop(
                    x = ship.x,
                    y = ship.y,
                    type = dropType
                )
            )
        }

        val activeCount = enemyShips.count { !it.isDestroyed }
        if (activeCount == 0) {
            onWaveVictory()
        }
    }

    private fun handlePowerUpCollected(type: PowerUpType) {
        jarlCheerAnim.floatValue = 1.0f

        // Ship evolution upgrade on beneficial power-ups
        if (type == PowerUpType.SHIELD_EXPAND || type == PowerUpType.BALLISTAS || type == PowerUpType.REPAIR_LIFE || type == PowerUpType.FROST_NOVA || type == PowerUpType.GUNGNIR_SPEAR) {
            val upgraded = paddle.upgradeTier()
            if (upgraded) {
                soundSynthesizer.playShipUpgrade()
                collisionEngine.floatingTexts.add(
                    FloatingText(
                        x = paddle.centerX,
                        y = paddle.top - 35f,
                        text = "⭐ ¡BARCO EVOLUCIONADO: TIER ${paddle.tier}! ⭐",
                        color = Color(0xFFFFD700)
                    )
                )
            }
        }

        when (type) {
            PowerUpType.MJOLNIR -> {
                for (b in activeBalls) {
                    b.type = BallType.MJOLNIR_LIGHTNING
                }
            }
            PowerUpType.SURTR_FIRE -> {
                for (b in activeBalls) {
                    b.type = BallType.SURTR_FIRE
                }
            }
            PowerUpType.MULTIBALL -> {
                if (activeBalls.isNotEmpty()) {
                    val base = activeBalls.first()
                    activeBalls.add(
                        Ball(
                            x = base.x,
                            y = base.y,
                            vx = base.vx * 0.7f - 300f,
                            vy = base.vy,
                            type = base.type,
                            isAttachedToPaddle = false
                        )
                    )
                    activeBalls.add(
                        Ball(
                            x = base.x,
                            y = base.y,
                            vx = base.vx * 0.7f + 300f,
                            vy = base.vy,
                            type = base.type,
                            isAttachedToPaddle = false
                        )
                    )
                }
            }
            PowerUpType.BALLISTAS -> {
                paddle.activateBallistas()
            }
            PowerUpType.SHIELD_EXPAND -> {
                paddle.activateExpansion()
            }
            PowerUpType.REPAIR_LIFE -> {
                lives.intValue = (lives.intValue + 1).coerceAtMost(5)
            }
            PowerUpType.FROST_NOVA -> {
                frostNovaTimer = 6.5f
                soundSynthesizer.playFrostFreeze()
                collisionEngine.floatingTexts.add(
                    FloatingText(x = paddle.centerX, y = paddle.top - 25f, text = "¡FLOTA CONGELADA! ❄️", color = Color(0xFF00E5FF))
                )
            }
            PowerUpType.GUNGNIR_SPEAR -> {
                soundSynthesizer.playGungnirBeam()
                hapticManager.onVesselFilled()
                // Launch celestial piercing spear that obliterates a vertical column of enemy ships
                val spearX = paddle.centerX
                for (ship in enemyShips) {
                    if (!ship.isDestroyed && !ship.isSinking && spearX in (ship.left - 20f)..(ship.right + 20f)) {
                        ship.hit(4)
                        collisionEngine.splinters.add(
                            SplinterParticle(x = ship.x, y = ship.y, vx = 0f, vy = -180f, color = Color(0xFFFFD700))
                        )
                        if (ship.isDestroyed) {
                            handleShipDestroyed(ship)
                        }
                    }
                }
                collisionEngine.floatingTexts.add(
                    FloatingText(x = spearX, y = paddle.top - 25f, text = "¡LANZA DE GUNGNIR! ⚡", color = Color(0xFFFFD700))
                )
            }
            PowerUpType.VORTEX_MAGNET -> {
                paddle.activateMagnet(8.0f)
                soundSynthesizer.playPowerUpCatch()
                collisionEngine.floatingTexts.add(
                    FloatingText(x = paddle.centerX, y = paddle.top - 25f, text = "¡VÓRTICE DE RUNAS! 🌀", color = Color(0xFF00E676))
                )
            }
            PowerUpType.LOKI_CURSE -> {
                paddle.applyLokiCurse(6.0f)
                soundSynthesizer.playCurseTrigger()
                collisionEngine.screenShakeAmount = 10f
                collisionEngine.floatingTexts.add(
                    FloatingText(x = paddle.centerX, y = paddle.top - 25f, text = "¡MALEFICIO DE LOKI! 😈", color = Color(0xFFE040FB))
                )
            }
            PowerUpType.FROZEN_KEEL -> {
                paddle.applyFrozenKeel(6.0f)
                soundSynthesizer.playCurseTrigger()
                collisionEngine.floatingTexts.add(
                    FloatingText(x = paddle.centerX, y = paddle.top - 25f, text = "¡QUILLA HELADA (-50% VEL)! 🧊", color = Color(0xFF80DEEA))
                )
            }
            PowerUpType.POWDER_KEG -> {
                soundSynthesizer.playShipExplosion()
                collisionEngine.screenShakeAmount = 22f
                hapticManager.onVesselFilled()
                val downgraded = paddle.downgradeTier()
                if (!downgraded) {
                    lives.intValue--
                    if (lives.intValue <= 0) {
                        _gameState.value = GameState.VOID_OVERLOAD
                    }
                }
                collisionEngine.floatingTexts.add(
                    FloatingText(x = paddle.centerX, y = paddle.top - 25f, text = "¡BARRIL EXPLOSIVO! 💥", color = Color(0xFFFF5722))
                )
            }
        }
    }

    private fun onWaveVictory() {
        _gameState.value = GameState.REALM_STABILIZED
        soundSynthesizer.playRealmVictory()
        hapticManager.onRealmComplete()

        val finalScore = score.intValue
        val target = currentWave.value.targetScore
        val stars = when {
            finalScore >= target && lives.intValue >= 2 -> 3
            finalScore >= (target * 0.6f).toInt() -> 2
            else -> 1
        }
        earnedStars.intValue = stars

        val lvl = levelNumber.intValue
        if (gameMode.value == GameMode.STORY_CAMPAIGN) {
            val isHigh = progressManager.setLevelHighScore(lvl, finalScore)
            isNewHighScore.value = isHigh
            progressManager.setLevelStars(lvl, stars)
            progressManager.highestCampaignLevel = lvl + 1
        } else {
            progressManager.endlessHighScore = finalScore
            progressManager.endlessHighestWave = lvl
        }
    }

    fun update(dt: Float, width: Float, height: Float) {
        if (isPaused.value) return

        screenWidth = width
        screenHeight = height

        paddle.y = height * 0.86f
        paddle.updateTimers(dt)

        if (frostNovaTimer > 0f) {
            frostNovaTimer = (frostNovaTimer - dt).coerceAtLeast(0f)
        }

        if (jarlCheerAnim.floatValue > 0f) {
            jarlCheerAnim.floatValue = (jarlCheerAnim.floatValue - dt * 2f).coerceAtLeast(0f)
        }

        val safeDt = dt.coerceIn(0.001f, 0.05f)

        // Dynamic Ocean Swells update
        for (swell in oceanSwells) {
            swell.update(if (frostNovaTimer > 0f) safeDt * 0.2f else safeDt, height)
        }

        // 1. Ambient Weather Particles
        val pIter = ambientParticles.iterator()
        while (pIter.hasNext()) {
            val p = pIter.next()
            p.x += p.vx * dt
            p.y += p.vy * dt
            if (p.y > height) {
                p.y = 0f
                p.x = Random.nextFloat() * width
            } else if (p.y < 0f) {
                p.y = height
                p.x = Random.nextFloat() * width
            }
        }

        // 2. Floating Scenery
        val sIter = floatingScenery.iterator()
        while (sIter.hasNext()) {
            val s = sIter.next()
            s.y += s.vy * dt
            if (s.y > height + 50f) {
                s.y = -50f
                s.x = Random.nextFloat() * (width - 100f) + 50f
            }
        }

        // 3. Sinking Ships
        val shipIter = enemyShips.iterator()
        while (shipIter.hasNext()) {
            val ship = shipIter.next()
            if (ship.isSinking) {
                ship.sinkProgress += dt * 0.85f
                ship.y += 25f * dt
                if (ship.sinkProgress >= 1f) {
                    shipIter.remove()
                }
            }
        }

        if (_gameState.value != GameState.PLAYING) return

        // 4. Update Enemy Ship Movement & Cannonball Fire (Frozen if Frost Nova is active)
        if (frostNovaTimer <= 0f) {
            for (ship in enemyShips) {
                if (ship.isSinking) continue
                ship.updateMovement(safeDt, width)

                // Cannon firing
                if (ship.canShoot) {
                    ship.cannonCooldown -= safeDt
                    if (ship.cannonCooldown <= 0f) {
                        cannonBalls.add(
                            CannonBall(x = ship.x, y = ship.bottom + 6f, vy = 380f)
                        )
                        soundSynthesizer.playCannonFire()
                        ship.cannonCooldown = if (ship.type.isBoss) 2.2f else Random.nextFloat() * 5f + 4f
                    }
                }

                // If an enemy ship reaches paddle line: emergency life lost!
                if (ship.bottom >= paddle.top - 15f && !ship.isDestroyed) {
                    ship.hit(99)
                    lives.intValue--
                    soundSynthesizer.playBallLost()
                    collisionEngine.screenShakeAmount = 18f
                }
            }
        }

        // 5. Update Cannonballs and Check Collisions with Paddle
        val cbIter = cannonBalls.iterator()
        while (cbIter.hasNext()) {
            val cb = cbIter.next()
            cb.y += cb.vy * safeDt
            if (cb.y > height) {
                cbIter.remove()
            }
        }
        collisionEngine.checkCannonPaddleCollisions(
            cannonBalls = cannonBalls,
            paddle = paddle,
            onPaddleHit = { isLifeLost ->
                if (isLifeLost) {
                    lives.intValue--
                    if (lives.intValue <= 0) {
                        _gameState.value = GameState.VOID_OVERLOAD
                    }
                }
            }
        )

        // 6. Update Viking Boarding Warriors
        val wIter = vikingWarriors.iterator()
        while (wIter.hasNext()) {
            val w = wIter.next()
            when (w.state) {
                WarriorState.LEAPING -> {
                    w.vy += 1200f * safeDt
                    w.x += w.vx * safeDt
                    w.y += w.vy * safeDt
                    w.rotation += 180f * safeDt

                    val targetShip = enemyShips.firstOrNull { ship ->
                        !ship.isDestroyed && !ship.isSinking &&
                                w.x in ship.left..ship.right && w.y in ship.top..ship.bottom
                    }
                    if (targetShip != null) {
                        w.state = WarriorState.SLASHING
                        w.boardingTimer = 0.35f
                        w.x = targetShip.x
                        w.y = targetShip.y
                        w.vx = 0f
                        w.vy = 0f
                        targetShip.hit(1)
                        collisionEngine.splinters.add(
                            SplinterParticle(
                                x = w.x,
                                y = w.y,
                                vx = (Random.nextFloat() - 0.5f) * 200f,
                                vy = -150f,
                                color = Color(0xFFFFD700)
                            )
                        )
                        collisionEngine.floatingTexts.add(
                            FloatingText(
                                x = w.x,
                                y = w.y - 15f,
                                text = "¡CORTE VIKINGO! 🪓",
                                color = Color(0xFFFFD700)
                            )
                        )
                        soundSynthesizer.playShipHit()
                    } else if (w.y > height) {
                        wIter.remove()
                    }
                }
                WarriorState.SLASHING -> {
                    w.boardingTimer -= safeDt
                    if (w.boardingTimer <= 0f) {
                        w.state = WarriorState.DIVING
                        w.vy = 280f
                        w.vx = (Random.nextFloat() - 0.5f) * 160f
                    }
                }
                WarriorState.DIVING -> {
                    w.y += w.vy * safeDt
                    w.x += w.vx * safeDt
                    if (w.y > paddle.top + 40f || w.y > height) {
                        collisionEngine.waterRipples.add(
                            WaterRipple(x = w.x, y = w.y.coerceAtMost(height - 20f), color = Color(0xFF00E5FF))
                        )
                        wIter.remove()
                    }
                }
                WarriorState.SPLASHED -> {
                    wIter.remove()
                }
            }
        }

        // 7. Dynamic Ocean Swell Wave Interceptions (Waves block cannonballs & projectiles)
        collisionEngine.checkWaveInterceptions(
            swells = oceanSwells,
            projectiles = projectiles,
            cannonBalls = cannonBalls
        )

        // 8. Move Projectiles
        collisionEngine.checkProjectileShipCollisions(
            projectiles = projectiles,
            ships = enemyShips,
            onShipDestroyed = { handleShipDestroyed(it) }
        )
        val projIter = projectiles.iterator()
        while (projIter.hasNext()) {
            val p = projIter.next()
            p.y += p.vy * safeDt
            if (p.y < 0) projIter.remove()
        }

        // 9. Move Power-Up Drops (with Vortex Magnet pull if active)
        if (paddle.isMagnetActive) {
            for (d in powerUps) {
                val dx = paddle.centerX - d.x
                val dy = paddle.top - d.y
                val dist = kotlin.math.hypot(dx.toDouble(), dy.toDouble()).toFloat()
                if (dist > 12f) {
                    d.x += (dx / dist) * 320f * safeDt
                    d.y += (dy / dist) * 320f * safeDt
                }
            }
        }

        collisionEngine.checkPowerUpCatch(
            powerUps = powerUps,
            paddle = paddle,
            onPowerUpCollected = { handlePowerUpCollected(it) }
        )
        val dropIter = powerUps.iterator()
        while (dropIter.hasNext()) {
            val d = dropIter.next()
            d.y += d.vy * safeDt
            if (d.y > height) dropIter.remove()
        }

        // 10. Move Balls and Check Collisions
        val ballIter = activeBalls.iterator()
        while (ballIter.hasNext()) {
            val ball = ballIter.next()

            if (ball.isAttachedToPaddle) {
                ball.x = paddle.centerX
                ball.y = paddle.top - ball.radius - 1f
                continue
            }

            ball.x += ball.vx * safeDt
            ball.y += ball.vy * safeDt
            ball.updateTrail()

            // Wall Collisions
            if (ball.x - ball.radius <= 0f) {
                ball.x = ball.radius
                ball.vx = -ball.vx
                soundSynthesizer.playPaddleHit()
            } else if (ball.x + ball.radius >= width) {
                ball.x = width - ball.radius
                ball.vx = -ball.vx
                soundSynthesizer.playPaddleHit()
            }

            if (ball.y - ball.radius <= height * 0.08f) {
                ball.y = height * 0.08f + ball.radius
                ball.vy = -ball.vy
                soundSynthesizer.playPaddleHit()
            }

            // Paddle Collision
            collisionEngine.checkBallPaddleCollision(ball, paddle)

            // Enemy Ships Collisions
            collisionEngine.checkBallShipCollisions(
                ball = ball,
                ships = enemyShips,
                onShipDestroyed = { handleShipDestroyed(it) },
                onBoardingSpawn = { launchVikingBoardingParty(it) }
            )

            // Bottom Death Plane
            if (ball.y - ball.radius > height) {
                ballIter.remove()
            }
        }

        // Check if all balls lost
        if (activeBalls.isEmpty()) {
            lives.intValue--
            soundSynthesizer.playBallLost()
            hapticManager.onRealmComplete()

            if (lives.intValue <= 0) {
                _gameState.value = GameState.VOID_OVERLOAD
            } else {
                spawnAttachedBall()
            }
        }

        // 10. Update Visual FX
        collisionEngine.updateSplintersAndTexts(safeDt)
    }
}
