package upv.pm.may_ago_26.iti_271503.pgu2_eq04

import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import kotlin.math.abs
import kotlin.math.atan2

abstract class RenderObject : Component() {

    @JvmField
    protected var material: Material? = null

    var isLooking: Boolean = false

    val modelView: FloatArray = FloatArray(16)

    init {
        RenderBox.instance.renderObjects.add(this)
    }

    fun getMaterial(): Material {
        return material!!
    }

    fun setMaterial(material: Material): RenderObject {
        this.material = material
        return this
    }

    fun draw(view: FloatArray?, perspective: FloatArray?) {
        if (!enabled) {
            return
        }

        transform!!.drawMatrices()
        material!!.draw(view, perspective)
        isLooking = isLookingAtObject
    }

    private val isLookingAtObject: Boolean
        get() {
            val initVec = floatArrayOf(0f, 0f, 0f, 1.0f)
            val objPositionVec = FloatArray(4)

            Matrix.multiplyMM(
                modelView,
                0,
                RenderBox.headView,
                0,
                model,
                0
            )

            Matrix.multiplyMV(
                objPositionVec,
                0,
                modelView,
                0,
                initVec,
                0
            )

            val pitch = atan2(
                objPositionVec[1].toDouble(),
                -objPositionVec[2].toDouble()
            ).toFloat()

            val yaw = atan2(
                objPositionVec[0].toDouble(),
                -objPositionVec[2].toDouble()
            ).toFloat()

            return abs(pitch) < PITCH_LIMIT && abs(yaw) < YAW_LIMIT
        }

    companion object {

        private const val TAG = "RenderObject"

        @JvmStatic
        var model: FloatArray = FloatArray(16)

        @JvmStatic
        var lightingModel: FloatArray = FloatArray(16)

        private const val YAW_LIMIT = 0.15f
        private const val PITCH_LIMIT = 0.15f

        @JvmStatic
        fun allocateFloatBuffer(data: FloatArray): FloatBuffer {
            val byteBuffer = ByteBuffer.allocateDirect(data.size * 4)
            byteBuffer.order(ByteOrder.nativeOrder())

            val buffer = byteBuffer.asFloatBuffer()
            buffer.put(data)
            buffer.position(0)

            return buffer
        }

        @JvmStatic
        fun allocateShortBuffer(data: ShortArray): ShortBuffer {
            val byteBuffer = ByteBuffer.allocateDirect(data.size * 2)
            byteBuffer.order(ByteOrder.nativeOrder())

            val buffer = byteBuffer.asShortBuffer()
            buffer.put(data)
            buffer.position(0)

            return buffer
        }
    }
}