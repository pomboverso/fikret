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
            {4,5,6},
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

        // Place items
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

        // POND 1
        tiles[2][5] = 35028;
        tiles[2][6] = 35028;
        tiles[2][7] = 35029;
        tiles[3][4] = 35027;
        tiles[3][5] = 25;
        tiles[3][6] = 25;
        tiles[3][7] = 35026;
        tiles[4][4] = 35024;
        tiles[4][5] = 25;
        tiles[4][6] = 65;
        tiles[4][7] = 35026;
        tiles[5][5] = 35021;
        tiles[5][6] = 35022;
        tiles[5][7] = 35023;

        // POND 2
        tiles[3][15] = 35028;
        tiles[3][16] = 35028;
        tiles[3][17] = 35028;
        tiles[3][18] = 35029;
        tiles[4][14] = 35027;
        tiles[4][15] = 25;
        tiles[4][16] = 25;
        tiles[4][17] = 25;
        tiles[4][18] = 35026;
        tiles[5][14] = 35024;
        tiles[5][15] = 25;
        tiles[5][16] = 65;
        tiles[5][17] = 65;
        tiles[5][18] = 35026;
        tiles[6][13] = 35027;
        tiles[6][14] = 25;
        tiles[6][15] = 25;
        tiles[6][16] = 65;
        tiles[6][17] = 65;
        tiles[6][18] = 35026;
        tiles[7][14] = 35021;
        tiles[7][15] = 35022;
        tiles[7][16] = 35022;
        tiles[7][17] = 35022;
        tiles[7][18] = 35023;

        // POND 3
        tiles[10][4] = 35028;
        tiles[10][5] = 35028;
        tiles[10][6] = 35028;
        tiles[10][7] = 35029;
        tiles[11][3] = 35027;
        tiles[11][4] = 25;
        tiles[11][5] = 25;
        tiles[11][6] = 25;
        tiles[11][7] = 35026;
        tiles[12][3] = 35024;
        tiles[12][4] = 25;
        tiles[12][5] = 65;
        tiles[12][6] = 65;
        tiles[12][7] = 35026;
        tiles[13][3] = 35024;
        tiles[13][4] = 25;
        tiles[13][5] = 25;
        tiles[13][6] = 65;
        tiles[13][7] = 35026;
        tiles[14][4] = 35021;
        tiles[14][5] = 25;
        tiles[14][6] = 25;
        tiles[14][7] = 35026;
        tiles[15][5] = 35021;
        tiles[15][6] = 35022;
        tiles[15][7] = 35023;

        // POND 4
        tiles[11][18] = 35028;
        tiles[11][19] = 35028;
        tiles[11][20] = 35028;
        tiles[11][21] = 35029;
        tiles[12][17] = 35027;
        tiles[12][18] = 25;
        tiles[12][19] = 25;
        tiles[12][20] = 25;
        tiles[12][21] = 35026;
        tiles[13][17] = 35024;
        tiles[13][18] = 25;
        tiles[13][19] = 65;
        tiles[13][20] = 65;
        tiles[13][21] = 35026;
        tiles[14][18] = 35021;
        tiles[14][19] = 25;
        tiles[14][20] = 25;
        tiles[14][21] = 35026;
        tiles[15][19] = 35021;
        tiles[15][20] = 35022;
        tiles[15][21] = 35023;

        // POND 5
        tiles[21][10] = 35028;
        tiles[21][11] = 35028;
        tiles[21][12] = 35028;
        tiles[21][13] = 35028;
        tiles[21][14] = 35029;
        tiles[22][9] = 35027;
        tiles[22][10] = 25;
        tiles[22][11] = 65;
        tiles[22][12] = 25;
        tiles[22][13] = 25;
        tiles[22][14] = 35026;
        tiles[23][9] = 35024;
        tiles[23][10] = 25;
        tiles[23][11] = 65;
        tiles[23][12] = 65;
        tiles[23][13] = 25;
        tiles[23][14] = 35026;
        tiles[24][8] = 35027;
        tiles[24][9] = 25;
        tiles[24][10] = 25;
        tiles[24][11] = 65;
        tiles[24][12] = 65;
        tiles[24][13] = 25;
        tiles[24][14] = 35026;
        tiles[25][8] = 35024;
        tiles[25][9] = 25;
        tiles[25][10] = 25;
        tiles[25][11] = 25;
        tiles[25][12] = 65;
        tiles[25][13] = 25;
        tiles[25][14] = 35026;
        tiles[26][9] = 35021;
        tiles[26][10] = 25;
        tiles[26][11] = 25;
        tiles[26][12] = 25;
        tiles[26][13] = 35026;
        tiles[27][10] = 35021;
        tiles[27][11] = 35022;
        tiles[27][12] = 35022;
        tiles[27][13] = 35023;

        // Grass
        tiles[24][26] = 35017;
        tiles[24][27] = 35019;
        tiles[24][29] = 35017;
        tiles[25][25] = 35017;
        tiles[25][26] = 15;
        tiles[25][27] = 15;
        tiles[25][28] = 35018;
        tiles[25][29] = 15;
        tiles[26][25] = 35011;
        tiles[26][26] = 15;
        tiles[26][27] = 15;
        tiles[26][28] = 15;
        tiles[26][29] = 35015;
        tiles[27][26] = 35014;
        tiles[27][27] = 15;
        tiles[27][28] = 15;
        tiles[27][29] = 15;
        tiles[28][26] = 35014;
        tiles[28][27] = 15;
        tiles[28][28] = 15;
        tiles[28][29] = 15;
        tiles[29][26] = 35014;
        tiles[29][27] = 15;
        tiles[29][28] = 15;
        tiles[29][29] = 15;

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
                40,
                2,
                2, 28,
                2, 28,
                15, 35
        );

        randomizedItems(
                tiles,
                beach_plant,
                10,
                2,
                2, 28,
                2, 28,
                35
        );

        randomizedItems(
                tiles,
                stone,
                10,
                1,
                2, 28,
                2, 28,
                35
        );

        return new Stage(tiles, 2, 2);
    }

    private static Stage forest() {
        int[][] tiles = new int[30][30];
        for (int y = 0; y < 30; y++) {
            Arrays.fill(tiles[y], 55);
        }

        drawItemLine(tiles, tree, 0, 5, 15, 5, 55, 15);
        drawItemLine(tiles, tree, 17, 5, 25, 5, 55, 15);
        drawItemLine(tiles, tree, 26, 0, 26, 2, 55, 15);
        drawItemLine(tiles, tree, 26, 4, 26, 10, 55, 15);
        drawItemLine(tiles, tree, 29, 11, 28, 11, 55, 15);
        drawItemLine(tiles, tree, 26, 11, 0, 11, 55, 15);
        drawItemLine(tiles, tree, 29, 20, 5, 20, 55, 15);
        drawItemLine(tiles, tree, 3, 20, 0, 20, 55, 15);

        tiles[1][2] += hole_up;

        tiles[21][8] = 55047;
        tiles[22][7] = 55047;
        tiles[22][8] = 45;
        tiles[22][9] = 55049;
        tiles[23][7] = 55044;
        tiles[23][8] = hole_down + 45;
        tiles[23][9] = 45;
        tiles[23][10] = 55049;
        tiles[24][7] = 55041;
        tiles[24][8] = 55042;
        tiles[24][9] = 55042;
        tiles[24][10] = 55043;

        tiles[25][24] = 55047;
        tiles[25][25] = 55048;
        tiles[25][26] = 55049;
        tiles[26][24] = 55044;
        tiles[26][25] = hole_down + 45;
        tiles[26][26] = 55046;
        tiles[27][24] = 55041;
        tiles[27][25] = 55042;
        tiles[27][26] = 55043;

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

        tiles[21][8] = 45097;
        tiles[22][7] = 45097;
        tiles[22][8] = 95;
        tiles[22][9] = 45099;
        tiles[23][7] = 45094;
        tiles[23][8] = hole_down + 95;
        tiles[23][9] = 95;
        tiles[23][10] = 45099;
        tiles[24][7] = 45091;
        tiles[24][8] = 45092;
        tiles[24][9] = 45092;
        tiles[24][10] = 45093;

        tiles[25][24] = 45097;
        tiles[25][25] = 45098;
        tiles[25][26] = 45099;
        tiles[26][24] = 45094;
        tiles[26][25] = hole_down + 95;
        tiles[26][26] = 45096;
        tiles[27][24] = 45091;
        tiles[27][25] = 45092;
        tiles[27][26] = 45093;

        randomizedItems(
                tiles,
                -SOIL * 10 + DEEP_GRASS * 10 + plant,
                50,
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
