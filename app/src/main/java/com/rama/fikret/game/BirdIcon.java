package com.rama.fikret.game;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.rama.fikret.R;

public final class BirdIcon {
    private static final int ATLAS_COLUMNS = 2;
    private static final int ATLAS_ROWS = 4;
    private static final int FRAME_IDLE = 1;

    private static Bitmap frame;

    private BirdIcon() {
    }

    public static synchronized Drawable drawable(Resources res) {
        if (frame == null) {
            SpriteSheet sheet = new SpriteSheet(res, R.drawable.bird, ATLAS_COLUMNS, ATLAS_ROWS);
            Rect r = sheet.frameRect(Direction.RIGHT.column, FRAME_IDLE);
            frame = Bitmap.createBitmap(sheet.getBitmap(), r.left, r.top, r.width(), r.height());
        }
        BitmapDrawable drawable = new BitmapDrawable(res, frame);
        drawable.setFilterBitmap(false);
        return drawable;
    }
}
