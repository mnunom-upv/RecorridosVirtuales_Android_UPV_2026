package upv.pm.may_ago_26.iti_271503.pgu2_eq05;

import android.opengl.Matrix;

import java.util.ArrayList;
import java.util.List;

public class EsqueletoEdificio {

    private Calle techo;
    private List<Pared> paredes;
    private List<Ventana> ventanas;
    private List<Ventana> puertas;   // usaremos ventana como “puerta”
    private List<Ventana> letreros;  // letreros dorados con letras rojas
    private int tx_puerta;
    private int tx_ladrillo;
    private int tx_ventana;
    private int tx_techo;

    public EsqueletoEdificio(int tx_ladrillo, int tx_ventana, int tx_puerta, int tx_techo, int LADO_X) {

        this.tx_ladrillo = tx_ladrillo;
        this.tx_ventana = tx_ventana;
        this.tx_puerta   = tx_puerta;
        this.tx_techo = tx_techo;

        int ancho  = LADO_X - 1100;
        float huecoAncho = 500f;

        paredes = new ArrayList<>();
        ventanas = new ArrayList<>();
        puertas  = new ArrayList<>();
        letreros = new ArrayList<>();

        techo = new Calle(tx_techo, ancho, ancho - huecoAncho + 100, huecoAncho + 100);

        // pared frontal
        paredes.add(new Pared(tx_ladrillo, (ancho/5)*3, 250f));
        paredes.add(new Pared(tx_ladrillo, (ancho/5)*1, 250f));
        paredes.add(new Pared(tx_ladrillo, (ancho/5)*1, 250f));
        // pared trasera
        paredes.add(new Pared(tx_ladrillo, ancho, 250f));
        // pared lateral izquierda
        paredes.add(new Pared(tx_ladrillo, ancho - 200, 250f));
        // pared lateral derecha
        paredes.add(new Pared(tx_ladrillo, ancho - 200, 250f));

        // paredes interiores
        paredes.add(new Pared(tx_ladrillo, (ancho/2 + 100) - 2 * (int)huecoAncho, 250f));
        paredes.add(new Pared(tx_ladrillo, (ancho/2 + 100) - 2 * (int)huecoAncho, 250f));
        paredes.add(new Pared(tx_ladrillo, ancho - 2 * (int)huecoAncho, 250f));
        paredes.add(new Pared(tx_ladrillo, ancho - 400 - 2 * (int)huecoAncho, 250f));
        paredes.add(new Pared(tx_ladrillo, ancho - 400 - 2 * (int)huecoAncho, 250f));

        // paredes separadoras d habitaciones FRENTE
        paredes.add(new Pared(tx_ladrillo, huecoAncho + 200, 250f));
        paredes.add(new Pared(tx_ladrillo, huecoAncho, 250f));
        paredes.add(new Pared(tx_ladrillo, huecoAncho, 250f));
        paredes.add(new Pared(tx_ladrillo, huecoAncho + 200, 250f));

        // paredes separadoras d habitaciones ATRAS
        paredes.add(new Pared(tx_ladrillo, huecoAncho, 250f));
        paredes.add(new Pared(tx_ladrillo, huecoAncho, 250f));
        paredes.add(new Pared(tx_ladrillo, huecoAncho, 250f));
        paredes.add(new Pared(tx_ladrillo, huecoAncho, 250f));

        // paredes separadoras d habitaciones IZQ
        paredes.add(new Pared(tx_ladrillo, huecoAncho, 250f));
        paredes.add(new Pared(tx_ladrillo, huecoAncho, 250f));
        paredes.add(new Pared(tx_ladrillo, huecoAncho, 250f));
        paredes.add(new Pared(tx_ladrillo, huecoAncho, 250f));

        // paredes separadoras d habitaciones DER
        paredes.add(new Pared(tx_ladrillo, huecoAncho, 250f));
        paredes.add(new Pared(tx_ladrillo, huecoAncho, 250f));
        paredes.add(new Pared(tx_ladrillo, huecoAncho, 250f));
        paredes.add(new Pared(tx_ladrillo, huecoAncho, 250f));

        ventanas.add(new Ventana(tx_ventana, 400f, 450f, 0.35f, 0.35f));
        ventanas.add(new Ventana(tx_ventana, 400f, 450f, 0.35f, 0.35f));

        // ventanas enfrente q no es fachada
        ventanas.add(new Ventana(tx_ventana, 400f, 450f, 0.35f, 0.35f));
        ventanas.add(new Ventana(tx_ventana, 400f, 450f, 0.35f, 0.35f));
        ventanas.add(new Ventana(tx_ventana, 400f, 450f, 0.35f, 0.35f));
        ventanas.add(new Ventana(tx_ventana, 400f, 450f, 0.35f, 0.35f));
        ventanas.add(new Ventana(tx_ventana, 400f, 450f, 0.35f, 0.35f));
        ventanas.add(new Ventana(tx_ventana, 400f, 450f, 0.35f, 0.35f));
        ventanas.add(new Ventana(tx_ventana, 400f, 450f, 0.35f, 0.35f));
        ventanas.add(new Ventana(tx_ventana, 400f, 450f, 0.35f, 0.35f));
        ventanas.add(new Ventana(tx_ventana, 400f, 450f, 0.35f, 0.35f));
        ventanas.add(new Ventana(tx_ventana, 400f, 450f, 0.35f, 0.35f));

        String[] nombresPuertas = {
                "Dirección",                                  // 1
                "Administración",                             // 2
                null,                                         // 3
                "Artes escenicas y Danza",                    // 4
                "Auditorio Consuelo Canto Leal",               // 5
                "Cafeteria",                                  // 6
                "Musica",                                     // 7
                "Danza",                                      // 8
                "Danza",                                      // 9
                null,                                         // 10
                "Danza",                                      // 11
                "Tamaulipas Cultura",                          // 12
                "Fotografia, Oratoria, Composición Literaria", // 13
                "Galeria Ramon Garcia Zurita",                // 14
                "Musica",                                     // 15
                "Artes Visuales",                             // 16
                "Artes Visuales",                             // 17
                "Artes Visuales",                             // 18
                "Artes visuales Aula Emilio Rodriguez Montantes", // 19
                "Musica",                                     // 20
                "Musica Juan D. Tercero",                     // 21
                "Musica",                                     // 22
                null,                                         // 23
                null,                                         // 24
                null,                                         // 25
                null                                          // 26
        };

        for (int p = 0; p < 26; p++) {
            puertas.add(new Ventana(tx_puerta, 320f, 220f, 0.5f, 0.75f));
            if (nombresPuertas[p] != null) {
                int tx_placa = TextTextureHelper.createGoldPlaqueTexture(nombresPuertas[p]);
                if (p == 11) { // Tamaulipas Cultura (Puerta 12)
                    letreros.add(new Ventana(tx_placa, 110f, 42f, 1.0f, 1.0f));
                } else {
                    letreros.add(new Ventana(tx_placa, 140f, 50f, 1.0f, 1.0f));
                }
            } else {
                letreros.add(null);
            }
        }

    }

