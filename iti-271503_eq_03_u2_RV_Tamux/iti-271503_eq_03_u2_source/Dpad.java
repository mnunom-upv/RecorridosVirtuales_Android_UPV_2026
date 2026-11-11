package upv_pm.may_ago_26.iti_271503.pgu2_eq03;

import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class Dpad {
    public static final int UP = 0;
    public static final int LEFT = 1;
    public static final int RIGHT = 2;
    public static final int DOWN = 3;
    public static final int CENTER = 4;

    public int getDirectionPressed(MotionEvent event) {
        if (!isDpadDevice(event)) return -1;
        float xAxis = event.getAxisValue(MotionEvent.AXIS_HAT_X);
        float yAxis = event.getAxisValue(MotionEvent.AXIS_HAT_Y);
        if (Float.compare(xAxis, -1.0f) == 0) return LEFT;
        if (Float.compare(xAxis, 1.0f) == 0) return RIGHT;
        if (Float.compare(yAxis, -1.0f) == 0) return UP;
        if (Float.compare(yAxis, 1.0f) == 0) return DOWN;
        return -1;
    }

    public int getDirectionPressed(KeyEvent event) {
        if (!isDpadDevice(event)) return -1;
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_DPAD_LEFT: return LEFT;
            case KeyEvent.KEYCODE_DPAD_RIGHT: return RIGHT;
            case KeyEvent.KEYCODE_DPAD_UP: return UP;
            case KeyEvent.KEYCODE_DPAD_DOWN: return DOWN;
            case KeyEvent.KEYCODE_DPAD_CENTER: return CENTER;
            default: return -1;
        }
    }

    public static boolean isDpadDevice(MotionEvent event) {
        return (event.getSource() & InputDevice.SOURCE_DPAD) == InputDevice.SOURCE_DPAD
                || event.getAxisValue(MotionEvent.AXIS_HAT_X) != 0
                || event.getAxisValue(MotionEvent.AXIS_HAT_Y) != 0;
    }

    public static boolean isDpadDevice(KeyEvent event) {
        return (event.getSource() & InputDevice.SOURCE_DPAD) == InputDevice.SOURCE_DPAD;
    }
}

