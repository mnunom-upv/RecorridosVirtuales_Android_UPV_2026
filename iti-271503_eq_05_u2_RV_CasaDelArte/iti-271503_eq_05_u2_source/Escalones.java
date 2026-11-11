package upv.pm.may_ago_26.iti_271503.pgu2_eq05;

import android.opengl.Matrix;

import java.util.ArrayList;
import java.util.List;

public class Escalones {
    private List<Prisma> escalones;
    private int textura;
    private int numEscalones;
    private float anchoBase;
    private float altoEscalon;
    private float profundidadBase;

    public Escalones(int textura, int numEscalones, float anchoBase, float altoEscalon, float profundidadBase) {
        this.textura = textura;
        this.numEscalones = numEscalones;
        this.anchoBase = anchoBase;
        this.altoEscalon = altoEscalon;
        this.profundidadBase = profundidadBase;

        escalones = new ArrayList<>();
        generarEscalones();
    }

    private void generarEscalones() {
        for (int i = 0; i < numEscalones; i++) {
            // factor de reducción para ancho y profundidad (evitar que llegue a 0)
            float factor = 1.0f - ((float) i / (numEscalones * 1.5f));

            if (factor < 0.5f) factor = 0.5f; // límite mínimo para evitar que sea muy pequeño

            float ancho = anchoBase * factor;

            // profundidad reducida, pero cara trasera fija, por lo que el prisma se desplaza en Z
            float profundidad = profundidadBase * factor;

            Prisma escalon = new Prisma(textura, ancho, altoEscalon, profundidad);
            escalones.add(escalon);
        }
    }

    public void draw(float[] modelMatrix, int mvpMatrixHandle, int textureUniformHandle,
                     int positionHandle, int textureCoordinateHandle) {
        for (int i = 0; i < numEscalones; i++) {
            Prisma escalon = escalones.get(i);

            // factor para calcular offset de profundidad (misma lógica que en generación)
            float factor = 1.0f - ((float) i / (numEscalones * 1.5f));
            if (factor < 0.5f) factor = 0.5f;

            float ancho = anchoBase * factor;
            float profundidad = profundidadBase * factor;

            // pos en Y normal (altura escalón)
            float offsetY = altoEscalon * i;

            // pa mantener la cara trasera fija en Z=0,
            // desplazamos el escalón en Z hacia adelante la mitad de la diferencia de profundidad:
            float offsetZ = (profundidad / 2f) - (profundidadBase / 2f);

            // offsetX para centrar escalón (si quieren centrar en x)
            float offsetX = 0;

            float[] escalonModelMatrix = new float[16];
            System.arraycopy(modelMatrix, 0, escalonModelMatrix, 0, modelMatrix.length);

            Matrix.translateM(escalonModelMatrix, 0, offsetX, offsetY, offsetZ);

            escalon.draw(escalonModelMatrix, mvpMatrixHandle, textureUniformHandle, positionHandle, textureCoordinateHandle);
        }
    }

}

