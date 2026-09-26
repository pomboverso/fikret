package com.rama.fikret.game;

/**
 * Facing direction, mapped to the column index of a 4-direction character
 * spritesheet like gm_goose (col 0 = left, 1 = up/back, 2 = right,
 * 3 = down/front - confirmed by looking at the actual goose art).
 */
public enum Direction {
    LEFT(0), RIGHT(1);

    public final int column;

    Direction(int column) {
        this.column = column;
    }
}
