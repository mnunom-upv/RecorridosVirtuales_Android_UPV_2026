precision mediump float;
uniform sampler2D u_Texture;
varying vec2 v_TexCoord;
varying float v_Depth;
void main() {
    vec4 texColor = texture2D(u_Texture, v_TexCoord);
    
    // Depth-based fog effect
    float fogDistance = 1200.0;
    float fogFactor = clamp(v_Depth / fogDistance, 0.0, 1.0);
    fogFactor = smoothstep(0.0, 1.0, fogFactor);
    
    // Un color de niebla que ayuda a darle profundidad (gris ligeramente azulado)
    vec4 fogColor = vec4(0.12, 0.15, 0.20, 1.0);
    
    gl_FragColor = mix(texColor, fogColor, fogFactor);
}
