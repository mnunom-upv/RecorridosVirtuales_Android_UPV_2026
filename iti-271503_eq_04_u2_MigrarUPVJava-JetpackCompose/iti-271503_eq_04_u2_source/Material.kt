package upv.pm.may_ago_26.iti_271503.pgu2_eq04

import android.opengl.GLES20
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

//import com.cardbookvr.renderbox.RenderBox;
/**
 * Created by Schoen and Jonathan on 4/16/2016.
 */
abstract class Material {
    abstract fun draw(view: FloatArray?, perspective: FloatArray?)

    companion object {
        private const val TAG = "RenderBox.Material"

        val modelView: FloatArray = FloatArray(16)
        val modelViewProjection: FloatArray = FloatArray(16)

        fun createProgram(vertexShaderResource: Int, fragmentShaderResource: Int): Int {
            val vertexShader: Int = loadGLShader(GLES20.GL_VERTEX_SHADER, vertexShaderResource)
            val passthroughShader: Int =
                loadGLShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderResource)

            val program = GLES20.glCreateProgram()
            GLES20.glAttachShader(program, vertexShader)
            GLES20.glAttachShader(program, passthroughShader)
            GLES20.glLinkProgram(program)
            GLES20.glUseProgram(program)

            RenderBox.Companion.checkGLError("Material.createProgram")
            return program
        }

        /**
         * Converts a raw text file, saved as a resource, into an OpenGL ES shader.
         * 
         * @param type The type of shader we will be creating.
         * @param resId The resource ID of the raw text file about to be turned into a shader.
         * @return The shader object handler.
         */
        fun loadGLShader(type: Int, resId: Int): Int {
            val code: String? = readRawTextFile(resId)
            var shader = GLES20.glCreateShader(type)
            GLES20.glShaderSource(shader, code)
            GLES20.glCompileShader(shader)

            // Get the compilation status.
            val compileStatus = IntArray(1)
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0)

            // If the compilation failed, delete the shader.
            if (compileStatus[0] == 0) {
                Log.e(TAG, "Error compiling shader: " + GLES20.glGetShaderInfoLog(shader))
                GLES20.glDeleteShader(shader)
                shader = 0
            }

            if (shader == 0) {
                throw RuntimeException("Error creating shader.")
            }

            return shader
        }

        /**
         * Converts a raw text file into a string.
         * 
         * @param resId The resource ID of the raw text file about to be turned into a shader.
         * @return The context of the text file, or null in case of error.
         */
        private fun readRawTextFile(resId: Int): String? {
            val inputStream: InputStream =
                RenderBox.Companion.instance.mainActivity.getResources().openRawResource(resId)
            try {
                val reader = BufferedReader(InputStreamReader(inputStream))
                val sb = StringBuilder()
                var line: String?
                while ((reader.readLine().also { line = it }) != null) {
                    sb.append(line).append("\n")
                }
                reader.close()
                return sb.toString()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }
    }
}
