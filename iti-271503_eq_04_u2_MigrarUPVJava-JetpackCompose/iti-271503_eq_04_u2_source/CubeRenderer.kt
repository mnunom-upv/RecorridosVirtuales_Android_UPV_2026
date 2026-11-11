package upv.pm.may_ago_26.iti_271503.pgu2_eq04

import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.util.Arrays
import java.util.Stack
import java.util.concurrent.TimeUnit
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class CubeRenderer(private val mActivityContext: Context) : GLSurfaceView.Renderer,
    SensorEventListener {
    var sensorManager: SensorManager
    var rotationVectorSensor: Sensor?

    //Matrices Necesarios
    private val inicioMatrix: FloatArray
    var g_viewMatrix: FloatArray?
    var g_projectionMatrix: FloatArray?
    private val g_mvpMatrix: FloatArray
    private var g_modelMatrix: FloatArray

    //Metodos para hacer push y pop Matrix
    var pila: Stack<FloatArray> = Stack<FloatArray>()

    fun pushMatrix(m: FloatArray) {
        val m2 = FloatArray(16)
        System.arraycopy(m, 0, m2, 0, 16)
        pila.push(m2)
    }


    fun popMatrix(): FloatArray {
        return pila.pop()
    }

    //Buffers para las posiciones y texturas
    private var mPositions: FloatBuffer? = null
    private var mTextureCoordinates: FloatBuffer? = null

    /** This will be used to pass in the transformation matrix.  */
    private var mMvpMatrixHandle = 0

    /** This will be used to pass in the texture.  */
    private var mTextureUniformHandle = 0

    /** This will be used to pass in model position information.  */
    private var mPositionHandle = 0

    /** This will be used to pass in model texture coordinate information.  */
    private var mTextureCoordinateHandle = 0

    /** How many bytes per float.  */
    private val mBytesPerFloat = 4

    /** Size of the position data in elements.  */
    private val mPositionDataSize = 3

    /** Size of the texture coordinate data in elements.  */
    private val mTextureCoordinateDataSize = 2

    /** This is a handle to our cube shading program.  */
    private var mProgramHandle = 0

    /** Texturas  */
    private val imagen_techoiti = 0
    private val imagen_logo_ext = 0
    private var textura_logo_ext = 0
    private val imagen_loby_pared = 0
    private var textura_loby_pared = 0
    private var textura_techoiti = 0
    private val imagen_puertaiti = 0
    private var textura_loby_planta = 0
    private val imagen_loby_planta = 0
    private var textura_puertaiti = 0
    private val imagen_tutorias = 0
    private var textura_tutorias = 0
    private val imagen_ventanaiti2 = 0
    private var textura_ventanaiti2 = 0
    private val imagen_cesped = 0
    private var textura_cesped = 0
    private val imagen_cielo = 0
    private var textura_cielo = 0
    private var textura_cielo_enfrente = 0
    private var textura_cielo_atras = 0
    private var textura_cielo_derecha = 0
    private var textura_cielo_izquierda = 0
    private val imagen_amarillo = 0
    private var textura_amarillo = 0
    private val imagen_azul = 0
    private var textura_azul = 0
    private val imagen_azulindigo = 0
    private var textura_azulindigo = 0
    private val imagen_blanco = 0
    private var textura_blanco = 0
    private val imagen_cafe = 0
    private var textura_cafe = 0
    private val imagen_crema = 0
    private var textura_crema = 0
    private val imagen_cyan = 0
    private var textura_cyan = 0
    private val imagen_entradaPrincipal = 0
    private var textura_entradaPrincipal = 0
    private val imagen_fucsia = 0
    private var textura_fucsia = 0
    private val imagen_gindo = 0
    private var textura_gindo = 0
    private val imagen_gris = 0
    private var textura_gris = 0
    private val imagen_grisclaro = 0
    private var textura_grisclaro = 0
    private val imagen_morado = 0
    private var textura_morado = 0
    private val imagen_naranja = 0
    private var textura_naranja = 0
    private val imagen_negro = 0
    private var textura_negro = 0
    private val imagen_oro = 0
    private var textura_oro = 0
    private val imagen_pisoupv = 0
    private var textura_pisoupv = 0
    private val imagen_puerta1 = 0
    private var textura_puerta1 = 0
    private val imagen_purpura = 0
    private var textura_purpura = 0
    private val imagen_rojo = 0
    private var textura_rojo = 0
    private val imagen_verde = 0
    private var textura_verde = 0
    private val imagen_verdefosfo = 0
    private var textura_verdefosfo = 0
    private val imagen_pisoupv2 = 0
    private var textura_pisoupv2 = 0
    private val imagen_ventanaISA = 0
    private var textura_ventanaISA = 0
    private val imagen_puertage = 0
    private var textura_puertage = 0
    private val imagen_pared5 = 0
    private var textura_pared5 = 0
    private val imagen_madera = 0
    private var textura_madera = 0
    private val imagen_puertaPymes = 0
    private var textura_puertaPymes = 0
    private val imagen_cristal = 0
    private var textura_cristal = 0
    private val imagen_puertabano = 0
    private var textura_puertabano = 0
    private val imagen_escolares = 0
    private var textura_escolares = 0
    private val imagen_cai = 0
    private var textura_cai = 0
    private val imagen_puertas = 0
    private var textura_puertas = 0
    private val imagen_pasillooficina = 0
    private var textura_pasillooficina = 0
    private val imagen_puertasalida = 0
    private var textura_puertasalida = 0
    private val imagen_biblioteca = 0
    private var textura_biblioteca = 0
    private val imagen_porbiblioteca = 0
    private var textura_porbiblioteca = 0
    private val imagen_salamaestros = 0
    private var textura_salamaestros = 0
    private val imagen_ventanapymes = 0
    private var textura_ventanapymesve = 0
    private val imagen_maquina = 0
    private var textura_maquina = 0
    private val imagen_grispymes = 0
    private var textura_grispymes = 0
    private val imagen_blancopymes = 0
    private var textura_blancopymes = 0
    private val gll = 0
    private val u_MvpMatrix = 0
    private val imagen_paredoficina = 0
    private var textura_paredoficina = 0

    var PI: Float = 3.1415926535897932384626433832795f
    var PIdiv180: Float = (PI / 180.0f)

    var RotatedX: Float
    var RotatedY: Float
    var RotatedZ: Float

    var Position: FloatArray
    var ViewDir: FloatArray
    var RightVector: FloatArray
    var UpVector: FloatArray

    private val rotationMatrix = FloatArray(16)


    private fun calcularCoordenadasMinimapa(
        posicion: FloatArray,
        anchoMapa: Float,
        altoMapa: Float
    ): FloatArray {
        val xRelativo = (posicion[0] / anchoMapa) * 2.0f - 1.0f // Normalizar a [-1, 1]
        val zRelativo = (posicion[2] / altoMapa) * 2.0f - 1.0f // Normalizar a [-1, 1]
        return floatArrayOf(xRelativo, zRelativo) // Nota: usamos Z porque Y es la altura
    }

    private fun dibujarPuntero(x: Float, y: Float) {
        // Coordenadas para un triángulo (flecha)
        val vertices = floatArrayOf(
            x, y + 0.05f, 0.0f,  // Punto superior
            x - 0.05f, y - 0.05f, 0.0f,  // Esquina inferior izquierda
            x + 0.05f, y - 0.05f, 0.0f // Esquina inferior derecha
        )

        // Sin textura (solo color sólido)
        GLES20.glDisableVertexAttribArray(mTextureCoordinateHandle)
        GLES20.glVertexAttribPointer(
            mPositionHandle, 3, GLES20.GL_FLOAT, false, 0,
            ByteBuffer.allocateDirect(vertices.size * 4).order(ByteOrder.nativeOrder())
                .asFloatBuffer().put(vertices).position(0)
        )
        GLES20.glEnableVertexAttribArray(mPositionHandle)

        // Color del puntero (rojo, por ejemplo)
        GLES20.glUniform4f(
            GLES20.glGetUniformLocation(mProgramHandle, "u_Color"),
            1.0f,
            0.0f,
            0.0f,
            1.0f
        )

        // Dibujar el triángulo (flecha)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3)

        // Restaurar configuración de textura
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle)
    }


    private var texturaMinimapa = 0
    private var texturaMinimapaa = 0
    private var texturaMinimapab = 0
    private var texturaMinimapah = 0
    private var texturaMinimapai = 0

    // Lista de ubicaciones predefinidas
    private val ubicaciones: MutableList<FloatArray> = Arrays.asList<FloatArray>(
        floatArrayOf(446.94974f, 19.0f, 192.60959f),  // Edificio A
        floatArrayOf(1359.5217f, 14.0f, -94.37073f),  // Edificio B
        floatArrayOf(-3.0502605f, 29.0f, -427.3904f),  // Edificio H
        floatArrayOf(1252.8394f, 24.0f, -432.67877f) // Edificio I
    )

    // Lista de direcciones para cada ubicación
    private val direcciones: MutableList<FloatArray> = Arrays.asList<FloatArray>(
        floatArrayOf(0.0f, 0.0f, -1.0f),  // Norte (Ubicación 1)
        floatArrayOf(-2.9802322E-8f, 0.0f, 1.0f),  // Sur (Ubicación 3)
        floatArrayOf(0.0f, 0.0f, -1.0f),  // Norte (Ubicación 4)
        floatArrayOf(0.0f, 0.0f, -1.0f) // Norte (Ubicación 5)
    )

    fun ajustarVistaInicial() {
        val rotationMatrix = FloatArray(16)
        Matrix.setIdentityM(rotationMatrix, 0)

        // Rotar 90 grados en el eje X para mirar al horizonte
        ViewDir[0] = 0.98f
        ViewDir[1] = 0.0f
        ViewDir[2] = 0.0f


        // Aplica la rotación inicial
        actualizar_vista_con_matriz(rotationMatrix)
    }

    fun mostrarToastConDuracion(context: Context?, mensaje: String?, duracionEnMilisegundos: Int) {
        // Crear el Toast
        val toast = Toast(context)
        val textView = TextView(context)
        textView.setText(mensaje)
        textView.setBackgroundColor(Color.BLACK)
        textView.setTextColor(Color.WHITE)
        textView.setPadding(20, 10, 20, 10)
        textView.setGravity(Gravity.CENTER)

        toast.setView(textView)
        toast.setDuration(Toast.LENGTH_SHORT)
        toast.show()

        // Ocultar el Toast manualmente después de la duración
        Handler(Looper.getMainLooper()).postDelayed(
            Runnable { toast.cancel() },
            duracionEnMilisegundos.toLong()
        )
    }


    // Índice para rastrear la ubicación actual
    private var indiceUbicacion = 1

    init {
        sensorManager = mActivityContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        Log.d("Purito", "Incializacion Cube Renderer")

        //Matrices
        inicioMatrix = FloatArray(16)
        g_modelMatrix = FloatArray(16)
        g_mvpMatrix = FloatArray(16)
        g_viewMatrix = FloatArray(16)
        g_projectionMatrix = FloatArray(16)

        Matrix.setLookAtM(g_viewMatrix, 0, 0.0f, 0.0f, -3.5f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f)

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



        MoveForward(500.0f)
        MoveUpward(10.0f)
        StrafeRight(450.0f)
    }


    fun NextPosicion() {
        RotatedX = 0.0f
        RotatedY = 0.0f
        RotatedZ = 0.0f

        Position = FloatArray(3)

        // Selecciona la ubicación actual de la lista
        val nuevaPosicion = ubicaciones.get(indiceUbicacion)

        // Asigna la nueva posición
        Position[0] = nuevaPosicion[0]
        Position[1] = nuevaPosicion[1]
        Position[2] = nuevaPosicion[2]

        val nuevaDireccion = direcciones.get(indiceUbicacion)
        ViewDir[0] = nuevaDireccion[0]
        ViewDir[1] = nuevaDireccion[1]
        ViewDir[2] = nuevaDireccion[2]

        when (indiceUbicacion) {
            0 -> mostrarToastConDuracion(mActivityContext, "Estas en el edificio A", 1000)
            1 -> mostrarToastConDuracion(mActivityContext, "Estas en el edificio B", 1000)
            2 -> mostrarToastConDuracion(mActivityContext, "Estas en el edificio H", 1000)
            3 -> mostrarToastConDuracion(mActivityContext, "Estas en xel edificio I", 1000)
        }
        // Incrementa el índice para la próxima vez
        indiceUbicacion++
        if (indiceUbicacion >= ubicaciones.size) {
            indiceUbicacion = 0
        }

        RightVector = FloatArray(3)
        RightVector[0] = 1.0f
        RightVector[1] = 0.0f
        RightVector[2] = 0.0f

        UpVector = FloatArray(3)
        UpVector[0] = 0.0f
        UpVector[1] = 1.0f
        UpVector[2] = 0.0f
    }

    fun ReiniciarPosiciones() {
        RotatedX = 0.0f
        RotatedY = 0.0f
        RotatedZ = 0.0f

        Position = FloatArray(3)
        Position[0] = 446.94974f
        Position[1] = 19.0f
        Position[2] = 192.60959f
        mostrarToastConDuracion(mActivityContext, "Estas en el edificio A", 1000)

        ViewDir = FloatArray(3)
        ViewDir[0] = 0.0f
        ViewDir[1] = 0.0f
        ViewDir[2] = -1.0f

        RightVector = FloatArray(3)
        RightVector[0] = 1.0f
        RightVector[1] = 0.0f
        RightVector[2] = 0.0f

        /*        LeftVector = new float[3];
        LeftVector[0]= -1.0f;
        LeftVector[1]= 0.0f;
        LeftVector[2]= 0.0f;*/
        UpVector = FloatArray(3)
        UpVector[0] = 0.0f
        UpVector[1] = 1.0f
        UpVector[2] = 0.0f
    }

    private fun dibujarMinimapa() {
        // Renderizar el minimapa (visor izquierdo)
        GLES20.glViewport(40, screenHeight.toInt() - 250, 200, 200)
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f) // Fondo negro
        Matrix.setIdentityM(g_mvpMatrix, 0)
        renderizarMinimapa()

        // Calcular la posición del usuario en el minimapa
        val coordMinimapa =
            calcularCoordenadasMinimapa(Position, 1000.0f, 1000.0f) // Ajusta el tamaño del mundo
        dibujarPuntero(coordMinimapa[0], coordMinimapa[1])

        // Renderizar el minimapa (visor derecho)
        GLES20.glViewport(screenWidth.toInt() + 80, screenHeight.toInt() - 250, 200, 200)
        Matrix.setIdentityM(g_mvpMatrix, 0)
        renderizarMinimapa()

        // Dibujar el puntero en el visor derecho
        dibujarPuntero(coordMinimapa[0], coordMinimapa[1])

        Log.d(
            "CoordenadasMinimapa",
            Position[0].toString() + " " + Position[1].toString() + " " + Position[2].toString()
        )
        Log.d(
            "DireccionMinimapa",
            ViewDir[0].toString() + " " + ViewDir[1].toString() + " " + ViewDir[2].toString()
        )
    }


    // Método que renderiza el minimapa
    private fun renderizarMinimapa() {
        // Coordenadas para un cuadrado 2D
        val vertices = floatArrayOf(
            -1.0f, -1.0f, 0.0f,
            1.0f, -1.0f, 0.0f,
            -1.0f, 1.0f, 0.0f,
            1.0f, 1.0f, 0.0f
        )

        val texturas = floatArrayOf(
            1.0f, 0.0f,  // Parte superior derecha
            0.0f, 0.0f,  // Parte superior izquierda
            1.0f, 1.0f,  // Parte inferior derecha
            0.0f, 1.0f // Parte inferior izquierda
        )

        when (indiceUbicacion) {
            0 -> texturaMinimapa = texturaMinimapai
            1 -> texturaMinimapa = texturaMinimapaa
            2 -> texturaMinimapa = texturaMinimapab
            3 -> texturaMinimapa = texturaMinimapah
        }
        cargarPosicion_Textura(vertices, texturas, texturaMinimapa)
        GLES20.glUniformMatrix4fv(mMvpMatrixHandle, 1, false, g_mvpMatrix, 0)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
    }

    private fun dibujarContornoLados() {
        // Coordenadas para el borde izquierdo
        val verticesIzquierda = floatArrayOf(
            -1.0f, -1.0f, 0.0f,  // Inferior izquierdo
            -0.8f, -1.0f, 0.0f,  // Inferior derecho (lado izquierdo)
            -1.0f, 1.0f, 0.0f,  // Superior izquierdo
            -0.8f, 1.0f, 0.0f // Superior derecho (lado izquierdo)
        )

        // Coordenadas para el borde derecho
        val verticesDerecha = floatArrayOf(
            0.8f, -1.0f, 0.0f,  // Inferior izquierdo (lado derecho)
            1.0f, -1.0f, 0.0f,  // Inferior derecho
            0.8f, 1.0f, 0.0f,  // Superior izquierdo (lado derecho)
            1.0f, 1.0f, 0.0f // Superior derecho
        )

        // Dibujar el borde izquierdo
        dibujarBorde(verticesIzquierda)

        // Dibujar el borde derecho
        dibujarBorde(verticesDerecha)
    }

    private fun dibujarBorde(vertices: FloatArray) {
        // Buffer para los vértices
        val vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        vertexBuffer.put(vertices).position(0)

        GLES20.glUseProgram(mProgramHandle) // Usa el programa adecuado

        // Establecer color negro
        GLES20.glUniform4f(
            GLES20.glGetUniformLocation(mProgramHandle, "u_Color"),
            0.0f,
            0.0f,
            0.0f,
            1.0f
        )

        // Configura la posición de los vértices
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)
        GLES20.glEnableVertexAttribArray(mPositionHandle)

        // Dibujar el rectángulo (borde negro)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        GLES20.glDisableVertexAttribArray(mPositionHandle)
    }


    private fun dibujarBordeNegro() {
        // Coordenadas para un borde negro alrededor de la vista
        val vertices = floatArrayOf(
            -1.0f, -1.0f, 0.0f,  // Inferior izquierdo
            1.0f, -1.0f, 0.0f,  // Inferior derecho
            -1.0f, 1.0f, 0.0f,  // Superior izquierdo
            1.0f, 1.0f, 0.0f // Superior derecho
        )

        // Buffer para los vértices
        val vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        vertexBuffer.put(vertices).position(0)

        GLES20.glUseProgram(mProgramHandle) // Usa el programa adecuado

        // Establecer color negro
        GLES20.glUniform4f(
            GLES20.glGetUniformLocation(mProgramHandle, "u_Color"),
            0.0f,
            0.0f,
            0.0f,
            1.0f
        )

        // Configura la posición de los vértices
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)
        GLES20.glEnableVertexAttribArray(mPositionHandle)

        // Dibujar el rectángulo (borde negro)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        GLES20.glDisableVertexAttribArray(mPositionHandle)
    }


    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        Log.d("Purito", "Creando Superficie")

        texturaMinimapa = generarTextura(R.drawable.map)
        texturaMinimapaa = generarTextura(R.drawable.mapaa)
        texturaMinimapab = generarTextura(R.drawable.mapab)
        texturaMinimapah = generarTextura(R.drawable.mapah)
        texturaMinimapai = generarTextura(R.drawable.mapai)

        // Set the background frame color
        //GLES20.glClearColor(5.0f, 0.5f, 0.7f, 1.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)

        val vertexShader = RawResourceReader.readTextFileFromRawResource(
            mActivityContext,
            R.raw.per_pixel_vertex_shader_tex_and_light
        )
        val fragmentShader = RawResourceReader.readTextFileFromRawResource(
            mActivityContext,
            R.raw.per_pixel_fragment_shader_tex_and_light
        )

        val vertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader)
        val fragmentShaderHandle =
            ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader)

        mProgramHandle = ShaderHelper.createAndLinkProgram(
            vertexShaderHandle, fragmentShaderHandle,
            arrayOf<String?>("a_Position", "a_TexCoord", "a_Color")
        )

        // Load the texture
        textura_techoiti = generarTextura(R.drawable.techo1)
        //textura_techoiti = generarTextura(R.drawable.amarillo);
        textura_logo_ext = generarTextura(R.drawable.logo_ext)
        textura_loby_pared = generarTextura(R.drawable.loby_pared)
        textura_puertaiti = generarTextura(R.drawable.puertaiti)
        textura_loby_planta = generarTextura(R.drawable.loby_planta)
        textura_tutorias = generarTextura(R.drawable.tutorias)
        textura_ventanaiti2 = generarTextura(R.drawable.ventanaiti2)
        textura_cesped = generarTextura(R.drawable.cesped)
        textura_cielo = generarTextura(R.drawable.cielo)
        textura_cielo_enfrente = generarTextura(R.drawable.cielo_enfrente)
        textura_cielo_atras = generarTextura(R.drawable.cielo_atras)
        textura_cielo_derecha = generarTextura(R.drawable.cielo_derecha)
        textura_cielo_izquierda = generarTextura(R.drawable.cielo_izquierda)
        textura_amarillo = generarTextura(R.drawable.amarillo)
        textura_azul = generarTextura(R.drawable.azul)
        textura_azulindigo = generarTextura(R.drawable.blanco)
        textura_blanco = generarTextura(R.drawable.blanco)
        textura_cafe = generarTextura(R.drawable.cafe)
        textura_crema = generarTextura(R.drawable.crema)
        textura_cyan = generarTextura(R.drawable.cyan)
        textura_entradaPrincipal = generarTextura(R.drawable.entradaprincipal)
        textura_fucsia = generarTextura(R.drawable.fucsia)
        textura_gindo = generarTextura(R.drawable.gindo)
        textura_gris = generarTextura(R.drawable.gris)
        textura_grisclaro = generarTextura(R.drawable.grisclaro)
        textura_morado = generarTextura(R.drawable.morado)
        textura_naranja = generarTextura(R.drawable.naranja)
        textura_negro = generarTextura(R.drawable.negro)
        textura_oro = generarTextura(R.drawable.oro)
        textura_pisoupv = generarTextura(R.drawable.pisoupv)
        textura_puerta1 = generarTextura(R.drawable.puerta1)
        textura_purpura = generarTextura(R.drawable.purpura)
        textura_rojo = generarTextura(R.drawable.rojo)
        textura_verde = generarTextura(R.drawable.verde)
        textura_verdefosfo = generarTextura(R.drawable.verdefosfo)
        textura_pisoupv2 = generarTextura(R.drawable.pisoupv2)
        textura_ventanaISA = generarTextura(R.drawable.ventanaisa)
        textura_puertage = generarTextura(R.drawable.puertage)
        textura_pared5 = generarTextura(R.drawable.pared5)
        textura_madera = generarTextura(R.drawable.madera)
        textura_puertaPymes = generarTextura(R.drawable.pymes)
        textura_cristal = generarTextura(R.drawable.ventanapymes)
        textura_puertabano = generarTextura(R.drawable.puertabano)
        textura_escolares = generarTextura(R.drawable.escolares)
        textura_cai = generarTextura(R.drawable.grisclaro)
        textura_puertas = generarTextura(R.drawable.puertas)
        textura_pasillooficina = generarTextura(R.drawable.pasillooficina)
        textura_puertasalida = generarTextura(R.drawable.puertasalida)
        textura_biblioteca = generarTextura(R.drawable.biblioteca)
        textura_porbiblioteca = generarTextura(R.drawable.porbiblioteca)
        textura_salamaestros = generarTextura(R.drawable.salamaestros)
        textura_ventanapymesve = generarTextura(R.drawable.ventanapymes)
        textura_maquina = generarTextura(R.drawable.maquina)
        textura_grispymes = generarTextura(R.drawable.grispymes)
        textura_blancopymes = generarTextura(R.drawable.blancopymes)
        textura_paredoficina = generarTextura(R.drawable.paredoficinas)

        Matrix.setIdentityM(g_mvpMatrix, 0)
        Matrix.setIdentityM(g_viewMatrix, 0)
        Matrix.setIdentityM(g_projectionMatrix, 0)
        actualizar_vista()
    }

    private fun generarTextura(textura: Int): Int {
        val t = TextureHelper.loadTexture(mActivityContext, textura)
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D)

        return t
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        // Set the OpenGL viewport to the same size as the surface.
        GLES20.glViewport(0, 0, width, height)
        screenHeight = height.toFloat()
        screenWidth = width.toFloat()
        // Create a new perspective projection matrix. The height will stay the same
        // while the width will vary as per aspect ratio.
        val ratio = width.toFloat() / height
        val left = -ratio
        val right = ratio
        val bottom = -1.0f
        val top = 1.0f
        val near = 1.0f
        val far = 5000.0f

        Matrix.frustumM(g_projectionMatrix, 0, left, right, bottom, top, near, far)
    }

    var axisLX = 0f
    var axisLY = 0f

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        // Set our per-vertex lighting program.
        GLES20.glUseProgram(mProgramHandle)

        // Set program handles for cube drawing.
        mMvpMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MvpMatrix")
        mTextureUniformHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_Sampler")
        mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position")
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_TexCoord")

        //NUEVOOOOO !!!!--------------------------

        aplicarMovimientoJoystick(axisLX, axisLY)

        //------------------------------------------


        actualizar_vista()

        Matrix.multiplyMM(g_modelMatrix, 0, g_projectionMatrix, 0, g_viewMatrix, 0)

        System.arraycopy(g_modelMatrix, 0, inicioMatrix, 0, 16)

        // Se llaman los metodos para mostrar el cielo
        cielo()
        cielo_enfrente()
        cielo_atras()
        cielo_derecha()
        cielo_izquierda()

        base_cesped()

        edificioA()

        Matrix.setIdentityM(g_modelMatrix, 0)
        System.arraycopy(inicioMatrix, 0, g_modelMatrix, 0, 16)

        edificioB()

        Matrix.setIdentityM(g_modelMatrix, 0)
        System.arraycopy(inicioMatrix, 0, g_modelMatrix, 0, 16)

        edificioH()

        Matrix.setIdentityM(g_modelMatrix, 0)
        System.arraycopy(inicioMatrix, 0, g_modelMatrix, 0, 16)


        edificioquefaltaba()

        dibujarMinimapa()
    }

    //CORRECCION DEL MOVIMIENTO EN DIAGONAL -------------------------------------------------------------------------------------------------------------------------------------------------
    
    //pasa porque en la funcion ViewDir no es solo la direccion horizontal, sino que, se recalcula directo desde la matriz de rotacion del telefono en 
    //la funcion de actualizar_vista_con_matriz, que incluye la inclinacion vertical hacia donde se este viendo (arriba/abajo). 
    //Y Como MoveForward nos mueve en la direccion completa de ViewDir, si estás viendo un poco hacia arriba o abajo cuando avanzas, tambien subes o bajas, creo que por eso el efecto diagonal.
    
    fun MoveForward(Distance: Float) {
        
        //Una solucion es que el movimiento hacia adelante/atras solo use la componente horizontal de ViewDir, lit ignorando el eje Y, 
        //dejando el ViewDir completo solo para la camara/render.
        
        //Aplanamos la direccion ignorando el componente vertical (Y)
        val viewDirPlano = floatArrayOf(ViewDir[0], 0f, ViewDir[2])
        val direccionMovimiento = Normalize(viewDirPlano)

        Position = suma(Position, multiplicacion(direccionMovimiento, -Distance))
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    fun StrafeRight(Distance: Float) {
        Position = suma(Position, multiplicacion(RightVector, Distance))
    }

    fun MoveUpward(Distance: Float) {
        Position = suma(Position, multiplicacion(UpVector, Distance))
    }

    fun RotateX(Angle: Float) {
        RotatedX = Angle

        //Rotate viewdir around the right vector:
        ViewDir = Normalize(
            suma(
                multiplicacion(ViewDir, cos((Angle * PIdiv180).toDouble()).toFloat()),
                multiplicacion(UpVector, sin((Angle * PIdiv180).toDouble()).toFloat())
            )
        )

        //now compute the new UpVector (by cross product)
        UpVector = multiplicacion(CrossProduct(ViewDir, RightVector), -1f)
    }

    fun RotateY(Angle: Float) {
        RotatedY = Angle

        //Rotate viewdir around the up vector:
        ViewDir = Normalize(
            resta(
                multiplicacion(ViewDir, cos((Angle * PIdiv180).toDouble()).toFloat()),
                multiplicacion(RightVector, sin((Angle * PIdiv180).toDouble()).toFloat())
            )
        )

        //now compute the new RightVector (by cross product)
        RightVector = CrossProduct(ViewDir, UpVector)
    }

    private fun CrossProduct(u: FloatArray, v: FloatArray): FloatArray {
        val resVector = FloatArray(3)
        resVector[0] = u[1] * v[2] - u[2] * v[1]
        resVector[1] = u[2] * v[0] - u[0] * v[2]
        resVector[2] = u[0] * v[1] - u[1] * v[0]

        return resVector
    }

    fun GetF3dVectorLength(v: FloatArray): Float {
        return (sqrt((v[0] * v[0] + v[1] * v[1] + v[2] * v[2]).toDouble())).toFloat()
    }

    private fun Normalize(v: FloatArray): FloatArray {
        val res = FloatArray(3)
        val l = GetF3dVectorLength(v)
        if (l == 0.0f) {
            res[0] = 0.0f
            res[1] = 0.0f
            res[2] = 0.0f
        } else {
            res[0] = v[0] / l
            res[1] = v[1] / l
            res[2] = v[2] / l
        }
        return res
    }

    private fun multiplicacion(v: FloatArray, r: Float): FloatArray {
        val res = FloatArray(3)
        res[0] = v[0] * r
        res[1] = v[1] * r
        res[2] = v[2] * r
        return res
    }

    private fun suma(v: FloatArray, u: FloatArray): FloatArray {
        val res = FloatArray(3)
        res[0] = v[0] + u[0]
        res[1] = v[1] + u[1]
        res[2] = v[2] + u[2]
        return res
    }

    private fun resta(v: FloatArray, u: FloatArray): FloatArray {
        val res = FloatArray(3)
        res[0] = v[0] - u[0]
        res[1] = v[1] - u[1]
        res[2] = v[2] - u[2]
        return res
    }


    fun Move(Direction: FloatArray) {
        Position = suma(Position, Direction)
    }

    fun actualizar_vista() {
        val ViewPoint = suma(Position, ViewDir)
        Matrix.setLookAtM(
            g_viewMatrix,
            0,
            Position[0],
            Position[1],
            Position[2],
            ViewPoint[0],
            ViewPoint[1],
            ViewPoint[2],
            UpVector[0],
            UpVector[1],
            UpVector[2]
        )
    }

    fun cargarPosicion_Textura(v: FloatArray, vt: FloatArray, textura: Int) {
        //Posiciones---------------------------------------------------------------------------------

        mPositions =
            ByteBuffer.allocateDirect(v.size * mBytesPerFloat).order(ByteOrder.nativeOrder())
                .asFloatBuffer()
        mPositions!!.put(v).position(0)

        // Pass in the position information
        mPositions!!.position(0)
        GLES20.glVertexAttribPointer(
            mPositionHandle,
            mPositionDataSize,
            GLES20.GL_FLOAT,
            false,
            0,
            mPositions
        )

        GLES20.glEnableVertexAttribArray(mPositionHandle)

        //Textura-----------------------------------------------------------------------------------
        mTextureCoordinates = ByteBuffer.allocateDirect(vt.size * mBytesPerFloat)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        mTextureCoordinates!!.put(vt).position(0)

        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)

        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textura)

        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(mTextureUniformHandle, 0)

        // Pass in the texture coordinate information
        mTextureCoordinates!!.position(0)
        GLES20.glVertexAttribPointer(
            mTextureCoordinateHandle,
            mTextureCoordinateDataSize,
            GLES20.GL_FLOAT,
            false,
            0,
            mTextureCoordinates
        )

        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle)

        //----------------------------------------------------------------------------------------------
    }

    //Cuadrilatero parado en el eje de los X
    private fun pared_X(
        nosirve1: Int,
        nosirve2: Int,
        largo: Double,
        alto: Double,
        nosirve3: Int,
        texturaHandle: Int
    ) {
        System.arraycopy(g_modelMatrix, 0, g_mvpMatrix, 0, 16)

        //Coordenadas de la pared
        val vertices = floatArrayOf(
            0f, 0f, 0f,
            0f, alto.toFloat(), 0f,
            largo.toFloat(), 0f, 0f,
            largo.toFloat(), alto.toFloat(), 0f
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

        val n = vertices.size / 3

        // Draw the cube.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, n)
    }


    //Cuadrilatero parado en el eje de los X
    private fun pared_Z(
        nosirve1: Int,
        nosirve2: Int,
        largo: Double,
        alto: Double,
        nosirve3: Int,
        texturaHandle: Int
    ) {
        System.arraycopy(g_modelMatrix, 0, g_mvpMatrix, 0, 16)

        //Coordenadas de la pared
        val vertices = floatArrayOf(
            0f, 0f, 0f,
            0f, alto.toFloat(), 0f,
            0f, 0f, -largo.toFloat(),
            0f, alto.toFloat(), -largo.toFloat()
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

        val n = vertices.size / 3

        // Draw the cube.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, n)
    }


    //Piso o techo
    private fun piso_o_techo(
        nosirve1: Int,
        nosirve2: Int,
        largo: Double,
        ancho: Double,
        nosirve3: Int,
        texturaHandle: Int
    ) {
        System.arraycopy(g_modelMatrix, 0, g_mvpMatrix, 0, 16)

        //Coordenadas de la pared
        val vertices = floatArrayOf(
            0f, 0f, 0f,
            0f, 0f, -ancho.toFloat(),
            largo.toFloat(), 0f, 0f,
            largo.toFloat(), 0f, -ancho.toFloat()
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

        val n = vertices.size / 3

        // Draw the cube.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, n)
    }


    //Piso o techo
    private fun piso_o_techo_especial(
        nosirve1: Int,
        nosirve2: Int,
        x1: Double,
        z1: Double,
        x2: Double,
        z2: Double,
        x3: Double,
        z3: Double,
        nosirve3: Int,
        texturaHandle: Int
    ) {
        System.arraycopy(g_modelMatrix, 0, g_mvpMatrix, 0, 16)

        //Coordenadas de la pared
        val vertices = floatArrayOf(
            0f, 0f, 0f,
            x1.toFloat(), 0f, z1.toFloat(),
            x2.toFloat(), 0f, z2.toFloat(),
            x3.toFloat(), 0f, z3.toFloat()
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

        val n = vertices.size / 3

        // Draw the cube.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, n)
    }

    private fun prisma_cuadrangularLOBY(
        nosirve1: Int,
        nosirve2: Int,
        largo: Double,
        ancho: Double,
        alto: Double,
        nosirve3: Int,
        texturaHandle: Int
    ) {
        //Cara frontal y trasera

        pared_X(nosirve1, nosirve2, largo, alto, nosirve3, texturaHandle)
        Matrix.translateM(g_modelMatrix, 0, 0f, 0f, -ancho.toFloat())
        pared_X(nosirve1, nosirve2, largo, alto, nosirve3, texturaHandle)
        Matrix.translateM(g_modelMatrix, 0, 0f, 0f, ancho.toFloat())

        //Cara izquierda
        pared_Z(nosirve1, nosirve2, ancho, alto, nosirve3, textura_loby_pared)

        //Piso
        piso_o_techo(nosirve1, nosirve2, largo, ancho, nosirve3, texturaHandle)
        Matrix.translateM(g_modelMatrix, 0, largo.toFloat(), 0f, 0f)

        //Cara derecha
        pared_Z(nosirve1, nosirve2, ancho, alto, nosirve3, texturaHandle)
        Matrix.translateM(g_modelMatrix, 0, -largo.toFloat(), alto.toFloat(), 0f)

        //Techo
        piso_o_techo(nosirve1, nosirve2, largo, ancho, nosirve3, texturaHandle)
        Matrix.translateM(g_modelMatrix, 0, 0f, -alto.toFloat(), 0f)
    }


    private fun prisma_cuadrangular(
        nosirve1: Int,
        nosirve2: Int,
        largo: Double,
        ancho: Double,
        alto: Double,
        nosirve3: Int,
        texturaHandle: Int
    ) {
        //System.arraycopy(g_modelMatrix, 0, g_mvpMatrix, 0, 16);

        //Cara frontal y trasera

        pared_X(nosirve1, nosirve2, largo, alto, nosirve3, texturaHandle)
        Matrix.translateM(g_modelMatrix, 0, 0f, 0f, -ancho.toFloat())
        pared_X(nosirve1, nosirve2, largo, alto, nosirve3, texturaHandle)
        Matrix.translateM(g_modelMatrix, 0, 0f, 0f, ancho.toFloat())

        //Cara izquierda
        pared_Z(nosirve1, nosirve2, ancho, alto, nosirve3, texturaHandle)

        //Piso
        piso_o_techo(nosirve1, nosirve2, largo, ancho, nosirve3, texturaHandle)
        Matrix.translateM(g_modelMatrix, 0, largo.toFloat(), 0f, 0f)

        //Cara derecha
        pared_Z(nosirve1, nosirve2, ancho, alto, nosirve3, texturaHandle)
        Matrix.translateM(g_modelMatrix, 0, -largo.toFloat(), alto.toFloat(), 0f)

        //Techo
        piso_o_techo(nosirve1, nosirve2, largo, ancho, nosirve3, texturaHandle)
        Matrix.translateM(g_modelMatrix, 0, 0f, -alto.toFloat(), 0f)
    }


    //Para no escribir todoo de nuevo:v
    private fun g_modelMatrix_translate(x: Double, y: Double, z: Double) {
        Matrix.translateM(g_modelMatrix, 0, x.toFloat(), y.toFloat(), z.toFloat())
    }

    private fun g_modelMatrix_rotate(angulo: Double, x: Double, y: Double, z: Double) {
        Matrix.rotateM(g_modelMatrix, 0, angulo.toFloat(), x.toFloat(), y.toFloat(), z.toFloat())
    }


    //Edificios
    private fun base_cesped() {
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-500.0, -0.1, 700.0)
        piso_o_techo(gll, u_MvpMatrix, 3000.0, 3000.0, imagen_cesped, textura_cesped)
        g_modelMatrix = popMatrix()
    }

    private fun edificioquefaltaba() {
        g_modelMatrix_translate(1700.0, 0.0, -500.0)
        //parte delantera del edificio
        for (i in 0..8) {
            if (i != 4) {
                pushMatrix(g_modelMatrix)
                g_modelMatrix_translate((-100 + (-100 * i)).toDouble(), 0.0, 0.0)
                prisma_cuadrangular(
                    gll,
                    u_MvpMatrix,
                    100.0,
                    100.0,
                    50.0,
                    imagen_grisclaro,
                    textura_paredoficina
                )
                g_modelMatrix = popMatrix()

                pushMatrix(g_modelMatrix)
                g_modelMatrix_translate((-100 + (-100 * i)).toDouble(), 0.0, -300.0)
                prisma_cuadrangular(
                    gll,
                    u_MvpMatrix,
                    100.0,
                    100.0,
                    50.0,
                    imagen_grisclaro,
                    textura_paredoficina
                )
                g_modelMatrix = popMatrix()
            }
        }
        for (i in 0..8) {
            pushMatrix(g_modelMatrix)
            g_modelMatrix_translate((-100 + (-100 * i)).toDouble(), 0.0, -100.0)
            piso_o_techo(gll, u_MvpMatrix, 100.0, 50.0, imagen_pisoupv, textura_pisoupv)
            g_modelMatrix = popMatrix()

            pushMatrix(g_modelMatrix)
            g_modelMatrix_translate((-100 + (-100 * i)).toDouble(), 0.0, -250.0)
            piso_o_techo(gll, u_MvpMatrix, 100.0, 50.0, imagen_pisoupv, textura_pisoupv)
            g_modelMatrix = popMatrix()

            pushMatrix(g_modelMatrix)
            g_modelMatrix_translate((-100 + (-100 * i)).toDouble(), 50.1, -100.0)
            piso_o_techo(gll, u_MvpMatrix, 100.0, 50.0, imagen_pisoupv, textura_pisoupv)
            g_modelMatrix = popMatrix()

            pushMatrix(g_modelMatrix)
            g_modelMatrix_translate((-100 + (-100 * i)).toDouble(), 50.1, -250.0)
            piso_o_techo(gll, u_MvpMatrix, 100.0, 50.0, imagen_pisoupv, textura_pisoupv)
            g_modelMatrix = popMatrix()

            if (i == 4) {
                pushMatrix(g_modelMatrix)
                g_modelMatrix_translate((-100 + (-100 * i)).toDouble(), 0.0, -300.0)
                piso_o_techo(gll, u_MvpMatrix, 100.0, 100.0, imagen_pisoupv, textura_pisoupv)
                g_modelMatrix = popMatrix()

                pushMatrix(g_modelMatrix)
                g_modelMatrix_translate((-120 + (-100 * i)).toDouble(), 0.0, -100.0)
                piso_o_techo(gll, u_MvpMatrix, 160.0, 200.0, imagen_pisoupv, textura_pisoupv)
                g_modelMatrix = popMatrix()

                pushMatrix(g_modelMatrix)
                g_modelMatrix_translate((-100 + (-100 * i)).toDouble(), 0.0, 0.0)
                piso_o_techo(gll, u_MvpMatrix, 100.0, 100.0, imagen_pisoupv, textura_pisoupv)
                g_modelMatrix = popMatrix()

                //
                pushMatrix(g_modelMatrix)
                g_modelMatrix_translate((-100 + (-100 * i)).toDouble(), 50.1, -300.0)
                piso_o_techo(gll, u_MvpMatrix, 100.0, 100.0, imagen_pisoupv, textura_pisoupv)
                g_modelMatrix = popMatrix()

                pushMatrix(g_modelMatrix)
                g_modelMatrix_translate((-120 + (-100 * i)).toDouble(), 50.1, -100.0)
                piso_o_techo(gll, u_MvpMatrix, 160.0, 200.0, imagen_pisoupv, textura_pisoupv)
                g_modelMatrix = popMatrix()

                pushMatrix(g_modelMatrix)
                g_modelMatrix_translate((-100 + (-100 * i)).toDouble(), 50.1, 0.0)
                piso_o_techo(gll, u_MvpMatrix, 100.0, 100.0, imagen_pisoupv, textura_pisoupv)
                g_modelMatrix = popMatrix()
            }
            if (i == 0 || i == 8) {
                pushMatrix(g_modelMatrix)
                g_modelMatrix_translate((-100 + (-100 * i)).toDouble(), 0.0, -100.0)
                piso_o_techo(gll, u_MvpMatrix, 100.0, 200.0, imagen_pisoupv, textura_pisoupv)
                g_modelMatrix = popMatrix()

                pushMatrix(g_modelMatrix)
                g_modelMatrix_translate((-100 + (-100 * i)).toDouble(), 50.1, -100.0)
                piso_o_techo(gll, u_MvpMatrix, 100.0, 200.0, imagen_pisoupv, textura_pisoupv)
                g_modelMatrix = popMatrix()
            }
        }
        //parte trazera del edificio
        for (i in 0..8) {
            if (i == 1 || i == 7) {
                pushMatrix(g_modelMatrix)
                g_modelMatrix_translate((-100 + (-100 * i)).toDouble(), 0.0, -500.0)
                prisma_cuadrangular(
                    gll,
                    u_MvpMatrix,
                    100.0,
                    100.0,
                    50.0,
                    imagen_grisclaro,
                    textura_grisclaro
                )
                g_modelMatrix = popMatrix()

                pushMatrix(g_modelMatrix)
                g_modelMatrix_translate((-100 + (-100 * i)).toDouble(), 0.0, -1300.0)
                prisma_cuadrangular(
                    gll,
                    u_MvpMatrix,
                    100.0,
                    100.0,
                    50.0,
                    imagen_grisclaro,
                    textura_grisclaro
                )
                g_modelMatrix = popMatrix()

                pushMatrix(g_modelMatrix)
                g_modelMatrix_translate((-50 + (-100 * i)).toDouble(), 0.0, -1299.9)
                pared_X(gll, u_MvpMatrix, 50.0, 50.0, imagen_blanco, textura_puertaiti)
                g_modelMatrix = popMatrix()

                pushMatrix(g_modelMatrix)
                g_modelMatrix_translate((-50 + (-100 * i)).toDouble(), 0.0, -600.1)
                pared_X(gll, u_MvpMatrix, 50.0, 50.0, imagen_blanco, textura_puertabano)
                g_modelMatrix = popMatrix()
            }

            if (i < 8) {
                pushMatrix(g_modelMatrix)
                g_modelMatrix_translate(-100.1, 0.0, (-600 + (-100 * i)).toDouble())
                pared_Z(gll, u_MvpMatrix, 50.0, 50.0, imagen_blanco, textura_puertaiti)
                g_modelMatrix = popMatrix()

                pushMatrix(g_modelMatrix)
                g_modelMatrix_translate(-799.9, 0.0, (-600 + (-100 * i)).toDouble())
                pared_Z(gll, u_MvpMatrix, 50.0, 50.0, imagen_blanco, textura_puertaiti)
                g_modelMatrix = popMatrix()

                if (i != 0 && i != 2 && i != 4 && i < 6) {
                    pushMatrix(g_modelMatrix)
                    g_modelMatrix_translate(-700.1, 0.0, (-600 + (-100 * i)).toDouble())
                    pared_Z(gll, u_MvpMatrix, 50.0, 50.0, imagen_blanco, textura_puertaiti)
                    g_modelMatrix = popMatrix()

                    pushMatrix(g_modelMatrix)
                    g_modelMatrix_translate(-199.9, 0.0, (-600 + (-100 * i)).toDouble())
                    pared_Z(gll, u_MvpMatrix, 50.0, 50.0, imagen_blanco, textura_puertaiti)
                    g_modelMatrix = popMatrix()
                }
            }
            if (i != 1 && i != 7) {
                pushMatrix(g_modelMatrix)
                g_modelMatrix_translate((-100 + (-100 * i)).toDouble(), 0.0, -700.0)
                prisma_cuadrangular(
                    gll,
                    u_MvpMatrix,
                    100.0,
                    100.0,
                    50.0,
                    imagen_grisclaro,
                    textura_grisclaro
                )
                g_modelMatrix = popMatrix()

                pushMatrix(g_modelMatrix)
                g_modelMatrix_translate((-100 + (-100 * i)).toDouble(), 0.0, -900.0)
                prisma_cuadrangular(
                    gll,
                    u_MvpMatrix,
                    100.0,
                    100.0,
                    50.0,
                    imagen_grisclaro,
                    textura_grisclaro
                )
                g_modelMatrix = popMatrix()

                pushMatrix(g_modelMatrix)
                g_modelMatrix_translate((-100 + (-100 * i)).toDouble(), 0.0, -1100.0)
                prisma_cuadrangular(
                    gll,
                    u_MvpMatrix,
                    100.0,
                    100.0,
                    50.0,
                    imagen_grisclaro,
                    textura_grisclaro
                )
                g_modelMatrix = popMatrix()
            }
        }
        //salones
        for (i in 0..6) {
            pushMatrix(g_modelMatrix)
            g_modelMatrix_translate(-900.0, 0.0, (-600 + (-100 * i)).toDouble())
            prisma_cuadrangular(
                gll,
                u_MvpMatrix,
                100.0,
                100.0,
                50.0,
                imagen_grisclaro,
                textura_grisclaro
            )
            g_modelMatrix = popMatrix()
            pushMatrix(g_modelMatrix)

            g_modelMatrix_translate(-100.0, 0.0, (-600 + (-100 * i)).toDouble())
            prisma_cuadrangular(
                gll,
                u_MvpMatrix,
                100.0,
                100.0,
                50.0,
                imagen_grisclaro,
                textura_grisclaro
            )
            g_modelMatrix = popMatrix()
        }
        //pisos de la parte trera de este edificio
        for (i in 0..8) {
            pushMatrix(g_modelMatrix)
            g_modelMatrix_translate((-100 + (-100 * i)).toDouble(), 0.0, -600.0)
            piso_o_techo(gll, u_MvpMatrix, 100.0, 200.0, imagen_pisoupv, textura_pisoupv)
            g_modelMatrix = popMatrix()

            pushMatrix(g_modelMatrix)
            g_modelMatrix_translate((-100 + (-100 * i)).toDouble(), 0.0, -1200.0)
            piso_o_techo(gll, u_MvpMatrix, 100.0, 200.0, imagen_pisoupv, textura_pisoupv)
            g_modelMatrix = popMatrix()

            pushMatrix(g_modelMatrix)
            g_modelMatrix_translate(-200.0, 0.0, (-600 + (-100 * i)).toDouble())
            piso_o_techo(gll, u_MvpMatrix, 100.0, 200.0, imagen_pisoupv, textura_pisoupv)
            g_modelMatrix = popMatrix()

            pushMatrix(g_modelMatrix)
            g_modelMatrix_translate(-800.0, 0.0, (-600 + (-100 * i)).toDouble())
            piso_o_techo(gll, u_MvpMatrix, 100.0, 200.0, imagen_pisoupv, textura_pisoupv)
            g_modelMatrix = popMatrix()
            /**/
            pushMatrix(g_modelMatrix)
            g_modelMatrix_translate((-100 + (-100 * i)).toDouble(), 50.1, -600.0)
            piso_o_techo(gll, u_MvpMatrix, 100.0, 200.0, imagen_pisoupv, textura_pisoupv)
            g_modelMatrix = popMatrix()

            pushMatrix(g_modelMatrix)
            g_modelMatrix_translate((-100 + (-100 * i)).toDouble(), 50.1, -1200.0)
            piso_o_techo(gll, u_MvpMatrix, 100.0, 200.0, imagen_pisoupv, textura_pisoupv)
            g_modelMatrix = popMatrix()

            pushMatrix(g_modelMatrix)
            g_modelMatrix_translate(-200.0, 50.1, (-600 + (-100 * i)).toDouble())
            piso_o_techo(gll, u_MvpMatrix, 100.0, 200.0, imagen_pisoupv, textura_pisoupv)
            g_modelMatrix = popMatrix()

            pushMatrix(g_modelMatrix)
            g_modelMatrix_translate(-800.0, 50.1, (-600 + (-100 * i)).toDouble())
            piso_o_techo(gll, u_MvpMatrix, 100.0, 200.0, imagen_pisoupv, textura_pisoupv)
            g_modelMatrix = popMatrix()
        }
        /**Seccion de paredes en Z */
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-900.0, 0.0, -100.0)
        pared_Z(gll, u_MvpMatrix, 50.0, 50.0, imagen_blanco, textura_grispymes)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-900.0, 40.0, -50.0)
        pared_Z(gll, u_MvpMatrix, 200.0, 10.0, imagen_blanco, textura_azulindigo)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-900.0, 0.0, -250.0)
        pared_Z(gll, u_MvpMatrix, 50.0, 50.0, imagen_blanco, textura_grispymes)
        g_modelMatrix = popMatrix()
        /** */
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-0.0, 0.0, -100.0)
        pared_Z(gll, u_MvpMatrix, 50.0, 50.0, imagen_blanco, textura_grispymes)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-0.0, 40.0, -50.0)
        pared_Z(gll, u_MvpMatrix, 200.0, 10.0, imagen_blanco, textura_azulindigo)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-0.0, 0.0, -250.0)
        pared_Z(gll, u_MvpMatrix, 50.0, 50.0, imagen_blanco, textura_grispymes)
        g_modelMatrix = popMatrix()


        /** */


        //pasillo de la entrada
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-400.0, 0.0, -100.0)
        pared_Z(gll, u_MvpMatrix, 50.0, 50.0, imagen_blanco, textura_cafe)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-400.0, 40.0, -100.0)
        pared_Z(gll, u_MvpMatrix, 200.0, 10.0, imagen_blanco, textura_cafe)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-400.0, 0.0, -250.0)
        pared_Z(gll, u_MvpMatrix, 50.0, 50.0, imagen_blanco, textura_cafe)
        g_modelMatrix = popMatrix()



        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-500.0, 0.0, -100.0)
        pared_Z(gll, u_MvpMatrix, 50.0, 50.0, imagen_blanco, textura_cafe)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-500.0, 40.0, -100.0)
        pared_Z(gll, u_MvpMatrix, 200.0, 10.0, imagen_blanco, textura_cafe)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-500.0, 0.0, -250.0)
        pared_Z(gll, u_MvpMatrix, 50.0, 50.0, imagen_blanco, textura_cafe)
        g_modelMatrix = popMatrix()


        /**
         * pilares de los cubos y pasillo */
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-110.0, 0.0, -150.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 50.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-360.0, 0.0, -150.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 50.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()
        /**/
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-360.0, 0.0, -150.0)
        pared_Z(gll, u_MvpMatrix, 100.0, 10.0, imagen_blanco, textura_azulindigo)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-100.0, 0.0, -150.0)
        pared_Z(gll, u_MvpMatrix, 100.0, 10.0, imagen_blanco, textura_azulindigo)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-360.0, 0.0, -150.0)
        pared_X(gll, u_MvpMatrix, 250.0, 10.0, imagen_blanco, textura_azulindigo)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-360.0, 0.0, -250.0)
        pared_X(gll, u_MvpMatrix, 250.0, 10.0, imagen_blanco, textura_azulindigo)
        g_modelMatrix = popMatrix()
        /**/
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-360.0, 40.0, -150.0)
        pared_Z(gll, u_MvpMatrix, 100.0, 10.0, imagen_blanco, textura_azulindigo)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-100.0, 40.0, -150.0)
        pared_Z(gll, u_MvpMatrix, 100.0, 10.0, imagen_blanco, textura_azulindigo)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-360.0, 40.0, -150.0)
        pared_X(gll, u_MvpMatrix, 250.0, 10.0, imagen_blanco, textura_azulindigo)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-360.0, 40.0, -250.0)
        pared_X(gll, u_MvpMatrix, 250.0, 10.0, imagen_blanco, textura_azulindigo)
        g_modelMatrix = popMatrix()

        // /**// */
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-360.0, 0.1, -150.0)
        piso_o_techo(gll, u_MvpMatrix, 260.0, 100.0, imagen_pisoupv, textura_gindo)
        g_modelMatrix = popMatrix()



        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-110.0, 0.0, -240.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 50.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-360.0, 0.0, -240.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 50.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()


        /**pilares de los cubos y pasillo */
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-530.0, 0.0, -150.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 50.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-800.0, 0.0, -150.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 50.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()
        /**/
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-800.0, 40.0, -150.0)
        pared_Z(gll, u_MvpMatrix, 100.0, 10.0, imagen_blanco, textura_azulindigo)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-520.0, 40.0, -150.0)
        pared_Z(gll, u_MvpMatrix, 100.0, 10.0, imagen_blanco, textura_azulindigo)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-800.0, 40.0, -150.0)
        pared_X(gll, u_MvpMatrix, 280.0, 10.0, imagen_blanco, textura_azulindigo)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-800.0, 40.0, -250.0)
        pared_X(gll, u_MvpMatrix, 280.0, 10.0, imagen_blanco, textura_azulindigo)
        g_modelMatrix = popMatrix()
        /**/
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-800.0, 0.0, -150.0)
        pared_Z(gll, u_MvpMatrix, 100.0, 10.0, imagen_blanco, textura_azulindigo)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-520.0, 0.0, -150.0)
        pared_Z(gll, u_MvpMatrix, 100.0, 10.0, imagen_blanco, textura_azulindigo)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-800.0, 0.0, -150.0)
        pared_X(gll, u_MvpMatrix, 280.0, 10.0, imagen_blanco, textura_azulindigo)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-800.0, 0.0, -250.0)
        pared_X(gll, u_MvpMatrix, 280.0, 10.0, imagen_blanco, textura_azulindigo)
        g_modelMatrix = popMatrix()
        /**/
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-800.0, 0.1, -150.0)
        piso_o_techo(gll, u_MvpMatrix, 280.0, 100.0, imagen_pisoupv, textura_gindo)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-530.0, 0.0, -240.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 50.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-800.0, 0.0, -240.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 50.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()

        /**////////////////////// */
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-400.0, 0.0, -600.0)
        pared_X(gll, u_MvpMatrix, 300.0, 50.0, imagen_blanco, textura_grispymes)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-500.0, 0.1, -400.0)
        piso_o_techo(gll, u_MvpMatrix, 100.0, 200.0, imagen_pisoupv, textura_gindo)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-500.0, 50.1, -400.0)
        piso_o_techo(gll, u_MvpMatrix, 100.0, 200.0, imagen_pisoupv, textura_grispymes)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-800.0, 0.0, -600.0)
        pared_X(gll, u_MvpMatrix, 300.0, 50.0, imagen_blanco, textura_grispymes)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-500.0, 0.0, -600.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 50.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-500.0, 0.0, -550.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 50.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-500.0, 0.0, -450.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 50.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-400.0, 0.0, -400.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 50.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-400.0, 0.0, -450.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 50.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-400.0, 0.0, -550.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 50.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-500.0, 0.0, -400.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 50.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-400.0, 0.0, -600.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 50.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()

        //pilares para mas estetica
        for (i in 0..8) {
            pushMatrix(g_modelMatrix)
            g_modelMatrix_translate((-100 + (-100 * i)).toDouble(), 0.0, -100.0)
            prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 50.0, imagen_morado, textura_morado)
            g_modelMatrix = popMatrix()

            pushMatrix(g_modelMatrix)
            g_modelMatrix_translate((-100 + (-100 * i)).toDouble(), 0.0, -290.0)
            prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 50.0, imagen_morado, textura_morado)
            g_modelMatrix = popMatrix()

            if (i < 7) {
                pushMatrix(g_modelMatrix)
                g_modelMatrix_translate(-110.0, 0.0, (-600 + (-100 * i)).toDouble())
                prisma_cuadrangular(
                    gll,
                    u_MvpMatrix,
                    10.0,
                    10.0,
                    50.0,
                    imagen_morado,
                    textura_morado
                )
                g_modelMatrix = popMatrix()

                pushMatrix(g_modelMatrix)
                g_modelMatrix_translate(-700.0, 0.0, (-590 + (-100 * i)).toDouble())
                prisma_cuadrangular(
                    gll,
                    u_MvpMatrix,
                    10.0,
                    10.0,
                    50.0,
                    imagen_morado,
                    textura_morado
                )
                g_modelMatrix = popMatrix()



                pushMatrix(g_modelMatrix)
                g_modelMatrix_translate(-200.0, 0.0, (-600 + (-100 * i)).toDouble())
                prisma_cuadrangular(
                    gll,
                    u_MvpMatrix,
                    10.0,
                    10.0,
                    50.0,
                    imagen_morado,
                    textura_morado
                )
                g_modelMatrix = popMatrix()

                pushMatrix(g_modelMatrix)
                g_modelMatrix_translate(-300.0, 0.0, (-590 + (-100 * i)).toDouble())
                prisma_cuadrangular(
                    gll,
                    u_MvpMatrix,
                    10.0,
                    10.0,
                    50.0,
                    imagen_morado,
                    textura_morado
                )
                g_modelMatrix = popMatrix()

                pushMatrix(g_modelMatrix)
                g_modelMatrix_translate(-400.0, 0.0, (-600 + (-100 * i)).toDouble())
                prisma_cuadrangular(
                    gll,
                    u_MvpMatrix,
                    10.0,
                    10.0,
                    50.0,
                    imagen_morado,
                    textura_morado
                )
                g_modelMatrix = popMatrix()
                pushMatrix(g_modelMatrix)

                g_modelMatrix_translate(-500.0, 0.0, (-590 + (-100 * i)).toDouble())
                prisma_cuadrangular(
                    gll,
                    u_MvpMatrix,
                    10.0,
                    10.0,
                    50.0,
                    imagen_morado,
                    textura_morado
                )
                g_modelMatrix = popMatrix()
                pushMatrix(g_modelMatrix)

                g_modelMatrix_translate(-600.0, 0.0, (-600 + (-100 * i)).toDouble())
                prisma_cuadrangular(
                    gll,
                    u_MvpMatrix,
                    10.0,
                    10.0,
                    50.0,
                    imagen_morado,
                    textura_morado
                )
                g_modelMatrix = popMatrix()
                pushMatrix(g_modelMatrix)

                g_modelMatrix_translate(0.0, 0.0, (-590 + (-100 * i)).toDouble())
                prisma_cuadrangular(
                    gll,
                    u_MvpMatrix,
                    10.0,
                    10.0,
                    50.0,
                    imagen_morado,
                    textura_morado
                )
                g_modelMatrix = popMatrix()
                pushMatrix(g_modelMatrix)

                g_modelMatrix_translate(-800.0, 0.0, (-600 + (-100 * i)).toDouble())
                prisma_cuadrangular(
                    gll,
                    u_MvpMatrix,
                    10.0,
                    10.0,
                    50.0,
                    imagen_morado,
                    textura_morado
                )
                g_modelMatrix = popMatrix()
                pushMatrix(g_modelMatrix)
            }

            pushMatrix(g_modelMatrix)
            g_modelMatrix_translate(-800.0, 0.0, -1300.0)
            pared_X(gll, u_MvpMatrix, 800.0, 10.0, imagen_blanco, textura_grispymes)
            g_modelMatrix = popMatrix()

            pushMatrix(g_modelMatrix)
            g_modelMatrix_translate(-800.0, 40.0, -1300.0)
            pared_X(gll, u_MvpMatrix, 800.0, 10.0, imagen_blanco, textura_grispymes)
            g_modelMatrix = popMatrix()

            pushMatrix(g_modelMatrix)
            g_modelMatrix_translate(-700.0, 0.2, -800.0)
            piso_o_techo(gll, u_MvpMatrix, 500.0, 100.0, imagen_pisoupv, textura_gindo)
            g_modelMatrix = popMatrix()

            pushMatrix(g_modelMatrix)
            g_modelMatrix_translate(-700.0, 0.2, -1000.0)
            piso_o_techo(gll, u_MvpMatrix, 500.0, 100.0, imagen_pisoupv, textura_gindo)
            g_modelMatrix = popMatrix()
        }

        for (i in 0..8) {
            if (i != 4) {
                pushMatrix(g_modelMatrix)
                g_modelMatrix_translate((-100 + (-100 * i)).toDouble(), 0.0, -100.1)
                pared_X(gll, u_MvpMatrix, 50.0, 50.0, imagen_blanco, textura_puertaiti)
                g_modelMatrix = popMatrix()


                pushMatrix(g_modelMatrix)
                g_modelMatrix_translate((-100 + (-100 * i)).toDouble(), 0.0, -299.9)
                pared_X(gll, u_MvpMatrix, 50.0, 50.0, imagen_blanco, textura_puertaiti)
                g_modelMatrix = popMatrix()
            }
        }
    }

    private fun edificioA() {
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(0.0, 0.0, 0.0)
        pared_X(gll, u_MvpMatrix, 50.0, 50.0, imagen_blanco, textura_blanco)
        g_modelMatrix = popMatrix()

        /** Torre de escaleras */
        //peredes
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-50.0, 0.0, -50.0)
        //pared fuera de las escaleras oficinas
        pared_X(gll, u_MvpMatrix, 50.0, 100.0, imagen_blanco, textura_blanco)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-50.0, 0.0, -100.0)
        pared_X(gll, u_MvpMatrix, 50.0, 100.0, imagen_blanco, textura_blanco)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-50.0, 0.0, -50.0)
        pared_Z(gll, u_MvpMatrix, 50.0, 100.0, imagen_gris, textura_gris)
        g_modelMatrix = popMatrix()
        //piso
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-50.0, 25.0, -50.0)
        piso_o_techo(gll, u_MvpMatrix, 25.0, 50.0, imagen_pisoupv, textura_pisoupv)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-25.0, 0.0, -75.0)
        pared_X(gll, u_MvpMatrix, 25.0, 100.0, imagen_grisclaro, textura_grisclaro)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(0.0, 0.0, -75.0)
        pared_Z(gll, u_MvpMatrix, 25.0, 50.0, imagen_gris, textura_gris)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(0.0, 50.0, -50.0)
        pared_Z(gll, u_MvpMatrix, 25.0, 50.0, imagen_gris, textura_gris)
        g_modelMatrix = popMatrix()
        //escaleras
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(0.0, 0.0, -50.0)
        pared_Z(gll, u_MvpMatrix, 25.0, 8.0, imagen_grisclaro, textura_grisclaro)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-8.0, 8.0, -50.0)
        piso_o_techo(gll, u_MvpMatrix, 8.0, 25.0, imagen_pisoupv, textura_pisoupv)
        g_modelMatrix = popMatrix()
        //2da
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-8.0, 8.0, -50.0)
        pared_Z(gll, u_MvpMatrix, 25.0, 8.0, imagen_gris, textura_gris)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-16.0, 16.0, -50.0)
        piso_o_techo(gll, u_MvpMatrix, 8.0, 25.0, imagen_pisoupv, textura_pisoupv)
        g_modelMatrix = popMatrix()
        //3ra
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-16.0, 16.0, -50.0)
        pared_Z(gll, u_MvpMatrix, 25.0, 9.0, imagen_gris, textura_gris)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-25.0, 25.0, -50.0)
        piso_o_techo(gll, u_MvpMatrix, 9.0, 25.0, imagen_pisoupv, textura_pisoupv)
        g_modelMatrix = popMatrix()
        //4ta
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-25.0, 25.0, -75.0)
        pared_Z(gll, u_MvpMatrix, 25.0, 9.0, imagen_gris, textura_gris)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-25.0, 33.0, -75.0)
        piso_o_techo(gll, u_MvpMatrix, 9.0, 25.0, imagen_pisoupv, textura_pisoupv)
        g_modelMatrix = popMatrix()
        //5ta
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-16.0, 32.0, -75.0)
        pared_Z(gll, u_MvpMatrix, 25.0, 9.0, imagen_gris, textura_gris)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-16.0, 41.0, -75.0)
        piso_o_techo(gll, u_MvpMatrix, 8.0, 25.0, imagen_pisoupv, textura_pisoupv)
        g_modelMatrix = popMatrix()
        //6ta
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-8.0, 41.0, -75.0)
        pared_Z(gll, u_MvpMatrix, 25.0, 9.5, imagen_grisclaro, textura_grisclaro)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-8.0, 50.5, -75.0)
        piso_o_techo(gll, u_MvpMatrix, 9.0, 25.0, imagen_pisoupv, textura_pisoupv)
        g_modelMatrix = popMatrix()
        /** Fin de Torre de escaleras */
        //Oficina del Rector
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-100.0, 0.0, -100.0)
        prisma_cuadrangular(
            gll,
            u_MvpMatrix,
            100.0,
            100.0,
            50.0,
            imagen_grisclaro,
            textura_grisclaro
        )
        g_modelMatrix = popMatrix()
        //cubiculos de arriba del rector
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-100.0, 50.0, -100.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 100.0, 100.0, 50.0, imagen_blanco, textura_blanco)
        g_modelMatrix = popMatrix()
        //cuartos
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(50.0, 0.0, 0.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 50.0, 50.0, 50.0, imagen_blanco, textura_blanco)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(100.0, 0.0, 0.0)
        prisma_cuadrangular(
            gll,
            u_MvpMatrix,
            110.0,
            110.0,
            50.0,
            imagen_grisclaro,
            textura_grisclaro
        )
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(200.0, 0.0, 0.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 100.0, 100.0, 50.0, imagen_blanco, textura_blanco)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(300.0, 0.0, 0.0)
        prisma_cuadrangular(
            gll,
            u_MvpMatrix,
            100.0,
            100.0,
            50.0,
            imagen_grisclaro,
            textura_grisclaro
        )
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(500.0, 0.0, 0.0)
        prisma_cuadrangularLOBY(
            gll,
            u_MvpMatrix,
            100.0,
            100.0,
            50.0,
            imagen_loby_pared,
            textura_blanco
        )
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(600.0, 0.0, 0.0)
        prisma_cuadrangular(
            gll,
            u_MvpMatrix,
            100.0,
            100.0,
            50.0,
            imagen_grisclaro,
            textura_grisclaro
        )
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(700.0, 0.0, 0.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 100.0, 100.0, 50.0, imagen_blanco, textura_blanco)
        g_modelMatrix = popMatrix()
        /**/
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(500.0, 0.0, -150.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 100.0, 100.0, 50.0, imagen_blanco, textura_blanco)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(600.0, 0.0, -150.0)
        prisma_cuadrangular(
            gll,
            u_MvpMatrix,
            100.0,
            100.0,
            50.0,
            imagen_grisclaro,
            textura_grisclaro
        )
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(700.0, 0.0, -150.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 100.0, 100.0, 50.0, imagen_blanco, textura_blanco)
        g_modelMatrix = popMatrix()
        /** */
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(0.0, 0.0, -150.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 100.0, 100.0, 50.0, imagen_gris, textura_gris)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(150.0, 0.0, -140.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 100.0, 30.0, 50.0, imagen_blanco, textura_blanco)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(300.0, 0.0, -150.0)
        prisma_cuadrangular(
            gll,
            u_MvpMatrix,
            100.0,
            100.0,
            50.0,
            imagen_grisclaro,
            textura_grisclaro
        )
        g_modelMatrix = popMatrix()
        /** */
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(100.0, 0.0, -220.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 100.0, 30.0, 50.0, imagen_blanco, textura_blanco)
        g_modelMatrix = popMatrix()
        /** */
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(200.0, 0.0, -220.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 100.0, 30.0, 50.0, imagen_gris, textura_gris)
        g_modelMatrix = popMatrix()
        /**PISO DEL PRIMER PISO */
        run {
            var i = 0
            while (i <= 800) {
                pushMatrix(g_modelMatrix)
                g_modelMatrix_translate(i.toDouble(), 0.25, 0.0)
                piso_o_techo(gll, u_MvpMatrix, 100.0, 70.0, imagen_pisoupv, textura_pisoupv)
                g_modelMatrix = popMatrix()

                pushMatrix(g_modelMatrix)
                g_modelMatrix_translate(i.toDouble(), 0.25, -70.0)
                piso_o_techo(gll, u_MvpMatrix, 100.0, 70.0, imagen_pisoupv, textura_pisoupv)
                g_modelMatrix = popMatrix()

                pushMatrix(g_modelMatrix)
                g_modelMatrix_translate(i.toDouble(), 0.25, -120.0)
                piso_o_techo(gll, u_MvpMatrix, 100.0, 130.0, imagen_pisoupv, textura_pisoupv)
                g_modelMatrix = popMatrix()
                //parte del suelo superior
                if (i < 400 || (i > 500 && i < 800)) {
                    pushMatrix(g_modelMatrix)
                    g_modelMatrix_translate(i.toDouble(), 50.5, 0.0)
                    piso_o_techo(gll, u_MvpMatrix, 100.0, 100.0, imagen_pisoupv, textura_pisoupv)
                    g_modelMatrix = popMatrix()
                    //
                    if (i > 400 && i < 500) {
                    }
                }
                if (i < 800) {
                    pushMatrix(g_modelMatrix)
                    g_modelMatrix_translate(i.toDouble(), 100.1, 0.0)
                    piso_o_techo(gll, u_MvpMatrix, 100.0, 100.0, imagen_pisoupv, textura_pisoupv)
                    g_modelMatrix = popMatrix()
                }

                pushMatrix(g_modelMatrix)
                g_modelMatrix_translate(i.toDouble(), 50.5, -100.0)
                piso_o_techo(gll, u_MvpMatrix, 100.0, 150.0, imagen_pisoupv, textura_pisoupv)
                g_modelMatrix = popMatrix()
                //
                pushMatrix(g_modelMatrix)
                g_modelMatrix_translate(i.toDouble(), 100.2, -100.0)
                piso_o_techo(gll, u_MvpMatrix, 100.0, 150.0, imagen_pisoupv, textura_pisoupv)
                g_modelMatrix = popMatrix()
                i = i + 99

                i++
            }
        }
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-50.0, 0.1, -50.0)
        piso_o_techo(gll, u_MvpMatrix, 50.0, 50.0, imagen_pisoupv, textura_pisoupv)
        g_modelMatrix = popMatrix()


        /** */
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(400.0, 50.5, -60.0)
        piso_o_techo(gll, u_MvpMatrix, 100.0, 40.0, imagen_pisoupv, textura_pisoupv)
        g_modelMatrix = popMatrix()
        /** */
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(400.0, 50.0, -60.0)
        pared_X(gll, u_MvpMatrix, 100.0, 10.0, imagen_grisclaro, textura_grisclaro)
        g_modelMatrix = popMatrix()
        /** */
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(0.0, 50.0, 0.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 100.0, 50.0, 50.0, imagen_gris, textura_gris)
        g_modelMatrix = popMatrix()
        /** */
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(100.0, 50.0, 0.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 110.0, 100.0, 50.0, imagen_gris, textura_gris)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(200.0, 50.0, 0.0)
        prisma_cuadrangular(
            gll,
            u_MvpMatrix,
            100.0,
            100.0,
            50.0,
            imagen_grisclaro,
            textura_grisclaro
        )
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(300.0, 50.0, 0.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 100.0, 100.0, 50.0, imagen_blanco, textura_blanco)
        g_modelMatrix = popMatrix()
        //
        //se omite esta para el balcon de iti
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(500.0, 50.0, 0.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 100.0, 100.0, 50.0, imagen_gris, textura_gris)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(600.0, 50.0, 0.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 100.0, 100.0, 50.0, imagen_blanco, textura_blanco)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(700.0, 50.0, 0.0)
        prisma_cuadrangular(
            gll,
            u_MvpMatrix,
            100.0,
            100.0,
            50.0,
            imagen_grisclaro,
            textura_grisclaro
        )
        g_modelMatrix = popMatrix()
        /**////////////////////////////////////////// */
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(0.0, 50.0, -150.0)
        prisma_cuadrangular(
            gll,
            u_MvpMatrix,
            200.0,
            100.0,
            50.0,
            imagen_grisclaro,
            textura_grisclaro
        )
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(200.0, 50.0, -150.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 100.0, 100.0, 50.0, imagen_gris, textura_gris)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(300.0, 50.0, -150.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 100.0, 100.0, 50.0, imagen_blanco, textura_blanco)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(400.0, 50.0, -150.0)
        prisma_cuadrangular(
            gll,
            u_MvpMatrix,
            100.0,
            100.0,
            50.0,
            imagen_grisclaro,
            textura_grisclaro
        )
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(500.0, 50.0, -150.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 100.0, 100.0, 50.0, imagen_blanco, textura_blanco)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(600.0, 50.0, -150.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 100.0, 100.0, 50.0, imagen_gris, textura_gris)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(700.0, 50.0, -150.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 100.0, 100.0, 50.0, imagen_blanco, textura_blanco)
        g_modelMatrix = popMatrix()
        //
        // Torre de escaleras2//////////////////////////////////////////////////////////////////////////////////////////////////////////
        //peredes
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(900.0, 0.0, -150.0)
        pared_X(gll, u_MvpMatrix, 50.0, 100.0, imagen_blanco, textura_blanco)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(900.0, 0.0, -200.0)
        pared_X(gll, u_MvpMatrix, 50.0, 100.0, imagen_blanco, textura_blanco)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(950.0, 0.0, -150.0)
        pared_Z(gll, u_MvpMatrix, 50.0, 100.0, imagen_gris, textura_gris)
        g_modelMatrix = popMatrix()
        //piso
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(925.0, 25.0, -150.0)
        piso_o_techo(gll, u_MvpMatrix, 25.0, 50.0, imagen_pisoupv, textura_pisoupv)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(900.0, 0.0, -175.0)
        pared_X(gll, u_MvpMatrix, 25.0, 100.0, imagen_grisclaro, textura_grisclaro)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(900.0, 0.0, -150.0)
        pared_Z(gll, u_MvpMatrix, 25.0, 50.0, imagen_gris, textura_gris)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(900.0, 50.0, -175.0)
        pared_Z(gll, u_MvpMatrix, 25.0, 50.0, imagen_gris, textura_gris)
        g_modelMatrix = popMatrix()
        //escaleras
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(900.0, 0.0, -175.0)
        pared_Z(gll, u_MvpMatrix, 25.0, 8.0, imagen_grisclaro, textura_grisclaro)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(900.0, 8.0, -175.0)
        piso_o_techo(gll, u_MvpMatrix, 8.0, 25.0, imagen_pisoupv, textura_pisoupv)
        g_modelMatrix = popMatrix()
        //2da
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(908.0, 8.0, -175.0)
        pared_Z(gll, u_MvpMatrix, 25.0, 8.0, imagen_gris, textura_gris)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(908.0, 16.0, -175.0)
        piso_o_techo(gll, u_MvpMatrix, 8.0, 25.0, imagen_pisoupv, textura_pisoupv)
        g_modelMatrix = popMatrix()
        //3ra
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(916.0, 16.0, -175.0)
        pared_Z(gll, u_MvpMatrix, 25.0, 9.0, imagen_gris, textura_gris)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(916.0, 25.0, -175.0)
        piso_o_techo(gll, u_MvpMatrix, 9.0, 25.0, imagen_pisoupv, textura_pisoupv)
        g_modelMatrix = popMatrix()
        //4ta
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(925.0, 25.0, -150.0)
        pared_Z(gll, u_MvpMatrix, 25.0, 9.0, imagen_gris, textura_gris)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(916.0, 33.0, -150.0)
        piso_o_techo(gll, u_MvpMatrix, 9.0, 25.0, imagen_pisoupv, textura_pisoupv)
        g_modelMatrix = popMatrix()
        //5ta
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(916.0, 32.0, -150.0)
        pared_Z(gll, u_MvpMatrix, 25.0, 9.0, imagen_gris, textura_gris)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(908.0, 41.0, -150.0)
        piso_o_techo(gll, u_MvpMatrix, 8.0, 25.0, imagen_pisoupv, textura_pisoupv)
        g_modelMatrix = popMatrix()
        //6ta
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(908.0, 41.0, -150.0)
        pared_Z(gll, u_MvpMatrix, 25.0, 9.5, imagen_grisclaro, textura_grisclaro)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(900.0, 50.5, -150.0)
        piso_o_techo(gll, u_MvpMatrix, 9.0, 25.0, imagen_pisoupv, textura_pisoupv)
        g_modelMatrix = popMatrix()
        /** Fin de Torre de escaleras */
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(800.0, 50.0, -100.0)
        pared_X(gll, u_MvpMatrix, 100.0, 50.0, imagen_gris, textura_gris)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(800.0, 50.0, -180.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 100.0, 70.0, 50.0, imagen_gris, textura_gris)
        g_modelMatrix = popMatrix()
        /**Parte del elevador */
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(900.5, 0.0, -99.9)
        pared_X(gll, u_MvpMatrix, 10.0, 100.0, imagen_grisclaro, textura_grisclaro)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(940.5, 0.0, -99.9)
        pared_X(gll, u_MvpMatrix, 10.0, 100.0, imagen_grisclaro, textura_grisclaro)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(950.5, 0.0, -100.0)
        pared_Z(gll, u_MvpMatrix, 10.0, 100.0, imagen_grisclaro, textura_grisclaro)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(950.5, 0.0, -140.0)
        pared_Z(gll, u_MvpMatrix, 10.0, 100.0, imagen_grisclaro, textura_grisclaro)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(900.0, 0.0, -100.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 50.0, 50.0, 50.0, imagen_gris, textura_gris)
        g_modelMatrix = popMatrix()
        /** techos faltantes */
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(900.0, 100.0, -100.0)
        piso_o_techo(gll, u_MvpMatrix, 50.0, 100.0, imagen_grisclaro, textura_grisclaro)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-50.0, 100.0, -50.0)
        piso_o_techo(gll, u_MvpMatrix, 50.0, 50.0, imagen_grisclaro, textura_grisclaro)
        g_modelMatrix = popMatrix()
        /** */
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(0.0, 0.0, 100.0)
        pared_X(gll, u_MvpMatrix, 90.0, 100.0, imagen_azulindigo, textura_azulindigo)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(170.0, 0.0, 100.0)
        pared_X(gll, u_MvpMatrix, 60.0, 100.0, imagen_azulindigo, textura_azulindigo)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(310.0, 0.0, 100.0)
        pared_X(gll, u_MvpMatrix, 90.0, 100.0, imagen_azulindigo, textura_azulindigo)
        g_modelMatrix = popMatrix()
        /**/
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(500.0, 0.0, 100.0)

        // PARED DEL LOGO
        pared_X(gll, u_MvpMatrix, 90.0, 100.0, imagen_azulindigo, textura_logo_ext)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(670.0, 0.0, 100.0)
        pared_X(gll, u_MvpMatrix, 60.0, 100.0, imagen_azulindigo, textura_azulindigo)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(810.0, 0.0, 100.0)
        pared_X(gll, u_MvpMatrix, 90.0, 100.0, imagen_azulindigo, textura_azulindigo)
        g_modelMatrix = popMatrix()
        /** */
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(0.0, 0.0, -340.0)
        pared_X(gll, u_MvpMatrix, 90.0, 100.0, imagen_azulindigo, textura_azulindigo)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(170.0, 0.0, -340.0)
        pared_X(gll, u_MvpMatrix, 60.0, 100.0, imagen_azulindigo, textura_azulindigo)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(310.0, 0.0, -340.0)
        pared_X(gll, u_MvpMatrix, 90.0, 100.0, imagen_azulindigo, textura_azulindigo)
        g_modelMatrix = popMatrix()
        /** */
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(500.0, 0.0, -340.0)
        pared_X(gll, u_MvpMatrix, 90.0, 100.0, imagen_azulindigo, textura_azulindigo)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(670.0, 0.0, -340.0)
        pared_X(gll, u_MvpMatrix, 60.0, 100.0, imagen_azulindigo, textura_azulindigo)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(810.0, 0.0, -340.0)
        pared_X(gll, u_MvpMatrix, 90.0, 100.0, imagen_azulindigo, textura_azulindigo)
        g_modelMatrix = popMatrix()
        /**/
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(0.0, 90.0, 100.0)
        pared_X(gll, u_MvpMatrix, 400.0, 10.0, imagen_azulindigo, textura_azulindigo)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(0.0, 70.0, 100.0)
        pared_X(gll, u_MvpMatrix, 400.0, 10.0, imagen_azulindigo, textura_azulindigo)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(500.0, 90.0, 100.0)
        pared_X(gll, u_MvpMatrix, 400.0, 10.0, imagen_azulindigo, textura_azulindigo)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(500.0, 70.0, 100.0)
        pared_X(gll, u_MvpMatrix, 400.0, 10.0, imagen_azulindigo, textura_azulindigo)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(0.0, 90.0, -340.0)
        pared_X(gll, u_MvpMatrix, 400.0, 10.0, imagen_azulindigo, textura_azulindigo)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(0.0, 70.0, -340.0)
        pared_X(gll, u_MvpMatrix, 400.0, 10.0, imagen_azulindigo, textura_azulindigo)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(500.0, 90.0, -340.0)
        pared_X(gll, u_MvpMatrix, 400.0, 10.0, imagen_azulindigo, textura_azulindigo)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(500.0, 70.0, -340.0)
        pared_X(gll, u_MvpMatrix, 400.0, 10.0, imagen_azulindigo, textura_azulindigo)
        g_modelMatrix = popMatrix()
        /** */
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(500.0, 70.0, -340.0)
        pared_X(gll, u_MvpMatrix, 400.0, 10.0, imagen_entradaPrincipal, textura_entradaPrincipal)
        g_modelMatrix = popMatrix()

        /** */
        var i = 0
        while (i < 800) {
            pushMatrix(g_modelMatrix)
            g_modelMatrix_translate(i.toDouble(), 100.0, 50.0)
            piso_o_techo(gll, u_MvpMatrix, 99.0, 50.0, imagen_techoiti, textura_techoiti)
            g_modelMatrix = popMatrix()
            pushMatrix(g_modelMatrix)
            g_modelMatrix_translate(i.toDouble(), 100.0, 100.0)
            piso_o_techo(gll, u_MvpMatrix, 100.0, 50.0, imagen_techoiti, textura_techoiti)
            g_modelMatrix = popMatrix()
            /**///////// */
            pushMatrix(g_modelMatrix)
            g_modelMatrix_translate(i.toDouble(), 100.0, -290.0)
            piso_o_techo(gll, u_MvpMatrix, 99.0, 50.0, imagen_techoiti, textura_techoiti)
            g_modelMatrix = popMatrix()
            pushMatrix(g_modelMatrix)
            g_modelMatrix_translate(i.toDouble(), 100.0, -240.0)
            piso_o_techo(gll, u_MvpMatrix, 100.0, 50.0, imagen_techoiti, textura_techoiti)
            g_modelMatrix = popMatrix()
            i = i + 99
            i++
        }

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(500.0, 70.0, -340.0)
        pared_X(gll, u_MvpMatrix, 400.0, 10.0, imagen_azulindigo, textura_azulindigo)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(0.1, 0.0, -110.0)
        pared_Z(gll, u_MvpMatrix, 40.0, 50.0, imagen_puertaiti, textura_puertaiti)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(0.1, 50.0, -110.0)
        pared_Z(gll, u_MvpMatrix, 40.0, 50.0, imagen_puertaiti, textura_puertaiti)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(0.0, 50.0, -51.0)
        pared_X(gll, u_MvpMatrix, 100.0, 50.0, imagen_tutorias, textura_tutorias)
        g_modelMatrix = popMatrix()
        /** */
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(100.0, 50.0, -100.2)
        pared_X(gll, u_MvpMatrix, 40.0, 50.0, imagen_puertaiti, textura_puertaiti)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(200.0, 50.0, -100.2)
        pared_X(gll, u_MvpMatrix, 40.0, 50.0, imagen_puertaiti, textura_puertaiti)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(300.0, 50.0, -100.2)
        pared_X(gll, u_MvpMatrix, 40.0, 50.0, imagen_puertaiti, textura_puertaiti)
        g_modelMatrix = popMatrix()
        /**/
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(500.0, 50.0, -100.2)
        pared_X(gll, u_MvpMatrix, 40.0, 50.0, imagen_puertaiti, textura_puertaiti)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(600.0, 50.0, -100.2)
        pared_X(gll, u_MvpMatrix, 40.0, 50.0, imagen_puertaiti, textura_puertaiti)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(700.0, 50.0, -100.2)
        pared_X(gll, u_MvpMatrix, 40.0, 50.0, imagen_puertaiti, textura_puertaiti)
        g_modelMatrix = popMatrix()
        /**///////////////////////////////////////////////////// */
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(100.0, 50.0, -149.9)
        pared_X(gll, u_MvpMatrix, 40.0, 50.0, imagen_puertaiti, textura_puertaiti)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(200.0, 50.0, -149.9)
        pared_X(gll, u_MvpMatrix, 40.0, 50.0, imagen_puertaiti, textura_puertaiti)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(300.0, 50.0, -149.9)
        pared_X(gll, u_MvpMatrix, 40.0, 50.0, imagen_puertaiti, textura_puertaiti)
        g_modelMatrix = popMatrix()

        /** */
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(400.0, 50.0, -149.9)
        pared_X(gll, u_MvpMatrix, 40.0, 50.0, imagen_puertaiti, textura_puertaiti)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(300.0, 0.0, -100.2)
        pared_X(gll, u_MvpMatrix, 40.0, 50.0, imagen_puertaiti, textura_puertaiti)
        g_modelMatrix = popMatrix()
        /**/
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(500.0, 50.0, -149.9)
        pared_X(gll, u_MvpMatrix, 40.0, 50.0, imagen_puertaiti, textura_puertaiti)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(600.0, 50.0, -149.9)
        pared_X(gll, u_MvpMatrix, 40.0, 50.0, imagen_puertaiti, textura_puertaiti)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(700.0, 50.0, -149.9)
        pared_X(gll, u_MvpMatrix, 40.0, 50.0, imagen_puertaiti, textura_puertaiti)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(800.0, 50.0, -179.0)
        pared_X(gll, u_MvpMatrix, 40.0, 50.0, imagen_puertaiti, textura_puertaiti)
        g_modelMatrix = popMatrix()
        /**/
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(60.0, 0.0, -149.9)
        pared_X(gll, u_MvpMatrix, 40.0, 50.0, imagen_puertaiti, textura_puertaiti)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(160.0, 0.0, -219.9)
        pared_X(gll, u_MvpMatrix, 40.0, 50.0, imagen_puertaiti, textura_puertaiti)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(260.0, 0.0, -219.9)
        pared_X(gll, u_MvpMatrix, 40.0, 50.0, imagen_puertaiti, textura_puertaiti)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(299.8, 0.0, -150.0)
        pared_Z(gll, u_MvpMatrix, 40.0, 50.0, imagen_puertaiti, textura_puertaiti)
        g_modelMatrix = popMatrix()

        /**/// */
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(600.0, 0.0, -100.2)
        pared_X(gll, u_MvpMatrix, 40.0, 50.0, imagen_puertaiti, textura_puertaiti)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(700.0, 0.0, -100.2)
        pared_X(gll, u_MvpMatrix, 40.0, 50.0, imagen_puertaiti, textura_puertaiti)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(500.0, 0.0, -149.9)
        pared_X(gll, u_MvpMatrix, 40.0, 50.0, imagen_loby_planta, textura_loby_planta)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(600.0, 0.0, -149.9)
        pared_X(gll, u_MvpMatrix, 40.0, 50.0, imagen_puertaiti, textura_puertaiti)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(700.0, 0.0, -149.9)
        pared_X(gll, u_MvpMatrix, 40.0, 50.0, imagen_puertaiti, textura_puertaiti)
        g_modelMatrix = popMatrix()
        /**imagen_ventanaiti2 */
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(0.0, 80.0, -149.99)
        pared_X(gll, u_MvpMatrix, 100.0, 20.0, imagen_ventanaiti2, textura_ventanaiti2)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(100.0, 80.0, -149.99)
        pared_X(gll, u_MvpMatrix, 100.0, 20.0, imagen_ventanaiti2, textura_ventanaiti2)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(200.0, 80.0, -149.99)
        pared_X(gll, u_MvpMatrix, 100.0, 20.0, imagen_ventanaiti2, textura_ventanaiti2)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(300.0, 80.0, -149.99)
        pared_X(gll, u_MvpMatrix, 100.0, 20.0, imagen_ventanaiti2, textura_ventanaiti2)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(400.0, 80.0, -149.99)
        pared_X(gll, u_MvpMatrix, 100.0, 20.0, imagen_ventanaiti2, textura_ventanaiti2)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(500.0, 80.0, -149.99)
        pared_X(gll, u_MvpMatrix, 100.0, 20.0, imagen_ventanaiti2, textura_ventanaiti2)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(600.0, 80.0, -149.99)
        pared_X(gll, u_MvpMatrix, 100.0, 20.0, imagen_ventanaiti2, textura_ventanaiti2)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(700.0, 80.0, -149.99)
        pared_X(gll, u_MvpMatrix, 100.0, 20.0, imagen_ventanaiti2, textura_ventanaiti2)
        g_modelMatrix = popMatrix()
        /**/////////////////////////////////////////////////////////////////// */
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(0.0, 80.0, -250.1)
        pared_X(gll, u_MvpMatrix, 100.0, 20.0, imagen_ventanaiti2, textura_ventanaiti2)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(100.0, 80.0, -250.1)
        pared_X(gll, u_MvpMatrix, 100.0, 20.0, imagen_ventanaiti2, textura_ventanaiti2)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(200.0, 80.0, -250.1)
        pared_X(gll, u_MvpMatrix, 100.0, 20.0, imagen_ventanaiti2, textura_ventanaiti2)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(300.0, 80.0, -250.1)
        pared_X(gll, u_MvpMatrix, 100.0, 20.0, imagen_ventanaiti2, textura_ventanaiti2)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(400.0, 80.0, -250.1)
        pared_X(gll, u_MvpMatrix, 100.0, 20.0, imagen_ventanaiti2, textura_ventanaiti2)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(500.0, 80.0, -250.1)
        pared_X(gll, u_MvpMatrix, 100.0, 20.0, imagen_ventanaiti2, textura_ventanaiti2)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(600.0, 80.0, -250.1)
        pared_X(gll, u_MvpMatrix, 100.0, 20.0, imagen_ventanaiti2, textura_ventanaiti2)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(700.0, 80.0, -250.1)
        pared_X(gll, u_MvpMatrix, 100.0, 20.0, imagen_ventanaiti2, textura_ventanaiti2)
        g_modelMatrix = popMatrix()
        /**/
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(0.0, 30.0, -250.1)
        pared_X(gll, u_MvpMatrix, 100.0, 20.0, imagen_ventanaiti2, textura_ventanaiti2)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(100.0, 30.0, -250.1)
        pared_X(gll, u_MvpMatrix, 100.0, 20.0, imagen_ventanaiti2, textura_ventanaiti2)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(200.0, 30.0, -250.1)
        pared_X(gll, u_MvpMatrix, 100.0, 20.0, imagen_ventanaiti2, textura_ventanaiti2)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(300.0, 30.0, -250.1)
        pared_X(gll, u_MvpMatrix, 100.0, 20.0, imagen_ventanaiti2, textura_ventanaiti2)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(400.0, 30.0, -250.1)
        pared_X(gll, u_MvpMatrix, 100.0, 20.0, imagen_ventanaiti2, textura_ventanaiti2)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(500.0, 30.0, -250.1)
        pared_X(gll, u_MvpMatrix, 100.0, 20.0, imagen_ventanaiti2, textura_ventanaiti2)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(600.0, 30.0, -250.1)
        pared_X(gll, u_MvpMatrix, 100.0, 20.0, imagen_ventanaiti2, textura_ventanaiti2)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(700.0, 30.0, -250.1)
        pared_X(gll, u_MvpMatrix, 100.0, 20.0, imagen_ventanaiti2, textura_ventanaiti2)
        g_modelMatrix = popMatrix()
        /**///////////////////////////////////////////////////////////////////////////////////////// */
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(0.0, 80.0, 0.1)
        pared_X(gll, u_MvpMatrix, 100.0, 20.0, imagen_ventanaiti2, textura_ventanaiti2)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(100.0, 80.0, 0.1)
        pared_X(gll, u_MvpMatrix, 100.0, 20.0, imagen_ventanaiti2, textura_ventanaiti2)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(200.0, 80.0, 0.1)
        pared_X(gll, u_MvpMatrix, 100.0, 20.0, imagen_ventanaiti2, textura_ventanaiti2)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(300.0, 80.0, 0.1)
        pared_X(gll, u_MvpMatrix, 100.0, 20.0, imagen_ventanaiti2, textura_ventanaiti2)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(500.0, 80.0, 0.1)
        pared_X(gll, u_MvpMatrix, 100.0, 20.0, imagen_ventanaiti2, textura_ventanaiti2)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(600.0, 80.0, 0.1)
        pared_X(gll, u_MvpMatrix, 100.0, 20.0, imagen_ventanaiti2, textura_ventanaiti2)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(700.0, 80.0, 0.1)
        pared_X(gll, u_MvpMatrix, 100.0, 20.0, imagen_ventanaiti2, textura_ventanaiti2)
        g_modelMatrix = popMatrix()
        /**/
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(0.0, 30.0, 0.1)
        pared_X(gll, u_MvpMatrix, 100.0, 20.0, imagen_ventanaiti2, textura_ventanaiti2)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(100.0, 30.0, 0.1)
        pared_X(gll, u_MvpMatrix, 100.0, 20.0, imagen_ventanaiti2, textura_ventanaiti2)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(200.0, 30.0, 0.1)
        pared_X(gll, u_MvpMatrix, 100.0, 20.0, imagen_ventanaiti2, textura_ventanaiti2)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(300.0, 30.0, 0.1)
        pared_X(gll, u_MvpMatrix, 100.0, 20.0, imagen_ventanaiti2, textura_ventanaiti2)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(400.0, 30.0, 0.1)
        pared_X(gll, u_MvpMatrix, 100.0, 20.0, imagen_ventanaiti2, textura_ventanaiti2)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(500.0, 30.0, 0.1)
        pared_X(gll, u_MvpMatrix, 100.0, 20.0, imagen_ventanaiti2, textura_ventanaiti2)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(600.0, 30.0, 0.1)
        pared_X(gll, u_MvpMatrix, 100.0, 20.0, imagen_ventanaiti2, textura_ventanaiti2)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(700.0, 30.0, 0.1)
        pared_X(gll, u_MvpMatrix, 100.0, 20.0, imagen_ventanaiti2, textura_ventanaiti2)
        g_modelMatrix = popMatrix()
        /**/
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(400.0, 0.0, 0.0)
        pared_X(gll, u_MvpMatrix, 100.0, 50.0, imagen_entradaPrincipal, textura_entradaPrincipal)
        g_modelMatrix = popMatrix()
        /**/
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(100.0, 80.0, -100.001)
        pared_X(gll, u_MvpMatrix, 100.0, 20.0, imagen_ventanaiti2, textura_ventanaiti2)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(200.0, 80.0, -100.001)
        pared_X(gll, u_MvpMatrix, 100.0, 20.0, imagen_ventanaiti2, textura_ventanaiti2)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(300.0, 80.0, -100.001)
        pared_X(gll, u_MvpMatrix, 100.0, 20.0, imagen_ventanaiti2, textura_ventanaiti2)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(500.0, 80.0, -100.001)
        pared_X(gll, u_MvpMatrix, 100.0, 20.0, imagen_ventanaiti2, textura_ventanaiti2)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(600.0, 80.0, -100.001)
        pared_X(gll, u_MvpMatrix, 100.0, 20.0, imagen_ventanaiti2, textura_ventanaiti2)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(700.0, 80.0, -100.001)
        pared_X(gll, u_MvpMatrix, 100.0, 20.0, imagen_ventanaiti2, textura_ventanaiti2)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        /** */
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(0.0, 50.0, -100.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 50.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(100.0, 50.0, -100.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 50.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(200.0, 50.0, -100.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 50.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(300.0, 50.0, -100.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 50.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()
        //
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(500.0, 50.0, -100.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 50.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()
        /**/
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(600.0, 50.0, -100.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 50.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()
        //
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(700.0, 50.0, -100.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 50.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        //
        g_modelMatrix_translate(100.0, 50.0, -145.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 50.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(200.0, 50.0, -145.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 50.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(300.0, 50.0, -145.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 50.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(400.0, 50.0, -145.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 50.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(500.0, 50.0, -145.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 50.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(600.0, 50.0, -145.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 50.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(700.0, 50.0, -145.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 50.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()
        /**//////////////////////////////////////////////////////////// */
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(100.0, 0.0, -145.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 50.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(200.0, 0.0, -145.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 50.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(300.0, 0.0, -145.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 50.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(400.0, 0.0, -145.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 50.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(500.0, 0.0, -145.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 50.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(600.0, 0.0, -145.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 50.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(700.0, 0.0, -145.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 50.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()

        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(100.0, 0.0, -100.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 50.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(200.0, 0.0, -100.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 50.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(300.0, 0.0, -100.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 50.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(400.0, 0.0, -100.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 50.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(500.0, 0.0, -100.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 50.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(600.0, 0.0, -100.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 50.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(700.0, 0.0, -100.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 50.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()
        /** */
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(0.0, 0.0, 10.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 100.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(100.0, 0.0, 10.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 100.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(200.0, 0.0, 10.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 100.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(300.0, 0.0, 10.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 100.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(400.0, 0.0, 10.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 100.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(500.0, 0.0, 10.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 100.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(600.0, 0.0, 10.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 100.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(700.0, 0.0, 10.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 100.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(800.0, 0.0, 10.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 100.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()
        /**/////////////////////////////////////////////////////////////////////////////////////////////////////////////// */
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(100.0, 0.0, -250.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 100.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(200.0, 0.0, -250.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 100.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(300.0, 0.0, -250.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 100.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(400.0, 0.0, -250.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 100.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(500.0, 0.0, -250.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 100.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(600.0, 0.0, -250.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 100.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(700.0, 0.0, -250.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 100.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()
        //
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(800.0, 0.0, -250.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 100.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()

        // pushMatrix(g_modelMatrix); imagen_entradaPrincipal
        //g_modelMatrix_translate(0,-0.15,0);
        //piso_o_techo(gll, u_MvpMatrix, 800, 300, imagen_pisoupv, textura_pisoupv);
        //g_modelMatrix = popMatrix();
        /** AQUI TERMINA EL PRIMER PISO */
    }

    private fun edificioB() {
        g_modelMatrix_translate(1500.0, 0.0, 0.0)
        g_modelMatrix_rotate(180.0, 0.0, 1.0, 0.0)

        //edificio
        pushMatrix(g_modelMatrix)
        prisma_cuadrangular(
            gll,
            u_MvpMatrix,
            300.0,
            400.0,
            100.0,
            imagen_grispymes,
            textura_grispymes
        )
        g_modelMatrix = popMatrix()

        //PUERTA PRINCIPAL
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(100.0, 0.0, 0.1)
        pared_X(gll, u_MvpMatrix, 100.0, 80.0, imagen_puertaPymes, textura_puertaPymes)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(30.0, 10.0, 0.1)
        pared_X(gll, u_MvpMatrix, 50.0, 50.0, imagen_ventanapymes, textura_ventanapymesve)
        g_modelMatrix = popMatrix()

        //SALITA PRIMERO A LA IZQUIERDA
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(50.0, 0.0, 0.0)
        pared_Z(gll, u_MvpMatrix, 80.0, 50.0, imagen_cristal, textura_cristal)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(0.0, 0.0, -80.0)
        pared_X(gll, u_MvpMatrix, 50.0, 50.0, imagen_blancopymes, textura_blancopymes)
        g_modelMatrix = popMatrix()

        //BAÑOS
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(250.0, 0.0, 0.0)
        pared_Z(gll, u_MvpMatrix, 15.0, 50.0, imagen_blancopymes, textura_blancopymes)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(250.0, 0.0, -15.0)
        pared_X(gll, u_MvpMatrix, 50.0, 50.0, imagen_puertabano, textura_puertabano)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(250.0, 0.0, -30.0)
        pared_Z(gll, u_MvpMatrix, 15.0, 50.0, imagen_puertabano, textura_puertabano)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(250.0, 0.0, -30.0)
        pared_X(gll, u_MvpMatrix, 50.0, 50.0, imagen_puertabano, textura_puertabano)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(250.0, 0.0, -45.0)
        pared_X(gll, u_MvpMatrix, 50.0, 50.0, imagen_crema, textura_crema)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(280.0, 0.0, -15.0)
        pared_Z(gll, u_MvpMatrix, 15.0, 50.0, imagen_blancopymes, textura_blancopymes)
        g_modelMatrix = popMatrix()

        //Oficina ESCOLARES
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(200.0, 0.0, -45.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 100.0, 80.0, 50.0, imagen_pared5, textura_pared5)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(199.9, 0.0, -45.0)
        pared_Z(gll, u_MvpMatrix, 80.0, 50.0, imagen_escolares, textura_escolares)
        g_modelMatrix = popMatrix()

        //Oficina alado de CAI
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(100.0, 0.0, -199.9)
        prisma_cuadrangular(
            gll,
            u_MvpMatrix,
            50.0,
            200.0,
            50.0,
            imagen_grispymes,
            textura_grispymes
        )
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(100.0, 0.0, -199.8)
        pared_X(gll, u_MvpMatrix, 50.0, 50.0, imagen_morado, textura_morado)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(100.0, 0.0, -155.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 30.0, 2.0, 10.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(100.0, 10.0, -149.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 35.0, 10.0, 1.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(150.5, 0.0, -210.0)
        pared_Z(gll, u_MvpMatrix, 15.0, 35.0, imagen_puertas, textura_puertas)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(150.5, 0.0, -245.0)
        pared_Z(gll, u_MvpMatrix, 65.0, 35.0, imagen_pasillooficina, textura_pasillooficina)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(150.9, 0.0, -399.0)
        pared_X(gll, u_MvpMatrix, 50.0, 50.0, imagen_puertasalida, textura_puertasalida)
        g_modelMatrix = popMatrix()


        //BARANDAL
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(50.0, 50.0, -80.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 200.0, 5.0, 5.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        //CAI
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(0.1, 0.0, -149.9)
        prisma_cuadrangular(gll, u_MvpMatrix, 100.0, 250.0, 50.0, imagen_pared5, textura_pared5)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(0.0, 0.0, -149.8)
        pared_X(gll, u_MvpMatrix, 80.0, 50.0, imagen_cai, textura_cai)
        g_modelMatrix = popMatrix()

        //Dentro de cai
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(0.1, 0.0, -280.0)
        prisma_cuadrangular(
            gll,
            u_MvpMatrix,
            35.0,
            120.0,
            50.0,
            imagen_grispymes,
            textura_grispymes
        )
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        /**//////////// */
        g_modelMatrix_translate(35.9, 0.0, -365.0)
        pared_Z(gll, u_MvpMatrix, 15.0, 35.0, imagen_puertabano, textura_puertabano)
        g_modelMatrix = popMatrix()


        //ESCALERAS
        //pared
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(0.0, 0.0, -130.0)
        pared_X(gll, u_MvpMatrix, 50.0, 50.0, imagen_blancopymes, textura_blancopymes)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(50.0, 0.0, -105.0)
        pared_Z(gll, u_MvpMatrix, 25.0, 2.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(48.0, 2.0, -105.0)
        piso_o_techo(gll, u_MvpMatrix, 2.0, 25.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(48.0, 2.0, -105.0)
        pared_Z(gll, u_MvpMatrix, 25.0, 2.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(46.0, 4.0, -105.0)
        piso_o_techo(gll, u_MvpMatrix, 2.0, 25.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(46.0, 4.0, -105.0)
        pared_Z(gll, u_MvpMatrix, 25.0, 2.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(44.0, 6.0, -105.0)
        piso_o_techo(gll, u_MvpMatrix, 2.0, 25.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(44.0, 6.0, -105.0)
        pared_Z(gll, u_MvpMatrix, 25.0, 2.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(42.0, 8.0, -105.0)
        piso_o_techo(gll, u_MvpMatrix, 2.0, 25.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(42.0, 8.0, -105.0)
        pared_Z(gll, u_MvpMatrix, 25.0, 2.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(40.0, 10.0, -105.0)
        piso_o_techo(gll, u_MvpMatrix, 2.0, 25.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(40.0, 10.0, -105.0)
        pared_Z(gll, u_MvpMatrix, 25.0, 2.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(38.0, 12.0, -105.0)
        piso_o_techo(gll, u_MvpMatrix, 2.0, 25.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(38.0, 12.0, -105.0)
        pared_Z(gll, u_MvpMatrix, 25.0, 2.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(36.0, 14.0, -105.0)
        piso_o_techo(gll, u_MvpMatrix, 2.0, 25.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(36.0, 14.0, -105.0)
        pared_Z(gll, u_MvpMatrix, 25.0, 2.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(34.0, 16.0, -105.0)
        piso_o_techo(gll, u_MvpMatrix, 2.0, 25.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(34.0, 16.0, -105.0)
        pared_Z(gll, u_MvpMatrix, 25.0, 2.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(32.0, 18.0, -105.0)
        piso_o_techo(gll, u_MvpMatrix, 2.0, 25.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(32.0, 18.0, -105.0)
        pared_Z(gll, u_MvpMatrix, 25.0, 2.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(30.0, 20.0, -105.0)
        piso_o_techo(gll, u_MvpMatrix, 2.0, 25.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(30.0, 20.0, -105.0)
        pared_Z(gll, u_MvpMatrix, 25.0, 2.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(28.0, 22.0, -105.0)
        piso_o_techo(gll, u_MvpMatrix, 2.0, 25.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(28.0, 22.0, -105.0)
        pared_Z(gll, u_MvpMatrix, 25.0, 2.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(26.0, 24.0, -105.0)
        piso_o_techo(gll, u_MvpMatrix, 2.0, 25.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(26.0, 24.0, -105.0)
        pared_Z(gll, u_MvpMatrix, 25.0, 2.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(0.0, 26.0, -80.0)
        piso_o_techo(gll, u_MvpMatrix, 26.0, 45.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(26.0, 26.0, -80.0)
        pared_Z(gll, u_MvpMatrix, 25.0, 2.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(26.0, 28.0, -80.0)
        piso_o_techo(gll, u_MvpMatrix, 2.0, 25.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(28.0, 28.0, -80.0)
        pared_Z(gll, u_MvpMatrix, 25.0, 2.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(28.0, 30.0, -80.0)
        piso_o_techo(gll, u_MvpMatrix, 2.0, 25.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(30.0, 30.0, -80.0)
        pared_Z(gll, u_MvpMatrix, 25.0, 2.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(30.0, 32.0, -80.0)
        piso_o_techo(gll, u_MvpMatrix, 2.0, 25.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(32.0, 32.0, -80.0)
        pared_Z(gll, u_MvpMatrix, 25.0, 2.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(32.0, 34.0, -80.0)
        piso_o_techo(gll, u_MvpMatrix, 2.0, 25.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(34.0, 34.0, -80.0)
        pared_Z(gll, u_MvpMatrix, 25.0, 2.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(34.0, 36.0, -80.0)
        piso_o_techo(gll, u_MvpMatrix, 2.0, 25.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(36.0, 36.0, -80.0)
        pared_Z(gll, u_MvpMatrix, 25.0, 2.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(36.0, 38.0, -80.0)
        piso_o_techo(gll, u_MvpMatrix, 2.0, 25.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(38.0, 38.0, -80.0)
        pared_Z(gll, u_MvpMatrix, 25.0, 2.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(38.0, 40.0, -80.0)
        piso_o_techo(gll, u_MvpMatrix, 2.0, 25.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(40.0, 40.0, -80.0)
        pared_Z(gll, u_MvpMatrix, 25.0, 2.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(40.0, 42.0, -80.0)
        piso_o_techo(gll, u_MvpMatrix, 2.0, 25.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(42.0, 42.0, -80.0)
        pared_Z(gll, u_MvpMatrix, 25.0, 2.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(42.0, 42.0, -80.0)
        piso_o_techo(gll, u_MvpMatrix, 2.0, 25.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(44.0, 42.0, -80.0)
        pared_Z(gll, u_MvpMatrix, 25.0, 2.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(44.0, 44.0, -80.0)
        piso_o_techo(gll, u_MvpMatrix, 2.0, 25.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(46.0, 44.0, -80.0)
        pared_Z(gll, u_MvpMatrix, 25.0, 2.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(46.0, 46.0, -80.0)
        piso_o_techo(gll, u_MvpMatrix, 2.0, 25.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(48.0, 46.0, -80.0)
        pared_Z(gll, u_MvpMatrix, 25.0, 2.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(48.0, 48.0, -80.0)
        piso_o_techo(gll, u_MvpMatrix, 2.0, 25.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(50.0, 48.0, -80.0)
        pared_Z(gll, u_MvpMatrix, 25.0, 2.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(50.0, 50.0, -80.0)
        piso_o_techo(gll, u_MvpMatrix, 2.0, 25.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        //MESA OFICINA
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(200.0, 0.0, -125.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 2.0, 25.0, 10.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(197.0, 10.0, -125.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 5.0, 30.0, 1.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(200.0, 0.0, -150.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 20.0, 2.0, 10.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(200.0, 10.0, -150.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 20.0, 5.0, 1.0, imagen_madera, textura_madera)
        g_modelMatrix = popMatrix()

        //Oficina camelina
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(200.0, 0.0, -220.0)
        prisma_cuadrangular(
            gll,
            u_MvpMatrix,
            100.0,
            180.0,
            50.0,
            imagen_grispymes,
            textura_grispymes
        )
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(210.0, 15.0, -219.9)
        pared_X(gll, u_MvpMatrix, 15.0, 15.0, imagen_ventanapymes, textura_ventanapymesve)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        /**//////////// */
        g_modelMatrix_translate(230.0, 0.0, -219.9)
        pared_X(gll, u_MvpMatrix, 15.0, 35.0, imagen_puertas, textura_puertas)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        /**//////////// */
        g_modelMatrix_translate(275.0, 0.0, -219.9)
        pared_X(gll, u_MvpMatrix, 15.0, 35.0, imagen_puertas, textura_puertas)
        g_modelMatrix = popMatrix()

        //baño camelina
        pushMatrix(g_modelMatrix)
        /**//////////// */
        g_modelMatrix_translate(299.9, 0.0, -175.0)
        pared_Z(gll, u_MvpMatrix, 15.0, 35.0, imagen_puertabano, textura_puertabano)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        /**//////////// */
        g_modelMatrix_translate(299.9, 0.0, -140.0)
        pared_Z(gll, u_MvpMatrix, 15.0, 35.0, imagen_puertabano, textura_puertabano)
        g_modelMatrix = popMatrix()

        //Maquina
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(45.0, 0.0, -80.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 20.0, imagen_cafe, textura_cafe)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(55.2, 0.0, -80.0)
        pared_Z(gll, u_MvpMatrix, 10.0, 20.0, imagen_maquina, textura_maquina)
        g_modelMatrix = popMatrix()

        //PISO
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(0.0, 0.1, 0.0)
        piso_o_techo(gll, u_MvpMatrix, 300.0, 400.0, imagen_pisoupv, textura_pisoupv)
        g_modelMatrix = popMatrix()

        //2do PISO
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(50.0, 50.5, -80.0)
        piso_o_techo(gll, u_MvpMatrix, 250.0, 320.0, imagen_pisoupv, textura_pisoupv)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(0.0, 50.5, -350.0)
        piso_o_techo(gll, u_MvpMatrix, 50.0, 50.0, imagen_pisoupv, textura_pisoupv)
        g_modelMatrix = popMatrix()

        //Sala maestros 2do piso
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(250.0, 51.0, -80.0)
        prisma_cuadrangular(
            gll,
            u_MvpMatrix,
            50.0,
            320.0,
            50.0,
            imagen_grispymes,
            textura_grispymes
        )
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(249.0, 51.0, -83.0)
        pared_Z(gll, u_MvpMatrix, 80.0, 50.0, imagen_salamaestros, textura_porbiblioteca)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(249.0, 51.0, -170.0)
        pared_Z(gll, u_MvpMatrix, 15.0, 35.0, imagen_puertas, textura_puertas)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(249.0, 51.0, -200.0)
        pared_Z(gll, u_MvpMatrix, 15.0, 35.0, imagen_puertas, textura_puertas)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(249.0, 51.0, -240.0)
        pared_Z(gll, u_MvpMatrix, 15.0, 35.0, imagen_puertas, textura_puertas)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(249.0, 51.0, -280.0)
        pared_Z(gll, u_MvpMatrix, 15.0, 35.0, imagen_puertas, textura_puertas)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(249.0, 51.0, -315.0)
        pared_Z(gll, u_MvpMatrix, 15.0, 35.0, imagen_puertas, textura_puertas)
        g_modelMatrix = popMatrix()


        //Salones 2do piso
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(150.0, 51.0, -150.0)
        prisma_cuadrangular(
            gll,
            u_MvpMatrix,
            50.0,
            250.0,
            50.0,
            imagen_grispymes,
            textura_grispymes
        )
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(201.0, 51.0, -160.0)
        pared_Z(gll, u_MvpMatrix, 15.0, 35.0, imagen_puertas, textura_puertas)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(201.0, 51.0, -195.0)
        pared_Z(gll, u_MvpMatrix, 15.0, 35.0, imagen_puertas, textura_puertas)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(201.0, 51.0, -230.0)
        pared_Z(gll, u_MvpMatrix, 15.0, 35.0, imagen_puertas, textura_puertas)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(201.0, 51.0, -265.0)
        pared_Z(gll, u_MvpMatrix, 15.0, 35.0, imagen_puertas, textura_puertas)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(201.0, 51.0, -300.0)
        pared_Z(gll, u_MvpMatrix, 15.0, 35.0, imagen_puertas, textura_puertas)
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(201.0, 51.0, -399.0)
        pared_X(gll, u_MvpMatrix, 50.0, 50.0, imagen_puertasalida, textura_puertasalida)
        g_modelMatrix = popMatrix()

        //Oficinas 2do piso
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(80.0, 51.0, -210.0)
        prisma_cuadrangular(
            gll,
            u_MvpMatrix,
            70.0,
            180.0,
            50.0,
            imagen_grispymes,
            textura_grispymes
        )
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(80.0, 51.0, -209.0)
        pared_X(gll, u_MvpMatrix, 80.0, 50.0, imagen_porbiblioteca, textura_porbiblioteca)
        g_modelMatrix = popMatrix()

        //Biblioteca
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(0.0, 51.0, -200.0)
        prisma_cuadrangular(
            gll,
            u_MvpMatrix,
            80.0,
            200.0,
            50.0,
            imagen_grispymes,
            textura_grispymes
        )
        g_modelMatrix = popMatrix()

        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(0.0, 51.0, -199.0)
        pared_X(gll, u_MvpMatrix, 80.0, 50.0, imagen_biblioteca, textura_biblioteca)
        g_modelMatrix = popMatrix()
    }

    private fun edificioH() {
        g_modelMatrix_translate(0.0, 0.0, -500.0)

        //Piso gris
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-175.0, 0.0, 25.0)
        piso_o_techo(gll, u_MvpMatrix, 350.0, 150.0, imagen_gris, textura_gris)

        //Prisma azul izquierda
        g_modelMatrix_translate(25.0, 0.0, -12.5)
        prisma_cuadrangular(
            gll,
            u_MvpMatrix,
            12.5,
            137.5,
            50.0,
            imagen_azulindigo,
            textura_azulindigo
        )

        //Cuartote blanco izquierda
        g_modelMatrix_translate(12.5, 0.0, -12.5)
        prisma_cuadrangular(gll, u_MvpMatrix, 100.0, 125.0, 50.0, imagen_blanco, textura_blanco)
        g_modelMatrix_translate(0.0, 50.1, 0.0)
        piso_o_techo(gll, u_MvpMatrix, 100.0, 125.0, imagen_pisoupv2, textura_pisoupv2)
        g_modelMatrix_translate(0.0, -50.1, 0.0)

        //La pared azul del cuartote blanco izquierda
        g_modelMatrix_translate(100.1, 0.0, 0.0)
        pared_Z(gll, u_MvpMatrix, 125.0, 50.0, imagen_azulindigo, textura_azulindigo)
        g_modelMatrix_translate(-100.1, 0.0, 0.0)

        //Cuartote blanco derecha
        g_modelMatrix_translate(175.0, 0.0, 0.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 100.0, 125.0, 50.0, imagen_blanco, textura_blanco)
        g_modelMatrix_translate(0.0, 50.1, 0.0)
        piso_o_techo(gll, u_MvpMatrix, 100.0, 125.0, imagen_pisoupv2, textura_pisoupv2)
        g_modelMatrix_translate(0.0, -50.1, 0.0)

        //Prisma azul derecha
        g_modelMatrix_translate(100.0, 0.0, 12.5)
        prisma_cuadrangular(
            gll,
            u_MvpMatrix,
            12.5,
            137.5,
            50.0,
            imagen_azulindigo,
            textura_azulindigo
        )

        g_modelMatrix = popMatrix()



        pushMatrix(g_modelMatrix)
        //Base gris 2
        g_modelMatrix_translate(-300.0, 0.0, -100.0)
        piso_o_techo(gll, u_MvpMatrix, 600.0, 75 * 5.5, imagen_gris, textura_gris)

        //Almacen izquierda
        g_modelMatrix_translate(25.0, 0.0, -25.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 75.0, 50.0, 100.0, imagen_blanco, textura_blanco)

        //Primer salon izquierda
        g_modelMatrix_translate(75.0, 0.0, 0.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 75.0, 150.0, 100.0, imagen_blanco, textura_blanco)
        g_modelMatrix_translate(0.0, 100.1, 0.0)
        piso_o_techo(gll, u_MvpMatrix, 75.0, 150.0, imagen_pisoupv2, textura_pisoupv2)
        g_modelMatrix_translate(0.0, -100.1, 0.0)



        g_modelMatrix_translate(75.0, 0.0, -150.0)

        //Pilar 1
        g_modelMatrix_translate(0.0, 0.0, 150.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 100.0, imagen_morado, textura_morado)

        //Ventana1
        g_modelMatrix_translate(0.1, 60.0, -10.0)
        pared_Z(gll, u_MvpMatrix, 140.0, 20.0, imagen_ventanaISA, textura_ventanaISA)
        g_modelMatrix_translate(-0.1, -60.0, 10.0)

        //Puerta1
        g_modelMatrix_translate(0.1, 0.0, 0.0)
        pared_Z(gll, u_MvpMatrix, 25.0, 35.0, imagen_puertage, textura_puertage)
        g_modelMatrix_translate(-0.1, 0.0, 0.0)

        g_modelMatrix_translate(0.0, 0.0, -150.0)

        //Pilar 2
        g_modelMatrix_translate(0.0, 0.0, 10.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 100.0, imagen_morado, textura_morado)
        g_modelMatrix_translate(0.0, 0.0, -10.0)

        //Segundo salon izquierda---------------------------------------------------
        g_modelMatrix_rotate(-20.0, 0.0, 1.0, 0.0)
        //Pared enfrente
        pared_Z(gll, u_MvpMatrix, 150.0, 100.0, imagen_blanco, textura_blanco)

        //Ventana2
        g_modelMatrix_translate(0.1, 60.0, -10.0)
        pared_Z(gll, u_MvpMatrix, 140.0, 20.0, imagen_ventanaISA, textura_ventanaISA)
        g_modelMatrix_translate(-0.1, -60.0, 10.0)

        //Puerta 2
        g_modelMatrix_translate(0.1, 0.0, 0.0)
        pared_Z(gll, u_MvpMatrix, 25.0, 35.0, imagen_puertage, textura_puertage)
        g_modelMatrix_translate(-0.1, 0.0, 0.0)

        g_modelMatrix_rotate(20.0, 0.0, 1.0, 0.0)

        //Pared atras
        g_modelMatrix_translate(-75.0, 0.0, 0.0)
        g_modelMatrix_rotate(-30.7, 0.0, 1.0, 0.0)
        pared_Z(gll, u_MvpMatrix, 245.0, 100.0, imagen_oro, textura_oro)
        g_modelMatrix_rotate(30.7, 0.0, 1.0, 0.0)

        //Techo
        g_modelMatrix_translate(0.0, 100.0, 0.0)
        piso_o_techo_especial(
            gll,
            u_MvpMatrix,
            75.0,
            0.0,
            (75 + 50).toDouble(),
            -136.35 - 75,
            (75 + 50).toDouble(),
            -136.35,
            imagen_pisoupv2,
            textura_pisoupv2
        )
        g_modelMatrix_translate(0.0, -100.0, 0.0)

        g_modelMatrix_translate(75.0, 0.0, 0.0)

        g_modelMatrix_translate(-75.0, 0.0, 150.0)

        //--------------------------------------------------------------------------
        g_modelMatrix_translate(-75.0, 0.0, 0.0)
        //Primer salon derecha
        g_modelMatrix_translate(400.0, 0.0, 0.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 75.0, 150.0, 100.0, imagen_blanco, textura_blanco)

        //Ventana5
        g_modelMatrix_translate(-0.1, 60.0, -10.0)
        pared_Z(gll, u_MvpMatrix, 140.0, 20.0, imagen_ventanaISA, textura_ventanaISA)
        g_modelMatrix_translate(0.1, -60.0, 10.0)

        //Puerta 5
        g_modelMatrix_translate(-0.1, 0.0, 0.0)
        pared_Z(gll, u_MvpMatrix, 25.0, 35.0, imagen_puertage, textura_puertage)
        g_modelMatrix_translate(0.1, 0.0, 0.0)

        g_modelMatrix_translate(0.0, 100.1, 0.0)
        piso_o_techo(gll, u_MvpMatrix, 75.0, 150.0, imagen_pisoupv2, textura_pisoupv2)
        g_modelMatrix_translate(0.0, -100.1, 0.0)

        //Pilar 5
        g_modelMatrix_translate(-10.0, 0.0, 0.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 100.0, imagen_morado, textura_morado)
        g_modelMatrix_translate(10.0, 0.0, 0.0)

        //Segundo salon derecha---------------------------------------------------
        g_modelMatrix_translate(0.0, 0.0, -150.0)

        //Pilar 6
        g_modelMatrix_translate(-10.0, 0.0, 0.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 100.0, imagen_morado, textura_morado)
        g_modelMatrix_translate(10.0, 0.0, 0.0)

        //Pared enfrente
        g_modelMatrix_rotate(20.0, 0.0, 1.0, 0.0)
        pared_Z(gll, u_MvpMatrix, 150.0, 100.0, imagen_blanco, textura_blanco)

        //Ventana1
        g_modelMatrix_translate(-0.1, 60.0, -10.0)
        pared_Z(gll, u_MvpMatrix, 140.0, 20.0, imagen_ventanaISA, textura_ventanaISA)
        g_modelMatrix_translate(0.1, -60.0, 10.0)

        //Puerta 6
        g_modelMatrix_translate(-0.1, 0.0, 0.0)
        pared_Z(gll, u_MvpMatrix, 25.0, 35.0, imagen_puertage, textura_puertage)
        g_modelMatrix_translate(0.1, 0.0, 0.0)

        g_modelMatrix_rotate(-20.0, 0.0, 1.0, 0.0)

        //Techo
        g_modelMatrix_translate(75.0, 100.0, 0.0)
        piso_o_techo_especial(
            gll,
            u_MvpMatrix,
            (-50 - 75).toDouble(),
            -136.35 - 75,
            -75.0,
            0.0,
            (-50 - 75).toDouble(),
            -136.35,
            imagen_pisoupv2,
            textura_pisoupv2
        )
        g_modelMatrix_translate(-75.0, -100.0, 0.0)

        //Pared atras
        g_modelMatrix_translate(75.0, 0.0, 0.0)
        g_modelMatrix_rotate(30.7, 0.0, 1.0, 0.0)
        pared_Z(gll, u_MvpMatrix, 245.0, 100.0, imagen_oro, textura_oro)
        g_modelMatrix_rotate(-30.7, 0.0, 1.0, 0.0)


        g_modelMatrix_translate(-75.0, 0.0, 0.0)

        g_modelMatrix_translate(0.0, 0.0, 150.0)

        //--------------------------------------------------------------------------

//Almacen derecha
        g_modelMatrix_translate(75.0, 0.0, 0.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 75.0, 50.0, 100.0, imagen_blanco, textura_blanco)

        g_modelMatrix = popMatrix()

        //Salon central
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-75.0, 0.0, -125 - 150 - 136.35)
        prisma_cuadrangular(gll, u_MvpMatrix, 150.0, 75.0, 100.0, imagen_blanco, textura_blanco)

        //Ventana3
        g_modelMatrix_translate(10.0, 60.0, 0.2)
        pared_X(gll, u_MvpMatrix, 140.0, 20.0, imagen_ventanaISA, textura_ventanaISA)
        g_modelMatrix_translate(-10.0, -60.0, -0.2)

        //Puerta 3
        g_modelMatrix_translate(0.0, 0.0, 0.1)
        pared_X(gll, u_MvpMatrix, 25.0, 35.0, imagen_puertage, textura_puertage)
        g_modelMatrix_translate(0.0, 0.0, -0.1)

        g_modelMatrix_translate(0.0, 100.1, 0.0)
        piso_o_techo(gll, u_MvpMatrix, 150.0, 75.0, imagen_pisoupv2, textura_pisoupv2)
        g_modelMatrix_translate(0.0, -100.1, 0.0)

        //Pilar 3
        g_modelMatrix_translate(0.0, 0.0, 10.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 100.0, imagen_morado, textura_morado)
        g_modelMatrix_translate(0.0, 0.0, -10.0)

        g_modelMatrix_translate(150.0, 0.0, 0.0)

        //Pilar 4
        g_modelMatrix_translate(-10.0, 0.0, 10.0)
        prisma_cuadrangular(gll, u_MvpMatrix, 10.0, 10.0, 100.0, imagen_morado, textura_morado)
        g_modelMatrix_translate(10.0, 0.0, -10.0)

        g_modelMatrix = popMatrix()
    }

    // Se crea y posiciona el cielo
    private fun cielo() {
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-500.0, 700.0, 700.0)
        piso_o_techo(gll, u_MvpMatrix, 3000.0, 3000.0, imagen_cielo, textura_cielo)
        g_modelMatrix = popMatrix()
    }

    private fun cielo_enfrente() {
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-500.0, -200.0, 700.0)
        pared_X(gll, u_MvpMatrix, 3000.0, 1000.0, imagen_cielo, textura_cielo_enfrente)
        g_modelMatrix = popMatrix()
    }

    private fun cielo_atras() {
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-500.0, -200.0, -2300.0)
        pared_X(gll, u_MvpMatrix, 3000.0, 1000.0, imagen_cielo, textura_cielo_atras)
        g_modelMatrix = popMatrix()
    }

    private fun cielo_derecha() {
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(-500.0, -200.0, 700.0)
        pared_Z(gll, u_MvpMatrix, 3000.0, 1000.0, imagen_cielo, textura_cielo_derecha)
        g_modelMatrix = popMatrix()
    }

    private fun cielo_izquierda() {
        pushMatrix(g_modelMatrix)
        g_modelMatrix_translate(2500.0, -200.0, 700.0)
        pared_Z(gll, u_MvpMatrix, 3000.0, 1000.0, imagen_cielo, textura_cielo_izquierda)
        g_modelMatrix = popMatrix()
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)

            actualizar_vista_con_matriz(rotationMatrix)

            val rotationMatrix = FloatArray(16)
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
            Log.d("SensorData", "Rotation Matrix: " + rotationMatrix.contentToString())
        }
    }

    private fun actualizar_vista_con_matriz(rotationMatrix: FloatArray?) {
        // Dirección inicial hacia adelante (eje Z negativo)
        val initialView = floatArrayOf(0f, 1f, 0f, 0f) // La cámara mira hacia adelante
        val transformedView = FloatArray(4)

        // Multiplicamos la matriz de rotación por el vector de vista inicial
        Matrix.multiplyMV(transformedView, 0, rotationMatrix, 0, initialView, 0)


        // Ahora asignamos los valores correctamente:
        // transformedView[0] = nuevo valor en el eje X
        // transformedView[1] = nuevo valor en el eje Y
        // transformedView[2] = nuevo valor en el eje Z// Ajusta esta sensibilidad para que la cámara suba/baje más suavemente


        // Corregimos la asignación de ViewDir según el sistema de coordenadas
        ViewDir[0] = transformedView[1] // Eje X: dirección horizontal
        ViewDir[1] = -transformedView[0] // Eje Y: dirección vertical
        ViewDir[2] = transformedView[2] // Eje Z: dirección hacia adelante/atrás

        Log.d("Valores de Victa", (-transformedView[0]).toString() + " .")
        // Actualizamos la vista
        actualizar_vista()
    }


    override fun onAccuracyChanged(sensor: Sensor?, i: Int) {
    }

    fun startSensors() {
        if (rotationVectorSensor != null) {
            sensorManager.registerListener(
                this,
                rotationVectorSensor,
                SensorManager.SENSOR_DELAY_GAME
            )
        }
    }

    fun stopSensors() {
        sensorManager.unregisterListener(this)
    }

    //CORRECCION DE DIRECCIONES --------------------------------------------------------------------------------------------------------------------------------------------------
    
    //todo el proyecto usa la funcion que esta en las lineas 137, la de moveforward, que para avanzar hay que pasarle un numero negativo
        
    fun aplicarMovimientoJoystick(lx: Float, ly: Float, velocidad: Float = 5f) {
        //if (ly != 0f) MoveForward(-ly * velocidad)
        if (ly != 0f) MoveForward(ly * velocidad)   // correcion quitando el menos, ahora si adelante/atras
        if (lx != 0f) StrafeRight(lx * velocidad)     // izquierda/derecha
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    companion object {
        /**
         * Rotation increment per frame.
         */
        const val CUBE_ROTATION_INCREMENT: Float = 0.1f

        //private static final float CUBE_ROTATION_INCREMENT = 0.6f;
        /**
         * The refresh rate, in frames per second.
         */
        private const val REFRESH_RATE_FPS = 60

        /**
         * The duration, in milliseconds, of one frame.
         */
        private val FRAME_TIME_MILLIS = (TimeUnit.SECONDS.toMillis(1) / REFRESH_RATE_FPS).toFloat()

        // Variables para hacer la vista dual
        var screenWidth: Float = 0f
        var screenHeight: Float = 0f
    }
}