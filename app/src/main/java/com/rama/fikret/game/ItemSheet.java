package com.rama.fikret.game;

import android.content.res.Resources;

import com.rama.fikret.R;

public final class ItemSheet {
    private static SpriteSheet sheet;

    private ItemSheet() {
    }

    public static synchronized SpriteSheet get(Resources res) {
        if (sheet == null) {
            sheet = new SpriteSheet(res, R.drawable.items, ItemType.SHEET_COLUMNS, ItemType.SHEET_ROWS);
        }
        return sheet;
    }
}
