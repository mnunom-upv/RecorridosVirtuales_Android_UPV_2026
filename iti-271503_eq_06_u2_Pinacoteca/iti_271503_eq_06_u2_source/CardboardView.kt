package upv.pm.may_ago_26.iti_271503.pgu2_eq06
import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import java.lang.String
import java.util.concurrent.CountDownLatch
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.concurrent.Volatile
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.min
import kotlin.math.tan
import kotlin.text.StringBuilder

//import com.google.vrtoolkit.cardboard.sensors.*;

//import com.google.vrtoolkit.cardboard.proto.*;
class CardboardView : GLSurfaceView {
    private var mRendererHelper: RendererHelper? = null
    private var mHeadTracker: HeadTracker? = null
    private var mHmdManager: HeadMountedDisplayManager? = null
    private var mUiLayer: UiLayer? = null
    private var mShutdownLatch: CountDownLatch? = null
    var vRMode: Boolean
        private set
    private var mRendererSet: Boolean

    @Volatile
    private var mRestoreGLStateEnabled: Boolean

    @Volatile
    private var mDistortionCorrectionEnabled: Boolean

    @Volatile
    private var mChromaticAberrationCorrectionEnabled: Boolean

    @Volatile
    private var mVignetteEnabled: Boolean

    constructor(context: Context) : super(context) {
        this.vRMode = true
        this.mRendererSet = false
        this.mRestoreGLStateEnabled = true
        this.mDistortionCorrectionEnabled = true
        this.mChromaticAberrationCorrectionEnabled = false
        this.mVignetteEnabled = true
        this.init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        this.vRMode = true
        this.mRendererSet = false
        this.mRestoreGLStateEnabled = true
        this.mDistortionCorrectionEnabled = true
        this.mChromaticAberrationCorrectionEnabled = false
        this.mVignetteEnabled = true
        this.init(context)
    }

    fun setRenderer(renderer: Renderer?) {
        if (renderer == null) {
            return
        }
        this.mRendererHelper!!.setRenderer(renderer)
        super.setRenderer(this.mRendererHelper as GLSurfaceView.Renderer)
        this.mRendererSet = true
    }

    fun setRenderer(renderer: StereoRenderer?) {
        this.setRenderer(if (renderer != null) StereoRendererHelper(renderer) else (null as Renderer?))
    }

    fun getCurrentEyeParams(head: HeadTransform, leftEye: Eye, rightEye: Eye, monocular: Eye) {
        this.mRendererHelper!!.getCurrentEyeParams(head, leftEye, rightEye, monocular)
    }

    fun setVRModeEnabled(enabled: Boolean) {
        this.vRMode = enabled
        this.mRendererHelper!!.setVRModeEnabled(enabled)
    }

    var alignmentMarkerEnabled: Boolean
        get() = this.mUiLayer!!.alignmentMarkerEnabled
        set(enabled) {
            this.mUiLayer!!.alignmentMarkerEnabled = enabled
        }

    var settingsButtonEnabled: Boolean
        get() = this.mUiLayer!!.settingsButtonEnabled
        set(enabled) {
            this.mUiLayer!!.settingsButtonEnabled = enabled
        }

    val headMountedDisplay: HeadMountedDisplay?
        get() = this.mHmdManager!!.headMountedDisplay

    var restoreGLStateEnabled: Boolean
        get() = this.mRestoreGLStateEnabled
        set(enabled) {
            this.mRestoreGLStateEnabled = enabled
            this.mRendererHelper!!.setRestoreGLStateEnabled(enabled)
        }

    var chromaticAberrationCorrectionEnabled: Boolean
        get() = this.mChromaticAberrationCorrectionEnabled
        set(enabled) {
            this.mChromaticAberrationCorrectionEnabled = enabled
            this.mRendererHelper!!.setChromaticAberrationCorrectionEnabled(enabled)
        }

    var vignetteEnabled: Boolean
        get() = this.mVignetteEnabled
        set(enabled) {
            this.mVignetteEnabled = enabled
            this.mRendererHelper!!.setVignetteEnabled(enabled)
        }

    fun setNeckModelEnabled(enabled: Boolean) {
        this.mHeadTracker!!.setNeckModelEnabled(enabled)
    }

    fun updateCardboardDeviceParams(cardboardDeviceParams: CardboardDeviceParams?) {
        if (this.mHmdManager!!.updateCardboardDeviceParams(cardboardDeviceParams)) {
            this.mRendererHelper!!.setCardboardDeviceParams(this.cardboardDeviceParams)
        }
    }

