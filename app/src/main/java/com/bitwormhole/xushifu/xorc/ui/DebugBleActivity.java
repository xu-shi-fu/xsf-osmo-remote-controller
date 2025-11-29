package com.bitwormhole.xushifu.xorc.ui;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.bitwormhole.xushifu.xorc.R;
import com.bitwormhole.xushifu.xorc.core.BLEDeviceDiscoverer;

public class DebugBleActivity extends XorcActivity {

    private BLEDeviceDiscoverer mDiscoverer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mDiscoverer = new BLEDeviceDiscoverer(this);

        setContentView(R.layout.layout_debug_ble);
        setupButton(R.id.button_scanning_start, this::onClickButtonScanningStart);
        setupButton(R.id.button_scanning_stop, this::onClickButtonScanningStop);
    }

    private void setupButton(int res_id, View.OnClickListener l) {
        findViewById(res_id).setOnClickListener(l);
    }

    private void onClickButtonScanningStart(View view) {
        BLEDeviceDiscoverer discoverer = this.mDiscoverer;
        discoverer.start();
    }

    private void onClickButtonScanningStop(View view) {
        BLEDeviceDiscoverer discoverer = this.mDiscoverer;
        discoverer.stop();
    }

}
