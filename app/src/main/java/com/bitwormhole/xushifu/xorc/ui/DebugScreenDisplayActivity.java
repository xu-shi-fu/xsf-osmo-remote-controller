package com.bitwormhole.xushifu.xorc.ui;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bitwormhole.xushifu.xorc.R;

import java.text.Format;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalField;
import java.util.Date;
import java.util.Formatter;
import java.util.Timer;

public class DebugScreenDisplayActivity extends XorcActivity {


    private Switch mSwitchKeepScreenOn;
    private SeekBar mSeekBarScreenBrightness;
    private TextView mTextYMD;
    private TextView mTextHMS;

    private InnerTimer mTimer;
    private Handler mHandler;


    private DateTimeFormatter mFormatYMD;
    private DateTimeFormatter mFormatHMS;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_debug_screen_display);

        mSwitchKeepScreenOn = findViewById(R.id.switch_keep_screen_on);
        mSeekBarScreenBrightness = findViewById(R.id.seek_bar_screen_brightness);
        mTextYMD = findViewById(R.id.text_yyyy_mm_dd);
        mTextHMS = findViewById(R.id.text_hh_mm_ss);

        mSwitchKeepScreenOn.setOnClickListener(this::onClickSwitchKSO);

        mSeekBarScreenBrightness.setOnSeekBarChangeListener(new InnerOnSeekBarChangeListener());
        int brightness = this.getScreenBrightness();
        mSeekBarScreenBrightness.setProgress(brightness);

        mHandler = new Handler();
        mTimer = new InnerTimer();
        mFormatYMD = this.makeFormatYMD();
        mFormatHMS = this.makeFormatHMS();
    }

    private DateTimeFormatter makeFormatHMS() {
        DateTimeFormatterBuilder fb = new DateTimeFormatterBuilder();
        fb.appendPattern("HH:mm:ss");
        return fb.toFormatter();
    }

    private DateTimeFormatter makeFormatYMD() {
        DateTimeFormatterBuilder fb = new DateTimeFormatterBuilder();
        fb.appendPattern("yyyy-MM-dd");
        return fb.toFormatter();
    }


    @Override
    protected void onStart() {
        super.onStart();
        mTimer.start();

    }

    @Override
    protected void onStop() {
        super.onStop();
        mTimer.stop();
    }

    private void onClickSwitchKSO(View view) {
        boolean yes = mSwitchKeepScreenOn.isChecked();
        mSwitchKeepScreenOn.setKeepScreenOn(yes);
        Log.i("DebugScreenDisplayActivity", "set KeepScreenOn = " + yes);
    }

    private void setScreenBrightness(int value) {

        final int m0 = mSeekBarScreenBrightness.getMin();
        final int m1 = mSeekBarScreenBrightness.getMax();

        Window win = getWindow();
        WindowManager.LayoutParams lp = win.getAttributes();
        if (m0 < m1) {
            lp.screenBrightness = (value - (float) m0) / (m1 - (float) m0);
        }
        win.setAttributes(lp);
    }

    private int getScreenBrightness() {

        final int m0 = mSeekBarScreenBrightness.getMin();
        final int m1 = mSeekBarScreenBrightness.getMax();
        final int full = m1 - m0;

        Window win = getWindow();
        WindowManager.LayoutParams lp = win.getAttributes();
        float vf = lp.screenBrightness;
        return (int) (m0 + (vf * full));
    }

    private void onTimer(InnerTimer it) {
        LocalDateTime now = LocalDateTime.now();
        String ymd = now.format(this.mFormatYMD);
        String hms = now.format(this.mFormatHMS);
        this.mTextYMD.setText(ymd);
        this.mTextHMS.setText(hms);
    }


    private class InnerOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        // for  Brightness

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            setScreenBrightness(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }


    private class InnerTimer {

        Thread current;
        int interval;

        InnerTimer() {
            this.interval = 1000;
        }

        void fire() {
            mHandler.post(() -> {
                onTimer(this);
            });
        }

        void sleep(int ms) {
            try {
                Thread.sleep(ms);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        void run() {
            for (; ; ) {
                if (this.current == null) {
                    break;
                }
                this.fire();
                this.sleep(this.interval);
            }
        }

        void start() {
            Thread th = this.current;
            if (th != null) {
                return;
            }
            th = new Thread(this::run);
            this.current = th;
            th.start();
        }

        void stop() {
            this.current = null;
        }
    }

}
