package upv.pm.may_ago_26.iti_271503.pgu2_eq05;

import android.opengl.GLES20;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Piso {
    private int textura;
    private float largo, ancho;

    private FloatBuffer mPositions;
    private FloatBuffer mTextureCoordinates;

    private final int mBytesPerFloat = 4;
    private final int mPositionDataSize = 3;
    private final int mTextureCoordinateDataSize = 2;

    public Piso(int textura, float largo, float ancho) {
        this.textura = textura;
        this.largo = largo;
        this.ancho = ancho;

        // generar buffers con vertices y textura
        final float[] vertices = {
                0,     0,    0,
                0,     0,   -ancho,
                largo, 0,    0,
                largo, 0,   -ancho
        };

        // escalado UV proporcional
        float repX = largo / 50f; // cada 100 unidades, una repetición horizontal
        float repY = ancho / 50f; // cada 100 unidades, una repetición vertical

        final float[] vertices_textura = {
                0.0f,  0.0f,
                0.0f,  repY,
                repX,  0.0f,
                repX,  repY
        };

        mPositions = ByteBuffer.allocateDirect(vertices.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mPositions.put(vertices).position(0);

        mTextureCoordinates = ByteBuffer.allocateDirect(vertices_textura.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTextureCoordinates.put(vertices_textura).position(0);
    }

    // metodo para dibujar el piso
    public void draw(float[] modelMatrix, int mvpMatrixHandle, int textureUniformHandle,
                     int positionHandle, int textureCoordinateHandle) {

        // enviar matriz MVP
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, modelMatrix, 0);

        // pasar posiciones
        mPositions.position(0);
        GLES20.glVertexAttribPointer(positionHandle, mPositionDataSize, GLES20.GL_FLOAT, false, 0, mPositions);
        GLES20.glEnableVertexAttribArray(positionHandle);

        // pasar textura
        mTextureCoordinates.position(0);
        GLES20.glVertexAttribPointer(textureCoordinateHandle, mTextureCoordinateDataSize, GLES20.GL_FLOAT, false, 0, mTextureCoordinates);
        GLES20.glEnableVertexAttribArray(textureCoordinateHandle);

        // bind textura
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textura);
        GLES20.glUniform1i(textureUniformHandle, 0);

        // dibujar
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }
}

