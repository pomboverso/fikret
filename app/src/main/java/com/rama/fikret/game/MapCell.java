package com.rama.fikret.game;

/**
 * Decodes one raw value from the map creation array. Format is 8 digits,
 * read left to right as EEDDCBBA (values with fewer digits are padded with
 * leading zeros first, so plain old short codes still work):
 *
 * <pre>
 * E (2 digits) - item, see ItemType. 00 = none.
 * D (2 digits) - background tile, see TileType. 00 = no background layer.
 * C (1 digit)  - background tile's numpad position (1-9, see TilePosition).
 * B (2 digits) - (foreground) tile, see TileType.
 * A (1 digit)  - tile's numpad position (1-9, see TilePosition).
 * </pre>
 *
 * The background layer is drawn first, the tile drawn on top of it, then
 * the item on top of both - so a corner/edge piece of the foreground tile
 * (e.g. a water shoreline) can let the background show through wherever
 * its art doesn't fully cover the cell.
 *
 * Example: {@code 00015029} -&gt; no item, grass background (middle),
 * water tile (top-right corner).
 * Example: {@code 01000015} -&gt; a stone sitting on plain grass.
 */
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

    /** Numpad positions only mean something in 1-9; treat 0 (or anything
     *  stray) as the plain center tile rather than letting a bad digit
     *  produce a wrapped-around/negative atlas lookup. */
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

    public boolean hasBackground() {
        return backgroundTile != TileType.NONE;
    }

    public boolean hasItem() {
        return item != ItemType.NONE;
    }
}
