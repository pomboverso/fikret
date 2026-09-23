package com.rama.fikret.game;

import java.util.Arrays;

public final class Maps {
    private Maps() {
    }

    public static final int MEADOW = 0;
    // public static final int NEXT_STAGE = 1;

    public static Stage get(int stageId) {
        switch (stageId) {
            case MEADOW:
                return meadow();
            default:
                throw new IllegalArgumentException("Unknown stage id: " + stageId);
        }
    }

    private static Stage meadow() {
        int stone = 1000000;
        int hole_down = 2000000;
        int hole_up = 3000000;
        int bird = 4000000;
        int tree = 5000000;

        int[][] tiles = new int[30][30];
        for (int y = 0; y < 30; y++) {
            Arrays.fill(tiles[y], 35);
        }

        // POND 1 - Small / irregular
        tiles[2][5] = 35028;
        tiles[2][6] = 35028;
        tiles[2][7] = 35029;
        tiles[3][4] = 35027;
        tiles[3][5] = 25;
        tiles[3][6] = 25;
        tiles[3][7] = 35026;
        tiles[4][4] = 35024;
        tiles[4][5] = 25;
        tiles[4][6] = 25;
        tiles[4][7] = 35026;
        tiles[5][5] = 35021;
        tiles[5][6] = 35022;
        tiles[5][7] = 35023;

        // POND 2 - Medium, wider on the bottom
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
        tiles[5][16] = 25;
        tiles[5][17] = 25;
        tiles[5][18] = 35026;
        tiles[6][13] = 35027;
        tiles[6][14] = 25;
        tiles[6][15] = 25;
        tiles[6][16] = 25;
        tiles[6][17] = 25;
        tiles[6][18] = 35026;
        tiles[7][14] = 35021;
        tiles[7][15] = 35022;
        tiles[7][16] = 35022;
        tiles[7][17] = 35022;
        tiles[7][18] = 35023;

        // POND 3 - Large, irregular / almost L-shaped
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
        tiles[12][5] = 25;
        tiles[12][6] = 25;
        tiles[12][7] = 35026;
        tiles[13][3] = 35024;
        tiles[13][4] = 25;
        tiles[13][5] = 25;
        tiles[13][6] = 25;
        tiles[13][7] = 35026;
        tiles[14][4] = 35021;
        tiles[14][5] = 25;
        tiles[14][6] = 25;
        tiles[14][7] = 35026;
        tiles[15][5] = 35021;
        tiles[15][6] = 35022;
        tiles[15][7] = 35023;

        // POND 4 - Long, narrow, winding
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
        tiles[13][19] = 25;
        tiles[13][20] = 25;
        tiles[13][21] = 35026;
        tiles[14][18] = 35021;
        tiles[14][19] = 25;
        tiles[14][20] = 25;
        tiles[14][21] = 35026;
        tiles[15][19] = 35021;
        tiles[15][20] = 35022;
        tiles[15][21] = 35023;

        // POND 5 - Largest, irregular and asymmetrical
        tiles[21][10] = 35028;
        tiles[21][11] = 35028;
        tiles[21][12] = 35028;
        tiles[21][13] = 35028;
        tiles[21][14] = 35029;
        tiles[22][9] = 35027;
        tiles[22][10] = 25;
        tiles[22][11] = 25;
        tiles[22][12] = 25;
        tiles[22][13] = 25;
        tiles[22][14] = 35026;
        tiles[23][9] = 35024;
        tiles[23][10] = 25;
        tiles[23][11] = 25;
        tiles[23][12] = 25;
        tiles[23][13] = 25;
        tiles[23][14] = 35026;
        tiles[24][8] = 35027;
        tiles[24][9] = 25;
        tiles[24][10] = 25;
        tiles[24][11] = 25;
        tiles[24][12] = 25;
        tiles[24][13] = 25;
        tiles[24][14] = 35026;
        tiles[25][8] = 35024;
        tiles[25][9] = 25;
        tiles[25][10] = 25;
        tiles[25][11] = 25;
        tiles[25][12] = 25;
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
        tiles[24][26] = 25017;
        tiles[24][27] = 25019;
        tiles[24][29] = 25017;
        tiles[25][25] = 25017;
        tiles[25][26] = 15;
        tiles[25][27] = 15;
        tiles[25][28] = 25018;
        tiles[25][29] = 15;
        tiles[26][25] = 25011;
        tiles[26][26] = 15;
        tiles[26][27] = 15;
        tiles[26][28] = 15;
        tiles[26][29] = 25015;
        tiles[27][26] = 25014;
        tiles[27][27] = 15;
        tiles[27][28] = 15;
        tiles[27][29] = 15;
        tiles[28][26] = 25014;
        tiles[28][27] = 15;
        tiles[28][28] = 15;
        tiles[28][29] = 15;
        tiles[29][26] = 25014;
        tiles[29][27] = 15;
        tiles[29][28] = 15;
        tiles[29][29] = 15;

        // Stones
        tiles[1][3] += stone;
        tiles[4][9] += stone;
        tiles[6][21] += stone;
        tiles[8][7] += stone;
        tiles[9][26] += stone;
        tiles[12][1] += stone;
        tiles[14][9] += stone;
        tiles[15][24] += stone;
        tiles[17][7] += stone;
        tiles[19][18] += stone;
        tiles[21][2] += stone;
        tiles[22][16] += stone;
        tiles[25][5] += stone;
        tiles[27][16] += stone;

        tiles[27][28] += hole_down;

        tiles[25][28] += tree;
        tiles[26][29] += tree;
        tiles[26][27] += tree;

        return new Stage(tiles, 2, 2);
    }
}
