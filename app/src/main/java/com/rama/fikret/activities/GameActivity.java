package com.rama.fikret.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import com.rama.fikret.economy.GameState;
import com.rama.fikret.game.GameView;
import com.rama.fikret.game.Maps;
import com.rama.fikret.managers.PrefsManager;

public class GameActivity extends Activity {
    public static final String EXTRA_STAGE = "stage";

    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        int stageId;
        int[] startPosition = null;
        if (getIntent().hasExtra(EXTRA_STAGE)) {
            stageId = getIntent().getIntExtra(EXTRA_STAGE, Maps.BEACH);
        } else {
            // Resume where the player was.
            PrefsManager prefs = PrefsManager.getInstance(this);
            stageId = prefs.getCurrentStage(Maps.BEACH);
            startPosition = prefs.getArrivalPosition();
        }
        gameView = new GameView(this, stageId, startPosition);
        gameView.setOnExitListener(() -> runOnUiThread(() -> finish()));

        setContentView(gameView);
        gameView.requestFocus();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (gameView != null && gameView.handleKeyEvent(event)) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        int[] position = gameView.getGoosePosition();
        if (position != null) {
            PrefsManager.getInstance(this).setArrivalPosition(position[0], position[1]);
        }
        GameState.get(this).save();
    }
}
