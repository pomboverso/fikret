package com.rama.fikret.game;

import android.graphics.Canvas;
import android.graphics.Paint;

class SwipeJoystick {

    private static final float CELL_DP = 44f;
    private static final float KNOB_RADIUS_FRACTION = 0.36f;

    private final float cellSize;
    private final float knobRadius;

    private final Paint panelPaint = new Paint();
    private final Paint activeCellPaint = new Paint();
    private final Paint linePaint = new Paint();
    private final Paint homeRingPaint = new Paint();
    private final Paint knobPaint = new Paint();

    private boolean active;
    private float anchorX, anchorY;
    private float offsetX, offsetY;
    private int dx, dy;

    SwipeJoystick(float density) {
        cellSize = CELL_DP * density;
        knobRadius = cellSize * KNOB_RADIUS_FRACTION;

        panelPaint.setColor(0xFFFFFFFF);
        panelPaint.setAlpha(45);

        activeCellPaint.setColor(0xFFFFFFFF);
        activeCellPaint.setAlpha(100);

        linePaint.setColor(0xFFFFFFFF);
        linePaint.setAlpha(130);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(Math.max(1f, 1.5f * density));

        homeRingPaint.setColor(0xFFFFFFFF);
        homeRingPaint.setAlpha(150);
        homeRingPaint.setStyle(Paint.Style.STROKE);
        homeRingPaint.setStrokeWidth(Math.max(1f, 1.5f * density));
        homeRingPaint.setAntiAlias(true);

        knobPaint.setColor(0xFFFFFFFF);
        knobPaint.setAlpha(210);
        knobPaint.setAntiAlias(true);
    }

    synchronized void begin(float x, float y) {
        active = true;
        anchorX = x;
        anchorY = y;
        offsetX = 0f;
        offsetY = 0f;
        dx = 0;
        dy = 0;
    }

    synchronized void move(float x, float y) {
        if (!active) {
            return;
        }
        offsetX = x - anchorX;
        offsetY = y - anchorY;

        float threshold = cellSize * 0.5f;
        dx = offsetX > threshold ? 1 : (offsetX < -threshold ? -1 : 0);
        dy = offsetY > threshold ? 1 : (offsetY < -threshold ? -1 : 0);
    }

    synchronized void end() {
        active = false;
        offsetX = 0f;
        offsetY = 0f;
        dx = 0;
        dy = 0;
    }

    synchronized int getDx() {
        return dx;
    }

    synchronized int getDy() {
        return dy;
    }

    synchronized void draw(Canvas canvas) {
        if (!active) {
            return;
        }

        float half = cellSize * 1.5f;
        float left = anchorX - half;
        float top = anchorY - half;
        float right = anchorX + half;
        float bottom = anchorY + half;

        canvas.drawRect(left, top, right, bottom, panelPaint);

        if (dx != 0 || dy != 0) {
            float cellLeft = anchorX + (dx - 0.5f) * cellSize;
            float cellTop = anchorY + (dy - 0.5f) * cellSize;
            canvas.drawRect(cellLeft, cellTop, cellLeft + cellSize, cellTop + cellSize, activeCellPaint);
        }

        for (int i = 1; i < 3; i++) {
            float x = left + i * cellSize;
            float y = top + i * cellSize;
            canvas.drawLine(x, top, x, bottom, linePaint);
            canvas.drawLine(left, y, right, y, linePaint);
        }
        canvas.drawRect(left, top, right, bottom, linePaint);

        canvas.drawCircle(anchorX, anchorY, knobRadius, homeRingPaint);

        float maxOffset = half - knobRadius;
        float knobX = anchorX + clamp(offsetX, -maxOffset, maxOffset);
        float knobY = anchorY + clamp(offsetY, -maxOffset, maxOffset);
        canvas.drawCircle(knobX, knobY, knobRadius, knobPaint);
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(value, max));
    }
}
