package com.rama.fikret.game;

public class PlayerStats {
    public static final int MAX_HP = 100;
    private static final long POISON_TICK_MS = 500;
    private static final float SLOWED_STEP_MULTIPLIER = 1.8f;

    private int hp = MAX_HP;
    private long slowMs;
    private long poisonMs;
    private int poisonDamage;
    private long poisonTickMs;

    public int getHp() {
        return hp;
    }

    public boolean isDead() {
        return hp <= 0;
    }

    public boolean isSlowed() {
        return slowMs > 0;
    }

    public boolean isPoisoned() {
        return poisonMs > 0;
    }

    public void damage(int amount) {
        hp = Math.max(0, hp - amount);
    }

    public void applySlow(long ms) {
        slowMs = Math.max(slowMs, ms);
    }

    public void applyPoison(long ms, int damagePerTick) {
        poisonMs = Math.max(poisonMs, ms);
        poisonDamage = Math.max(poisonDamage, damagePerTick);
    }

    public float stepMultiplier() {
        return slowMs > 0 ? SLOWED_STEP_MULTIPLIER : 1f;
    }

    public void update(long deltaMs) {
        if (slowMs > 0) {
            slowMs = Math.max(0, slowMs - deltaMs);
        }
        if (poisonMs > 0) {
            long elapsed = Math.min(deltaMs, poisonMs);
            poisonMs -= elapsed;
            poisonTickMs += elapsed;
            while (poisonTickMs >= POISON_TICK_MS) {
                poisonTickMs -= POISON_TICK_MS;
                damage(poisonDamage);
            }
            if (poisonMs == 0) {
                poisonTickMs = 0;
                poisonDamage = 0;
            }
        }
    }

    public void revive() {
        hp = MAX_HP;
        slowMs = 0;
        poisonMs = 0;
        poisonDamage = 0;
        poisonTickMs = 0;
    }
}
