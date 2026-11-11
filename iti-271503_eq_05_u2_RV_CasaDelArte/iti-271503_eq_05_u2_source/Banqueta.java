package upv.pm.may_ago_26.iti_271503.pgu2_eq05;

import android.opengl.Matrix;

public class Banqueta {
    private Calle superficieHueca;       // marco hueco, como la calle
    private Pared[] bordillos = new Pared[4]; // 4 bordes
    private float altura;
    private float anchoTotal;
    private float largoTotal;
    private float anchoBanqueta;
    private float largoVertical;
    private float largoHorizontal;

    public Banqueta(int texSuperficie, int texBordillo, float largoTotal, float anchoTotal, float altura, float anchoCalle, float anchoBanqueta) {
        this.largoTotal = largoTotal;
        this.anchoTotal = anchoTotal;
        this.altura = altura;
        this.anchoBanqueta = anchoBanqueta;

        largoHorizontal = largoTotal - 2 * anchoCalle;
        largoVertical   = anchoTotal - 2 * anchoCalle;

        // superficie hueca tipo marco
        superficieHueca = new Calle(texSuperficie, largoHorizontal, largoVertical, anchoBanqueta);

        // bordillos pa la banqueta
        bordillos[0] = new Pared(texBordillo, largoHorizontal, altura); // arriba
        bordillos[1] = new Pared(texBordillo, largoHorizontal, altura); // abajo
        bordillos[2] = new Pared(texBordillo, largoVertical, altura); // izquierda
        bordillos[3] = new Pared(texBordillo, largoVertical, altura); // derecha
    }

    public void draw(float[] modelMatrix, int mvpMatrixHandle, int textureUniformHandle,
                     int positionHandle, int textureCoordinateHandle) {

        float[] superficieMatrix = new float[16];
        Matrix.setIdentityM(superficieMatrix, 0);
        Matrix.translateM(superficieMatrix, 0, 0, altura, 0); // subir al nivel de la banqueta
        Matrix.multiplyMM(superficieMatrix, 0, modelMatrix, 0, superficieMatrix, 0);
        superficieHueca.draw(superficieMatrix, mvpMatrixHandle, textureUniformHandle, positionHandle, textureCoordinateHandle);

        // dibujar costados (4 paredes)
        for (int i = 0; i < 4; i++) {
            float[] paredMatrix = new float[16];
            Matrix.setIdentityM(paredMatrix, 0);

            // centrar verticalmente
            Matrix.translateM(paredMatrix, 0, 0, altura / 2f, 0);

            switch (i) {
                case 0: Matrix.translateM(paredMatrix, 0, 0, 0, 0); break; // arriba
                case 1: Matrix.translateM(paredMatrix, 0, 0, 0, -largoVertical); break; // abajo
                case 2:
                    Matrix.translateM(paredMatrix, 0, 0, 0, 0);
                    Matrix.rotateM(paredMatrix, 0, 90, 0, 1, 0);
                    break;
                case 3:
                    Matrix.translateM(paredMatrix, 0, largoHorizontal, 0, 0);
                    Matrix.rotateM(paredMatrix, 0, 90, 0, 1, 0);
                    break;
            }

            Matrix.multiplyMM(paredMatrix, 0, modelMatrix, 0, paredMatrix, 0);
            bordillos[i].draw(paredMatrix, mvpMatrixHandle, textureUniformHandle, positionHandle, textureCoordinateHandle);
        }
    }
}
