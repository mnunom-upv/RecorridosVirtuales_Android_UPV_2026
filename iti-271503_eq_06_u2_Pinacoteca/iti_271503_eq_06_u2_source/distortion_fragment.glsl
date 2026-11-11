precision mediump float;

uniform sampler2D u_Sampler;
varying vec2 v_TexCoord;

// Parámetros de distorsión
const vec4 K = vec4(0.22, 0.24, 0.0, 1.0); // Coeficientes de distorsión
const vec2 LensCenter = vec2(0.5, 0.5); // Centro de la lente
const vec2 Scale = vec2(0.5, 0.5); // Escalado
const vec2 ScaleIn = vec2(2.0, 2.0); // Escalado inverso

void main() {
    vec2 texCoord = (v_TexCoord - LensCenter) * ScaleIn;
    float rSq = texCoord.x * texCoord.x + texCoord.y * texCoord.y;
    vec2 distorted = texCoord * (K.x + K.y * rSq + K.z * rSq * rSq + K.w * rSq * rSq * rSq);
    distorted = distorted * Scale + LensCenter;

    if (distorted.x < 0.0 || distorted.x > 1.0 || distorted.y < 0.0 || distorted.y > 1.0) {
        gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0); // Color negro para las áreas fuera de la distorsión
    } else {
        gl_FragColor = texture2D(u_Sampler, distorted);
    }
}
