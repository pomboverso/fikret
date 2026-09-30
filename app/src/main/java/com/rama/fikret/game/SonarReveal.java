package com.rama.fikret.game;

public final class SonarReveal {
    public final int row;
    public final int col;
    public final ItemType item;
    private final int zoneTop;
    private final int zoneLeft;
    private final int zoneBottom;
    private final int zoneRight;

    public SonarReveal(int row, int col, ItemType item,
                       int zoneTop, int zoneLeft, int zoneBottom, int zoneRight) {
        this.row = row;
        this.col = col;
        this.item = item;
        this.zoneTop = zoneTop;
        this.zoneLeft = zoneLeft;
        this.zoneBottom = zoneBottom;
        this.zoneRight = zoneRight;
    }

    public boolean zoneContains(int r, int c) {
        return r >= zoneTop && r <= zoneBottom && c >= zoneLeft && c <= zoneRight;
    }
}
