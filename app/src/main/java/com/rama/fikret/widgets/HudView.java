package com.rama.fikret.widgets;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rama.fikret.R;
import com.rama.fikret.game.Ability;
import com.rama.fikret.managers.FontManager;
import com.rama.fikret.managers.PrefsManager;

public class HudView extends LinearLayout {

    public interface OnActionListener {
        void onMenu();

        void onSleep();

        void onSonar();

        void onHeal();

        void onTeleport();

        void onTeleportHome();

        void onMagic();

        void onCandle();

        void onDiamondSkin();

        void onThunder();

        void onDive();
    }

    private final PrefsManager prefs;
    private OnActionListener listener;

    private View rowAbilities;
    private View rowStats;
    private ImageView menuIcon;
    private ImageView expandIcon;

    private View btnSonar;
    private ImageView iconSonar;
    private View btnHeal;
    private ImageView iconHeal;
    private View btnTeleport;
    private ImageView iconTeleport;
    private View btnTeleportHome;
    private ImageView iconTeleportHome;

    private View btnMagic;
    private ImageView iconMagic;
    private View btnCandle;
    private ImageView iconCandle;
    private View btnDiamondSkin;
    private ImageView iconDiamondSkin;
    private View btnThunder;
    private ImageView iconThunder;
    private View btnDive;
    private ImageView iconDive;

    private TextView valueHp;
    private TextView valueDef;
    private TextView valueAtk;
    private TextView valueMana;
    private TextView valueLv;
    private TextView valueCoins;

    public HudView(Context context) {
        this(context, null);
    }

    public HudView(Context context, AttributeSet attrs) {
        super(context, attrs);
        prefs = PrefsManager.getInstance(context);
        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.hud, this, true);

        bindViews();
        wireClicks();
        applyForegroundTint();
        FontManager.apply(this, FontManager.getJersey25(context));