    val cardboardDeviceParams: CardboardDeviceParams
        get() = this.mHmdManager!!.headMountedDisplay!!.cardboardDeviceParams!!

    fun updateScreenParams(screenParams: ScreenParams?) {
        if (this.mHmdManager!!.updateScreenParams(screenParams)) {
            this.mRendererHelper!!.setScreenParams(this.screenParams)
        }
    }

    val screenParams: ScreenParams
        get() = this.mHmdManager!!.headMountedDisplay!!.screenParams

    val interpupillaryDistance: Float
        get() = this.cardboardDeviceParams.interLensDistance

    var distortionCorrectionEnabled: Boolean
        get() = this.mDistortionCorrectionEnabled
        set(enabled) {
            this.mDistortionCorrectionEnabled = enabled
            this.mRendererHelper!!.setDistortionCorrectionEnabled(enabled)
        }

    fun setDistortionCorrectionTextureFormat(textureFormat: Int, textureType: Int) {
        this.mRendererHelper!!.setDistortionCorrectionTextureFormat(textureFormat, textureType)
    }

    fun undistortTexture(inputTexture: Int) {
        this.mRendererHelper!!.undistortTexture(inputTexture)
    }

    fun renderUiLayer() {
        this.mRendererHelper!!.renderUiLayer()
    }

    fun setDistortionCorrectionScale(scale: Float) {
        this.mRendererHelper!!.setDistortionCorrectionScale(scale)
    }

    override fun onResume() {
        this.mHmdManager!!.onResume()
        this.mRendererHelper!!.setCardboardDeviceParams(this.cardboardDeviceParams)
        if (this.mRendererSet) {
            super.onResume()
        }
        val phoneParams = PhoneParams.readFromExternalStorage()
        if (phoneParams != null) {
            this.mHeadTracker!!.setGyroBias(phoneParams.gyroBias)
        }
        this.mHeadTracker!!.startTracking()
    }

    override fun onPause() {
        this.mHmdManager!!.onPause()
        if (this.mRendererSet) {
            super.onPause()
        }
        this.mHeadTracker!!.stopTracking()
    }

    override fun queueEvent(r: Runnable) {
        if (!this.mRendererSet) {
            r.run()
            return
        }
        super.queueEvent(r)
    }

    override fun setRenderer(renderer: GLSurfaceView.Renderer?) {
        throw RuntimeException("Please use the CardboardView renderer interfaces")
    }

    public override fun onDetachedFromWindow() {
        if (this.mRendererSet && this.mShutdownLatch == null) {
            this.mShutdownLatch = CountDownLatch(1)
            this.mRendererHelper!!.shutdown()
            try {
                this.mShutdownLatch!!.await()
            } catch (e: InterruptedException) {
                val s = "CardboardView"
                val s2 = "Interrupted during shutdown: "
                val value = e.toString().toString()
                Log.e(s, if (value.length != 0) (s2 + value) else String(s2) as kotlin.String)
            }
            this.mShutdownLatch = null
        }
        super.onDetachedFromWindow()
    }

    private fun init(context: Context) {
        this.setEGLContextClientVersion(2)
        this.setPreserveEGLContextOnPause(true)
        this.mHeadTracker = HeadTracker.Companion.createFromContext(context)
        this.mHmdManager = HeadMountedDisplayManager(context)
        this.mRendererHelper = RendererHelper()
        this.mUiLayer = UiLayer(context)
    }

    override fun onTouchEvent(e: MotionEvent?): Boolean {
        return this.mUiLayer!!.onTouchEvent(e!!) || super.onTouchEvent(e)
    }

