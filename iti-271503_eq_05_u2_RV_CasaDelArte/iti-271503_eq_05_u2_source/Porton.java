package upv.pm.may_ago_26.iti_271503.pgu2_eq05;

import android.opengl.Matrix;

import java.util.ArrayList;
import java.util.List;

public class Porton {

    private List<Prisma> paredPorton;
    private List<Pared> barras;

    private final int tx_pared_guinda;
    private final int tx_barras;

    private final float espacio = 100f;
    private float anchoBase = 0;
    private final float anchoBase_atras;

    public Porton(int tx_pared_guinda, int tx_barras, int anchoBase) {
        this.tx_pared_guinda = tx_pared_guinda;
        this.tx_barras = tx_barras;
        this.anchoBase = anchoBase;

        this.anchoBase_atras = anchoBase * 2 + espacio * 2;

        paredPorton = new ArrayList<>();
        barras = new ArrayList<>();

        // Paredes principales
        paredPorton.add(new Prisma(tx_pared_guinda, anchoBase, 50f, 30f)); // idx 0
        paredPorton.add(new Prisma(tx_pared_guinda, anchoBase, 50f, 30f)); // idx 1
        paredPorton.add(new Prisma(tx_pared_guinda, 30f, 50f, anchoBase_atras)); // idx 2
        paredPorton.add(new Prisma(tx_pared_guinda, 30f, 50f, anchoBase_atras)); // idx 3
        paredPorton.add(new Prisma(tx_pared_guinda, anchoBase_atras, 50f, 30f)); // idx 4

        // Puerta
        paredPorton.add(new Prisma(tx_pared_guinda, 30f, 190f, 30f)); // idx 5
        paredPorton.add(new Prisma(tx_pared_guinda, 30f, 190f, 30f)); // idx 6
        // postes
        paredPorton.add(new Prisma(tx_pared_guinda, 20f, 190f, 20f)); // idx 7
        paredPorton.add(new Prisma(tx_pared_guinda, 20f, 190f, 20f)); // idx 8
        paredPorton.add(new Prisma(tx_pared_guinda, 20f, 190f, 20f)); // idx 7
        paredPorton.add(new Prisma(tx_pared_guinda, 20f, 190f, 20f)); // idx 8

        // Puerta techo en V invertida (diagonales)
        paredPorton.add(new Prisma(tx_pared_guinda, 20f, 120f, 30f)); // idx 9 - diagonal izq
        paredPorton.add(new Prisma(tx_pared_guinda, 20f, 120f, 30f)); // idx 10 - diagonal der

        // Barras
        barras.add(new Pared(tx_barras, anchoBase, 150)); // idx 0
        barras.add(new Pared(tx_barras, anchoBase, 150)); // idx 1
        barras.add(new Pared(tx_barras, anchoBase_atras, 150)); // idx 2
        barras.add(new Pared(tx_barras, anchoBase_atras, 150)); // idx 3
        barras.add(new Pared(tx_barras, anchoBase_atras, 150)); // idx 4
    }

    public void draw(float[] modelMatrix,
                     int mMvpMatrixHandle, int mTextureUniformHandle,
                     int mPositionHandle, int mTextureCoordinateHandle,
                     float CENTRO_TABLERO, float z, float LADO_X, float LADO) {

        int i = 0, k = 0;

        float x1 = CENTRO_TABLERO - (anchoBase/2 + espacio);
        float x2 = CENTRO_TABLERO + (anchoBase/2 + espacio);
        float x3 = CENTRO_TABLERO - (anchoBase_atras/2);
        float x4 = CENTRO_TABLERO + (anchoBase_atras/2);
        float x5 = CENTRO_TABLERO;

        float[] tempMatrix = new float[16];

        // frontal izq
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, x1, 10.1f, z - 250 - 100);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        paredPorton.get(i++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

        // barras frontal izq
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, x1 - anchoBase/2, 10.1f, z - 250 - 100);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        barras.get(k++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

        // frontal der
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, x2, 10.1f, z - 250 - 100);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        paredPorton.get(i++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

        // barras frontal der
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, x2 - anchoBase/2, 10.1f, z - 250 - 100);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        barras.get(k++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

        // lateral izq
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, x3, 10.1f, z - 250 - 100 - (anchoBase_atras / 2));
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        paredPorton.get(i++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

        // barras lateral izq (rotado 90°)
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, x3, 10.1f, z - 250 - 100);
        Matrix.rotateM(tempMatrix, 0, 90f, 0, 1, 0);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        barras.get(k++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

        // lateral der
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, x4, 10.1f, z - 250 - 100 - (anchoBase_atras / 2));
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        paredPorton.get(i++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

        // barras lateral der (rotado 90°)
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, x4, 10.1f, z - 250 - 100);
        Matrix.rotateM(tempMatrix, 0, 90f, 0, 1, 0);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        barras.get(k++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

        // atras
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, x5, 10.1f, z - LADO + 350);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        paredPorton.get(i++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

        // barras atras
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, x5 - anchoBase_atras/2, 10.1f, z - LADO + 350);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        barras.get(k++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

        // puerta izq
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, CENTRO_TABLERO - espacio, 80.1f, z - 250 - 100);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        paredPorton.get(i++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

        // puerta der
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, CENTRO_TABLERO + espacio*3, 80.1f, z - 250 - 100);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        paredPorton.get(i++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

        // poste puerta izq
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, CENTRO_TABLERO - espacio*3, 80.1f, z - 250 - 100);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        paredPorton.get(i++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

        // poste puerta der
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, CENTRO_TABLERO + espacio, 80.1f, z - 250 - 100);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        paredPorton.get(i++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

        // poste izq
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, x1, 80.1f, z - 250 - 100);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        paredPorton.get(i++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

        // poste der
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, x2, 80.1f, z - 250 - 100);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        paredPorton.get(i++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

        // diagonal izquierda (techo V invertida)
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, CENTRO_TABLERO - espacio + 48, 190f, z - 250 - 100);
        Matrix.rotateM(tempMatrix, 0, -70f, 0, 0, 1);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        paredPorton.get(i++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

        // diagonal derecha (techo V invertida)
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, CENTRO_TABLERO + espacio - 48, 190f, z - 250 - 100);
        Matrix.rotateM(tempMatrix, 0, 70f, 0, 0, 1);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        paredPorton.get(i++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

    }
}
