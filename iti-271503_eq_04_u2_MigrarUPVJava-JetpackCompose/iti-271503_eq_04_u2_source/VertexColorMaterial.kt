package upv.pm.may_ago_26.iti_271503.pgu2_eq04

import android.opengl.GLES20
import android.opengl.Matrix
import java.nio.FloatBuffer

/*import com.cardbookvr.renderbox.R;
import com.cardbookvr.renderbox.RenderBox;
import com.cardbookvr.renderbox.components.RenderObject;*/ /**
 * Created by Schoen and Jonathan on 4/16/2016.
 */
class VertexColorMaterial : Material() {
    var vertexBuffer: FloatBuffer? = null
    var colorBuffer: FloatBuffer? = null
    var numIndices: Int = 0

    init {
        setupProgram()
    }

    fun setBuffers(vertexBuffer: FloatBuffer?, colorBuffer: FloatBuffer?, numIndices: Int) {
        this.vertexBuffer = vertexBuffer
        this.colorBuffer = colorBuffer
        this.numIndices = numIndices
    }

    override fun draw(view: FloatArray?, perspective: FloatArray?) {
        Matrix.multiplyMM(Material.Companion.modelView, 0, view, 0, RenderObject.Companion.model, 0)
        Matrix.multiplyMM(
            Material.Companion.modelViewProjection,
            0,
            perspective,
            0,
            Material.Companion.modelView,
            0
        )

        GLES20.glUseProgram(program)

        // Set the Model in the shader, used to calculate lighting
        GLES20.glUniformMatrix4fv(modelParam, 1, false, RenderObject.Companion.model, 0)

        // Set the position of the cube
        GLES20.glVertexAttribPointer(positionParam, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)

        // Set the ModelViewProjection matrix in the shader.
        GLES20.glUniformMatrix4fv(MVPParam, 1, false, Material.Companion.modelViewProjection, 0)

        // Set the normal positions of the cube, again for shading
        GLES20.glVertexAttribPointer(colorParam, 4, GLES20.GL_FLOAT, false, 0, colorBuffer)

        // Set the ModelViewProjection matrix in the shader.
        GLES20.glUniformMatrix4fv(MVPParam, 1, false, Material.Companion.modelViewProjection, 0)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, numIndices)
    }

    companion object {
        var program: Int = -1

        var positionParam: Int = 0
        var colorParam: Int = 0
        var modelParam: Int = 0
        var MVPParam: Int = 0

        fun setupProgram() {
            if (program != -1) return

            //Create shader program
            program = Material.Companion.createProgram(
                R.raw.vertex_color_vertex,
                R.raw.vertex_color_fragment
            )

            //Get vertex attribute parameters
            positionParam = GLES20.glGetAttribLocation(program, "a_Position")
            colorParam = GLES20.glGetAttribLocation(program, "a_Color")

            //Enable vertex attribute parameters
            GLES20.glEnableVertexAttribArray(positionParam)
            GLES20.glEnableVertexAttribArray(colorParam)

            //Shader-specific paramteters
            modelParam = GLES20.glGetUniformLocation(program, "u_Model")
            MVPParam = GLES20.glGetUniformLocation(program, "u_MVP")

            RenderBox.Companion.checkGLError("Solid Color Lighting params")
        }

        fun destroy() {
            program = -1
        }
    }
}
