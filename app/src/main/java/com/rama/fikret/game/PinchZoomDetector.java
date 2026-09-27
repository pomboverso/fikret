package com.rama.fikret.game;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

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
