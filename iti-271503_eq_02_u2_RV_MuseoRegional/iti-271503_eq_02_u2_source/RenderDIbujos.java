package upv.pm.may_ago_26.iti_271503.pgu2_eq02;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class RenderDIbujos {

    /** Texturas */
    private int textura_ventanas_afuera = 0;
    private int textura_cesped = 0;
    private int textura_cielo = 0;
    private int textura_cielo_enfrente = 0;
    private int textura_cielo_atras = 0;
    private int textura_cielo_derecha = 0;
    private int textura_cielo_izquierda = 0;
    private int  textura_barda = 0;
    private int  textura_barda_u = 0;
    private int  textura_barda_u_estacionamiento = 0;
    private int  textura_puerta_principal_izq = 0;
    private int  textura_puerta_principal = 0;
    private int  textura_puerta_principal_der = 0;
    private int textura_banqueta = 0;
    private int textura_banqueta_lateral = 0;
    private int textura_fachada_cuarto = 0;
    private  int textura_puerta_lateral= 0;
    private  int textura_pared= 0;
    private int textura_fachada_biblioteca = 0;
    private int textura_suelo_ladrillos = 0;
    private int textura_discapacitados= 0;
    private int textura_puerta_biblioteca = 0;
    private int textura_puerta_interior_biblioteca = 0;
    private int textura_suelo_madera = 0;
    private int textura_techo_madera_luces = 0;
    private int textura_pared_lateral_taquilla = 0;
    private int textura_puerta_dentro_taquilla = 0;
    private int textura_suelo_nave1 = 0;
    private int textura_pared_cristal_nave1 = 0;
    private int textura_pared2_nave1 = 0;
    private int textura_puerta_principal_nave1 = 0;
    private int textura_puerta_interior_nave1= 0;
    private int textura_pared_interior_nave=0;
    private int textura_puerta_taquilla=0;
    private int textura_jardin= 0;
    private int textura_arte_jardin= 0;
    private int textura_arte1_jardin= 0;
    private int textura_puerta_sala= 0;
    private int textura_interior1_biblioteca= 0;
    private int textura_interior2_biblioteca= 0;
    private int textura_interior3_biblioteca= 0;
    private int textura_techo_exterior= 0;
    private int textura_bano_izq= 0;
    private int textura_bano_der= 0;
    private int textura_puerta_oficina= 0;
    private int textura_arte_oficina= 0;
    private int textura_maceta_oficina= 0;
    private int textura_maceta1_oficina=0;
    private int getTextura_pared_cristal_nave2=0;


    // Model variables
    private FloatBuffer mPositions;
    private FloatBuffer mTextureCoordinates;

    // Rendering variables
    /** This will be used to pass in the transformation matrix. */
    private int mMvpMatrixHandle;
    /** This will be used to pass in the texture. */
    private int mTextureUniformHandle;
    /** This will be used to pass in model position information. */
    private int mPositionHandle;
    /** This will be used to pass in model texture coordinate information. */
    private int mTextureCoordinateHandle;
    /** This is a handle to our cube shading program. */
    private int mProgramHandle;
    /** How many bytes per float. */
    private final int mBytesPerFloat = 4;
    /** Size of the position data in elements. */
    private final int mPositionDataSize = 3;
    /** Size of the texture coordinate data in elements. */
    private final int mTextureCoordinateDataSize = 2;


    //Viewing variables
    float[] MVProjection;
    float[] Mdp;


    //Contexto de la MainActivity
    Context context;

    //Basura
    int gll = 0;
    int u_MvpMatrix = 0;


    public RenderDIbujos(int mProgramHandle, int mMvpMatrixHandle, int mTextureUniformHandle, int mPositionHandle, int mTextureCoordinateHandle, Context context) {
        this.RenderDibujos(mProgramHandle, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle, context);
    }

    public void RenderDibujos(int mProgramHandle, int mMvpMatrixHandle, int mTextureUniformHandle, int mPositionHandle, int mTextureCoordinateHandle, Context context) {

        this.context = context;
        this.Mdp = new float[16];

        textura_ventanas_afuera = generarTextura(R.drawable.fachada_biblioteca);
        //TEXTURAS DEL CIELO
        textura_cesped = generarTextura(R.drawable.cesped);
        textura_cielo = generarTextura(R.drawable.cielo_nublado);
        textura_cielo_enfrente = generarTextura(R.drawable.cielo_nublado);
        textura_cielo_atras = generarTextura(R.drawable.cielo_nublado);
        textura_cielo_derecha = generarTextura(R.drawable.fondo_emntrada_contrario);
        textura_cielo_izquierda = generarTextura(R.drawable.cielo_nublado);
        //TEXTURAS DE LA BARDA
        textura_puerta_lateral= generarTextura(R.drawable.afurea_puerta_principal1);
        textura_barda = generarTextura(R.drawable.barda);
        textura_barda_u = generarTextura(R.drawable.barda_u);
        textura_puerta_principal_izq = generarTextura(R.drawable.puerta_principal_izq);
        textura_puerta_principal = generarTextura(R.drawable.puerta_principal);
        textura_puerta_principal_der = generarTextura(R.drawable.puerta_principal_der);
        textura_barda_u_estacionamiento = generarTextura(R.drawable.barda_u_estacionamiento);
        //TEXTURAS DE LA BANQUETA
        textura_banqueta = generarTextura(R.drawable.afuera_banqueta_1);
        textura_banqueta_lateral = generarTextura(R.drawable.afuera_banqueta_2);

        textura_suelo_ladrillos= generarTextura(R.drawable.afuera_banqueta_1);
        //TEXTURAS DE LA FACHADA DELANTERA DE LA SALA DE LECTURA Y LA NAVE 2
        textura_fachada_cuarto = generarTextura(R.drawable.fachada_cuarto);
        //TEXTURAS DE LA FACHADA LATERAL DE LA BIBLIOTECA
        textura_fachada_biblioteca = generarTextura(R.drawable.fachada_biblioteca);

        //DENTRO DEL MUSEO
        textura_techo_exterior= generarTextura(R.drawable.techo_exterior);
        textura_pared= generarTextura(R.drawable.pared_1);
        textura_suelo_madera= generarTextura(R.drawable.suelo_madera);
        textura_techo_madera_luces= generarTextura(R.drawable.techo_madera_luces);
        textura_suelo_nave1= generarTextura(R.drawable.suelo_dentro_nave2);
        textura_jardin= generarTextura(R.drawable.jardin);
        textura_discapacitados= generarTextura(R.drawable.suelo_rampa_discapacitados);
        textura_arte_jardin= generarTextura(R.drawable.jardin_arte1);
        textura_arte1_jardin= generarTextura(R.drawable.jardin_arte2);

            //Taquilla
        textura_puerta_sala= generarTextura(R.drawable.pared_puerta_puerta_sala);
        textura_puerta_taquilla= generarTextura(R.drawable.puerta_taquilla);
         textura_pared_lateral_taquilla= generarTextura(R.drawable.pared_taquilla_ventana_1);
         textura_puerta_dentro_taquilla= generarTextura(R.drawable.adentro_puerta_principal_1);

         //Biblioteca
            textura_puerta_biblioteca= generarTextura(R.drawable.puerta_biblioteca);
            textura_interior1_biblioteca= generarTextura(R.drawable.pared1_interior_biblioteca);
            textura_interior2_biblioteca= generarTextura(R.drawable.pared2_interior_biblioteca);
            textura_interior3_biblioteca= generarTextura(R.drawable.pared3_interior_biblioteca);
        textura_puerta_interior_biblioteca= generarTextura(R.drawable.biblioteca_pared_blanca);

            //NAVE1
        textura_pared_cristal_nave1= generarTextura(R.drawable.pared_cristal1);
        textura_pared2_nave1= generarTextura(R.drawable.pared_puerta_nave1);
        textura_puerta_principal_nave1= generarTextura(R.drawable.pared_puerta2_nave1);
        textura_puerta_interior_nave1= generarTextura(R.drawable.pared_blanca_puerta_nave1);
        textura_pared_interior_nave= generarTextura(R.drawable.pared_blanca);

        //oficina
        textura_bano_izq= generarTextura(R.drawable.puerta_bano2);
        textura_bano_der= generarTextura(R.drawable.puerta_bano1);
        textura_puerta_oficina= generarTextura(R.drawable.puerta1_oficina);
        textura_arte_oficina= generarTextura(R.drawable.anuncio_oficina_bano);
        textura_maceta_oficina= generarTextura(R.drawable.pared_maceta1);
        textura_maceta1_oficina= generarTextura(R.drawable.pared_maceta2);

        //NAVE 2
        getTextura_pared_cristal_nave2= generarTextura(R.drawable.pared_cristal2);

        this.mProgramHandle = mProgramHandle;
        this.mMvpMatrixHandle = mMvpMatrixHandle;
        this.mTextureUniformHandle = mTextureUniformHandle;
        this.mPositionHandle = mPositionHandle;
        this.mTextureCoordinateHandle = mTextureCoordinateHandle;

        MVProjection = new float[16];
        Mdp = new float[16];



    }

    private int generarTextura(int textura){
        int t = TextureHelper.loadTexture(context, textura);

        // Asegurar que la textura maneje correctamente la transparencia
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, t);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        // Habilitar el canal alfa para la textura
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        return t;
    }


    //Métodos que dibujan las formas
    private void piso_o_techo(int nosirve1, int nosirve2, double largo, double ancho, int nosirve3, int texturaHandle) {

        //Coordenadas de la pared
        final float[] vertices = {
                0,     0,    -1,
                0,     0,    (float)-ancho,
                (float)largo, 0,    -1,
                (float)largo, 0,    (float)-ancho
        };

        //Coordenadas de la textura
        final float[] vertices_textura = {
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 0.0f,
                1.0f, 1.0f
        };

        GLES20.glUseProgram(mProgramHandle);

        //voy hasta aqui
        cargarPosicion_Textura(vertices, vertices_textura, texturaHandle);


        // Pass in the combined matrix.
        GLES20.glUniformMatrix4fv(mMvpMatrixHandle, 1, false, Mdp, 0);

        int n = vertices.length / 3;

        // Draw the cube.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, n);
    }

    //Cuadrilatero parado en el eje de los X
    private void pared_X(int nosirve1, int nosirve2, double largo, double alto, int nosirve3, int texturaHandle) {


        //Coordenadas de la pared
        final float[] vertices = {
                0,              0,               0,
                0,              (float)alto,     0,
                (float)largo,   0,               0,
                (float)largo,   (float)alto,     0
        };

        //Coordenadas de la textura
        final float[] vertices_textura = {
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 0.0f,
                1.0f, 1.0f
        };

        cargarPosicion_Textura(vertices, vertices_textura, texturaHandle);
        // Antes de renderizar, habilitar blending
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        // Pass in the combined matrix.
        GLES20.glUniformMatrix4fv(mMvpMatrixHandle, 1, false, Mdp, 0);

        int n = vertices.length / 3;

        // Draw the cube.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, n);
    }

    //Cuadrilatero parado en el eje de los X
    private void pared_Z(int nosirve1, int nosirve2, double largo, double alto, int nosirve3, int texturaHandle) {


        //Coordenadas de la pared
        final float[] vertices = {
                0,     0,    0,
                0,     (float)alto, 0,
                0,     0,    (float)-largo,
                0,     (float)alto, (float)-largo
        };

        //Coordenadas de la textura
        final float[] vertices_textura = {
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 0.0f,
                1.0f, 1.0f
        };




        cargarPosicion_Textura(vertices, vertices_textura, texturaHandle);
        // Antes de renderizar, habilitar blending
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        // Pass in the combined matrix.
        GLES20.glUniformMatrix4fv(mMvpMatrixHandle, 1, false, Mdp, 0);

        int n = vertices.length / 3;

        // Draw the cube.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, n);
    }

    private void prisma_cuadrangular(int nosirve1, int nosirve2, double largo, double ancho, double alto, int nosirve3, int texturaHandle) {
        //System.arraycopy(g_modelMatrix, 0, g_mvpMatrix, 0, 16);

        //Cara frontal y trasera
        pared_X(nosirve1, nosirve2, largo, alto, nosirve3, texturaHandle);
        Matrix.translateM(Mdp, 0, 0,0, (float)-ancho);
        pared_X(nosirve1, nosirve2, largo, alto, nosirve3, texturaHandle);
        Matrix.translateM(Mdp, 0, 0,0, (float)ancho);

        //Cara izquierda
        pared_Z(nosirve1, nosirve2, ancho, alto, nosirve3, texturaHandle);

        //Piso
        piso_o_techo(nosirve1, nosirve2, largo, ancho, nosirve3, texturaHandle);
        Matrix.translateM(Mdp, 0, (float)largo,0, 0);

        //Cara derecha
        pared_Z(nosirve1, nosirve2, ancho, alto, nosirve3, texturaHandle);
        Matrix.translateM(Mdp, 0, (float)-largo, (float)alto, 0);

        //Techo
        piso_o_techo(nosirve1, nosirve2, largo, ancho, nosirve3, texturaHandle);
        Matrix.translateM(Mdp, 0, 0, (float)-alto, 0);
    }

    //Piso o techo
    private void piso_o_techo_especial(int nosirve1, int nosirve2, double x1, double z1, double x2, double z2, double x3, double z3, int nosirve3, int texturaHandle) {


        //Coordenadas de la pared
        final float[] vertices = {
                0,     0,    0,
                (float)x1,    0,   (float)z1,
                (float)x2,    0,   (float)z2,
                (float)x3,    0,   (float)z3
        };

        //Coordenadas de la textura
        final float[] vertices_textura = {
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 0.0f,
                1.0f, 1.0f
        };


        cargarPosicion_Textura(vertices, vertices_textura, texturaHandle);


        // Pass in the combined matrix.
        GLES20.glUniformMatrix4fv(mMvpMatrixHandle, 1, false, Mdp, 0);

        int n = vertices.length / 3;

        // Draw the cube.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, n);
    }

    public void cargarPosicion_Textura(float[] v, float[] vt, int textura){

        //Posiciones---------------------------------------------------------------------------------

        mPositions = ByteBuffer.allocateDirect(v.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mPositions.put(v).position(0);

        // Pass in the position information
        mPositions.position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false, 0, mPositions);

        GLES20.glEnableVertexAttribArray(mPositionHandle);

        //Textura-----------------------------------------------------------------------------------
        mTextureCoordinates = ByteBuffer.allocateDirect(vt.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTextureCoordinates.put(vt).position(0);

        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textura);

        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(mTextureUniformHandle, 0);

        // Pass in the texture coordinate information
        mTextureCoordinates.position(0);
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, mTextureCoordinateDataSize, GLES20.GL_FLOAT, false, 0, mTextureCoordinates);

        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);

        //----------------------------------------------------------------------------------------------

    }


    //Métodos para dibujar formas


        public void cielo(float[] modelViewProjection){
            MVProjection = modelViewProjection;
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(-500,700,700);
            piso_o_techo(0, 0, 3000, 3000, 0, textura_cielo);
        }

        public void cielo_enfrente(float[] modelViewProjection){
            MVProjection = modelViewProjection;
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(-500,-200,700);
            pared_X(0,0,3000,1000,0,textura_cielo_enfrente);
        }

        public void cielo_atras(float[] modelViewProjection){
            MVProjection = modelViewProjection;
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(-500,-200,-2300);
            pared_X(0,0,3000,1000,0,textura_cielo_atras);

        }

        public void cielo_derecha(float[] modelViewProjection){
            MVProjection = modelViewProjection;
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(-500,-200,700);
            pared_Z(0, 0, 3000, 1000, 0, textura_cielo_derecha);
        }

        public void cielo_izquierda(float[] modelViewProjection){
            MVProjection = modelViewProjection;
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(2500,-200,700);
            pared_Z(0, 0, 3000, 1000, 0, textura_cielo_izquierda);


        }



    //Edificios


        public void base_cesped(float[] modelViewProjection){
            MVProjection = modelViewProjection;
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(-500,-0.1f,700);
            piso_o_techo(0, 0, 3000, 3000, 0, textura_cesped);
        }

        public void barda(float[] modelViewProjection) {
            MVProjection = modelViewProjection;

            //////////////////////BARDA FRENTE//////////////////////
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(0, 0, 100);
            pared_X(0, 0, 210, 180, 0, textura_fachada_cuarto);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(300, 0, 100);
            pared_X(0, 0, 300, 140, 0, textura_barda_u);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(600, 0, 100);
            g_modelMatrix_rotate(30,0,1,0);
            pared_X(0, 0, 110, 190, 0, textura_puerta_principal_izq);
            g_modelMatrix_rotate(-30,0,1,0);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(650, 0, 44);
            pared_X(0, 0, 400, 280, 0, textura_puerta_principal);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1015, 0, 44);
            g_modelMatrix_rotate(-30,0,1,0);
            pared_X(0, 0, 112, 190, 0, textura_puerta_principal_der);
            g_modelMatrix_rotate(30,0,1,0);



            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1110, 0, 100);
            pared_X(0, 0, 300, 140, 0, textura_barda_u);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1410, 0, 100);
            pared_X(0, 0, 300, 180, 0, textura_fachada_cuarto);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1709, 0, 100);
            pared_X(0, 0, 98, 140, 0, textura_barda_u_estacionamiento);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1902, 0, 100);
            pared_X(0, 0, 98, 140, 0, textura_barda_u_estacionamiento);

            //////////////////////BARDA IZQUIERDA//////////////////////
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(0, 0, 100);
            pared_Z(0, 0, 415, 135, 0, textura_ventanas_afuera);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(0, 0, -300);
            pared_Z(0, 0, 350, 140, 0, textura_barda_u);

            //PUERTA PRINCIPAL

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(0, 0, -650);
            pared_Z(0, 0, 520, 180, 0, textura_puerta_lateral);


            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(0, 0, -1150);
            pared_Z(0, 0, 360, 100, 0, textura_barda_u);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(0, 0, -1500);
            pared_Z(0, 0, 400, 135, 0, textura_ventanas_afuera);

            //////////////////////BARDA DERECHA//////////////////////
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(2000, 0, 100);
            pared_Z(0, 0, 2000, 60, 0, textura_barda);

            //////////////////////BARDA ATRAS//////////////////////
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(0, 0, -1900);
            pared_X(0, 0, 2000, 60, 0, textura_barda);
        }

        public void banqueta(float[] modelViewProjection) {
            MVProjection = modelViewProjection;

            //////////////////////BANQUETA FRENTE//////////////////////
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(-60, 1f, 150);
            piso_o_techo(0, 0,  60, 50, 0, textura_banqueta);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(0, 1f, 150);
            piso_o_techo(0, 0,  200, 50, 0, textura_banqueta);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(200, 1f, 150);
            piso_o_techo(0, 0,  200, 50, 0, textura_banqueta);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(400, 1f, 150);
            piso_o_techo(0, 0,  200, 50, 0, textura_banqueta);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(600, 1f, 150);
            piso_o_techo(0, 0,  200, 50, 0, textura_banqueta);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(800, 1f, 150);
            piso_o_techo(0, 0,  200, 50, 0, textura_banqueta);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1000, 1f, 150);
            piso_o_techo(0, 0,  200, 50, 0, textura_banqueta);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1200, 1f, 150);
            piso_o_techo(0, 0,  200, 50, 0, textura_banqueta);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1400, 1f, 150);
            piso_o_techo(0, 0,  200, 50, 0, textura_banqueta);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1600, 1f, 150);
            piso_o_techo(0, 0,  200, 50, 0, textura_banqueta);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1800, 1f, 150);
            piso_o_techo(0, 0,  200, 50, 0, textura_banqueta);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(2000, 1f, 150);
            piso_o_techo(0, 0,  60, 50, 0, textura_banqueta);

            //////////////////////BANQUETA IZQUIERDA//////////////////////
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(-60, 1f, 101);
            piso_o_techo(0, 0,  60, 200, 0, textura_banqueta_lateral);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(-60, 1f, -98);
            piso_o_techo(0, 0,  60, 200, 0, textura_banqueta_lateral);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(-60, 1f, -297);
            piso_o_techo(0, 0,  60, 200, 0, textura_banqueta_lateral);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(-60, 1f, -496);
            piso_o_techo(0, 0,  60, 200, 0, textura_banqueta_lateral);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(-60, 1f, -695);
            piso_o_techo(0, 0,  60, 200, 0, textura_banqueta_lateral);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(-60, 1f, -894);
            piso_o_techo(0, 0,  60, 200, 0, textura_banqueta_lateral);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(-60, 1f, -1093);
            piso_o_techo(0, 0,  60, 200, 0, textura_banqueta_lateral);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(-60, 1f, -1292);
            piso_o_techo(0, 0,  60, 200, 0, textura_banqueta_lateral);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(-60, 1f, -1491);
            piso_o_techo(0, 0,  60, 200, 0, textura_banqueta_lateral);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(-60, 1f, -1690);
            piso_o_techo(0, 0,  60, 200, 0, textura_banqueta_lateral);

            //////////////////////BANQUETA DERECHA//////////////////////
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(2000, 1f, 101);
            piso_o_techo(0, 0,  60, 200, 0, textura_banqueta_lateral);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(2000, 1f, -98);
            piso_o_techo(0, 0,  60, 200, 0, textura_banqueta_lateral);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(2000, 1f, -297);
            piso_o_techo(0, 0,  60, 200, 0, textura_banqueta_lateral);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(2000, 1f, -496);
            piso_o_techo(0, 0,  60, 200, 0, textura_banqueta_lateral);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(2000, 1f, -695);
            piso_o_techo(0, 0,  60, 200, 0, textura_banqueta_lateral);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(2000, 1f, -894);
            piso_o_techo(0, 0,  60, 200, 0, textura_banqueta_lateral);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(2000, 1f, -1093);
            piso_o_techo(0, 0,  60, 200, 0, textura_banqueta_lateral);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(2000, 1f, -1292);
            piso_o_techo(0, 0,  60, 200, 0, textura_banqueta_lateral);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(2000, 1f, -1491);
            piso_o_techo(0, 0,  60, 200, 0, textura_banqueta_lateral);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(2000, 1f, -1690);
            piso_o_techo(0, 0,  60, 200, 0, textura_banqueta_lateral);

            //////////////////////BANQUETA ATRAS//////////////////////
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(-60, 1f, -1889);
            piso_o_techo(0, 0,  60, 60, 0, textura_banqueta);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(0, 1f, -1899);
            piso_o_techo(0, 0,  200, 50, 0, textura_banqueta);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(200, 1f, -1899);
            piso_o_techo(0, 0,  200, 50, 0, textura_banqueta);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(400, 1f, -1899);
            piso_o_techo(0, 0,  200, 50, 0, textura_banqueta);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(600, 1f, -1899);
            piso_o_techo(0, 0,  200, 50, 0, textura_banqueta);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(800, 1f, -1899);
            piso_o_techo(0, 0,  200, 50, 0, textura_banqueta);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1000, 1f, -1899);
            piso_o_techo(0, 0,  200, 50, 0, textura_banqueta);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1200, 1f, -1899);
            piso_o_techo(0, 0,  200, 50, 0, textura_banqueta);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1400, 1f, -1899);
            piso_o_techo(0, 0,  200, 50, 0, textura_banqueta);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1600, 1f, -1899);
            piso_o_techo(0, 0,  200, 50, 0, textura_banqueta);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1800, 1f, -1899);
            piso_o_techo(0, 0,  200, 50, 0, textura_banqueta);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(2000, 1f, -1889);
            piso_o_techo(0, 0,  60, 60, 0, textura_banqueta);

        }

        //DENTRO DEL MUSEO

        public void suelo_ladrillos_dentro (float[] modelViewProjection) {

            //TUNEL ENTRADA
            MVProjection = modelViewProjection;
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(-20, 1, -830);
            piso_o_techo(0, 0,  140, 100, 0, textura_suelo_ladrillos);

            MVProjection = modelViewProjection;
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(70, 1, -830);
            piso_o_techo(0, 0,  140, 100, 0, textura_suelo_ladrillos);


            //BIBLIOTECA
                //frente a la puerta
                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(1, 1f, -1458);
                piso_o_techo(0, 0,  70, 70, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(71, 1f, -1458);
                piso_o_techo(0, 0,  70, 70, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(141, 1f, -1458);
                piso_o_techo(0, 0,  70, 70, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(211, 1f, -1458);
                piso_o_techo(0, 0,  70, 70, 0, textura_suelo_ladrillos);

                //Lateral de la biblioteca
                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(200, 1f, -1500);
                piso_o_techo(0, 0,  80, 70, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(200, 1f, -1550);
                piso_o_techo(0, 0,  80, 70, 0, textura_suelo_ladrillos);

            //TAQUILLA
                //hacia biblioteca
                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(70, 1f, -1150);
                piso_o_techo(0, 0,  90, 70, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(160, 1f, -1150);
                piso_o_techo(0, 0,  90, 70, 0, textura_suelo_ladrillos);

                //frente
                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(200, 1f, -1080);
                piso_o_techo(0, 0,  70, 120, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(200, 1f, -1040);
                piso_o_techo(0, 0,  70, 60, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(200, 1f, -980);
                piso_o_techo(0, 0,  70, 60, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(200, 1f, -940);
                piso_o_techo(0, 0,  70, 60, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(200, 1f, -880);
                piso_o_techo(0, 0,  70, 60, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(200, 1f, -840);
                piso_o_techo(0, 0,  70, 60, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(200, 1f, -780);
                piso_o_techo(0, 0,  70, 60, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(200, 1f, -720);
                piso_o_techo(0, 0,  70, 60, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(200, 1f, -660);
                piso_o_techo(0, 0,  70, 60, 0, textura_suelo_ladrillos);


                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(200, 1f, -600);
                piso_o_techo(0, 0,  70, 60, 0, textura_suelo_ladrillos);


            //Hacia sala de lectura
                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(-10, 1f, -600);
                piso_o_techo(0, 0,  120, 60, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(100, 1f, -600);
                piso_o_techo(0, 0,  70, 60, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(155, 1f, -600);
                piso_o_techo(0, 0,  70, 60, 0, textura_suelo_ladrillos);



            //CRUCE entre taquilla y biblioteca
                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(1, 1f, -1150);
                piso_o_techo(0, 0,  70, 60, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(1, 1f, -1199);
                piso_o_techo(0, 0,  70, 60, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(1, 1f, -1249);
                piso_o_techo(0, 0,  70, 60, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(1, 1f, -1299);
                piso_o_techo(0, 0,  70, 60, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(1, 1f, -1349);
                piso_o_techo(0, 0,  70, 60, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(1, 1f, -1399);
                piso_o_techo(0, 0,  70, 60, 0, textura_suelo_ladrillos);


                //CRUCE ENTRE TAQUILLA Y SALA DE LECTURA

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(0, 1f, -590);
                piso_o_techo(0, 0,  70, 50, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(0, 1f, -539);
                piso_o_techo(0, 0,  70, 50, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(0, 1f, -490);
                piso_o_techo(0, 0,  70, 50, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(0, 1f, -440);
                piso_o_techo(0, 0,  70, 50, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(0, 1f, -390);
                piso_o_techo(0, 0,  70, 50, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(0, 1f, -339);
                piso_o_techo(0, 0,  70, 50, 0, textura_suelo_ladrillos);

                //ALREDEDOR DE SALA DE LECTURA

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(0, 1f, -290);
                piso_o_techo(0, 0,  70, 50, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(50, 1f, -290);
                piso_o_techo(0, 0,  70, 50, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(100, 1f, -290);
                piso_o_techo(0, 0,  70, 50, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(150, 1f, -290);
                piso_o_techo(0, 0,  70, 50, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(200, 1f, -290);
                piso_o_techo(0, 0,  70, 50, 0, textura_suelo_ladrillos);

                //SALA DE LECTURA
                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(200, 1f, -246);
                piso_o_techo(0, 0,  70, 50, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(200, 1f, -197);
                piso_o_techo(0, 0,  70, 50, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(200, 1f, -147);
                piso_o_techo(0, 0,  70, 50, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(200, 1f, -97);
                piso_o_techo(0, 0,  70, 50, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(200, 1f, -52);
                piso_o_techo(0, 0,  70, 50, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(200, 1f, -4);
                piso_o_techo(0, 0,  70, 50, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(200, 1f, 6);
                piso_o_techo(0, 0,  70, 50, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(200, 1f, 55);
                piso_o_techo(0, 0,  70, 50, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(200, 1f, 100);
                piso_o_techo(0, 0,  70, 50, 0, textura_suelo_ladrillos);

                //CRUCE SALA DE LECTURA A NAVE 1

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(260, 1f, 100);
                piso_o_techo(0, 0,  70, 50, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(320, 1f, 100);
                piso_o_techo(0, 0,  70, 50, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(380, 1f, 100);
                piso_o_techo(0, 0,  70, 50, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(440, 1f, 100);
                piso_o_techo(0, 0,  70, 50, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(500, 1f, 100);
                piso_o_techo(0, 0,  70, 50, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(560, 1f, 100);
                piso_o_techo(0, 0,  70, 50, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(600, 1f, 70);
                piso_o_techo(0, 0,  70, 50, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(600, 1f, 140);
                piso_o_techo(0, 0,  70, 50, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(600, 1f, 210);
                piso_o_techo(0, 0,  70, 50, 0, textura_suelo_ladrillos);

                //Pegado a la pared de nave 1
                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(600, 1f, 30);
                piso_o_techo(0, 0,  100, 100, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(600, 1f, -60);
                piso_o_techo(0, 0,  100, 100, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(600, 1f, -110);
                piso_o_techo(0, 0,  100, 100, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(600, 1f, -160);
                piso_o_techo(0, 0,  100, 100, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(540, 1f, -640);
                piso_o_techo(0, 0,  155, 100, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(540, 1f, -570);
                piso_o_techo(0, 0,  100, 100, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(540, 1f, -360);
                piso_o_techo(0, 0,  100, 240, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(640, 1f, -360);
                piso_o_techo(0, 0,  70, 100, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(600, 1f, -260);
                piso_o_techo(0, 0,  100, 200, 0, textura_suelo_ladrillos);





                //Taquilla  a nave 1
                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(260, 1f, -800);
                piso_o_techo(0, 0,  70, 70, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(260, 1f, -870);
                piso_o_techo(0, 0,  70, 50, 0, textura_discapacitados);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(260, 1f, -920);
                piso_o_techo(0, 0,  70, 70, 0, textura_suelo_ladrillos);

                  ///
                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(330, 1f, -800);
                piso_o_techo(0, 0,  130, 70, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(330, 1f, -870);
                piso_o_techo(0, 0,  130, 70, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(330, 1f, -920);
                piso_o_techo(0, 0,  130, 70, 0, textura_suelo_ladrillos);

                //
                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(460, 1f, -800);
                piso_o_techo(0, 0,  130, 70, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(460, 1f, -870);
                piso_o_techo(0, 0,  130, 70, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(460, 1f, -920);
                piso_o_techo(0, 0,  130, 70, 0, textura_suelo_ladrillos);

                ///
                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(560, 1f, -800);
                piso_o_techo(0, 0,  135, 70, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(560, 1f, -870);
                piso_o_techo(0, 0,  135, 70, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(560, 1f, -920);
                piso_o_techo(0, 0,  135, 70, 0, textura_suelo_ladrillos);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(540, 1f, -710);
                piso_o_techo(0, 0,  150, 100, 0, textura_suelo_ladrillos);






        }
        public void suelo_ladrillos_nave2_dentro (float[] modelViewProjection){
            //Pasillo entre paredes de cristal
                MVProjection = modelViewProjection;
                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(1099, 1, -380);
                piso_o_techo(0, 0,  140, 70, 0, textura_suelo_ladrillos);

                MVProjection = modelViewProjection;
                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(1099, 1, -429);
                piso_o_techo(0, 0,  140, 70, 0, textura_suelo_ladrillos);

                MVProjection = modelViewProjection;
                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(1099, 1, -498);
                piso_o_techo(0, 0,  140, 70, 0, textura_suelo_ladrillos);



            //PEGADA NAVE 1
                MVProjection = modelViewProjection;
                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(1000, 1, 70);
                piso_o_techo(0, 0,  90, 70, 0, textura_suelo_ladrillos);

                MVProjection = modelViewProjection;
                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(1000, 1, 0);
                piso_o_techo(0, 0,  90, 70, 0, textura_suelo_ladrillos);

                MVProjection = modelViewProjection;
                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(1000, 1, -70);
                piso_o_techo(0, 0,  90, 70, 0, textura_suelo_ladrillos);

                MVProjection = modelViewProjection;
                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(1000, 1, -140);
                piso_o_techo(0, 0,  90, 70, 0, textura_suelo_ladrillos);

                MVProjection = modelViewProjection;
                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(1000, 1, -210);
                piso_o_techo(0, 0,  90, 70, 0, textura_suelo_ladrillos);

                MVProjection = modelViewProjection;
                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(1000, 1, -280);
                piso_o_techo(0, 0,  90, 70, 0, textura_suelo_ladrillos);

                MVProjection = modelViewProjection;
                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(1000, 1, -350);
                piso_o_techo(0, 0,  90, 70, 0, textura_suelo_ladrillos);

            //PEGADA A NAVE 2


                MVProjection = modelViewProjection;
                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(1305, 1, 0);
                piso_o_techo(0, 0,  100, 70, 0, textura_suelo_ladrillos);

                MVProjection = modelViewProjection;
                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(1305, 1, -70);
                piso_o_techo(0, 0,  100, 70, 0, textura_suelo_ladrillos);

                MVProjection = modelViewProjection;
                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(1305, 1, -140);
                piso_o_techo(0, 0,  100, 70, 0, textura_suelo_ladrillos);

                MVProjection = modelViewProjection;
                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(1300, 1, -210);
                piso_o_techo(0, 0,  100, 70, 0, textura_suelo_ladrillos);





           //CRUCE DE NAVE1 A NAVE 2
                MVProjection = modelViewProjection;
                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(1090, 1, -170);
                piso_o_techo(0, 0,  100, 70, 0, textura_suelo_ladrillos);

                MVProjection = modelViewProjection;
                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(1190, 1, -170);
                piso_o_techo(0, 0,  120, 70, 0, textura_suelo_ladrillos);



           //PEGADA A BARDA
                MVProjection = modelViewProjection;
                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(1090, 1, 100);
                piso_o_techo(0, 0,  120, 100, 0, textura_suelo_ladrillos);

                MVProjection = modelViewProjection;
                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(1190, 1, 100);
                piso_o_techo(0, 0,  100, 100, 0, textura_suelo_ladrillos);

                MVProjection = modelViewProjection;
                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(1290, 1, 100);
                piso_o_techo(0, 0,  110, 100, 0, textura_suelo_ladrillos);


        }
        public void suelo_ladrillos2_nave2_dentro (float[] modelViewProjection){
            //OFICINAS
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(200, 1, -1575);
            piso_o_techo(0, 0,  190, 75, 0, textura_suelo_ladrillos);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(390, 1, -1575);
            piso_o_techo(0, 0,  190, 75, 0, textura_suelo_ladrillos);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(480, 1, -1575);
            piso_o_techo(0, 0,  190, 75, 0, textura_suelo_ladrillos);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(670, 1, -1575);
            piso_o_techo(0, 0,  190, 75, 0, textura_suelo_ladrillos);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(860, 1, -1575);
            piso_o_techo(0, 0,  190, 75, 0, textura_suelo_ladrillos);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1050, 1, -1575);
            piso_o_techo(0, 0,  190, 75, 0, textura_suelo_ladrillos);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1240, 1, -1575);
            piso_o_techo(0, 0,  225, 75, 0, textura_suelo_ladrillos);

            //ENTRADA SALIDA NAVE 2
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1365, 1, -1500);
            piso_o_techo(0, 0,  100, 310, 0, textura_suelo_ladrillos);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1365, 1, -1410);
            piso_o_techo(0, 0,  100, 90, 0, textura_suelo_ladrillos);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1365, 1, -1320);
            piso_o_techo(0, 0,  100, 90, 0, textura_suelo_ladrillos);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1365, 1, -1200);
            piso_o_techo(0, 0,  100, 120, 0, textura_suelo_ladrillos);

           //SUELO ENFRENTE LAS OFICINAS
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(500, 1, -990);
            piso_o_techo(0, 0,  190, 45, 0, textura_suelo_ladrillos);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(500, 1, -1165);
            piso_o_techo(0, 0,  100, 180, 0, textura_suelo_ladrillos);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(500, 1, -1095);
            piso_o_techo(0, 0,  100, 70, 0, textura_suelo_ladrillos);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(500, 1, -1025);
            piso_o_techo(0, 0,  100, 70, 0, textura_suelo_ladrillos);

            ///
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(600, 1, -1165);
            piso_o_techo(0, 0,  100, 180, 0, textura_suelo_ladrillos);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(600, 1, -1095);
            piso_o_techo(0, 0,  100, 70, 0, textura_suelo_ladrillos);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(600, 1, -1025);
            piso_o_techo(0, 0,  100, 70, 0, textura_suelo_ladrillos);

            //
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(700, 1, -1165);
            piso_o_techo(0, 0,  100, 180, 0, textura_suelo_ladrillos);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(700, 1, -1095);
            piso_o_techo(0, 0,  100, 70, 0, textura_suelo_ladrillos);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(700, 1, -1025);
            piso_o_techo(0, 0,  100, 70, 0, textura_suelo_ladrillos);

            ///
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(800, 1, -1165);
            piso_o_techo(0, 0,  100, 180, 0, textura_suelo_ladrillos);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(800, 1, -1095);
            piso_o_techo(0, 0,  100, 70, 0, textura_suelo_ladrillos);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(800, 1, -1025);
            piso_o_techo(0, 0,  100, 70, 0, textura_suelo_ladrillos);

            ///
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(900, 1, -1165);
            piso_o_techo(0, 0,  100, 180, 0, textura_suelo_ladrillos);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(900, 1, -1095);
            piso_o_techo(0, 0,  100, 70, 0, textura_suelo_ladrillos);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(900, 1, -1025);
            piso_o_techo(0, 0,  100, 70, 0, textura_suelo_ladrillos);

            //
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1000, 1, -1165);
            piso_o_techo(0, 0,  100, 180, 0, textura_suelo_ladrillos);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1000, 1, -1095);
            piso_o_techo(0, 0,  100, 70, 0, textura_suelo_ladrillos);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1000, 1, -1025);
            piso_o_techo(0, 0,  100, 70, 0, textura_suelo_ladrillos);

            //
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1100, 1, -1165);
            piso_o_techo(0, 0,  100, 180, 0, textura_suelo_ladrillos);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1100, 1, -1095);
            piso_o_techo(0, 0,  100, 70, 0, textura_suelo_ladrillos);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1100, 1, -1025);
            piso_o_techo(0, 0,  100, 70, 0, textura_suelo_ladrillos);

            //
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1200, 1, -1165);
            piso_o_techo(0, 0,  40, 180, 0, textura_suelo_ladrillos);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1200, 1, -1095);
            piso_o_techo(0, 0,  40, 70, 0, textura_suelo_ladrillos);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1200, 1, -1025);
            piso_o_techo(0, 0,  40, 70, 0, textura_suelo_ladrillos);

            //
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1100, 1, -955);
            piso_o_techo(0, 0,  135, 180, 0, textura_suelo_ladrillos);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1100, 1, -885);
            piso_o_techo(0, 0,  135, 70, 0, textura_suelo_ladrillos);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1100, 1, -815);
            piso_o_techo(0, 0,  135, 70, 0, textura_suelo_ladrillos);

            //
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1100, 1, -745);
            piso_o_techo(0, 0,  135, 180, 0, textura_suelo_ladrillos);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1100, 1, -675);
            piso_o_techo(0, 0,  135, 70, 0, textura_suelo_ladrillos);


            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1010, 1, -567);
            piso_o_techo(0, 0,  135, 120, 0, textura_suelo_ladrillos);

            ///A
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1010, 1, -955);
            piso_o_techo(0, 0,  135, 80, 0, textura_suelo_ladrillos);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1020, 1, -885);
            piso_o_techo(0, 0,  135, 70, 0, textura_suelo_ladrillos);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1010, 1, -815);
            piso_o_techo(0, 0,  135, 70, 0, textura_suelo_ladrillos);

            //
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1010, 1, -745);
            piso_o_techo(0, 0,  135, 80, 0, textura_suelo_ladrillos);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1010, 1, -675);
            piso_o_techo(0, 0,  135, 70, 0, textura_suelo_ladrillos);


            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1100, 1, -567);
            piso_o_techo(0, 0,  135, 120, 0, textura_suelo_ladrillos);



            //SUELO EN FRENTE DE TAQUILLA
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1000, 1, -985);
            piso_o_techo(0, 0,  100, 43, 0, textura_suelo_ladrillos);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1000, 1, -985);
            piso_o_techo(0, 0,  100, 43, 0, textura_suelo_ladrillos);
        }
        public void suelo_jardin (float[] modelViewProjection) {

            //Empieza en la sala de lectura

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(270, 1f, 50);
            piso_o_techo(0, 0, 150, 200, 0, textura_jardin);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(270, 1f, -40);
            piso_o_techo(0, 0, 200, 230, 0, textura_jardin);

            ////Pegado a nave 1
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(420, 1f, 50);
            piso_o_techo(0, 0, 200, 200, 0, textura_jardin);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(420, 1f, -40);
            piso_o_techo(0, 0, 200, 230, 0, textura_jardin);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(420, 1f, -160);
            piso_o_techo(0, 0, 190, 160, 0, textura_jardin);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(420, 1f, -300);
            piso_o_techo(0, 0, 190, 160, 0, textura_jardin);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(420, 1f, -450);
            piso_o_techo(0, 0, 120, 160, 0, textura_jardin);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(420, 1f, -530);
            piso_o_techo(0, 0, 120, 280, 0, textura_jardin);

            //
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(270, 1f, -160);
            piso_o_techo(0, 0, 190, 160, 0, textura_jardin);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(310, 1f, -320);
            piso_o_techo(0, 0, 130, 140, 0, textura_arte_jardin);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(270, 1f, -450);
            piso_o_techo(0, 0, 190, 160, 0, textura_jardin);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(270, 1f, -530);
            piso_o_techo(0, 0, 190, 310, 0, textura_jardin);

            //taquilla a sala de lectura
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(70, 1f, -320);
            piso_o_techo(0, 0, 240, 300, 0, textura_jardin);

            ////
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(90, 1f, -1220);
            piso_o_techo(0, 0, 200, 250, 0, textura_jardin);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(270, 1f, -1000);
            piso_o_techo(0, 0, 130, 240, 0, textura_jardin);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(400, 1f, -1000);
            piso_o_techo(0, 0, 130, 240, 0, textura_jardin);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(280, 1f, -1420);
            piso_o_techo(0, 0, 150, 160, 0, textura_jardin);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(280, 1f, -1240);
            piso_o_techo(0, 0, 230, 200, 0, textura_jardin);


            //FRENTE A BIBLIOTECA Y OFICINAS
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(430, 1f, -1400);
            piso_o_techo(0, 0, 270, 140, 0, textura_arte1_jardin);


            ///////
            //PASILLO PARED CRISTAL NAVE 2
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1270, 1f, -300);
            piso_o_techo(0, 0, 100, 60, 0, textura_jardin);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1250, 1f, -350);
            piso_o_techo(0, 0, 150, 150, 0, textura_jardin);

            System.arraycopy(MVProjection, 0, Mdp,0, 16);
            g_modelMatrix_translate(1327, 1f, -270);
            piso_o_techo(0, 0, 70, 100, 0, textura_jardin);

            //NAVE 1 A NAVE 2
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1092, 1f, -240);
            piso_o_techo(0, 0, 130, 140, 0, textura_jardin);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1212, 1f, -240);
            piso_o_techo(0, 0, 130, 140, 0, textura_jardin);

            //NAVE 1 A NAVE 2 BARDA
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1095, 1f, 35);
            piso_o_techo(0, 0, 120, 120, 0, textura_jardin);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1095, 1f, -80);
            piso_o_techo(0, 0, 120, 120, 0, textura_jardin);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1215, 1f, 35);
            piso_o_techo(0, 0, 120, 120, 0, textura_jardin);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1215, 1f, -80);
            piso_o_techo(0, 0, 120, 120, 0, textura_jardin);

        }
        public void taquilla (float[] modelViewProjection) {

            //taquilla
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(50, -0.2, -831);
            pared_X(0, 0, 80, 80, 0, textura_puerta_taquilla);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(-10, -0.2, -830);
            pared_X(0, 0, 220, 120, 0, textura_pared);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(-10, -0.2, -829);
            pared_X(0, 0, 220, 120, 0, textura_pared_interior_nave);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(50, -0.2, -828);
            pared_X(0, 0, 80, 80, 0, textura_puerta_taquilla);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(-10, -0.2, -659);
            pared_X(0, 0, 270, 90, 0, textura_pared_interior_nave);


            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(-10, 1f, -670);
            piso_o_techo(0, 0, 270, 220, 0, textura_suelo_madera);


            //sala con piano

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(50, -0.2, -930);
            pared_X(0, 0, 80, 80, 0, textura_puerta_taquilla);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(-10, -0.2, -931);
            pared_X(0, 0, 220, 120, 0, textura_pared);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(-10, -0.2, -932);
            pared_X(0, 0, 220, 120, 0, textura_pared_interior_nave);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(50, -0.2, -833);
            pared_X(0, 0, 80, 80, 0, textura_puerta_taquilla);



            //tunel
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(210, -0.2, -650);
            pared_Z(0, 0, 510, 180, 0, textura_puerta_dentro_taquilla);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(10, -0.2, -1160);
            pared_X(0, 0, 200, 140, 0, textura_pared);

            //ventana lateral
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(50, -0.2, -655);
            pared_X(0, 0, 70, 90, 0,textura_pared_lateral_taquilla );

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(10, -0.2, -656);
            pared_X(0, 0, 200, 100, 0, textura_pared);




        }
        public void biblioteca (float[] modelViewProjection) {
            //Puerta
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(50, -0.2, -1500);
            pared_X(0, 0, 95, 100, 0, textura_puerta_biblioteca);

            //Pared
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(0, -0.2, -1500);
            pared_X(0, 0, 200, 100, 0, textura_pared);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(200, -0.2, -1490);
            pared_Z(0, 0, 410, 100, 0, textura_pared);

            //suelo
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1, 1f, -1500);
            piso_o_techo(0, 0,  200, 400, 0, textura_suelo_madera);

            //Techo
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1, 100, -1500);
            piso_o_techo(0, 0,  200, 400, 0, textura_techo_madera_luces);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1, 102, -1500);
            piso_o_techo(0, 0,  200, 400, 0, textura_techo_exterior);




            //INTERIOR
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(50, -0.2f, -1501);
            pared_X(0, 0, 95, 70, 0, textura_puerta_interior_biblioteca);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(0, -0.1f, -1501);
            pared_X(0, 0, 200, 100, 0, textura_pared_interior_nave);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(0, -0.1f, -1890);
            pared_X(0, 0, 200, 100, 0, textura_interior3_biblioteca);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(10, -0.2f, -1601);
            pared_Z(0, 0, 230, 100, 0, textura_interior1_biblioteca);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(9, -0.2f, -1527);
            pared_Z(0, 0, 360, 100, 0, textura_pared_interior_nave);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(199, -0.2f, -1530);
            pared_Z(0, 0, 350, 100, 0, textura_interior2_biblioteca);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(199, -0.2f, -1497);
            pared_Z(0, 0, 400, 100, 0, textura_pared_interior_nave);
        }
        public void oficinas (float[] modelViewProjection){

            //anuncio
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(450, -0.2, -1649);
            pared_X(0, 0, 70, 60, 0, textura_arte_oficina);

            //maceta
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(640, -0.2, -1645);
            pared_X(0, 0, 20, 36, 0, textura_maceta_oficina);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(800, -0.2, -1645);
            pared_X(0, 0, 20, 36, 0, textura_maceta1_oficina);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(910, -0.2, -1645);
            pared_X(0, 0, 20, 36, 0, textura_maceta_oficina);

            //Puerta
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(650, -0.2, -1650);
            pared_X(0, 0, 120, 100, 0, textura_puerta_biblioteca);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(800, -0.2, -1650);
            pared_X(0, 0, 125, 80, 0, textura_puerta_oficina);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(940, -0.2, -1650);
            pared_X(0, 0, 120, 100, 0, textura_puerta_biblioteca);

            //Pared bano izq
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(290, -0.2, -1650);
            pared_X(0, 0, 120, 120, 0, textura_pared);

            //pared oficina
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(520, -0.2, -1650);
            pared_X(0, 0, 570, 120, 0, textura_pared);

            //Pared bano der
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1175, -0.2, -1650);
            pared_X(0, 0, 120, 120, 0, textura_pared);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(200, -0.2, -1650);
            pared_X(0, 0, 100, 90, 0, textura_bano_izq);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1300, -0.2, -1650);
            pared_X(0, 0, 100, 90, 0, textura_bano_der);
        }
        public void saladelectura(float[] modelViewProjection) {


            //puerta

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(50, -0.2, -301);
            pared_X(0, 0, 80, 70, 0, textura_puerta_sala);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(10, -0.2, -300);
            pared_X(0, 0, 200, 120, 0, textura_pared);

            //Pared
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(210, -0.2, 100);
            pared_Z(0, 0, 397, 120, 0, textura_ventanas_afuera);


            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(-10, 1f, 100);
            piso_o_techo(0, 0, 270, 400, 0, textura_suelo_madera);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(-10, 119f, 100);
            piso_o_techo(0, 0, 270, 400, 0, textura_techo_exterior);



        }
        public void nave1 (float[] modelViewProjection) {

            //Puerta entrada exterior anterior hacia sala de lectura
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(699, -0.2f, -120);
            pared_Z(0, 0, 152, 95, 0,  textura_puerta_principal_nave1);


            //Pared hacia sala de lectura
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(700, 1f, 90);
            pared_Z(0, 0, 550, 200, 0, textura_pared);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(700, 1f, -640);
            pared_Z(0, 0, 390, 200, 0,textura_pared_cristal_nave1);


            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(640, 1f, -460);
            pared_X(0, 0, 60, 200, 0, textura_pared);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(640, 20f, -640);
            pared_X(0, 0, 57, 100, 0, textura_pared_lateral_taquilla);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(640, 1f, -640);
            pared_X(0, 0, 60, 200, 0, textura_pared);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(640, 1f, -460);
            pared_Z(0, 0, 180, 200, 0, textura_pared);

            //puerta entrada nave 1 hacia oficinas
                //puerta 1
                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(740, -0.2f, -1030);
                pared_X(0, 0, 150, 145, 0, textura_pared2_nave1);

                //puerta 2
                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(840, -0.2f, -1030);
                pared_X(0, 0, 140, 145, 0, textura_pared2_nave1);

                //pared
                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(690, -0.2f, -1030);
                pared_X(0, 0, 320, 200, 0, textura_pared);


                //puerta salida de nave 1
                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(1001, -0.2f, -120);
                pared_Z(0, 0, 152, 95, 0,  textura_puerta_principal_nave1);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(1001, 16f, -270);
                pared_Z(0, 0, 70, 100, 0,  textura_pared_lateral_taquilla);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(1000, 1f, 50);
                pared_Z(0, 0, 470, 200, 0, textura_pared);

                //pared lateral

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(1000, 1f, -409);
                pared_X(0, 0, 87, 110, 0, textura_pared_lateral_taquilla);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(1000, 1f, -410);
                pared_X(0, 0, 100, 200, 0, textura_pared);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(1099, 1f, -410);
                pared_Z(0, 0, 166, 200, 0, textura_pared);

                    //pared hacia cristal
                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(1010, 1f, -580);
                pared_X(0, 0, 87, 200, 0, textura_pared);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(1000, 1f, -581);
                pared_X(0, 0, 87, 110, 0, textura_pared_lateral_taquilla);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(1010, 1f, -580);
                pared_Z(0, 0, 60, 200, 0, textura_pared);

                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(1010, 1f, -625);
                pared_Z(0, 0, 407, 200, 0,textura_pared_cristal_nave1);

                //PARED ESTACIONAMIENTO
                System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                g_modelMatrix_translate(1710, 1f, 83);
                pared_Z(0, 0, 1800, 200, 0, textura_pared);

                    //pared interior
                    System.arraycopy(MVProjection, 0, Mdp, 0, 16);
                    g_modelMatrix_translate(1708, 1f, 83);
                    pared_Z(0, 0, 1800, 200, 0, textura_pared_interior_nave);



        }
        public void pared_nave1 (float[] modelViewProjection) {

            //puerta interior antigua
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(702, -0.2f, -120);
            pared_Z(0, 0, 132, 95, 0,  textura_puerta_interior_nave1);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(702, -0.2f, 40);
            pared_Z(0, 0, 500, 200, 0,  textura_pared_interior_nave);

            //puerta interior antigua
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(999, -0.2f, -120);
            pared_Z(0, 0, 132, 95, 0,  textura_puerta_interior_nave1);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(999, -0.2f, 40);
            pared_Z(0, 0, 470, 180, 0,  textura_pared_interior_nave);



        }
        public void suelo_nave1 (float[] modelViewProjection) {

            MVProjection = modelViewProjection;
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(640, 1f, -460);
            piso_o_techo(0, 0,  60, 179, 0, textura_suelo_nave1);

            MVProjection = modelViewProjection;
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(700, 1f, 40);
            piso_o_techo(0, 0,  300, 1070, 0, textura_suelo_nave1);

            MVProjection = modelViewProjection;
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1000, 1f, -410);
            piso_o_techo(0, 0,  90, 170, 0, textura_suelo_nave1);

            MVProjection = modelViewProjection;
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1000, 1f, -578);
            piso_o_techo(0, 0,  10, 440, 0, textura_suelo_nave1);


        }
        public void nave2 (float[] modelViewProjection) {

            //Puerta grande nave 2 a nave 1
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1469, 1f,-120);
            pared_Z(0, 0, 152, 127, 0, textura_puerta_principal_nave1);

            //Pared lateral pegada a barda puerta principal
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1470, 1f, 93);
            pared_Z(0, 0, 423, 200, 0, textura_pared);


            //PARED CRISTAL PUERTA NAVE 2
            //pared cristal lateral puerta oficinas
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1400, 1f, -640);
            pared_X(0, 0, 70, 200, 0, getTextura_pared_cristal_nave2);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1400, 1f, -330);
            pared_Z(0, 0, 310, 200, 0,textura_pared_cristal_nave1);

            //pared lateral puerta nave 1 a nave 2
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1400, 1f, -330);
            pared_X(0, 0, 70, 200, 0, getTextura_pared_cristal_nave2);


            //pared cristal lateral puerta oficinas
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1400, 1f, -1200);
            pared_X(0, 0, 70, 200, 0, getTextura_pared_cristal_nave2);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1400, 1f, -790);
            pared_Z(0, 0, 420, 200, 0,textura_pared_cristal_nave1);

            //pared lateral puerta nave 1 a nave 2
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1400, 1f, -805);
            pared_X(0, 0, 70, 200, 0, getTextura_pared_cristal_nave2);

            //PARED ENTRE CRISTALES NAVE 2
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1470, 1f, -640);
            pared_Z(0, 0, 170, 200, 0,textura_pared);

            //Puerta del lado de las oficinas
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1469, 9f, -1455);
            pared_Z(0, 0, 150, 130, 0, textura_pared2_nave1);

            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1470, 1f, -1200);
            pared_Z(0, 0, 550, 200, 0, textura_pared);

            //pared estacionamiento


        }

        public void suelo_nave2 (float[] modelViewProjection) {

            //dentro
            MVProjection = modelViewProjection;
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1470, 1, 100);
            piso_o_techo(0, 0,  240, 250, 0, textura_suelo_nave1);

            MVProjection = modelViewProjection;
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1470, 1, -120);
            piso_o_techo(0, 0,  240, 207, 0, textura_suelo_nave1);

            MVProjection = modelViewProjection;
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1400, 1, -306);
            piso_o_techo(0, 0,  307, 332, 0, textura_suelo_nave1);


            MVProjection = modelViewProjection;
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1470, 1, -638);
            piso_o_techo(0, 0,  240, 169, 0, textura_suelo_nave1);

            MVProjection = modelViewProjection;
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1400, 1, -806);
            piso_o_techo(0, 0,  307, 400, 0, textura_suelo_nave1);

            MVProjection = modelViewProjection;
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1470, 1, -1200);
            piso_o_techo(0, 0,  240, 169, 0, textura_suelo_nave1);

            MVProjection = modelViewProjection;
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(1470, 1, -1300);
            piso_o_techo(0, 0,  240, 169, 0, textura_suelo_nave1);




            MVProjection = modelViewProjection;
            System.arraycopy(MVProjection, 0, Mdp, 0, 16);
            g_modelMatrix_translate(0, 1, -1510);
            piso_o_techo(0, 0,  80, 80, 0, textura_suelo_ladrillos);
        }



    //Para no escribir todo de nuevo
    private void g_modelMatrix_translate(double x, double y, double z){
        Matrix.translateM(Mdp, 0, (float)x, (float)y, (float)z);
    }

    private void g_modelMatrix_rotate(double angulo, double x, double y, double z){
        Matrix.rotateM(Mdp, 0, (float)angulo, (float)x, (float)y ,(float)z);
    }
}