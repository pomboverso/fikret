package com.rama.fikret.game;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.rama.fikret.R;

/**
 * A small companion. Spawns standing still wherever ItemType.BIRD appears
 * in the map (see GameView, which scans for it once when a stage loads).
 * Once the goose steps onto its tile it starts following, trailing exactly
 * one tile behind the goose from then on for the rest of the stage.
 *
 * Follow logic: GameView calls moveTowards(...) with the tile the goose
 * just left every time the goose takes a step. Since the bird is always
 * either sitting on the goose's previous tile or walking into it, that
 * target is guaranteed to be a single step away - no pathfinding needed.
 *
 * Shares the goose's sprite layout: 2 direction columns (left/right) x 4
 * rows (rest, idle, walk A, walk B) - see Goose for what each row means.
 * The bird swims (row 0, legs tucked) whenever it is standing in liquid -
 * see setSwimming(), which GameView drives from the tile under the bird,
 * whether it is still idle or already following the goose.
 */
public class Bird {
    private static final int ATLAS_COLUMNS = 2;
    private static final int ATLAS_ROWS = 4;
    // Matches Goose's STEP_DURATION_MS so the two stay in lockstep.
    private static final long STEP_DURATION_MS = 200;
    private static final long WALK_FRAME_DURATION_MS = 80;

    private static final int FRAME_REST = 0;
    private static final int FRAME_IDLE = 1;
    private static final int FRAME_WALK_A = 2;
    private static final int FRAME_WALK_B = 3;

    private final SpriteSheet spriteSheet;

    private int fromRow, fromCol;
    private int row, col;
    private float stepProgress = 1f;
    private boolean hasPendingTarget = false;
    private int pendingTargetRow, pendingTargetCol;

    private Direction facing = Direction.RIGHT;
    private boolean moving = false;
    private boolean following = false;
    private boolean swimming = false;
    private boolean walkToggle = false;
    private long walkAnimTimer = 0;

    public Bird(Resources res, int startRow, int startCol) {
        this.spriteSheet = new SpriteSheet(res, R.drawable.bird, ATLAS_COLUMNS, ATLAS_ROWS);
        this.row = this.fromRow = startRow;
        this.col = this.fromCol = startCol;
    }

    public boolean isFollowing() {
        return following;
    }

    /** Swimming = standing in liquid: draws the tucked-legs pose (row 0)
     *  instead of idle/walk. Independent of following - an idle bird
     *  sitting in a pond swims too. */
    public void setSwimming(boolean swimming) {
        this.swimming = swimming;
    }

    public boolean isSwimming() {
        return swimming;
    }

    public void startFollowing() {
        following = true;
    }

    public void moveTowards(int targetRow, int targetCol) {
        if (!following) {
            return;
        }
        pendingTargetRow = targetRow;
        pendingTargetCol = targetCol;
        hasPendingTarget = true;
    }

    public void update(long deltaMs) {
        if (!moving && hasPendingTarget) {
            hasPendingTarget = false;
            int dRow = pendingTargetRow - row;
            int dCol = pendingTargetCol - col;

            if (dRow != 0 || dCol != 0) {
                if (dCol != 0) {
                    facing = dCol < 0 ? Direction.LEFT : Direction.RIGHT;
                }
                fromRow = row;
                fromCol = col;
                row = pendingTargetRow;
                col = pendingTargetCol;
                stepProgress = 0f;
                moving = true;
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

    public void draw(Canvas canvas, Rect dst) {
        int frame = swimming ? FRAME_REST : (moving ? (walkToggle ? FRAME_WALK_A : FRAME_WALK_B) : FRAME_IDLE);
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
}
