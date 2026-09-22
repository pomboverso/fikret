package com.rama.fikret.game;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.rama.fikret.R;

/**
 * The player-controlled goose. Moves freely in continuous world-space pixels
 * while a direction is held (see {@link GameView} for where the direction
 * comes from: screen regions or the keyboard/numpad).
 *
 * Animation rows in gm_goose (per direction column):
 *   row 0 - resting pose, legs tucked in - used ONLY while swimming or
 *           sleeping, never during normal walking/idle.
 *   row 1 - standing still (used whenever the goose has no movement input).
 *   row 2, row 3 - the two walk-cycle poses, alternated while moving.
 */
public class Goose {
    private static final int ATLAS_COLUMNS = 4;
    private static final int ATLAS_ROWS = 4;
    private static final float PIXELS_PER_SECOND = GameMap.TILE_SIZE * 3f;
    private static final long WALK_FRAME_DURATION_MS = 120;

    private static final int FRAME_REST = 0;   // swimming / sleeping only
    private static final int FRAME_IDLE = 1;   // standing still
    private static final int FRAME_WALK_A = 2;
    private static final int FRAME_WALK_B = 3;

    private final SpriteSheet spriteSheet;

    // World-space top-left pixel position.
    private float x, y;

    private Direction facing = Direction.DOWN;
    private boolean moving = false;
    private boolean resting = false; // true while swimming or sleeping
    private boolean walkToggle = false;
    private long walkAnimTimer = 0;

    public Goose(Resources res, float startX, float startY) {
        this.spriteSheet = new SpriteSheet(res, R.drawable.gm_goose, ATLAS_COLUMNS, ATLAS_ROWS);
        this.x = startX;
        this.y = startY;
    }

    /**
     * Advances the goose by one frame. dx/dy describe the held direction,
     * each in [-1, 1] - only their direction matters, this normalizes the
     * vector itself so diagonal movement isn't faster than cardinal movement.
     * (0, 0) means "no input", i.e. stand still.
     */
    public void update(long deltaMs, float dx, float dy, GameMap map) {
        moving = (dx != 0f || dy != 0f) && !resting;

        if (moving) {
            float length = (float) Math.sqrt(dx * dx + dy * dy);
            float normalizedX = dx / length;
            float normalizedY = dy / length;

            // Sprite only has 4 facings - horizontal wins on a diagonal.
            if (Math.abs(dx) > 0.0001f) {
                facing = dx > 0 ? Direction.RIGHT : Direction.LEFT;
            } else {
                facing = dy > 0 ? Direction.DOWN : Direction.UP;
            }

            float distance = PIXELS_PER_SECOND * (deltaMs / 1000f);
            x += normalizedX * distance;
            y += normalizedY * distance;

            float maxX = Math.max(0, map.getWidthPx() - GameMap.TILE_SIZE);
            float maxY = Math.max(0, map.getHeightPx() - GameMap.TILE_SIZE);
            x = clamp(x, 0, maxX);
            y = clamp(y, 0, maxY);

            walkAnimTimer += deltaMs;
            if (walkAnimTimer >= WALK_FRAME_DURATION_MS) {
                walkAnimTimer = 0;
                walkToggle = !walkToggle;
            }
        } else {
            walkAnimTimer = 0;
        }
    }

    /** Call when the goose enters/leaves water or falls asleep - while true,
     *  it holds the legs-tucked-in resting pose and ignores movement input.
     *  Not driven by anything yet (no water tiles/sleep trigger exist),
     *  wire this up once those features land. */
    public void setResting(boolean resting) {
        this.resting = resting;
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(value, max));
    }

    public void draw(Canvas canvas, Rect dst) {
        int frame = resting ? FRAME_REST : (moving ? (walkToggle ? FRAME_WALK_A : FRAME_WALK_B) : FRAME_IDLE);
        Rect src = spriteSheet.frameRect(facing.column, frame);
        canvas.drawBitmap(spriteSheet.getBitmap(), src, dst, null);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public boolean isMoving() {
        return moving;
    }
}

