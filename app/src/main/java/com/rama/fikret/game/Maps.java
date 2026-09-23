package com.rama.fikret.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public final class Maps {
    static int stone = 1000000;
    static int hole_down = 2000000;
    static int hole_up = 3000000;
    static int bird = 4000000;
    static int plant = 5000000;
    static int beach_plant = 6000000;

    static int grass = 10;
    static int deep_grass = 50;

    private Maps() {
    }

    public static final int BEACH = 0;
    public static final int FOREST = 1;
    // public static final int NEXT_STAGE = 1;

    public static Stage get(int stageId) {
        switch (stageId) {
            case BEACH:
                return beach();
            case FOREST:
                return forest();
            default:
                throw new IllegalArgumentException("Unknown stage id: " + stageId);
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

//        int birdMax = 20;

//        randomizedItems(
//                tiles,
//                bird,
//                birdMax,
//                2,
//                2, 28,
//                2, 28,
//                35021, 35022, 35023, 35024, 25, 35026, 35027, 35028, 35029
//        );

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

        randomizedItems(
                tiles,
                -deep_grass + grass,
                350,
                1,
                2, 28,
                2, 28,
                55
        );

        drawItemLine(tiles, stone, 5, 5, 5, 24, 55, 15);

        randomizedItems(
                tiles,
                plant,
                40,
                2,
                2, 28,
                2, 28,
                55, 15
        );

//        randomizedItems(
//                tiles,
//                stone,
//                10,
//                1,
//                2, 28,
//                2, 28,
//                55, 15
//        );


        return new Stage(tiles, 2, 2);
    }
}