    public void draw(float[] modelMatrix,
                     int mMvpMatrixHandle, int mTextureUniformHandle,
                     int mPositionHandle, int mTextureCoordinateHandle,
                     float CENTRO_TABLERO, float z, float LADO_X, float LADO_Z) {

        int i = 0, k = 0;
        float ancho = LADO_X - 1100;
        float huecoAncho = 500f;

        float[] tempMatrix = new float[16];

        // techo
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, CENTRO_TABLERO - ((ancho/5)*5)/2, 260f, -900);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        techo.draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

        // pared frontal
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, CENTRO_TABLERO - ((ancho/5)*3)/2, 10.1f, -900);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        paredes.get(i++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, CENTRO_TABLERO - (ancho/2), 10.1f, -900 + 200);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        paredes.get(i++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, CENTRO_TABLERO + (ancho/2) - ((ancho/5)*1), 10.1f, -900 + 200);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        paredes.get(i++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

        // ventanas frontales
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, CENTRO_TABLERO - 250 - (300 * 0.35f), 80.1f, -899);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        ventanas.get(k++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, CENTRO_TABLERO + 250, 80.1f, -899);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        ventanas.get(k++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

        // izq
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, CENTRO_TABLERO + 700 - (300 * 0.35f), 80.1f, -899);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        ventanas.get(k++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, CENTRO_TABLERO + 850 - (300 * 0.35f), 80.1f, -899);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        ventanas.get(k++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, CENTRO_TABLERO + 1000 - (300 * 0.35f), 80.1f, -899);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        ventanas.get(k++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, CENTRO_TABLERO + 1450 - (300 * 0.35f), 80.1f, -900 + 201);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        ventanas.get(k++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, CENTRO_TABLERO + 1750 - (300 * 0.35f), 80.1f, -900 + 201);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        ventanas.get(k++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

        // der
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, CENTRO_TABLERO - 700 - (300 * 0.35f), 80.1f, -899);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        ventanas.get(k++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, CENTRO_TABLERO - 850 - (300 * 0.35f),  80.1f, -899);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        ventanas.get(k++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, CENTRO_TABLERO - 1000 - (300 * 0.35f), 80.1f, -899);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        ventanas.get(k++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, CENTRO_TABLERO - 1450, 80.1f, -900 + 201);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        ventanas.get(k++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, CENTRO_TABLERO - 1750, 80.1f, -900 + 201);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        ventanas.get(k++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

        // pared trasera
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, CENTRO_TABLERO - ancho/2, 10.1f, -LADO_Z + 600);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        paredes.get(i++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

        // pared lateral izquierda
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, CENTRO_TABLERO - ancho/2, 10.1f, CENTRO_TABLERO - ancho + 700);
        Matrix.rotateM(tempMatrix, 0, 90f, 0, 1, 0);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        paredes.get(i++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

        // pared lateral derecha
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, CENTRO_TABLERO + ancho/2, 10.1f, CENTRO_TABLERO - ancho + 700);
        Matrix.rotateM(tempMatrix, 0, 90f, 0, 1, 0);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        paredes.get(i++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

        // paredes interiores (dos frontales)
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, CENTRO_TABLERO - (ancho - 2 * huecoAncho)/2, 10.1f, -900 - huecoAncho);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        paredes.get(i++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, CENTRO_TABLERO + 8*50, 10.1f, -900 - huecoAncho);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        paredes.get(i++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

        int d = 0; // índice de puertas y letreros

        // 1. PUERTAS Y LETREROS EN PARED INTERIOR FRENTE (5 puertas)
        // Z = -910 - huecoAncho = -1410
        // Centro despejado para el portón de salida
        float zFrente = -910f - huecoAncho;
        float[] xFrente = {
                CENTRO_TABLERO - 1280f,
                CENTRO_TABLERO - 900f,
                CENTRO_TABLERO + 580f,
                CENTRO_TABLERO + 880f,
                CENTRO_TABLERO + 1180f
        };

        for (int p = 0; p < 5; p++) {
            int idx = d++;
            // Puerta
            Matrix.setIdentityM(tempMatrix, 0);
            Matrix.translateM(tempMatrix, 0, xFrente[p], 10.1f, zFrente);
            Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
            puertas.get(idx).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

            // Letrero a un lado (rotado 180° para invertir dirección horizontal)
            if (letreros.get(idx) != null) {
                Matrix.setIdentityM(tempMatrix, 0);
                Matrix.translateM(tempMatrix, 0, xFrente[p] + 165f + 140f, 80.0f, zFrente + 1.0f);
                Matrix.rotateM(tempMatrix, 0, 180f, 0, 1, 0);
                Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
                letreros.get(idx).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);
            }
        }

        // 2. PUERTAS Y LETREROS EN PARED INTERIOR TRASERA (7 puertas)
        // Z = -LADO_Z + 610 + huecoAncho
        float zAtras = -LADO_Z + 610f + huecoAncho;
        float[] xAtras = {
                CENTRO_TABLERO - 1300f,
                CENTRO_TABLERO - 880f,
                CENTRO_TABLERO - 460f,
                CENTRO_TABLERO - 40f,
                CENTRO_TABLERO + 380f,
                CENTRO_TABLERO + 800f,
                CENTRO_TABLERO + 1220f
        };

        for (int p = 0; p < 7; p++) {
            int idx = d++;
            // Puerta
            Matrix.setIdentityM(tempMatrix, 0);
            Matrix.translateM(tempMatrix, 0, xAtras[p], 10.1f, zAtras);
            Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
            puertas.get(idx).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

            // Letrero a un lado (para Puerta 12 "Tamaulipas Cultura", colocar a la izquierda a -120f)
            if (letreros.get(idx) != null) {
                float posX = (p == 6) ? (xAtras[p] - 120f) : (xAtras[p] + 165f);
                Matrix.setIdentityM(tempMatrix, 0);
                Matrix.translateM(tempMatrix, 0, posX, 80.0f, zAtras + 1.0f);
                Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
                letreros.get(idx).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);
            }
        }

        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, CENTRO_TABLERO - (ancho - 2 * huecoAncho)/2, 10.1f, -LADO_Z + 600 + huecoAncho);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        paredes.get(i++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

        // 3. PUERTAS Y LETREROS EN PARED INTERIOR LATERAL IZQUIERDA (7 puertas)
        // X = CENTRO_TABLERO - ancho/2 + huecoAncho + 10
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, CENTRO_TABLERO - ancho/2 + huecoAncho, 10.1f, CENTRO_TABLERO - ancho + 500 - huecoAncho);
        Matrix.rotateM(tempMatrix, 0, 90f, 0, 1, 0);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        paredes.get(i++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

        float xIzq = CENTRO_TABLERO - ancho / 2f + huecoAncho + 10f;
        float[] zIzq = {
                -1520f,
                -1870f,
                -2220f,
                -2570f,
                -2920f,
                -3270f,
                -3620f
        };

        for (int p = 0; p < 7; p++) {
            int idx = d++;
            // Puerta
            Matrix.setIdentityM(tempMatrix, 0);
            Matrix.translateM(tempMatrix, 0, xIzq, 10.1f, zIzq[p]);
            Matrix.rotateM(tempMatrix, 0, 90f, 0, 1, 0);
            Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
            puertas.get(idx).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

            // Letrero a un lado
            if (letreros.get(idx) != null) {
                Matrix.setIdentityM(tempMatrix, 0);
                Matrix.translateM(tempMatrix, 0, xIzq + 1.0f, 80.0f, zIzq[p] - 165f);
                Matrix.rotateM(tempMatrix, 0, 90f, 0, 1, 0);
                Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
                letreros.get(idx).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);
            }
        }

        // 4. PUERTAS Y LETREROS EN PARED INTERIOR LATERAL DERECHA (7 puertas)
        // X = CENTRO_TABLERO + ancho/2 - huecoAncho - 10
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.translateM(tempMatrix, 0, CENTRO_TABLERO + ancho/2 - huecoAncho, 10.1f, CENTRO_TABLERO - ancho + 500 - huecoAncho);
        Matrix.rotateM(tempMatrix, 0, 90f, 0, 1, 0);
        Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
        paredes.get(i++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

        float xDerecha = CENTRO_TABLERO + ancho / 2f - huecoAncho - 10f;
        float[] zDerecha = {
                -1520f,
                -1870f,
                -2220f,
                -2570f,
                -2920f,
                -3270f,
                -3620f
        };

        for (int p = 0; p < 7; p++) {
            int idx = d++;
            // Puerta
            Matrix.setIdentityM(tempMatrix, 0);
            Matrix.translateM(tempMatrix, 0, xDerecha, 10.1f, zDerecha[p]);
            Matrix.rotateM(tempMatrix, 0, 90f, 0, 1, 0);
            Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
            puertas.get(idx).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);

            // Letrero a un lado (rotado -90° para lectura de izquierda a derecha hacia el patio)
            if (letreros.get(idx) != null) {
                Matrix.setIdentityM(tempMatrix, 0);
                Matrix.translateM(tempMatrix, 0, xDerecha - 1.0f, 80.0f, zDerecha[p] - 165f - 140f);
                Matrix.rotateM(tempMatrix, 0, -90f, 0, 1, 0);
                Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
                letreros.get(idx).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);
            }
        }


        // separadores de habitaciones (frontal)
        float z_normal = -900f;
        float z_adelantado = -700f;

        for (int sep = 1; sep <= 4; sep++) {
            Matrix.setIdentityM(tempMatrix, 0);

            float zgrande = (sep == 1 || sep == 4) ? z_adelantado : z_normal;

            Matrix.translateM(tempMatrix, 0, (CENTRO_TABLERO - ancho/2) + (ancho/5)*sep, 10.1f, zgrande);
            Matrix.rotateM(tempMatrix, 0, 90f, 0, 1, 0);
            Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
            paredes.get(i++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);
        }


        // separadores de habitaciones (trasero)
        for (int sep = 1; sep <= 4; sep++) {
            Matrix.setIdentityM(tempMatrix, 0);
            Matrix.translateM(tempMatrix, 0, (CENTRO_TABLERO - ancho/2) + (ancho/5)*sep, 10.1f, -LADO_Z + 600 + huecoAncho);
            Matrix.rotateM(tempMatrix, 0, 90f, 0, 1, 0);
            Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
            paredes.get(i++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);
        }

        // separadores de habitaciones (izquierda)
        for (int sep = 1; sep <= 4; sep++) {
            Matrix.setIdentityM(tempMatrix, 0);
            Matrix.translateM(tempMatrix, 0, CENTRO_TABLERO - ancho/2, 10.1f, -900 - ((ancho - 400)/5) * sep);
            Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
            paredes.get(i++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);
        }

        // separadores de habitaciones (derecha)
        for (int sep = 1; sep <= 4; sep++) {
            Matrix.setIdentityM(tempMatrix, 0);
            Matrix.translateM(tempMatrix, 0, CENTRO_TABLERO + ancho/2 - huecoAncho, 10.1f, -900 - ((ancho - 400)/5) * sep);
            Matrix.multiplyMM(tempMatrix, 0, modelMatrix, 0, tempMatrix, 0);
            paredes.get(i++).draw(tempMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);
        }

    }

}
