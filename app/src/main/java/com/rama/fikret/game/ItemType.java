package com.rama.fikret.game;

public enum ItemType {
    NONE(0, -1, -1, false),

    // column 0
    HOLE_DOWN(2, 0, 0, false),
    HOLE_UP(3, 0, 1, false),
    PLANT(5, 0, 2, false),
    GEM(7, 0, 3, false),
    TREE(8, 0, 4, true),
    WALL_CAVE(9, 0, 5, true),
    WALL_VOLCANO(10, 0, 6, true),
    FLOWER_FLOOR_PINK(14, 0, 7, false),
    ALIEN_STONE_1(33, 0, 8, true),
    ALIEN_STONE_2(34, 0, 9, true),

    // column 1
    FLOWER_FLOOR_BLUE(15, 1, 0, false),
    FLOWER_FLOOR_YELLOW(16, 1, 1, false),
    GEMS(20, 1, 2, false),
    SPACE_GEMS(21, 1, 3, false),
    SPACE_GEM(22, 1, 4, false),
    WALL_ARCTIC(23, 1, 5, true),
    LAVA_STONES_01(24, 1, 6, false),
    LAVA_STONES_02(25, 1, 7, false),
    ALIEN_STONE_3(35, 1, 8, true),
    ALIEN_STONE_4(36, 1, 9, true),

    // column 2
    LAVA_STONES_03(26, 2, 0, false),
    LILYPOND_01(27, 2, 1, false),
    LILYPOND_02(28, 2, 2, false),
    WALL_SPACE(32, 2, 3, true),
    WALL_BUBBLEGUM(37, 2, 4, true),
    BUBBLES_1(38, 2, 5, false),
    WALL_ACID(40, 2, 6, true),
    WALL_BLOOD(41, 2, 7, true),
    BUBBLES_2(43, 2, 9, false),

    FARM_BUTTON(42, 2, 8, false),

    // logic-only, never drawn
    BIRD(4, -1, -1, false);

    public static final int SHEET_COLUMNS = 3;
    public static final int SHEET_ROWS = 10;

    public final int id;
    public final int spriteCol;
    public final int spriteRow;
    public final boolean blocksMovement;

    ItemType(int id, int spriteCol, int spriteRow, boolean blocksMovement) {
        this.id = id;
        this.spriteCol = spriteCol;
        this.spriteRow = spriteRow;
        this.blocksMovement = blocksMovement;
    }

    public boolean hasSprite() {
        return spriteCol >= 0 && spriteRow >= 0;
    }

    public static ItemType fromId(int id) {
        for (ItemType t : values()) {
            if (t.id == id) {
                return t;
            }
        }
        return NONE;
    }
}