    private inner class RendererHelper : GLSurfaceView.Renderer {
        private val mHeadTransform: HeadTransform
        private val mMonocular: Eye
        private val mLeftEye: Eye
        private val mRightEye: Eye
        private val mLeftEyeTranslate: FloatArray
        private val mRightEyeTranslate: FloatArray
        private var mRenderer: Renderer? = null
        private var mSurfaceCreated = false
        private val mHmd: HeadMountedDisplay
        private var mDistortionRenderer: DistortionRenderer? = null
        private var mVRMode: Boolean
        private var mDistortionCorrectionEnabled: Boolean
        private var mProjectionChanged: Boolean
        private var mInvalidSurfaceSize = false

        init {
            this.mHmd = HeadMountedDisplay(this@CardboardView.headMountedDisplay!!)
            this.mHeadTransform = HeadTransform()
            this.mMonocular = Eye(0)
            this.mLeftEye = Eye(1)
            this.mRightEye = Eye(2)
            this.updateFieldOfView(this.mLeftEye.fov, this.mRightEye.fov)
            val dr = DistortionRenderer()
            this.mDistortionRenderer = dr
            dr.setRestoreGLStateEnabled(
                this@CardboardView.mRestoreGLStateEnabled
            )
            dr.setChromaticAberrationCorrectionEnabled(this@CardboardView.mChromaticAberrationCorrectionEnabled)
            dr.setVignetteEnabled(this@CardboardView.mVignetteEnabled)
            this.mLeftEyeTranslate = FloatArray(16)
            this.mRightEyeTranslate = FloatArray(16)
            this.mVRMode = this@CardboardView.vRMode
            this.mDistortionCorrectionEnabled = this@CardboardView.mDistortionCorrectionEnabled
            this.mProjectionChanged = true
        }

        fun setRenderer(renderer: Renderer?) {
            this.mRenderer = renderer
        }

        fun shutdown() {
            this@CardboardView.queueEvent(object : Runnable {
                override fun run() {
                    if (this@RendererHelper.mRenderer != null && this@RendererHelper.mSurfaceCreated) {
                        this@RendererHelper.mSurfaceCreated = false
                        this@RendererHelper.mRenderer!!.onRendererShutdown()
                    }
                    this@CardboardView.mShutdownLatch!!.countDown()
                }
            })
        }

        fun setCardboardDeviceParams(newParams: CardboardDeviceParams?) {
            val deviceParams = if (newParams != null) CardboardDeviceParams(newParams) else null
            this@CardboardView.queueEvent(object : Runnable {
                override fun run() {
                    this@RendererHelper.mHmd.cardboardDeviceParams = deviceParams
                    this@RendererHelper.mProjectionChanged = true
                }
            })
        }

        fun setScreenParams(newParams: ScreenParams) {
            val sParams = ScreenParams(newParams)
            this@CardboardView.queueEvent(object : Runnable {
                override fun run() {
                    this@RendererHelper.mHmd.screenParams = sParams
                    this@RendererHelper.mProjectionChanged = true
                }
            })
        }

        fun setDistortionCorrectionEnabled(enabled: Boolean) {
            this@CardboardView.queueEvent(object : Runnable {
                override fun run() {
                    this@RendererHelper.mDistortionCorrectionEnabled = enabled
                    this@RendererHelper.mProjectionChanged = true
                }
            })
        }

        fun setDistortionCorrectionScale(scale: Float) {
            this@CardboardView.queueEvent(object : Runnable {
                override fun run() {
                    this@RendererHelper.mDistortionRenderer!!.setResolutionScale(scale)
                }
            })
        }

        fun setVRModeEnabled(enabled: Boolean) {
            this@CardboardView.queueEvent(object : Runnable {
                override fun run() {
                    if (this@RendererHelper.mVRMode == enabled) {
                        return
                    }
                    this@RendererHelper.mVRMode = enabled
                    if (this@RendererHelper.mRenderer is StereoRendererHelper) {
                        val stereoHelper: StereoRendererHelper =
                            this@RendererHelper.mRenderer as StereoRendererHelper
                        stereoHelper.setVRModeEnabled(enabled)
                    }
                    this@RendererHelper.mProjectionChanged = true
                    this@RendererHelper.onSurfaceChanged(
                        null,
                        this@RendererHelper.mHmd.screenParams.width,
                        this@RendererHelper.mHmd.screenParams.height
                    )
                }
            })
        }

        fun setRestoreGLStateEnabled(enabled: Boolean) {
            this@CardboardView.queueEvent(object : Runnable {
                override fun run() {
                    this@RendererHelper.mDistortionRenderer!!.setRestoreGLStateEnabled(enabled)
                }
            })
        }

        fun setChromaticAberrationCorrectionEnabled(enabled: Boolean) {
            this@CardboardView.queueEvent(object : Runnable {
                override fun run() {
                    this@RendererHelper.mDistortionRenderer!!.setChromaticAberrationCorrectionEnabled(
                        enabled
                    )
                }
            })
        }

        fun setVignetteEnabled(enabled: Boolean) {
            this@CardboardView.queueEvent(object : Runnable {
                override fun run() {
                    this@RendererHelper.mDistortionRenderer!!.setVignetteEnabled(enabled)
                    this@RendererHelper.mProjectionChanged = true
                }
            })
        }

        fun setDistortionCorrectionTextureFormat(textureFormat: Int, textureType: Int) {
            this@CardboardView.queueEvent(object : Runnable {
                override fun run() {
                    this@RendererHelper.mDistortionRenderer!!.setTextureFormat(
                        textureFormat,
                        textureType
                    )
                }
            })
        }

        fun undistortTexture(inputTexture: Int) {
            this@CardboardView.queueEvent(object : Runnable {
                override fun run() {
                    this@RendererHelper.mDistortionRenderer!!.undistortTexture(inputTexture)
                }
            })
        }

        fun renderUiLayer() {
            this@CardboardView.queueEvent(object : Runnable {
                override fun run() {
                    this@CardboardView.mUiLayer!!.draw()
                }
            })
        }

        fun getCurrentEyeParams(head: HeadTransform, leftEye: Eye, rightEye: Eye, monocular: Eye) {
            val finished = CountDownLatch(1)
            this@CardboardView.queueEvent(object : Runnable {
                override fun run() {
                    this@RendererHelper.getFrameParams(head, leftEye, rightEye, monocular)
                    finished.countDown()
                }
            })
            try {
                finished.await()
            } catch (e: InterruptedException) {
                val s = "CardboardView"
                val s2 = "Interrupted while reading frame params: "
                val value = e.toString().toString()
                Log.e(s, if (value.length != 0) (s2 + value) else String(s2) as kotlin.String)
            }
        }

        fun getFrameParams(head: HeadTransform, leftEye: Eye, rightEye: Eye, monocular: Eye) {
            val cdp = this.mHmd.cardboardDeviceParams!!
            val screen = this.mHmd.screenParams!!
            this@CardboardView.mHeadTracker!!.getLastHeadView(head.headView, 0)
            val halfInterpupillaryDistance = cdp.interLensDistance * 0.5f
            if (this.mVRMode) {
                Matrix.setIdentityM(this.mLeftEyeTranslate, 0)
                Matrix.setIdentityM(this.mRightEyeTranslate, 0)
                Matrix.translateM(this.mLeftEyeTranslate, 0, halfInterpupillaryDistance, 0.0f, 0.0f)
                Matrix.translateM(
                    this.mRightEyeTranslate,
                    0,
                    -halfInterpupillaryDistance,
                    0.0f,
                    0.0f
                )
                Matrix.multiplyMM(
                    leftEye.eyeView,
                    0,
                    this.mLeftEyeTranslate,
                    0,
                    head.headView,
                    0
                )
                Matrix.multiplyMM(
                    rightEye.eyeView,
                    0,
                    this.mRightEyeTranslate,
                    0,
                    head.headView,
                    0
                )
            } else {
                System.arraycopy(
                    head.headView,
                    0,
                    monocular.eyeView,
                    0,
                    head.headView.size
                )
            }
            if (this.mProjectionChanged) {
                monocular.viewport.setViewport(0, 0, screen.width, screen.height)
                this@CardboardView.mUiLayer!!.updateViewport(monocular.viewport)
                if (!this.mVRMode) {
                    this.updateMonocularFieldOfView(monocular.fov)
                } else if (this.mDistortionCorrectionEnabled) {
                    this.updateFieldOfView(leftEye.fov, rightEye.fov)
                    this.mDistortionRenderer?.onFovChanged(
                        this.mHmd, leftEye.fov, rightEye.fov,
                        this.virtualEyeToScreenDistance
                    )
                } else {
                    this.updateUndistortedFovAndViewport()
                }
                leftEye.setProjectionChanged()
                rightEye.setProjectionChanged()
                monocular.setProjectionChanged()
                this.mProjectionChanged = false
            }
            if (this.mDistortionCorrectionEnabled && this.mDistortionRenderer!!.haveViewportsChanged()) {
                this.mDistortionRenderer!!.updateViewports(
                    leftEye.viewport,
                    rightEye.viewport
                )
            }
        }

        override fun onDrawFrame(gl: GL10?) {
            if (this.mRenderer == null || !this.mSurfaceCreated || this.mInvalidSurfaceSize) {
                return
            }
            this.getFrameParams(this.mHeadTransform, this.mLeftEye, this.mRightEye, this.mMonocular)
            if (this.mVRMode) {
                if (this.mDistortionCorrectionEnabled) {
                    this.mDistortionRenderer?.beforeDrawFrame()
                    this.mRenderer!!.onDrawFrame(this.mHeadTransform, this.mLeftEye, this.mRightEye)
                    this.mDistortionRenderer?.afterDrawFrame()
                } else {
                    this.mRenderer!!.onDrawFrame(this.mHeadTransform, this.mLeftEye, this.mRightEye)
                }
            } else {
                this.mRenderer!!.onDrawFrame(this.mHeadTransform, this.mMonocular, null)
            }
            this.mRenderer!!.onFinishFrame(this.mMonocular.viewport)
            if (this.mVRMode) {
                this@CardboardView.mUiLayer!!.draw()
            }
        }

        override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
            if (this.mRenderer == null || !this.mSurfaceCreated) {
                return
            }
            val screen = this.mHmd.screenParams!!
            if (width != screen.width || height != screen.height) {
                if (!this.mInvalidSurfaceSize) {
                    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
                    Log.w(
                        "CardboardView",
                        StringBuilder(124).append("Surface size ").append(width).append("x")
                            .append(height).append(" does not match the expected screen size ")
                            .append(screen.width).append("x").append(screen.height)
                            .append(". Rendering is disabled.").toString()
                    )
                }
                //this.mInvalidSurfaceSize = true;
                this.mInvalidSurfaceSize = false

                // Por mis HuevoS!!!
            } else {
                this.mInvalidSurfaceSize = false
            }
            this.mRenderer!!.onSurfaceChanged(width, height)
        }

