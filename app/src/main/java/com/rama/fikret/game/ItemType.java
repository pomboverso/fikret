package com.rama.fikret.game;

import com.rama.fikret.R;

public enum ItemType {
    NONE(0, 0, false),
    STONE(1, R.drawable.stone, true),
    HOLE_DOWN(2, R.drawable.hole, false),
    HOLE_UP(3, R.drawable.hole_up, false),
    BIRD(4, 0, false),
    PLANT(5, R.drawable.plant, false),
    BEACH_PLANT(6, R.drawable.beach_plant, false),
    TREE(7, R.drawable.tree, true),
    WALL_FOREST(8, R.drawable.wall_forest, true),
    WALL_CAVE(9, R.drawable.wall_cave, true),
    WALL_VOLCAN(10, R.drawable.wall_volcan, true),
    WALL_NUCLEAR(11, R.drawable.wall_nuclear, true),
    WALL_ARTIC(12, R.drawable.wall_artic, true);

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
