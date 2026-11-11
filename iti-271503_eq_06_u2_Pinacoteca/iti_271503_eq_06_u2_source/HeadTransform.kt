package upv.pm.may_ago_26.iti_271503.pgu2_eq06

import android.opengl.Matrix
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.sqrt

class HeadTransform {
    val headView: FloatArray

    init {
        Matrix.setIdentityM(FloatArray(16).also { this.headView = it }, 0)
    }

    fun getHeadView(headView: FloatArray, offset: Int) {
        require(offset + 16 <= headView.size) { "Not enough space to write the result" }
        System.arraycopy(this.headView, 0, headView, offset, 16)
    }

    fun getForwardVector(forward: FloatArray, offset: Int) {
        require(offset + 3 <= forward.size) { "Not enough space to write the result" }
        for (i in 0..2) {
            forward[i + offset] = -this.headView[8 + i]
        }
    }

    fun getUpVector(up: FloatArray, offset: Int) {
        require(offset + 3 <= up.size) { "Not enough space to write the result" }
        for (i in 0..2) {
            up[i + offset] = this.headView[4 + i]
        }
    }

    fun getRightVector(right: FloatArray, offset: Int) {
        require(offset + 3 <= right.size) { "Not enough space to write the result" }
        for (i in 0..2) {
            right[i + offset] = this.headView[i]
        }
    }

    fun getQuaternion(quaternion: FloatArray, offset: Int) {
        require(offset + 4 <= quaternion.size) { "Not enough space to write the result" }
        val m = this.headView
        val t = m[0] + m[5] + m[10]
        val w: Float
        val x: Float
        val y: Float
        val z: Float
        if (t >= 0.0f) {
            //float s = FloatMath.sqrt(t + 1.0f);
            var s = sqrt((t + 1.0f).toDouble()).toFloat()
            w = 0.5f * s
            s = 0.5f / s
            x = (m[9] - m[6]) * s
            y = (m[2] - m[8]) * s
            z = (m[4] - m[1]) * s
        } else if (m[0] > m[5] && m[0] > m[10]) {
            var s = sqrt((1.0f + m[0] - m[5] - m[10]).toDouble()).toFloat()
            x = s * 0.5f
            s = 0.5f / s
            y = (m[4] + m[1]) * s
            z = (m[2] + m[8]) * s
            w = (m[9] - m[6]) * s
        } else if (m[5] > m[10]) {
            var s = sqrt((1.0f + m[5] - m[0] - m[10]).toDouble()).toFloat()
            y = s * 0.5f
            s = 0.5f / s
            x = (m[4] + m[1]) * s
            z = (m[9] + m[6]) * s
            w = (m[2] - m[8]) * s
        } else {
            var s = sqrt((1.0f + m[10] - m[0] - m[5]).toDouble()).toFloat()
            z = s * 0.5f
            s = 0.5f / s
            x = (m[2] + m[8]) * s
            y = (m[9] + m[6]) * s
            w = (m[4] - m[1]) * s
        }
        quaternion[offset + 0] = x
        quaternion[offset + 1] = y
        quaternion[offset + 2] = z
        quaternion[offset + 3] = w
    }

    fun getEulerAngles(eulerAngles: FloatArray, offset: Int) {
        require(offset + 3 <= eulerAngles.size) { "Not enough space to write the result" }
        val pitch = asin(this.headView[6].toDouble()).toFloat()
        val yaw: Float
        val roll: Float
        if (sqrt((1.0f - this.headView[6] * this.headView[6]).toDouble()).toFloat() >= GIMBAL_LOCK_EPSILON) {
            yaw = atan2(-this.headView[2].toDouble(), this.headView[10].toDouble()).toFloat()
            roll = atan2(-this.headView[4].toDouble(), this.headView[5].toDouble()).toFloat()
        } else {
            yaw = 0.0f
            roll = atan2(this.headView[1].toDouble(), this.headView[0].toDouble()).toFloat()
        }
        eulerAngles[offset + 0] = -pitch
        eulerAngles[offset + 1] = -yaw
        eulerAngles[offset + 2] = -roll
    }

    companion object {
        private const val GIMBAL_LOCK_EPSILON = 0.01f
    }
}
