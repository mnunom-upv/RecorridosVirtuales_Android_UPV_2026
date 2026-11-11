precision mediump float;

varying vec2 v_TexCoord;        // Coordenadas de textura interpoladas.
uniform sampler2D u_Texture;    // La textura de entrada.
uniform float u_VignetteIntensity; // Intensidad de la viñeta.

void main() {
    // Calcular la distancia desde el centro para aplicar el efecto de viñeta
    vec2 coord = v_TexCoord - 0.5;
    float dist = length(coord);
    float vignette = 1.0 - smoothstep(0.4, 0.8, dist * u_VignetteIntensity);

    // Obtener el color original de la textura base
    vec4 originalColor = texture2D(u_Texture, v_TexCoord);

    // Mezclar el efecto de viñeta con el color original
    gl_FragColor = vec4(originalColor.rgb * vignette, originalColor.a);
}
