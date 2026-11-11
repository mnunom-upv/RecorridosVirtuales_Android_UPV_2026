package upv.pm.may_ago_26.iti_271503.pgu2_eq04

import android.opengl.GLES20
import android.opengl.Matrix
import android.os.Bundle
import android.view.InputDevice
import android.view.KeyEvent
import android.view.MotionEvent
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import kotlin.math.abs
import kotlin.math.atan2

class MainActivity : CardboardActivity(), CardboardView.StereoRenderer {

    var Joystick_Izquierdo_Derecho: Int = 0

    private var previousX = 0f
    private var previousY = 0f

    private lateinit var cubeRenderer: CubeRenderer

    private val lightPosInEyeSpace = FloatArray(4)

    private val triVertexCount: Int = triCoords.size / COORDS_PER_VERTEX
    private val triColor: FloatArray = floatArrayOf(0.8f, 0.6f, 0.2f, 0.0f)
    private lateinit var triVerticesBuffer: FloatBuffer
    private val triTransform: FloatArray = FloatArray(16)

    private val cubeVertexCount: Int = cubeCoords.size / COORDS_PER_VERTEX
    private val cubeTransform: FloatArray = FloatArray(16)
    private val cubeDistance = 5f

    private val floorVertexCount: Int = floorCoords.size / COORDS_PER_VERTEX
    private val floorTransform: FloatArray = FloatArray(16)
    private val floorDepth = 20f

    private val camera: FloatArray = FloatArray(16)
    private val view: FloatArray = FloatArray(16)
    private val modelViewProjection: FloatArray = FloatArray(16)

    private val triView: FloatArray = FloatArray(16)
    private val cubeView: FloatArray = FloatArray(16)
    private val floorView: FloatArray = FloatArray(16)
    private val headView: FloatArray = FloatArray(16)

    private val dpad = Dpad()

    private var simpleVertexShader = 0
    private var simpleFragmentShader = 0
    private var triProgram = 0
    private var triPositionParam = 0
    private var triColorParam = 0
    private var triMVPMatrixParam = 0

    private lateinit var cubeVerticesBuffer: FloatBuffer
    private lateinit var cubeColorsBuffer: FloatBuffer
    private lateinit var cubeFoundColorsBuffer: FloatBuffer
    private lateinit var cubeNormalsBuffer: FloatBuffer

    private var cubeProgram = 0
    private var cubePositionParam = 0
    private var cubeColorParam = 0
    private var cubeMVPMatrixParam = 0

    private var lightVertexShader = 0
    private var passthroughFragmentShader = 0

    private var cubeNormalParam = 0
    private var cubeModelViewParam = 0
    private var cubeLightPosParam = 0

    private var gridFragmentShader = 0

    private lateinit var floorVerticesBuffer: FloatBuffer
    private lateinit var floorColorsBuffer: FloatBuffer
    private lateinit var floorNormalsBuffer: FloatBuffer

    private var floorProgram = 0
    private var floorPositionParam = 0
    private var floorColorParam = 0
    private var floorMVPMatrixParam = 0
    private var floorNormalParam = 0
    private var floorModelParam = 0
    private var floorModelViewParam = 0
    private var floorLightPosParam = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cubeRenderer = CubeRenderer(this)
        cubeRenderer.ajustarVistaInicial()

        val cardboardView = CardboardView(this)
        cardboardView.setRenderer(this)

