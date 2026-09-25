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
    static int hole_down_nest = 13000000;

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

    public static final int NEST_OFFSET = 100;

    public static final int FOREST_NEST = FOREST + NEST_OFFSET;
    public static final int CAVE_NEST = CAVE + NEST_OFFSET;
    public static final int VOLCAN_NEST = VOLCAN + NEST_OFFSET;
    public static final int NUCLEAR_NEST = NUCLEAR + NEST_OFFSET;
    public static final int ARTIC_NEST = ARTIC + NEST_OFFSET;
    public static final int BUBBLEGUM_LAND_NEST = BUBBLEGUM_LAND + NEST_OFFSET;
    public static final int SPACE_NEST = SPACE + NEST_OFFSET;
    public static final int NIGHTMARE_NEST = NIGHTMARE + NEST_OFFSET;

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
            {7, 8, 9, 0, 0, 0, 0, 0, 7, 8, 8, 8, 8, 8, 9},
            {4, 5, 5, 8, 8, 8, 8, 8, 5, 5, 5, 5, 5, 3, 0},
            {1, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 0, 0},
            {0, 1, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 8, 9},
            {0, 0, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6},
            {0, 0, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 2, 3},
            {0, 0, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 0, 0},
            {0, 7, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 0, 0},
            {7, 5, 5, 5, 5, 5, 5, 2, 2, 2, 5, 5, 5, 9, 0},
            {1, 2, 2, 2, 2, 2, 3, 0, 0, 0, 1, 2, 2, 2, 3},
    };

    private Maps() {
    }

    /**
     * Bare placeholder room for a nest stage: just the parent's floor
     * tile plus a HOLE_UP back out. Swap the body of each *Nest() method
     * below for a real layout whenever - the HOLE_UP always finds its
     * way back to the correct parent stage on its own (see NEST_OFFSET).
     */
    private static Stage nestRoom(int fillTile) {
        int[][] tiles = new int[30][30];
        for (int y = 0; y < 30; y++) {
            Arrays.fill(tiles[y], fillTile);
        }
        tiles[1][2] += hole_up;
        return new Stage(tiles, 2, 2);
    }

    private static Stage forestNest() {
        int[][] tiles = new int[10][10];
        for (int y = 0; y < 10; y++) {
            Arrays.fill(tiles[y], 55);
        }

        int[][] FOREST_WALLS = {
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 0, 0, 1, 1, 0, 1, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 1, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 1, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 1, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 1, 1, 0, 0, 0, 0, 0, 1, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 1, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        };

        drawWalls(
                tiles,
                0,
                0,
                FOREST_WALLS,
                wall_forest,
                55,
                15
        );

        drawPond(tiles, 5, 5, DEEP_GRASS, POND_6, SOIL);
        tiles[2][2] += hole_up;
        tiles[6][6] += bird;

        randomizedItems(tiles, -DEEP_GRASS * 10 + GRASS * 10, 5, 1, 2, 8, 2, 8, 55);
        randomizedItems(tiles, plant, 5, 2, 2, 8, 2, 8, 55, 15);

        return new Stage(tiles, 2, 2);
    }

    private static Stage caveNest() {
        return nestRoom(45);
    }

    private static Stage volcanNest() {
        return nestRoom(105);
    }

    private static Stage nuclearNest() {
        return nestRoom(125);
    }

    private static Stage articNest() {
        return nestRoom(75);
    }

    private static Stage bubblegumLandNest() {
        return nestRoom(145);
    }

    private static Stage spaceNest() {
        return nestRoom(155);
    }

    private static Stage nightmareNest() {
        return nestRoom(175);
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
            case FOREST_NEST:
                return forestNest();
            case CAVE_NEST:
                return caveNest();
            case VOLCAN_NEST:
                return volcanNest();
            case NUCLEAR_NEST:
                return nuclearNest();
            case ARTIC_NEST:
                return articNest();
            case BUBBLEGUM_LAND_NEST:
                return bubblegumLandNest();
            case SPACE_NEST:
                return spaceNest();
            case NIGHTMARE_NEST:
                return nightmareNest();
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

    private static int drawWalls(
            int[][] tiles,
            int startY,
            int startX,
            int[][] shape,
            int material,
            int... allowedTiles
    ) {
        int count = 0;

        for (int y = 0; y < shape.length; y++) {
            for (int x = 0; x < shape[y].length; x++) {

                if (shape[y][x] == 0) {
                    continue;
                }

                int tileY = startY + y;
                int tileX = startX + x;

                if (tileY < 0 || tileY >= tiles.length ||
                        tileX < 0 || tileX >= tiles[tileY].length) {
                    continue;
                }

                if (allowedTiles.length > 0) {
                    boolean allowed = false;

                    for (int tile : allowedTiles) {
                        if (tiles[tileY][tileX] == tile) {
                            allowed = true;
                            break;
                        }
                    }

                    if (!allowed) {
                        continue;
                    }
                }

                tiles[tileY][tileX] += material;
                count++;
            }
        }

        return count;
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

        drawPond(tiles, 21, 7, DEEP_GRASS, POND_1, SOIL);
        drawPond(tiles, 25, 24, DEEP_GRASS, POND_6, SOIL);
        tiles[2][2] += hole_up;
        tiles[23][8] = hole_down + 45;
        tiles[26][25] = hole_down_nest + 45;

        int[][] FOREST_WALLS = {
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, // 0
                {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 1
                {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 2
                {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 3
                {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 4
                {1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 5
                {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 6
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1}, // 7
                {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 8
                {1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 9
                {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 10
                {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 11
                {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 12
                {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 13
                {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 14
                {1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 15
                {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 16
                {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1}, // 17
                {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 18
                {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 19
                {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 20
                {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 21
                {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 22
                {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 23
                {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 24
                {1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 25
                {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 26
                {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 27
                {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 28
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},  // 29
        };

        drawWalls(
                tiles,
                0,
                0,
                FOREST_WALLS,
                wall_forest,
                55,
                15
        );

        randomizedItems(tiles, -DEEP_GRASS * 10 + GRASS * 10, 90, 1, 2, 28, 2, 28, 55);
        randomizedItems(tiles, plant, 40, 2, 2, 28, 2, 28, 55, 15);

        return new Stage(tiles, 2, 2);
    }

    private static Stage cave() {
        int[][] tiles = new int[30][30];
        for (int y = 0; y < 30; y++) {
            Arrays.fill(tiles[y], 45);
        }

        int[][] CAVE_WALLS = {
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, // 0
                {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 1
                {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 2
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 3
                {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 4
                {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 5
                {1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, // 6
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 7
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 8
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 9
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 10
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 11
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1}, // 12
                {1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 13
                {1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 14
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 15
                {1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 16
                {1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 17
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, // 18
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 19
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 20
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 21
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 22
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 23
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1}, // 24
                {1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 25
                {1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 26
                {1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 27
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 28
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}  // 29
        };

        drawWalls(
                tiles,
                0,
                0,
                CAVE_WALLS,
                wall_cave,
                45
        );

        drawPond(tiles, 8, 4, SOIL, POND_1, VOLCANIC_SOIL);
        drawPond(tiles, 19, 14, SOIL, POND_6, VOLCANIC_SOIL);
        tiles[2][2] += hole_up;
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

        int[][] VOLCAN_WALLS = {
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, // 0
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 1
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1}, // 2
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1}, // 3
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1}, // 4
                {1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1}, // 5
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1}, // 6
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1}, // 7
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1}, // 8
                {1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1}, // 9
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1}, // 10
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1}, // 11
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1}, // 12
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 13
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1}, // 14
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 15
                {1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 16
                {1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 17
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 18
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 19
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1}, // 20
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1}, // 21
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1}, // 22
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1}, // 23
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1}, // 24
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1}, // 25
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 26
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1}, // 27
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1}, // 28
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1} // 29
        };

        drawWalls(
                tiles,
                0,
                0,
                VOLCAN_WALLS,
                wall_cave,
                105
        );

        drawPond(tiles, 1, 1, LAVA, POND_6, VOLCANIC_SOIL);
        drawPond(tiles, 24, 25, LAVA, POND_1, VOLCANIC_SOIL);
        drawPond(tiles, 2, 14, LAVA, POND_3, VOLCANIC_SOIL);
        drawPond(tiles, 10, 3, LAVA, POND_5, VOLCANIC_SOIL);
        drawPond(tiles, 22, 2, LAVA, POND_2, VOLCANIC_SOIL);
        drawPond(tiles, 22, 15, LAVA, POND_4, VOLCANIC_SOIL);

        tiles[2][2] = hole_up + 95;
        tiles[3][16] = hole_down + 95;
        tiles[26][26] = hole_down + 95;

        return new Stage(tiles, 2, 2);
    }

    private static Stage nuclear() {
        int[][] tiles = new int[30][30];
        for (int y = 0; y < 30; y++) {
            Arrays.fill(tiles[y], 125);
        }

        int[][] NUCLEAR_WALLS = {
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, // 0
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 1
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 2
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 3
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 4
                {1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1}, // 5
                {1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 6
                {1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 7
                {1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 9
                {1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, // 8
                {1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 10
                {1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 11
                {1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 12
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 13
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1}, // 14
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 15
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 16
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 17
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 18
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 19
                {1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1}, // 20
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1}, // 21
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1}, // 22
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1}, // 23
                {1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1}, // 24
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1}, // 25
                {1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1}, // 26
                {1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1}, // 27
                {1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 28
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}  // 29
        };

        drawWalls(
                tiles,
                0,
                0,
                NUCLEAR_WALLS,
                wall_nuclear,
                125
        );

        drawPond(tiles, 1, 1, ACID_LAKE, POND_6, ACID_SOIL);
        drawPond(tiles, 1, 24, ACID_LAKE, POND_6, ACID_SOIL);
        drawPond(tiles, 24, 1, ACID_LAKE, POND_6, ACID_SOIL);
        drawPond(tiles, 15, 15, ACID_LAKE, POND_2, ACID_SOIL);

        tiles[2][2] = hole_up + 115;
        tiles[25][2] = hole_down + 115;
        tiles[2][25] = hole_down + 115;

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
        tiles[2][2] = hole_up + 135;
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
        drawPond(tiles, 3, 20, SPACE_SOIL, POND_6, SPACE_LAKE);
        drawPond(tiles, 24, 13, SPACE_SOIL, POND_1, SPACE_LAKE);

        tiles[2][2] = hole_up + 155;
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
