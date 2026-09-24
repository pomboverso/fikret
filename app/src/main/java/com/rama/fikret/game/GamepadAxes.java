package com.rama.fikret.game;

import android.view.InputDevice;
import android.view.MotionEvent;

/**
 * Reads analog sticks and hat-style d-pads off a MotionEvent and turns
 * them into the same -1/0/1 per-axis directions the keyboard produces.
 *
 * MotionEvent.getAxisValue(), the AXIS_* constants and InputDevice's
 * joystick source flags only exist from API 12 onward. Kept in its own
 * class for the same reason as PinchZoomDetector: GameView only ever
 * touches this behind a Build.VERSION.SDK_INT >= 12 check, so on API 5-11
 * this class is simply never loaded.
 *
 * Reads the left stick (AXIS_X / AXIS_Y) and the d-pad hat
 * (AXIS_HAT_X / AXIS_HAT_Y), because controllers differ in whether their
 * d-pad shows up as key events or as a hat axis. Each axis is treated as
 * pressed once it passes half its range, so pushing the stick into a
 * diagonal registers on both axes - 8-way movement, like the touch grid.
 */
class GamepadAxes {
    private static final float THRESHOLD = 0.5f;

    private GamepadAxes() {
    }

    /** True for stick/hat movement (as opposed to mouse hover, scroll etc.,
     *  which also arrive as generic motion events). */
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

    /** Whichever of stick/hat is pushed harder wins. */
    private static int digital(float stick, float hat) {
        float value = Math.abs(hat) > Math.abs(stick) ? hat : stick;
        return value > THRESHOLD ? 1 : (value < -THRESHOLD ? -1 : 0);
    }
}
