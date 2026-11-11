package upv.pm.may_ago_26.iti_271503.pgu2_eq04

import android.opengl.GLES20
import android.opengl.Matrix
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class SolidColorLightingMaterial(c: FloatArray?) : Material() {

    @JvmField
    var color: FloatArray? = FloatArray(4)

    var vertexBuffer: FloatBuffer? = null
    var normalBuffer: FloatBuffer? = null
    var indexBuffer: ShortBuffer? = null
    var numIndices: Int = 0

    init {
        setColor(c)
        setupProgram()
    }

    fun setColor(c: FloatArray?) {
        color = c ?: floatArrayOf(1f, 1f, 1f, 1f)
    }

    fun setBuffers(
        vertexBuffer: FloatBuffer?,
        normalBuffer: FloatBuffer?,
        indexBuffer: ShortBuffer?,
        numIndices: Int
    ) {
        this.vertexBuffer = vertexBuffer
        this.normalBuffer = normalBuffer
        this.indexBuffer = indexBuffer
        this.numIndices = numIndices
    }

    override fun draw(view: FloatArray?, perspective: FloatArray?) {
        if (view == null || perspective == null) {
            return
        }

        if (vertexBuffer == null || normalBuffer == null || indexBuffer == null) {
            return
        }

        GLES20.glUseProgram(program)

        GLES20.glUniform3fv(
            lightPosParam,
            1,
            RenderBox.instance.mainLight!!.lightPosInEyeSpace,
            0
        )

        GLES20.glUniform4fv(
            lightColParam,
            1,
            RenderBox.instance.mainLight!!.color,
            0
        )

        Matrix.multiplyMM(
            Material.modelView,
            0,
            view,
            0,
            RenderObject.lightingModel,
            0
        )

        GLES20.glUniformMatrix4fv(
            MVParam,
            1,
            false,
            Material.modelView,
            0
        )

        Matrix.multiplyMM(
            Material.modelView,
            0,
            view,
            0,
            RenderObject.model,
            0
        )

        Matrix.multiplyMM(
            Material.modelViewProjection,
            0,
            perspective,
            0,
            Material.modelView,
            0
        )

        GLES20.glUniformMatrix4fv(
            MVPParam,
            1,
            false,
            Material.modelViewProjection,
            0
        )

        GLES20.glUniform4fv(
            colorParam,
            1,
            color,
            0
        )

        GLES20.glVertexAttribPointer(
            positionParam,
            3,
            GLES20.GL_FLOAT,
            false,
            0,
            vertexBuffer
        )

        GLES20.glVertexAttribPointer(
            normalParam,
            3,
            GLES20.GL_FLOAT,
            false,
            0,
            normalBuffer
        )

        GLES20.glDrawElements(
            GLES20.GL_TRIANGLES,
            numIndices,
            GLES20.GL_UNSIGNED_SHORT,
            indexBuffer
        )
    }

    companion object {

        private const val TAG = "solidcolorlighting"

        var program: Int = -1
        var positionParam: Int = 0
        var colorParam: Int = 0
        var normalParam: Int = 0
        var modelParam: Int = 0
        var MVParam: Int = 0
        var MVPParam: Int = 0
        var lightPosParam: Int = 0
        var lightColParam: Int = 0

        fun setupProgram() {
            if (program != -1) {
                return
            }

            program = Material.createProgram(
                R.raw.solid_color_lighting_vertex,
                R.raw.solid_color_lighting_fragment
            )

            positionParam = GLES20.glGetAttribLocation(program, "a_Position")
            normalParam = GLES20.glGetAttribLocation(program, "a_Normal")

            GLES20.glEnableVertexAttribArray(positionParam)
            GLES20.glEnableVertexAttribArray(normalParam)

            colorParam = GLES20.glGetUniformLocation(program, "u_Color")
            MVParam = GLES20.glGetUniformLocation(program, "u_MV")
            MVPParam = GLES20.glGetUniformLocation(program, "u_MVP")
            lightPosParam = GLES20.glGetUniformLocation(program, "u_LightPos")
            lightColParam = GLES20.glGetUniformLocation(program, "u_LightCol")

            RenderBox.checkGLError("Solid Color Lighting params")
        }
    }
}