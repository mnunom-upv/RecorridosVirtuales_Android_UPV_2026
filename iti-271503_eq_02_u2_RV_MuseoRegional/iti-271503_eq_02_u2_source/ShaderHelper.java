package upv.pm.may_ago_26.iti_271503.pgu2_eq02;

import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_VALIDATE_STATUS;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glGetProgramInfoLog;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glValidateProgram;

import android.opengl.GLES20;
import android.util.Log;

public class ShaderHelper
{
	private static final String TAG = "ShaderHelper";
	//private static final String TAG = ShaderHelper.class.getSimpleName();
	
	/** 
	 * Helper function to compile a shader.
	 * 
	 * @param shaderType The shader type.
	 * @param shaderSource The shader source code.
	 * @return An OpenGL handle to the shader.
	 */
	public static int compileShader(final int shaderType, final String shaderSource) 
	{
		int shaderHandle = GLES20.glCreateShader(shaderType);

		if (shaderHandle != 0) 
		{
			// Pass in the shader source.
			GLES20.glShaderSource(shaderHandle, shaderSource);

			// Compile the shader.
			GLES20.glCompileShader(shaderHandle);

			// Get the compilation status.
			final int[] compileStatus = new int[1];
			GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

			// If the compilation failed, delete the shader.
			if (compileStatus[0] == 0) 
			{
				Log.e(TAG, "Error compiling shader: " + GLES20.glGetShaderInfoLog(shaderHandle));
				GLES20.glDeleteShader(shaderHandle);
				shaderHandle = 0;
			}
		}

		if (shaderHandle == 0)
		{			
			throw new RuntimeException("Error creating shader.");
		}
		
		return shaderHandle;
	}
	
	/**
	 * Helper function to compile and link a program.
	 * 
	 * @param vertexShaderHandle An OpenGL handle to an already-compiled vertex shader.
	 * @param fragmentShaderHandle An OpenGL handle to an already-compiled fragment shader.
	 * @param attributes Attributes that need to be bound to the program.
	 * @return An OpenGL handle to the program.
	 */
	public static int createAndLinkProgram(final int vertexShaderHandle, final int fragmentShaderHandle, final String[] attributes) 
	{
		int programHandle = GLES20.glCreateProgram();
		
		if (programHandle != 0) 
		{
			// Bind the vertex shader to the program.
			GLES20.glAttachShader(programHandle, vertexShaderHandle);			

			// Bind the fragment shader to the program.
			GLES20.glAttachShader(programHandle, fragmentShaderHandle);
			
			// Bind attributes
			if (attributes != null)
			{
				final int size = attributes.length;
				for (int i = 0; i < size; i++)
				{
					GLES20.glBindAttribLocation(programHandle, i, attributes[i]);
				}						
			}
			
			// Link the two shaders together into a program.
			GLES20.glLinkProgram(programHandle);

			// Get the link status.
			final int[] linkStatus = new int[1];
			GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);

			// If the link failed, delete the program.
			if (linkStatus[0] == 0) 
			{				
				Log.e(TAG, "Error compiling program: " + GLES20.glGetProgramInfoLog(programHandle));
				GLES20.glDeleteProgram(programHandle);
				programHandle = 0;
			}
		}
		
		if (programHandle == 0)
		{
			throw new RuntimeException("Error creating program.");
		}
		
		return programHandle;
	}

	// -----------------------

	//private static final boolean D = BuildConfig.DEBUG;
	//private static final String TAG = ShaderHelper.class.getSimpleName();

	public static int compileVertexShader(String shaderCode) {
		return compileShader(GL_VERTEX_SHADER, shaderCode);
	}

	public static int compileFragmentShader(String shaderCode) {
		return compileShader(GL_FRAGMENT_SHADER, shaderCode);
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

	public static int linkProgram(int vertexShaderId, int fragmentShaderId) {
		final int programObjectId = glCreateProgram();

		if (programObjectId == 0) {
			//if (D) {
			Log.w(TAG, "Could not create new program.");
			//}
			return 0;
		}

		glAttachShader(programObjectId, vertexShaderId);
		glAttachShader(programObjectId, fragmentShaderId);

		glLinkProgram(programObjectId);

		final int[] linkStatus = new int[1];
		glGetProgramiv(programObjectId, GL_LINK_STATUS, linkStatus, 0);

		//if (D) {
		Log.v(TAG, "Results of linking program:\n"
				+ glGetProgramInfoLog(programObjectId));
		//}

		if (linkStatus[0] == 0) {
			glDeleteProgram(programObjectId);
			//if (D) {
			Log.w(TAG, "Linking of program failed.");
			//}
			return 0;
		}

		return programObjectId;
	}

	public static boolean validateProgram(int programObjectId) {
		glValidateProgram(programObjectId);

		final int[] validateStatus = new int[1];
		glGetProgramiv(programObjectId, GL_VALIDATE_STATUS, validateStatus, 0);

		Log.v(TAG, "Results of validating program: " + validateStatus[0]
				+ "\nLog:" + glGetProgramInfoLog(programObjectId));
		return validateStatus[0] != 0;
	}

	public static int buildProgram(String vertexShaderSource, String fragmentShaderSource) {
		int program;

		int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
		int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);
		program = ShaderHelper.linkProgram(vertexShader, fragmentShader);

		//if (D) {
		ShaderHelper.validateProgram(program);
		//}

		return program;
	}

	private ShaderHelper() {
	}
}
