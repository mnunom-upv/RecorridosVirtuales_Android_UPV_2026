package upv.pm.may_ago_26.iti_271503.pgu2_eq05;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.microedition.khronos.egl.EGLConfig;

public class CubeRenderer implements CardboardView.StereoRenderer {

    /** matrices necesarias **/
    private float[] inicioMatrix;
    private float[] g_viewMatrix;
    private float[] g_projectionMatrix;
    private float[] g_mvpMatrix;
    private float[] g_modelMatrix;

    /** metodos para hacer push y pop Matrix */
    Stack<float[]> pila = new Stack<>();
    public void pushMatrix(float [] m){
        float[] m2 = new float[16];
        System.arraycopy(m,0,m2,0,16);
        pila.push(m2);
    }
    public float[] popMatrix(){
        return pila.pop();
    }

    private float x = 0f;
    private float y = 0f;
    private float z = -5f;
    private float angleX = 0f;
    private float angleY = 0f;

    float RotatedX;
    float RotatedY;
    float RotatedZ;

    float pitch = 0f;

    public float[] Position;
    public float[] ViewDir;
    public float[] RightVector;
    public float[] UpVector;

    /** variables para la vista dual **/
    public static float screenWidth;
    public static float screenHeight;

    /** buffers para las posiciones y texturas **/
    private FloatBuffer mPositions;
    private FloatBuffer mTextureCoordinates;


    /** variable para pasar la matriz de transformacion */
    private int mMvpMatrixHandle;

    /** variable para pasar las texturas */
    private int mTextureUniformHandle;

    /** variable para pasar la informacion de la posicion del modelo */
    private int mPositionHandle;

    /** variable para pasar la informacion de las cordenadas del modelo de textura */
    private int mTextureCoordinateHandle;

    /** how many bytes per float. */
    private final int mBytesPerFloat = 4;

    /** Size of the position data in elements. */
    private final int mPositionDataSize = 3;

    /** Size of the texture coordinate data in elements. */
    private final int mTextureCoordinateDataSize = 2;

    /** handle para el shading del cubo. **/
    private int mProgramHandle;


    /** the textures */
    private int tx_minecraft_grass;
    private int tx_cielo_techo;
    private int tx_pavimento;
    private int tx_bordillo_banqueta;
    private int tx_mosaico_gris;
    private int tx_pared_guinda;
    private int tx_barras;
    private int tx_pared_blanca;
    private int tx_ladrillo;
    private int tx_ventana;
    private int tx_techo_fachada;
    private  int tx_puerta;
    private int tx_techo;
    private int tx_arbol;
    private int tx_fuente;
    private int tx_banca;

    /** modelos **/
    private Piso piso;
    private Piso techo;
    private Calle calle;
    private Banqueta banqueta;
    private Piso mosaico_gris;
    private List<Jardin> jardines;
    private List<Pared> fondo;
    private FachadaPrincipal fachadaPrincipal;
    private EsqueletoEdificio esqueleto;
    private Porton porton;

    private List<Ventana> arboles;

    private int gll = 0;
    private int u_MvpMatrix = 0;

    private final int LADO_Z = 5000;
    private final int LADO_X = 5000;
    private final int ALTURA_TABLERO = 2000;
    private final int CENTRO_TABLERO = LADO_X / 2;
    float PI = (float) Math.PI;
    float PIdiv180 = PI / 180.0f;

    private final Context mActivityContext;

