package upv.pm.may_ago_26.iti_271503.pgu2_eq04

import android.view.InputDevice
import android.view.InputEvent
import android.view.KeyEvent
import android.view.MotionEvent
import java.lang.Float
import kotlin.Boolean
import kotlin.Int

class Dpad {
    var directionPressed: Int = -1 // initialized to -1

    fun getDirectionPressed(event: InputEvent): Int {
        directionPressed = -1
        if (!isDpadDevice(event)) {
            return -1
        }

        // If the input event is a MotionEvent, check its hat axis values.
        if (event is MotionEvent) {
            // Use the hat axis value to find the D-pad direction

            val motionEvent = event
            val xaxis = motionEvent.getAxisValue(MotionEvent.AXIS_HAT_X)
            val yaxis = motionEvent.getAxisValue(MotionEvent.AXIS_HAT_Y)

            // Check if the AXIS_HAT_X value is -1 or 1, and set the D-pad
            // LEFT and RIGHT direction accordingly.
            if (Float.compare(xaxis, -1.0f) == 0) {
                directionPressed = LEFT
            } else if (Float.compare(xaxis, 1.0f) == 0) {
                directionPressed = RIGHT
            } else if (Float.compare(yaxis, -1.0f) == 0) {
                directionPressed = UP
            } else if (Float.compare(yaxis, 1.0f) == 0) {
                directionPressed = DOWN
            }
        } else if (event is KeyEvent) {
            // Use the key code to find the D-pad direction.

            val keyEvent = event
            if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                directionPressed = LEFT
            } else if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                directionPressed = RIGHT
            } else if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                directionPressed = UP
            } else if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                directionPressed = DOWN
            } else if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {
                directionPressed = CENTER
            }
        }
        return directionPressed
    }

    companion object {
        const val UP: Int = 0
        const val LEFT: Int = 1
        const val RIGHT: Int = 2
        const val DOWN: Int = 3
        const val CENTER: Int = 4

        fun isDpadDevice(event: InputEvent): Boolean {
            // Check that input comes from a device with directional pads.
            if ((event.getSource() and InputDevice.SOURCE_DPAD)
                != InputDevice.SOURCE_DPAD
            ) {
                return true
            } else {
                return false
            }
        }
    }
}
