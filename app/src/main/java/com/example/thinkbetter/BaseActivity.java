package com.example.thinkbetter;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.materialswitch.MaterialSwitch;

public abstract class BaseActivity extends MenuJava {

    protected SharedPreferences prefs() {
        return getSharedPreferences("settings", MODE_PRIVATE);
    }

    protected void setupHeader(String title) {
        ((TextView) findViewById(R.id.txtHeaderTitle)).setText(title);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    // Preenche um item_switch e salva o estado em SharedPreferences
    protected void bindSwitch(int itemId, String title, String subtitle,
                              String prefKey, boolean defaultValue) {
        View item = findViewById(itemId);
        ((TextView) item.findViewById(R.id.txtTitle)).setText(title);
        ((TextView) item.findViewById(R.id.txtSubtitle)).setText(subtitle);
        MaterialSwitch sw = item.findViewById(R.id.switchItem);
        sw.setChecked(prefs().getBoolean(prefKey, defaultValue));
        sw.setOnCheckedChangeListener((b, checked) ->
                prefs().edit().putBoolean(prefKey, checked).apply());
    }
}