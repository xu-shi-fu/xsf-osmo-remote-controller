package com.bitwormhole.xushifu.xorc.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.bitwormhole.xushifu.xorc.R;

public class DebugHomeActivity extends XorcActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_debug_home);
        setupButtons();
    }


    private void setupButtons() {
        setupButton(R.id.button_debug_ble, DebugBleActivity.class);
        setupButton(R.id.button_debug_location, DebugLocationActivity.class);
        setupButton(R.id.button_debug_screen_display, DebugScreenDisplayActivity.class);
    }

    private void setupButton(int res_id, Class<?> activity_class) {
        findViewById(res_id).setOnClickListener((v) -> {
            Intent i = new Intent(this, activity_class);
            startActivity(i);
        });
    }

}
