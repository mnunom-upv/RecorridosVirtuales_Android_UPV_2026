package upv.pm.may_ago_26.iti_271503.pgu2_eq05;

import android.opengl.Matrix;

import java.util.ArrayList;
import java.util.List;

public class FachadaPrincipal {

    private List<Prisma> partes;
    private Escalones escalones;
    private List<Ventana> techito;

    public FachadaPrincipal(int tx_pared_guinda, int tx_pared_blanca, int tx_pavimento, int tx_techo_fachada) {
        partes = new ArrayList<>();
        techito = new ArrayList<>();

        // paredes escaleras
        partes.add(new Prisma(tx_pared_guinda, 1050, 60f, 50f));  // idx 0
        partes.add(new Prisma(tx_pared_blanca, 1050, 20f, 25));   // idx 1

        // base pilares centro
        partes.add(new Prisma(tx_pared_guinda, 50, 50f, 50)); // idx 2
        partes.add(new Prisma(tx_pared_guinda, 50, 50f, 50)); // idx 3

        // pilares centro (ahora más altos, igual que los laterales)
        partes.add(new Prisma(tx_pared_blanca, 50, 200f, 50)); // idx 4
        partes.add(new Prisma(tx_pared_blanca, 50, 200f, 50)); // idx 5

        // pilares costados
        partes.add(new Prisma(tx_pared_blanca, 30, 200f, 30)); // idx 6
        partes.add(new Prisma(tx_pared_blanca, 30, 200f, 30)); // idx 7

        // techo
        partes.add(new Prisma(tx_pared_guinda, 1050, 10f, 300)); // idx 8
        partes.add(new Prisma(tx_pared_blanca, 1055, 10f, 305)); // idx 9
        partes.add(new Prisma(tx_pared_blanca, 1060, 5f, 310)); // idx 10
        partes.add(new Prisma(tx_pared_blanca, 1000, 50f, 250)); // idx 11

        // techo del techo
        partes.add(new Prisma(tx_pared_guinda, 600, 150, 100)); // cosa ladrillo
        partes.add(new Prisma(tx_pared_blanca, 50, 135, 100)); // idx 6
        partes.add(new Prisma(tx_pared_blanca, 50, 135f, 100)); // idx 7

        // Puerta techo en V invertida (diagonales)
        partes.add(new Prisma(tx_pared_blanca, 20f, 330f, 100f)); // idx 9 - diagonal izq
        partes.add(new Prisma(tx_pared_blanca, 20f, 330f, 100f)); // idx 10 - diagonal der

        // pisopartes
        partes.add(new Prisma(tx_pavimento, 1050, 45f, 200)); // idx 11
        partes.add(new Prisma(tx_pavimento, 150, 45f, 50)); // idx 12

        // escalones base
        escalones = new Escalones(tx_pavimento, 5, 400, 10, 100);

        // techito blanco raro
        techito.add(new Ventana(tx_techo_fachada, 500f, 1200f, 0.3f, 0.22f));
        techito.add(new Ventana(tx_techo_fachada, 500f, 1200f, 0.3f, 0.22f));
        techito.add(new Ventana(tx_techo_fachada, 500f, 1200f, 0.3f, 0.22f));
        techito.add(new Ventana(tx_techo_fachada, 500f, 1200f, 0.3f, 0.22f));
        techito.add(new Ventana(tx_techo_fachada, 500f, 1200f, 0.3f, 0.22f));
        techito.add(new Ventana(tx_techo_fachada, 500f, 1200f, 0.3f, 0.22f));
        techito.add(new Ventana(tx_techo_fachada, 500f, 1200f, 0.3f, 0.22f));

    }

    public void draw(float[] modelMatrix,
                     int mMvpMatrixHandle, int mTextureUniformHandle,
                     int mPositionHandle, int mTextureCoordinateHandle) {

        int i = 0;
        float[] tempMatrix = new float[16];

        // paredes escaleras
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, 0, 0, -675);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        partes.get(i++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, 0, 40f, -675);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        partes.get(i++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

        // base pilares centro
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, -100, 40f, -675);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        partes.get(i++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, 100, 40f, -675);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        partes.get(i++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

        // pilares centro
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, -100, 165f, -675);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        partes.get(i++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, 100, 165f, -675);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        partes.get(i++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

        // pilares costados
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, -500, 140f, -675);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        partes.get(i++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, 500, 140f, -675);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        partes.get(i++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

        // techo
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, 0, 240f, -750);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        partes.get(i++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

        float avance = 500 * 0.3f;
        float posicionX = 0 - avance - 375;

        for (Ventana v : techito) {
            Matrix.setIdentityM(tempMatrix, 0);
            Matrix.translateM(tempMatrix, 0, posicionX, 234f, - (880));
            Matrix.rotateM(tempMatrix, 0, 90, 1, 0, 0);
            Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
            v.draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

            posicionX += avance;  // avanza al siguiente lugar horizontalmente
        }

        // decorado
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, 0, 250f, -750);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        partes.get(i++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, 0, 255f, -750);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        partes.get(i++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, 0, 260f, -750);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        partes.get(i++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

        // techo del techo
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, 0, 320f, -750);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        partes.get(i++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, -325, 350f, -750);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        partes.get(i++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, 325, 350f, -750);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        partes.get(i++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

        // diagonal izquierda (techo V invertida)
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, -150, 460f, -750);
        Matrix.rotateM(tempMatrix, 0, -70f, 0, 0, 1);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        partes.get(i++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

        // diagonal derecha (techo V invertida)
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, 150, 460f, -750);
        Matrix.rotateM(tempMatrix, 0, 70f, 0, 0, 1);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        partes.get(i++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

        // piso
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, 0, 30f, -800);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        partes.get(i++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, 0, 30f, -675);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        partes.get(i++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

        // escalones base
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, 0, 0, -600);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        escalones.draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);
    }
}
