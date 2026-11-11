package upv.pm.may_ago_26.iti_271503.pgu2_eq06

import android.view.InputDevice
import android.view.InputEvent
import android.view.KeyEvent
import android.view.MotionEvent

class Dpad {
    companion object {
        const val UP = 0
        const val LEFT = 1
        const val RIGHT = 2
        const val DOWN = 3
        const val CENTER = 4

        fun isDpadDevice(event: InputEvent): Boolean {
            return (event.source and InputDevice.SOURCE_DPAD) == InputDevice.SOURCE_DPAD
        }
    }

    var directionPressed: Int = -1

    fun getDirectionPressed(event: InputEvent): Int {
        directionPressed = -1
        if (!isDpadDevice(event)) return -1

        if (event is MotionEvent) {
            val xaxis = event.getAxisValue(MotionEvent.AXIS_HAT_X)
            val yaxis = event.getAxisValue(MotionEvent.AXIS_HAT_Y)
            directionPressed = when {
                xaxis.compareTo(-1.0f) == 0 -> LEFT
                xaxis.compareTo(1.0f) == 0 -> RIGHT
                yaxis.compareTo(-1.0f) == 0 -> UP
                yaxis.compareTo(1.0f) == 0 -> DOWN
                else -> directionPressed
            }
        } else if (event is KeyEvent) {
            directionPressed = when (event.keyCode) {
                KeyEvent.KEYCODE_DPAD_LEFT -> LEFT
                KeyEvent.KEYCODE_DPAD_RIGHT -> RIGHT
                KeyEvent.KEYCODE_DPAD_UP -> UP
                KeyEvent.KEYCODE_DPAD_DOWN -> DOWN
                KeyEvent.KEYCODE_DPAD_CENTER -> CENTER
                else -> directionPressed
            }
        }
        return directionPressed
    }
}
