uniform mat4 u_MvpMatrix;
attribute vec4 a_Position;
attribute vec2 a_TexCoord;
varying vec2 v_TexCoord;
varying float v_Depth;
void main() {
    v_TexCoord = a_TexCoord;
    vec4 pos = u_MvpMatrix * a_Position;
    gl_Position = pos;
    v_Depth = pos.z;
}
