package com.rama.fikret.game;

public class MapCell {
    public final ItemType item;
    public final EnemyType enemy;   // null when no enemy is placed here
    public final TileType backgroundTile;
    public final int backgroundPosition;
    public final TileType tile;
    public final int position;

    public MapCell(int rawValue) {
        int raw = Math.max(rawValue, 0);
        int enemyId = (raw / 100000000) % 100;
        int e = (raw / 1000000) % 100;
        int d = (raw / 10000) % 100;
        int c = (raw / 1000) % 10;
        int b = (raw / 10) % 100;
        int a = raw % 10;

        this.enemy = EnemyType.fromId(enemyId);
        this.item = ItemType.fromId(e);
        this.backgroundTile = TileType.fromId(d);
        this.backgroundPosition = sanitizePosition(c);
        this.tile = TileType.fromId(b);
        this.position = sanitizePosition(a);
    }

    private static int sanitizePosition(int p) {
        return (p < 1 || p > 9) ? 5 : p;
    }

    private TileType topTile() {
        return tile != TileType.NONE ? tile : backgroundTile;
    }

    public boolean isLiquid() {
        return topTile().liquid;
    }

    public boolean isNonWaterLiquid() {
        TileType top = topTile();
        return top.liquid && !top.isWater();
    }

    public boolean hasBackground() {
        return backgroundTile != TileType.NONE;
    }

    public boolean hasEnemy() {
        return enemy != null;
    }

    public boolean hasItem() {
        return item != ItemType.NONE;
    }
}
