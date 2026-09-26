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
    static int gem = 7000000;
    static int wall_forest = 8000000;
    static int wall_cave = 9000000;
    static int wall_volcano = 10000000;
    static int wall_nuclear = 11000000;
    static int wall_arctic = 12000000;
    static int hole_down_nest = 13000000;
    static int flower_floor_pink = 14000000;
    static int flower_floor_blue = 15000000;
    static int flower_floor_yellow = 16000000;
    static int flower_pink = 17000000;
    static int flower_orange = 18000000;
    static int nautilus = 19000000;
    static int gems = 20000000;
    static int space_gems = 21000000;
    static int space_gem = 22000000;
    static int wall_space = 23000000;

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
    public static final int VOLCANO = 3;
    public static final int NUCLEAR = 4;
    public static final int ARCTIC = 5;
    public static final int BUBBLEGUM_LAND = 6;
    public static final int SPACE = 7;
    public static final int NIGHTMARE = 8;

    public static final int NEST_OFFSET = 100;

    public static final int FOREST_NEST = FOREST + NEST_OFFSET;
    public static final int CAVE_NEST = CAVE + NEST_OFFSET;
    public static final int VOLCANO_NEST = VOLCANO + NEST_OFFSET;

    public static final int ARCTIC_NEST = ARCTIC + NEST_OFFSET;
    public static final int BUBBLEGUM_LAND_NEST = BUBBLEGUM_LAND + NEST_OFFSET;
    public static final int SPACE_NEST = SPACE + NEST_OFFSET;

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
            {7, 5, -7, -9, 6},
            {1, 5, -1, -3, 6},
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
            {4, 5, -7, -9, 6, 0},
            {4, 5, -4, -6, 6, 0},
            {4, 5, -4, -6, 5, 9},
            {4, 5, -1, -3, 5, 3},
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

    private static final int[][] NEST_WALLS = {
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
            case VOLCANO:
                return volcano();
            case NUCLEAR:
                return nuclear();
            case ARCTIC:
                return arctic();
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
            case VOLCANO_NEST:
                return volcanoNest();
            case ARCTIC_NEST:
                return arcticNest();
            case BUBBLEGUM_LAND_NEST:
                return bubblegumLandNest();
            case SPACE_NEST:
                return spaceNest();
            default:
                throw new IllegalArgumentException("Unknown stage id: " + stageId);
        }
    }

    private static int tile(int item, int backgroundTile, int backgroundDirection, int foregroundTile, int direction) {
        return item * 1_000_000 + backgroundTile * 10_000 + backgroundDirection * 1_000 + foregroundTile * 10 + direction;
    }

    private static void drawPond(
            int[][] tiles,
            int startY,
            int startX,
            int backgroundTile,
            int[][] shape,
            int... internalTiles
    ) {
        int primaryTile = internalTiles.length > 0
                ? internalTiles[0]
                : backgroundTile;

        boolean hasSecondary = internalTiles.length > 1;
        int secondaryTile = hasSecondary
                ? internalTiles[1]
                : primaryTile;

        for (int y = 0; y < shape.length; y++) {
            for (int x = 0; x < shape[y].length; x++) {
                int value = shape[y][x];

                if (value == 0) {
                    continue;
                }

                boolean secondary = value < 0;

                int foregroundTile = secondary
                        ? secondaryTile
                        : primaryTile;

                int direction;

                if (secondary && !hasSecondary) {
                    direction = 5;
                } else {
                    direction = Math.abs(value);
                }

                tiles[startY + y][startX + x] =
                        tile(0, backgroundTile, 5, foregroundTile, direction);
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
            Arrays.fill(tiles[y], 25);
        }

        drawPond(tiles, 2, 2, WATER, POND_2, GRASS);
        drawPond(tiles, 10, 6, WATER, POND_3, GRASS, DEEP_WATER);
        drawPond(tiles, 24, 24, WATER, POND_4, GRASS);
        drawPond(tiles, 3, 18, WATER, POND_5, GRASS, DEEP_WATER);
        drawPond(tiles, 15, 13, WATER, POND_2, SAND);
        drawPond(tiles, 17, 23, WATER, POND_3, GRASS);
        drawPond(tiles, 24, 0, WATER, POND_4, SAND);

        tiles[27][2] += hole_down;
        tiles[26][2] += bird;

        randomizedItems(tiles, plant, 5, 1, 2, 28, 2, 28, 25031, 25032, 25033, 25034, 25035, 25036, 25037, 25038, 25039);
        randomizedItems(tiles, wall_forest, 30, 1, 2, 28, 2, 28, 25011, 25012, 25013, 25014, 25015, 25016, 25017, 25018, 25019);
        randomizedItems(tiles, flower_floor_blue, 50, 1, 2, 28, 2, 28, 25011, 25012, 25013, 25014, 25015, 25016, 25017, 25018, 25019);
        randomizedItems(tiles, flower_floor_pink, 20, 1, 2, 28, 2, 28, 25011, 25012, 25013, 25014, 25015, 25016, 25017, 25018, 25019);
        randomizedItems(tiles, flower_floor_yellow, 10, 1, 2, 28, 2, 28, 25011, 25012, 25013, 25014, 25015, 25016, 25017, 25018, 25019);

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
        tiles[23][8] += hole_down;
        tiles[26][25] += hole_down_nest;

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
        randomizedItems(tiles, plant, 10, 2, 2, 28, 2, 28, 55, 15);
        randomizedItems(tiles, flower_floor_yellow, 10, 2, 2, 28, 2, 28, 55, 15);
        randomizedItems(tiles, flower_floor_pink, 10, 2, 2, 28, 2, 28, 55, 15);
        randomizedItems(tiles, flower_floor_blue, 10, 2, 2, 28, 2, 28, 55, 15);
        randomizedItems(tiles, gem, 7, 2, 2, 28, 2, 28, 55041, 55042, 55043, 55044, 55046, 55047, 55048, 55049, 45);

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
        tiles[10][5] += hole_down_nest;
        tiles[20][15] += hole_down;

        randomizedItems(tiles, gem, 30, 1, 2, 28, 2, 28, 45);
        randomizedItems(tiles, gems, 20, 1, 2, 28, 2, 28, 45);

        return new Stage(tiles, 2, 2);
    }

    private static Stage volcano() {
        int[][] tiles = new int[30][30];
        for (int y = 0; y < 30; y++) {
            Arrays.fill(tiles[y], 105);
        }

        int[][] VOLCANO_WALLS = {
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
                VOLCANO_WALLS,
                wall_volcano,
                105
        );

        drawPond(tiles, 1, 1, LAVA, POND_6, VOLCANIC_SOIL);
        drawPond(tiles, 24, 25, LAVA, POND_1, VOLCANIC_SOIL);
        drawPond(tiles, 2, 14, LAVA, POND_3, VOLCANIC_SOIL);
        drawPond(tiles, 10, 3, LAVA, POND_5, VOLCANIC_SOIL);
        drawPond(tiles, 22, 2, LAVA, POND_2, VOLCANIC_SOIL);
        drawPond(tiles, 22, 15, LAVA, POND_4, VOLCANIC_SOIL);

        tiles[2][2] += hole_up;
        tiles[3][16] += hole_down;
        tiles[26][26] += hole_down_nest;

        randomizedItems(tiles, gem, 10, 1, 2, 28, 2, 28, 105091, 105092, 105093, 105094, 105095, 105096, 105097, 105098, 105099);
        randomizedItems(tiles, gems, 5, 1, 2, 28, 2, 28, 105091, 105092, 105093, 105094, 105095, 105096, 105097, 105098, 105099);

        return new Stage(tiles, 2, 2);
    }

    private static Stage nuclear() {
        int[][] tiles = new int[30][30];
        for (int y = 0; y < 30; y++) {
            Arrays.fill(tiles[y], 125);
        }

        drawPond(tiles, 0, 0, ACID_LAKE, POND_3, ACID_SOIL);
        drawPond(tiles, 11, 11, ACID_LAKE, POND_7, ACID_SOIL);
        tiles[1][2] += hole_up;
        tiles[15][23] += bird;

        return new Stage(tiles, 2, 2);
    }

    private static Stage arctic() {
        int[][] tiles = new int[30][30];
        for (int y = 0; y < 30; y++) {
            Arrays.fill(tiles[y], 75);
        }

        int[][] ARCTIC_WALLS = {
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, // 0
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 1
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 2
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 3
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 4
                {1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1}, // 5
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
                ARCTIC_WALLS,
                wall_space,
                75
        );

        drawPond(tiles, 1, 1, SNOW, POND_3, WATER, DEEP_WATER);
        drawPond(tiles, 2, 23, SNOW, POND_6, ICE);

        tiles[24][26] += hole_down;

//        randomizedItems(tiles, gems, 30, 1, 2, 28, 2, 28, 75);
//        randomizedItems(tiles, gem, 40, 1, 2, 28, 2, 28, 75);
//        randomizedItems(tiles, space_gems, 30, 1, 2, 28, 2, 28, 75);
//        randomizedItems(tiles, space_gem, 40, 1, 2, 28, 2, 28, 75);

        return new Stage(tiles, 2, 2);
    }

    private static Stage bubblegum_land() {
        int[][] tiles = new int[30][30];
        for (int y = 0; y < 30; y++) {
            Arrays.fill(tiles[y], 145);
        }

        int[][] BUBBLEGUM_WALLS = {
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
                BUBBLEGUM_WALLS,
                wall_nuclear,
                145
        );

        drawPond(tiles, 1, 1, BUBBLEGUM_LAKE, POND_6, BUBBLEGUM);
        drawPond(tiles, 1, 24, BUBBLEGUM_LAKE, POND_6, BUBBLEGUM);
        drawPond(tiles, 24, 1, BUBBLEGUM_LAKE, POND_6, BUBBLEGUM);
        drawPond(tiles, 15, 15, BUBBLEGUM_LAKE, POND_2, BUBBLEGUM);

        tiles[2][2] += hole_up;
        tiles[2][25] += hole_down;
        tiles[25][2] += hole_down_nest;

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

        tiles[2][2] += hole_up;
        tiles[13][16] += hole_down_nest;
        tiles[26][25] += hole_down;

        randomizedItems(tiles, space_gem, 50, 2, 2, 28, 2, 28, 155);
        randomizedItems(tiles, space_gems, 30, 2, 2, 28, 2, 28, 155);

        return new Stage(tiles, 2, 2);
    }


    private static Stage nightmare() {
        int[][] tiles = new int[30][30];
        for (int y = 0; y < 30; y++) {
            Arrays.fill(tiles[y], 175);
        }

        drawPond(tiles, 0, 0, BLOOD_LAKE, POND_3, VOLCANIC_SOIL);
        drawPond(tiles, 11, 11, BLOOD_LAKE, POND_7, VOLCANIC_SOIL);
        tiles[1][2] += hole_up;
        tiles[15][23] += bird;

        return new Stage(tiles, 2, 2);
    }

    private static Stage forestNest() {
        int[][] tiles = new int[10][10];
        int base_tile = 55;
        for (int y = 0; y < 10; y++) {
            Arrays.fill(tiles[y], base_tile);
        }

        drawWalls(
                tiles,
                0,
                0,
                NEST_WALLS,
                wall_forest,
                base_tile,
                15
        );

        drawPond(tiles, 5, 5, DEEP_GRASS, POND_6, SOIL);
        tiles[2][2] += hole_up;
        tiles[6][6] += bird;

        randomizedItems(tiles, -DEEP_GRASS * 10 + GRASS * 10, 5, 1, 2, 8, 2, 8, base_tile);
        randomizedItems(tiles, flower_floor_pink, 8, 2, 2, 8, 2, 8, base_tile, 15);
        randomizedItems(tiles, flower_floor_blue, 5, 2, 2, 8, 2, 8, base_tile, 15);
        randomizedItems(tiles, flower_floor_yellow, 1, 2, 2, 8, 2, 8, base_tile, 15);

        return new Stage(tiles, 2, 2);
    }

    private static Stage caveNest() {
        int[][] tiles = new int[10][10];
        int base_tile = 45;
        for (int y = 0; y < 10; y++) {
            Arrays.fill(tiles[y], base_tile);
        }

        drawWalls(
                tiles,
                0,
                0,
                NEST_WALLS,
                wall_cave,
                base_tile
        );

        drawPond(tiles, 5, 5, SOIL, POND_6, VOLCANIC_SOIL);
        tiles[2][2] += hole_up;
        tiles[6][6] += bird;

        randomizedItems(tiles, gem, 5, 2, 2, 8, 2, 8, base_tile);
        randomizedItems(tiles, gems, 3, 2, 2, 8, 2, 8, base_tile);

        return new Stage(tiles, 2, 2);
    }

    private static Stage volcanoNest() {
        int[][] tiles = new int[10][10];
        int base_tile = 105;
        for (int y = 0; y < 10; y++) {
            Arrays.fill(tiles[y], base_tile);
        }

        drawWalls(
                tiles,
                0,
                0,
                NEST_WALLS,
                wall_volcano,
                base_tile
        );

        drawPond(tiles, 5, 5, LAVA, POND_6, VOLCANIC_SOIL);
        tiles[2][2] += hole_up;
        tiles[6][6] += bird;

        return new Stage(tiles, 2, 2);
    }

    private static Stage arcticNest() {
        int[][] tiles = new int[10][10];
        int base_tile = 75;
        for (int y = 0; y < 10; y++) {
            Arrays.fill(tiles[y], base_tile);
        }

        drawWalls(
                tiles,
                0,
                0,
                NEST_WALLS,
                wall_arctic,
                base_tile
        );

        drawPond(tiles, 5, 5, SNOW, POND_6, ICE);
        tiles[2][2] += hole_up;
        tiles[6][6] += bird;

        return new Stage(tiles, 2, 2);
    }

    private static Stage bubblegumLandNest() {
        int[][] tiles = new int[10][10];
        int base_tile = 145;
        for (int y = 0; y < 10; y++) {
            Arrays.fill(tiles[y], base_tile);
        }

        drawWalls(
                tiles,
                0,
                0,
                NEST_WALLS,
                stone,
                base_tile
        );

        drawPond(tiles, 5, 5, BUBBLEGUM_LAKE, POND_6, BUBBLEGUM_LAND);
        tiles[2][2] += hole_up;
        tiles[6][6] += bird;

        return new Stage(tiles, 2, 2);
    }

    private static Stage spaceNest() {
        int[][] tiles = new int[10][10];
        int base_tile = 165;
        for (int y = 0; y < 10; y++) {
            Arrays.fill(tiles[y], base_tile);
        }

        drawWalls(
                tiles,
                0,
                0,
                NEST_WALLS,
                wall_space,
                base_tile
        );

        drawPond(tiles, 5, 5, SPACE_LAKE, POND_6, SPACE_SOIL);
        tiles[2][2] += hole_up;
        tiles[6][6] += bird;

        return new Stage(tiles, 2, 2);
    }
}
