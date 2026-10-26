package com.heimdall.aetherloop.game.physics

import android.graphics.PointF
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * Parametric Math Representation of the Bifröst Loop Track.
 *
 * Represents a stadium-shaped conveyor loop positioned beneath the sand art hoppers.
 * Vessels travel clockwise around this closed perimeter.
 */
class BifrostTrack {

    var width: Float = 1000f
    var height: Float = 1800f

    // Track boundaries
    var left: Float = 120f
    var right: Float = 880f
    var top: Float = 940f      // Top section right below funnels
    var bottom: Float = 1450f  // Bottom loop section
    var cornerRadius: Float = 140f

    private var straightLength: Float = 0f
    private var verticalLength: Float = 0f
    private var cornerArcLength: Float = 0f
    var totalPerimeter: Float = 0f

    // Belt texture scroll animation offset [0, 1)
    var beltScrollOffset: Float = 0f

    fun updateDimensions(w: Float, h: Float) {
        width = w
        height = h

        val marginH = w * 0.12f
        left = marginH
        right = w - marginH

        top = h * 0.52f     // Top straight right below funnels (which sit at ~0.46f - 0.50f)
        bottom = h * 0.81f  // Bottom straight

        cornerRadius = ((right - left) * 0.28f).coerceAtMost((bottom - top) * 0.45f)

        straightLength = (right - left) - (2f * cornerRadius)
        verticalLength = (bottom - top) - (2f * cornerRadius)
        cornerArcLength = (0.5f * PI.toFloat() * cornerRadius)

        totalPerimeter = (2f * straightLength) + (2f * verticalLength) + (4f * cornerArcLength)
    }

    /**
     * Calculates the exact (x, y) coordinates on the track for normalized progress t in [0, 1].
     * Progression flows Clockwise:
     * 1. Top straight (moving Left to Right) -> Intercepts falling sand!
     * 2. Top-Right corner curve (heading Down)
     * 3. Right vertical straight (moving Down)
     * 4. Bottom-Right corner curve (heading Left)
     * 5. Bottom straight (moving Right to Left)
     * 6. Bottom-Left corner curve (heading Up)
     * 7. Left vertical straight (moving Up)
     * 8. Top-Left corner curve (heading Right into top straight)
     */
    fun getPointAtProgress(progress: Float, outPoint: PointF = PointF()): PointF {
        val normT = ((progress % 1f) + 1f) % 1f
        var dist = normT * totalPerimeter

        // Section 1: Top straight (left + cornerRadius -> right - cornerRadius, y = top)
        if (dist <= straightLength) {
            val frac = dist / straightLength
            outPoint.x = left + cornerRadius + (frac * straightLength)
            outPoint.y = top
            return outPoint
        }
        dist -= straightLength

        // Section 2: Top-Right corner
        if (dist <= cornerArcLength) {
            val angle = -PI.toFloat() / 2f + (dist / cornerArcLength) * (PI.toFloat() / 2f)
            val cx = right - cornerRadius
            val cy = top + cornerRadius
            outPoint.x = cx + cos(angle) * cornerRadius
            outPoint.y = cy + sin(angle) * cornerRadius
            return outPoint
        }
        dist -= cornerArcLength

        // Section 3: Right vertical straight
        if (dist <= verticalLength) {
            val frac = dist / verticalLength
            outPoint.x = right
            outPoint.y = top + cornerRadius + (frac * verticalLength)
            return outPoint
        }
        dist -= verticalLength

        // Section 4: Bottom-Right corner
        if (dist <= cornerArcLength) {
            val angle = 0f + (dist / cornerArcLength) * (PI.toFloat() / 2f)
            val cx = right - cornerRadius
            val cy = bottom - cornerRadius
            outPoint.x = cx + cos(angle) * cornerRadius
            outPoint.y = cy + sin(angle) * cornerRadius
            return outPoint
        }
        dist -= cornerArcLength

        // Section 5: Bottom straight (Right to Left)
        if (dist <= straightLength) {
            val frac = dist / straightLength
            outPoint.x = (right - cornerRadius) - (frac * straightLength)
            outPoint.y = bottom
            return outPoint
        }
        dist -= straightLength

        // Section 6: Bottom-Left corner
        if (dist <= cornerArcLength) {
            val angle = (PI.toFloat() / 2f) + (dist / cornerArcLength) * (PI.toFloat() / 2f)
            val cx = left + cornerRadius
            val cy = bottom - cornerRadius
            outPoint.x = cx + cos(angle) * cornerRadius
            outPoint.y = cy + sin(angle) * cornerRadius
            return outPoint
        }
        dist -= cornerArcLength

        // Section 7: Left vertical straight
        if (dist <= verticalLength) {
            val frac = dist / verticalLength
            outPoint.x = left
            outPoint.y = (bottom - cornerRadius) - (frac * verticalLength)
            return outPoint
        }
        dist -= verticalLength

        // Section 8: Top-Left corner
        val angle = PI.toFloat() + (dist / cornerArcLength) * (PI.toFloat() / 2f)
        val cx = left + cornerRadius
        val cy = top + cornerRadius
        outPoint.x = cx + cos(angle) * cornerRadius
        outPoint.y = cy + sin(angle) * cornerRadius
        return outPoint
    }

    /**
     * Orientation angle (in degrees) of a vessel at progress t.
     */
    fun getOrientationAtProgress(progress: Float): Float {
        val p1 = getPointAtProgress(progress)
        val p2 = getPointAtProgress((progress + 0.005f) % 1f)
        val rad = atan2(p2.y - p1.y, p2.x - p1.x)
        return Math.toDegrees(rad.toDouble()).toFloat()
    }
}
