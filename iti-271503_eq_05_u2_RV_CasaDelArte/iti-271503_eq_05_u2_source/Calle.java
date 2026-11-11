package upv.pm.may_ago_26.iti_271503.pgu2_eq05;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Calle {

    private int textura;
    private FloatBuffer mPositions;
    private FloatBuffer mTextureCoordinates;

    private final int mBytesPerFloat = 4;
    private final int mPositionDataSize = 3;
    private final int mTextureCoordinateDataSize = 2;

    private float[] vertices;
    private float[] vertices_textura;

    public Calle(int textura, float totalLargo, float totalAncho, float anchoCalle) {
        this.textura = textura;

        // 4 tramos: arriba, abajo, izquierda, derecha
        vertices = new float[]{
                // Calle superior
                0, 0, 0,
                0, 0, -anchoCalle,
                totalLargo, 0, 0,
                totalLargo, 0, -anchoCalle,

                // Calle inferior
                0, 0, -totalAncho,
                0, 0, -totalAncho + anchoCalle,
                totalLargo, 0, -totalAncho,
                totalLargo, 0, -totalAncho + anchoCalle,

                // Calle izquierda
                0, 0, -anchoCalle,
                0, 0, -totalAncho + anchoCalle,
                anchoCalle, 0, -anchoCalle,
                anchoCalle, 0, -totalAncho + anchoCalle,

                // Calle derecha
                totalLargo - anchoCalle, 0, -anchoCalle,
                totalLargo - anchoCalle, 0, -totalAncho + anchoCalle,
                totalLargo, 0, -anchoCalle,
                totalLargo, 0, -totalAncho + anchoCalle
        };

        float[] texArriba = generarCoordenadasTextura(totalLargo, anchoCalle);
        float[] texAbajo = generarCoordenadasTextura(totalLargo, anchoCalle);
        float[] texIzquierda = generarCoordenadasTextura(anchoCalle, totalAncho - 2 * anchoCalle);
        float[] texDerecha = generarCoordenadasTextura(anchoCalle, totalAncho - 2 * anchoCalle);

        // unir los 4 bloques
        vertices_textura = new float[4 * 8]; // 4 rectángulos x 4 vértices x 2 coords
        System.arraycopy(texArriba, 0, vertices_textura, 0, 8);
        System.arraycopy(texAbajo, 0, vertices_textura, 8, 8);
        System.arraycopy(texIzquierda, 0, vertices_textura, 16, 8);
        System.arraycopy(texDerecha, 0, vertices_textura, 24, 8);

        mPositions = ByteBuffer.allocateDirect(vertices.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mPositions.put(vertices).position(0);

        mTextureCoordinates = ByteBuffer.allocateDirect(vertices_textura.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTextureCoordinates.put(vertices_textura).position(0);
    }

    private float[] generarCoordenadasTextura(float largo, float ancho) {
        float escalaTextura = 256f; // píxeles de la textura
        return new float[]{
                0f, 0f,
                0f, ancho / escalaTextura,
                largo / escalaTextura, 0f,
                largo / escalaTextura, ancho / escalaTextura
        };
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

        for (int i = 0; i < 4; i++) {
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, i * 4, 4);
        }
    }
}

