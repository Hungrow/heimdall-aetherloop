package com.heimdall.aetherloop.game.physics

import androidx.compose.ui.graphics.Color
import com.heimdall.aetherloop.core.HapticManager
import com.heimdall.aetherloop.core.SoundSynthesizer
import com.heimdall.aetherloop.game.model.*
import kotlin.math.*
import kotlin.random.Random

/**
 * Visual wood splinter particle from destroyed/damaged Viking ships.
 */
data class SplinterParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val color: Color,
    val size: Float = 4f,
    var life: Float = 0.5f,
    var alpha: Float = 1.0f
)

/**
 * Complete Arkanoid Physics & Collision Engine for Viking Naval Combat.
 */
class CollisionEngine(
    val soundSynthesizer: SoundSynthesizer,
    val hapticManager: HapticManager
) {
    val splinters = mutableListOf<SplinterParticle>()
    val floatingTexts = mutableListOf<FloatingText>()
    val waterRipples = mutableListOf<WaterRipple>()

    var screenShakeAmount: Float = 0f

    /**
     * Resolves ball vs paddle collision with angle-directed deflection.
     */
    fun checkBallPaddleCollision(ball: Ball, paddle: Paddle): Boolean {
        if (ball.vy <= 0) return false

        val closestX = ball.x.coerceIn(paddle.left, paddle.right)
        val closestY = ball.y.coerceIn(paddle.top, paddle.bottom)

        val distX = ball.x - closestX
        val distY = ball.y - closestY
        val distSq = distX * distX + distY * distY

        if (distSq <= ball.radius * ball.radius) {
            val normalizedHit = ((ball.x - paddle.centerX) / (paddle.width * 0.5f)).coerceIn(-0.95f, 0.95f)
            val currentSpeed = hypot(ball.vx, ball.vy).coerceIn(600f, 1150f) * 1.02f
            val maxAngleRad = 62.0 * (PI / 180.0)
            val bounceAngle = normalizedHit * maxAngleRad

            ball.vx = (currentSpeed * sin(bounceAngle)).toFloat()
            ball.vy = -(currentSpeed * cos(bounceAngle)).toFloat()
            ball.y = paddle.top - ball.radius - 1f

            waterRipples.add(
                WaterRipple(x = ball.x, y = paddle.top, color = Color(0xFF00E5FF))
            )

            soundSynthesizer.playPaddleHit()
            hapticManager.onParticleCaptured()

            return true
        }
        return false
    }

    /**
     * Resolves enemy cannonballs vs player paddle.
     * Downgrades player ship tier on hit, or costs a life if already at Tier 1.
     */
    fun checkCannonPaddleCollisions(
        cannonBalls: MutableList<CannonBall>,
        paddle: Paddle,
        onPaddleHit: (isLifeLost: Boolean) -> Unit
    ) {
        val iter = cannonBalls.iterator()
        while (iter.hasNext()) {
            val cb = iter.next()
            if (cb.x in paddle.left..paddle.right && cb.y in paddle.top..paddle.bottom) {
                // Cannonball hit player ship!
                screenShakeAmount = 14f
                soundSynthesizer.playCannonHit()
                hapticManager.onVesselFilled()

                // Spawn fiery splinters
                for (i in 0 until 8) {
                    splinters.add(
                        SplinterParticle(
                            x = cb.x,
                            y = cb.y,
                            vx = (Random.nextFloat() - 0.5f) * 260f,
                            vy = -Random.nextFloat() * 200f,
                            color = Color(0xFFFF5722)
                        )
                    )
                }

                // Downgrade paddle tier or lose life
                val downgraded = paddle.downgradeTier()
                if (downgraded) {
                    soundSynthesizer.playShipDowngrade()
                    floatingTexts.add(
                        FloatingText(
                            x = paddle.centerX,
                            y = paddle.top - 20f,
                            text = "¡CASCO DAÑADO! TIER ${paddle.tier}",
                            color = Color(0xFFFF5722)
                        )
                    )
                    onPaddleHit(false)
                } else {
                    floatingTexts.add(
                        FloatingText(
                            x = paddle.centerX,
                            y = paddle.top - 20f,
                            text = "¡IMPACTO DIRECTO! -1 VIDA",
                            color = Color(0xFFFF1744)
                        )
                    )
                    onPaddleHit(true)
                }

                iter.remove()
            }
        }
    }

    /**
     * Resolves ball vs enemy longships collisions.
     */
    fun checkBallShipCollisions(
        ball: Ball,
        ships: MutableList<EnemyShip>,
        onShipDestroyed: (EnemyShip) -> Unit,
        onBoardingSpawn: ((EnemyShip) -> Unit)? = null
    ) {
        val iterator = ships.iterator()
        while (iterator.hasNext()) {
            val ship = iterator.next()
            if (ship.isDestroyed || ship.isSinking) continue

            val closestX = ball.x.coerceIn(ship.left, ship.right)
            val closestY = ball.y.coerceIn(ship.top, ship.bottom)

            val distX = ball.x - closestX
            val distY = ball.y - closestY
            val distSq = distX * distX + distY * distY

            if (distSq <= ball.radius * ball.radius) {
                val isPiercing = ball.type.isPiercing

                if (!isPiercing) {
                    val overlapX = (ball.radius + ship.width / 2f) - abs(ball.x - ship.x)
                    val overlapY = (ball.radius + ship.height / 2f) - abs(ball.y - ship.y)

                    if (overlapX < overlapY) {
                        ball.vx = -ball.vx
                    } else {
                        ball.vy = -ball.vy
                    }
                }

                val damage = if (isPiercing) 2 else 1
                val sank = ship.hit(damage)

                val particleColor = ship.type.primaryColor
                for (i in 0 until (if (sank) 14 else 5)) {
                    splinters.add(
                        SplinterParticle(
                            x = closestX,
                            y = closestY,
                            vx = (Random.nextFloat() - 0.5f) * 320f,
                            vy = (Random.nextFloat() - 0.5f) * 320f,
                            color = if (Random.nextBoolean()) Color(0xFF8D6E63) else particleColor
                        )
                    )
                }

                if (sank) {
                    screenShakeAmount = if (ship.type.isBoss) 24f else 8f
                    soundSynthesizer.playShipExplosion()
                    hapticManager.onVesselFilled()
                    floatingTexts.add(
                        FloatingText(
                            x = ship.x,
                            y = ship.y - 10f,
                            text = "+${ship.type.scoreValue}",
                            color = ship.type.primaryColor
                        )
                    )
                    waterRipples.add(
                        WaterRipple(x = ship.x, y = ship.y + 10f, maxRadius = 60f, color = ship.type.primaryColor)
                    )
                    onShipDestroyed(ship)
                } else {
                    soundSynthesizer.playShipHit()
                    hapticManager.onParticleCaptured()

                    if (Random.nextFloat() < 0.35f) {
                        onBoardingSpawn?.invoke(ship)
                    }
                }

                if (!isPiercing) break
            }
        }
    }

    /**
     * Intercepts and absorbs player laser bolts and enemy cannonballs when an Ocean Swell wave
     * surges at peak crest!
     */
    fun checkWaveInterceptions(
        swells: List<OceanSwell>,
        projectiles: MutableList<Projectile>,
        cannonBalls: MutableList<CannonBall>
    ) {
        val blockingSwells = swells.filter { it.isBlocking }
        if (blockingSwells.isEmpty()) return

        // Intercept player projectiles
        val projIter = projectiles.iterator()
        while (projIter.hasNext()) {
            val p = projIter.next()
            for (swell in blockingSwells) {
                val halfH = swell.height * 0.6f
                if (p.y in (swell.y - halfH)..(swell.y + halfH)) {
                    // Blocked by wave!
                    waterRipples.add(
                        WaterRipple(x = p.x, y = p.y, maxRadius = 38f, color = Color(0xFF00E5FF))
                    )
                    for (i in 0 until 4) {
                        splinters.add(
                            SplinterParticle(
                                x = p.x,
                                y = p.y,
                                vx = (Random.nextFloat() - 0.5f) * 160f,
                                vy = -Random.nextFloat() * 120f,
                                color = Color(0xFFE0F7FA),
                                size = 3f
                            )
                        )
                    }
                    floatingTexts.add(
                        FloatingText(x = p.x, y = p.y - 12f, text = "¡OLA! 🌊", color = Color(0xFF00E5FF))
                    )
                    soundSynthesizer.playWaterSplash()
                    projIter.remove()
                    break
                }
            }
        }

        // Intercept enemy cannonballs
        val cbIter = cannonBalls.iterator()
        while (cbIter.hasNext()) {
            val cb = cbIter.next()
            for (swell in blockingSwells) {
                val halfH = swell.height * 0.6f
                if (cb.y in (swell.y - halfH)..(swell.y + halfH)) {
                    // Absorbed by wave crest!
                    waterRipples.add(
                        WaterRipple(x = cb.x, y = cb.y, maxRadius = 45f, color = Color(0xFF00E5FF))
                    )
                    for (i in 0 until 6) {
                        splinters.add(
                            SplinterParticle(
                                x = cb.x,
                                y = cb.y,
                                vx = (Random.nextFloat() - 0.5f) * 180f,
                                vy = -Random.nextFloat() * 140f,
                                color = Color(0xFF80DEEA),
                                size = 3.5f
                            )
                        )
                    }
                    floatingTexts.add(
                        FloatingText(x = cb.x, y = cb.y - 12f, text = "¡OLA! 🌊", color = Color(0xFF80DEEA))
                    )
                    soundSynthesizer.playWaterSplash()
                    hapticManager.onParticleCaptured()
                    cbIter.remove()
                    break
                }
            }
        }
    }

    /**
     * Resolves ballista projectiles vs enemy ships.
     */
    fun checkProjectileShipCollisions(
        projectiles: MutableList<Projectile>,
        ships: MutableList<EnemyShip>,
        onShipDestroyed: (EnemyShip) -> Unit
    ) {
        val projIter = projectiles.iterator()
        while (projIter.hasNext()) {
            val proj = projIter.next()
            var consumed = false

            val shipIter = ships.iterator()
            while (shipIter.hasNext()) {
                val ship = shipIter.next()
                if (ship.isDestroyed || ship.isSinking) continue

                if (proj.x in ship.left..ship.right && proj.y in ship.top..ship.bottom) {
                    val sank = ship.hit(proj.damage)

                    for (i in 0 until 5) {
                        splinters.add(
                            SplinterParticle(
                                x = proj.x,
                                y = proj.y,
                                vx = (Random.nextFloat() - 0.5f) * 220f,
                                vy = (Random.nextFloat() - 0.5f) * 220f,
                                color = Color(0xFFD500F9)
                            )
                        )
                    }

                    if (sank) {
                        screenShakeAmount = 7f
                        soundSynthesizer.playShipExplosion()
                        hapticManager.onVesselFilled()
                        floatingTexts.add(
                            FloatingText(
                                x = ship.x,
                                y = ship.y - 10f,
                                text = "+${ship.type.scoreValue}",
                                color = ship.type.primaryColor
                            )
                        )
                        waterRipples.add(
                            WaterRipple(x = ship.x, y = ship.y, maxRadius = 45f, color = Color(0xFFD500F9))
                        )
                        onShipDestroyed(ship)
                    } else {
                        soundSynthesizer.playShipHit()
                    }

                    consumed = true
                    break
                }
            }

            if (consumed) {
                projIter.remove()
            }
        }
    }

    /**
     * Checks if paddle caught any falling power-up runes.
     */
    fun checkPowerUpCatch(
        powerUps: MutableList<PowerUpDrop>,
        paddle: Paddle,
        onPowerUpCollected: (PowerUpType) -> Unit
    ) {
        val caughtTypes = mutableListOf<PowerUpType>()
        val iter = powerUps.iterator()
        while (iter.hasNext()) {
            val p = iter.next()
            if (p.x in paddle.left..paddle.right && p.y in paddle.top..paddle.bottom) {
                soundSynthesizer.playPowerUpCatch()
                hapticManager.onVesselFilled()
                floatingTexts.add(
                    FloatingText(
                        x = paddle.centerX,
                        y = paddle.top - 20f,
                        text = if (p.type.isCurse) "¡${p.type.title.uppercase()}!" else "¡${p.type.title.uppercase()}!",
                        color = p.type.color
                    )
                )
                waterRipples.add(
                    WaterRipple(x = paddle.centerX, y = paddle.top, maxRadius = 40f, color = p.type.color)
                )
                caughtTypes.add(p.type)
                iter.remove()
            }
        }
        // Invoke callbacks safely outside iteration
        for (type in caughtTypes) {
            onPowerUpCollected(type)
        }
    }

    fun updateSplintersAndTexts(dt: Float) {
        if (screenShakeAmount > 0f) {
            screenShakeAmount = (screenShakeAmount - dt * 25f).coerceAtLeast(0f)
        }

        val sIter = splinters.iterator()
        while (sIter.hasNext()) {
            val s = sIter.next()
            s.vy += 650f * dt
            s.x += s.vx * dt
            s.y += s.vy * dt
            s.life -= dt
            s.alpha = (s.life / 0.5f).coerceIn(0f, 1f)
            if (s.life <= 0f) sIter.remove()
        }

        val tIter = floatingTexts.iterator()
        while (tIter.hasNext()) {
            val t = tIter.next()
            t.y -= 50f * dt
            t.life -= dt
            t.alpha = (t.life / 0.8f).coerceIn(0f, 1f)
            if (t.life <= 0f) tIter.remove()
        }

        val rIter = waterRipples.iterator()
        while (rIter.hasNext()) {
            val r = rIter.next()
            r.radius += (r.maxRadius - r.radius) * 8f * dt
            r.alpha = (r.alpha - dt * 1.5f).coerceAtLeast(0f)
            if (r.alpha <= 0.02f || r.radius >= r.maxRadius * 0.98f) {
                rIter.remove()
            }
        }
    }
}