        refreshAbilities();
        applyExpanded(prefs.isHudExpanded());
    }

    @SuppressWarnings("deprecation")
    private void applyForegroundTint() {
        int color = getResources().getColor(R.color.text);
        PorterDuffColorFilter filter = new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN);
        menuIcon.setColorFilter(filter);
        expandIcon.setColorFilter(filter);
    }

    private void bindViews() {
        rowAbilities = findViewById(R.id.hud_row_abilities);
        rowStats = findViewById(R.id.hud_row_stats);
        menuIcon = findViewById(R.id.hud_icon_menu);
        expandIcon = findViewById(R.id.hud_icon_expand);

        btnSonar = findViewById(R.id.hud_btn_sonar);
        iconSonar = findViewById(R.id.hud_icon_sonar);
        btnHeal = findViewById(R.id.hud_btn_heal);
        iconHeal = findViewById(R.id.hud_icon_heal);
        btnTeleport = findViewById(R.id.hud_btn_teleport);
        iconTeleport = findViewById(R.id.hud_icon_teleport);
        btnTeleportHome = findViewById(R.id.hud_btn_teleport_home);
        iconTeleportHome = findViewById(R.id.hud_icon_teleport_home);

        btnMagic = findViewById(R.id.hud_btn_magic);
        iconMagic = findViewById(R.id.hud_icon_magic);
        btnCandle = findViewById(R.id.hud_btn_candle);
        iconCandle = findViewById(R.id.hud_icon_candle);
        btnDiamondSkin = findViewById(R.id.hud_btn_diamond_skin);
        iconDiamondSkin = findViewById(R.id.hud_icon_diamond_skin);
        btnThunder = findViewById(R.id.hud_btn_thunder);
        iconThunder = findViewById(R.id.hud_icon_thunder);
        btnDive = findViewById(R.id.hud_btn_dive);
        iconDive = findViewById(R.id.hud_icon_dive);

        valueHp = findViewById(R.id.hud_value_hp);
        valueDef = findViewById(R.id.hud_value_def);
        valueAtk = findViewById(R.id.hud_value_atk);
        valueMana = findViewById(R.id.hud_value_mana);
        valueLv = findViewById(R.id.hud_value_lv);
        valueCoins = findViewById(R.id.hud_value_coins);
    }

    private void wireClicks() {
        findViewById(R.id.hud_btn_menu).setOnClickListener(v -> {
            if (listener != null) listener.onMenu();
        });
        findViewById(R.id.hud_btn_sleep).setOnClickListener(v -> {
            if (listener != null) listener.onSleep();
        });
        findViewById(R.id.hud_btn_expand).setOnClickListener(v -> toggleExpanded());

        btnSonar.setOnClickListener(v -> {
            if (listener != null) listener.onSonar();
        });
        btnHeal.setOnClickListener(v -> {
            if (listener != null) listener.onHeal();
        });
        btnTeleport.setOnClickListener(v -> {
            if (listener != null) listener.onTeleport();
        });
        btnTeleportHome.setOnClickListener(v -> {
            if (listener != null) listener.onTeleportHome();
        });
        btnMagic.setOnClickListener(v -> {
            if (listener != null) listener.onMagic();
        });
        btnCandle.setOnClickListener(v -> {
            if (listener != null) listener.onCandle();
        });
        btnDiamondSkin.setOnClickListener(v -> {
            if (listener != null) listener.onDiamondSkin();
        });
        btnThunder.setOnClickListener(v -> {
            if (listener != null) listener.onThunder();
        });
        btnDive.setOnClickListener(v -> {
            if (listener != null) listener.onDive();
        });
    }

    public void setOnActionListener(OnActionListener listener) {
        this.listener = listener;
    }

    public void refreshAbilities() {
        applyAbility(Ability.SONAR, btnSonar, iconSonar);
        applyAbility(Ability.HEALING, btnHeal, iconHeal);
        applyAbility(Ability.RANDOM_TELEPORT, btnTeleport, iconTeleport);
        applyAbility(Ability.TELEPORT_HOME, btnTeleportHome, iconTeleportHome);

        applyAbility(Ability.SHOOT_MAGIC, btnMagic, iconMagic);
        applyAbility(Ability.SEE_IN_DARK, btnCandle, iconCandle);
        applyAbility(Ability.SWIM_NON_WATER, btnDiamondSkin, iconDiamondSkin);
        applyAbility(Ability.THUNDER_ATTACK, btnThunder, iconThunder);
        applyAbility(Ability.DIVE_DEEP_WATER, btnDive, iconDive);
    }

    private void applyAbility(Ability ability, View button, ImageView icon) {
        boolean unlocked = prefs.hasAbility(ability);
        icon.setVisibility(unlocked ? VISIBLE : INVISIBLE);
        button.setEnabled(unlocked);
        button.setClickable(unlocked);
    }

    public void toggleExpanded() {
        applyExpanded(!isExpanded());
    }

    public boolean isExpanded() {
        return rowStats.getVisibility() == VISIBLE;
    }

    private void applyExpanded(boolean expanded) {
        int visibility = expanded ? VISIBLE : GONE;
        rowAbilities.setVisibility(visibility);
        rowStats.setVisibility(visibility);
        expandIcon.setImageResource(expanded ? R.drawable.px_chevron_down : R.drawable.px_chevron_up);
        expandIcon.setContentDescription(getResources().getString(
                expanded ? R.string.hud_desc_collapse : R.string.hud_desc_expand));
        prefs.setHudExpanded(expanded);
    }

    public void setHp(String value) {
        valueHp.setText(value);
    }

    public void setDef(String value) {
        valueDef.setText(value);
    }

    public void setAtk(String value) {
        valueAtk.setText(value);
    }

    public void setMana(String value) {
        valueMana.setText(value);
    }

    public void setLevel(String value) {
        valueLv.setText(value);
    }

    public void setCoins(String value) {
        valueCoins.setText(value);
    }
}
