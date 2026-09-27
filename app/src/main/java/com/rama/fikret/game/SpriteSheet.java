package com.rama.fikret.game;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class SpriteSheet {
    private final Bitmap bitmap;
    private final int frameWidth;
    private final int frameHeight;

    public SpriteSheet(Resources res, int resId, int columns, int rows) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inScaled = false;
        this.bitmap = BitmapFactory.decodeResource(res, resId, opts);
        if (this.bitmap == null) {
            throw new IllegalStateException("Could not decode drawable resource id " + resId);
        }
        this.frameWidth = bitmap.getWidth() / columns;
        this.frameHeight = bitmap.getHeight() / rows;
    }

    public Rect frameRect(int col, int row) {
        int x = col * frameWidth;
        int y = row * frameHeight;
        return new Rect(x, y, x + frameWidth, y + frameHeight);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}


