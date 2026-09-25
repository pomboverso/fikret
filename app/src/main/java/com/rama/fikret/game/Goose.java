package com.rama.fikret.game;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.rama.fikret.R;

public class Goose {
    private static final int ATLAS_COLUMNS = 2;
    private static final int ATLAS_ROWS = 5;
    private static final long STEP_DURATION_MS = 200;
    private static final long WALK_FRAME_DURATION_MS = 80;
    private static final int FRAME_SWIMMING_A = 0;
    private static final int FRAME_SWIMMING_B = 1;
    private static final int FRAME_IDLE = 2;
    private static final int FRAME_WALK_A = 3;
    private static final int FRAME_WALK_B = 4;
    private final SpriteSheet spriteSheet;
    private int fromRow, fromCol;
    private int row, col;
    private float stepProgress = 1f;
    private Direction facing = Direction.RIGHT;
    private boolean moving = false;
    private boolean resting = false;
    private boolean swimming = false;
    private boolean walkToggle = false;
    private long walkAnimTimer = 0;

    public Goose(Resources res, int startRow, int startCol) {
        this.spriteSheet = new SpriteSheet(res, R.drawable.goose, ATLAS_COLUMNS, ATLAS_ROWS);
        this.row = this.fromRow = startRow;
        this.col = this.fromCol = startCol;
    }

    public void update(long deltaMs, int dx, int dy, GameMap map) {
        if (!moving) {
            if (resting) {
                return;
            }
            if (dx != 0 || dy != 0) {
                if(dx != 0){
                    facing = dx < 0 ? Direction.LEFT : Direction.RIGHT;
                }

                // dx/dy can both be non-zero: that's a diagonal step (one
                // tile over AND one tile up/down), see GameMap.canStep().
                if (map.canStep(row, col, dx, dy)) {
                    fromRow = row;
                    fromCol = col;
                    row = row + dy;
                    col = col + dx;
                    stepProgress = 0f;
                    moving = true;
                }
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

    public void setResting(boolean resting) {
        this.resting = resting;
    }

    /** Swimming = standing in liquid (water, lava...). Unlike resting the
     *  goose can still move; it just uses the tucked-legs pose (row 0)
     *  instead of the walk/idle poses. GameView sets this every frame from
     *  the tile under the goose. */
    public void setSwimming(boolean swimming) {
        this.swimming = swimming;
    }

    public boolean isSwimming() {
        return swimming;
    }

    public void draw(Canvas canvas, Rect dst) {
        int frame = resting
                ? FRAME_SWIMMING_A
                : swimming
                ? (walkToggle ? FRAME_SWIMMING_A : FRAME_SWIMMING_B)
                : (moving ? (walkToggle ? FRAME_WALK_A : FRAME_WALK_B) : FRAME_IDLE);
        Rect src = spriteSheet.frameRect(facing.column, frame);
        canvas.drawBitmap(spriteSheet.getBitmap(), src, dst, null);
    }

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
