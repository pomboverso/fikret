package com.rama.fikret.game;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

/**
 * Thin wrapper around ScaleGestureDetector, which only exists from API 8
 * onward. Kept in its own class on purpose: a class's bytecode is verified
 * (and every type it references resolved) the first time it's touched, so
 * if GameView itself mentioned ScaleGestureDetector directly, devices below
 * API 8 could fail to even load GameView. By only ever instantiating this
 * wrapper behind a Build.VERSION.SDK_INT check (see GameView), this class -
 * and therefore ScaleGestureDetector - is simply never loaded on those
 * devices.
 */
class PinchZoomDetector {
    interface Listener {
        void onZoom(float scaleFactor, float focusX, float focusY);
    }

    private final ScaleGestureDetector detector;

    PinchZoomDetector(Context context, final Listener listener) {
        detector = new ScaleGestureDetector(context, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                listener.onZoom(detector.getScaleFactor(), detector.getFocusX(), detector.getFocusY());
                return true;
            }
        });
    }

    void onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
    }
}
