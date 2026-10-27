package com.heimdall.aetherloop.game.engine

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import com.heimdall.aetherloop.core.HapticManager
import com.heimdall.aetherloop.core.PlayerProgressManager
import com.heimdall.aetherloop.core.SoundSynthesizer
import com.heimdall.aetherloop.game.model.*
import com.heimdall.aetherloop.game.physics.CollisionEngine
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

    val paddle = Paddle()
    val activeBalls = mutableStateListOf<Ball>()
    val enemyShips = mutableStateListOf<EnemyShip>()
    val powerUps = mutableStateListOf<PowerUpDrop>()
    val projectiles = mutableStateListOf<Projectile>()

    val lives = mutableIntStateOf(3)
    val score = mutableIntStateOf(0)
    val isBallAttached = mutableStateOf(true)

    val currentWave = mutableStateOf(FleetWave.ALL_WAVES.first())
    val waveIndex = mutableIntStateOf(0)

    val earnedStars = mutableIntStateOf(0)
    val isNewHighScore = mutableStateOf(false)

    private val _gameState = MutableStateFlow(GameState.MISSION_BRIEFING)
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private var screenWidth = 1080f
    private var screenHeight = 1920f

    init {
        loadWave(0)
    }

    fun startWaveAction() {
        _gameState.value = GameState.PLAYING
        launchBall()
    }

    fun loadWave(index: Int) {
        val safeIndex = index.coerceIn(0, FleetWave.ALL_WAVES.lastIndex)
        waveIndex.intValue = safeIndex
        currentWave.value = FleetWave.ALL_WAVES[safeIndex]

        activeBalls.clear()
        enemyShips.clear()
        powerUps.clear()
        projectiles.clear()
        collisionEngine.splinters.clear()
        collisionEngine.floatingTexts.clear()

        lives.intValue = 3
        isBallAttached.value = true
        earnedStars.intValue = 0
        isNewHighScore.value = false

        paddle.width = paddle.baseWidth
        paddle.hasBallistas = false

        buildFleetFormation(safeIndex, screenWidth, screenHeight)
        spawnAttachedBall()

        _gameState.value = GameState.MISSION_BRIEFING
    }

    fun restartCurrentWave() {
        loadWave(waveIndex.intValue)
    }

    fun nextWave() {
        if (waveIndex.intValue < FleetWave.ALL_WAVES.lastIndex) {
            loadWave(waveIndex.intValue + 1)
        } else {
            loadWave(0) // loop back to first
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
            // Launch upward with slight angle
            val launchAngle = (Random.nextFloat() - 0.5f) * 0.5f
            val baseSpeed = 750f
            ball.vx = baseSpeed * launchAngle
            ball.vy = -baseSpeed
            isBallAttached.value = false
            soundSynthesizer.playLaunchWhoosh()
        }
    }

    fun dragPaddle(newCenterX: Float) {
        val halfW = paddle.width / 2f
        paddle.centerX = newCenterX.coerceIn(halfW, screenWidth - halfW)

        if (isBallAttached.value && activeBalls.isNotEmpty()) {
            val ball = activeBalls.first()
            ball.x = paddle.centerX
            ball.y = paddle.top - ball.radius - 1f
        }
    }

    fun fireBallistas() {
        if (paddle.hasBallistas && _gameState.value == GameState.PLAYING) {
            // Twin bolts from left and right side of paddle
            projectiles.add(Projectile(x = paddle.left + 14f, y = paddle.top))
            projectiles.add(Projectile(x = paddle.right - 14f, y = paddle.top))
            soundSynthesizer.playLaserShot()
            hapticManager.onParticleCaptured()
        }
    }

    fun handleScreenTap(offset: Offset) {
        if (_gameState.value != GameState.PLAYING) return

        if (isBallAttached.value) {
            launchBall()
        } else if (paddle.hasBallistas) {
            fireBallistas()
        }
    }

    private fun buildFleetFormation(waveIdx: Int, w: Float, h: Float) {
        val safeW = if (w > 100f) w else 1080f
        val safeH = if (h > 100f) h else 1920f

        val startY = safeH * 0.16f
        val shipW = (safeW * 0.19f).coerceIn(60f, 95f)
        val shipH = (safeH * 0.045f).coerceIn(34f, 55f)

        when (waveIdx) {
            0 -> {
                // Wave 1: 3 rows of Nimble Scouts (Karves)
                val rows = 3
                val cols = 4
                val spacingX = safeW * 0.80f / cols
                val startX = safeW * 0.10f + spacingX * 0.5f

                for (r in 0 until rows) {
                    for (c in 0 until cols) {
                        val shipX = startX + c * spacingX
                        val shipY = startY + r * (shipH + 18f)
                        val drop = if (r == 0 && c == 1) PowerUpType.MULTIBALL else if (r == 1 && c == 2) PowerUpType.MJOLNIR else null

                        enemyShips.add(
                            EnemyShip(
                                id = shipIdGen.incrementAndGet(),
                                x = shipX,
                                y = shipY,
                                width = shipW,
                                height = shipH,
                                type = ShipType.SCOUT,
                                powerUpType = drop
                            )
                        )
                    }
                }
            }
            1 -> {
                // Wave 2: Shield-Wall Drakkars + Scouts
                val rows = 4
                val cols = 4
                val spacingX = safeW * 0.82f / cols
                val startX = safeW * 0.09f + spacingX * 0.5f

                for (r in 0 until rows) {
                    for (c in 0 until cols) {
                        val shipX = startX + c * spacingX
                        val shipY = startY + r * (shipH + 16f)
                        val isArmored = r % 2 == 0
                        val drop = when {
                            r == 1 && c == 0 -> PowerUpType.BALLISTAS
                            r == 2 && c == 2 -> PowerUpType.SHIELD_EXPAND
                            else -> null
                        }

                        enemyShips.add(
                            EnemyShip(
                                id = shipIdGen.incrementAndGet(),
                                x = shipX,
                                y = shipY,
                                width = shipW,
                                height = shipH,
                                type = if (isArmored) ShipType.ARMORED_DRAKKAR else ShipType.SCOUT,
                                powerUpType = drop
                            )
                        )
                    }
                }
            }
            2 -> {
                // Wave 3: Jötunheim Ice Fleet (Frost Brigs & Armored)
                val rows = 4
                val cols = 5
                val spacingX = safeW * 0.84f / cols
                val startX = safeW * 0.08f + spacingX * 0.5f

                for (r in 0 until rows) {
                    for (c in 0 until cols) {
                        val shipX = startX + c * spacingX
                        val shipY = startY + r * (shipH + 16f)
                        val type = when {
                            r == 0 -> ShipType.FROST_BRIG
                            r % 2 == 1 -> ShipType.ARMORED_DRAKKAR
                            else -> ShipType.SCOUT
                        }
                        val drop = if (r == 0 && c == 2) PowerUpType.SURTR_FIRE else if (r == 2 && c == 1) PowerUpType.MULTIBALL else null

                        enemyShips.add(
                            EnemyShip(
                                id = shipIdGen.incrementAndGet(),
                                x = shipX,
                                y = shipY,
                                width = shipW,
                                height = shipH,
                                type = type,
                                powerUpType = drop
                            )
                        )
                    }
                }
            }
            3 -> {
                // Wave 4: Surtr's Fire Armada
                val rows = 5
                val cols = 4
                val spacingX = safeW * 0.82f / cols
                val startX = safeW * 0.09f + spacingX * 0.5f

                for (r in 0 until rows) {
                    for (c in 0 until cols) {
                        val shipX = startX + c * spacingX
                        val shipY = startY + r * (shipH + 16f)
                        val type = when (r) {
                            0, 1 -> ShipType.FIRE_DREADNOUGHT
                            2 -> ShipType.FROST_BRIG
                            else -> ShipType.ARMORED_DRAKKAR
                        }
                        val drop = if (r == 0 && c == 3) PowerUpType.BALLISTAS else if (r == 1 && c == 1) PowerUpType.REPAIR_LIFE else null

                        enemyShips.add(
                            EnemyShip(
                                id = shipIdGen.incrementAndGet(),
                                x = shipX,
                                y = shipY,
                                width = shipW,
                                height = shipH,
                                type = type,
                                powerUpType = drop
                            )
                        )
                    }
                }
            }
            else -> {
                // Wave 5: BOSS NAGLFAR + Elite Vanguard!
                val bossW = safeW * 0.65f
                val bossH = shipH * 2.2f
                enemyShips.add(
                    EnemyShip(
                        id = shipIdGen.incrementAndGet(),
                        x = safeW * 0.5f,
                        y = startY + bossH * 0.5f,
                        width = bossW,
                        height = bossH,
                        type = ShipType.NAGLFAR_BOSS,
                        powerUpType = PowerUpType.MJOLNIR
                    )
                )

                // Flanking escorts
                val escortsY = startY + bossH + 25f
                val cols = 4
                val spacingX = safeW * 0.82f / cols
                val startX = safeW * 0.09f + spacingX * 0.5f
                for (c in 0 until cols) {
                    val drop = if (c == 0) PowerUpType.BALLISTAS else if (c == 3) PowerUpType.MULTIBALL else null
                    enemyShips.add(
                        EnemyShip(
                            id = shipIdGen.incrementAndGet(),
                            x = startX + c * spacingX,
                            y = escortsY,
                            width = shipW,
                            height = shipH,
                            type = ShipType.FIRE_DREADNOUGHT,
                            powerUpType = drop
                        )
                    )
                }
            }
        }
    }

    private fun handleShipDestroyed(ship: EnemyShip) {
        score.intValue += ship.type.scoreValue

        // Chance to spawn falling power-up
        val dropType = ship.powerUpType ?: if (Random.nextFloat() < 0.22f) {
            PowerUpType.entries.random()
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

        // Check if all ships destroyed -> Wave Cleared!
        if (enemyShips.isEmpty()) {
            onWaveVictory()
        }
    }

    private fun handlePowerUpCollected(type: PowerUpType) {
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

        val isHigh = progressManager.setRealmHighScore(waveIndex.intValue, finalScore)
        isNewHighScore.value = isHigh
        progressManager.setRealmStars(waveIndex.intValue, stars)

        if (waveIndex.intValue + 1 > progressManager.highestUnlockedRealm) {
            progressManager.highestUnlockedRealm = waveIndex.intValue + 1
        }
    }

    fun update(dt: Float, width: Float, height: Float) {
        screenWidth = width
        screenHeight = height

        paddle.y = height * 0.86f
        paddle.updateTimers(dt)

        if (_gameState.value != GameState.PLAYING) return

        val safeDt = dt.coerceIn(0.001f, 0.05f)

        // 1. Move Projectiles
        collisionEngine.checkProjectileShipCollisions(
            projectiles = projectiles,
            ships = enemyShips,
            onShipDestroyed = { handleShipDestroyed(it) }
        )
        val pIter = projectiles.iterator()
        while (pIter.hasNext()) {
            val p = pIter.next()
            p.y += p.vy * safeDt
            if (p.y < 0) pIter.remove()
        }

        // 2. Move Power-Up Drops
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

        // 3. Move Balls and Check Collisions
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

            if (ball.y - ball.radius <= height * 0.08f) { // Top wall below top status bar
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
                onShipDestroyed = { handleShipDestroyed(it) }
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
                _gameState.value = GameState.VOID_OVERLOAD // Game Over
            } else {
                spawnAttachedBall()
            }
        }

        // 4. Update Visual FX
        collisionEngine.updateSplintersAndTexts(safeDt)
    }
}
