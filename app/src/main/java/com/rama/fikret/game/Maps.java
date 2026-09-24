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

    private Maps() {
    }

    public static final int BEACH = 0;
    public static final int FOREST = 1;
    public static final int CAVE = 2;
    public static final int VOLCAN = 3;
    // public static final int NEXT_STAGE = 1;

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
            default:
                throw new IllegalArgumentException("Unknown stage id: " + stageId);
        }
    }

    private static final int[][] POND_1 = {
            {0,7,0},
            {7,5,9},
            {4,-5,6},
            {1,2,3},
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
            {7,8,9},
            {4,5,6},
            {1,2,3},
    };

    private static int tile(
            int item,
            int backgroundTile,
            int backgroundDirection,
            int foregroundTile,
            int direction
    ) {
        return item * 1_000_000
                + backgroundTile * 10_000
                + backgroundDirection * 1_000
                + foregroundTile * 10
                + direction;
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

        int secondaryTile = internalTiles.length > 1
                ? internalTiles[1]
                : primaryTile;

        for (int y = 0; y < shape.length; y++) {
            for (int x = 0; x < shape[y].length; x++) {

                int value = shape[y][x];

                if (value == 0) {
                    continue;
                }

                boolean secondary = value < 0;
                int direction = Math.abs(value);

                int foregroundTile = secondary
                        ? secondaryTile
                        : primaryTile;

                tiles[startY + y][startX + x] =
                        tile(
                                0,
                                backgroundTile,
                                5,
                                foregroundTile,
                                direction
                        );
            }
        }
    }

    private static int drawItemLine(
            int[][] tiles,
            int item,
            int startY,
            int startX,
            int endY,
            int endX,
            int... allowedTiles
    ) {
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

    private static int randomizedItems(
            int[][] tiles,
            int item,
            int maxCount,
            int minDistance,
            int startY,
            int endY,
            int startX,
            int endX,
            int... allowedTiles
    ) {
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

        // Randomize candidates
        Collections.shuffle(candidates);

        for (int[] pos : candidates) {

            int y = pos[0];
            int x = pos[1];

            boolean tooClose = false;

            if (minDistance > 0) {
                for (int[] placed : placedPositions) {

                    int distance = Math.max(
                            Math.abs(y - placed[0]),
                            Math.abs(x - placed[1])
                    );

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

        drawPond(
                tiles,
                2,
                2,
                SAND,
                POND_2,
                WATER,
                DEEP_WATER
        );

        drawPond(
                tiles,
                10,
                6,
                SAND,
                POND_3,
                WATER,
                DEEP_WATER
        );

        drawPond(
                tiles,
                18,
                1,
                SAND,
                POND_4,
                WATER,
                DEEP_WATER
        );

        drawPond(
                tiles,
                3,
                18,
                SAND,
                POND_5,
                WATER,
                DEEP_WATER
        );

        drawPond(
                tiles,
                15,
                13,
                SAND,
                POND_2,
                WATER,
                DEEP_WATER
        );

        drawPond(
                tiles,
                17,
                23,
                SAND,
                POND_3,
                WATER,
                DEEP_WATER
        );

        drawPond(
                tiles,
                24,
                25,
                SAND,
                POND_4,
                GRASS
        );

        tiles[27][28] += hole_down;

        int birdMax = 20;

        randomizedItems(
                tiles,
                bird,
                birdMax,
                2,
                2, 28,
                2, 28,
                35021, 35022, 35023, 35024, 25, 35026, 35027, 35028, 35029
        );

        randomizedItems(
                tiles,
                plant,
                30,
                2,
                2, 28,
                2, 28,
                15, 35
        );

        return new Stage(tiles, 2, 2);
    }

    private static Stage forest() {
        int[][] tiles = new int[30][30];
        for (int y = 0; y < 30; y++) {
            Arrays.fill(tiles[y], 55);
        }

        tiles[1][2] += hole_up;

        drawPond(
                tiles,
                21,
                7,
                DEEP_GRASS,
                POND_1,
                SOIL
        );

        tiles[23][8] = hole_down + 45;

        drawPond(
                tiles,
                25,
                24,
                DEEP_GRASS,
                POND_6,
                SOIL
        );

        tiles[26][25] = hole_down + 45;

        drawItemLine(tiles, tree, 0, 5, 15, 5, 55, 15);
        drawItemLine(tiles, tree, 17, 5, 25, 5, 55, 15);
        drawItemLine(tiles, tree, 26, 0, 26, 2, 55, 15);
        drawItemLine(tiles, tree, 26, 4, 26, 10, 55, 15);
        drawItemLine(tiles, tree, 29, 11, 28, 11, 55, 15);
        drawItemLine(tiles, tree, 26, 11, 0, 11, 55, 15);
        drawItemLine(tiles, tree, 29, 20, 5, 20, 55, 15);
        drawItemLine(tiles, tree, 3, 20, 0, 20, 55, 15);

        randomizedItems(
                tiles,
                -DEEP_GRASS * 10 + GRASS * 10,
                90,
                1,
                2, 28,
                2, 28,
                55
        );

        randomizedItems(
                tiles,
                plant,
                40,
                2,
                2, 28,
                2, 28,
                55, 15
        );

        return new Stage(tiles, 2, 2);
    }

    private static Stage cave() {
        int[][] tiles = new int[30][30];
        for (int y = 0; y < 30; y++) {
            Arrays.fill(tiles[y], 45);
        }

        drawItemLine(tiles, stone, 0, 5, 15, 5, 45);
        drawItemLine(tiles, stone, 17, 5, 25, 5, 45);
        drawItemLine(tiles, stone, 26, 0, 26, 2, 45);
        drawItemLine(tiles, stone, 26, 4, 26, 10, 45);
        drawItemLine(tiles, stone, 29, 11, 28, 11, 45);
        drawItemLine(tiles, stone, 26, 11, 0, 11, 45);
        drawItemLine(tiles, stone, 29, 20, 5, 20, 45);
        drawItemLine(tiles, stone, 3, 20, 0, 20, 45);

        tiles[1][2] += hole_up;

        drawPond(
                tiles,
                21,
                7,
                SOIL,
                POND_1,
                VOLCANIC_SOIL
        );

        tiles[23][8] = hole_down + 95;

        drawPond(
                tiles,
                25,
                24,
                SOIL,
                POND_6,
                VOLCANIC_SOIL
        );

        tiles[26][25] = hole_down + 95;

        randomizedItems(
                tiles,
                beach_plant,
                20,
                1,
                2, 28,
                2, 28,
                45
        );

        return new Stage(tiles, 2, 2);
    }

    private static Stage volcan() {
        int[][] tiles = new int[30][30];
        for (int y = 0; y < 30; y++) {
            Arrays.fill(tiles[y], 105);
        }

        drawPond(
                tiles,
                0,
                0,
                LAVA,
                POND_2,
                VOLCANIC_SOIL
        );

        tiles[1][2] = hole_up + 95;

        drawPond(
                tiles,
                11,
                14,
                LAVA,
                POND_3,
                VOLCANIC_SOIL
        );

        tiles[13][16] = hole_down + 95;

        drawPond(
                tiles,
                23,
                23,
                LAVA,
                POND_4,
                VOLCANIC_SOIL
        );
        tiles[26][25] = hole_down + 95;

        drawPond(
                tiles,
                10,
                4,
                LAVA,
                POND_5,
                VOLCANIC_SOIL
        );

        drawPond(
                tiles,
                22,
                2,
                LAVA,
                POND_2,
                VOLCANIC_SOIL
        );

        drawPond(
                tiles,
                2,
                20,
                LAVA,
                POND_3,
                VOLCANIC_SOIL
        );

        drawPond(
                tiles,
                24,
                13,
                LAVA,
                POND_4,
                VOLCANIC_SOIL
        );

        return new Stage(tiles, 2, 2);
    }
}
