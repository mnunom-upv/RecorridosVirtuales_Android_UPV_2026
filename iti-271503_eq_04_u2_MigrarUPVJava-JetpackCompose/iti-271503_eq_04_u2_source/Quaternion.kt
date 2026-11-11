package upv.pm.may_ago_26.iti_271503.pgu2_eq04

import kotlin.math.cos
import kotlin.math.sin

/**
 * Created by mtsch on 11/16/2015.
 * Somewhat sourced from https://github.com/libgdx/libgdx/blob/master/gdx/src/com/badlogic/gdx/math/Quaternion.java
 */
class Quaternion {
    var x: Float = 0f
    var y: Float = 0f
    var z: Float = 0f
    var w: Float

    constructor() {
        w = 1f
    }

    constructor(quat: Quaternion) {
        x = quat.x
        y = quat.y
        z = quat.z
        w = quat.w
    }

    fun setEulerAngles(pitch: Float, yaw: Float, roll: Float): Quaternion {
        return setEulerAnglesRad(
            pitch * MathUtils.degreesToRadians,
            yaw * MathUtils.degreesToRadians,
            roll * MathUtils.degreesToRadians
        )
    }

    fun setEulerAnglesRad(pitch: Float, yaw: Float, roll: Float): Quaternion {
        val hr = roll * 0.5f
        val shr = sin(hr.toDouble()).toFloat()
        val chr = cos(hr.toDouble()).toFloat()
        val hp = pitch * 0.5f
        val shp = sin(hp.toDouble()).toFloat()
        val chp = cos(hp.toDouble()).toFloat()
        val hy = yaw * 0.5f
        val shy = sin(hy.toDouble()).toFloat()
        val chy = cos(hy.toDouble()).toFloat()
        val chy_shp = chy * shp
        val shy_chp = shy * chp
        val chy_chp = chy * chp
        val shy_shp = shy * shp

        x =
            (chy_shp * chr) + (shy_chp * shr) // cos(yaw/2) * sin(pitch/2) * cos(roll/2) + sin(yaw/2) * cos(pitch/2) * sin(roll/2)
        y =
            (shy_chp * chr) - (chy_shp * shr) // sin(yaw/2) * cos(pitch/2) * cos(roll/2) - cos(yaw/2) * sin(pitch/2) * sin(roll/2)
        z =
            (chy_chp * shr) - (shy_shp * chr) // cos(yaw/2) * cos(pitch/2) * sin(roll/2) - sin(yaw/2) * sin(pitch/2) * cos(roll/2)
        w =
            (chy_chp * chr) + (shy_shp * shr) // cos(yaw/2) * cos(pitch/2) * cos(roll/2) + sin(yaw/2) * sin(pitch/2) * sin(roll/2)

        return this
    }

    fun multiply(other: Quaternion): Quaternion {
        val newX = this.w * other.x + this.x * other.w + this.y * other.z - this.z * other.y
        val newY = this.w * other.y + this.y * other.w + this.z * other.x - this.x * other.z
        val newZ = this.w * other.z + this.z * other.w + this.x * other.y - this.y * other.x
        val newW = this.w * other.w - this.x * other.x - this.y * other.y - this.z * other.z
        this.x = newX
        this.y = newY
        this.z = newZ
        this.w = newW
        return this
    }

    fun conjugate(): Quaternion {
        x = -x
        y = -y
        z = -z
        return this
    }

    fun toMatrix4(): Matrix4 {
        val result = Matrix4()
        val matrix = result.`val`
        val xx = x * x
        val xy = x * y
        val xz = x * z
        val xw = x * w
        val yy = y * y
        val yz = y * z
        val yw = y * w
        val zz = z * z
        val zw = z * w
        // Set matrix from quaternion
        matrix[Matrix4.Companion.M00] = 1 - 2 * (yy + zz)
        matrix[Matrix4.Companion.M01] = 2 * (xy - zw)
        matrix[Matrix4.Companion.M02] = 2 * (xz + yw)
        matrix[Matrix4.Companion.M03] = 0f
        matrix[Matrix4.Companion.M10] = 2 * (xy + zw)
        matrix[Matrix4.Companion.M11] = 1 - 2 * (xx + zz)
        matrix[Matrix4.Companion.M12] = 2 * (yz - xw)
        matrix[Matrix4.Companion.M13] = 0f
        matrix[Matrix4.Companion.M20] = 2 * (xz - yw)
        matrix[Matrix4.Companion.M21] = 2 * (yz + xw)
        matrix[Matrix4.Companion.M22] = 1 - 2 * (xx + yy)
        matrix[Matrix4.Companion.M23] = 0f
        matrix[Matrix4.Companion.M30] = 0f
        matrix[Matrix4.Companion.M31] = 0f
        matrix[Matrix4.Companion.M32] = 0f
        matrix[Matrix4.Companion.M33] = 1f
        return result
    }

    override fun toString(): String {
        return String.format("Quaternion(%.5f, %.5f, %.5f, %.5f)", x, y, z, w)
    }

    companion object {
        private const val TAG = "RenderBox.Quaternion"
    }
}