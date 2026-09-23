package com.rama.fikret.game;

import com.rama.fikret.R;

public enum ItemType {
    NONE(0, 0, false),
    STONE(1, R.drawable.stone, true),
    HOLE_DOWN(2, R.drawable.hole, false),
    HOLE_UP(3, R.drawable.hole_up, false),
    BIRD(4, 0, false);

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
