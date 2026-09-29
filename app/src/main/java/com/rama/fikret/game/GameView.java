package com.rama.fikret.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.rama.fikret.R;
import com.rama.fikret.managers.PrefsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private GameThread thread;
    private GameMap map;
    private Goose goose;
    private Stage stage;
    private int stageId;
    private final List<Bird> followingBirds = new ArrayList<>();
    private final List<int[]> chaseTargets = new ArrayList<>();
    private final List<Bird> idleBirds = new ArrayList<>();
    private final Map<Integer, int[]> lastPositionByStage = new HashMap<>();
    private int holeGooseRow, holeGooseCol;
    private boolean diveLocked;
    private SpriteSheet tileSheet;
    private SpriteSheet itemSheet;
    private final Paint backgroundPaint = new Paint();
    private final Rect reusableSrc = new Rect();
    private final Rect reusableDst = new Rect();
    private final SwipeJoystick joystick;
    private final Blizzard blizzard;
    private int cameraX, cameraY;
    private static final float MIN_ZOOM = 1f;
    private static final float MAX_ZOOM = 4f;
    private float zoom = 2f;
    private PinchZoomDetector pinchZoomDetector;
    private float panOffsetX, panOffsetY;
    private float panAnchorX, panAnchorY;
    private boolean wasMoving;
    private static final long PAN_RESET_DURATION_MS = 200;
    private boolean panResetting;
    private long panResetElapsedMs;
    private float panResetStartX, panResetStartY;
    private volatile boolean keyLeft, keyRight, keyUp, keyDown;
    private volatile int activeNumpadKeyCode = 0;
    private volatile int numpadDx, numpadDy;
    private volatile int stickDx, stickDy;
    private boolean pinching;
    private OnAbilityUnlockedListener abilityUnlockedListener;

    public interface OnAbilityUnlockedListener {
        void onAbilityUnlocked(Ability ability);
    }

    public void setOnAbilityUnlockedListener(OnAbilityUnlockedListener listener) {
        this.abilityUnlockedListener = listener;
    }

    public GameView(Context context) {
        this(context, Maps.BEACH);
    }

    public GameView(Context context, int stageId) {
        super(context);
        getHolder().addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);

        backgroundPaint.setColor(Color.BLACK);
        joystick = new SwipeJoystick(getResources().getDisplayMetrics().density);
        blizzard = new Blizzard(getResources());

        if (Build.VERSION.SDK_INT >= 8) {
            pinchZoomDetector = new PinchZoomDetector(context, new PinchZoomDetector.Listener() {
                @Override
                public void onZoom(float scaleFactor, float focusX, float focusY) {
                    setZoom(zoom * scaleFactor);
                }
            });
        }

        this.stageId = stageId;
        stage = Maps.get(stageId);
        map = new GameMap(stage.tiles);
        loadTileSheets();
        loadItemSheet();
        findIdleBirdSpawns();
    }

    private void loadTileSheets() {
        if (tileSheet == null) {
            tileSheet = new SpriteSheet(getResources(), R.drawable.tiles, TileType.SHEET_COLUMNS, TileType.SHEET_ROWS);
        }
    }

    private void loadItemSheet() {
        if (itemSheet == null) {
            itemSheet = ItemSheet.get(getResources());
        }
    }

    private void findIdleBirdSpawns() {
        idleBirds.clear();
        PrefsManager prefs = PrefsManager.getInstance(getContext());
        for (int r = 0; r < map.getRows(); r++) {
            for (int c = 0; c < map.getCols(); c++) {
                if (map.getCell(r, c).item == ItemType.BIRD && !prefs.isBirdRescued(stageId, r, c)) {
                    idleBirds.add(new Bird(getResources(), r, c));
                }
            }
        }
    }

    private void setZoom(float newZoom) {
        zoom = Math.max(MIN_ZOOM, Math.min(newZoom, MAX_ZOOM));
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (goose == null) {
            goose = new Goose(getResources(), stage.spawnRow, stage.spawnCol);
            restoreRescuedBirds();
        }
        holeGooseRow = goose.getRow();
        holeGooseCol = goose.getCol();
        thread = new GameThread(holder, this);
        thread.setRunning(true);
        thread.start();
    }

    private void restoreRescuedBirds() {
        List<String> rescuedKeys = PrefsManager.getInstance(getContext()).getRescuedBirdKeys();
        int row = goose.getRow();
        int col = goose.getCol();
        for (int i = 0; i < rescuedKeys.size(); i++) {
            Bird bird = new Bird(getResources(), row, col);
            bird.startFollowing();
            followingBirds.add(bird);
            chaseTargets.add(new int[]{row, col});
        }
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (pinchZoomDetector != null) {
            pinchZoomDetector.onTouchEvent(event);
        }

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                pinching = false;
                joystick.begin(event.getX(), event.getY());
                return true;
            case MotionEvent.ACTION_POINTER_DOWN:
                pinching = true;
                panResetting = false;
                joystick.end();
                panAnchorX = averagePointerX(event);
                panAnchorY = averagePointerY(event);
                return true;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() > 1) {
                    pinching = true;
                    joystick.end();
                    float avgX = averagePointerX(event);
                    float avgY = averagePointerY(event);
                    panOffsetX -= (avgX - panAnchorX) / zoom;
                    panOffsetY -= (avgY - panAnchorY) / zoom;
                    panAnchorX = avgX;
                    panAnchorY = avgY;
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

    private static float averagePointerX(MotionEvent event) {
        float sum = 0f;
        int count = event.getPointerCount();
        for (int i = 0; i < count; i++) {
            sum += event.getX(i);
        }
        return sum / count;
    }

    private static float averagePointerY(MotionEvent event) {
        float sum = 0f;
        int count = event.getPointerCount();
        for (int i = 0; i < count; i++) {
            sum += event.getY(i);
        }
        return sum / count;
    }

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

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (Build.VERSION.SDK_INT >= 12 && GamepadAxes.isJoystickMove(event)) {
            stickDx = GamepadAxes.digitalX(event);
            stickDy = GamepadAxes.digitalY(event);
            return true;
        }
        return false;
    }

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
        panOffsetX = 0f;
        panOffsetY = 0f;
        panResetting = false;
        wasMoving = false;
        joystick.end();
    }

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

    public void update(long deltaMs) {
        if (goose == null) {
            return;
        }
        int dx = resolvedDx();
        int dy = resolvedDy();

        boolean nowMoving = dx != 0 || dy != 0;
        if (nowMoving && !wasMoving) {
            startPanReset();
        }
        wasMoving = nowMoving;

        goose.update(deltaMs, dx, dy, map);
        goose.setSwimming(isOverLiquid(goose.getX(), goose.getY()));
        checkHoleTransition();
        checkBirdRescues();
        updateBirds(deltaMs);
        updatePanReset(deltaMs);
        updateCamera();
        if (stage.hasBlizzard) {
            blizzard.update(deltaMs);
        }
    }

    private void startPanReset() {
        if (panOffsetX == 0f && panOffsetY == 0f) {
            return;
        }
        panResetting = true;
        panResetElapsedMs = 0;
        panResetStartX = panOffsetX;
        panResetStartY = panOffsetY;
    }

    private void updatePanReset(long deltaMs) {
        if (!panResetting) {
            return;
        }
        panResetElapsedMs += deltaMs;
        float t = Math.min(1f, panResetElapsedMs / (float) PAN_RESET_DURATION_MS);
        float eased = 1f - (1f - t) * (1f - t); // ease-out quad
        panOffsetX = panResetStartX * (1f - eased);
        panOffsetY = panResetStartY * (1f - eased);
        if (t >= 1f) {
            panResetting = false;
        }
    }

    private void checkHoleTransition() {
        if (goose.getRow() == holeGooseRow && goose.getCol() == holeGooseCol) {
            return;
        }
        holeGooseRow = goose.getRow();
        holeGooseCol = goose.getCol();

        MapCell cell = map.getCell(holeGooseRow, holeGooseCol);
        if (cell == null) {
            return;
        }
        if (cell.item == ItemType.HOLE_DOWN) {
            changeStage(stageId + 1);
        } else if (cell.item == ItemType.HOLE_UP) {
            changeStage(isNestStage(stageId) ? stageId - Maps.NEST_OFFSET : stageId - 1);
        } else if (cell.item == ItemType.HOLE_DOWN_NEST) {
            changeStage(stageId + Maps.NEST_OFFSET);
        } else {
            checkDiveTransition(cell);
        }
    }

    private static boolean isDiveItem(ItemType item) {
        return item == ItemType.DIVE_TO_ARCTIC
                || item == ItemType.DIVE_TO_BEACH_CAVE
                || item == ItemType.DIVE_TO_BEACH;
    }

    private void checkDiveTransition(MapCell cell) {
        if (!goose.isSwimming()) {
            diveLocked = false;
        }
        if (diveLocked || !isDiveItem(cell.item)) {
            return;
        }
        diveLocked = true;
        if (cell.item == ItemType.DIVE_TO_ARCTIC) {
            diveTo(Maps.ARCTIC);
        } else if (cell.item == ItemType.DIVE_TO_BEACH_CAVE) {
            diveTo(Maps.BEACH_CAVE);
        } else {
            diveTo(Maps.BEACH);
        }
    }

    private void diveTo(int targetStageId) {
        if (PrefsManager.getInstance(getContext()).hasAbility(Ability.DIVE_DEEP_WATER)) {
            changeStage(targetStageId);
        }
    }

    private static boolean isNestStage(int stageId) {
        return stageId >= Maps.NEST_OFFSET;
    }

    private void changeStage(int newStageId) {
        Stage newStage;
        try {
            newStage = Maps.get(newStageId);
        } catch (IllegalArgumentException noSuchStage) {
            return;
        }

        lastPositionByStage.put(stageId, new int[]{goose.getRow(), goose.getCol()});

        stageId = newStageId;
        stage = newStage;
        map = new GameMap(stage.tiles);
        findIdleBirdSpawns();

        int spawnRow = stage.spawnRow;
        int spawnCol = stage.spawnCol;
        int[] savedPos = lastPositionByStage.get(newStageId);
        if (savedPos != null && map.isPassable(savedPos[0], savedPos[1])) {
            spawnRow = savedPos[0];
            spawnCol = savedPos[1];
        }

        goose = new Goose(getResources(), spawnRow, spawnCol);

        for (int i = 0; i < followingBirds.size(); i++) {
            Bird carried = new Bird(getResources(), spawnRow, spawnCol);
            carried.startFollowing();
            followingBirds.set(i, carried);
            int[] chaseTarget = chaseTargets.get(i);
            chaseTarget[0] = spawnRow;
            chaseTarget[1] = spawnCol;
        }

        holeGooseRow = goose.getRow();
        holeGooseCol = goose.getCol();
        diveLocked = true;
        panOffsetX = 0f;
        panOffsetY = 0f;
        panResetting = false;
    }

    private boolean isOverLiquid(float spriteX, float spriteY) {
        return map.isLiquidAt(spriteX + GameMap.TILE_SIZE / 2f, spriteY + GameMap.TILE_SIZE / 2f);
    }

    private void checkBirdRescues() {
        for (Iterator<Bird> it = idleBirds.iterator(); it.hasNext(); ) {
            Bird idle = it.next();
            if (idle.getRow() != goose.getRow() || idle.getCol() != goose.getCol()) {
                continue;
            }
            it.remove();
            idle.startFollowing();

            int leaderRow, leaderCol;
            if (followingBirds.isEmpty()) {
                leaderRow = goose.getRow();
                leaderCol = goose.getCol();
            } else {
                Bird lastInChain = followingBirds.get(followingBirds.size() - 1);
                leaderRow = lastInChain.getRow();
                leaderCol = lastInChain.getCol();
            }

            followingBirds.add(idle);
            chaseTargets.add(new int[]{leaderRow, leaderCol});

            onBirdRescued(idle.getRow(), idle.getCol());
        }
    }

    private void onBirdRescued(int spawnRow, int spawnCol) {
        PrefsManager prefs = PrefsManager.getInstance(getContext());
        prefs.setBirdRescued(stageId, spawnRow, spawnCol);

        Ability ability = Ability.forStage(stageId);
        if (ability != null) {
            prefs.unlockAbility(ability);
            if (abilityUnlockedListener != null) {
                abilityUnlockedListener.onAbilityUnlocked(ability);
            }
        }
    }

    private void updateBirds(long deltaMs) {
        int leaderRow = goose.getRow();
        int leaderCol = goose.getCol();

        for (int i = 0; i < followingBirds.size(); i++) {
            Bird bird = followingBirds.get(i);
            int[] chaseTarget = chaseTargets.get(i);

            if (leaderRow != chaseTarget[0] || leaderCol != chaseTarget[1]) {
                bird.moveTowards(chaseTarget[0], chaseTarget[1]);
            }
            chaseTarget[0] = leaderRow;
            chaseTarget[1] = leaderCol;

            bird.update(deltaMs);
            bird.setSwimming(isOverLiquid(bird.getX(), bird.getY()));

            leaderRow = bird.getRow();
            leaderCol = bird.getCol();
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
        cameraX = Math.round(goose.getX() + panOffsetX) + GameMap.TILE_SIZE / 2 - (int) (visibleW / 2f);
        cameraY = Math.round(goose.getY() + panOffsetY) + GameMap.TILE_SIZE / 2 - (int) (visibleH / 2f);

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

        int tileSize = GameMap.TILE_SIZE;
        float visibleW = canvas.getWidth() / zoom;
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

        drawBirds(canvas, idleBirds, tileSize);
        drawBirds(canvas, followingBirds, tileSize);

        if (goose != null) {
            int screenX = Math.round(goose.getX()) - cameraX;
            int screenY = Math.round(goose.getY()) - cameraY;
            reusableDst.set(screenX, screenY, screenX + tileSize, screenY + tileSize);
            goose.draw(canvas, reusableDst);
        }

        canvas.restore();

        if (stage.hasBlizzard) {
            blizzard.draw(canvas, canvas.getWidth(), canvas.getHeight());
        }
        joystick.draw(canvas);
    }

    private void drawBirds(Canvas canvas, List<Bird> birds, int tileSize) {
        for (int i = 0; i < birds.size(); i++) {
            Bird bird = birds.get(i);
            int bx = Math.round(bird.getX()) - cameraX;
            int by = Math.round(bird.getY()) - cameraY;
            reusableDst.set(bx, by, bx + tileSize, by + tileSize);
            bird.draw(canvas, reusableDst);
        }
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

        if (cell.hasItem() && cell.item.hasSprite() && itemSheet != null) {
            int screenX = col * tileSize - cameraX;
            int screenY = row * tileSize - cameraY;
            reusableSrc.set(itemSheet.frameRect(cell.item.spriteCol, cell.item.spriteRow));
            reusableDst.set(screenX, screenY, screenX + tileSize, screenY + tileSize);
            canvas.drawBitmap(itemSheet.getBitmap(), reusableSrc, reusableDst, null);
        }
    }

    private void drawTileLayer(Canvas canvas, TileType type, int position, int row, int col, int tileSize) {
        if (type == TileType.NONE) {
            return;
        }
        if (tileSheet == null) {
            return;
        }

        reusableSrc.set(tileSheet.frameRect(
                type.blockCol + TilePosition.col(position),
                type.blockRow + TilePosition.row(position)));
        int screenX = col * tileSize - cameraX;
        int screenY = row * tileSize - cameraY;
        reusableDst.set(screenX, screenY, screenX + tileSize, screenY + tileSize);
        canvas.drawBitmap(tileSheet.getBitmap(), reusableSrc, reusableDst, null);
    }
}
