package upv.pm.may_ago_26.iti_271503.pgu2_eq05;

import android.opengl.GLES20;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Ventana {
    private int textura;
    private FloatBuffer mPositions;
    private FloatBuffer mTextureCoordinates;

    private float anchoOriginal;  // ancho real de la imagen (pixeles)
    private float altoOriginal;   // alto real de la imagen (pixeles)

    private float escalaAncho = 1.0f;
    private float escalaAlto = 1.0f;

    public Ventana(int textura, float anchoOriginal, float altoOriginal, float escalaAncho, float escalaAlto) {
        this.textura = textura;
        this.anchoOriginal = anchoOriginal;
        this.altoOriginal = altoOriginal;
        this.escalaAncho = escalaAncho;
        this.escalaAlto = escalaAlto;

        float anchoEscalado = anchoOriginal * escalaAncho;
        float altoEscalado = altoOriginal * escalaAlto;

        // vertices de la ventana (en plano XY, z=0)
        float[] vertices = {
                0,         0,          0,
                0,         altoEscalado, 0,
                anchoEscalado, 0,          0,
                anchoEscalado, altoEscalado, 0
        };

        // coordenadas de textura mapeadas proporcionalmente
        float[] texCoords = {
                0f, 0f,
                0f, 1f,
                1f, 0f,
                1f, 1f
        };

        mPositions = ByteBuffer.allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mPositions.put(vertices).position(0);

        mTextureCoordinates = ByteBuffer.allocateDirect(texCoords.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTextureCoordinates.put(texCoords).position(0);
    }

    // metodo para cambiar la escala después de creada la ventana (por si las dudas nomas)
    public void setEscala(float escalaAncho, float escalaAlto) {
        this.escalaAncho = escalaAncho;
        this.escalaAlto = escalaAlto;

        float anchoEscalado = anchoOriginal * escalaAncho;
        float altoEscalado = altoOriginal * escalaAlto;

        float[] vertices = {
                0,         0,          0,
                0,         altoEscalado, 0,
                anchoEscalado, 0,          0,
                anchoEscalado, altoEscalado, 0
        };

        mPositions.clear();
        mPositions.put(vertices).position(0);
    }

    public void draw(float[] modelMatrix, int mvpMatrixHandle, int textureUniformHandle,
                     int positionHandle, int textureCoordinateHandle) {
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, modelMatrix, 0);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, mPositions);
        GLES20.glEnableVertexAttribArray(positionHandle);

        GLES20.glVertexAttribPointer(textureCoordinateHandle, 2, GLES20.GL_FLOAT, false, 0, mTextureCoordinates);
        GLES20.glEnableVertexAttribArray(textureCoordinateHandle);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textura);
        GLES20.glUniform1i(textureUniformHandle, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }
}
