precision mediump float;       	// Set the default precision to medium. We don't need as high of a 
								// precision in the fragment shader.

uniform sampler2D u_Sampler;    // The input texture.
varying vec2 v_TexCoord;   // Interpolated texture coordinate per fragment.
varying vec4 v_Color;

// The entry point for our fragment shader.
void main()                    		
{
    vec4 color0 = texture2D(u_Sampler, v_TexCoord);
    gl_FragColor = color0;
}

