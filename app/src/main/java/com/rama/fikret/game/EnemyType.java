package com.rama.fikret.game;

public enum EnemyType {
    BLUE(0, 30, 420, 4, 500, 2000, 0, 0, false),     // slows you down while draining life
    RED(1, 20, 320, 40, 0, 0, 0, 0, true),           // explodes on touch, big damage
    PURPLE(2, 25, 380, 8, 800, 0, 0, 0, false),      // plain damage
    GREEN(3, 30, 400, 3, 800, 0, 5000, 2, false);    // poisons you and takes a little life

    public final int column;
    public final int maxHp;
    public final long stepMs;
    public final int touchDamage;
    public final long touchCooldownMs;
    public final long slowMs;
    public final long poisonMs;
    public final int poisonDamage;
    public final boolean explodes;

    EnemyType(int column, int maxHp, long stepMs, int touchDamage, long touchCooldownMs,
              long slowMs, long poisonMs, int poisonDamage, boolean explodes) {
        this.column = column;
        this.maxHp = maxHp;
        this.stepMs = stepMs;
        this.touchDamage = touchDamage;
        this.touchCooldownMs = touchCooldownMs;
        this.slowMs = slowMs;
        this.poisonMs = poisonMs;
        this.poisonDamage = poisonDamage;
        this.explodes = explodes;
    }
}
