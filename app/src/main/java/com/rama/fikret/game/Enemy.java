package com.rama.fikret.game;

import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.List;

public class Enemy {
    public enum HitSource {
        BULLET(4), LIGHTNING(5);

        final int column;

        HitSource(int column) {
            this.column = column;
        }
    }

    private static final long FLASH_MS = 150;
    private static final long ANIM_FRAME_MS = 300;

    public final EnemyType type;
    protected final SpriteSheet spriteSheet;

    private int fromRow, fromCol;
    private int row, col;
    private float stepProgress = 1f;
    private boolean moving = false;

    private int hp;
    private boolean dead = false;
    private long flashMs = 0;
    private HitSource flashSource = HitSource.BULLET;
    private long touchCooldownMs = 0;
    private long animMs = 0;
    private boolean alwaysChase = false;

    public Enemy(EnemyType type, SpriteSheet spriteSheet, int row, int col) {
        this.type = type;
        this.spriteSheet = spriteSheet;
        this.hp = type.maxHp;
        this.row = this.fromRow = row;
        this.col = this.fromCol = col;
    }

    public void update(long deltaMs, GameMap map, int targetRow, int targetCol, boolean chase, List<Enemy> others) {
        animMs += deltaMs;
        if (flashMs > 0) {
            flashMs = Math.max(0, flashMs - deltaMs);
        }
        if (dead) {
            return;
        }
        if (touchCooldownMs > 0) {
            touchCooldownMs = Math.max(0, touchCooldownMs - deltaMs);
        }

        if (!moving && (chase || alwaysChase)) {
            startStepToward(map, targetRow, targetCol, others);
        }
        if (moving) {
            stepProgress += deltaMs / (float) type.stepMs;
            if (stepProgress >= 1f) {
                stepProgress = 1f;
                moving = false;
            }
        }
    }

    private void startStepToward(GameMap map, int targetRow, int targetCol, List<Enemy> others) {
        int bestDx = 0;
        int bestDy = 0;
        long best = distanceSq(row, col, targetRow, targetCol);
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if ((dx == 0 && dy == 0) || !map.canStep(row, col, dx, dy, false)) {
                    continue;
                }
                if (isOccupied(row + dy, col + dx, others)) {
                    continue;
                }
                long d = distanceSq(row + dy, col + dx, targetRow, targetCol);
                if (d < best) {
                    best = d;
                    bestDx = dx;
                    bestDy = dy;
                }
            }
        }
        if (bestDx != 0 || bestDy != 0) {
            fromRow = row;
            fromCol = col;
            row += bestDy;
            col += bestDx;
            stepProgress = 0f;
            moving = true;
        }
    }

    private boolean isOccupied(int r, int c, List<Enemy> others) {
        for (int i = 0; i < others.size(); i++) {
            Enemy o = others.get(i);
            if (o != this && o.row == r && o.col == c) {
                return true;
            }
        }
        return false;
    }

    private static long distanceSq(int r1, int c1, int r2, int c2) {
        long dr = r1 - r2;
        long dc = c1 - c2;
        return dr * dr + dc * dc;
    }

    public boolean hit(int damage, HitSource source) {
        if (dead) {
            return false;
        }
        hp -= damage;
        flashMs = FLASH_MS;
        flashSource = source;
        dead = hp <= 0;
        return dead;
    }

    public void setAlwaysChase(boolean alwaysChase) {
        this.alwaysChase = alwaysChase;
    }

    public boolean isMoving() {
        return moving;
    }

    public float getTapRadius() {
        return 56f;
    }

    public float getHitRadius() {
        return 22f;
    }

    public boolean isDead() {
        return dead;
    }

    public boolean isGone() {
        return dead && flashMs == 0;
    }

    public boolean canTouch() {
        return !dead && touchCooldownMs <= 0;
    }

    public void startTouchCooldown() {
        touchCooldownMs = type.touchCooldownMs;
    }

    public void draw(Canvas canvas, Rect dst) {
        int column = flashMs > 0 ? flashSource.column : type.column;
        int frame = (int) ((animMs / ANIM_FRAME_MS) % spriteSheet.getRows());
        canvas.drawBitmap(spriteSheet.getBitmap(), spriteSheet.frameRect(column, frame), dst, null);
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getHp() {
        return hp;
    }

    public boolean isFlashing() {
        return flashMs > 0;
    }

    public float getX() {
        return lerp(fromCol, col) * GameMap.TILE_SIZE;
    }

    public float getY() {
        return lerp(fromRow, row) * GameMap.TILE_SIZE;
    }

    public float getCenterX() {
        return getX() + GameMap.TILE_SIZE / 2f;
    }

    public float getCenterY() {
        return getY() + GameMap.TILE_SIZE / 2f;
    }

    private float lerp(int from, int to) {
        return from + (to - from) * stepProgress;
    }
}