    public CubeRenderer(final Context activityContext) {
        mActivityContext = activityContext;

        // matrices
        inicioMatrix = new float[16];
        g_modelMatrix = new float[16];
        g_mvpMatrix = new float[16];
        g_viewMatrix = new float[16];
        g_projectionMatrix = new float[16];

        RotatedX = 0.0f;
        RotatedY = 0.0f;
        RotatedZ = 0.0f;

        Position = new float[3];
        Position[0] = 0.0f;
        Position[1] = 0.0f;
        Position[2] = 0.0f;

        ViewDir = new float[3];
        ViewDir[0] = 0.0f;
        ViewDir[1] = 0.0f;
        ViewDir[2] = -1.0f;

        RightVector =  new float[3];
        RightVector[0] = 1.0f;
        RightVector[1] = 0.0f;
        RightVector[2] = 0.0f;

        UpVector =  new float[3];
        UpVector[0] = 0.0f;
        UpVector[1] = 1.0f;
        UpVector[2] = 0.0f;

        MoveForward(-200.0f);
        MoveUpward(10.0f);
        StrafeRight(CENTRO_TABLERO);

    }

    @Override
    public void onSurfaceCreated(EGLConfig config) {

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glClearColor(0f, 1f, 0f, 1.0f);

        // cargar codigo fuente d los shaders (archivos raw)
        final String vertexShader = RawResourceReader.readTextFileFromRawResource(mActivityContext, R.raw.per_pixel_vertex_shader_tex_and_light);
        final String fragmentShader = RawResourceReader.readTextFileFromRawResource(mActivityContext, R.raw.per_pixel_fragment_shader_tex_and_light);

        // compila el código fuente de los shaders, devolviendo un identificador para usar en OpenGL.
        final int vertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);
        final int fragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);

        // crea y enlaza un programa OpenGL con ambos shaders compilados
        // se vinculan los 3 atributos a indices para usar en el codigo
        mProgramHandle = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle,
                new String[] {"a_Position", "a_TexCoord",  "a_Color"});

        tx_minecraft_grass = generarTextura(R.drawable.minecraft_grass, true);
        tx_cielo_techo = generarTextura(R.drawable.cielo, true);
        tx_pavimento = generarTextura(R.drawable.pavimento2, true);
        tx_bordillo_banqueta = generarTextura(R.drawable.amarillo, true);
        tx_mosaico_gris = generarTextura(R.drawable.mosaico_gris2, true);
        tx_pared_guinda = generarTextura(R.drawable.pared_guinda, true);
        tx_barras = generarTextura(R.drawable.barras, true);
        tx_pared_blanca = generarTextura(R.drawable.blanco, true);
        tx_ladrillo = generarTextura(R.drawable.ladrillos, true);
        tx_ventana = generarTextura(R.drawable.ventana, false);
        tx_techo_fachada = generarTextura(R.drawable.techo_fachada, false);
        tx_puerta = generarTextura(R.drawable.puerta,false);
        tx_techo = generarTextura(R.drawable.techo, true);
        tx_arbol = generarTextura(R.drawable.arbol, false);
        tx_fuente = generarTextura(R.drawable.fuente, false);
        tx_banca =  generarTextura(R.drawable.banca, false);

        Matrix.setIdentityM(g_mvpMatrix, 0);
        Matrix.setIdentityM(g_viewMatrix, 0);
        Matrix.setIdentityM(g_projectionMatrix, 0);

    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        // Set the OpenGL viewport to the same size as the surface.
        GLES20.glViewport(0, 0, width, height);
        screenHeight = height;
        screenWidth = width;

        // Create a new perspective projection matrix. The height will stay the same
        // while the width will vary as per aspect ratio.
        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 5000.0f;

        Matrix.frustumM(g_projectionMatrix, 0, left, right, bottom, top, near, far);
    }

    @Override
    public void onNewFrame(HeadTransform headTransform) {
        // Se llama antes de dibujar el frame, no requiere operaciones
    }

    @Override
    public void onDrawEye(Eye eye) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Set our per-vertex lighting program.
        GLES20.glUseProgram(mProgramHandle);

        // Set program handles for drawing.
        mMvpMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MvpMatrix");
        mTextureUniformHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_Sampler");
        mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position");
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_TexCoord");

        // Actualizar la matriz de vista en base al movimiento del joystick
        actualizar_vista();

        // Combinar la matriz del ojo (sensor/head tracking) con el movimiento del joystick:
        // eyeViewMatrix = eye.getEyeView() * g_viewMatrix
        float[] eyeViewMatrix = new float[16];
        Matrix.multiplyMM(eyeViewMatrix, 0, eye.getEyeView(), 0, g_viewMatrix, 0);

        // Obtener la matriz de perspectiva calibrada por el Cardboard
        float[] perspective = eye.getPerspective(1.0f, 5000.0f);

        // Calcular la matriz de Vista-Proyección final (inicioMatrix)
        Matrix.multiplyMM(g_modelMatrix, 0, perspective, 0, eyeViewMatrix, 0);
        System.arraycopy(g_modelMatrix, 0, inicioMatrix, 0, 16);

        // Dibujar modelos de la Casa del Arte
        piso();
        cielo_techo();
        laterales();
        banqueta();
        piso_casa();
        fachada_principal();
        esqueleto_edificio();
        jardines();
        porton();
    }

    @Override
    public void onFinishFrame(Viewport viewport) {
        // Nada que hacer aquí
    }

    @Override
    public void onRendererShutdown() {
        Log.i("CubeRenderer", "onRendererShutdown");
    }


    private int generarTextura(int textura, boolean repetir){

        // llama al helper y elige si repetir o no la textura
        int t = TextureHelper.loadTexture(mActivityContext, textura, repetir);
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

        return t;
    }


    public void MoveForward(float Distance) {
        float[] horizontalDir = new float[]{ViewDir[0], 0.0f, ViewDir[2]};
        horizontalDir = normalize(horizontalDir);
        if (horizontalDir[0] != 0.0f || horizontalDir[2] != 0.0f) {
            Position = suma(Position, multiplicacion(horizontalDir, -Distance));
        }
    }

    public void MoveUpward( float Distance ) {
        float nuevaY = Position[1] + UpVector[1] * Distance;

        // limitar hacia abajo
        if (nuevaY < 30.0f) {
            Distance = 30.0f - Position[1]; // solo mueve hasta el piso
        }

        // limitar hacia arriba
        if (nuevaY > 490.0f) {
            Distance = 490.0f - Position[1]; // solo mueve hasta el techo
        }

        Position = suma(Position, multiplicacion(UpVector, Distance));
    }

    public void StrafeRight(float Distance) {
        float[] horizontalRight = new float[]{RightVector[0], 0.0f, RightVector[2]};
        horizontalRight = normalize(horizontalRight);
        if (horizontalRight[0] != 0.0f || horizontalRight[2] != 0.0f) {
            Position = suma(Position, multiplicacion(horizontalRight, Distance));
        }
    }

    public void RotateX(float Angle) {

        // actualizar pitch con límites para evitar giros raros segun
        float newPitch = pitch + Angle;
        newPitch = Math.max(-89f, Math.min(89f, newPitch));
        float clampedAngle = newPitch - pitch;
        pitch = newPitch;

        RotatedX = clampedAngle;

        float rad = clampedAngle * PIdiv180;

        // rotar ViewDir alrededor de RightVector (vertical movement)
        ViewDir = normalize(suma(multiplicacion(ViewDir, (float)Math.cos(rad)), multiplicacion(UpVector, (float)Math.sin(rad))));

        // recalcular RightVector y UpVector
        RightVector = normalize(crossProduct(ViewDir, UpVector));
        UpVector = normalize(crossProduct(RightVector, ViewDir));
    }

    public void RotateY(float Angle) {
        RotatedY = Angle;
        float rad = Angle * PIdiv180;

        // rotar ViewDir alrededor de UpVector (horizontal movement)
        ViewDir = normalize(suma(multiplicacion(ViewDir, (float)Math.cos(rad)),multiplicacion(RightVector, (float)Math.sin(rad) * -1f)));

        // recalcular RightVector
        RightVector = normalize(crossProduct(ViewDir, UpVector));
        UpVector = normalize(crossProduct(RightVector, ViewDir));
    }


    // operaciones para las matrices
    private float[] crossProduct (float[] u, float[] v) {
        float[] resVector = new float[3];
        resVector[0] = u[1]*v[2] - u[2]*v[1];
        resVector[1] = u[2]*v[0] - u[0]*v[2];
        resVector[2] = u[0]*v[1] - u[1]*v[0];

        return resVector;
    }

    float GetF3dVectorLength(float[] v) {
        return (float)(Math.sqrt(v[0]*v[0]+v[1]*v[1]+v[2]*v[2]));
    }
    private float[] normalize( float[] v) {
        float[] res = new float[3];
        float l = GetF3dVectorLength(v);
        if (l == 0.0f){
            res[0] = 0.0f;
            res[1] = 0.0f;
            res[2] = 0.0f;
        }else {
            res[0] = v[0] / l;
            res[1] = v[1] / l;
            res[2] = v[2] / l;
        }
        return res;
    }

    private float[] multiplicacion(float[] v,float r){
        float[] res = new float[3];
        res[0] = v[0]*r;
        res[1] = v[1]*r;
        res[2] = v[2]*r;
        return res;
    }

    private float[] suma(float[] v,float [] u){
        float[] res = new float[3];
        res[0] = v[0]+u[0];
        res[1] = v[1]+u[1];
        res[2] = v[2]+u[2];
        return res;
    }

    private float[] resta(float[] v,float [] u){
        float[] res = new float[3];
        res[0] = v[0]-u[0];
        res[1] = v[1]-u[1];
        res[2] = v[2]-u[2];
        return res;
    }

    public void reiniciarPosiciones() {
        RotatedX = 0.0f;
        RotatedY = 0.0f;
        RotatedZ = 0.0f;

        Position = new float[3];
        Position[0] = 0.0f;
        Position[1] = 4.0f;
        Position[2] = 0.0f;

        ViewDir = new float[3];
        ViewDir[0] = 0.0f;
        ViewDir[1] = 0.0f;
        ViewDir[2] = -1.0f;

        RightVector =  new float[3];
        RightVector[0] = 1.0f;
        RightVector[1] = 0.0f;
        RightVector[2] = 0.0f;

        UpVector =  new float[3];
        UpVector[0] = 0.0f;
        UpVector[1] = 1.0f;
        UpVector[2] = 0.0f;

        MoveForward(-200.0f);
        MoveUpward(10.0f);
        StrafeRight(CENTRO_TABLERO);

    }

    public void actualizar_vista() {
        // límites del perímetro AUN NO RESUELVO PARA X Y Z pero al menos Y si sirve
        float minX = -LADO_X;
        float maxX =  LADO_X;
        float minY = 20f;
        float maxY =  ALTURA_TABLERO;
        float minZ = -LADO_Z;
        float maxZ =  LADO_Z;

        // limitar posición de cámara
        if (Position[0] < minX) Position[0] = minX;
        if (Position[0] > maxX) Position[0] = maxX;
        if (Position[1] < minY) Position[1] = minY;
        if (Position[1] > maxY) Position[1] = maxY;
        if (Position[2] < minZ) Position[2] = minZ;
        if (Position[2] > maxZ) Position[2] = maxZ;

        // calcular punto de vista
        float[] ViewPoint = suma(Position, ViewDir);

        // actualizar vista
        Matrix.setLookAtM(
                g_viewMatrix,
                0,
                Position[0], Position[1], Position[2],
                ViewPoint[0], ViewPoint[1], ViewPoint[2], // hacia dónde mira
                UpVector[0], UpVector[1], UpVector[2]
        );
    }


    // simplificar posicionamiento en escena
    private void g_modelMatrix_translate(double x, double y, double z) {
        Matrix.translateM(g_modelMatrix, 0, (float)x, (float)y, (float)z);
    }

    private void g_modelMatrix_rotate(double angulo, double x, double y, double z) {
        Matrix.rotateM(g_modelMatrix, 0, (float)angulo, (float)x, (float)y ,(float)z);
    }

    // dibuja el modelo del techo (cielo)
    private void cielo_techo() {

        if (techo == null) techo = new Piso(tx_cielo_techo, LADO_X, LADO_Z);

        pushMatrix(g_modelMatrix);
        g_modelMatrix_translate(x, ALTURA_TABLERO, z);
        techo.draw(g_modelMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);
        g_modelMatrix = popMatrix();

    }

    // dibuja las 4 paredes que dan a la calle
    private void laterales() {

        if (fondo == null) {
            fondo = new ArrayList<>();

            // pared frontal
            fondo.add(new Pared(tx_cielo_techo, LADO_X, ALTURA_TABLERO));
            // pared trasera
            fondo.add(new Pared(tx_cielo_techo, LADO_X, ALTURA_TABLERO));
            // pared lateral izquierda
            fondo.add(new Pared(tx_cielo_techo, LADO_X, ALTURA_TABLERO));
            // pared lateral derecha
            fondo.add(new Pared(tx_cielo_techo, LADO_X, ALTURA_TABLERO));
        }

        int i = 0;

        // enfrente
        pushMatrix(g_modelMatrix);
        g_modelMatrix_translate(x, 0f, z);
        fondo.get(i++).draw(g_modelMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);
        g_modelMatrix = popMatrix();

        // atras
        pushMatrix(g_modelMatrix);
        g_modelMatrix_translate(x, 0f, z - LADO_Z);
        fondo.get(i++).draw(g_modelMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);
        g_modelMatrix = popMatrix();

        // izq
        pushMatrix(g_modelMatrix);
        g_modelMatrix_translate(x + LADO_X, 0f, z);
        g_modelMatrix_rotate(90, 0, 1, 0);  // rotación para paredes laterales
        fondo.get(i++).draw(g_modelMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);
        g_modelMatrix = popMatrix();

        // der
        pushMatrix(g_modelMatrix);
        g_modelMatrix_translate(x, 0f, z);
        g_modelMatrix_rotate(90, 0, 1, 0);  // rotación para paredes laterales
        fondo.get(i++).draw(g_modelMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);
        g_modelMatrix = popMatrix();

    }

    // dibuja el modelo del piso (cesped)
    private void piso() {

        if (piso == null) piso = new Piso(tx_pavimento, LADO_X, LADO_Z);
        if (calle == null) calle = new Calle(tx_pavimento, LADO_X, LADO_Z, 250);

        // dibujar el pasto
        pushMatrix(g_modelMatrix);
        g_modelMatrix_translate(x, -0.10f, z);
        piso.draw(g_modelMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);
        g_modelMatrix = popMatrix();

        // dibujar la calle un poco por encima del pasto
        pushMatrix(g_modelMatrix);
        g_modelMatrix_translate(x, 0.01f, z); // ligeramente más arriba
        calle.draw(g_modelMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);
        g_modelMatrix = popMatrix();

    }

    // dibujar el modelo de la banqueta
    private void banqueta() {

        if (banqueta == null) banqueta = new Banqueta(tx_pavimento, tx_bordillo_banqueta, LADO_X, LADO_Z, 10, 250, 100);

        pushMatrix(g_modelMatrix);
        g_modelMatrix_translate(x + 250, -0.1, z - 250); // alineado con el hueco
        banqueta.draw(g_modelMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);
        g_modelMatrix = popMatrix();
    }

    // dibujar el modelo de los mosaicos o sea el piso dentro d la casa del arte
    private void piso_casa() {

        if (mosaico_gris == null) mosaico_gris = new Piso(tx_mosaico_gris, LADO_X - 350 * 2, LADO_Z - 350 * 2);

        pushMatrix(g_modelMatrix);
        g_modelMatrix_translate(x + 250 + 100, 10, z - 250 - 100);
        mosaico_gris.draw(g_modelMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);
        g_modelMatrix = popMatrix();

    }

    // dibujar modelos d los pequenos jardines q hay por ahi
    private void jardines() {

        float jardinAncho_chico = 250;
        float jardinAncho_grande = 1000;

        if (jardines == null) {
            jardines = new ArrayList<>();

            jardines.add(new Jardin(tx_minecraft_grass, jardinAncho_chico, 100)); // jardín 1
            jardines.add(new Jardin(tx_minecraft_grass, jardinAncho_chico, 100)); // jardín 2
            jardines.add(new Jardin(tx_minecraft_grass, jardinAncho_grande + 650, 100)); // jardín 3
            jardines.add(new Jardin(tx_minecraft_grass, jardinAncho_grande + 650, 100)); // jardín 4

            // jardines entrada escaleras
            jardines.add(new Jardin(tx_minecraft_grass, jardinAncho_chico*2 -200, 100));
            jardines.add(new Jardin(tx_minecraft_grass, jardinAncho_chico*2 -200, 100));

            // jardines parque central
            jardines.add(new Jardin(tx_minecraft_grass, 900, 900));
            jardines.add(new Jardin(tx_minecraft_grass, 900, 900));
            jardines.add(new Jardin(tx_minecraft_grass, 900, 900));
            jardines.add(new Jardin(tx_minecraft_grass, 900, 900));
        }

        if (arboles == null) {
            arboles = new ArrayList<>();

            arboles.add(new Ventana(tx_arbol, 1100, 823, 0.8f, 0.8f));
            arboles.add(new Ventana(tx_arbol, 1100, 823, 0.8f, 0.8f));

            arboles.add(new Ventana(tx_arbol, 1100, 823, 0.8f, 0.8f));
            arboles.add(new Ventana(tx_arbol, 1100, 823, 0.8f, 0.8f));
            arboles.add(new Ventana(tx_arbol, 1100, 823, 0.8f, 0.8f));
            arboles.add(new Ventana(tx_arbol, 1100, 823, 0.8f, 0.8f));

            // fuente pero pasarla como arbol
            arboles.add(new Ventana(tx_fuente, 640, 640, 0.5f, 0.5f));

            // banca
            arboles.add(new Ventana(tx_banca, 360, 192, 0.6f, 0.6f));
            arboles.add(new Ventana(tx_banca, 360, 192, 0.6f, 0.6f));
            // banca parque central
            arboles.add(new Ventana(tx_banca, 360, 192, 0.6f, 0.6f));
            arboles.add(new Ventana(tx_banca, 360, 192, 0.6f, 0.6f));
            arboles.add(new Ventana(tx_banca, 360, 192, 0.6f, 0.6f));
            arboles.add(new Ventana(tx_banca, 360, 192, 0.6f, 0.6f));
        }

        int i = 0;
        float espacio = 100f;

        int anchez = 100;

        float x1 = CENTRO_TABLERO - (jardinAncho_chico + espacio);
        float x2 = CENTRO_TABLERO + espacio;
        float x3 = x1 - (jardinAncho_grande + 650 + espacio);
        float x4 = x2 + (jardinAncho_chico + espacio);

        float zJardines_principal = -250 - 100 - 50;

        // Jardín 1
        pushMatrix(g_modelMatrix);
        g_modelMatrix_translate(x1, 10.2f, zJardines_principal);
        jardines.get(i++).draw(g_modelMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);
        g_modelMatrix = popMatrix();

        // Jardín 2
        pushMatrix(g_modelMatrix);
        g_modelMatrix_translate(x2, 10.2f, zJardines_principal);
        jardines.get(i++).draw(g_modelMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);
        g_modelMatrix = popMatrix();

        // Jardín 3
        pushMatrix(g_modelMatrix);
        g_modelMatrix_translate(x3, 10.2f, zJardines_principal);
        jardines.get(i++).draw(g_modelMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);
        g_modelMatrix = popMatrix();

        // Jardín 4
        pushMatrix(g_modelMatrix);
        g_modelMatrix_translate(x4, 10.2f, zJardines_principal);
        jardines.get(i++).draw(g_modelMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);
        g_modelMatrix = popMatrix();

        // jardines entrada
        pushMatrix(g_modelMatrix);
        g_modelMatrix_translate(x1 - jardinAncho_chico + 50, 10.2f, zJardines_principal - jardinAncho_chico/2 - 25);
        jardines.get(i++).draw(g_modelMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);
        g_modelMatrix = popMatrix();

        pushMatrix(g_modelMatrix);
        g_modelMatrix_translate(x2 + 150, 10.2f, zJardines_principal - jardinAncho_chico/2 - 25);
        jardines.get(i++).draw(g_modelMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);
        g_modelMatrix = popMatrix();

        // Jardín central 1 izq enfrente
        pushMatrix(g_modelMatrix);
        g_modelMatrix_translate(CENTRO_TABLERO - jardinAncho_grande - anchez - 150, 10.5f, - CENTRO_TABLERO + jardinAncho_grande - anchez);
        jardines.get(i++).draw(g_modelMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);
        g_modelMatrix = popMatrix();

        // Jardín central 2 izq atras
        pushMatrix(g_modelMatrix);
        g_modelMatrix_translate(CENTRO_TABLERO - jardinAncho_grande - anchez - 150, 10.5f, - CENTRO_TABLERO - jardinAncho_grande + anchez*7);
        jardines.get(i++).draw(g_modelMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);
        g_modelMatrix = popMatrix();

        // Jardín central 3 der enfrente
        pushMatrix(g_modelMatrix);
        g_modelMatrix_translate(CENTRO_TABLERO + 250 + anchez, 10.5f, - CENTRO_TABLERO + jardinAncho_grande - anchez);
        jardines.get(i++).draw(g_modelMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);
        g_modelMatrix = popMatrix();

        // Jardín central 4 der atras
        pushMatrix(g_modelMatrix);
        g_modelMatrix_translate(CENTRO_TABLERO + 250 + anchez, 10.5f, - CENTRO_TABLERO - jardinAncho_grande + anchez*7);
        jardines.get(i++).draw(g_modelMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);
        g_modelMatrix = popMatrix();

        int k = 0;

        // arboles jardines enfrente
        pushMatrix(g_modelMatrix);
        g_modelMatrix_translate(x1 - jardinAncho_chico + 50 - 800, -15.2f, zJardines_principal - jardinAncho_chico/2 + 100);
        arboles.get(k++).draw(g_modelMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);
        g_modelMatrix = popMatrix();
        pushMatrix(g_modelMatrix);
        g_modelMatrix_translate(x2 + 150 + 450, -15.2f, zJardines_principal - jardinAncho_chico/2 + 100);
        arboles.get(k++).draw(g_modelMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);
        g_modelMatrix = popMatrix();

        // arboles jardines en medio en medio
        pushMatrix(g_modelMatrix);
        g_modelMatrix_translate(CENTRO_TABLERO - jardinAncho_grande - anchez - 150, -15.2f, - CENTRO_TABLERO + jardinAncho_grande - anchez - 450);
        arboles.get(k++).draw(g_modelMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);
        g_modelMatrix = popMatrix();
        pushMatrix(g_modelMatrix);
        g_modelMatrix_translate(CENTRO_TABLERO - jardinAncho_grande - anchez - 150, -15.2f, - CENTRO_TABLERO - jardinAncho_grande + anchez*7 - 450);
        arboles.get(k++).draw(g_modelMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);
        g_modelMatrix = popMatrix();
        pushMatrix(g_modelMatrix);
        g_modelMatrix_translate(CENTRO_TABLERO + 250 + anchez, -15.2f, - CENTRO_TABLERO + jardinAncho_grande - anchez - 450);
        arboles.get(k++).draw(g_modelMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);
        g_modelMatrix = popMatrix();
        pushMatrix(g_modelMatrix);
        g_modelMatrix_translate(CENTRO_TABLERO + 250 + anchez, -15.2f, - CENTRO_TABLERO - jardinAncho_grande + anchez*7 - 450);
        arboles.get(k++).draw(g_modelMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);
        g_modelMatrix = popMatrix();

        // fuente
        float ancho = 640*0.5f;
        pushMatrix(g_modelMatrix);
        g_modelMatrix_translate(CENTRO_TABLERO - ancho/2, -15f, - CENTRO_TABLERO - ancho/2);
        arboles.get(k++).draw(g_modelMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);
        g_modelMatrix = popMatrix();

        // bancas
        pushMatrix(g_modelMatrix);
        g_modelMatrix_translate(x1 - jardinAncho_chico + 50 - 400, -15.2f, -870);
        arboles.get(k++).draw(g_modelMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);
        g_modelMatrix = popMatrix();
        pushMatrix(g_modelMatrix);
        g_modelMatrix_translate(x2 + 150 + 400, -15.2f, -870);
        arboles.get(k++).draw(g_modelMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);
        g_modelMatrix = popMatrix();


        pushMatrix(g_modelMatrix);
        g_modelMatrix_translate(x1 - jardinAncho_chico + 50 - 600, -15.2f, -1430);
        arboles.get(k++).draw(g_modelMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);
        g_modelMatrix = popMatrix();
        pushMatrix(g_modelMatrix);
        pushMatrix(g_modelMatrix);
        g_modelMatrix_translate(x1 - jardinAncho_chico + 50 - 800, -15.2f, -1430);
        arboles.get(k++).draw(g_modelMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);
        g_modelMatrix = popMatrix();
        pushMatrix(g_modelMatrix);
        g_modelMatrix_translate(x2 + 150 + 600, -15.2f, -1430);
        arboles.get(k++).draw(g_modelMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);
        g_modelMatrix = popMatrix();
        g_modelMatrix_translate(x2 + 150 + 800, -15.2f, -1430);
        arboles.get(k++).draw(g_modelMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);
        g_modelMatrix = popMatrix();

    }

    // dibujar el modelo de la fachada
    private void fachada_principal() {

        if (fachadaPrincipal == null) fachadaPrincipal = new FachadaPrincipal(tx_pared_guinda, tx_pared_blanca, tx_pavimento, tx_techo_fachada);

        pushMatrix(g_modelMatrix);
        g_modelMatrix_translate(CENTRO_TABLERO, 10.1f, z);
        fachadaPrincipal.draw(g_modelMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle);
        g_modelMatrix = popMatrix();

    }

    // dibujar modelo del esqueleto del edificio (paredes)
    private void esqueleto_edificio() {

        if (esqueleto == null) esqueleto = new EsqueletoEdificio(tx_ladrillo, tx_ventana,tx_puerta, tx_techo, LADO_X);

        pushMatrix(g_modelMatrix);
        g_modelMatrix_translate(x, 10.1f, z);
        esqueleto.draw(g_modelMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle,
                CENTRO_TABLERO, z, LADO_X, LADO_Z);
        g_modelMatrix = popMatrix();

    }

    // dibujar porton
    private void porton() {

        if (porton == null) porton = new Porton(tx_pared_guinda, tx_barras, CENTRO_TABLERO - 450);

        pushMatrix(g_modelMatrix);
        g_modelMatrix_translate(x, 10.1f, z);
        porton.draw(g_modelMatrix, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle,
                CENTRO_TABLERO, z, LADO_X, LADO_Z);
        g_modelMatrix = popMatrix();

    }

}