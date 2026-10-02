package com.rama.fikret.game;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;

import com.rama.fikret.R;

import java.util.List;

public class Boss extends Enemy {
    private static final int SHEET_ROWS = 3;            // inferno.png: 1 column, 3 walk frames stacked
    private static final int DRAW_TILES = 4;            // the sprite is drawn this many tiles wide/tall
    private static final long WALK_FRAME_MS = 250;      // time each walk frame is shown
    private static final long WALK_PHASE_MS = 5000;     // how long it walks before stopping
    private static final long SHOOT_PHASE_MS = 4600;    // how long it stands and attacks
    private static final long WINDUP_MS = 600;          // pause before the first volley
    private static final long VOLLEY_INTERVAL_MS = 1000;

    private enum Phase { WALK, SHOOT }

    private final Paint normalPaint = new Paint();
    private final Paint flashPaint = new Paint();
    private final Paint barPaint = new Paint();
    private final Rect drawRect = new Rect();

    private Phase phase = Phase.WALK;
    private long phaseMs = 0;
    private long walkMs = 0;
    private long volleyMs = 0;
    private boolean volleyReady = false;
    private boolean summonReady = false;

    public Boss(Resources res, int row, int col) {
        super(EnemyType.BOSS, new SpriteSheet(res, R.drawable.inferno, 1, SHEET_ROWS), row, col);
        flashPaint.setColorFilter(new PorterDuffColorFilter(0xB0FFFFFF, PorterDuff.Mode.SRC_ATOP));
    }

    @Override
    public void update(long deltaMs, GameMap map, int targetRow, int targetCol, boolean chase, List<Enemy> others) {
        phaseMs += deltaMs;

        boolean walking = phase == Phase.WALK && phaseMs < WALK_PHASE_MS;
        super.update(deltaMs, map, targetRow, targetCol, walking, others);
        if (isDead()) {
            return;
        }

        if (phase == Phase.WALK) {
            walkMs += deltaMs;
            if (phaseMs >= WALK_PHASE_MS && !isMoving()) {
                enterShoot();
            }
        } else {
            volleyMs -= deltaMs;
            if (volleyMs <= 0) {
                volleyReady = true;
                volleyMs += VOLLEY_INTERVAL_MS;
            }
            if (phaseMs >= SHOOT_PHASE_MS) {
                phase = Phase.WALK;
                phaseMs = 0;
            }
        }
    }

    private void enterShoot() {
        phase = Phase.SHOOT;
        phaseMs = 0;
        volleyMs = WINDUP_MS;
        summonReady = true;
    }

    public boolean consumeVolley() {
        boolean ready = volleyReady;
        volleyReady = false;
        return ready;
    }

    public boolean consumeSummon() {
        boolean ready = summonReady;
        summonReady = false;
        return ready;
    }

    @Override
    public float getTapRadius() {
        return 100f;
    }

    @Override
    public float getHitRadius() {
        return 70f;
    }

    @Override
    public void draw(Canvas canvas, Rect dst) {
        int frame = phase == Phase.WALK ? (int) ((walkMs / WALK_FRAME_MS) % SHEET_ROWS) : 0;
        int size = dst.width() * DRAW_TILES;
        int cx = dst.centerX();
        int cy = dst.centerY();
        drawRect.set(cx - size / 2, cy - size / 2, cx + size / 2, cy + size / 2);
        canvas.drawBitmap(spriteSheet.getBitmap(), spriteSheet.frameRect(0, frame), drawRect,
                isFlashing() ? flashPaint : normalPaint);

        float barW = dst.width() * 2f;
        float barH = 6f;
        float left = cx - barW / 2f;
        float top = drawRect.top - 4f - barH;
        barPaint.setStyle(Paint.Style.FILL);
        barPaint.setColor(0xAA000000);
        canvas.drawRect(left - 1f, top - 1f, left + barW + 1f, top + barH + 1f, barPaint);
        barPaint.setColor(0xFFD0201A);
        float fraction = Math.max(0f, getHp() / (float) type.maxHp);
        canvas.drawRect(left, top, left + barW * fraction, top + barH, barPaint);
    }
}
