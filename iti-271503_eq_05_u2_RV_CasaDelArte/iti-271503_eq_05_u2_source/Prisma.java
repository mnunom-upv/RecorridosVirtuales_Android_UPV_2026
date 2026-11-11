package upv.pm.may_ago_26.iti_271503.pgu2_eq05;

import android.opengl.GLES20;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Prisma {
    private int textura;

    private FloatBuffer mPositions;
    private FloatBuffer mTextureCoordinates;

    private final int mBytesPerFloat = 4;
    private final int mPositionDataSize = 3; // x, y, z
    private final int mTextureCoordinateDataSize = 2; // u, v

    // textures para cada cara, el mismo para todas (puedes ajustar luego si quieres diferentes)
    private static final float[] CUBE_TEXTURE_COORDS = {
            0f, 0f,  0f, 1f,  1f, 0f,  1f, 1f,  // cara 1
            0f, 0f,  0f, 1f,  1f, 0f,  1f, 1f,  // cara 2
            0f, 0f,  0f, 1f,  1f, 0f,  1f, 1f,  // cara 3
            0f, 0f,  0f, 1f,  1f, 0f,  1f, 1f,  // cara 4
            0f, 0f,  0f, 1f,  1f, 0f,  1f, 1f,  // cara 5
            0f, 0f,  0f, 1f,  1f, 0f,  1f, 1f   // cara 6
    };

    public Prisma(int textura, float ancho, float alto, float profundo) {
        this.textura = textura;

        float hx = ancho / 2f;
        float hy = alto / 2f;
        float hz = profundo / 2f;

        // Vértices de un cubo centrado en el origen
        float[] vertices = {
                // Frente
                -hx,  hy,  hz,
                -hx, -hy,  hz,
                hx,  hy,  hz,
                hx, -hy,  hz,
                // Derecha
                hx,  hy,  hz,
                hx, -hy,  hz,
                hx,  hy, -hz,
                hx, -hy, -hz,
                // Atrás
                hx,  hy, -hz,
                hx, -hy, -hz,
                -hx,  hy, -hz,
                -hx, -hy, -hz,
                // Izquierda
                -hx,  hy, -hz,
                -hx, -hy, -hz,
                -hx,  hy,  hz,
                -hx, -hy,  hz,
                // Arriba
                -hx,  hy, -hz,
                -hx,  hy,  hz,
                hx,  hy, -hz,
                hx,  hy,  hz,
                // Abajo
                -hx, -hy,  hz,
                -hx, -hy, -hz,
                hx, -hy,  hz,
                hx, -hy, -hz
        };

        mPositions = ByteBuffer.allocateDirect(vertices.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mPositions.put(vertices).position(0);

        mTextureCoordinates = ByteBuffer.allocateDirect(CUBE_TEXTURE_COORDS.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mTextureCoordinates.put(CUBE_TEXTURE_COORDS).position(0);
    }

    public void draw(float[] modelMatrix, int mvpMatrixHandle, int textureUniformHandle,
                     int positionHandle, int textureCoordinateHandle) {

        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, modelMatrix, 0);

        mPositions.position(0);
        GLES20.glVertexAttribPointer(positionHandle, mPositionDataSize, GLES20.GL_FLOAT, false, 0, mPositions);
        GLES20.glEnableVertexAttribArray(positionHandle);

        mTextureCoordinates.position(0);
        GLES20.glVertexAttribPointer(textureCoordinateHandle, mTextureCoordinateDataSize, GLES20.GL_FLOAT, false, 0, mTextureCoordinates);
        GLES20.glEnableVertexAttribArray(textureCoordinateHandle);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textura);
        GLES20.glUniform1i(textureUniformHandle, 0);

        // 6 caras, 4 vértices cada una, dibujamos con TRIANGLE_STRIP por cara (usando 4 vértices por cara)
        for (int i = 0; i < 6; i++) {
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, i * 4, 4);
        }
    }
}
