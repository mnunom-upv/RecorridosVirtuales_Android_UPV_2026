package upv.pm.may_ago_26.iti_271503.pgu2_eq04

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.opengl.GLES20
import android.opengl.Matrix
import android.util.Log
import android.view.MotionEvent
import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.concurrent.Volatile
import kotlin.math.cos
import kotlin.math.sin

internal class UiLayer(private val mContext: Context) {

    private val mTouchWidthPx: Int

    @Volatile
    private var mTouchRect: Rect

    private var mDownWithinBounds = false
    private val mGlStateBackup: GLStateBackup
    private val mShader: ShaderProgram
    private val mSettingsButtonRenderer: SettingsButtonRenderer?
    private val mAlignmentMarkerRenderer: AlignmentMarkerRenderer
    private val mViewport: Viewport

    private var mShouldUpdateViewport = true
    private var mSettingsButtonEnabled = true
    private var mAlignmentMarkerEnabled = true
    private var initialized = false

    fun updateViewport(viewport: Viewport) {
        synchronized(this) {
            if (mViewport == viewport) {
                return
            }

            val w = viewport.width
            val h = viewport.height

            mTouchRect = Rect(
                (w - mTouchWidthPx) / 2,
                h - mTouchWidthPx,
                (w + mTouchWidthPx) / 2,
                h
            )

            mViewport.setViewport(
                viewport.x,
                viewport.y,
                viewport.width,
                viewport.height
            )

            mShouldUpdateViewport = true
        }
    }

    fun initializeGl() {
        try {
            clearGlErrors()

            mShader.initializeGl()

            mGlStateBackup.clearTrackedVertexAttributes()
            mGlStateBackup.addTrackedVertexAttribute(mShader.aPosition)
            mGlStateBackup.readFromGL()

            mSettingsButtonRenderer?.initializeGl()
            mAlignmentMarkerRenderer.initializeGl()

            mGlStateBackup.writeToGL()

            initialized = true
        } catch (e: Exception) {
            Log.e(TAG, "UiLayer disabled because OpenGL UI shader failed.", e)

            initialized = false
            mSettingsButtonEnabled = false
            mAlignmentMarkerEnabled = false
        }
    }

    fun draw() {
        if (!settingsButtonEnabled && !alignmentMarkerEnabled) {
            return
        }

        if (!initialized) {
            initializeGl()

            if (!initialized) {
                return
            }
        }

        mGlStateBackup.readFromGL()

        synchronized(this) {
            if (mShouldUpdateViewport) {
                mShouldUpdateViewport = false
                mSettingsButtonRenderer?.updateViewport(mViewport)
                mAlignmentMarkerRenderer.updateViewport(mViewport)
            }

            mViewport.setGLViewport()
        }

        if (settingsButtonEnabled) {
            mSettingsButtonRenderer?.draw()
        }

        if (alignmentMarkerEnabled) {
            mAlignmentMarkerRenderer.draw()
        }

        mGlStateBackup.writeToGL()
    }

    @get:Synchronized
    @set:Synchronized
    var alignmentMarkerEnabled: Boolean
        get() = mAlignmentMarkerEnabled
        set(enabled) {
            if (mAlignmentMarkerEnabled != enabled) {
                mAlignmentMarkerEnabled = enabled
                mShouldUpdateViewport = true
            }
        }

    @get:Synchronized
    @set:Synchronized
    var settingsButtonEnabled: Boolean
        get() = mSettingsButtonEnabled
        set(enabled) {
            if (mSettingsButtonEnabled != enabled) {
                mSettingsButtonEnabled = enabled
                mShouldUpdateViewport = true
            }
        }

    fun onTouchEvent(e: MotionEvent): Boolean {
        var touchWithinBounds: Boolean

        synchronized(this) {
            if (!mSettingsButtonEnabled) {
                return false
            }

            touchWithinBounds = mTouchRect.contains(
                e.x.toInt(),
                e.y.toInt()
            )
        }

        if (e.actionMasked == MotionEvent.ACTION_DOWN && touchWithinBounds) {
            mDownWithinBounds = true
        }

        if (!mDownWithinBounds) {
            return false
        }

        if (e.actionMasked == MotionEvent.ACTION_UP) {
            if (touchWithinBounds) {
                UiUtils.launchOrInstallCardboard(mContext)
            }

            mDownWithinBounds = false
        } else if (e.actionMasked == MotionEvent.ACTION_CANCEL) {
            mDownWithinBounds = false
        }

        setPressed(mDownWithinBounds && touchWithinBounds)

        return true
    }

    private fun setPressed(pressed: Boolean) {
        mSettingsButtonRenderer?.setColor(
            if (pressed) PRESSED_COLOR else NORMAL_COLOR
        )
    }

