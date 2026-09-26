package com.rama.fikret.game;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Floating "swipe from anywhere" joystick.
 *
 * Touch the screen at any point and a 3x3 grid appears centred on your
 * finger, with a circle (the knob) in the middle cell. Drag the knob into
 * one of the 8 outer cells and the goose walks that way - the four side
 * cells are up/down/left/right, the four corner cells are the diagonals.
 * The middle cell is the dead zone (no movement). Lift your finger and the
 * grid disappears.
 *
 * The drag direction is read from where the finger is relative to where it
 * first landed, using exactly the same cell boundaries that are drawn, so
 * the knob crossing a grid line is precisely when the direction changes.
 * The outer cells extend past the drawn grid, so you can keep dragging
 * beyond it and the direction holds; only the knob's drawing is clamped to
 * stay inside the grid.
 *
 * Threading: touch callbacks arrive on the UI thread, getDx()/getDy() and
 * draw() are called from GameThread - hence every method is synchronized
 * (uncontended and tiny, so no measurable cost per frame).
 *
 * API 5 safe: only Canvas/Paint calls that exist since API 1.
 */
class SwipeJoystick {

    /** Width/height of one grid cell in dp (the whole grid is 3x this). */
    private static final float CELL_DP = 44f;
    /** Knob radius as a fraction of one cell. */
    private static final float KNOB_RADIUS_FRACTION = 0.36f;

    private final float cellSize;
    private final float knobRadius;

    private final Paint panelPaint = new Paint();
    private final Paint activeCellPaint = new Paint();
    private final Paint linePaint = new Paint();
    private final Paint homeRingPaint = new Paint();
    private final Paint knobPaint = new Paint();

    private boolean active;
    private float anchorX, anchorY; // where the finger first landed = grid centre
    private float offsetX, offsetY; // finger position relative to the anchor
    private int dx, dy;             // resolved direction: each -1, 0 or 1

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

    /** Finger touched down: the grid appears centred here. */
    synchronized void begin(float x, float y) {
        active = true;
        anchorX = x;
        anchorY = y;
        offsetX = 0f;
        offsetY = 0f;
        dx = 0;
        dy = 0;
    }

    /** Finger moved: drag the knob and re-resolve the direction. */
    synchronized void move(float x, float y) {
        if (!active) {
            return;
        }
        offsetX = x - anchorX;
        offsetY = y - anchorY;

        // A cell is one cellSize wide and the middle cell straddles the
        // anchor, so the boundaries sit half a cell either side of it.
        float threshold = cellSize * 0.5f;
        dx = offsetX > threshold ? 1 : (offsetX < -threshold ? -1 : 0);
        dy = offsetY > threshold ? 1 : (offsetY < -threshold ? -1 : 0);
    }

    /** Finger lifted (or the gesture turned into a pinch): grid disappears,
     *  movement stops. */
    synchronized void end() {
        active = false;
        offsetX = 0f;
        offsetY = 0f;
        dx = 0;
        dy = 0;
    }

    /** Horizontal direction requested: -1 left, 0 none, 1 right. */
    synchronized int getDx() {
        return dx;
    }

    /** Vertical direction requested: -1 up, 0 none, 1 down. */
    synchronized int getDy() {
        return dy;
    }

    /** Draws in raw screen pixels - call after the world (zoomed) layer
     *  has been restored so it isn't scaled or scrolled with the camera. */
    synchronized void draw(Canvas canvas) {
        if (!active) {
            return;
        }

        float half = cellSize * 1.5f;
        float left = anchorX - half;
        float top = anchorY - half;
        float right = anchorX + half;
        float bottom = anchorY + half;

        // Panel behind everything.
        canvas.drawRect(left, top, right, bottom, panelPaint);

        // Highlight the cell the knob is in (nothing for the middle cell).
        if (dx != 0 || dy != 0) {
            float cellLeft = anchorX + (dx - 0.5f) * cellSize;
            float cellTop = anchorY + (dy - 0.5f) * cellSize;
            canvas.drawRect(cellLeft, cellTop, cellLeft + cellSize, cellTop + cellSize, activeCellPaint);
        }

        // 3x3 grid lines + outer border.
        for (int i = 1; i < 3; i++) {
            float x = left + i * cellSize;
            float y = top + i * cellSize;
            canvas.drawLine(x, top, x, bottom, linePaint);
            canvas.drawLine(left, y, right, y, linePaint);
        }
        canvas.drawRect(left, top, right, bottom, linePaint);

        // Ring marking the knob's home position (middle of the grid).
        canvas.drawCircle(anchorX, anchorY, knobRadius, homeRingPaint);

        // The knob itself, kept fully inside the grid however far you drag.
        float maxOffset = half - knobRadius;
        float knobX = anchorX + clamp(offsetX, -maxOffset, maxOffset);
        float knobY = anchorY + clamp(offsetY, -maxOffset, maxOffset);
        canvas.drawCircle(knobX, knobY, knobRadius, knobPaint);
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(value, max));
    }
}
