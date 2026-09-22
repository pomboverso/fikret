package com.rama.fikret.game;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.rama.fikret.R;

/**
 * The player-controlled goose. Moves freely in continuous world-space pixels
 * while a direction is held (see {@link GameView} for where the direction
 * comes from: screen regions or the keyboard/numpad).
 */
public class Goose {
    private static final int ATLAS_COLUMNS = 4;
    private static final int ATLAS_ROWS = 4; // 4 walk-cycle frames per direction
    private static final float PIXELS_PER_SECOND = GameMap.TILE_SIZE * 3f;
    private static final long FRAME_DURATION_MS = 120;

    private final SpriteSheet spriteSheet;

    // World-space top-left pixel position.
    private float x, y;

    private Direction facing = Direction.DOWN;
    private int animFrame = 0;
    private long animTimer = 0;
    private boolean moving = false;

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
        moving = dx != 0f || dy != 0f;

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
        }

        animTimer += deltaMs;
        if (animTimer >= FRAME_DURATION_MS) {
            animTimer = 0;
            animFrame = moving ? (animFrame + 1) % ATLAS_ROWS : 0;
        }
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(value, max));
    }

    public void draw(Canvas canvas, Rect dst) {
        Rect src = spriteSheet.frameRect(facing.column, animFrame);
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
