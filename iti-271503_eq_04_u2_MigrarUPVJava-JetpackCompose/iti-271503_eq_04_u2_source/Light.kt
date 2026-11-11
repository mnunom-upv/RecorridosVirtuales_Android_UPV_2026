package upv.pm.may_ago_26.iti_271503.pgu2_eq04

import android.opengl.Matrix

class Light : Component() {

    val lightPosInEyeSpace: FloatArray = FloatArray(4)

    var color: FloatArray = floatArrayOf(
        1f,
        1f,
        1f,
        1f
    )

    fun onDraw(view: FloatArray?) {
        if (view == null || transform == null) {
            return
        }

        Matrix.multiplyMV(
            lightPosInEyeSpace,
            0,
            view,
            0,
            transform!!.position.toFloat4(),
            0
        )
    }

    companion object {
        private const val TAG = "RenderBox.Light"
    }
}