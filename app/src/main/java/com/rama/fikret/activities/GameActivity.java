package com.rama.fikret.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.rama.fikret.game.GameView;
import com.rama.fikret.game.Maps;

/**
 * Hosts the game surface fullscreen. Nothing else lives here on purpose -
 * GameView owns the loop, the map and the goose.
 *
 * Pass which stage to load via EXTRA_STAGE, e.g.:
 *   Intent intent = new Intent(this, GameActivity.class);
 *   intent.putExtra(GameActivity.EXTRA_STAGE, Maps.NEXT_STAGE);
 *   startActivity(intent);
 * Omitting the extra loads Maps.MEADOW.
 */
public class GameActivity extends Activity {
    public static final String EXTRA_STAGE = "stage";

    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        int stageId = getIntent().getIntExtra(EXTRA_STAGE, Maps.VOLCAN);
        gameView = new GameView(this, stageId);
        setContentView(gameView);
        gameView.requestFocus();
    }
}

