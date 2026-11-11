package upv.pm.may_ago_26.iti_271503.pgu2_eq04

import android.opengl.GLES20
import android.opengl.Matrix
import java.nio.FloatBuffer
import java.nio.ShortBuffer

/*import com.cardbookvr.renderbox.R;
import com.cardbookvr.renderbox.RenderBox;
import com.cardbookvr.renderbox.components.RenderObject;*/ /**
 * Created by Schoen and Jonathan on 4/21/2016.
 */
class DayNightMaterial(resourceId: Int, nightResourceId: Int) : Material() {
    var textureId: Int
    var nightTextureId: Int

    var vertexBuffer: FloatBuffer? = null
    var texCoordBuffer: FloatBuffer? = null
    var normalBuffer: FloatBuffer? = null
    var indexBuffer: ShortBuffer? = null
    var numIndices: Int = 0

    init {
        setupProgram()
        this.textureId = RenderBox.Companion.loadTexture(resourceId)

        this.nightTextureId = RenderBox.Companion.loadTexture(nightResourceId)
    }

    fun setBuffers(
        vertexBuffer: FloatBuffer?,
        normalBuffer: FloatBuffer?,
        texCoordBuffer: FloatBuffer?,
        indexBuffer: ShortBuffer?,
        numIndices: Int
    ) {
        //Associate VBO data with this instance of the material
        this.vertexBuffer = vertexBuffer
        this.normalBuffer = normalBuffer
        this.texCoordBuffer = texCoordBuffer
        this.indexBuffer = indexBuffer
        this.numIndices = numIndices
    }

    override fun draw(view: FloatArray?, perspective: FloatArray?) {
        GLES20.glUseProgram(program)

        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)

        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, nightTextureId)

        // Tell the texture uniform sampler to use this texture in
        // the shader by binding to texture unit 0.
        GLES20.glUniform1i(textureParam, 0)
        GLES20.glUniform1i(nightTextureParam, 1)

        //Technically, we don't need to do this with every draw
        //call, but the light could move.
        //We could also add a step for shader-global parameters
        //which don't vary per-object
        GLES20.glUniform3fv(
            lightPosParam,
            1,
            RenderBox.Companion.instance.mainLight!!.lightPosInEyeSpace,
            0
        )
        GLES20.glUniform4fv(lightColParam, 1, RenderBox.Companion.instance.mainLight!!.color, 0)

        Matrix.multiplyMM(
            Material.Companion.modelView,
            0,
            view,
            0,
            RenderObject.Companion.lightingModel,
            0
        )
        // Set the ModelView in the shader, used to calculate
        // lighting
        GLES20.glUniformMatrix4fv(MVParam, 1, false, Material.Companion.modelView, 0)
        Matrix.multiplyMM(Material.Companion.modelView, 0, view, 0, RenderObject.Companion.model, 0)
        Matrix.multiplyMM(
            Material.Companion.modelViewProjection,
            0,
            perspective,
            0,
            Material.Companion.modelView,
            0
        )
        // Set the ModelViewProjection matrix for eye position.
        GLES20.glUniformMatrix4fv(MVPParam, 1, false, Material.Companion.modelViewProjection, 0)

        //Set vertex attributes
        GLES20.glVertexAttribPointer(positionParam, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)
        GLES20.glVertexAttribPointer(normalParam, 3, GLES20.GL_FLOAT, false, 0, normalBuffer)
        GLES20.glVertexAttribPointer(texCoordParam, 2, GLES20.GL_FLOAT, false, 0, texCoordBuffer)

        GLES20.glDrawElements(
            GLES20.GL_TRIANGLES,
            numIndices,
            GLES20.GL_UNSIGNED_SHORT,
            indexBuffer
        )

        RenderBox.Companion.checkGLError("DayNight Texture Color Lighting draw")
    }

    companion object {
        private const val TAG = "daynightmaterial"

        var program: Int = -1

        //Initialize to a totally invalid value for setup state
        var positionParam: Int = 0
        var texCoordParam: Int = 0
        var textureParam: Int = 0
        var nightTextureParam: Int = 0
        var normalParam: Int = 0
        var MVParam: Int = 0
        var MVPParam: Int = 0
        var lightPosParam: Int = 0
        var lightColParam: Int = 0

        fun setupProgram() {
            if (program != -1) return
            //Create shader program
            program =
                Material.Companion.createProgram(R.raw.day_night_vertex, R.raw.day_night_fragment)

            //Get vertex attribute parameters
            positionParam = GLES20.glGetAttribLocation(program, "a_Position")
            normalParam = GLES20.glGetAttribLocation(program, "a_Normal")
            texCoordParam = GLES20.glGetAttribLocation(program, "a_TexCoordinate")

            //Enable them (turns out this is kind of a big deal ;)
            GLES20.glEnableVertexAttribArray(positionParam)
            GLES20.glEnableVertexAttribArray(normalParam)
            GLES20.glEnableVertexAttribArray(texCoordParam)

            //Shader-specific parameters
            textureParam = GLES20.glGetUniformLocation(program, "u_Texture")
            nightTextureParam = GLES20.glGetUniformLocation(program, "u_NightTexture")
            MVParam = GLES20.glGetUniformLocation(program, "u_MV")
            MVPParam = GLES20.glGetUniformLocation(program, "u_MVP")
            lightPosParam = GLES20.glGetUniformLocation(program, "u_LightPos")
            lightColParam = GLES20.glGetUniformLocation(program, "u_LightCol")

            RenderBox.Companion.checkGLError("Day/Night params")
        }
    }
}

