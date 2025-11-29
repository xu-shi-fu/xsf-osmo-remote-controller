package com.bitwormhole.xushifu.xorc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.bitwormhole.xushifu.xorc.ui.DebugHomeActivity;
import com.bitwormhole.xushifu.xorc.ui.XorcActivity;

public class MainActivity extends XorcActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        setupButtons();
    }

    private void setupButtons() {
        findViewById(R.id.button_debug).setOnClickListener(this::onClickButtonDebug);
    }

    private void onClickButtonDebug(View view) {
        Intent i = new Intent(this, DebugHomeActivity.class);
        startActivity(i);
    }

}
