package upv.pm.may_ago_26.iti_271503.pgu2_eq04

import android.opengl.GLES20
import android.opengl.Matrix
import java.nio.FloatBuffer
import java.nio.ShortBuffer

//import com.cardbookvr.renderbox.R;
//import com.cardbookvr.renderbox.RenderBox;
//import com.cardbookvr.renderbox.components.RenderObject;
/**
 * Created by Schoen and Jonathan on 4/21/2016.
 */
class UnlitTexMaterial(resourceId: Int) : Material() {
    var texture: Int

    var vertexBuffer: FloatBuffer? = null
    var texCoordBuffer: FloatBuffer? = null
    var indexBuffer: ShortBuffer? = null
    var numIndices: Int = 0

    init {
        setupProgram()
        this.texture = RenderBox.Companion.loadTexture(resourceId)
    }

    fun setBuffers(
        vertexBuffer: FloatBuffer?, texCoordBuffer: FloatBuffer?,
        indexBuffer: ShortBuffer?, numIndices: Int
    ) {
        //Associate VBO data with this instance of the material
        this.vertexBuffer = vertexBuffer
        this.texCoordBuffer = texCoordBuffer
        this.indexBuffer = indexBuffer
        this.numIndices = numIndices
    }

    override fun draw(view: FloatArray?, perspective: FloatArray?) {
        GLES20.glUseProgram(program)

        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)

        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, this.texture)

        // Tell the texture uniform sampler to use this texture in
        // the shader by binding to texture unit 0.
        GLES20.glUniform1i(textureParam, 0)

        Matrix.multiplyMM(Material.Companion.modelView, 0, view, 0, RenderObject.Companion.model, 0)
        Matrix.multiplyMM(
            Material.Companion.modelViewProjection,
            0,
            perspective,
            0,
            Material.Companion.modelView,
            0
        )

        // Set the ModelViewProjection matrix in the shader.
        GLES20.glUniformMatrix4fv(MVPParam, 1, false, Material.Companion.modelViewProjection, 0)

        // Set the vertex attributes
        GLES20.glVertexAttribPointer(positionParam, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)
        GLES20.glVertexAttribPointer(texCoordParam, 2, GLES20.GL_FLOAT, false, 0, texCoordBuffer)

        GLES20.glDrawElements(
            GLES20.GL_TRIANGLES,
            numIndices,
            GLES20.GL_UNSIGNED_SHORT,
            indexBuffer
        )

        RenderBox.Companion.checkGLError("Unlit Texture draw")
    }

    companion object {
        private const val TAG = "unlittex"

        var program: Int = -1

        //Initialize to a totally invalid value for setup state
        var positionParam: Int = 0
        var texCoordParam: Int = 0
        var textureParam: Int = 0
        var MVPParam: Int = 0

        fun setupProgram() {
            if (program > -1)  //This means program has been set up (valid program or error)
                return
            //Create shader program
            program =
                Material.Companion.createProgram(R.raw.unlit_tex_vertex, R.raw.unlit_tex_fragment)

            //Get vertex attribute parameters
            positionParam = GLES20.glGetAttribLocation(program, "a_Position")
            texCoordParam = GLES20.glGetAttribLocation(program, "a_TexCoordinate")

            //Enable them (turns out this is kind of a big deal ;)
            GLES20.glEnableVertexAttribArray(positionParam)
            GLES20.glEnableVertexAttribArray(texCoordParam)

            //Shader-specific parameters
            textureParam = GLES20.glGetUniformLocation(program, "u_Texture")
            MVPParam = GLES20.glGetUniformLocation(program, "u_MVP")

            RenderBox.Companion.checkGLError("Unlit Texture params")
        }
    }
}
