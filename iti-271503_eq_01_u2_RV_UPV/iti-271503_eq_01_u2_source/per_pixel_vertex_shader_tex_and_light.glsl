uniform mat4 u_MvpMatrix;		// A constant representing the combined model/view/projection matrix.

attribute vec2 a_TexCoord; // Per-vertex texture coordinate information we will pass in.
attribute vec4 a_Color;
attribute vec4 a_Position;		// Per-vertex position information we will pass in.

varying vec2 v_TexCoord;   // This will be passed into the fragment shader.
varying vec4 v_Color;
		  
// The entry point for our vertex shader.  
void main()                                                 	
{
	v_TexCoord = a_TexCoord;
	v_Color = a_Color;
	gl_Position = u_MvpMatrix * a_Position;
}                                                          