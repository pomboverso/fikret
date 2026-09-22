package com.rama.fikret.game;

/**
 * Decodes one raw value from the map creation array into its three parts.
 *
 * Format: {@code [item digits][tile type digit][position digit]}
 * <ul>
 *   <li><b>position</b> (ones digit, 1-9): which of the 9 tiles in the
 *   atlas to draw, addressed like a numpad (see {@link TileType}).
 *   5 = plain center tile.</li>
 *   <li><b>tileType</b> (tens digit, 0-9): which biome, see {@link TileType}.
 *   If the whole value is a single digit (&lt; 10) there's no tens digit, so
 *   it defaults to the first registered tile type (grass). That's what
 *   makes a quick test map like {@code [[1,1,1],[1,1,1]]} render as
 *   all-grass with the "1" read purely as a position.</li>
 *   <li><b>itemId</b> (hundreds digit and up, {@code value / 100}): an
 *   item/decoration sitting on top of the tile, 0 = none. Deliberately left
 *   as "everything above the tens digit" instead of a fixed single digit,
 *   so the item id space can grow past 9 (into the thousands, etc.) later
 *   without changing this format.</li>
 * </ul>
 *
 * Example: {@code 111} -&gt; position 1 (bottom-left), type 1 (grass),
 * item 1 (e.g. a rock).
 */
public class MapCell {
    public final int position;
    public final TileType tileType;
    public final int itemId;

    public MapCell(int rawValue) {
        this.position = rawValue % 10;
        int typeDigit = (rawValue / 10) % 10;
        this.tileType = TileType.fromId(typeDigit);
        this.itemId = rawValue / 100;
    }

    public boolean hasItem() {
        return itemId > 0;
    }
}
