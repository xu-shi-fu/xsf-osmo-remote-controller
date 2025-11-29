package com.bitwormhole.xushifu.xorc.ui;

import android.app.Activity;
import android.view.View;

public class XorcActivity extends Activity {

    protected void setupButtonListener(int res_id, View.OnClickListener li) {
        findViewById(res_id).setOnClickListener(li);
    }

}
