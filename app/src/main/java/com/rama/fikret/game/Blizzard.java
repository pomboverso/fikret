package com.rama.fikret.game;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.rama.fikret.R;

public class Blizzard {
    private static final float SPEED_X = 200f;
    private static final float SPEED_Y = 100f;

    private final Bitmap bitmap;
    private final int tileSize;
    private final Paint paint = new Paint();
    private float offsetX;
    private float offsetY;

    public Blizzard(Resources res) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inScaled = false;
        bitmap = BitmapFactory.decodeResource(res, R.drawable.blizzard, opts);
        tileSize = bitmap.getWidth();
        paint.setFilterBitmap(false);
    }

    public void update(long deltaMs) {
        float seconds = deltaMs / 1000f;
        offsetX = wrap(offsetX + SPEED_X * seconds);
        offsetY = wrap(offsetY + SPEED_Y * seconds);
    }

    private float wrap(float value) {
        value %= tileSize;
        return value < 0 ? value + tileSize : value;
    }

    public void draw(Canvas canvas, int width, int height) {
        int startX = (int) offsetX - tileSize;
        int startY = (int) offsetY - tileSize;
        for (int x = startX; x < width; x += tileSize) {
            for (int y = startY; y < height; y += tileSize) {
                canvas.drawBitmap(bitmap, x, y, paint);
            }
        }
    }
}
