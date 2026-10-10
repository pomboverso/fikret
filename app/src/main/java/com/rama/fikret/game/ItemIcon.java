package com.rama.fikret.game;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public enum ItemIcon {
    SLEEP(0),
    SONAR(1),
    TELEPORT(2),
    TELEPORT_HOME(3),
    THUNDER(4),
    DIAMOND(5),
    DIVE(6),
    HEALING(7),
    CANDLE(8),
    STAR(9);

    private static final int SPRITE_COLUMN = 2;

    private final int row;

    ItemIcon(int row) {
        this.row = row;
    }

    public Drawable drawable(Resources res) {
        SpriteSheet sheet = ItemSheet.get(res);
        Rect r = sheet.frameRect(SPRITE_COLUMN, row);
        Bitmap icon = Bitmap.createBitmap(sheet.getBitmap(), r.left, r.top, r.width(), r.height());
        return new BitmapDrawable(res, icon);
    }
}
