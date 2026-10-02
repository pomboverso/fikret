package com.rama.fikret.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameMap {
    public static final int TILE_SIZE = 64;

    private final MapCell[][] cells;
    private final int[][] raw;
    private final int rows;
    private final int cols;

    public GameMap(int[][] rawData) {
        this.rows = rawData.length;
        this.cols = rawData[0].length;
        this.cells = new MapCell[rows][cols];
        this.raw = new int[rows][cols];
        for (int r = 0; r < rows; r++) {
            if (rawData[r].length != cols) {
                throw new IllegalArgumentException("Map row " + r + " has a different length than row 0 - all rows must be the same length.");
            }
            for (int c = 0; c < cols; c++) {
                raw[r][c] = rawData[r][c];
                cells[r][c] = new MapCell(rawData[r][c]);
            }
        }
    }

    public boolean addItem(int row, int col, ItemType item) {
        if (!isInBounds(row, col) || cells[row][col].hasItem()) {
            return false;
        }
        raw[row][col] += item.id * 1000000;
        cells[row][col] = new MapCell(raw[row][col]);
        return true;
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

    public boolean isPassable(int row, int col) {
        if (!isInBounds(row, col)) {
            return false;
        }
        return !cells[row][col].item.blocksMovement;
    }

    public boolean isWalkable(int row, int col, boolean canSwimNonWater) {
        if (!isPassable(row, col)) {
            return false;
        }
        return canSwimNonWater || !cells[row][col].isNonWaterLiquid();
    }

    public boolean canStep(int row, int col, int dx, int dy, boolean canSwimNonWater) {
        if (dx == 0 && dy == 0) {
            return false;
        }
        if (!isWalkable(row + dy, col + dx, canSwimNonWater)) {
            return false;
        }
        if (dx != 0 && dy != 0) {
            boolean horizontalSideOpen = isWalkable(row, col + dx, canSwimNonWater);
            boolean verticalSideOpen = isWalkable(row + dy, col, canSwimNonWater);
            if (!horizontalSideOpen && !verticalSideOpen) {
                return false;
            }
        }
        return true;
    }

    public int[] randomFreeCell(Random random, boolean canSwimNonWater, int avoidRow, int avoidCol) {
        List<int[]> candidates = new ArrayList<>();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (r == avoidRow && c == avoidCol) {
                    continue;
                }
                MapCell cell = cells[r][c];
                if (cell.hasItem()) {
                    continue;
                }
                if (!canSwimNonWater && cell.isNonWaterLiquid()) {
                    continue;
                }
                candidates.add(new int[]{r, c});
            }
        }
        if (candidates.isEmpty()) {
            return null;
        }
        return candidates.get(random.nextInt(candidates.size()));
    }

    public boolean isLiquidAt(float worldX, float worldY) {
        int col = (int) Math.floor(worldX / TILE_SIZE);
        int row = (int) Math.floor(worldY / TILE_SIZE);
        MapCell cell = getCell(row, col);
        return cell != null && cell.isLiquid();
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
