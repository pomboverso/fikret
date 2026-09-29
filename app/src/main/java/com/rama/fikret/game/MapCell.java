package com.rama.fikret.game;

public class MapCell {
    public final ItemType item;
    public final TileType backgroundTile;
    public final int backgroundPosition;
    public final TileType tile;
    public final int position;

    public MapCell(int rawValue) {
        String digits = padTo8(rawValue);
        int e = Integer.parseInt(digits.substring(0, 2));
        int d = Integer.parseInt(digits.substring(2, 4));
        int c = Integer.parseInt(digits.substring(4, 5));
        int b = Integer.parseInt(digits.substring(5, 7));
        int a = Integer.parseInt(digits.substring(7, 8));

        this.item = ItemType.fromId(e);
        this.backgroundTile = TileType.fromId(d);
        this.backgroundPosition = sanitizePosition(c);
        this.tile = TileType.fromId(b);
        this.position = sanitizePosition(a);
    }

    private static int sanitizePosition(int p) {
        return (p < 1 || p > 9) ? 5 : p;
    }

    private static String padTo8(int value) {
        String s = Integer.toString(Math.max(value, 0));
        StringBuilder sb = new StringBuilder();
        for (int i = s.length(); i < 8; i++) {
            sb.append('0');
        }
        sb.append(s);
        return sb.toString();
    }

    private TileType topTile() {
        return tile != TileType.NONE ? tile : backgroundTile;
    }

    public boolean isLiquid() {
        return topTile().liquid;
    }

    /** Lava, acid, etc. Swimmable only with the diamond skin. */
    public boolean isNonWaterLiquid() {
        TileType top = topTile();
        return top.liquid && !top.isWater();
    }

    public boolean hasBackground() {
        return backgroundTile != TileType.NONE;
    }

    public boolean hasItem() {
        return item != ItemType.NONE;
    }
}