        override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
            if (this.mRenderer == null) {
                return
            }
            this.mSurfaceCreated = true
            this.mRenderer!!.onSurfaceCreated(config)
            this@CardboardView.mUiLayer!!.initializeGl()
        }

        fun updateFieldOfView(leftEyeFov: FieldOfView, rightEyeFov: FieldOfView) {
            val cdp = this.mHmd.cardboardDeviceParams!!
            val screen = this.mHmd.screenParams!!
            val distortion = cdp.distortion
            val eyeToScreenDist = this.virtualEyeToScreenDistance
            val outerDist = (screen.widthMeters - cdp.interLensDistance) / 2.0f
            val innerDist = cdp.interLensDistance / 2.0f
            val bottomDist = cdp.verticalDistanceToLensCenter - screen.borderSizeMeters
            val topDist =
                screen.heightMeters + screen.borderSizeMeters - cdp.verticalDistanceToLensCenter
            val outerAngle =
                Math.toDegrees(atan(distortion.distort(outerDist / eyeToScreenDist).toDouble()))
                    .toFloat()
            val innerAngle =
                Math.toDegrees(atan(distortion.distort(innerDist / eyeToScreenDist).toDouble()))
                    .toFloat()
            val bottomAngle =
                Math.toDegrees(atan(distortion.distort(bottomDist / eyeToScreenDist).toDouble()))
                    .toFloat()
            val topAngle =
                Math.toDegrees(atan(distortion.distort(topDist / eyeToScreenDist).toDouble()))
                    .toFloat()
            leftEyeFov.left = min(outerAngle, cdp.leftEyeMaxFov!!.left)
            leftEyeFov.right = min(innerAngle, cdp.leftEyeMaxFov!!.right)
            leftEyeFov.bottom = min(bottomAngle, cdp.leftEyeMaxFov!!.bottom)
            leftEyeFov.top = min(topAngle, cdp.leftEyeMaxFov!!.top)
            rightEyeFov.left = leftEyeFov.right
            rightEyeFov.right = leftEyeFov.left
            rightEyeFov.bottom = leftEyeFov.bottom
            rightEyeFov.top = leftEyeFov.top
        }

        fun updateMonocularFieldOfView(monocularFov: FieldOfView) {
            val screen = this.mHmd.screenParams!!
            val monocularBottomFov = 22.5f
            val monocularLeftFov =
                Math.toDegrees(atan(tan(Math.toRadians(monocularBottomFov.toDouble())) * screen.widthMeters / screen.heightMeters))
                    .toFloat()
            monocularFov.left = monocularLeftFov
            monocularFov.right = monocularLeftFov
            monocularFov.bottom = monocularBottomFov
            monocularFov.top = monocularBottomFov
        }

        fun updateUndistortedFovAndViewport() {
            val screen = this.mHmd.screenParams!!
            val cdp = this.mHmd.cardboardDeviceParams!!
            val halfLensDistance = cdp.interLensDistance / 2.0f
            val eyeToScreen = this.virtualEyeToScreenDistance
            val left = screen.widthMeters / 2.0f - halfLensDistance
            val right = halfLensDistance
            val bottom = cdp.verticalDistanceToLensCenter - screen.borderSizeMeters
            val top =
                screen.borderSizeMeters + screen.heightMeters - cdp.verticalDistanceToLensCenter
            val leftEyeFov = this.mLeftEye.fov
            leftEyeFov.left =
                Math.toDegrees(atan2(left.toDouble(), eyeToScreen.toDouble())).toFloat()
            leftEyeFov.right =
                Math.toDegrees(atan2(right.toDouble(), eyeToScreen.toDouble())).toFloat()
            leftEyeFov.bottom =
                Math.toDegrees(atan2(bottom.toDouble(), eyeToScreen.toDouble())).toFloat()
            leftEyeFov.top =
                Math.toDegrees(atan2(top.toDouble(), eyeToScreen.toDouble())).toFloat()
            val rightEyeFov = this.mRightEye.fov
            rightEyeFov.left = leftEyeFov.right
            rightEyeFov.right = leftEyeFov.left
            rightEyeFov.bottom = leftEyeFov.bottom
            rightEyeFov.top = leftEyeFov.top
            this.mLeftEye.viewport.setViewport(0, 0, screen.width / 2, screen.height)
            this.mRightEye.viewport
                .setViewport(screen.width / 2, 0, screen.width / 2, screen.height)
        }

        val virtualEyeToScreenDistance: Float
            get() = this.mHmd.cardboardDeviceParams!!.screenToLensDistance
    }

    private inner class StereoRendererHelper(private val mStereoRenderer: StereoRenderer) :
        Renderer {
        private var mVRMode: Boolean

        init {
            this.mVRMode = this@CardboardView.vRMode
        }

        fun setVRModeEnabled(enabled: Boolean) {
            this@CardboardView.queueEvent(object : Runnable {
                override fun run() {
                    this@StereoRendererHelper.mVRMode = enabled
                }
            })
        }

        override fun onDrawFrame(head: HeadTransform?, leftEye: Eye?, rightEye: Eye?) {
            this.mStereoRenderer.onNewFrame(head)
            GLES20.glEnable(GLES20.GL_SCISSOR_TEST)
            leftEye?.viewport?.setGLViewport()
            leftEye?.viewport?.setGLScissor()
            this.mStereoRenderer.onDrawEye(leftEye)
            if (rightEye == null) {
                return
            }
            rightEye.viewport.setGLViewport()
            rightEye.viewport.setGLScissor()
            this.mStereoRenderer.onDrawEye(rightEye)
        }

        override fun onFinishFrame(viewport: Viewport?) {
            viewport?.setGLViewport()
            viewport?.setGLScissor()
            this.mStereoRenderer.onFinishFrame(viewport)
        }

        override fun onSurfaceChanged(width: Int, height: Int) {
            if (this.mVRMode) {
                this.mStereoRenderer.onSurfaceChanged(width / 2, height)
            } else {
                this.mStereoRenderer.onSurfaceChanged(width, height)
            }
        }

        override fun onSurfaceCreated(config: EGLConfig?) {
            this.mStereoRenderer.onSurfaceCreated(config)
        }

        override fun onRendererShutdown() {
            this.mStereoRenderer.onRendererShutdown()
        }
    }

    interface StereoRenderer {
        fun onNewFrame(p0: HeadTransform?)
        fun onDrawEye(p0: Eye?)
        fun onFinishFrame(p0: Viewport?)
        fun onSurfaceChanged(p0: Int, p1: Int)
        fun onSurfaceCreated(p0: EGLConfig?)
        fun onRendererShutdown()
    }

    interface Renderer {
        fun onDrawFrame(p0: HeadTransform?, p1: Eye?, p2: Eye?)
        fun onFinishFrame(p0: Viewport?)
        fun onSurfaceChanged(p0: Int, p1: Int)
        fun onSurfaceCreated(p0: EGLConfig?)
        fun onRendererShutdown()
    }

    companion object {
        private const val TAG = "CardboardView"
    }
}
