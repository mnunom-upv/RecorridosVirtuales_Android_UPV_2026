package upv.pm.may_ago_26.iti_271503.pgu2_eq04

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper

class DeviceSensorLooper(private val mSensorManager: SensorManager) : SensorEventProvider {
    private var mIsRunning = false
    private var mSensorLooper: Looper? = null
    private var mSensorEventListener: SensorEventListener? = null
    private val mRegisteredListeners: ArrayList<SensorEventListener>
    override fun start() {
        if (this.mIsRunning) {
            return
        }
        this.mSensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                for (listener in this@DeviceSensorLooper.mRegisteredListeners) {
                    synchronized(listener) {
                        listener.onSensorChanged(event)
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                for (listener in this@DeviceSensorLooper.mRegisteredListeners) {
                    synchronized(listener) {
                        listener.onAccuracyChanged(sensor, accuracy)
                    }
                }
            }
        } as SensorEventListener
        val sensorThread: HandlerThread = object : HandlerThread("sensor") {
            override fun onLooperPrepared() {
                val handler = Handler(Looper.myLooper()!!)
                for (sensorType in INPUT_SENSORS) {
                    val sensor = this@DeviceSensorLooper.mSensorManager.getDefaultSensor(sensorType)
                    //DeviceSensorLooper.this.mSensorManager.registerListener(DeviceSensorLooper.this.mSensorEventListener, sensor, 0, handler);
                    this@DeviceSensorLooper.mSensorManager.registerListener(
                        this@DeviceSensorLooper.mSensorEventListener,
                        sensor,
                        0,
                        handler
                    )
                }
            }
        }
        sensorThread.start()
        this.mSensorLooper = sensorThread.getLooper()
        this.mIsRunning = true
    }

    override fun stop() {
        if (!this.mIsRunning) {
            return
        }
        this.mSensorManager.unregisterListener(this.mSensorEventListener)
        this.mSensorEventListener = null
        this.mSensorLooper!!.quit()
        this.mSensorLooper = null
        this.mIsRunning = false
    }

    override fun registerListener(listener: SensorEventListener?) {
        synchronized(this.mRegisteredListeners) {
            this.mRegisteredListeners.add(listener!!)
        }
    }

    override fun unregisterListener(listener: SensorEventListener?) {
        synchronized(this.mRegisteredListeners) {
            this.mRegisteredListeners.remove(listener)
        }
    }

    init {
        this.mRegisteredListeners = ArrayList<SensorEventListener>()
    }

    companion object {
        private val INPUT_SENSORS: IntArray

        init {
            INPUT_SENSORS = intArrayOf(1, 4)
        }
    }
}
