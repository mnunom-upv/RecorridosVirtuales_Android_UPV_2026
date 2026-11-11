package upv.pm.may_ago_26.iti_271503.pgu2_eq04

import kotlin.math.sqrt

/**
 * Created by mtsch on 11/16/2015.
 */
class Vector2 {
    var x: Float = 0f
    var y: Float = 0f

    constructor()

    constructor(xValue: Float, yValue: Float) {
        set(xValue, yValue)
    }

    constructor(other: Vector2) {
        set(other)
    }

    constructor(vec: FloatArray) {
        //Assume array has at least 2 elements--speed optimization
        x = vec[0]
        y = vec[1]
    }

    fun add(other: Vector2): Vector2 {
        x += other.x
        y += other.y
        return this
    }

    fun add(otherX: Float, otherY: Float, otherZ: Float): Vector2 {
        x += otherX
        y += otherY
        return this
    }

    fun subtract(other: Vector2): Vector2 {
        x -= other.x
        y -= other.y
        return this
    }

    fun multiply(magnitude: Float): Vector2 {
        x *= magnitude
        y *= magnitude
        return this
    }

    fun multiply(other: Vector2): Vector2 {
        x *= other.x
        y *= other.y
        return this
    }

    fun divide(magnitude: Float): Vector2 {
        if (magnitude != 0.0f) {
            x /= magnitude
            y /= magnitude
        }
        return this
    }

    fun set(other: Vector2): Vector2 {
        x = other.x
        y = other.y
        return this
    }

    fun set(xValue: Float, yValue: Float): Vector2 {
        x = xValue
        y = yValue
        return this
    }

    fun scale(xValue: Float, yValue: Float): Vector2 {
        x *= xValue
        y *= yValue
        return this
    }

    fun scale(scale: Vector2): Vector2 {
        x *= scale.x
        y *= scale.y
        return this
    }

    fun dot(other: Vector2): Float {
        return (x * other.x) + (y * other.y)
    }

    fun length(): Float {
        return sqrt(length2().toDouble()).toFloat()
    }

    fun length2(): Float {
        return (x * x) + (y * y)
    }

    fun distance2(other: Vector2): Float {
        val dx = x - other.x
        val dy = y - other.y
        return (dx * dx) + (dy * dy)
    }

    fun normalize(): Vector2 {
        val magnitude = length()

        // TODO: I'm choosing safety over speed here.
        if (magnitude != 0.0f) {
            x /= magnitude
            y /= magnitude
        }

        return this
    }

    fun zero(): Vector2 {
        set(0.0f, 0.0f)
        return this
    }

    fun toFloat3(): FloatArray? {
        return floatArrayOf(x, y)
    }

    fun toFloat4(): FloatArray? {
        return floatArrayOf(x, y, 1f)
    }

    fun toFloat4(w: Float): FloatArray? {
        return floatArrayOf(x, y, w)
    }

    override fun toString(): String {
        return String.format("Vector2(%f,%f)", x, y)
    }

    companion object {
        val zero: Vector2 = Vector2(0f, 0f)
        val up: Vector2 = Vector2(0f, 1f)
        val down: Vector2 = Vector2(0f, -1f)
        val left: Vector2 = Vector2(-1f, 0f)
        val right: Vector2 = Vector2(1f, 0f)
    }
}
