package upv.pm.may_ago_26.iti_271503.pgu2_eq04

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.opengl.Matrix
import android.view.Display
import android.view.WindowManager
import java.util.concurrent.TimeUnit
import kotlin.concurrent.Volatile

class HeadTracker(
    sensorEventProvider: SensorEventProvider,
    clock: Clock,
    display: Display
) : SensorEventListener {

    private val mDisplay: Display = display
    private val mEkfToHeadTracker: FloatArray = FloatArray(16)
    private val mSensorToDisplay: FloatArray = FloatArray(16)
    private var mDisplayRotation: Float = Float.NaN
    private val mNeckModelTranslation: FloatArray = FloatArray(16)
    private val mTmpHeadView: FloatArray = FloatArray(16)
    private val mTmpHeadView2: FloatArray = FloatArray(16)
    private var mNeckModelEnabled: Boolean = DEFAULT_NECK_MODEL_ENABLED

    @Volatile
    private var mTracking = false

    private val mTracker: OrientationEKF = OrientationEKF()
    private val mSensorEventProvider: SensorEventProvider = sensorEventProvider
    private val mClock: Clock = clock
    private var mLatestGyroEventClockTimeNs: Long = 0
    private val mGyroBias: Vector3d = Vector3d()
    private val mLatestGyro: Vector3d = Vector3d()
    private val mLatestAcc: Vector3d = Vector3d()

    init {
        Matrix.setIdentityM(mNeckModelTranslation, 0)
        Matrix.translateM(
            mNeckModelTranslation,
            0,
            0.0f,
            -DEFAULT_NECK_VERTICAL_OFFSET,
            DEFAULT_NECK_HORIZONTAL_OFFSET
        )
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                mLatestAcc.set(
                    event.values[0].toDouble(),
                    event.values[1].toDouble(),
                    event.values[2].toDouble()
                )

                mTracker.processAcc(mLatestAcc, event.timestamp)
            }

            Sensor.TYPE_GYROSCOPE -> {
                mLatestGyroEventClockTimeNs = mClock.nanoTime()

                mLatestGyro.set(
                    event.values[0].toDouble(),
                    event.values[1].toDouble(),
                    event.values[2].toDouble()
                )

                Vector3d.sub(mLatestGyro, mGyroBias, mLatestGyro)
                mTracker.processGyro(mLatestGyro, event.timestamp)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No se requiere acción.
    }

    fun startTracking() {
        if (mTracking) {
            return
        }

        mTracker.reset()
        mSensorEventProvider.registerListener(this)
        mSensorEventProvider.start()
        mTracking = true
    }

    fun stopTracking() {
        if (!mTracking) {
            return
        }

        mSensorEventProvider.unregisterListener(this)
        mSensorEventProvider.stop()
        mTracking = false
    }

    fun setGyroBias(gyroBias: FloatArray?) {
        if (gyroBias == null) {
            mGyroBias.setZero()
            return
        }

        require(gyroBias.size == 3) {
            "Gyro bias should be an array of 3 values"
        }

        mGyroBias.set(
            gyroBias[0].toDouble(),
            gyroBias[1].toDouble(),
            gyroBias[2].toDouble()
        )
    }

    fun setNeckModelEnabled(enabled: Boolean) {
        mNeckModelEnabled = enabled
    }

    fun getLastHeadView(headView: FloatArray, offset: Int) {
        require(offset + 16 <= headView.size) {
            "Not enough space to write the result"
        }

        val rotation = when (mDisplay.rotation) {
            0 -> 0.0f
            1 -> 90.0f
            2 -> 180.0f
            3 -> 270.0f
            else -> 0.0f
        }

        if (rotation != mDisplayRotation) {
            mDisplayRotation = rotation

            Matrix.setRotateEulerM(
                mSensorToDisplay,
                0,
                0.0f,
                0.0f,
                -rotation
            )

            Matrix.setRotateEulerM(
                mEkfToHeadTracker,
                0,
                -90.0f,
                0.0f,
                rotation
            )
        }

        synchronized(mTracker) {
            val secondsSinceLastGyroEvent =
                TimeUnit.NANOSECONDS.toSeconds(
                    mClock.nanoTime() - mLatestGyroEventClockTimeNs
                ).toDouble()

            val secondsToPredictForward = secondsSinceLastGyroEvent + 1.0 / 30.0
            val mat = mTracker.getPredictedGLMatrix(secondsToPredictForward)

            for (i in 0 until 16) {
                mTmpHeadView[i] = mat[i].toFloat()
            }
        }

        Matrix.multiplyMM(
            mTmpHeadView2,
            0,
            mSensorToDisplay,
            0,
            mTmpHeadView,
            0
        )

        Matrix.multiplyMM(
            headView,
            offset,
            mTmpHeadView2,
            0,
            mEkfToHeadTracker,
            0
        )

        if (mNeckModelEnabled) {
            Matrix.multiplyMM(
                mTmpHeadView,
                0,
                mNeckModelTranslation,
                0,
                headView,
                offset
            )

            Matrix.translateM(
                headView,
                offset,
                mTmpHeadView,
                0,
                0.0f,
                DEFAULT_NECK_VERTICAL_OFFSET,
                0.0f
            )
        }
    }

    companion object {
        private const val DEFAULT_NECK_HORIZONTAL_OFFSET = 0.08f
        private const val DEFAULT_NECK_VERTICAL_OFFSET = 0.075f
        private const val DEFAULT_NECK_MODEL_ENABLED = false

        fun createFromContext(context: Context): HeadTracker {
            val sensorManager =
                context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

            val windowManager =
                context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

            val display = windowManager.defaultDisplay

            return HeadTracker(
                DeviceSensorLooper(sensorManager),
                SystemClock(),
                display
            )
        }
    }
}