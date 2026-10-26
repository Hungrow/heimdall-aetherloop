package com.heimdall.aetherloop.game.physics

import android.graphics.PointF
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * Parametric Math Representation of the Bifröst Loop Track.
 *
 * Represents an elegant stadium-shaped racetrack or rounded circuit loop.
 * Vessels travel clockwise around this closed perimeter.
 * The top horizontal segment passes directly beneath the celestial conduits.
 */
class BifrostTrack {

    var width: Float = 1000f
    var height: Float = 1800f

    // Track boundaries (computed in updateDimensions)
    var left: Float = 100f
    var right: Float = 900f
    var top: Float = 400f
    var bottom: Float = 1100f
    var cornerRadius: Float = 160f

    private var straightLength: Float = 0f
    private var verticalLength: Float = 0f
    private var cornerArcLength: Float = 0f
    var totalPerimeter: Float = 0f

    fun updateDimensions(w: Float, h: Float) {
        width = w
        height = h

        val marginHorizontal = w * 0.10f
        left = marginHorizontal
        right = w - marginHorizontal

        top = h * 0.28f      // Top section right below emitters (conduits at 0.12f - 0.22f)
        bottom = h * 0.70f   // Bottom loop section

        cornerRadius = ((right - left) * 0.28f).coerceAtMost((bottom - top) * 0.45f)

        straightLength = (right - left) - (2f * cornerRadius)
        verticalLength = (bottom - top) - (2f * cornerRadius)
        cornerArcLength = (0.5f * PI.toFloat() * cornerRadius)

        // 2 straights horizontal + 2 straights vertical + 4 corners
        totalPerimeter = (2f * straightLength) + (2f * verticalLength) + (4f * cornerArcLength)
    }

    /**
     * Calculates the exact (x, y) coordinates on the track for normalized progress t in [0, 1].
     * Progression flows Clockwise:
     * 1. Top straight (moving Left to Right) -> Directly catches falling aether!
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

        // Section 2: Top-Right corner (center: right - cornerRadius, top + cornerRadius)
        // Angle from -PI/2 (top) to 0 (right)
        if (dist <= cornerArcLength) {
            val angle = -PI.toFloat() / 2f + (dist / cornerArcLength) * (PI.toFloat() / 2f)
            val cx = right - cornerRadius
            val cy = top + cornerRadius
            outPoint.x = cx + cos(angle) * cornerRadius
            outPoint.y = cy + sin(angle) * cornerRadius
            return outPoint
        }
        dist -= cornerArcLength

        // Section 3: Right vertical straight (x = right, y goes down)
        if (dist <= verticalLength) {
            val frac = dist / verticalLength
            outPoint.x = right
            outPoint.y = top + cornerRadius + (frac * verticalLength)
            return outPoint
        }
        dist -= verticalLength

        // Section 4: Bottom-Right corner (center: right - cornerRadius, bottom - cornerRadius)
        // Angle from 0 to PI/2
        if (dist <= cornerArcLength) {
            val angle = 0f + (dist / cornerArcLength) * (PI.toFloat() / 2f)
            val cx = right - cornerRadius
            val cy = bottom - cornerRadius
            outPoint.x = cx + cos(angle) * cornerRadius
            outPoint.y = cy + sin(angle) * cornerRadius
            return outPoint
        }
        dist -= cornerArcLength

        // Section 5: Bottom straight (moving Right to Left, y = bottom)
        if (dist <= straightLength) {
            val frac = dist / straightLength
            outPoint.x = (right - cornerRadius) - (frac * straightLength)
            outPoint.y = bottom
            return outPoint
        }
        dist -= straightLength

        // Section 6: Bottom-Left corner (center: left + cornerRadius, bottom - cornerRadius)
        // Angle from PI/2 to PI
        if (dist <= cornerArcLength) {
            val angle = (PI.toFloat() / 2f) + (dist / cornerArcLength) * (PI.toFloat() / 2f)
            val cx = left + cornerRadius
            val cy = bottom - cornerRadius
            outPoint.x = cx + cos(angle) * cornerRadius
            outPoint.y = cy + sin(angle) * cornerRadius
            return outPoint
        }
        dist -= cornerArcLength

        // Section 7: Left vertical straight (x = left, y goes up)
        if (dist <= verticalLength) {
            val frac = dist / verticalLength
            outPoint.x = left
            outPoint.y = (bottom - cornerRadius) - (frac * verticalLength)
            return outPoint
        }
        dist -= verticalLength

        // Section 8: Top-Left corner (center: left + cornerRadius, top + cornerRadius)
        // Angle from PI to 3*PI/2
        val angle = PI.toFloat() + (dist / cornerArcLength) * (PI.toFloat() / 2f)
        val cx = left + cornerRadius
        val cy = top + cornerRadius
        outPoint.x = cx + cos(angle) * cornerRadius
        outPoint.y = cy + sin(angle) * cornerRadius
        return outPoint
    }

    /**
     * Approximate orientation / rotation angle (in degrees) of a vessel at progress t.
     */
    fun getOrientationAtProgress(progress: Float): Float {
        val p1 = getPointAtProgress(progress)
        val p2 = getPointAtProgress((progress + 0.005f) % 1f)
        val rad = atan2(p2.y - p1.y, p2.x - p1.x)
        return Math.toDegrees(rad.toDouble()).toFloat()
    }
}
