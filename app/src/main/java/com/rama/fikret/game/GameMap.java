package com.rama.fikret.game;

/**
 * A parsed map: a grid of {@link MapCell}s built from your raw int[][]
 * map-creation array.
 *
 * cells[row][col] - row 0 is the top row of the map, row increases
 * downward, col increases to the right (standard screen/array orientation).
 */
public class GameMap {

    /** Logical tile size used for world-space layout and on-screen size.
     *  This is independent of the actual pixel size of the source art -
     *  SpriteSheet figures out the real source frame size from the bitmap
     *  itself, so this can stay 64 even if the art changes resolution. */
    public static final int TILE_SIZE = 64;

    private final MapCell[][] cells;
    private final int rows;
    private final int cols;

    public GameMap(int[][] rawData) {
        this.rows = rawData.length;
        this.cols = rawData[0].length;
        this.cells = new MapCell[rows][cols];
        for (int r = 0; r < rows; r++) {
            if (rawData[r].length != cols) {
                throw new IllegalArgumentException("Map row " + r + " has a different length than row 0 - all rows must be the same length.");
            }
            for (int c = 0; c < cols; c++) {
                cells[r][c] = new MapCell(rawData[r][c]);
            }
        }
    }

    public MapCell getCell(int row, int col) {
        if (!isInBounds(row, col)) {
            return null;
        }
        return cells[row][col];
    }

    public boolean isInBounds(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    /** False if out of bounds, or if the cell's item blocks movement
     *  (e.g. a stone). Used by Goose (and could be reused by any other
     *  mover) instead of isInBounds() alone when deciding whether a step
     *  is allowed. */
    public boolean isPassable(int row, int col) {
        if (!isInBounds(row, col)) {
            return false;
        }
        return !cells[row][col].item.blocksMovement;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int getWidthPx() {
        return cols * TILE_SIZE;
    }

    public int getHeightPx() {
        return rows * TILE_SIZE;
    }
}
