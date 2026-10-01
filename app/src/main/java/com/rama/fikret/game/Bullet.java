package com.rama.fikret.game;

public class Bullet {
    public float x, y;
    private final float vx, vy;
    private float distanceLeft;

    public Bullet(float x, float y, float dirX, float dirY, float speed, float range) {
        this.x = x;
        this.y = y;
        this.vx = dirX * speed;
        this.vy = dirY * speed;
        this.distanceLeft = range;
    }

    public void update(long deltaMs) {
        x += vx * deltaMs;
        y += vy * deltaMs;
        distanceLeft -= (float) Math.sqrt(vx * vx + vy * vy) * deltaMs;
    }

    public boolean isSpent() {
        return distanceLeft <= 0f;
    }
}
