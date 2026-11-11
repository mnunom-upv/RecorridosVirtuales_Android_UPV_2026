package upv.pm.may_ago_26.iti_271503.pgu2_eq04

import android.opengl.GLES20
import java.nio.FloatBuffer
import java.nio.IntBuffer

internal class GLStateBackup {
    private var mCullFaceEnabled = false
    private var mScissorTestEnabled = false
    private var mDepthTestEnabled = false
    private val mViewport: IntBuffer
    private val mTexture2dId: IntBuffer
    private val mTextureUnit: IntBuffer
    private val mScissorBox: IntBuffer
    private val mShaderProgram: IntBuffer
    private val mArrayBufferBinding: IntBuffer
    private val mElementArrayBufferBinding: IntBuffer
    private val mClearColor: FloatBuffer
    private val mVertexAttributes: ArrayList<VertexAttributeState>

    init {
        this.mViewport = IntBuffer.allocate(4)
        this.mTexture2dId = IntBuffer.allocate(1)
        this.mTextureUnit = IntBuffer.allocate(1)
        this.mScissorBox = IntBuffer.allocate(4)
        this.mShaderProgram = IntBuffer.allocate(1)
        this.mArrayBufferBinding = IntBuffer.allocate(1)
        this.mElementArrayBufferBinding = IntBuffer.allocate(1)
        this.mClearColor = FloatBuffer.allocate(4)
        this.mVertexAttributes = ArrayList<VertexAttributeState>()
    }

    fun addTrackedVertexAttribute(attributeId: Int) {
        this.mVertexAttributes.add(VertexAttributeState(attributeId))
    }

    fun clearTrackedVertexAttributes() {
        this.mVertexAttributes.clear()
    }

    fun readFromGL() {
        GLES20.glGetIntegerv(GLES20.GL_VIEWPORT, this.mViewport)
        this.mCullFaceEnabled = GLES20.glIsEnabled(GLES20.GL_CULL_FACE)
        this.mScissorTestEnabled = GLES20.glIsEnabled(GLES20.GL_SCISSOR_TEST)
        this.mDepthTestEnabled = GLES20.glIsEnabled(GLES20.GL_DEPTH_TEST)
        GLES20.glGetFloatv(GLES20.GL_COLOR_CLEAR_VALUE, this.mClearColor)
        GLES20.glGetIntegerv(GLES20.GL_CURRENT_PROGRAM, this.mShaderProgram)
        GLES20.glGetIntegerv(GLES20.GL_SCISSOR_BOX, this.mScissorBox)
        GLES20.glGetIntegerv(GLES20.GL_ACTIVE_TEXTURE, this.mTextureUnit)
        GLES20.glGetIntegerv(GLES20.GL_TEXTURE_BINDING_2D, this.mTexture2dId)
        GLES20.glGetIntegerv(GLES20.GL_ARRAY_BUFFER_BINDING, this.mArrayBufferBinding)
        GLES20.glGetIntegerv(
            GLES20.GL_ELEMENT_ARRAY_BUFFER_BINDING,
            this.mElementArrayBufferBinding
        )
        for (vas in this.mVertexAttributes) {
            vas.readFromGL()
        }
    }

    fun writeToGL() {
        for (vas in this.mVertexAttributes) {
            vas.writeToGL()
        }
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, this.mArrayBufferBinding.array()[0])
        GLES20.glBindBuffer(
            GLES20.GL_ELEMENT_ARRAY_BUFFER,
            this.mElementArrayBufferBinding.array()[0]
        )
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, this.mTexture2dId.array()[0])
        GLES20.glActiveTexture(this.mTextureUnit.array()[0])
        GLES20.glScissor(
            this.mScissorBox.array()[0],
            this.mScissorBox.array()[1],
            this.mScissorBox.array()[2],
            this.mScissorBox.array()[3]
        )
        GLES20.glUseProgram(this.mShaderProgram.array()[0])
        GLES20.glClearColor(
            this.mClearColor.array()[0],
            this.mClearColor.array()[1],
            this.mClearColor.array()[2],
            this.mClearColor.array()[3]
        )
        if (this.mCullFaceEnabled) {
            GLES20.glEnable(GLES20.GL_CULL_FACE)
        } else {
            GLES20.glDisable(GLES20.GL_CULL_FACE)
        }
        if (this.mScissorTestEnabled) {
            GLES20.glEnable(GLES20.GL_SCISSOR_TEST)
        } else {
            GLES20.glDisable(GLES20.GL_SCISSOR_TEST)
        }
        if (this.mDepthTestEnabled) {
            GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        } else {
            GLES20.glDisable(GLES20.GL_DEPTH_TEST)
        }
        GLES20.glViewport(
            this.mViewport.array()[0],
            this.mViewport.array()[1],
            this.mViewport.array()[2],
            this.mViewport.array()[3]
        )
    }

    private inner class VertexAttributeState
        (private val mAttributeId: Int) {
        private val mEnabled: IntBuffer

        init {
            this.mEnabled = IntBuffer.allocate(1)
        }

        fun readFromGL() {
            GLES20.glGetVertexAttribiv(
                this.mAttributeId,
                GLES20.GL_VERTEX_ATTRIB_ARRAY_ENABLED,
                this.mEnabled
            )
        }

        fun writeToGL() {
            if (this.mEnabled.array()[0] == 0) {
                GLES20.glDisableVertexAttribArray(this.mAttributeId)
            } else {
                GLES20.glEnableVertexAttribArray(this.mAttributeId)
            }
        }
    }
}
