package com.rama.fikret.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.EnumMap;

/**
 * The game surface: owns the map, the goose, the bird, the camera/zoom,
 * and all input.
 *
 * The goose moves one whole tile at a time, locked to the grid, in 8
 * directions (diagonals included) - see Goose. Controls:
 *  - Touch: put a finger down ANYWHERE and a 3x3 grid with a circle in its
 *    middle appears under it (see SwipeJoystick). Drag the circle into one
 *    of the outer cells and the goose walks that way until you let go or
 *    drag back to the middle. Two fingers = pinch-to-zoom instead.
 *  - Keyboard / d-pad: arrow keys or WASD; press two at once for a diagonal.
 *    Numpad 1-9 (5 = stop) sets a direction directly.
 *  - Analog stick / gamepad d-pad hat (API 12+ only, see GamepadAxes).
 * All sources are combined, so they can be mixed freely. Goose itself
 * refuses to step onto a blocking item like a stone, and won't squeeze
 * diagonally between two of them (see GameMap.canStep()).
 *
 * The bird: if the map has an ItemType.BIRD cell, findBirdSpawn() finds it
 * once at load time and a Bird entity is spawned there in surfaceCreated().
 * It stands still until the goose steps onto its tile, then follows one
 * tile behind for the rest of the stage (see updateBird()/Bird).
 *
 * Pinch-to-zoom is wired up (ScaleGestureDetector, isolated in
 * PinchZoomDetector so it never loads on devices below API 8).
 *
 * This is deliberately a starting point. Stages live in {@link Maps} - add
 * one there and pass its id to this view's constructor (or via
 * GameActivity.EXTRA_STAGE) to switch maps.
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private GameThread thread;
    private GameMap map;
    private Goose goose;
    private Bird bird;
    private Stage stage;
    private int birdSpawnRow = -1, birdSpawnCol = -1;
    private int lastGooseRow, lastGooseCol;
    private final EnumMap<TileType, SpriteSheet> tileSheets = new EnumMap<TileType, SpriteSheet>(TileType.class);
    private final EnumMap<ItemType, Bitmap> itemBitmaps = new EnumMap<ItemType, Bitmap>(ItemType.class);
    private final Paint backgroundPaint = new Paint();
    private final Rect reusableSrc = new Rect();
    private final Rect reusableDst = new Rect();
    private final SwipeJoystick joystick;

    private int cameraX, cameraY;

    // --- Zoom ----------------------------------------------------------
    private static final float MIN_ZOOM = 1f;
    private static final float MAX_ZOOM = 4f;
    private float zoom = 2f; // default a bit zoomed in - 64px tiles read as small otherwise
    private PinchZoomDetector pinchZoomDetector; // null below API 8

    // --- Input state -----------------------------------------------------
    // Written on the UI thread, read every frame on GameThread - hence volatile.
    // Keyboard: arrow keys / WASD, combined additively for diagonals.
    private volatile boolean keyLeft, keyRight, keyUp, keyDown;
    // Keyboard: numpad 1-9 sets the vector directly (5 = stop).
    private volatile int activeNumpadKeyCode = 0;
    private volatile int numpadDx, numpadDy;
    // Analog stick / gamepad hat, already reduced to -1/0/1 per axis.
    private volatile int stickDx, stickDy;
    // Touch: true once a second finger lands, until every finger is lifted -
    // that gesture is a pinch, and must not also drive the joystick.
    private boolean pinching;

    public GameView(Context context) {
        this(context, Maps.NUCLEAR);
    }

    public GameView(Context context, int stageId) {
        super(context);
        getHolder().addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);

        backgroundPaint.setColor(Color.BLACK);
        joystick = new SwipeJoystick(getResources().getDisplayMetrics().density);

        if (Build.VERSION.SDK_INT >= 8) {
            pinchZoomDetector = new PinchZoomDetector(context, new PinchZoomDetector.Listener() {
                @Override
                public void onZoom(float scaleFactor, float focusX, float focusY) {
                    setZoom(zoom * scaleFactor);
                }
            });
        }

        stage = Maps.get(stageId);
        map = new GameMap(stage.tiles);
        loadTileSheets();
        loadItemBitmaps();
        findBirdSpawn();
    }

    private void loadTileSheets() {
        for (TileType type : TileType.values()) {
            if (type == TileType.NONE) {
                continue;
            }
            tileSheets.put(type, new SpriteSheet(getResources(), type.atlasRes, TileType.ATLAS_COLUMNS, TileType.ATLAS_ROWS));
        }
    }

    /** Stone/hole/hole-up are plain static images (no direction, no
     *  animation) - decoded straight to a Bitmap rather than through
     *  SpriteSheet. BIRD is excluded on purpose: it's a moving entity, not
     *  something drawn from map data every frame (see findBirdSpawn()). */
    private void loadItemBitmaps() {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inScaled = false;
        for (ItemType type : ItemType.values()) {
            if (type == ItemType.NONE || type == ItemType.BIRD || type.drawableRes == 0) {
                continue;
            }
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), type.drawableRes, opts);
            itemBitmaps.put(type, bitmap);
        }
    }

    /** Scans the map once for the (first) BIRD item cell - that's where
     *  surfaceCreated() spawns the actual Bird entity. Supports one bird
     *  per stage for now; extend this to a list if a stage ever needs more. */
    private void findBirdSpawn() {
        for (int r = 0; r < map.getRows(); r++) {
            for (int c = 0; c < map.getCols(); c++) {
                if (map.getCell(r, c).item == ItemType.BIRD) {
                    birdSpawnRow = r;
                    birdSpawnCol = c;
                    return;
                }
            }
        }
    }

    private void setZoom(float newZoom) {
        zoom = Math.max(MIN_ZOOM, Math.min(newZoom, MAX_ZOOM));
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        goose = new Goose(getResources(), stage.spawnRow, stage.spawnCol);
        lastGooseRow = goose.getRow();
        lastGooseCol = goose.getCol();
        if (birdSpawnRow >= 0) {
            bird = new Bird(getResources(), birdSpawnRow, birdSpawnCol);
        }
        thread = new GameThread(holder, this);
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Nothing to do yet: the camera reads getWidth()/getHeight() live each frame.
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        clearInput();
        if (thread == null) {
            return;
        }
        thread.setRunning(false);
        boolean retry = true;
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException ignored) {
            }
        }
    }

    // --- Touch input: floating 3x3 swipe joystick ------------------------

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (pinchZoomDetector != null) {
            pinchZoomDetector.onTouchEvent(event);
        }

        // ACTION_MASK strips the pointer index that multi-touch packs into
        // getAction() (both exist since API 5; getActionMasked() is API 8).
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                pinching = false;
                joystick.begin(event.getX(), event.getY());
                return true;
            case MotionEvent.ACTION_POINTER_DOWN:
                // A second finger means this is a pinch, not a move request -
                // drop the joystick, and stay out of it until all fingers lift.
                pinching = true;
                joystick.end();
                return true;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() > 1) {
                    pinching = true;
                    joystick.end();
                } else if (!pinching) {
                    joystick.move(event.getX(), event.getY());
                }
                return true;
            case MotionEvent.ACTION_POINTER_UP:
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                pinching = false;
                joystick.end();
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    // --- Keyboard input: arrows/WASD + numpad 1-9 -----------------------

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_A:
                keyLeft = true;
                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_D:
                keyRight = true;
                return true;
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_W:
                keyUp = true;
                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_S:
                keyDown = true;
                return true;
            default:
                if (keyCode >= KeyEvent.KEYCODE_NUMPAD_1 && keyCode <= KeyEvent.KEYCODE_NUMPAD_9) {
                    int numpadPosition = keyCode - KeyEvent.KEYCODE_NUMPAD_0;
                    activeNumpadKeyCode = keyCode;
                    numpadDx = TilePosition.col(numpadPosition) - 1;
                    numpadDy = TilePosition.row(numpadPosition) - 1;
                    return true;
                }
                return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_A:
                keyLeft = false;
                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_D:
                keyRight = false;
                return true;
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_W:
                keyUp = false;
                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_S:
                keyDown = false;
                return true;
            default:
                if (keyCode == activeNumpadKeyCode) {
                    activeNumpadKeyCode = 0;
                    numpadDx = 0;
                    numpadDy = 0;
                    return true;
                }
                return super.onKeyUp(keyCode, event);
        }
    }

    // --- Analog stick / gamepad (API 12+) --------------------------------

    /** Sticks and hats arrive as generic motion events, which don't exist
     *  before API 12 - on older devices the system just never calls this.
     *  All API-12-only calls live in GamepadAxes, only reached behind the
     *  SDK check, so this class still loads fine on API 5. */
    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (Build.VERSION.SDK_INT >= 12 && GamepadAxes.isJoystickMove(event)) {
            stickDx = GamepadAxes.digitalX(event);
            stickDy = GamepadAxes.digitalY(event);
            return true;
        }
        return false;
    }

    /** Releases every input source. Called when the surface goes away
     *  (app paused/backgrounded), because a key-up or touch-up that
     *  happens while we're not around would otherwise leave the goose
     *  walking forever on resume. */
    private void clearInput() {
        keyLeft = false;
        keyRight = false;
        keyUp = false;
        keyDown = false;
        activeNumpadKeyCode = 0;
        numpadDx = 0;
        numpadDy = 0;
        stickDx = 0;
        stickDy = 0;
        pinching = false;
        joystick.end();
    }

    /** Combines every input source into one direction per axis: each of
     *  dx/dy is -1, 0 or 1, and both non-zero means a diagonal step. Sources
     *  are summed then reduced to their sign, so two sources agreeing don't
     *  double up, and two pushing opposite ways cancel out. */
    private int resolvedDx() {
        return sign(rawDx());
    }

    private int resolvedDy() {
        return sign(rawDy());
    }

    private int rawDx() {
        return (keyRight ? 1 : 0) - (keyLeft ? 1 : 0) + numpadDx + stickDx + joystick.getDx();
    }

    private int rawDy() {
        return (keyDown ? 1 : 0) - (keyUp ? 1 : 0) + numpadDy + stickDy + joystick.getDy();
    }

    private static int sign(int v) {
        return v > 0 ? 1 : (v < 0 ? -1 : 0);
    }

    // --- Update / render -------------------------------------------------

    /** Called from GameThread, off the UI thread - keep this cheap and
     *  avoid touching Views directly. */
    public void update(long deltaMs) {
        if (goose == null) {
            return;
        }
        goose.update(deltaMs, resolvedDx(), resolvedDy(), map);
        updateBird(deltaMs);
        updateCamera();
    }

    private void updateBird(long deltaMs) {
        if (bird == null) {
            return;
        }
        // The goose only ever changes tile when it actually took a step
        // (blocked/out-of-bounds attempts leave it in place) - so this is
        // exactly "the goose just moved one tile".
        if (goose.getRow() != lastGooseRow || goose.getCol() != lastGooseCol) {
            bird.moveTowards(lastGooseRow, lastGooseCol);
            lastGooseRow = goose.getRow();
            lastGooseCol = goose.getCol();
        }
        bird.update(deltaMs);

        if (!bird.isFollowing() && bird.getRow() == goose.getRow() && bird.getCol() == goose.getCol()) {
            bird.startFollowing();
        }
    }

    private void updateCamera() {
        int viewW = getWidth();
        int viewH = getHeight();
        if (viewW == 0 || viewH == 0) {
            return;
        }
        float visibleW = viewW / zoom;
        float visibleH = viewH / zoom;

        // Built from the same ROUNDED goose position render() draws at, so
        // camera and sprite always round together: no 1px shimmer while walking.
        cameraX = Math.round(goose.getX()) + GameMap.TILE_SIZE / 2 - (int) (visibleW / 2f);
        cameraY = Math.round(goose.getY()) + GameMap.TILE_SIZE / 2 - (int) (visibleH / 2f);

        int maxCamX = Math.max(0, (int) (map.getWidthPx() - visibleW));
        int maxCamY = Math.max(0, (int) (map.getHeightPx() - visibleH));
        cameraX = clamp(cameraX, 0, maxCamX);
        cameraY = clamp(cameraY, 0, maxCamY);
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }

    public void render(Canvas canvas) {
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), backgroundPaint);
        if (map == null) {
            return;
        }

        canvas.save();
        canvas.scale(zoom, zoom);
        // Everything from here on draws in "world" pixels (pre-zoom) -
        // canvas.scale above already applies the zoom multiplication.

        int tileSize = GameMap.TILE_SIZE;
        float visibleW = canvas.getWidth() / zoom; // canvas dims are still physical px pre-scale-application to our math
        float visibleH = canvas.getHeight() / zoom;
        int firstCol = Math.max(0, cameraX / tileSize);
        int firstRow = Math.max(0, cameraY / tileSize);
        int lastCol = Math.min(map.getCols() - 1, (int) ((cameraX + visibleW) / tileSize) + 1);
        int lastRow = Math.min(map.getRows() - 1, (int) ((cameraY + visibleH) / tileSize) + 1);

        for (int r = firstRow; r <= lastRow; r++) {
            for (int c = firstCol; c <= lastCol; c++) {
                drawTile(canvas, r, c, tileSize);
            }
        }

        if (bird != null) {
            int bx = Math.round(bird.getX()) - cameraX;
            int by = Math.round(bird.getY()) - cameraY;
            reusableDst.set(bx, by, bx + tileSize, by + tileSize);
            bird.draw(canvas, reusableDst);
        }

        if (goose != null) {
            int screenX = Math.round(goose.getX()) - cameraX;
            int screenY = Math.round(goose.getY()) - cameraY;
            reusableDst.set(screenX, screenY, screenX + tileSize, screenY + tileSize);
            goose.draw(canvas, reusableDst);
        }

        canvas.restore();

        // Screen-space overlay: drawn after restore() so the joystick isn't
        // scaled by the zoom or scrolled by the camera.
        joystick.draw(canvas);
    }

    private void drawTile(Canvas canvas, int row, int col, int tileSize) {
        MapCell cell = map.getCell(row, col);
        if (cell == null) {
            return;
        }

        if (cell.hasBackground()) {
            drawTileLayer(canvas, cell.backgroundTile, cell.backgroundPosition, row, col, tileSize);
        }
        drawTileLayer(canvas, cell.tile, cell.position, row, col, tileSize);

        // BIRD is drawn as its own moving entity (see render()/Bird), never
        // as a static per-cell image, even on the tile it spawned on.
        if (cell.hasItem() && cell.item != ItemType.BIRD) {
            Bitmap itemBitmap = itemBitmaps.get(cell.item);
            if (itemBitmap != null) {
                int screenX = col * tileSize - cameraX;
                int screenY = row * tileSize - cameraY;
                reusableDst.set(screenX, screenY, screenX + tileSize, screenY + tileSize);
                canvas.drawBitmap(itemBitmap, null, reusableDst, null);
            }
        }
    }

    private void drawTileLayer(Canvas canvas, TileType type, int position, int row, int col, int tileSize) {
        if (type == TileType.NONE) {
            return;
        }
        SpriteSheet sheet = tileSheets.get(type);
        if (sheet == null) {
            return;
        }

        reusableSrc.set(sheet.frameRect(TilePosition.col(position), TilePosition.row(position)));
        int screenX = col * tileSize - cameraX;
        int screenY = row * tileSize - cameraY;
        reusableDst.set(screenX, screenY, screenX + tileSize, screenY + tileSize);
        canvas.drawBitmap(sheet.getBitmap(), reusableSrc, reusableDst, null);
    }
}
