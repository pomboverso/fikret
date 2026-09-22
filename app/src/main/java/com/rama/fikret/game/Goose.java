package com.rama.fikret.game;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.rama.fikret.R;

/**
 * The player-controlled goose. Moves one whole tile at a time: pressing a
 * direction steps it to the next tile over, snapped to the grid, with a
 * short smooth glide between the two tiles rather than an instant jump.
 * Holding a direction keeps stepping, tile after tile.
 *
 * Animation rows in gm_goose (per direction column):
 *   row 0 - resting pose, legs tucked in - used ONLY while swimming or
 *           sleeping, never during normal walking/idle.
 *   row 1 - standing still (used whenever the goose has no movement input).
 *   row 2, row 3 - the two walk-cycle poses, alternated while stepping.
 */
public class Goose {
    private static final int ATLAS_COLUMNS = 4;
    private static final int ATLAS_ROWS = 4;
    private static final long STEP_DURATION_MS = 160; // time to glide across one tile
    private static final long WALK_FRAME_DURATION_MS = 80;

    private static final int FRAME_REST = 0;
    private static final int FRAME_IDLE = 1;
    private static final int FRAME_WALK_A = 2;
    private static final int FRAME_WALK_B = 3;

    private final SpriteSheet spriteSheet;

    // Grid position the goose is stepping FROM and TO. Equal when standing still.
    private int fromRow, fromCol;
    private int row, col;
    private float stepProgress = 1f; // 0 = at fromRow/fromCol, 1 = at row/col

    private Direction facing = Direction.DOWN;
    private boolean moving = false;
    private boolean resting = false;
    private boolean walkToggle = false;
    private long walkAnimTimer = 0;

    public Goose(Resources res, int startRow, int startCol) {
        this.spriteSheet = new SpriteSheet(res, R.drawable.gm_goose, ATLAS_COLUMNS, ATLAS_ROWS);
        this.row = this.fromRow = startRow;
        this.col = this.fromCol = startCol;
    }

    /**
     * Advances the goose by one frame. dx/dy is the currently held
     * direction already resolved down to a single cardinal step by the
     * caller: exactly one of them is -1, 0, or 1, never both nonzero at
     * once (see GameView, which collapses 8-directional input to 4 before
     * calling this).
     */
    public void update(long deltaMs, int dx, int dy, GameMap map) {
        if (!moving) {
            if (resting) {
                return;
            }
            if (dx != 0 || dy != 0) {
                facing = dx > 0 ? Direction.RIGHT : dx < 0 ? Direction.LEFT
                        : dy > 0 ? Direction.DOWN : Direction.UP;

                int targetRow = row + dy;
                int targetCol = col + dx;
                if (map.isInBounds(targetRow, targetCol)) {
                    fromRow = row;
                    fromCol = col;
                    row = targetRow;
                    col = targetCol;
                    stepProgress = 0f;
                    moving = true;
                }
                // else: bumped the edge of the map - just faces that way
                // without moving. A passability check (water, obstacles...)
                // belongs right here too, once those tiles exist.
            }
        }

        if (moving) {
            stepProgress += deltaMs / (float) STEP_DURATION_MS;
            if (stepProgress >= 1f) {
                stepProgress = 1f;
                moving = false;
            }

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

    public void draw(Canvas canvas, Rect dst) {
        int frame = resting ? FRAME_REST : (moving ? (walkToggle ? FRAME_WALK_A : FRAME_WALK_B) : FRAME_IDLE);
        Rect src = spriteSheet.frameRect(facing.column, frame);
        canvas.drawBitmap(spriteSheet.getBitmap(), src, dst, null);
    }

    /** World-space pixel position, interpolated between tiles mid-step. */
    public float getX() {
        return lerp(fromCol, col) * GameMap.TILE_SIZE;
    }

    public float getY() {
        return lerp(fromRow, row) * GameMap.TILE_SIZE;
    }

    private float lerp(int from, int to) {
        return from + (to - from) * stepProgress;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean isMoving() {
        return moving;
    }
}
