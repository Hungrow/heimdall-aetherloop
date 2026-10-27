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
        if (ball.vy <= 0) return false // Only collide when moving downwards

        val closestX = ball.x.coerceIn(paddle.left, paddle.right)
        val closestY = ball.y.coerceIn(paddle.top, paddle.bottom)

        val distX = ball.x - closestX
        val distY = ball.y - closestY
        val distSq = distX * distX + distY * distY

        if (distSq <= ball.radius * ball.radius) {
            // Impact! Determine rebound angle based on hit position along paddle
            val normalizedHit = ((ball.x - paddle.centerX) / (paddle.width * 0.5f)).coerceIn(-0.95f, 0.95f)

            // Current speed with slight acceleration for excitement
            val currentSpeed = hypot(ball.vx, ball.vy).coerceIn(600f, 1100f) * 1.02f
            val maxAngleRad = 62.0 * (PI / 180.0) // 62 degrees max spread
            val bounceAngle = normalizedHit * maxAngleRad

            ball.vx = (currentSpeed * sin(bounceAngle)).toFloat()
            ball.vy = -(currentSpeed * cos(bounceAngle)).toFloat()
            ball.y = paddle.top - ball.radius - 1f

            // Add water ripple around paddle hit
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
                // Collision with longship!
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

                // Apply damage
                val damage = if (isPiercing) 2 else 1
                val sank = ship.hit(damage)

                // Spawn wood splinters
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
                    screenShakeAmount = if (ship.type == ShipType.NAGLFAR_BOSS) 20f else 8f
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

                    // 35% chance to trigger Viking Einherjar warrior boarding strike!
                    if (Random.nextFloat() < 0.35f) {
                        onBoardingSpawn?.invoke(ship)
                    }
                }

                if (!isPiercing) break
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
                        text = "¡${p.type.title.uppercase()}!",
                        color = p.type.color
                    )
                )
                waterRipples.add(
                    WaterRipple(x = paddle.centerX, y = paddle.top, maxRadius = 40f, color = p.type.color)
                )
                onPowerUpCollected(p.type)
                iter.remove()
            }
        }
    }

    fun updateSplintersAndTexts(dt: Float) {
        // Decay screen shake
        if (screenShakeAmount > 0f) {
            screenShakeAmount = (screenShakeAmount - dt * 25f).coerceAtLeast(0f)
        }

        // Splinter physics
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

        // Floating texts
        val tIter = floatingTexts.iterator()
        while (tIter.hasNext()) {
            val t = tIter.next()
            t.y -= 50f * dt
            t.life -= dt
            t.alpha = (t.life / 0.8f).coerceIn(0f, 1f)
            if (t.life <= 0f) tIter.remove()
        }

        // Water ripples
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
