package com.rama.fikret.game;

/**
 * Converts a numpad-style position code (1-9) into a (column, row) inside
 * a 3x3 tile atlas.
 *
 * <pre>
 * 7 8 9      col: 0 1 2, row 0 (top)
 * 4 5 6  ->  col: 0 1 2, row 1 (middle)
 * 1 2 3      col: 0 1 2, row 2 (bottom)
 * </pre>
 */
public final class TilePosition {
    private TilePosition() {
    }

    public static int col(int position) {
        return (position - 1) % 3;
    }

    public static int row(int position) {
        return 2 - ((position - 1) / 3);
    }
}
