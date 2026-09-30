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
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private GameThread thread;
    private GameMap map;
    private Goose goose;
    private Stage stage;
    private int stageId;
    private final List<Bird> followingBirds = new ArrayList<>();
    private final List<int[]> chaseTargets = new ArrayList<>();
    private final List<Bird> idleBirds = new ArrayList<>();
    private final Random random = new Random();
    private int[] startPosition;
    private int holeGooseRow, holeGooseCol;
    private SpriteSheet tileSheet;
    private SpriteSheet itemSheet;
    private final Paint backgroundPaint = new Paint();

    private static final float SONAR_SPEED = 1.5f;
    private static final long SONAR_PING_MS = 600;
    private static final float SONAR_STROKE = 3f;
    private static final float SONAR_ECHO_GAP = 1f;
    private static final int SONAR_RINGS = 1;
    private final Paint sonarPaint = new Paint();
    private final List<SonarReveal> sonarPendingReveals = new ArrayList<>();
    private boolean sonarActive;
    private float sonarX;
    private float sonarY;
    private float sonarMaxRadius;
    private long sonarElapsedMs;
    private final Rect reusableSrc = new Rect();
    private final Rect reusableDst = new Rect();
    private final SwipeJoystick joystick;
    private final Blizzard blizzard;
    private int cameraX, cameraY;
    private static final float MIN_ZOOM = 1f;
    private static final float MAX_ZOOM = 4f;
    private float zoomRaw = 2f;
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
        this(context, stageId, null);
    }

    public GameView(Context context, int stageId, int[] startPosition) {
        super(context);
        getHolder().addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);

        backgroundPaint.setColor(Color.BLACK);
        sonarPaint.setStyle(Paint.Style.STROKE);
        sonarPaint.setAntiAlias(true);
        sonarPaint.setColor(0xFF7FE8FF);
        joystick = new SwipeJoystick(getResources().getDisplayMetrics().density);
        blizzard = new Blizzard(getResources());

        if (Build.VERSION.SDK_INT >= 8) {
            pinchZoomDetector = new PinchZoomDetector(context, new PinchZoomDetector.Listener() {
                @Override
                public void onZoom(float scaleFactor, float focusX, float focusY) {
                    setZoom(zoomRaw * scaleFactor);
                }
            });
        }

        this.startPosition = startPosition;
        try {
            stage = Maps.get(stageId);
        } catch (IllegalArgumentException unknownStage) {
            stageId = Maps.BEACH;
            stage = Maps.get(stageId);
            this.startPosition = null;
        }
        this.stageId = stageId;
        map = new GameMap(stage.tiles);
        applyRevealedItems();
        refreshPassiveAbilities();
        loadTileSheets();
        loadItemSheet();
        findIdleBirdSpawns();
    }

    private void refreshPassiveAbilities() {
        canSwimNonWater = PrefsManager.getInstance(getContext()).hasAbility(Ability.SWIM_NON_WATER);
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
        zoomRaw = Math.max(MIN_ZOOM, Math.min(newZoom, MAX_ZOOM));
        zoom = Math.round(zoomRaw);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (goose == null) {
            int row = stage.spawnRow;
            int col = stage.spawnCol;
            if (startPosition != null && map.isWalkable(startPosition[0], startPosition[1], canSwimNonWater)) {
                row = startPosition[0];
                col = startPosition[1];
            }
            goose = new Goose(getResources(), row, col);
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

        goose.update(deltaMs, dx, dy, map, canSwimNonWater);
        goose.setSwimming(isOverLiquid(goose.getX(), goose.getY()));
        checkHoleTransition();
        checkBirdRescues();
        updateSonar(deltaMs);
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
        }
    }

    private static int diveTarget(ItemType item) {
        if (item == ItemType.DIVE_TO_ARCTIC) {
            return Maps.ARCTIC;
        } else if (item == ItemType.DIVE_TO_BEACH_CAVE) {
            return Maps.BEACH_CAVE;
        } else if (item == ItemType.DIVE_TO_BEACH) {
            return Maps.BEACH;
        }
        return -1;
    }

    private static boolean isNestStage(int stageId) {
        return stageId >= Maps.NEST_OFFSET;
    }

    private void changeStage(int newStageId) {
        changeStage(newStageId, false);
    }

    private void changeStage(int newStageId, boolean resetWorld) {
        Stage newStage;
        try {
            newStage = Maps.get(newStageId);
        } catch (IllegalArgumentException noSuchStage) {
            return;
        }

        PrefsManager prefs = PrefsManager.getInstance(getContext());
        int leftStageId = stageId;
        int leftRow = goose.getRow();
        int leftCol = goose.getCol();

        stageId = newStageId;
        stage = newStage;
        map = new GameMap(stage.tiles);
        applyRevealedItems();
        resetSonar();
        findIdleBirdSpawns();

        int spawnRow = stage.spawnRow;
        int spawnCol = stage.spawnCol;
        if (!resetWorld) {
            int[] savedPos = prefs.getStagePosition(newStageId);
            if (savedPos != null && map.isWalkable(savedPos[0], savedPos[1], canSwimNonWater)) {
                spawnRow = savedPos[0];
                spawnCol = savedPos[1];
            }
        }

        if (resetWorld) {
            prefs.saveTeleportHome(newStageId, spawnRow, spawnCol);
        } else {
            prefs.saveStageEntry(leftStageId, leftRow, leftCol, newStageId, spawnRow, spawnCol);
        }

        goose = new Goose(getResources(), spawnRow, spawnCol);
        relocateFollowers(spawnRow, spawnCol);

        holeGooseRow = goose.getRow();
        holeGooseCol = goose.getCol();
        resetPan();
    }

    private void applyRevealedItems() {
        PrefsManager prefs = PrefsManager.getInstance(getContext());
        for (int i = 0; i < stage.sonarReveals.size(); i++) {
            SonarReveal reveal = stage.sonarReveals.get(i);
            if (prefs.isSonarRevealed(stageId, reveal.row, reveal.col)) {
                map.addItem(reveal.row, reveal.col, reveal.item);
            }
        }
    }

    private void resetSonar() {
        sonarActive = false;
        sonarPendingReveals.clear();
    }

    private void relocateFollowers(int row, int col) {
        for (int i = 0; i < followingBirds.size(); i++) {
            followingBirds.get(i).teleportTo(row, col);
            int[] chaseTarget = chaseTargets.get(i);
            chaseTarget[0] = row;
            chaseTarget[1] = col;
        }
    }

    private void resetPan() {
        panOffsetX = 0f;
        panOffsetY = 0f;
        panResetting = false;
    }

    private void moveGooseTo(int row, int col) {
        goose.teleportTo(row, col);
        relocateFollowers(row, col);
        holeGooseRow = row;
        holeGooseCol = col;
        resetPan();
    }

    public boolean teleportRandom() {
        if (!PrefsManager.getInstance(getContext()).hasAbility(Ability.RANDOM_TELEPORT)) {
            return false;
        }
        synchronized (getHolder()) {
            if (goose == null) {
                return false;
            }
            int[] spot = map.randomFreeCell(random, canSwimNonWater, goose.getRow(), goose.getCol());
            if (spot == null) {
                return false;
            }
            moveGooseTo(spot[0], spot[1]);
            return true;
        }
    }

    public boolean teleportHome() {
        if (!PrefsManager.getInstance(getContext()).hasAbility(Ability.TELEPORT_HOME)) {
            return false;
        }
        synchronized (getHolder()) {
            if (goose == null) {
                return false;
            }
            if (stageId == Maps.BEACH) {
                moveGooseTo(stage.spawnRow, stage.spawnCol);
            } else {
                changeStage(Maps.BEACH, true);
            }
            return true;
        }
    }

    public boolean dive() {
        if (!PrefsManager.getInstance(getContext()).hasAbility(Ability.DIVE_DEEP_WATER)) {
            return false;
        }
        synchronized (getHolder()) {
            if (goose == null) {
                return false;
            }
            MapCell cell = map.getCell(goose.getRow(), goose.getCol());
            int target = cell == null ? -1 : diveTarget(cell.item);
            if (target < 0) {
                return false;
            }
            changeStage(target);
            return true;
        }
    }

    public boolean sonar() {
        if (!PrefsManager.getInstance(getContext()).hasAbility(Ability.SONAR)) {
            return false;
        }
        synchronized (getHolder()) {
            if (goose == null || sonarActive) {
                return false;
            }
            float half = GameMap.TILE_SIZE / 2f;
            sonarX = goose.getX() + half;
            sonarY = goose.getY() + half;
            float farX = Math.max(sonarX, map.getWidthPx() - sonarX);
            float farY = Math.max(sonarY, map.getHeightPx() - sonarY);
            sonarMaxRadius = (float) Math.sqrt(farX * farX + farY * farY);

            sonarPendingReveals.clear();
            for (int i = 0; i < stage.sonarReveals.size(); i++) {
                SonarReveal reveal = stage.sonarReveals.get(i);
                MapCell cell = map.getCell(reveal.row, reveal.col);
                if (cell != null && !cell.hasItem() && reveal.zoneContains(goose.getRow(), goose.getCol())) {
                    sonarPendingReveals.add(reveal);
                }
            }
            sonarElapsedMs = 0;
            sonarActive = true;
            return true;
        }
    }

    private void updateSonar(long deltaMs) {
        if (!sonarActive) {
            return;
        }
        sonarElapsedMs += deltaMs;
        float radius = sonarElapsedMs * SONAR_SPEED;

        float half = GameMap.TILE_SIZE / 2f;
        for (Iterator<SonarReveal> it = sonarPendingReveals.iterator(); it.hasNext(); ) {
            SonarReveal reveal = it.next();
            float dx = reveal.col * GameMap.TILE_SIZE + half - sonarX;
            float dy = reveal.row * GameMap.TILE_SIZE + half - sonarY;
            if (Math.sqrt(dx * dx + dy * dy) <= radius) {
                if (map.addItem(reveal.row, reveal.col, reveal.item)) {
                    PrefsManager.getInstance(getContext()).setSonarRevealed(stageId, reveal.row, reveal.col);
                }
                it.remove();
            }
        }

        if (sonarElapsedMs >= sonarMaxRadius / SONAR_SPEED + SONAR_PING_MS) {
            resetSonar();
        }
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
            refreshPassiveAbilities();
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
        drawSonar(canvas, visibleW, visibleH);

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

    private void drawSonar(Canvas canvas, float visibleW, float visibleH) {
        if (!sonarActive) {
            return;
        }
        float cx = sonarX - cameraX;
        float cy = sonarY - cameraY;
        float radius = sonarElapsedMs * SONAR_SPEED;

        if (radius <= sonarMaxRadius) {
            float fade = 1f - radius / sonarMaxRadius;
            for (int i = 0; i < SONAR_RINGS; i++) {
                float r = radius - i * SONAR_ECHO_GAP;
                if (r <= 0f) {
                    break;
                }
                float stroke = SONAR_STROKE / (1f + i * 0.5f);
                if (!ringOnScreen(cx, cy, r, stroke, visibleW, visibleH)) {
                    continue;
                }
                sonarPaint.setAlpha((int) (220f * (0.3f + 0.7f * fade) / (1f + i * 1.5f)));
                sonarPaint.setStrokeWidth(stroke);
                canvas.drawCircle(cx, cy, r, sonarPaint);
            }
        }

        float half = GameMap.TILE_SIZE / 2f;
        for (int i = 0; i < idleBirds.size(); i++) {
            Bird bird = idleBirds.get(i);
            float bx = bird.getX() + half;
            float by = bird.getY() + half;
            float dx = bx - sonarX;
            float dy = by - sonarY;
            float pingStart = (float) Math.sqrt(dx * dx + dy * dy) / SONAR_SPEED;
            float t = sonarElapsedMs - pingStart;
            if (t < 0f || t >= SONAR_PING_MS) {
                continue;
            }
            float phase = t / SONAR_PING_MS;
            sonarPaint.setAlpha((int) (255f * (1f - phase)));
            sonarPaint.setStrokeWidth(SONAR_STROKE);
            canvas.drawCircle(bx - cameraX, by - cameraY, GameMap.TILE_SIZE * (0.3f + 0.6f * phase), sonarPaint);
        }
    }

    private static boolean ringOnScreen(float cx, float cy, float r, float stroke, float w, float h) {
        float nearX = cx < 0f ? -cx : (cx > w ? cx - w : 0f);
        float nearY = cy < 0f ? -cy : (cy > h ? cy - h : 0f);
        float farX = Math.max(Math.abs(cx), Math.abs(cx - w));
        float farY = Math.max(Math.abs(cy), Math.abs(cy - h));
        double near = Math.sqrt(nearX * nearX + nearY * nearY);
        double far = Math.sqrt(farX * farX + farY * farY);
        return r + stroke >= near && r - stroke <= far;
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
