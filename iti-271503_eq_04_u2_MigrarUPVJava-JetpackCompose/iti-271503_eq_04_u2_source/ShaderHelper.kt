package upv.pm.may_ago_26.iti_271503.pgu2_eq04

import android.opengl.GLES20
import android.util.Log

object ShaderHelper {
    private const val TAG = "ShaderHelper"

    //private static final String TAG = ShaderHelper.class.getSimpleName();
    /**
     * Helper function to compile a shader.
     * 
     * @param shaderType The shader type.
     * @param shaderSource The shader source code.
     * @return An OpenGL handle to the shader.
     */
    fun compileShader(shaderType: Int, shaderSource: String?): Int {
        var shaderHandle = GLES20.glCreateShader(shaderType)

        if (shaderHandle != 0) {
            // Pass in the shader source.
            GLES20.glShaderSource(shaderHandle, shaderSource)

            // Compile the shader.
            GLES20.glCompileShader(shaderHandle)

            // Get the compilation status.
            val compileStatus = IntArray(1)
            GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0)

            // If the compilation failed, delete the shader.
            if (compileStatus[0] == 0) {
                Log.e(TAG, "Error compiling shader: " + GLES20.glGetShaderInfoLog(shaderHandle))
                GLES20.glDeleteShader(shaderHandle)
                shaderHandle = 0
            }
        }

        if (shaderHandle == 0) {
            throw RuntimeException("Error creating shader.")
        }

        return shaderHandle
    }

    /**
     * Helper function to compile and link a program.
     * 
     * @param vertexShaderHandle An OpenGL handle to an already-compiled vertex shader.
     * @param fragmentShaderHandle An OpenGL handle to an already-compiled fragment shader.
     * @param attributes Attributes that need to be bound to the program.
     * @return An OpenGL handle to the program.
     */
    fun createAndLinkProgram(
        vertexShaderHandle: Int,
        fragmentShaderHandle: Int,
        attributes: Array<String?>?
    ): Int {
        var programHandle = GLES20.glCreateProgram()

        if (programHandle != 0) {
            // Bind the vertex shader to the program.
            GLES20.glAttachShader(programHandle, vertexShaderHandle)


            // Bind the fragment shader to the program.
            GLES20.glAttachShader(programHandle, fragmentShaderHandle)


            // Bind attributes
            if (attributes != null) {
                val size = attributes.size
                for (i in 0..<size) {
                    GLES20.glBindAttribLocation(programHandle, i, attributes[i])
                }
            }


            // Link the two shaders together into a program.
            GLES20.glLinkProgram(programHandle)

            // Get the link status.
            val linkStatus = IntArray(1)
            GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0)

            // If the link failed, delete the program.
            if (linkStatus[0] == 0) {
                Log.e(TAG, "Error compiling program: " + GLES20.glGetProgramInfoLog(programHandle))
                GLES20.glDeleteProgram(programHandle)
                programHandle = 0
            }
        }

        if (programHandle == 0) {
            throw RuntimeException("Error creating program.")
        }

        return programHandle
    }

    // -----------------------
    //private static final boolean D = BuildConfig.DEBUG;
    //private static final String TAG = ShaderHelper.class.getSimpleName();
    fun compileVertexShader(shaderCode: String?): Int {
        return compileShader(GLES20.GL_VERTEX_SHADER, shaderCode)
    }

    fun compileFragmentShader(shaderCode: String?): Int {
        return compileShader(GLES20.GL_FRAGMENT_SHADER, shaderCode)
    }

    /*private static int compileShader(int type, String shaderCode) {
		final int shaderObjectId = glCreateShader(type);

		if (shaderObjectId == 0) {
			//if (D) {
			Log.w(TAG, "Could not create new shader.");
			//}
			return 0;
		}

		glShaderSource(shaderObjectId, shaderCode);
		glCompileShader(shaderObjectId);

		final int[] compileStatus = new int[1];
		glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0);
		//if (D) {
		Log.v(TAG, "Results of compiling source:\n"
				+ shaderCode + "\n"
				+ glGetShaderInfoLog(shaderObjectId));
		//}
		if (compileStatus[0] == 0) {
			glDeleteShader(shaderObjectId);
			//if (D) {
			Log.w(TAG, "Compilation of shader failed.");
			//}
			return 0;
		}
		return shaderObjectId;
	}*/
    fun linkProgram(vertexShaderId: Int, fragmentShaderId: Int): Int {
        val programObjectId = GLES20.glCreateProgram()

        if (programObjectId == 0) {
            //if (D) {
            Log.w(TAG, "Could not create new program.")
            //}
            return 0
        }

        GLES20.glAttachShader(programObjectId, vertexShaderId)
        GLES20.glAttachShader(programObjectId, fragmentShaderId)

        GLES20.glLinkProgram(programObjectId)

        val linkStatus = IntArray(1)
        GLES20.glGetProgramiv(programObjectId, GLES20.GL_LINK_STATUS, linkStatus, 0)

        //if (D) {
        Log.v(
            TAG, "Results of linking program:\n"
                    + GLES20.glGetProgramInfoLog(programObjectId)
        )

        //}
        if (linkStatus[0] == 0) {
            GLES20.glDeleteProgram(programObjectId)
            //if (D) {
            Log.w(TAG, "Linking of program failed.")
            //}
            return 0
        }

        return programObjectId
    }

    fun validateProgram(programObjectId: Int): Boolean {
        GLES20.glValidateProgram(programObjectId)

        val validateStatus = IntArray(1)
        GLES20.glGetProgramiv(programObjectId, GLES20.GL_VALIDATE_STATUS, validateStatus, 0)

        Log.v(
            TAG, ("Results of validating program: " + validateStatus[0]
                    + "\nLog:" + GLES20.glGetProgramInfoLog(programObjectId))
        )
        return validateStatus[0] != 0
    }

    fun buildProgram(vertexShaderSource: String?, fragmentShaderSource: String?): Int {
        val program: Int

        val vertexShader = compileVertexShader(vertexShaderSource)
        val fragmentShader = compileFragmentShader(fragmentShaderSource)
        program = linkProgram(vertexShader, fragmentShader)

        //if (D) {
        validateProgram(program)

        //}
        return program
    }
}
