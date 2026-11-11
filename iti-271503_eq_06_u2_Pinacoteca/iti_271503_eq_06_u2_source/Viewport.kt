package upv.pm.may_ago_26.iti_271503.pgu2_eq06

import android.opengl.GLES20

class Viewport {
    var x: Int = 0
    var y: Int = 0
    var width: Int = 0
    var height: Int = 0

    fun setViewport(x: Int, y: Int, width: Int, height: Int) {
        this.x = x
        this.y = y
        this.width = width
        this.height = height
    }

    fun setGLViewport() {
        GLES20.glViewport(this.x, this.y, this.width, this.height)
    }

    fun setGLScissor() {
        GLES20.glScissor(this.x, this.y, this.width, this.height)
    }

    fun getAsArray(array: IntArray, offset: Int) {
        require(offset + 4 <= array.size) { "Not enough space to write the result" }
        array[offset] = this.x
        array[offset + 1] = this.y
        array[offset + 2] = this.width
        array[offset + 3] = this.height
    }

    override fun toString(): String {
        return "{\n" + StringBuilder(18).append("  x: ").append(this.x).append(",\n")
            .toString() + StringBuilder(18).append("  y: ").append(this.y).append(",\n")
            .toString() + StringBuilder(22).append("  width: ").append(this.width).append(",\n")
            .toString() + StringBuilder(23).append("  height: ").append(this.height).append(",\n")
            .toString() + "}"
    }

    override fun equals(obj: Any?): Boolean {
        if (obj === this) {
            return true
        }
        if (obj !is Viewport) {
            return false
        }
        val other = obj
        return this.x == other.x && this.y == other.y && this.width == other.width && this.height == other.height
    }

    override fun hashCode(): Int {
        return this.x.hashCode() xor this.y.hashCode() xor this.width.hashCode() xor this.height.hashCode()
    }
}
