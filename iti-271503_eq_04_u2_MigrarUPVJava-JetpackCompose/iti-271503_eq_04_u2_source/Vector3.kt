package upv.pm.may_ago_26.iti_271503.pgu2_eq04

import kotlin.math.sqrt

/**
 * Created by mtsch on 11/16/2015.
 * Simple 3D vector class.  Handles basic vector math for 3D vectors.
 * Somewhat sourced from https://github.com/libgdx/libgdx/blob/master/gdx/src/com/badlogic/gdx/math/Vector3.java
 */
//TODO: migrate to class with final values, more like a struct/like Unity
class Vector3 {
    var x: Float = 0f
    var y: Float = 0f
    var z: Float = 0f

    constructor()

    constructor(xValue: Float, yValue: Float, zValue: Float) {
        set(xValue, yValue, zValue)
    }

    constructor(other: Vector3) {
        set(other)
    }

    constructor(vec: FloatArray) {
        //Assume array has at least 3 elements--speed optimization
        x = vec[0]
        y = vec[1]
        z = vec[2]
    }

    fun add(other: Vector3): Vector3 {
        x += other.x
        y += other.y
        z += other.z
        return this
    }

    fun add(otherX: Float, otherY: Float, otherZ: Float): Vector3 {
        x += otherX
        y += otherY
        z += otherZ
        return this
    }

    fun subtract(other: Vector3): Vector3 {
        x -= other.x
        y -= other.y
        z -= other.z
        return this
    }

    fun multiply(magnitude: Float): Vector3 {
        x *= magnitude
        y *= magnitude
        z *= magnitude
        return this
    }

    fun multiply(other: Vector3): Vector3 {
        x *= other.x
        y *= other.y
        z *= other.z
        return this
    }

    fun divide(magnitude: Float): Vector3 {
        if (magnitude != 0.0f) {
            x /= magnitude
            y /= magnitude
            z /= magnitude
        }
        return this
    }

    fun set(other: Vector3): Vector3 {
        x = other.x
        y = other.y
        z = other.z
        return this
    }

    fun set(xValue: Float, yValue: Float, zValue: Float): Vector3 {
        x = xValue
        y = yValue
        z = zValue
        return this
    }

    fun scale(xValue: Float, yValue: Float, zValue: Float): Vector3 {
        x *= xValue
        y *= yValue
        z *= zValue
        return this
    }

    fun scale(scale: Vector3): Vector3 {
        x *= scale.x
        y *= scale.y
        z *= scale.z
        return this
    }

    fun dot(other: Vector3): Float {
        return (x * other.x) + (y * other.y) + (z * other.z)
    }

    fun length(): Float {
        return sqrt(length2().toDouble()).toFloat()
    }

    fun length2(): Float {
        return (x * x) + (y * y) + (z * z)
    }

    fun distance2(other: Vector3): Float {
        val dx = x - other.x
        val dy = y - other.y
        val dz = z - other.z
        return (dx * dx) + (dy * dy) + (dz * dz)
    }

    fun normalize(): Vector3 {
        val magnitude = length()

        // TODO: I'm choosing safety over speed here.
        if (magnitude != 0.0f) {
            x /= magnitude
            y /= magnitude
            z /= magnitude
        }

        return this
    }

    fun zero(): Vector3 {
        set(0.0f, 0.0f, 0.0f)
        return this
    }

    fun toFloat3(): FloatArray {
        return floatArrayOf(x, y, z)
    }

    fun toFloat4(): FloatArray {
        return floatArrayOf(x, y, z, 1f)
    }

    fun toFloat4(w: Float): FloatArray {
        return floatArrayOf(x, y, z, w)
    }

    override fun toString(): String {
        return String.format("Vector3(%f,%f,%f)", x, y, z)
    }

    companion object {
        val zero: Vector3 = Vector3(0f, 0f, 0f)
        val up: Vector3 = Vector3(0f, 1f, 0f)
        val down: Vector3 = Vector3(0f, -1f, 0f)
        val left: Vector3 = Vector3(-1f, 0f, 0f)
        val right: Vector3 = Vector3(1f, 0f, 0f)
        val forward: Vector3 = Vector3(0f, 0f, 1f)
        val backward: Vector3 = Vector3(0f, 0f, -1f)
    }
}