        setCardboardView(cardboardView, cubeRenderer)
        setContentView(cardboardView)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event != null) {
            if ((event.source and InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD) {
                if (event.repeatCount == 0) {
                    when (keyCode) {
                        KeyEvent.KEYCODE_BUTTON_Y -> cubeRenderer.MoveUpward(5f)
                        KeyEvent.KEYCODE_BUTTON_A -> cubeRenderer.NextPosicion()
                        KeyEvent.KEYCODE_BUTTON_X -> cubeRenderer.MoveUpward(-5f)
                    }
                }
            }
        }

        return super.onKeyDown(keyCode, event)
    }

    override fun onGenericMotionEvent(event: MotionEvent): Boolean {
        if (Dpad.isDpadDevice(event)) {
            val press = dpad.getDirectionPressed(event)

            when (press) {
                LEFT -> {
                    cubeRenderer.StrafeRight(-10f)
                    return true
                }

                RIGHT -> {
                    cubeRenderer.StrafeRight(10f)
                    return true
                }

                UP -> {
                    cubeRenderer.MoveForward(-10f)
                    return true
                }

                DOWN -> {
                    cubeRenderer.MoveForward(10f)
                    return true
                }
            }
        }

        val x = event.x
        val y = event.y
        val dx = x - previousX
        val dy = y - previousY

        previousX = x
        previousY = y

        processJoystickInput(event, -1)

        return true
    }

    private fun processJoystickInput(event: MotionEvent, historyPos: Int): String {
        val inputDevice = event.device ?: return "(0,0)"

        var x: Float = getCenteredAxis(event, inputDevice, MotionEvent.AXIS_X, historyPos)

        if (x == 0f) {
            x = getCenteredAxis(event, inputDevice, MotionEvent.AXIS_Z, historyPos)
            if (x != 0f) {
                Joystick_Izquierdo_Derecho = 1
            }
        } else {
            Joystick_Izquierdo_Derecho = 0
        }

        var y: Float = getCenteredAxis(event, inputDevice, MotionEvent.AXIS_Y, historyPos)

        if (y == 0f) {
            y = getCenteredAxis(event, inputDevice, MotionEvent.AXIS_RZ, historyPos)
            if (y != 0f) {
                Joystick_Izquierdo_Derecho = 1
            }
        } else {
            Joystick_Izquierdo_Derecho = 0
        }

        return "(${x.toInt()},${y.toInt()})"
    }

    override fun onNewFrame(headTransform: HeadTransform?) {
        Matrix.setLookAtM(
            camera,
            0,
            0.0f,
            0.0f,
            CAMERA_Z,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            1.0f,
            0.0f
        )

        headTransform?.getHeadView(headView, 0)

        cubeRenderer.actualizar_vista()
    }

    override fun onDrawEye(eye: Eye?) {
        if (eye == null) {
            return
        }

        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        Matrix.multiplyMM(
            view,
            0,
            eye.eyeView,
            0,
            camera,
            0
        )

        Matrix.multiplyMV(
            lightPosInEyeSpace,
            0,
            view,
            0,
            LIGHT_POS_IN_WORLD_SPACE,
            0
        )

        cubeRenderer.actualizar_vista()
        cubeRenderer.onDrawFrame(null)
    }

    override fun onFinishFrame(viewport: Viewport?) {
    }

    override fun onSurfaceChanged(width: Int, height: Int) {
        cubeRenderer.onSurfaceChanged(null, width, height)
    }

    override fun onSurfaceCreated(eglConfig: EGLConfig?) {
        cubeRenderer.onSurfaceCreated(null, eglConfig)
        initializeScene()
        compileShaders()
        prepareRenderingTriangle()
        prepareRenderingCube()
        prepareRenderingFloor()
    }

    override fun onRendererShutdown() {
    }

    private fun drawTriangle() {
        GLES20.glUseProgram(triProgram)

        GLES20.glUniformMatrix4fv(
            triMVPMatrixParam,
            1,
            false,
            modelViewProjection,
            0
        )

        GLES20.glVertexAttribPointer(
            triPositionParam,
            COORDS_PER_VERTEX,
            GLES20.GL_FLOAT,
            false,
            0,
            triVerticesBuffer
        )

        GLES20.glUniform4fv(
            triColorParam,
            1,
            triColor,
            0
        )

        GLES20.glDrawArrays(
            GLES20.GL_TRIANGLES,
            0,
            triVertexCount
        )
    }

    private fun drawCube() {
        GLES20.glUseProgram(cubeProgram)

        GLES20.glUniform3fv(
            cubeLightPosParam,
            1,
            lightPosInEyeSpace,
            0
        )

        GLES20.glUniformMatrix4fv(
            cubeModelViewParam,
            1,
            false,
            cubeView,
            0
        )

        GLES20.glUniformMatrix4fv(
            cubeMVPMatrixParam,
            1,
            false,
            modelViewProjection,
            0
        )

        GLES20.glVertexAttribPointer(
            cubePositionParam,
            COORDS_PER_VERTEX,
            GLES20.GL_FLOAT,
            false,
            0,
            cubeVerticesBuffer
        )

        GLES20.glVertexAttribPointer(
            cubeNormalParam,
            3,
            GLES20.GL_FLOAT,
            false,
            0,
            cubeNormalsBuffer
        )

        if (isLookingAtObject(cubeView, cubeTransform)) {
            GLES20.glVertexAttribPointer(
                cubeColorParam,
                4,
                GLES20.GL_FLOAT,
                false,
                0,
                cubeFoundColorsBuffer
            )
        } else {
            GLES20.glVertexAttribPointer(
                cubeColorParam,
                4,
                GLES20.GL_FLOAT,
                false,
                0,
                cubeColorsBuffer
            )
        }

        GLES20.glDrawArrays(
            GLES20.GL_TRIANGLES,
            0,
            cubeVertexCount
        )
    }

    private fun drawFloor() {
        GLES20.glUseProgram(floorProgram)

        GLES20.glUniform3fv(
            floorLightPosParam,
            1,
            lightPosInEyeSpace,
            0
        )

        GLES20.glUniformMatrix4fv(
            floorModelParam,
            1,
            false,
            floorTransform,
            0
        )

        GLES20.glUniformMatrix4fv(
            floorModelViewParam,
            1,
            false,
            floorView,
            0
        )

        GLES20.glUniformMatrix4fv(
            floorMVPMatrixParam,
            1,
            false,
            modelViewProjection,
            0
        )

        GLES20.glVertexAttribPointer(
            floorPositionParam,
            COORDS_PER_VERTEX,
            GLES20.GL_FLOAT,
            false,
            0,
            floorVerticesBuffer
        )

        GLES20.glVertexAttribPointer(
            floorNormalParam,
            3,
            GLES20.GL_FLOAT,
            false,
            0,
            floorNormalsBuffer
        )

        GLES20.glVertexAttribPointer(
            floorColorParam,
            4,
            GLES20.GL_FLOAT,
            false,
            0,
            floorColorsBuffer
        )

        GLES20.glDrawArrays(
            GLES20.GL_TRIANGLES,
            0,
            floorVertexCount
        )
    }

    private fun initializeScene() {
        Matrix.setIdentityM(triTransform, 0)
        Matrix.translateM(triTransform, 0, 5f, 0f, -5f)

        Matrix.setIdentityM(cubeTransform, 0)
        Matrix.translateM(cubeTransform, 0, 0f, 0f, -cubeDistance)
        Matrix.rotateM(cubeTransform, 0, 30f, 1f, 1f, 0f)

        Matrix.setIdentityM(floorTransform, 0)
        Matrix.translateM(floorTransform, 0, 0f, -floorDepth, 0f)
    }

    private fun compileShaders() {
        simpleVertexShader = loadShader(GLES20.GL_VERTEX_SHADER, R.raw.mvp_vertex)
        simpleFragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, R.raw.simple_fragment)

        lightVertexShader = loadShader(GLES20.GL_VERTEX_SHADER, R.raw.light_vertex)
        passthroughFragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, R.raw.passthrough_fragment)

        gridFragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, R.raw.grid_fragment)
    }

    private fun prepareRenderingTriangle() {
        val bb = ByteBuffer.allocateDirect(triCoords.size * 4)
        bb.order(ByteOrder.nativeOrder())

        triVerticesBuffer = bb.asFloatBuffer()
        triVerticesBuffer.put(triCoords)
        triVerticesBuffer.position(0)

        triProgram = GLES20.glCreateProgram()
        GLES20.glAttachShader(triProgram, simpleVertexShader)
        GLES20.glAttachShader(triProgram, simpleFragmentShader)
        GLES20.glLinkProgram(triProgram)
        GLES20.glUseProgram(triProgram)

        triPositionParam = GLES20.glGetAttribLocation(triProgram, "a_Position")
        GLES20.glEnableVertexAttribArray(triPositionParam)

        triColorParam = GLES20.glGetUniformLocation(triProgram, "u_Color")
        triMVPMatrixParam = GLES20.glGetUniformLocation(triProgram, "u_MVP")
    }

    private fun prepareRenderingCube() {
        val bb = ByteBuffer.allocateDirect(cubeCoords.size * 4)
        bb.order(ByteOrder.nativeOrder())

        cubeVerticesBuffer = bb.asFloatBuffer()
        cubeVerticesBuffer.put(cubeCoords)
        cubeVerticesBuffer.position(0)

        val bbColors = ByteBuffer.allocateDirect(cubeColors.size * 4)
        bbColors.order(ByteOrder.nativeOrder())

        cubeColorsBuffer = bbColors.asFloatBuffer()
        cubeColorsBuffer.put(cubeColors)
        cubeColorsBuffer.position(0)

        val bbFoundColors = ByteBuffer.allocateDirect(cubeFoundColors.size * 4)
        bbFoundColors.order(ByteOrder.nativeOrder())

        cubeFoundColorsBuffer = bbFoundColors.asFloatBuffer()
        cubeFoundColorsBuffer.put(cubeFoundColors)
        cubeFoundColorsBuffer.position(0)

        val bbNormals = ByteBuffer.allocateDirect(cubeNormals.size * 4)
        bbNormals.order(ByteOrder.nativeOrder())

        cubeNormalsBuffer = bbNormals.asFloatBuffer()
        cubeNormalsBuffer.put(cubeNormals)
        cubeNormalsBuffer.position(0)

        cubeProgram = GLES20.glCreateProgram()
        GLES20.glAttachShader(cubeProgram, lightVertexShader)
        GLES20.glAttachShader(cubeProgram, passthroughFragmentShader)
        GLES20.glLinkProgram(cubeProgram)
        GLES20.glUseProgram(cubeProgram)

        cubeModelViewParam = GLES20.glGetUniformLocation(cubeProgram, "u_MVMatrix")
        cubeMVPMatrixParam = GLES20.glGetUniformLocation(cubeProgram, "u_MVP")
        cubeLightPosParam = GLES20.glGetUniformLocation(cubeProgram, "u_LightPos")

        cubePositionParam = GLES20.glGetAttribLocation(cubeProgram, "a_Position")
        cubeNormalParam = GLES20.glGetAttribLocation(cubeProgram, "a_Normal")
        cubeColorParam = GLES20.glGetAttribLocation(cubeProgram, "a_Color")

        GLES20.glEnableVertexAttribArray(cubePositionParam)
        GLES20.glEnableVertexAttribArray(cubeNormalParam)
        GLES20.glEnableVertexAttribArray(cubeColorParam)
    }

    private fun prepareRenderingFloor() {
        val bb = ByteBuffer.allocateDirect(floorCoords.size * 4)
        bb.order(ByteOrder.nativeOrder())

        floorVerticesBuffer = bb.asFloatBuffer()
        floorVerticesBuffer.put(floorCoords)
        floorVerticesBuffer.position(0)

        val bbColors = ByteBuffer.allocateDirect(floorColors.size * 4)
        bbColors.order(ByteOrder.nativeOrder())

        floorColorsBuffer = bbColors.asFloatBuffer()
        floorColorsBuffer.put(floorColors)
        floorColorsBuffer.position(0)

        val bbNormals = ByteBuffer.allocateDirect(floorNormals.size * 4)
        bbNormals.order(ByteOrder.nativeOrder())

        floorNormalsBuffer = bbNormals.asFloatBuffer()
        floorNormalsBuffer.put(floorNormals)
        floorNormalsBuffer.position(0)

        floorProgram = GLES20.glCreateProgram()
        GLES20.glAttachShader(floorProgram, lightVertexShader)
        GLES20.glAttachShader(floorProgram, gridFragmentShader)
        GLES20.glLinkProgram(floorProgram)
        GLES20.glUseProgram(floorProgram)

        floorPositionParam = GLES20.glGetAttribLocation(floorProgram, "a_Position")
        floorNormalParam = GLES20.glGetAttribLocation(floorProgram, "a_Normal")
        floorColorParam = GLES20.glGetAttribLocation(floorProgram, "a_Color")

        floorModelParam = GLES20.glGetUniformLocation(floorProgram, "u_Model")
        floorModelViewParam = GLES20.glGetUniformLocation(floorProgram, "u_MVMatrix")
        floorMVPMatrixParam = GLES20.glGetUniformLocation(floorProgram, "u_MVP")
        floorLightPosParam = GLES20.glGetUniformLocation(floorProgram, "u_LightPos")

        GLES20.glEnableVertexAttribArray(floorPositionParam)
        GLES20.glEnableVertexAttribArray(floorNormalParam)
        GLES20.glEnableVertexAttribArray(floorColorParam)
    }

    private fun isLookingAtObject(
        modelView: FloatArray,
        modelTransform: FloatArray
    ): Boolean {
        val initVec = floatArrayOf(0f, 0f, 0f, 1.0f)
        val objPositionVec = FloatArray(4)

        Matrix.multiplyMM(
            modelView,
            0,
            headView,
            0,
            modelTransform,
            0
        )

        Matrix.multiplyMV(
            objPositionVec,
            0,
            modelView,
            0,
            initVec,
            0
        )

        val pitch = atan2(
            objPositionVec[1].toDouble(),
            -objPositionVec[2].toDouble()
        ).toFloat()

        val yaw = atan2(
            objPositionVec[0].toDouble(),
            -objPositionVec[2].toDouble()
        ).toFloat()

        return abs(pitch) < PITCH_LIMIT && abs(yaw) < YAW_LIMIT
    }

    private fun loadShader(type: Int, resId: Int): Int {
        val code = readRawTextFile(resId)
        val shader = GLES20.glCreateShader(type)

        GLES20.glShaderSource(shader, code)
        GLES20.glCompileShader(shader)

        return shader
    }

    private fun readRawTextFile(resId: Int): String {
        val inputStream = resources.openRawResource(resId)

        return try {
            val reader = BufferedReader(InputStreamReader(inputStream))
            val sb = StringBuilder()
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                sb.append(line).append("\n")
            }

            reader.close()
            sb.toString()
        } catch (e: IOException) {
            e.printStackTrace()
            ""
        }
    }

    companion object {
        private const val TAG = "MainActivity"

        const val UP: Int = 0
        const val LEFT: Int = 1
        const val RIGHT: Int = 2
        const val DOWN: Int = 3

        private val LIGHT_POS_IN_WORLD_SPACE = floatArrayOf(
            0.0f,
            2.0f,
            0.0f,
            1.0f
        )

        private const val COORDS_PER_VERTEX = 3

        private val triCoords: FloatArray = floatArrayOf(
            0.0f, 0.6f, -1.0f,
            -0.5f, -0.3f, -1.0f,
            0.5f, -0.3f, -1.0f
        )

        private val cubeCoords: FloatArray = Cube.CUBE_COORDS
        private val cubeColors: FloatArray = Cube.cubeFacesToArray(Cube.CUBE_COLORS_FACES, 4)
        private val cubeFoundColors: FloatArray = Cube.cubeFacesToArray(Cube.CUBE_FOUND_COLORS_FACES, 4)
        private val cubeNormals: FloatArray = Cube.cubeFacesToArray(Cube.CUBE_NORMALS_FACES, 3)

        private val floorCoords: FloatArray = Floor.FLOOR_COORDS
        private val floorColors: FloatArray = Floor.FLOOR_COLORS
        private val floorNormals: FloatArray = Floor.FLOOR_NORMALS

        private const val Z_NEAR = 0.1f
        private const val Z_FAR = 100.0f
        private const val CAMERA_Z = 0.01f

        private const val TIME_DELTA = 0.3f

        private const val YAW_LIMIT = 0.12f
        private const val PITCH_LIMIT = 0.12f

        private fun getCenteredAxis(
            event: MotionEvent,
            device: InputDevice,
            axis: Int,
            historyPos: Int
        ): Float {
            val range = device.getMotionRange(axis, event.source)

            if (range != null) {
                val flat = range.flat

                val value =
                    if (historyPos < 0) {
                        event.getAxisValue(axis)
                    } else {
                        event.getHistoricalAxisValue(axis, historyPos)
                    }

                if (abs(value) > flat) {
                    return value
                }
            }

            return 0f
        }
    }
}