    init {
        mTouchRect = Rect()

        val density = mContext.resources.displayMetrics.density
        val buttonWidthPx = (BUTTON_WIDTH_DP.toFloat() * density).toInt()

        mTouchWidthPx = (buttonWidthPx * TOUCH_SLOP_FACTOR).toInt()
        mGlStateBackup = GLStateBackup()
        mShader = ShaderProgram()
        mSettingsButtonRenderer = SettingsButtonRenderer(mShader, buttonWidthPx)
        mAlignmentMarkerRenderer = AlignmentMarkerRenderer(
            mShader,
            mTouchWidthPx.toFloat(),
            CENTER_LINE_THICKNESS_DP * density
        )
        mViewport = Viewport()
    }

    private class ShaderProgram {

        var program: Int = 0
        var aPosition: Int = 0
        var uMvpMatrix: Int = 0
        var uColor: Int = 0

        fun initializeGl() {
            program = createProgram(VERTEX_SHADER, FRAGMENT_SHADER)

            if (program == 0) {
                throw RuntimeException("Could not create UiLayer shader program.")
            }

            aPosition = GLES20.glGetAttribLocation(program, "aPosition")

            if (aPosition == -1) {
                throw RuntimeException("Could not get attrib location for aPosition.")
            }

            uMvpMatrix = GLES20.glGetUniformLocation(program, "uMVPMatrix")

            if (uMvpMatrix == -1) {
                throw RuntimeException("Could not get uniform location for uMVPMatrix.")
            }

            uColor = GLES20.glGetUniformLocation(program, "uColor")

            if (uColor == -1) {
                throw RuntimeException("Could not get uniform location for uColor.")
            }
        }

        fun loadShader(shaderType: Int, source: String?): Int {
            if (source.isNullOrBlank()) {
                Log.e(TAG, "Shader source is null or blank.")
                return 0
            }

            val shader = GLES20.glCreateShader(shaderType)

            if (shader == 0) {
                Log.e(TAG, "Could not create shader type $shaderType.")
                return 0
            }

            GLES20.glShaderSource(shader, source)
            GLES20.glCompileShader(shader)

            val compiled = intArrayOf(0)
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0)

            if (compiled[0] == 0) {
                Log.e(TAG, "Could not compile shader $shaderType:")
                Log.e(TAG, GLES20.glGetShaderInfoLog(shader))

                GLES20.glDeleteShader(shader)

                return 0
            }

            return shader
        }

        fun createProgram(vertexSource: String?, fragmentSource: String?): Int {
            clearGlErrors()

            val vertexShader = loadShader(
                GLES20.GL_VERTEX_SHADER,
                vertexSource
            )

            if (vertexShader == 0) {
                Log.e(TAG, "Vertex shader could not be created.")
                return 0
            }

            val pixelShader = loadShader(
                GLES20.GL_FRAGMENT_SHADER,
                fragmentSource
            )

            if (pixelShader == 0) {
                Log.e(TAG, "Fragment shader could not be created.")
                GLES20.glDeleteShader(vertexShader)
                return 0
            }

            val program = GLES20.glCreateProgram()

            if (program == 0) {
                Log.e(TAG, "Could not create OpenGL program.")
                GLES20.glDeleteShader(vertexShader)
                GLES20.glDeleteShader(pixelShader)
                return 0
            }

            GLES20.glAttachShader(program, vertexShader)
            GLES20.glAttachShader(program, pixelShader)
            GLES20.glLinkProgram(program)

            val linkStatus = intArrayOf(0)
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0)

            if (linkStatus[0] != GLES20.GL_TRUE) {
                Log.e(TAG, "Could not link program:")
                Log.e(TAG, GLES20.glGetProgramInfoLog(program))

                GLES20.glDeleteProgram(program)
                GLES20.glDeleteShader(vertexShader)
                GLES20.glDeleteShader(pixelShader)

                return 0
            }

            GLES20.glDeleteShader(vertexShader)
            GLES20.glDeleteShader(pixelShader)

            clearGlErrors()

