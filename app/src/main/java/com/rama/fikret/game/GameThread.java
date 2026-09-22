package com.rama.fikret.game;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * Classic SurfaceView game loop: locks the canvas, lets GameView update
 * and render, then posts the frame, aiming for TARGET_FPS.
 */
public class GameThread extends Thread {
    private static final int TARGET_FPS = 60;
    private static final long FRAME_TIME_MS = 1000 / TARGET_FPS;

    private final SurfaceHolder surfaceHolder;
    private final GameView gameView;
    private volatile boolean running = false;

    public GameThread(SurfaceHolder surfaceHolder, GameView gameView) {
        this.surfaceHolder = surfaceHolder;
        this.gameView = gameView;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        long lastTime = System.currentTimeMillis();
        while (running) {
            long now = System.currentTimeMillis();
            long deltaMs = now - lastTime;
            lastTime = now;

            Canvas canvas = null;
            try {
                canvas = surfaceHolder.lockCanvas();
                if (canvas != null) {
                    synchronized (surfaceHolder) {
                        gameView.update(deltaMs);
                        gameView.render(canvas);
                    }
                }
            } finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }

            long frameDuration = System.currentTimeMillis() - now;
            long sleepTime = FRAME_TIME_MS - frameDuration;
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }
}
