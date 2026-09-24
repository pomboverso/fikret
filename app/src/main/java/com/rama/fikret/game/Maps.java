package com.rama.fikret.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public final class Maps {
    static int stone = 1000000;
    static int hole_down = 2000000;
    static int hole_up = 3000000;
    static int bird = 4000000;
    static int plant = 5000000;
    static int beach_plant = 6000000;
    static int tree = 7000000;
    static int wall_forest = 8000000;
    static int wall_cave = 9000000;
    static int wall_volcan = 10000000;
    static int wall_nuclear = 11000000;
    static int wall_artic = 12000000;

    static final int GRASS = 1;
    static final int WATER = 2;
    static final int SAND = 3;
    static final int SOIL = 4;
    static final int DEEP_GRASS = 5;
    static final int DEEP_WATER = 6;
    static final int SNOW = 7;
    static final int ICE = 8;
    static final int VOLCANIC_SOIL = 9;
    static final int LAVA = 10;
    static final int ACID_SOIL = 11;
    static final int ACID_LAKE = 12;
    static final int BUBBLEGUM = 13;
    static final int BUBBLEGUM_LAKE = 14;
    static final int SPACE_SOIL = 15;
    static final int SPACE_LAKE = 16;
    static final int BLOOD_LAKE = 17;

    public static final int BEACH = 0;
    public static final int FOREST = 1;
    public static final int CAVE = 2;
    public static final int VOLCAN = 3;
    public static final int NUCLEAR = 4;
    public static final int ARTIC = 5;
    public static final int BUBBLEGUM_LAND = 6;
    public static final int SPACE = 7;
    public static final int NIGHTMARE = 8;
    // public static final int NEXT_STAGE = 1;

    private static final int[][] POND_1 = {
            {0, 7, 0},
            {7, 5, 9},
            {4, -5, 6},
            {1, 2, 3},
    };

    private static final int[][] POND_2 = {
            {0, 7, 8, 9},
            {0, 4, 5, 6},
            {7, 5, -5, 6},
            {1, 5, -5, 6},
            {0, 1, 2, 3}
    };

    private static final int[][] POND_3 = {
            {0, 7, 8, 8, 9},
            {0, 4, 5, 5, 6},
            {7, 5, -5, -5, 6},
            {1, 5, 5, -5, 6},
            {0, 1, 5, 5, 3},
            {0, 0, 1, 3, 0}
    };

    private static final int[][] POND_4 = {
            {0, 0, 7, 9, 0},
            {0, 0, 4, 5, 9},
            {7, 8, 5, 5, 6},
            {4, 5, -5, -5, 6},
            {1, 2, 5, 5, 3},
            {0, 0, 1, 3, 0}
    };

    private static final int[][] POND_5 = {
            {0, 0, 7, 9, 0, 0},
            {0, 7, 5, 5, 9, 0},
            {7, 5, 5, 5, 6, 0},
            {4, 5, -5, -5, 6, 0},
            {4, 5, -5, -5, 6, 0},
            {4, 5, -5, -5, 5, 9},
            {4, 5, -5, -5, 5, 3},
            {1, 2, 5, 5, 3, 0},
            {0, 0, 1, 3, 0, 0}
    };

    private static final int[][] POND_6 = {
            {7, 8, 9},
            {4, 5, 6},
            {1, 2, 3},
    };

    private static final int[][] POND_7 = {
            {7,8,9,0,0,0,0,0,7,8,8,8,8,8,9},
            {4,5,5,8,8,8,8,8,5,5,5,5,5,3,0},
            {1,5,5,5,5,5,5,5,5,5,5,5,6,0,0},
            {0,1,5,5,5,5,5,5,5,5,5,5,5,8,9},
            {0,0,4,5,5,5,5,5,5,5,5,5,5,5,6},
            {0,0,4,5,5,5,5,5,5,5,5,5,5,2,3},
            {0,0,4,5,5,5,5,5,5,5,5,5,6,0,0},
            {0,7,5,5,5,5,5,5,5,5,5,5,6,0,0},
            {7,5,5,5,5,5,5,2,2,2,5,5,5,9,0},
            {1,2,2,2,2,2,3,0,0,0,1,2,2,2,3},
    };

    private Maps() {
    }

    public static Stage get(int stageId) {
        switch (stageId) {
            case BEACH:
                return beach();
            case FOREST:
                return forest();
            case CAVE:
                return cave();
            case VOLCAN:
                return volcan();
            case NUCLEAR:
                return nuclear();
            case ARTIC:
                return artic();
            case BUBBLEGUM_LAND:
                return bubblegum_land();
            case SPACE:
                return space();
            case NIGHTMARE:
                return nightmare();
            default:
                throw new IllegalArgumentException("Unknown stage id: " + stageId);
        }
    }

    private static int tile(int item, int backgroundTile, int backgroundDirection, int foregroundTile, int direction) {
        return item * 1_000_000 + backgroundTile * 10_000 + backgroundDirection * 1_000 + foregroundTile * 10 + direction;
    }

    private static void drawPond(int[][] tiles, int startY, int startX, int backgroundTile, int[][] shape, int... internalTiles) {
        int primaryTile = internalTiles.length > 0 ? internalTiles[0] : backgroundTile;

        int secondaryTile = internalTiles.length > 1 ? internalTiles[1] : primaryTile;

        for (int y = 0; y < shape.length; y++) {
            for (int x = 0; x < shape[y].length; x++) {

                int value = shape[y][x];

                if (value == 0) {
                    continue;
                }

                boolean secondary = value < 0;
                int direction = Math.abs(value);

                int foregroundTile = secondary ? secondaryTile : primaryTile;

                tiles[startY + y][startX + x] = tile(0, backgroundTile, 5, foregroundTile, direction);
            }
        }
    }

    private static int drawItemLine(int[][] tiles, int item, int startY, int startX, int endY, int endX, int... allowedTiles) {
        int dy = Integer.compare(endY, startY);
        int dx = Integer.compare(endX, startX);

        int y = startY;
        int x = startX;
        int count = 0;

        while (true) {
            boolean allowed = false;

            for (int tile : allowedTiles) {
                if (tiles[y][x] == tile) {
                    allowed = true;
                    break;
                }
            }

            if (!allowed) {
                break;
            }

            tiles[y][x] += item;
            count++;

            if (y == endY && x == endX) {
                break;
            }

            y += dy;
            x += dx;
        }

        return count;
    }

    private static int randomizedItems(int[][] tiles, int item, int maxCount, int minDistance, int startY, int endY, int startX, int endX, int... allowedTiles) {
        ArrayList<int[]> candidates = new ArrayList<>();
        ArrayList<int[]> placedPositions = new ArrayList<>();

        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {

                boolean allowed = false;

                for (int tile : allowedTiles) {
                    if (tiles[y][x] == tile) {
                        allowed = true;
                        break;
                    }
                }

                if (allowed) {
                    candidates.add(new int[]{y, x});
                }
            }
        }

        Collections.shuffle(candidates);

        for (int[] pos : candidates) {

            int y = pos[0];
            int x = pos[1];

            boolean tooClose = false;

            if (minDistance > 0) {
                for (int[] placed : placedPositions) {

                    int distance = Math.max(Math.abs(y - placed[0]), Math.abs(x - placed[1]));

                    if (distance < minDistance) {
                        tooClose = true;
                        break;
                    }
                }
            }

            if (!tooClose) {
                tiles[y][x] += item;
                placedPositions.add(new int[]{y, x});
            }

            if (placedPositions.size() >= maxCount) {
                break;
            }
        }

        return placedPositions.size();
    }

    private static Stage beach() {
        int[][] tiles = new int[30][30];
        for (int y = 0; y < 30; y++) {
            Arrays.fill(tiles[y], 35);
        }

        drawPond(tiles, 2, 2, SAND, POND_2, WATER, DEEP_WATER);
        drawPond(tiles, 10, 6, SAND, POND_3, WATER, DEEP_WATER);
        drawPond(tiles, 18, 1, SAND, POND_4, WATER, DEEP_WATER);
        drawPond(tiles, 3, 18, SAND, POND_5, WATER, DEEP_WATER);
        drawPond(tiles, 15, 13, SAND, POND_2, WATER, DEEP_WATER);
        drawPond(tiles, 17, 23, SAND, POND_3, WATER, DEEP_WATER);
        drawPond(tiles, 24, 25, SAND, POND_4, GRASS);
        tiles[27][28] += hole_down;

        int birdMax = 20;

        randomizedItems(tiles, bird, birdMax, 2, 2, 28, 2, 28, 35021, 35022, 35023, 35024, 25, 35026, 35027, 35028, 35029);
        randomizedItems(tiles, plant, 30, 2, 2, 28, 2, 28, 15, 35);

        return new Stage(tiles, 2, 2);
    }

    private static Stage forest() {
        int[][] tiles = new int[30][30];
        for (int y = 0; y < 30; y++) {
            Arrays.fill(tiles[y], 55);
        }

        tiles[1][2] += hole_up;
        drawPond(tiles, 21, 7, DEEP_GRASS, POND_1, SOIL);
        tiles[23][8] = hole_down + 45;
        drawPond(tiles, 25, 24, DEEP_GRASS, POND_6, SOIL);
        tiles[26][25] = hole_down + 45;

        drawItemLine(tiles, wall_forest, 0, 6, 6, 6, 55, 15);
        drawItemLine(tiles, wall_forest, 8, 6, 29, 6, 55, 15);
        drawItemLine(tiles, wall_forest, 0, 13, 19, 13, 55, 15);
        drawItemLine(tiles, wall_forest, 21, 13, 29, 13, 55, 15);
        drawItemLine(tiles, wall_forest, 0, 20, 9, 20, 55, 15);
        drawItemLine(tiles, wall_forest, 11, 20, 29, 20, 55, 15);
        drawItemLine(tiles, wall_forest, 0, 26, 23, 26, 55, 15);
        drawItemLine(tiles, wall_forest, 25, 26, 29, 26, 55, 15);
        drawItemLine(tiles, wall_forest, 5, 0, 5, 2, 55, 15);
        drawItemLine(tiles, wall_forest, 5, 4, 5, 6, 55, 15);
        drawItemLine(tiles, wall_forest, 9, 7, 9, 9, 55, 15);
        drawItemLine(tiles, wall_forest, 9, 11, 9, 13, 55, 15);
        drawItemLine(tiles, wall_forest, 15, 0, 15, 2, 55, 15);
        drawItemLine(tiles, wall_forest, 15, 4, 15, 6, 55, 15);
        drawItemLine(tiles, wall_forest, 21, 14, 21, 16, 55, 15);
        drawItemLine(tiles, wall_forest, 21, 18, 21, 20, 55, 15);
        drawItemLine(tiles, wall_forest, 7, 21, 7, 23, 55, 15);
        drawItemLine(tiles, wall_forest, 7, 25, 7, 26, 55, 15);
        drawItemLine(tiles, wall_forest, 25, 7, 25, 9, 55, 15);
        drawItemLine(tiles, wall_forest, 25, 11, 25, 13, 55, 15);
        drawItemLine(tiles, wall_forest, 17, 21, 17, 22, 55, 15);
        drawItemLine(tiles, wall_forest, 17, 24, 17, 26, 55, 15);
        randomizedItems(tiles, -DEEP_GRASS * 10 + GRASS * 10, 90, 1, 2, 28, 2, 28, 55);
        randomizedItems(tiles, plant, 40, 2, 2, 28, 2, 28, 55, 15);

        return new Stage(tiles, 2, 2);
    }

    private static Stage cave() {
        int[][] tiles = new int[30][30];
        for (int y = 0; y < 30; y++) {
            Arrays.fill(tiles[y], 45);
        }

        drawItemLine(tiles, wall_cave, 6, 0, 6, 6, 45);
        drawItemLine(tiles, wall_cave, 6, 8, 6, 29, 45);
        drawItemLine(tiles, wall_cave, 12, 0, 12, 21, 45);
        drawItemLine(tiles, wall_cave, 12, 23, 12, 29, 45);
        drawItemLine(tiles, wall_cave, 18, 0, 18, 9, 45);
        drawItemLine(tiles, wall_cave, 18, 11, 18, 29, 45);
        drawItemLine(tiles, wall_cave, 24, 0, 24, 23, 45);
        drawItemLine(tiles, wall_cave, 24, 25, 24, 29, 45);
        drawItemLine(tiles, wall_cave, 0, 6, 2, 6, 45);
        drawItemLine(tiles, wall_cave, 4, 6, 5, 6, 45);
        drawItemLine(tiles, wall_cave, 7, 15, 8, 15, 45);
        drawItemLine(tiles, wall_cave, 10, 15, 11, 15, 45);
        drawItemLine(tiles, wall_cave, 13, 8, 14, 8, 45);
        drawItemLine(tiles, wall_cave, 16, 8, 17, 8, 45);
        drawItemLine(tiles, wall_cave, 19, 20, 20, 20, 45);
        drawItemLine(tiles, wall_cave, 22, 20, 23, 20, 45);
        drawItemLine(tiles, wall_cave, 25, 7, 27, 7, 45);
        drawItemLine(tiles, wall_cave, 29, 7, 29, 7, 45);
        drawPond(tiles, 8, 4, SOIL, POND_1, VOLCANIC_SOIL);
        drawPond(tiles, 19, 14, SOIL, POND_6, VOLCANIC_SOIL);
        tiles[1][2] += hole_up;
        tiles[10][5] = hole_down + 95;
        tiles[20][15] = hole_down + 95;
        randomizedItems(tiles, beach_plant, 20, 1, 2, 28, 2, 28, 45);

        return new Stage(tiles, 2, 2);
    }

    private static Stage volcan() {
        int[][] tiles = new int[30][30];
        for (int y = 0; y < 30; y++) {
            Arrays.fill(tiles[y], 105);
        }

        drawPond(tiles, 0, 0, LAVA, POND_2, VOLCANIC_SOIL);
        tiles[1][2] = hole_up + 95;
        drawPond(tiles, 11, 14, LAVA, POND_3, VOLCANIC_SOIL);
        tiles[13][16] = hole_down + 95;
        drawPond(tiles, 23, 23, LAVA, POND_4, VOLCANIC_SOIL);
        tiles[26][25] = hole_down + 95;
        drawPond(tiles, 10, 4, LAVA, POND_5, VOLCANIC_SOIL);
        drawPond(tiles, 22, 2, LAVA, POND_2, VOLCANIC_SOIL);
        drawPond(tiles, 2, 20, LAVA, POND_3, VOLCANIC_SOIL);
        drawPond(tiles, 24, 13, LAVA, POND_4, VOLCANIC_SOIL);

        drawItemLine(tiles, wall_volcan, 5, 3, 5, 11, 105);
        drawItemLine(tiles, wall_volcan, 5, 13, 5, 19, 105);
        drawItemLine(tiles, wall_volcan, 2, 11, 5, 11, 105);
        drawItemLine(tiles, wall_volcan, 7, 11, 10, 11, 105);
        drawItemLine(tiles, wall_volcan, 2, 25, 7, 25, 105);
        drawItemLine(tiles, wall_volcan, 9, 25, 12, 25, 105);
        drawItemLine(tiles, wall_volcan, 9, 0, 9, 3, 105);
        drawItemLine(tiles, wall_volcan, 9, 6, 9, 13, 105);
        drawItemLine(tiles, wall_volcan, 8, 17, 8, 24, 105);
        drawItemLine(tiles, wall_volcan, 8, 27, 8, 29, 105);
        drawItemLine(tiles, wall_volcan, 14, 0, 14, 7, 105);
        drawItemLine(tiles, wall_volcan, 14, 9, 14, 13, 105);
        drawItemLine(tiles, wall_volcan, 14, 18, 14, 23, 105);
        drawItemLine(tiles, wall_volcan, 14, 25, 14, 29, 105);
        drawItemLine(tiles, wall_volcan, 17, 5, 17, 12, 105);
        drawItemLine(tiles, wall_volcan, 17, 14, 17, 20, 105);
        drawItemLine(tiles, wall_volcan, 20, 0, 20, 5, 105);
        drawItemLine(tiles, wall_volcan, 20, 8, 20, 14, 105);
        drawItemLine(tiles, wall_volcan, 20, 17, 20, 22, 105);
        drawItemLine(tiles, wall_volcan, 20, 25, 20, 29, 105);
        drawItemLine(tiles, wall_volcan, 23, 7, 23, 12, 105);
        drawItemLine(tiles, wall_volcan, 23, 15, 23, 21, 105);
        drawItemLine(tiles, wall_volcan, 27, 0, 27, 8, 105);
        drawItemLine(tiles, wall_volcan, 27, 10, 27, 18, 105);
        drawItemLine(tiles, wall_volcan, 27, 21, 27, 29, 105);
        drawItemLine(tiles, wall_volcan, 6, 7, 12, 7, 105);
        drawItemLine(tiles, wall_volcan, 18, 3, 22, 3, 105);
        drawItemLine(tiles, wall_volcan, 21, 24, 26, 24, 105);
        return new Stage(tiles, 2, 2);
    }

    private static Stage nuclear() {
        int[][] tiles = new int[30][30];
        for (int y = 0; y < 30; y++) {
            Arrays.fill(tiles[y], 125);
        }

        drawPond(tiles, 0, 0, ACID_LAKE, POND_3, ACID_SOIL);
        tiles[1][2] = hole_up + 115;
        drawPond(tiles, 11, 14, ACID_LAKE, POND_2, ACID_SOIL);
        tiles[13][16] = hole_down + 115;
        drawPond(tiles, 23, 23, ACID_LAKE, POND_3, ACID_SOIL);
        tiles[26][25] = hole_down + 115;
        drawPond(tiles, 10, 4, ACID_LAKE, POND_4, ACID_SOIL);
        drawPond(tiles, 22, 2, ACID_LAKE, POND_3, ACID_SOIL);
        drawPond(tiles, 2, 20, ACID_LAKE, POND_6, ACID_SOIL);
        drawPond(tiles, 24, 13, ACID_LAKE, POND_1, ACID_SOIL);

        drawItemLine(tiles, wall_nuclear, 5, 3, 5, 10, 125);
        drawItemLine(tiles, wall_nuclear, 5, 12, 5, 19, 125);
        drawItemLine(tiles, wall_nuclear, 2, 10, 5, 10, 125);
        drawItemLine(tiles, wall_nuclear, 7, 10, 10, 10, 125);
        drawItemLine(tiles, wall_nuclear, 2, 26, 7, 26, 125);
        drawItemLine(tiles, wall_nuclear, 9, 26, 12, 26, 125);
        drawItemLine(tiles, wall_nuclear, 8, 0, 8, 3, 125);
        drawItemLine(tiles, wall_nuclear, 8, 6, 8, 13, 125);
        drawItemLine(tiles, wall_nuclear, 8, 17, 8, 24, 125);
        drawItemLine(tiles, wall_nuclear, 8, 27, 8, 29, 125);
        drawItemLine(tiles, wall_nuclear, 14, 0, 14, 6, 125);
        drawItemLine(tiles, wall_nuclear, 14, 8, 14, 13, 125);
        drawItemLine(tiles, wall_nuclear, 14, 18, 14, 23, 125);
        drawItemLine(tiles, wall_nuclear, 14, 25, 14, 29, 125);
        drawItemLine(tiles, wall_nuclear, 17, 5, 17, 12, 125);
        drawItemLine(tiles, wall_nuclear, 17, 14, 17, 21, 125);
        drawItemLine(tiles, wall_nuclear, 20, 0, 20, 5, 125);
        drawItemLine(tiles, wall_nuclear, 20, 8, 20, 14, 125);
        drawItemLine(tiles, wall_nuclear, 20, 17, 20, 22, 125);
        drawItemLine(tiles, wall_nuclear, 20, 25, 20, 29, 125);
        drawItemLine(tiles, wall_nuclear, 23, 7, 23, 12, 125);
        drawItemLine(tiles, wall_nuclear, 23, 15, 23, 21, 125);
        drawItemLine(tiles, wall_nuclear, 27, 0, 27, 8, 125);
        drawItemLine(tiles, wall_nuclear, 27, 10, 27, 18, 125);
        drawItemLine(tiles, wall_nuclear, 27, 21, 27, 29, 125);
        drawItemLine(tiles, wall_nuclear, 6, 7, 12, 7, 125);
        drawItemLine(tiles, wall_nuclear, 18, 3, 22, 3, 125);
        drawItemLine(tiles, wall_nuclear, 21, 24, 26, 24, 125);

        return new Stage(tiles, 2, 2);
    }

    private static Stage artic() {
        int[][] tiles = new int[30][30];
        for (int y = 0; y < 30; y++) {
            Arrays.fill(tiles[y], 75);
        }

        drawPond(tiles, 0, 0, SNOW, POND_3, WATER, DEEP_WATER);
        drawPond(tiles, 23, 23, SNOW, POND_3, ICE);

        drawItemLine(tiles, wall_artic, 0, 6, 5, 6, 75);
        drawItemLine(tiles, wall_artic, 7, 6, 13, 6, 75);
        drawItemLine(tiles, wall_artic, 15, 6, 21, 6, 75);
        drawItemLine(tiles, wall_artic, 23, 6, 29, 6, 75);
        drawItemLine(tiles, wall_artic, 0, 13, 7, 13, 75);
        drawItemLine(tiles, wall_artic, 9, 13, 17, 13, 75);
        drawItemLine(tiles, wall_artic, 19, 13, 25, 13, 75);
        drawItemLine(tiles, wall_artic, 27, 13, 29, 13, 75);
        drawItemLine(tiles, wall_artic, 0, 20, 4, 20, 75);
        drawItemLine(tiles, wall_artic, 6, 20, 12, 20, 75);
        drawItemLine(tiles, wall_artic, 14, 20, 20, 20, 75);
        drawItemLine(tiles, wall_artic, 22, 20, 29, 20, 75);
        drawItemLine(tiles, wall_artic, 0, 26, 6, 26, 75);
        drawItemLine(tiles, wall_artic, 8, 26, 14, 26, 75);
        drawItemLine(tiles, wall_artic, 16, 26, 22, 26, 75);
        drawItemLine(tiles, wall_artic, 24, 26, 29, 26, 75);
        drawItemLine(tiles, wall_artic, 6, 0, 6, 4, 75);
        drawItemLine(tiles, wall_artic, 6, 6, 6, 11, 75);
        drawItemLine(tiles, wall_artic, 6, 13, 6, 18, 75);
        drawItemLine(tiles, wall_artic, 6, 20, 6, 24, 75);
        drawItemLine(tiles, wall_artic, 6, 26, 6, 29, 75);
        drawItemLine(tiles, wall_artic, 12, 0, 12, 2, 75);
        drawItemLine(tiles, wall_artic, 12, 4, 12, 11, 75);
        drawItemLine(tiles, wall_artic, 12, 13, 12, 17, 75);
        drawItemLine(tiles, wall_artic, 12, 20, 12, 24, 75);
        drawItemLine(tiles, wall_artic, 12, 26, 12, 29, 75);
        drawItemLine(tiles, wall_artic, 18, 0, 18, 5, 75);
        drawItemLine(tiles, wall_artic, 18, 7, 18, 11, 75);
        drawItemLine(tiles, wall_artic, 18, 13, 18, 18, 75);
        drawItemLine(tiles, wall_artic, 18, 20, 18, 24, 75);
        drawItemLine(tiles, wall_artic, 18, 26, 18, 29, 75);
        drawItemLine(tiles, wall_artic, 24, 0, 24, 3, 75);
        drawItemLine(tiles, wall_artic, 24, 5, 24, 11, 75);
        drawItemLine(tiles, wall_artic, 24, 13, 24, 16, 75);
        drawItemLine(tiles, wall_artic, 24, 18, 24, 24, 75);
        drawItemLine(tiles, wall_artic, 24, 26, 24, 29, 75);
        drawItemLine(tiles, wall_artic, 2, 3, 4, 3, 75);
        drawItemLine(tiles, wall_artic, 9, 2, 9, 5, 75);
        drawItemLine(tiles, wall_artic, 3, 9, 5, 9, 75);
        drawItemLine(tiles, wall_artic, 8, 15, 8, 18, 75);
        drawItemLine(tiles, wall_artic, 3, 22, 5, 22, 75);
        drawItemLine(tiles, wall_artic, 9, 23, 9, 25, 75);
        drawItemLine(tiles, wall_artic, 14, 2, 16, 2, 75);
        drawItemLine(tiles, wall_artic, 14, 8, 17, 8, 75);
        drawItemLine(tiles, wall_artic, 15, 15, 17, 15, 75);
        drawItemLine(tiles, wall_artic, 14, 22, 16, 22, 75);
        drawItemLine(tiles, wall_artic, 20, 9, 23, 9, 75);
        drawItemLine(tiles, wall_artic, 20, 16, 23, 16, 75);
        drawItemLine(tiles, wall_artic, 20, 23, 23, 23, 75);
        drawItemLine(tiles, wall_artic, 26, 24, 28, 24, 75);

        return new Stage(tiles, 2, 2);
    }

    private static Stage bubblegum_land() {
        int[][] tiles = new int[30][30];
        for (int y = 0; y < 30; y++) {
            Arrays.fill(tiles[y], 145);
        }

        drawPond(tiles, 0, 0, BUBBLEGUM_LAKE, POND_3, BUBBLEGUM);
        drawPond(tiles, 11, 14, BUBBLEGUM_LAKE, POND_2, BUBBLEGUM);
        drawPond(tiles, 23, 23, BUBBLEGUM_LAKE, POND_3, BUBBLEGUM);
        drawPond(tiles, 10, 4, BUBBLEGUM_LAKE, POND_4, BUBBLEGUM);
        drawPond(tiles, 22, 2, BUBBLEGUM_LAKE, POND_3, BUBBLEGUM);
        drawPond(tiles, 2, 20, BUBBLEGUM_LAKE, POND_6, BUBBLEGUM);
        drawPond(tiles, 24, 13, BUBBLEGUM_LAKE, POND_1, BUBBLEGUM);
        tiles[1][2] = hole_up + 135;
        tiles[13][16] = hole_down + 135;
        tiles[26][25] = hole_down + 135;

        return new Stage(tiles, 2, 2);
    }

    private static Stage space() {
        int[][] tiles = new int[30][30];
        for (int y = 0; y < 30; y++) {
            Arrays.fill(tiles[y], 155);
        }

        drawPond(tiles, 11, 14, SPACE_SOIL, POND_2, SPACE_LAKE);
        drawPond(tiles, 23, 23, SPACE_SOIL, POND_3, VOLCANIC_SOIL);
        drawPond(tiles, 10, 4, SPACE_SOIL, POND_4, SPACE_LAKE);
        drawPond(tiles, 22, 2, SPACE_SOIL, POND_3, SPACE_LAKE);
        drawPond(tiles, 2, 20, SPACE_SOIL, POND_6, SPACE_LAKE);
        drawPond(tiles, 24, 13, SPACE_SOIL, POND_1, SPACE_LAKE);
        tiles[1][2] = hole_up + 155;
        tiles[13][16] = hole_down + 165;
        tiles[26][25] = hole_down + 95;

        return new Stage(tiles, 2, 2);
    }


    private static Stage nightmare() {
        int[][] tiles = new int[30][30];
        for (int y = 0; y < 30; y++) {
            Arrays.fill(tiles[y], 175);
        }

        drawPond(tiles, 0, 0, BLOOD_LAKE, POND_3, VOLCANIC_SOIL);
        drawPond(tiles, 11, 11, BLOOD_LAKE, POND_7, VOLCANIC_SOIL);
        tiles[1][2] = hole_up + 95;

        return new Stage(tiles, 2, 2);
    }
}