            return program
        }

        companion object {
            private const val VERTEX_SHADER =
                "uniform mat4 uMVPMatrix;\n" +
                        "attribute vec2 aPosition;\n" +
                        "void main() {\n" +
                        "    gl_Position = uMVPMatrix * vec4(aPosition, 0.0, 1.0);\n" +
                        "}\n"

            private const val FRAGMENT_SHADER =
                "precision mediump float;\n" +
                        "uniform vec4 uColor;\n" +
                        "void main() {\n" +
                        "    gl_FragColor = uColor;\n" +
                        "}\n"
        }
    }

    private open class MeshRenderer(
        protected var mShader: ShaderProgram
    ) {

        protected var mArrayBufferId: Int = -1
        protected var mElementBufferId: Int = -1
        protected var mMvp: FloatArray = FloatArray(16)

        private var mNumIndices = 0

        fun genAndBindBuffers(vertexData: FloatArray, indexData: ShortArray) {
            val vertexBuffer = ByteBuffer
                .allocateDirect(vertexData.size * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()

            vertexBuffer.put(vertexData).position(0)

            mNumIndices = indexData.size

            val indexBuffer = ByteBuffer
                .allocateDirect(indexData.size * BYTES_PER_SHORT)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer()

            indexBuffer.put(indexData).position(0)

            val bufferIds = IntArray(2)

            GLES20.glGenBuffers(2, bufferIds, 0)

            mArrayBufferId = bufferIds[0]
            mElementBufferId = bufferIds[1]

            GLES20.glBindBuffer(
                GLES20.GL_ARRAY_BUFFER,
                mArrayBufferId
            )

            GLES20.glBufferData(
                GLES20.GL_ARRAY_BUFFER,
                vertexData.size * BYTES_PER_FLOAT,
                vertexBuffer as Buffer,
                GLES20.GL_STATIC_DRAW
            )

            GLES20.glBindBuffer(
                GLES20.GL_ELEMENT_ARRAY_BUFFER,
                mElementBufferId
            )

            GLES20.glBufferData(
                GLES20.GL_ELEMENT_ARRAY_BUFFER,
                indexData.size * BYTES_PER_SHORT,
                indexBuffer as Buffer,
                GLES20.GL_STATIC_DRAW
            )

            checkGlError("genAndBindBuffers")
        }

        open fun updateViewport(viewport: Viewport?) {
            Matrix.setIdentityM(mMvp, 0)
        }

        open fun draw() {
            if (mShader.program == 0) {
                return
            }

            GLES20.glDisable(GLES20.GL_DEPTH_TEST)
            GLES20.glDisable(GLES20.GL_CULL_FACE)

            GLES20.glUseProgram(mShader.program)

            GLES20.glUniformMatrix4fv(
                mShader.uMvpMatrix,
                1,
                false,
                mMvp,
                0
            )

            GLES20.glBindBuffer(
                GLES20.GL_ARRAY_BUFFER,
                mArrayBufferId
            )

            GLES20.glVertexAttribPointer(
                mShader.aPosition,
                COMPONENTS_PER_VERT,
                GLES20.GL_FLOAT,
                false,
                DATA_STRIDE_BYTES,
                DATA_POS_OFFSET
            )

            GLES20.glEnableVertexAttribArray(mShader.aPosition)

            GLES20.glBindBuffer(
                GLES20.GL_ELEMENT_ARRAY_BUFFER,
                mElementBufferId
            )

            GLES20.glDrawElements(
                GLES20.GL_TRIANGLE_STRIP,
                mNumIndices,
                GLES20.GL_UNSIGNED_SHORT,
                0
            )
        }

        companion object {
            private const val BYTES_PER_FLOAT = 4
            private const val BYTES_PER_SHORT = 2
            protected const val COMPONENTS_PER_VERT: Int = 2
            private const val DATA_STRIDE_BYTES = 8
            private const val DATA_POS_OFFSET = 0
        }
    }

    private class AlignmentMarkerRenderer(
        shader: ShaderProgram,
        private val mVerticalBorderPaddingPx: Float,
        private val mLineThicknessPx: Float
    ) : MeshRenderer(shader) {

        fun initializeGl() {
            val vertexData = floatArrayOf(
                1.0f, 1.0f,
                -1.0f, 1.0f,
                1.0f, -1.0f,
                -1.0f, -1.0f
            )

            val indexData = ShortArray(vertexData.size / 2)

            for (i in indexData.indices) {
                indexData[i] = i.toShort()
            }

            genAndBindBuffers(vertexData, indexData)
        }

        override fun updateViewport(viewport: Viewport?) {
            if (viewport == null || viewport.width == 0 || viewport.height == 0) {
                return
            }

            Matrix.setIdentityM(mMvp, 0)

            val xScale = mLineThicknessPx / viewport.width
            val yScale = 1.0f - 2.0f * mVerticalBorderPaddingPx / viewport.height

            Matrix.scaleM(mMvp, 0, xScale, yScale, 1.0f)
        }

        override fun draw() {
            if (mShader.program == 0) {
                return
            }

            GLES20.glUseProgram(mShader.program)

            GLES20.glUniform4f(
                mShader.uColor,
                Color.red(COLOR) / 255.0f,
                Color.green(COLOR) / 255.0f,
                Color.blue(COLOR) / 255.0f,
                Color.alpha(COLOR) / 255.0f
            )

            super.draw()
        }

        companion object {
            private val COLOR: Int = Color.argb(255, 50, 50, 50)
        }
    }

    private class SettingsButtonRenderer(
        shader: ShaderProgram,
        private val mButtonWidthPx: Int
    ) : MeshRenderer(shader) {

        private var mColor: Int = NORMAL_COLOR

        fun initializeGl() {
            val vertexData = FloatArray(120)
            val numVerticesPerRim = 30
            val lerpInterval = 8.0f

            for (i in 0 until numVerticesPerRim) {
                val theta = i.toFloat() / numVerticesPerRim.toFloat() * 360.0f
                val mod = theta % 60.0f

                val r = when {
                    mod <= 12.0f -> 1.0f
                    mod <= 20.0f -> lerp(
                        1.0f,
                        0.75f,
                        (mod - 12.0f) / lerpInterval
                    )

                    mod <= 40.0f -> 0.75f
                    mod <= 48.0f -> lerp(
                        0.75f,
                        1.0f,
                        (mod - 60.0f + 20.0f) / lerpInterval
                    )

                    else -> 1.0f
                }

                vertexData[2 * i] =
                    r * cos(Math.toRadians((90.0f - theta).toDouble())).toFloat()

                vertexData[2 * i + 1] =
                    r * sin(Math.toRadians((90.0f - theta).toDouble())).toFloat()
            }

            val innerStartingIndex = 2 * numVerticesPerRim

            for (j in 0 until numVerticesPerRim) {
                val theta2 = j.toFloat() / numVerticesPerRim.toFloat() * 360.0f

                vertexData[innerStartingIndex + 2 * j] =
                    0.3125f * cos(Math.toRadians((90.0f - theta2).toDouble())).toFloat()

                vertexData[innerStartingIndex + 2 * j + 1] =
                    0.3125f * sin(Math.toRadians((90.0f - theta2).toDouble())).toFloat()
            }

            val indexData = ShortArray(62)

            for (k in 0 until numVerticesPerRim) {
                indexData[2 * k] = k.toShort()
                indexData[2 * k + 1] = (numVerticesPerRim + k).toShort()
            }

            indexData[indexData.size - 2] = 0
            indexData[indexData.size - 1] = numVerticesPerRim.toShort()

            genAndBindBuffers(vertexData, indexData)
        }

        @Synchronized
        fun setColor(color: Int) {
            mColor = color
        }

        override fun updateViewport(viewport: Viewport?) {
            if (viewport == null || viewport.width == 0 || viewport.height == 0) {
                return
            }

            Matrix.setIdentityM(mMvp, 0)

            val yScale = mButtonWidthPx.toFloat() / viewport.height.toFloat()
            val xScale = yScale * viewport.height.toFloat() / viewport.width.toFloat()

            Matrix.translateM(
                mMvp,
                0,
                0.0f,
                yScale - 1.0f,
                0.0f
            )

            Matrix.scaleM(
                mMvp,
                0,
                xScale,
                yScale,
                1.0f
            )
        }

        override fun draw() {
            if (mShader.program == 0) {
                return
            }

            GLES20.glUseProgram(mShader.program)

            synchronized(this) {
                GLES20.glUniform4f(
                    mShader.uColor,
                    Color.red(mColor) / 255.0f,
                    Color.green(mColor) / 255.0f,
                    Color.blue(mColor) / 255.0f,
                    Color.alpha(mColor) / 255.0f
                )
            }

            super.draw()
        }

        companion object {
            private const val DEGREES_PER_GEAR_SECTION = 60
            private const val OUTER_RIM_END_DEG = 12
            private const val INNER_RIM_BEGIN_DEG = 20
            private const val OUTER_RADIUS = 1.0f
            private const val MIDDLE_RADIUS = 0.75f
            private const val INNER_RADIUS = 0.3125f
            private const val NUM_VERTICES = 60
        }
    }

    companion object {

        private val TAG: String = UiLayer::class.java.simpleName

        private const val NORMAL_COLOR = -3355444
        private const val PRESSED_COLOR = -12303292
        private const val CENTER_LINE_THICKNESS_DP = 4.0f
        private const val BUTTON_WIDTH_DP = 28
        private const val TOUCH_SLOP_FACTOR = 1.5f

        private fun checkGlError(op: String?) {
            val error = GLES20.glGetError()

            if (error != GLES20.GL_NO_ERROR) {
                Log.e(
                    TAG,
                    "${op ?: "OpenGL"}: glError $error"
                )
            }
        }

        private fun clearGlErrors() {
            while (GLES20.glGetError() != GLES20.GL_NO_ERROR) {
                // Limpia errores anteriores de OpenGL.
            }
        }

        private fun lerp(a: Float, b: Float, t: Float): Float {
            return a * (1.0f - t) + b * t
        }
    }
}