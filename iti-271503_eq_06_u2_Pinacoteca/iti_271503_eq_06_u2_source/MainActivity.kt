package upv.pm.may_ago_26.iti_271503.pgu2_eq06

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.InputDevice
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import javax.microedition.khronos.egl.EGLConfig

class MainActivity : ComponentActivity(), SensorEventListener, CardboardView.StereoRenderer {

    companion object {
        private const val UP = 0
        private const val LEFT = 1
        private const val RIGHT = 2
        private const val DOWN = 3
        private const val Z_NEAR = 1.0f
        private const val Z_FAR = 5000.0f
    }

    private var cardboardView: CardboardView? = null
    private var renderer: CubeRenderer? = null
    private var mediaPlayer: MediaPlayer? = null

    // Se conserva tu control actual por sensor de rotación.
    private lateinit var sensorManager: SensorManager
    private var rotationVectorSensor: Sensor? = null
    private val rotationMatrix = FloatArray(16)
    private val orientationAngles = FloatArray(3)

    private val dpad = Dpad()

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        renderer = CubeRenderer(this).also { it.ReiniciarPosiciones() }

        mediaPlayer = MediaPlayer.create(this, R.raw.minecraft).apply {
            isLooping = true
            start()
        }

        setContent { PinacotecaVrScreen() }
    }

    @Composable
    private fun PinacotecaVrScreen() {
        MaterialTheme {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { context ->
                        CardboardView(context).apply {
                            // Estas dos opciones son las que producen el efecto
                            // de lente: malla de distorsión + viñeteado negro.
                            distortionCorrectionEnabled = true
                            vignetteEnabled = true
                            chromaticAberrationCorrectionEnabled = false
                            setVRModeEnabled(true)

                            setRenderer(this@MainActivity)
                            cardboardView = this
                        }
                    }
                )
            }
        }

        DisposableEffect(Unit) {
            cardboardView?.onResume()
            onDispose { cardboardView?.onPause() }
        }
    }

    // ------------------------------------------------------------
    // CardboardView.StereoRenderer
    // ------------------------------------------------------------

    override fun onNewFrame(headTransform: HeadTransform?) {
        // CardboardView actualiza aquí sus parámetros de cabeza y lentes.
        // La orientación de la cámara de tu proyecto se sigue tomando del
        // TYPE_ROTATION_VECTOR para no alterar tu navegación existente.
    }

    override fun onDrawEye(eye: Eye?) {
        val currentEye = eye ?: return
        val cubeRenderer = renderer ?: return

        // El viewport y scissor de este ojo ya fueron configurados por
        // CardboardView.StereoRendererHelper.
        cubeRenderer.setEyeProjection(currentEye.getPerspective(Z_NEAR, Z_FAR))
        cubeRenderer.onDrawFrame(null)
    }

    override fun onFinishFrame(viewport: Viewport?) = Unit

    override fun onSurfaceChanged(width: Int, height: Int) {
        renderer?.onSurfaceChanged(null, width, height)
    }

    override fun onSurfaceCreated(eglConfig: EGLConfig?) {
        if (eglConfig != null) {
            renderer?.onSurfaceCreated(null, eglConfig)
        }
    }

    override fun onRendererShutdown() = Unit

    // ------------------------------------------------------------
    // Ciclo de vida
    // ------------------------------------------------------------

    override fun onResume() {
        super.onResume()
        rotationVectorSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
        cardboardView?.onResume()

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.minecraft).apply { isLooping = true }
        }
        mediaPlayer?.takeIf { !it.isPlaying }?.start()
    }

    override fun onPause() {
        sensorManager.unregisterListener(this)
        cardboardView?.onPause()
        mediaPlayer?.takeIf { it.isPlaying }?.pause()
        super.onPause()
    }

    override fun onStop() {
        mediaPlayer?.release()
        mediaPlayer = null
        super.onStop()
    }

    // ------------------------------------------------------------
    // Controles existentes
    // ------------------------------------------------------------

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if ((event.source and InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD && event.repeatCount == 0) {
            when (keyCode) {
                KeyEvent.KEYCODE_BUTTON_Y -> renderer?.MoveUpward(5f)
                KeyEvent.KEYCODE_BUTTON_A -> renderer?.ReiniciarPosiciones()
                KeyEvent.KEYCODE_BUTTON_X -> {
                    renderer?.MoveUpward(-5f)
                    Log.d("control", "Abajo")
                }
                else -> return super.onKeyDown(keyCode, event)
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onGenericMotionEvent(event: MotionEvent): Boolean {
        if ((event.source and InputDevice.SOURCE_JOYSTICK) != InputDevice.SOURCE_JOYSTICK &&
            (event.source and InputDevice.SOURCE_GAMEPAD) != InputDevice.SOURCE_GAMEPAD &&
            !Dpad.isDpadDevice(event)
        ) {
            return super.onGenericMotionEvent(event)
        }

        if (Dpad.isDpadDevice(event)) {
            when (dpad.getDirectionPressed(event)) {
                LEFT -> { renderer?.StrafeRight(-6f); return true }
                RIGHT -> { renderer?.StrafeRight(6f); return true }
                UP -> { renderer?.MoveForward(-6f); return true }
                DOWN -> { renderer?.MoveForward(6f); return true }
            }
        }

        val device = event.device ?: return super.onGenericMotionEvent(event)
        val leftX = getCenteredAxis(event, device, MotionEvent.AXIS_X, -1)
        val leftY = getCenteredAxis(event, device, MotionEvent.AXIS_Y, -1)
        val rightX = getCenteredAxis(event, device, MotionEvent.AXIS_Z, -1)
        val rightY = getCenteredAxis(event, device, MotionEvent.AXIS_RZ, -1)

        var handled = false
        val moveSpeed = 4.0f
        val lookSpeed = 3.0f

        if (leftY != 0f) { renderer?.MoveForward(leftY * moveSpeed); handled = true }
        if (leftX != 0f) { renderer?.StrafeRight(leftX * moveSpeed); handled = true }
        if (rightY != 0f) { renderer?.RotateX(-rightY * lookSpeed); handled = true }
        if (rightX != 0f) { renderer?.RotateY(-rightX * lookSpeed); handled = true }

        return handled || super.onGenericMotionEvent(event)
    }

    private fun getCenteredAxis(
        event: MotionEvent,
        device: InputDevice,
        axis: Int,
        historyPos: Int
    ): Float {
        val range = device.getMotionRange(axis, event.source) ?: return 0f
        val value = if (historyPos < 0) {
            event.getAxisValue(axis)
        } else {
            event.getHistoricalAxisValue(axis, historyPos)
        }
        val deadZone = kotlin.math.max(range.flat, 0.18f)
        return if (kotlin.math.abs(value) > deadZone) value else 0f
    }

    override fun onSensorChanged(event: SensorEvent) {
        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
        SensorManager.remapCoordinateSystem(
            rotationMatrix,
            SensorManager.AXIS_X,
            SensorManager.AXIS_MINUS_Z,
            rotationMatrix
        )
        SensorManager.getOrientation(rotationMatrix, orientationAngles)
        renderer?.setOrientation(orientationAngles)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}
