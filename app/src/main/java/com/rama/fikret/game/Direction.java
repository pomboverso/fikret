package com.rama.fikret.game;

public enum Direction {
    LEFT(0), RIGHT(1);

    public final int column;

    Direction(int column) {
        this.column = column;
    }
}
