package com.bitwormhole.xushifu.xorc.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bitwormhole.xushifu.xorc.R;
import com.bitwormhole.xushifu.xorc.core.LocationLogger;

public class DebugLocationActivity extends XorcActivity {

    private LocationLogger mLocationLogger;
    private TextView mTextOutput;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_debug_location);

        this.mLocationLogger = new LocationLogger(this);
        this.mTextOutput = findViewById(R.id.text_output);

        setupButtonListener(R.id.button_fetch_status, this::onClickButtonFetchStatus);
    }

    private void onClickButtonFetchStatus(View view) {
        String msg = this.mLocationLogger.getStatusMessage();
        this.mTextOutput.setText(msg);
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.mLocationLogger.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.mLocationLogger.stop();
    }
}
