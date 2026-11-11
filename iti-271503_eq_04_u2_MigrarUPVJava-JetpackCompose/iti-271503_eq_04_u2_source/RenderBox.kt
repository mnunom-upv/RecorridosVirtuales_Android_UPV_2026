package upv.pm.may_ago_26.iti_271503.pgu2_eq04

import android.app.Activity
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLU
import android.opengl.GLUtils
import android.util.Log
import upv.pm.may_ago_26.iti_271503.pgu2_eq04.CardboardView.StereoRenderer
import javax.microedition.khronos.egl.EGLConfig

class RenderBox(
    var mainActivity: Activity,
    var callbacks: IRenderBox
) : StereoRenderer {

    var renderObjects: MutableList<RenderObject?> = ArrayList()

    var mainLight: Light? = null

    init {
        instance = this
        Time.start()
    }

    override fun onNewFrame(headTransform: HeadTransform?) {
        Time.update()

        headTransform?.getHeadView(headView, 0)
        headTransform?.getEulerAngles(headAngles, 0)

        mainCamera?.onNewFrame()

        callbacks.preDraw()
    }

    override fun onDrawEye(eye: Eye?) {
        if (eye == null) {
            return
        }

        mainCamera?.onDrawEye(eye)
    }

    override fun onFinishFrame(viewport: Viewport?) {
        callbacks.postDraw()
    }

    override fun onSurfaceChanged(width: Int, height: Int) {
        // No se requiere lógica aquí por ahora.
    }

    override fun onSurfaceCreated(eglConfig: EGLConfig?) {
        reset()

        GLES20.glClearColor(
            0.1f,
            0.1f,
            0.1f,
            0.5f
        )

        val light = Light()
        mainLight = light

        Transform().addComponent(light)

        mainCamera = Camera()

        checkGLError("onSurfaceCreated")

        callbacks.setup()
    }

    override fun onRendererShutdown() {
        // No se requiere lógica aquí por ahora.
    }

    companion object {

        private const val TAG = "RenderBox"

        lateinit var instance: RenderBox

        var mainCamera: Camera? = null

        val headView: FloatArray = FloatArray(16)
        val headAngles: FloatArray = FloatArray(3)

        fun reset() {
            VertexColorMaterial.destroy()
            VertexColorLightingMaterial.destroy()
        }

        fun checkGLError(label: String) {
            var error = GLES20.glGetError()

            while (error != GLES20.GL_NO_ERROR) {
                val errorText = String.format(
                    "%s: glError %d, %s",
                    label,
                    error,
                    GLU.gluErrorString(error)
                )

                Log.e(TAG, errorText)

                throw RuntimeException(errorText)
            }
        }

        fun loadTexture(resourceId: Int): Int {
            val textureHandle = IntArray(1)

            GLES20.glGenTextures(
                1,
                textureHandle,
                0
            )

            if (textureHandle[0] != 0) {
                val options = BitmapFactory.Options()
                options.inScaled = false

                val bitmap = BitmapFactory.decodeResource(
                    instance.mainActivity.resources,
                    resourceId,
                    options
                )

                GLES20.glBindTexture(
                    GLES20.GL_TEXTURE_2D,
                    textureHandle[0]
                )

                GLES20.glTexParameteri(
                    GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MIN_FILTER,
                    GLES20.GL_NEAREST
                )

                GLES20.glTexParameteri(
                    GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MAG_FILTER,
                    GLES20.GL_NEAREST
                )

                GLUtils.texImage2D(
                    GLES20.GL_TEXTURE_2D,
                    0,
                    bitmap,
                    0
                )

                bitmap.recycle()
            }

            if (textureHandle[0] == 0) {
                throw RuntimeException("Error loading texture.")
            }

            return textureHandle[0]
        }
    }
}