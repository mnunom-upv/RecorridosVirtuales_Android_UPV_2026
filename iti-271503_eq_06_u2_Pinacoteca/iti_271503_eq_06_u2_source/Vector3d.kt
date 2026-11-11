package upv.pm.may_ago_26.iti_271503.pgu2_eq06

import kotlin.math.abs
import kotlin.math.sqrt

class Vector3d {
    var x: Double = 0.0
    var y: Double = 0.0
    var z: Double = 0.0

    constructor() : super()

    constructor(xx: Double, yy: Double, zz: Double) : super() {
        this.set(xx, yy, zz)
    }

    fun set(xx: Double, yy: Double, zz: Double) {
        this.x = xx
        this.y = yy
        this.z = zz
    }

    fun setComponent(i: Int, `val`: Double) {
        if (i == 0) {
            this.x = `val`
        } else if (i == 1) {
            this.y = `val`
        } else {
            this.z = `val`
        }
    }

    fun setZero() {
        val x = 0.0
        this.z = x
        this.y = x
        this.x = x
    }

    fun set(other: Vector3d) {
        this.x = other.x
        this.y = other.y
        this.z = other.z
    }

    fun scale(s: Double) {
        this.x *= s
        this.y *= s
        this.z *= s
    }

    fun normalize() {
        val d = this.length()
        if (d != 0.0) {
            this.scale(1.0 / d)
        }
    }

    fun length(): Double {
        return sqrt(this.x * this.x + this.y * this.y + this.z * this.z)
    }

    fun sameValues(other: Vector3d): Boolean {
        return this.x == other.x && this.y == other.y && this.z == other.z
    }

    override fun toString(): String {
        val builder = StringBuilder().append("{ ")
        builder.append(this.x.toString())
        builder.append(", ")
        builder.append(this.y.toString())
        builder.append(", ")
        builder.append(this.z.toString())
        builder.append(" }")
        return builder.toString()
    }

    companion object {
        fun dot(a: Vector3d, b: Vector3d): Double {
            return a.x * b.x + a.y * b.y + a.z * b.z
        }

        fun add(a: Vector3d, b: Vector3d, result: Vector3d) {
            result.set(a.x + b.x, a.y + b.y, a.z + b.z)
        }

        fun sub(a: Vector3d, b: Vector3d, result: Vector3d) {
            result.set(a.x - b.x, a.y - b.y, a.z - b.z)
        }

        fun cross(a: Vector3d, b: Vector3d, result: Vector3d) {
            result.set(a.y * b.z - a.z * b.y, a.z * b.x - a.x * b.z, a.x * b.y - a.y * b.x)
        }

        fun ortho(v: Vector3d, result: Vector3d) {
            var k: Int = largestAbsComponent(v) - 1
            if (k < 0) {
                k = 2
            }
            result.setZero()
            result.setComponent(k, 1.0)
            cross(v, result, result)
            result.normalize()
        }

        fun largestAbsComponent(v: Vector3d): Int {
            val xAbs = abs(v.x)
            val yAbs = abs(v.y)
            val zAbs = abs(v.z)
            if (xAbs > yAbs) {
                if (xAbs > zAbs) {
                    return 0
                }
                return 2
            } else {
                if (yAbs > zAbs) {
                    return 1
                }
                return 2
            }
        }
    }
}
