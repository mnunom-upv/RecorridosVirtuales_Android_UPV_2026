package upv.pm.may_ago_26.iti_271503.pgu2_eq04
import upv.pm.may_ago_26.iti_271503.pgu2_eq04.R

import android.opengl.GLES20
import android.opengl.Matrix
import java.nio.FloatBuffer
import java.nio.ShortBuffer

/*import com.cardbookvr.renderbox.R;
import com.cardbookvr.renderbox.RenderBox;
import com.cardbookvr.renderbox.Transform;
import com.cardbookvr.renderbox.materials.Material;
import com.cardbookvr.renderbox.math.Vector3;
import com.google.vrtoolkit.cardboard.Eye;*/ /**
 * Created by Schoen and Jonathan on 4/16/2016.
 */
class Camera : Component() {
    private val camera = FloatArray(16)
    private val view = FloatArray(16)

    var headTracking: Boolean = true
    var trailsMode: Boolean = false

    init {
        //The camera breaks pattern and creates its own Transform
        this.transform = Transform()
        setupProgram()
        allocateBuffers()
    }

    fun onNewFrame() {
        // Build the camera matrix and apply it to the ModelView.
        val position = transform!!.position
        Matrix.setLookAtM(
            camera, 0, position.x, position.y,
            position.z + Z_NEAR, position.x, position.y, position.z,
            0.0f, 1.0f, 0.0f
        )

        RenderBox.checkGLError("onNewFrame")
    }

    fun onDrawEye(eye: Eye) {
        if (trailsMode) {
            GLES20.glEnable(GLES20.GL_BLEND)
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
            customClear(customClearColor)
            GLES20.glEnable(GLES20.GL_DEPTH_TEST)
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT)
        } else {
            GLES20.glEnable(GLES20.GL_DEPTH_TEST)
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        }

        RenderBox.checkGLError("glClear")

        if (headTracking) {
            // Apply the eye transformation to the camera.
            Matrix.multiplyMM(view, 0, eye.eyeView, 0, camera, 0)
        } else {
            // copy camera into view
            for (i in camera.indices) {
                view[i] = camera[i]
            }
        }

        // Compute lighting position
        RenderBox.instance.mainLight?.onDraw(view)

        // Build the ModelView and ModelViewProjection matrices
        val perspective = eye.getPerspective(Z_NEAR, Z_FAR)

        for (obj in RenderBox.instance.renderObjects) {
            obj?.draw(view, perspective)
        }
        RenderBox.checkGLError("Drawing complete")
    }

    companion object {
        private const val TAG = "renderbox.Camera"

        private const val Z_NEAR = .1f
        const val Z_FAR: Float = 1000f

        // for custom clear (fullscreen quad and buffers)
        var program: Int = -1
        var positionParam: Int = 0
        var colorParam: Int = 0
        var setup: Boolean = false
        var vertexBuffer: FloatBuffer? = null
        var indexBuffer: ShortBuffer? = null
        const val numIndices: Int = 6

        val COORDS: FloatArray = floatArrayOf(
            -1.0f, 1.0f, 0.0f,
            1.0f, 1.0f, 0.0f,
            -1.0f, -1.0f, 0.0f,
            1.0f, -1.0f, 0.0f
        )
        val INDICES: ShortArray = shortArrayOf(
            0, 1, 2,
            1, 3, 2
        )
        var customClearColor: FloatArray = floatArrayOf(0f, 0f, 0f, 0.05f)


        fun setupProgram() {
            if (program > -1)  //This means program has been set up
            //(valid program or error)
                return
            //Create shader program
            program = Material.createProgram(
                R.raw.fullscreen_solid_color_vertex,
                R.raw.fullscreen_solid_color_fragment
            )

            //Get vertex attribute parameters
            positionParam = GLES20.glGetAttribLocation(program, "v_Position")

            //Enable vertex attribute parameters
            GLES20.glEnableVertexAttribArray(positionParam)

            //Shader-specific parameters
            colorParam = GLES20.glGetUniformLocation(program, "u_Color")

            RenderBox.checkGLError("Fullscreen Solid Color params")
        }

        fun allocateBuffers() {
            setup = true
            vertexBuffer = RenderObject.allocateFloatBuffer(COORDS)
            indexBuffer = RenderObject.allocateShortBuffer(INDICES)
        }

        fun customClear(clearColor: FloatArray?) {
            GLES20.glUseProgram(program)
            // Set the position buffer
            GLES20.glVertexAttribPointer(positionParam, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)
            GLES20.glUniform4fv(colorParam, 1, clearColor, 0)
            GLES20.glDrawElements(
                GLES20.GL_TRIANGLES,
                numIndices,
                GLES20.GL_UNSIGNED_SHORT,
                indexBuffer
            )
        }
    }
}
