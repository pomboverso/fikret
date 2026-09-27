package com.rama.fikret.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.rama.fikret.game.GameView;
import com.rama.fikret.game.Maps;
import com.rama.fikret.widgets.HudView;

/**
 * Hosts the game surface with the HUD (see HudView) docked below it - the
 * two are stacked, not overlapping, so the HUD never has to be composited
 * on top of the SurfaceView's own surface (which older Android versions/
 * emulators can fail to do, showing black - see GameView) and the goose is
 * never drawn underneath the HUD in the first place. GameView still owns
 * the loop, the map and the goose - the HUD just fires callbacks into this
 * activity (see the OnActionListener below).
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
    private HudView hudView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        int stageId = getIntent().getIntExtra(EXTRA_STAGE, Maps.ARCTIC);
        gameView = new GameView(this, stageId);
        hudView = new HudView(this);
        hudView.setOnActionListener(hudListener);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        // GameView takes all the space the HUD doesn't need, above it.
        root.addView(gameView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));
        root.addView(hudView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        // checkBirdRescues()/onBirdRescued() run on GameThread, off the UI
        // thread (see GameView's comment there) - runOnUiThread() is what
        // makes it safe to touch hudView from that callback.
        gameView.setOnAbilityUnlockedListener(ability -> runOnUiThread(() -> hudView.refreshAbilities()));

        setContentView(root);
        gameView.requestFocus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Picks up any ability unlocked since the HUD was last shown (e.g.
        // rescuing a bird while this activity was paused).
        hudView.refreshAbilities();
    }

    // TODO: most of these just have their box wired up for now - hook each
    // one into the real gameplay action (GameView/Goose/PrefsManager) when
    // that logic exists.
    private final HudView.OnActionListener hudListener = new HudView.OnActionListener() {
        @Override
        public void onMenu() {
            startActivity(new Intent(GameActivity.this, Main.class));
            finish();
        }

        @Override
        public void onSleep() {
        }

        @Override
        public void onSonar() {
        }

        @Override
        public void onHeal() {
        }

        @Override
        public void onTeleport() {
        }

        @Override
        public void onTeleportHome() {
        }

        @Override
        public void onMagic() {
        }

        @Override
        public void onCandle() {
        }

        @Override
        public void onDiamondSkin() {
        }

        @Override
        public void onThunder() {
        }

        @Override
        public void onDive() {
        }
    };
}

