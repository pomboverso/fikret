package com.rama.fikret.game;

import com.rama.fikret.R;

public enum ItemType {
    NONE(0, 0, false),
    STONE(1, R.drawable.stone, true),
    HOLE_DOWN(2, R.drawable.hole_down, false),
    HOLE_UP(3, R.drawable.hole_up, false),
    BIRD(4, 0, false),
    PLANT(5, R.drawable.plant, false),
    BEACH_PLANT(6, R.drawable.beach_plant, false),
    GEM(7, R.drawable.gem, false),
    WALL_FOREST(8, R.drawable.wall_forest, true),
    WALL_CAVE(9, R.drawable.wall_cave, true),
    WALL_VOLCAN(10, R.drawable.wall_volcan, true),
    WALL_NUCLEAR(11, R.drawable.wall_nuclear, true),
    WALL_OLD(12, R.drawable.wall_artic, true),
    HOLE_DOWN_NEST(13, R.drawable.hole_down, false),
    FLOWER_FLOOR_PINK(14, R.drawable.flower_floor_pink, false),
    FLOWER_FLOOR_BLUE(15, R.drawable.flower_floor_blue, false),
    FLOWER_FLOOR_YELLOW(16, R.drawable.flower_floor_yellow, false),
    FLOWER_PINK(17, R.drawable.flower_pink, false),
    FLOWER_ORANGE(18, R.drawable.flower_orange, false),
    NAUTILUS(19, R.drawable.nautilus, false),
    GEMS(20, R.drawable.gem_group, false),
    SPACE_GEMS(21, R.drawable.space_gem_group, false),
    SPACE_GEM(22, R.drawable.space_gem, false),
    WALL_ARCTIC(23, R.drawable.wall_space, true),
    LAVA_STONES_01(24, R.drawable.lava_stone_01, false),
    LAVA_STONES_02(25, R.drawable.lava_stone_02, false),
    LAVA_STONES_03(26, R.drawable.lava_stone_03, false),
    LILYPOND_01(27, R.drawable.lilypad_01, false),
    LILYPOND_02(28, R.drawable.lilypad_02, false),
    DIVE_TO_ARCTIC(29, 0, false),
    DIVE_TO_BEACH_CAVE(30, 0, false),
    DIVE_TO_BEACH(31, 0, false),
    WALL_SPACE(32, R.drawable.alien_wall, true);

    public final int id;
    public final int drawableRes;
    public final boolean blocksMovement;

    ItemType(int id, int drawableRes, boolean blocksMovement) {
        this.id = id;
        this.drawableRes = drawableRes;
        this.blocksMovement = blocksMovement;
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
