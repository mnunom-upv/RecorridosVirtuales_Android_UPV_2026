package upv.pm.may_ago_26.iti_271503.pgu2_eq04


import android.app.Activity
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.View
import android.view.View.OnSystemUiVisibilityChangeListener
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.InputDevice
import android.view.MotionEvent

import upv.pm.may_ago_26.iti_271503.pgu2_eq04.MagnetSensor.OnCardboardTriggerListener
import upv.pm.may_ago_26.iti_271503.pgu2_eq04.NfcSensor.OnCardboardNfcListener

//import com.google.vrtoolkit.cardboard.sensors.MagnetSensor;
//import com.google.vrtoolkit.cardboard.sensors.NfcSensor;

open class CardboardActivity : Activity() {
    private var cubeRenderer: CubeRenderer? = null
    private var mCardboardView: CardboardView? = null
    private var mMagnetSensor: MagnetSensor? = null
    private var mNfcSensor: NfcSensor? = null
    private var sensorListener: SensorListener? = null
    private var axisLX = 0f
    private var axisLY = 0f
    var volumeKeysMode: Int = 0

    init {
        sensorListener = this.SensorListener()
    }

    fun setCardboardView(cardboardView: CardboardView?, cubeRende: CubeRenderer?) {
        mCardboardView = cardboardView

        cubeRenderer = cubeRende
        if (cardboardView == null) {
            return
        }
        val tagContents = mNfcSensor!!.tagContents
        if (tagContents != null) {
            updateCardboardDeviceParams(
                CardboardDeviceParams.Companion.createFromNfcContents(
                    tagContents
                )
            )
        }
    }

    var cardboardView: CardboardView?
        get() = mCardboardView
        private set(view) {
        }

    val nfcSensor: NfcSensor
        get() = mNfcSensor!!

    fun areVolumeKeysDisabled(): Boolean {
        when (this.volumeKeysMode) {
            VolumeKeys.NOT_DISABLED -> {
                return false
            }

            VolumeKeys.DISABLED_WHILE_IN_CARDBOARD -> {
                return this.mNfcSensor!!.isDeviceInCardboard
            }

            VolumeKeys.DISABLED -> {
                return true
            }

            else -> {
                throw IllegalStateException(
                    StringBuilder()
                        .append("Invalid volume keys mode ")
                        .append(this.volumeKeysMode).toString()
                )
            }
        }
    }

    fun onInsertedIntoCardboard(cardboardDeviceParams: CardboardDeviceParams?) {
        this.updateCardboardDeviceParams(cardboardDeviceParams)
    }

    fun onRemovedFromCardboard() {
    }

    fun onCardboardTrigger() {
    }

    protected fun updateCardboardDeviceParams(newParams: CardboardDeviceParams?) {
        if (mCardboardView != null) {
            mCardboardView!!.updateCardboardDeviceParams(newParams)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        getWindow().addFlags(WindowManager.LayoutParams.ALPHA_CHANGED)

        mMagnetSensor = MagnetSensor(this)
        mMagnetSensor!!.setOnCardboardTriggerListener(sensorListener)
        mNfcSensor = NfcSensor.Companion.getInstance(this)
        mNfcSensor!!.addOnCardboardNfcListener(sensorListener)

        mNfcSensor!!.onNfcIntent(this.getIntent())
        this.volumeKeysMode = VolumeKeys.DISABLED_WHILE_IN_CARDBOARD

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            val handler = Handler()
            getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(
                object : OnSystemUiVisibilityChangeListener {
                    override fun onSystemUiVisibilityChange(visibility: Int) {
                        if ((visibility and 0x2) == 0x0) {
                            handler.postDelayed(object : Runnable {
                                override fun run() {
                                    this@CardboardActivity.setFullscreenMode()
                                }
                            }, NAVIGATION_BAR_TIMEOUT_MS)
                        }
                    }
                })
        }
    }

    override fun onResume() {
        super.onResume()
        if (mCardboardView != null) {
            mCardboardView!!.onResume()
        }

        mMagnetSensor!!.start()
        mNfcSensor!!.onResume(this)

        if (cubeRenderer != null && cubeRenderer!!.sensorManager != null) {
            cubeRenderer!!.sensorManager.registerListener(
                cubeRenderer,
                cubeRenderer!!.rotationVectorSensor,
                SensorManager.SENSOR_DELAY_UI
            )
        }

        cubeRenderer!!.startSensors()
    }

    override fun onPause() {
        super.onPause()
        if (mCardboardView != null) {
            mCardboardView!!.onPause()
        }
        mMagnetSensor!!.stop()
        mNfcSensor!!.onPause(this)

        if (cubeRenderer != null && cubeRenderer!!.sensorManager != null) {
            cubeRenderer!!.sensorManager.unregisterListener(cubeRenderer)
        }

        cubeRenderer!!.stopSensors()
    }

    override fun onDestroy() {
        mNfcSensor!!.removeOnCardboardNfcListener(sensorListener)
        super.onDestroy()
    }

    override fun setContentView(view: View?) {
        if (view is CardboardView) {
            this.cardboardView = view
        }
        super.setContentView(view)
    }

    override fun setContentView(view: View?, params: ViewGroup.LayoutParams?) {
        if (view is CardboardView) {
            this.cardboardView = view
        }
        super.setContentView(view, params)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return ((keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
                && areVolumeKeysDisabled()) || super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        return ((keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
                && areVolumeKeysDisabled()) || super.onKeyUp(keyCode, event)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            setFullscreenMode()
        }
    }

    private fun setFullscreenMode() {
        getWindow().getDecorView().setSystemUiVisibility(5894)
    }

    object VolumeKeys {
        const val NOT_DISABLED: Int = 0
        const val DISABLED: Int = 1
        const val DISABLED_WHILE_IN_CARDBOARD: Int = 2
    }

    private inner class SensorListener

        : OnCardboardTriggerListener, OnCardboardNfcListener {
        override fun onInsertedIntoCardboard(deviceParams: CardboardDeviceParams?) {
            this@CardboardActivity.onInsertedIntoCardboard(deviceParams)
        }

        override fun onRemovedFromCardboard() {
            this@CardboardActivity.onRemovedFromCardboard()
        }

        override fun onCardboardTrigger() {
            this@CardboardActivity.onCardboardTrigger()
        }
    }

    override fun dispatchGenericMotionEvent(event: MotionEvent): Boolean {
        if ((event.source and InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK &&
            event.action == MotionEvent.ACTION_MOVE) {

            val lx = event.getAxisValue(MotionEvent.AXIS_X)
            val ly = event.getAxisValue(MotionEvent.AXIS_Y)

            axisLX = if (kotlin.math.abs(lx) > 0.15f) lx else 0f
            axisLY = if (kotlin.math.abs(ly) > 0.15f) ly else 0f

            // NUEVO: empujar los valores al renderer, en el hilo GL
            mCardboardView?.queueEvent {
                cubeRenderer?.axisLX = axisLX
                cubeRenderer?.axisLY = axisLY
            }
        }
        return super.dispatchGenericMotionEvent(event)
    }

    companion object {
        private const val NAVIGATION_BAR_TIMEOUT_MS = 2000L
    }
}