package upv.pm.may_ago_26.iti_271503.pgu2_eq06

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.util.Stack
import java.util.concurrent.TimeUnit
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class CubeRenderer(private val mActivityContext: Context) : GLSurfaceView.Renderer {
    companion object {
        const val CUBE_ROTATION_INCREMENT = 0.1f
        private const val REFRESH_RATE_FPS = 60
        private val FRAME_TIME_MILLIS = TimeUnit.SECONDS.toMillis(1).toFloat() / REFRESH_RATE_FPS
        var screenWidth = 0f
        var screenHeight = 0f
    }

    private lateinit var inicioMatrix: FloatArray
    private lateinit var g_viewMatrix: FloatArray
    private lateinit var g_projectionMatrix: FloatArray
    private lateinit var g_mvpMatrix: FloatArray
    private lateinit var g_modelMatrix: FloatArray

    private val pila = Stack<FloatArray>()
    fun pushMatrix(m: FloatArray) {
        val m2 = FloatArray(16)
        System.arraycopy(m, 0, m2, 0, 16)
        pila.push(m2)
    }
    fun popMatrix(): FloatArray = pila.pop()

    private lateinit var mPositions: FloatBuffer
    private lateinit var mTextureCoordinates: FloatBuffer
    private var mMvpMatrixHandle = 0
    private var mTextureUniformHandle = 0
    private var mPositionHandle = 0
    private var mTextureCoordinateHandle = 0
    private val mBytesPerFloat = 4
    private val mPositionDataSize = 3
    private val mTextureCoordinateDataSize = 2
    private var mProgramHandle = 0

    private var indiceUbicacion = 0
    private var gll = 0
    private var u_MvpMatrix = 0

    private val PI = 3.1415926535897932384626433832795f
    private val PIdiv180 = PI / 180.0f
    private var RotatedX = 0f
    private var RotatedY = 0f
    private var RotatedZ = 0f
    var Position = FloatArray(3)
    var ViewDir = FloatArray(3)
    var RightVector = FloatArray(3)
    var UpVector = FloatArray(3)
    private val orientationAngles = FloatArray(3)
    private val rotationMatrix = FloatArray(16)
    private var movementDirection = floatArrayOf(0f, 0f, -1f)

    fun setOrientation(angles: FloatArray) {
        System.arraycopy(angles, 0, orientationAngles, 0, angles.size)
    }

    private fun ubicacion_inicial() { MoveForward(150.0f); MoveUpward(10.0f); StrafeRight(20.0f) }
    private fun centro_patio() { MoveForward(-370.0f); MoveUpward(10.0f); StrafeRight(20.0f) }
    private fun centro_salon() { MoveForward(-390.0f); MoveUpward(10.0f); StrafeRight(260.0f) }


    fun ReiniciarPosiciones() {
        RotatedX = 0.0f
        RotatedY = 0.0f
        RotatedZ = 0.0f

        Position = FloatArray(3)
        Position[0] = 0.0f
        Position[1] = 4.0f
        Position[2] = 0.0f

        ViewDir = FloatArray(3)
        ViewDir[0] = 0.0f
        ViewDir[1] = 0.0f
        ViewDir[2] = -1.0f

        RightVector = FloatArray(3)
        RightVector[0] = 1.0f
        RightVector[1] = 0.0f
        RightVector[2] = 0.0f

        UpVector = FloatArray(3)
        UpVector[0] = 0.0f
        UpVector[1] = 1.0f
        UpVector[2] = 0.0f

        ubicacion_inicial()
    }

    private var imagen_lado1 = 0; private var imagen_entrada = 0; private var textura_entrada_reverso = 0; private var imagen_entrada_reverso = 0; private var imagen_cesped = 0; private var textura_cielo1 = 0; private var imagen_cielo1 = 0
    private var imagen_ladoderecho1 = 0; private var imagen_ladoizquierdo1 = 0; private var imagen_ladoizquierdo2 = 0; private var imagen_piso2 = 0; private var textura_piso2 = 0; private var textura_cielo2 = 0; private var imagen_cielo2 = 0
    private var imagen_frontalderecho = 0; private var imagen_frontalizquierdo = 0; private var textura_frontalderecho = 0; private var textura_frontalizquierdo = 0; private var imagen_fondo = 0; private var textura_ladobueno = 0
    private var textura_ladobueno2 = 0; private var textura_cesped = 0; private var textura_ladobueno3 = 0; private var textura_ladobueno4 = 0; private var textura_entradafondo = 0; private var textura_principal = 0; private var textura_blanco = 0
    private var img_piso = 0; private var textura_piso = 0; private var img_back1 = 0; private var textura_back1 = 0; private var img_izq1 = 0; private var textura_izq1 = 0; private var img_der1 = 0; private var textura_der1 = 0
    private var img_back2 = 0; private var textura_espalda2 = 0; private var img_techo1 = 0; private var textura_techo1 = 0; private var img_der2 = 0; private var textura_der2 = 0; private var img_der3 = 0; private var textura_der3 = 0
    private var img_der4 = 0; private var textura_der4 = 0; private var img_der5 = 0; private var textura_der5 = 0; private var img_der6 = 0; private var textura_der6 = 0; private var img_frontal1 = 0; private var textura_frontal1 = 0
    private var img_frontal2 = 0; private var textura_frontal2 = 0; private var img_izq2 = 0; private var textura_izq2 = 0; private var img_izq3 = 0; private var textura_izq3 = 0; private var img_izq4 = 0; private var textura_izq4 = 0
    private var img_izq5 = 0; private var textura_izq5 = 0; private var img_izq6 = 0; private var textura_izq6 = 0; private var imagen_puertaSalon1 = 0; private var textura_puertaSalon1 = 0
    private var Largo = 100.0
    private var Alto = 50.0

    init {

        Log.d("Purito", "Inicialización Cube Renderer")

        // Matrices
        inicioMatrix = FloatArray(16)
        g_modelMatrix = FloatArray(16)
        g_mvpMatrix = FloatArray(16)
        g_viewMatrix = FloatArray(16)
        g_projectionMatrix = FloatArray(16)

        RotatedX = 0.0f
        RotatedY = 0.0f
        RotatedZ = 0.0f

        Position = FloatArray(3)
        Position[0] = 0.0f
        Position[1] = 0.0f
        Position[2] = 0.0f

        ViewDir = FloatArray(3)
        ViewDir[0] = 0.0f
        ViewDir[1] = 0.0f
        ViewDir[2] = -1.0f

        RightVector = FloatArray(3)
        RightVector[0] = 1.0f
        RightVector[1] = 0.0f
        RightVector[2] = 0.0f

        UpVector = FloatArray(3)
        UpVector[0] = 0.0f
        UpVector[1] = 1.0f
        UpVector[2] = 0.0f

        ubicacion_inicial()
        //centro_patio()
        //centro_salon()

    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig) {
        Log.d("Purito", "Creando Superficie")

        // Set the background frame color
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)

        val vertexShader = RawResourceReader.readTextFileFromRawResource(mActivityContext, R.raw.per_pixel_vertex_shader_tex_and_light)
        val fragmentShader = RawResourceReader.readTextFileFromRawResource(mActivityContext, R.raw.per_pixel_fragment_shader_tex_and_light)

        val vertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader)
        val fragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader)

        mProgramHandle = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle,
                arrayOf("a_Position", "a_TexCoord", "a_Color"))

        GLES20.glClearColor(0f, 0f, 0f, 1f)


        // Texturas del fondo
        texturas_exterior()
        texturas_salon()

        Matrix.setIdentityM(g_mvpMatrix, 0)
        Matrix.setIdentityM(g_viewMatrix, 0)
        Matrix.setIdentityM(g_projectionMatrix, 0)
    }

    fun texturas_exterior() {
        // Cargar textura del césped
        textura_cesped = generarTextura(R.drawable.cesped)
        imagen_cesped = textura_cesped // Asignar el mismo identificador

        // Cargar textura del cielo
        textura_cielo2 = generarTextura(R.drawable.cielo)
        imagen_cielo2 = textura_cielo2 // Asignar el mismo identificador

        textura_ladobueno = generarTextura(R.drawable.ladobueno)
        textura_ladobueno2 = generarTextura(R.drawable.ladobueno2)
        textura_ladobueno3 = generarTextura(R.drawable.ladobueno3)
        textura_ladobueno4 = generarTextura(R.drawable.ladobueno4)

        textura_entrada_reverso = generarTextura(R.drawable.entradaprincipal_reverso)
        textura_entradafondo = generarTextura(R.drawable.entradafondo)
        textura_principal = generarTextura(R.drawable.principal)
        textura_blanco = generarTextura(R.drawable.blanco)

        textura_puertaSalon1 = generarTextura(R.drawable.puerta_salon)

        textura_cielo1 = generarTextura(R.drawable.techo1)
        textura_cielo2 = generarTextura(R.drawable.cielo)

        textura_piso2 = generarTextura(R.drawable.piso2)
        textura_frontalderecho = generarTextura(R.drawable.frontal_derecho)
        textura_frontalizquierdo = generarTextura(R.drawable.frontal_izquierdo)
    }

    fun texturas_salon() {
        // piso
        textura_piso = generarTextura(R.drawable.madera_1)

        // imagenes pared -> espalda
        textura_back1 = generarTextura(R.drawable.espalda_1)
        textura_espalda2 = generarTextura(R.drawable.espalda_2)

        // imagenes pared -> frontal
        textura_frontal1 = generarTextura(R.drawable.frente_1)
        textura_frontal2 = generarTextura(R.drawable.frente_2)

        // imagenes pared -> izq
        textura_izq1 = generarTextura(R.drawable.izq_1)
        textura_izq2 = generarTextura(R.drawable.izq_2)
        textura_izq3 = generarTextura(R.drawable.izq_3)
        textura_izq4 = generarTextura(R.drawable.izq_4)
        textura_izq5 = generarTextura(R.drawable.izq_5)
        textura_izq6 = generarTextura(R.drawable.izq_6)

        // imagenes pared -> der
        textura_der1 = generarTextura(R.drawable.der_1)
        textura_der2 = generarTextura(R.drawable.der_2)
        textura_der3 = generarTextura(R.drawable.der_3)
        textura_der4 = generarTextura(R.drawable.der_4)
        textura_der5 = generarTextura(R.drawable.der_5)
        textura_der6 = generarTextura(R.drawable.der_6)

        // techo
        textura_techo1 = generarTextura(R.drawable.techo)
    }

    fun generarTextura(textura: Int): Int {
        var t = TextureHelper.loadTexture(mActivityContext, textura)
        return t
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        screenHeight = height.toFloat()
        screenWidth = width.toFloat()

        // CardboardView ya entrega a este renderer el tamaño de UN ojo.
        // Esta proyección sirve como respaldo; en cada onDrawEye se sustituye
        // por la proyección calculada por el perfil real del visor Cardboard.
        val ratio = width.toFloat() / height.toFloat()
        Matrix.frustumM(g_projectionMatrix, 0, -ratio, ratio, -1.0f, 1.0f, 1.0f, 5000.0f)
    }

    /**
     * CardboardView calcula una proyección distinta para cada ojo.
     * MainActivity llama este método antes de renderizar cada ojo.
     */
    fun setEyeProjection(projection: FloatArray) {
        System.arraycopy(projection, 0, g_projectionMatrix, 0, 16)
    }

    override fun onDrawFrame(gl: GL10?) {
        // IMPORTANTE: no enlazar aquí el framebuffer 0.
        // Cuando la corrección Cardboard está activa, DistortionRenderer ya
        // enlazó su framebuffer interno antes de llamar a este método.

        // StereoRendererHelper deja activado GL_SCISSOR_TEST y configura el
        // viewport/scissor del ojo actual, así que este clear afecta solo al ojo.
        GLES20.glClearColor(0f, 0f, 0f, 1f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glUseProgram(mProgramHandle)

        mMvpMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MvpMatrix")
        mTextureUniformHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_Sampler")
        mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position")
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_TexCoord")

        actualizar_vista()

        Matrix.multiplyMM(g_modelMatrix, 0, g_projectionMatrix, 0, g_viewMatrix, 0)
        System.arraycopy(g_modelMatrix, 0, inicioMatrix, 0, 16)

        // Se dibuja UNA sola escena. CardboardView llama este método una vez
        // para el ojo izquierdo y otra para el derecho.
        dibujarEscena()
    }


    private fun dibujarPunteroMinimapa(x: Float, y: Float) {
        val vertices = floatArrayOf(
                x, y + 0.06f, 0.0f,
                x - 0.06f, y - 0.06f, 0.0f,
                x + 0.06f, y - 0.06f, 0.0f
        )
        val texturas = floatArrayOf(
                0.5f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f
        )
        // Reutiliza una textura blanca para dibujar el puntero sin depender de shaders de color.
        cargarPosicion_Textura(vertices, texturas, textura_blanco)
        GLES20.glUniformMatrix4fv(mMvpMatrixHandle, 1, false, g_mvpMatrix, 0)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3)
    }

    fun dibujarEscena() {
        // Se llaman los métodos para mostrar el cielo
        cielo()
        cielo_enfrente()
        cielo_atras()
        cielo_derecha()
        cielo_izquierda()

        // Dibujar el césped base
        base_cesped()

        // Dibujar la pinacoteca
        pinacoteca()

        // Restablecer la matriz de modelo
        Matrix.setIdentityM(g_modelMatrix, 0)
        System.arraycopy(inicioMatrix, 0, g_modelMatrix, 0, 16)
    }

    fun pinacoteca() {
        exterior()
        salon1()
    }

    fun exterior() {
        // Piso
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-50, 1, -50) // Ajustar la posición del piso
        piso_o_techo(gll, u_MvpMatrix, Largo, Largo, imagen_cesped, textura_piso2) // Textura del césped
        g_modelMatrix = popMatrix()

        // Piso 2
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-50, 1, -150) // Ajustar la posición del piso
        piso_o_techo(gll, u_MvpMatrix, Largo, Largo, imagen_piso2, textura_piso2) // Textura del césped
        g_modelMatrix = popMatrix()

        // Piso 3
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-50, 1, -250) // Ajustar la posición del piso
        piso_o_techo(gll, u_MvpMatrix, Largo, Largo, imagen_piso2, textura_piso2) // Textura del césped
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(50, 1, -250) // Ajustar la posición del piso
        piso_o_techo(gll, u_MvpMatrix, Largo, Largo, imagen_piso2, textura_piso2) // Textura del césped
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-150, 1, -250) // Ajustar la posición del piso
        piso_o_techo(gll, u_MvpMatrix, Largo, Largo, imagen_piso2, textura_piso2) // Textura del césped
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-50, 1, -350) // Ajustar la posición del piso
        piso_o_techo(gll, u_MvpMatrix, Largo, Largo, imagen_piso2, textura_piso2) // Textura del césped
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(50, 1, -350) // Ajustar la posición del piso
        piso_o_techo(gll, u_MvpMatrix, Largo, Largo, imagen_piso2, textura_piso2) // Textura del césped
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-150, 1, -350) // Ajustar la posición del piso
        piso_o_techo(gll, u_MvpMatrix, Largo, Largo, imagen_piso2, textura_piso2) // Textura del césped
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-50, 1, -450) // Ajustar la posición del piso
        piso_o_techo(gll, u_MvpMatrix, Largo, Largo, imagen_piso2, textura_piso2) // Textura del césped
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(50, 1, -450) // Ajustar la posición del piso
        piso_o_techo(gll, u_MvpMatrix, Largo, Largo, imagen_piso2, textura_piso2) // Textura del césped
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-150, 1, -450) // Ajustar la posición del piso
        piso_o_techo(gll, u_MvpMatrix, Largo, Largo, imagen_piso2, textura_piso2) // Textura del césped
        g_modelMatrix = popMatrix()

        // Techo
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-50, 50, -50) // Ajustar la posición del techo
        piso_o_techo(gll, u_MvpMatrix, Largo, Largo, imagen_cielo1, textura_cielo1) // Textura del cielo
        g_modelMatrix = popMatrix()

        // Techo 2
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-50, 50, -150) // Ajustar la posición del techo
        piso_o_techo(gll, u_MvpMatrix, Largo, Largo, imagen_cielo1, textura_cielo1) // Textura del cielo
        g_modelMatrix = popMatrix()

        // Frontal (Usando pared_X)
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-170, -75, -50) // Ajustar la posición de la pared frontal
        pared_X(gll, u_MvpMatrix, 400, 200, imagen_entrada, textura_principal) // Textura de la entrada principal
        g_modelMatrix = popMatrix()

        // Frontal-Dentro-derecha
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(50, 0, -250) // Ajustar la posición de la pared frontal
        pared_X(gll, u_MvpMatrix, Largo, Alto, imagen_frontalderecho, textura_frontalderecho) // Textura de la entrada principal
        g_modelMatrix = popMatrix()

        // Frontal-Dentro-izquierda
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-150, 0, -250) // Ajustar la posición de la pared frontal
        pared_X(gll, u_MvpMatrix, Largo, Alto, imagen_frontalizquierdo, textura_frontalizquierdo) // Textura de la entrada principal
        g_modelMatrix = popMatrix()

        // Frontal-reverso (Usando pared_X)
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-50, 0, -51) // Ajustar la posición de la pared frontal
        pared_X(gll, u_MvpMatrix, Largo, Alto, imagen_entrada_reverso, textura_entrada_reverso) // Textura de la entrada principal
        g_modelMatrix = popMatrix()

        // Lado izquierdo (Usando pared_Z)
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-50, 0, -50) // Ajustar la posición del lado izquierdo
        pared_Z(gll, u_MvpMatrix, Largo, Alto, imagen_ladoizquierdo1, textura_blanco) // Textura del lado izquierdo
        g_modelMatrix = popMatrix()

        // Lado izquierdo 2(Usando pared_Z)
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-50, 0, -150) // Ajustar la posición del lado izquierdo
        pared_Z(gll, u_MvpMatrix, Largo, Alto, imagen_ladoizquierdo2, textura_blanco) // Textura del lado izquierdo
        g_modelMatrix = popMatrix()

        // Lado derecho (Usando pared_Z)
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(50, 0, -50) // Ajustar la posición del lado derecho
        pared_Z(gll, u_MvpMatrix, Largo, Alto, imagen_ladoderecho1, textura_blanco) // Textura del lado derecho
        g_modelMatrix = popMatrix()

        // Lado derecho 2(Usando pared_Z)
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(50, 0, -150) // Ajustar la posición del lado derecho
        pared_Z(gll, u_MvpMatrix, Largo, Alto, imagen_ladoizquierdo2, textura_blanco) // Textura del lado derecho
        g_modelMatrix = popMatrix()

        // Fondo
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-150, 0, -550) // Ajustar la posición de la pared frontal
        pared_X(gll, u_MvpMatrix, 300, Alto, imagen_fondo, textura_ladobueno3) // Textura de la entrada principal
        g_modelMatrix = popMatrix()

        // Lado derecho 3(Usando pared_Z)
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(150, 0, -250) // Ajustar la posición del lado derecho
        pared_Z(gll, u_MvpMatrix, 300, Alto, imagen_lado1, textura_ladobueno2) // Textura del lado derecho
        g_modelMatrix = popMatrix()

        // Lado izquierdo 3(Usando pared_Z)
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-150, 0, -250) // Ajustar la posición del lado derecho
        pared_Z(gll, u_MvpMatrix, 300, Alto, imagen_lado1, textura_ladobueno) // Textura del lado derecho
        g_modelMatrix = popMatrix()

        // Lado frontal 4(Usando pared_Z)
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-150, 0, -251) // Ajustar la posición del lado derecho
        pared_X(gll, u_MvpMatrix, 300, 75, imagen_lado1, textura_ladobueno4) // Textura del lado derecho
        g_modelMatrix = popMatrix()

        // Entrada fondo 4(Usando pared_Z)
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-50, 0, -249) // Ajustar la posición del lado derecho
        pared_X(gll, u_MvpMatrix, Largo, Alto, imagen_lado1, textura_entradafondo) // Textura del lado derecho
        g_modelMatrix = popMatrix()
    }

    fun salon1() {
        // puerta sala 1
        puerta_Sala1()

        // Piso del salon 1
        piso_salon()

        // Techo del salon 1
        techo_salon()

        // Paredes alrededor del piso
        // Pared izq -> puerta
        pared_izq()

        // Pared -> espalda
        pared_esp()

        // Pared der -> 1 texto y pinturas
        pared_der()

        // Pared frontal
        pared_frontal()
    }

    fun puerta_Sala1() {
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(149.5, -.25, -340)
        pared_Z(gll, u_MvpMatrix, Largo, Alto, imagen_puertaSalon1, textura_puertaSalon1)
        g_modelMatrix = popMatrix()
    }

    fun piso_salon() {
        // PISO -------------------- 1
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(150.8, 1, -140)
        piso_o_techo(gll, u_MvpMatrix, Largo, Largo, img_piso, textura_piso)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(150.8, 1, -240)
        piso_o_techo(gll, u_MvpMatrix, Largo, Largo, img_piso, textura_piso)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(150.8, 1, -340)
        piso_o_techo(gll, u_MvpMatrix, Largo, Largo, img_piso, textura_piso)
        g_modelMatrix = popMatrix()

        // ------------------------------------------ piso de la puerta 1
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(150.8, 1, -440)
        piso_o_techo(gll, u_MvpMatrix, Largo, Largo, img_piso, textura_piso)
        g_modelMatrix = popMatrix()
        // ------------------------------------------

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(150.8, 1, -540)
        piso_o_techo(gll, u_MvpMatrix, Largo, Largo, img_piso, textura_piso)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(150.8, 1, -640)
        piso_o_techo(gll, u_MvpMatrix, Largo, Largo, img_piso, textura_piso)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(150.8, 1, -740)
        piso_o_techo(gll, u_MvpMatrix, Largo, Largo, img_piso, textura_piso)
        g_modelMatrix = popMatrix()



        // PISO -------------------- 2
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(250.8, 1, -140)
        piso_o_techo(gll, u_MvpMatrix, Largo, Largo, img_piso, textura_piso)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(250.8, 1, -240)
        piso_o_techo(gll, u_MvpMatrix, Largo, Largo, img_piso, textura_piso)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(250.8, 1, -340)
        piso_o_techo(gll, u_MvpMatrix, Largo, Largo, img_piso, textura_piso)
        g_modelMatrix = popMatrix()

        // ------------------------------------------ piso de la puerta 1
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(250.8, 1, -440)
        piso_o_techo(gll, u_MvpMatrix, Largo, Largo, img_piso, textura_piso)
        g_modelMatrix = popMatrix()
        // ------------------------------------------

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(250.8, 1, -540)
        piso_o_techo(gll, u_MvpMatrix, Largo, Largo, img_piso, textura_piso)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(250.8, 1, -640)
        piso_o_techo(gll, u_MvpMatrix, Largo, Largo, img_piso, textura_piso)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(250.8, 1, -740)
        piso_o_techo(gll, u_MvpMatrix, Largo, Largo, img_piso, textura_piso)
        g_modelMatrix = popMatrix()

    }

    fun techo_salon() {
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(150.8, 51, -140)
        piso_o_techo(gll, u_MvpMatrix, Largo *2, Largo *2, img_techo1, textura_techo1)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(150.8, 51, -340)
        piso_o_techo(gll, u_MvpMatrix, Largo *2, Largo *2, img_techo1, textura_techo1)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(150.8, 51, -540)
        piso_o_techo(gll, u_MvpMatrix, Largo *2, Largo *2, img_techo1, textura_techo1)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(150.8, 51, -740)
        piso_o_techo(gll, u_MvpMatrix, Largo *2, Largo *2, img_techo1, textura_techo1)
        g_modelMatrix = popMatrix()
    }

    // Pared izq -> puerta
    fun pared_izq() {
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(150.8, -.25, -140)
        pared_Z(gll, u_MvpMatrix, Largo, Alto, img_izq1, textura_izq1)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(150.8, -.25, -240)
        pared_Z(gll, u_MvpMatrix, Largo, Alto, img_izq2, textura_izq2)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(150.8, -.25, -340)
        pared_Z(gll, u_MvpMatrix, Largo, Alto, img_izq3, textura_izq3)
        g_modelMatrix = popMatrix()

        // ---------------------------------------
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(150.8, -.25, -440)
        pared_Z(gll, u_MvpMatrix, Largo, Alto, img_izq4, textura_izq4)
        g_modelMatrix = popMatrix()

        // ---------------------------------------

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(150.8, -.25, -540)
        pared_Z(gll, u_MvpMatrix, Largo, Alto, img_izq5, textura_izq5)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(150.8, -.25, -640)
        pared_Z(gll, u_MvpMatrix, Largo, Alto, img_izq4, textura_izq4)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(150.8, -.25, -740)
        pared_Z(gll, u_MvpMatrix, Largo, Alto, img_izq6, textura_izq6)
        g_modelMatrix = popMatrix()
    }

    // Pared der -> 1 texto y pinturas
    fun pared_der() {
        // texto pared
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(350.8, -.25, -140)
        pared_Z(gll, u_MvpMatrix, Largo, Alto, img_der1, textura_der1)
        g_modelMatrix = popMatrix()

        // pinturas 1
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(350.8, -.25, -240)
        pared_Z(gll, u_MvpMatrix, Largo, Alto, img_der2, textura_der2)
        g_modelMatrix = popMatrix()

        // pinturas 2
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(350.8, -.25, -340)
        pared_Z(gll, u_MvpMatrix, Largo, Alto, img_der3, textura_der3)
        g_modelMatrix = popMatrix()

        // pinturas 3
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(350.8, -.25, -440)
        pared_Z(gll, u_MvpMatrix, Largo, Alto, img_der4, textura_der4)
        g_modelMatrix = popMatrix()

        // pinturas 4
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(350.8, -.25, -540)
        pared_Z(gll, u_MvpMatrix, Largo, Alto, img_der5, textura_der5)
        g_modelMatrix = popMatrix()

        // pinturas 5
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(350.8, -.25, -640)
        pared_Z(gll, u_MvpMatrix, Largo, Alto, img_der6, textura_der6)
        g_modelMatrix = popMatrix()

        // texto pared 2
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(350.8, -.25, -740)
        pared_Z(gll, u_MvpMatrix, Largo, Alto, img_der1, textura_der1)
        g_modelMatrix = popMatrix()
    }

    fun pared_frontal() {
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(250.8, 0, -840)
        pared_X(gll, u_MvpMatrix, Largo, Alto, img_frontal2, textura_frontal2)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(150.8, 0, -840)
        pared_X(gll, u_MvpMatrix, Largo, Alto, img_frontal1, textura_frontal1)
        g_modelMatrix = popMatrix()
    }

    // Pared -> espalda
    fun pared_esp() {
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(250.8, 0, -140)
        pared_X(gll, u_MvpMatrix, Largo, Alto, img_back1, textura_back1)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(150.8, 0, -140)
        pared_X(gll, u_MvpMatrix, Largo, Alto, img_back2, textura_espalda2)
        g_modelMatrix = popMatrix()
    }

    private fun horizontalForwardVector(): FloatArray {
        // Movimiento de navegación en plano XZ: ignora el pitch para no subir/bajar al mirar arriba o abajo.
        val forward = floatArrayOf(ViewDir[0], 0f, ViewDir[2])
        val length = GetF3dVectorLength(forward)
        return if (length < 0.0001f) floatArrayOf(0f, 0f, -1f) else Normalize(forward)
    }

    private fun horizontalRightVector(): FloatArray {
        // Vector lateral calculado desde la dirección horizontal de la mirada.
        val forward = horizontalForwardVector()
        return Normalize(CrossProduct(forward, floatArrayOf(0f, 1f, 0f)))
    }

    fun MoveForward(Distance: Float) {
        Position = suma(Position, multiplicacion(horizontalForwardVector(), -Distance))
    }

    fun StrafeRight(Distance: Float) {
        Position = suma(Position, multiplicacion(horizontalRightVector(), Distance))
    }

    fun MoveUpward(Distance: Float) {
        // Subir/bajar siempre sobre el eje Y del mundo, no sobre el UpVector de la cámara.
        Position = suma(Position, floatArrayOf(0f, Distance, 0f))
    }

    fun RotateX(Angle: Float) {
        RotatedX = Angle

        //Rotate viewdir around the right vector:
        ViewDir = Normalize(suma(multiplicacion(ViewDir, cos((Angle * PIdiv180).toDouble()).toFloat()),
                multiplicacion(UpVector, sin((Angle * PIdiv180).toDouble()).toFloat())))

        //now compute the new UpVector (by cross product)
        UpVector = multiplicacion(CrossProduct(ViewDir, RightVector), -1f)

    }

    fun RotateY(Angle: Float) {
        RotatedY = Angle

        //Rotate viewdir around the up vector:
        ViewDir = Normalize(resta(multiplicacion(ViewDir, cos((Angle * PIdiv180).toDouble()).toFloat()),
                multiplicacion(RightVector, sin((Angle * PIdiv180).toDouble()).toFloat())))

        //now compute the new RightVector (by cross product)
        RightVector = CrossProduct(ViewDir, UpVector)
    }

    fun CrossProduct(u: FloatArray, v: FloatArray): FloatArray {
        val resVector = FloatArray(3)
        resVector[0] = u[1]*v[2] - u[2]*v[1]
        resVector[1] = u[2]*v[0] - u[0]*v[2]
        resVector[2] = u[0]*v[1] - u[1]*v[0]

        return resVector
    }

    fun GetF3dVectorLength(v: FloatArray): Float {
        return sqrt((v[0]*v[0] + v[1]*v[1] + v[2]*v[2]).toDouble()).toFloat()
    }

    fun Normalize(v: FloatArray): FloatArray {
        val res = FloatArray(3)
        var l = GetF3dVectorLength(v)
        if (l == 0.0f){
            res[0] = 0.0f
            res[1] = 0.0f
            res[2] = 0.0f
        }else {
            res[0] = v[0] / l
            res[1] = v[1] / l
            res[2] = v[2] / l
        }
        return res
    }

    fun multiplicacion(v: FloatArray, r: Float): FloatArray {
        val res = FloatArray(3)
        res[0] = v[0]*r
        res[1] = v[1]*r
        res[2] = v[2]*r
        return res
    }

    fun suma(v: FloatArray, u: FloatArray): FloatArray {
        val res = FloatArray(3)
        res[0] = v[0]+u[0]
        res[1] = v[1]+u[1]
        res[2] = v[2]+u[2]
        return res
    }

    fun resta(v: FloatArray, u: FloatArray): FloatArray {
        val res = FloatArray(3)
        res[0] = v[0]-u[0]
        res[1] = v[1]-u[1]
        res[2] = v[2]-u[2]
        return res
    }

    fun Move(Direction: FloatArray) {
        Position = suma(Position, Direction)
    }

    fun actualizar_vista() {
        // Crear la matriz de rotación a partir de los ángulos de orientación
        Matrix.setIdentityM(rotationMatrix, 0)

        // Rotar alrededor del eje Y (azimuth)
        Matrix.rotateM(rotationMatrix, 0, Math.toDegrees((-orientationAngles[0]).toDouble()).toFloat(), 0f, 1f, 0f)
        // Rotar alrededor del eje X (pitch)
        Matrix.rotateM(rotationMatrix, 0, Math.toDegrees(orientationAngles[1].toDouble()).toFloat(), 1f, 0f, 0f)

        // Aplicar la rotación a la dirección de la mirada
        val lookAtVector = floatArrayOf(0f, 0f, -1f, 1f)
        val transformedLookAt = FloatArray(4)
        Matrix.multiplyMV(transformedLookAt, 0, rotationMatrix, 0, lookAtVector, 0)

        // Actualizar ViewDir con la nueva dirección
        ViewDir[0] = transformedLookAt[0]
        ViewDir[1] = transformedLookAt[1]
        ViewDir[2] = transformedLookAt[2]

        // Actualizar RightVector y UpVector
        RightVector = CrossProduct(ViewDir, floatArrayOf(0f, 1f, 0f))
        UpVector = CrossProduct(RightVector, ViewDir)

        // Normalizar los vectores
        ViewDir = Normalize(ViewDir)
        RightVector = Normalize(RightVector)
        UpVector = Normalize(UpVector)

        // Calcular el punto de vista (Position + ViewDir)
        val ViewPoint = suma(Position, ViewDir)

        // Establecer la matriz de vista
        Matrix.setLookAtM(g_viewMatrix, 0, Position[0], Position[1], Position[2], ViewPoint[0], ViewPoint[1], ViewPoint[2], UpVector[0], UpVector[1], UpVector[2])
    }

    fun cargarPosicion_Textura(v: FloatArray, vt: FloatArray, textura: Int) {

        //Posiciones---------------------------------------------------------------------------------

        mPositions = ByteBuffer.allocateDirect(v.size * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer()
        mPositions.put(v).position(0)

        // Pass in the position information
        mPositions.position(0)
        GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false, 0, mPositions)

        GLES20.glEnableVertexAttribArray(mPositionHandle)

        //Textura-----------------------------------------------------------------------------------
        mTextureCoordinates = ByteBuffer.allocateDirect(vt.size * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer()
        mTextureCoordinates.put(vt).position(0)

        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)

        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textura)

        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(mTextureUniformHandle, 0)

        // Pass in the texture coordinate information
        mTextureCoordinates.position(0)
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, mTextureCoordinateDataSize, GLES20.GL_FLOAT, false, 0, mTextureCoordinates)

        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle)

        //----------------------------------------------------------------------------------------------

    }

    //Cuadrilatero parado en el eje de los X
    fun pared_X(nosirve1: Int, nosirve2: Int, largo: Number, alto: Number, nosirve3: Int, texturaHandle: Int) {
        System.arraycopy(g_modelMatrix, 0, g_mvpMatrix, 0, 16)

        //Coordenadas de la pared
        val vertices = floatArrayOf(
                0f,              0f,               0f,
                0f,              alto.toFloat(),     0f,
                largo.toFloat(),   0f,               0f,
                largo.toFloat(),   alto.toFloat(),     0f
        )

        //Coordenadas de la textura
        val vertices_textura = floatArrayOf(
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 0.0f,
                1.0f, 1.0f
        )

        cargarPosicion_Textura(vertices, vertices_textura, texturaHandle)


        // Pass in the combined matrix.
        GLES20.glUniformMatrix4fv(mMvpMatrixHandle, 1, false, g_mvpMatrix, 0)

        var n = vertices.size / 3

        // Draw the cube.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, n)
    }

    //Cuadrilatero parado en el eje de los X
    fun pared_Z(nosirve1: Int, nosirve2: Int, largo: Number, alto: Number, nosirve3: Int, texturaHandle: Int) {
        System.arraycopy(g_modelMatrix, 0, g_mvpMatrix, 0, 16)

        //Coordenadas de la pared
        val vertices = floatArrayOf(
                0f,     0f,    0f,
                0f,     alto.toFloat(), 0f,
                0f,     0f,    -largo.toFloat(),
                0f,     alto.toFloat(), -largo.toFloat()
        )

        //Coordenadas de la textura
        val vertices_textura = floatArrayOf(
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 0.0f,
                1.0f, 1.0f
        )


        cargarPosicion_Textura(vertices, vertices_textura, texturaHandle)


        // Pass in the combined matrix.
        GLES20.glUniformMatrix4fv(mMvpMatrixHandle, 1, false, g_mvpMatrix, 0)

        var n = vertices.size / 3

        // Draw the cube.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, n)
    }

    //Piso o techo
    fun piso_o_techo(nosirve1: Int, nosirve2: Int, largo: Number, ancho: Number, nosirve3: Int, texturaHandle: Int) {
        System.arraycopy(g_modelMatrix, 0, g_mvpMatrix, 0, 16)

        //Coordenadas de la pared
        val vertices = floatArrayOf(
                0f,     0f,    0f,
                0f,     0f,    -ancho.toFloat(),
                largo.toFloat(), 0f,    0f,
                largo.toFloat(), 0f,    -ancho.toFloat()
        )

        //Coordenadas de la textura
        val vertices_textura = floatArrayOf(
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 0.0f,
                1.0f, 1.0f
        )


        cargarPosicion_Textura(vertices, vertices_textura, texturaHandle)


        // Pass in the combined matrix.
        GLES20.glUniformMatrix4fv(mMvpMatrixHandle, 1, false, g_mvpMatrix, 0)

        var n = vertices.size / 3

        // Draw the cube.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, n)
    }

    //Piso o techo
    fun piso_o_techo_especial(nosirve1: Int, nosirve2: Int, x1: Number, z1: Number, x2: Number, z2: Number, x3: Number, z3: Number, nosirve3: Int, texturaHandle: Int) {
        System.arraycopy(g_modelMatrix, 0, g_mvpMatrix, 0, 16)

        //Coordenadas de la pared
        val vertices = floatArrayOf(
                0f,     0f,    0f,
                x1.toFloat(),    0f,   z1.toFloat(),
                x2.toFloat(),    0f,   z2.toFloat(),
                x3.toFloat(),    0f,   z3.toFloat()
        )

        //Coordenadas de la textura
        val vertices_textura = floatArrayOf(
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 0.0f,
                1.0f, 1.0f
        )


        cargarPosicion_Textura(vertices, vertices_textura, texturaHandle)


        // Pass in the combined matrix.
        GLES20.glUniformMatrix4fv(mMvpMatrixHandle, 1, false, g_mvpMatrix, 0)

        var n = vertices.size / 3

        // Draw the cube.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, n)
    }

    fun prisma_cuadrangular(nosirve1: Int, nosirve2: Int, largo: Number, ancho: Number, alto: Number, nosirve3: Int, texturaHandle: Int) {
        //System.arraycopy(g_modelMatrix, 0, g_mvpMatrix, 0, 16)

        //Cara frontal y trasera
        pared_X(nosirve1, nosirve2, largo, alto, nosirve3, texturaHandle)
        Matrix.translateM(g_modelMatrix, 0, 0f,0f, -ancho.toFloat())
        pared_X(nosirve1, nosirve2, largo, alto, nosirve3, texturaHandle)
        Matrix.translateM(g_modelMatrix, 0, 0f,0f, ancho.toFloat())

        //Cara izquierda
        pared_Z(nosirve1, nosirve2, ancho, alto, nosirve3, texturaHandle)

        //Piso
        piso_o_techo(nosirve1, nosirve2, largo, ancho, nosirve3, texturaHandle)
        Matrix.translateM(g_modelMatrix, 0, largo.toFloat(),0f, 0f)

        //Cara derecha
        pared_Z(nosirve1, nosirve2, ancho, alto, nosirve3, texturaHandle)
        Matrix.translateM(g_modelMatrix, 0, -largo.toFloat(), alto.toFloat(), 0f)

        //Techo
        piso_o_techo(nosirve1, nosirve2, largo, ancho, nosirve3, texturaHandle)
        Matrix.translateM(g_modelMatrix, 0, 0f, -alto.toFloat(), 0f)
    }

    //Para no escribir todoo de nuevo:v
    fun g_modelMatrix_translate(x: Number, y: Number, z: Number) {
        Matrix.translateM(g_modelMatrix, 0, x.toFloat(), y.toFloat(), z.toFloat())
    }

    fun g_modelMatrix_rotate(angulo: Number, x: Number, y: Number, z: Number) {
        Matrix.rotateM(g_modelMatrix, 0, angulo.toFloat(), x.toFloat(), y.toFloat() ,z.toFloat())
    }

    fun base_cesped() {
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-500,-10,700)
        piso_o_techo(gll, u_MvpMatrix, 3000, 3000, imagen_cesped, imagen_cesped)
        g_modelMatrix = popMatrix()
    }

    // Se crea y posiciona el cielo
    fun cielo() {
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-500,700,700)
        piso_o_techo(gll, u_MvpMatrix, 3000, 3000, textura_cielo2, imagen_cielo2)
        g_modelMatrix = popMatrix()
    }

    fun cielo_enfrente() {
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-500,-200,700)
        pared_X(gll,u_MvpMatrix,3000,1000,imagen_cielo2,imagen_cielo2)
        g_modelMatrix = popMatrix()
    }

    fun cielo_atras() {
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-500,-200,-2300)
        pared_X(gll,u_MvpMatrix,3000,1000,imagen_cielo2,imagen_cielo2)
        g_modelMatrix = popMatrix()
    }

    fun cielo_derecha() {
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-500,-200,700)
        pared_Z(gll, u_MvpMatrix, 3000, 1000, imagen_cielo2, imagen_cielo2)
        g_modelMatrix = popMatrix()
    }

    fun cielo_izquierda() {
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(2500,-200,700)
        pared_Z(gll, u_MvpMatrix, 3000, 1000, imagen_cielo2, imagen_cielo2)
        g_modelMatrix = popMatrix()
    }

}
