package com.rama.fikret.game;

import android.view.InputDevice;
import android.view.MotionEvent;

class GamepadAxes {
    private static final float THRESHOLD = 0.5f;

    private GamepadAxes() {
    }

    static boolean isJoystickMove(MotionEvent event) {
        return event.getAction() == MotionEvent.ACTION_MOVE
                && (event.getSource() & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK;
    }

    static int digitalX(MotionEvent event) {
        return digital(event.getAxisValue(MotionEvent.AXIS_X),
                event.getAxisValue(MotionEvent.AXIS_HAT_X));
    }

    static int digitalY(MotionEvent event) {
        return digital(event.getAxisValue(MotionEvent.AXIS_Y),
                event.getAxisValue(MotionEvent.AXIS_HAT_Y));
    }

    private static int digital(float stick, float hat) {
        float value = Math.abs(hat) > Math.abs(stick) ? hat : stick;
        return value > THRESHOLD ? 1 : (value < -THRESHOLD ? -1 : 0);
    }
}
