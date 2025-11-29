package com.bitwormhole.xushifu.xorc.core;

import android.content.Context;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;

public class LocationLogger {

    private final Context mContext;
    private Handler mHandler;
    private LocationManager mLocationManager;
    private LocationListener mListener;
    private GnssStatus.Callback mGnssCallback;

    private final InnerStatus mStatus;

    public LocationLogger(Context ctx) {
        this.mContext = ctx;
        this.mStatus = new InnerStatus();
    }

    public void start() {
        //  Handler h = this.innerGetHandler();

        LocationManager lm = this.innerGetLocationManager();
        LocationListener listener = this.innerGetListener();
        LocationRequest.Builder rb = new LocationRequest.Builder(1000);
        GnssStatus.Callback gnss_callback = innerGetGnssStatusCallback();

        rb.setMinUpdateDistanceMeters(2).setMinUpdateIntervalMillis(1000);
        rb.setMaxUpdates(Integer.MAX_VALUE).setMaxUpdateDelayMillis(5000);
        rb.setIntervalMillis(1000);
        rb.setQuality(LocationRequest.QUALITY_BALANCED_POWER_ACCURACY);
        rb.setDurationMillis(Long.MAX_VALUE);

        LocationRequest request = rb.build();
        Executor executor = this.mContext.getMainExecutor();

        //        String provider = LocationManager.PASSIVE_PROVIDER;

        lm.registerGnssStatusCallback(executor, gnss_callback);

        String[] providers = {
                LocationManager.NETWORK_PROVIDER, LocationManager.GPS_PROVIDER
        };
        for (String pro : providers) {
            lm.requestLocationUpdates(pro, request, executor, listener);
        }
    }

    public void stop() {
        GnssStatus.Callback gnss_callback = innerGetGnssStatusCallback();
        LocationListener li = this.innerGetListener();
        LocationManager lm = this.innerGetLocationManager();
        lm.removeUpdates(li);
        lm.unregisterGnssStatusCallback(gnss_callback);
    }


    private GnssStatus.Callback innerGetGnssStatusCallback() {
        GnssStatus.Callback callback = this.mGnssCallback;
        if (callback == null) {
            callback = new InnerGnssStatusCallback();
            this.mGnssCallback = callback;
        }
        return callback;
    }

    private LocationListener innerGetListener() {
        LocationListener li = this.mListener;
        if (li == null) {
            li = new InnerLocationListener();
            this.mListener = li;
        }
        return li;
    }

    private LocationManager innerGetLocationManager() {
        LocationManager lm = this.mLocationManager;
        if (lm == null) {
            lm = this.mContext.getSystemService(LocationManager.class);
            this.mLocationManager = lm;
        }
        return lm;
    }

    private Handler innerGetHandler() {
        Handler h = this.mHandler;
        if (h == null) {
            h = new Handler();
            this.mHandler = h;
        }
        return h;
    }

    public String getStatusMessage() {
        final char nl = '\n';
        InnerStatus sta = this.mStatus;
        StringBuilder mb = new StringBuilder();

        mb.append(nl).append(" provider:").append(sta.provider);
        mb.append(nl).append(" status:").append(sta.status);

        mb.append(nl).append(" time:").append(sta.time);

        mb.append(nl).append(" lon:").append(sta.longitude);
        mb.append(nl).append(" lat:").append(sta.latitude);
        mb.append(nl).append(" alt:").append(sta.altitude);
        mb.append(nl).append(" speed:").append(sta.speed);
        mb.append(nl).append(" bearing:").append(sta.bearing);

        mb.append(nl).append(" count:").append(sta.count);
        mb.append(nl).append(" count_sate_sta_changed:").append(sta.countOnSatelliteStatusChanged);

        return mb.toString();
    }


    private static class InnerStatus {
        String provider;
        int status;

        double latitude;
        double longitude;
        double altitude;
        float speed;
        float bearing; // aka. direction
        long time;
        int count;
        int countOnSatelliteStatusChanged;
    }

    private class InnerGnssStatusCallback extends GnssStatus.Callback {

        @Override
        public void onSatelliteStatusChanged(@NonNull GnssStatus status) {
            super.onSatelliteStatusChanged(status);
            mStatus.countOnSatelliteStatusChanged++;
        }
    }

    private class InnerLocationListener implements LocationListener {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            LocationListener.super.onStatusChanged(provider, status, extras);
            mStatus.status = status;
            mStatus.provider = provider;
        }

        @Override
        public void onLocationChanged(@NonNull Location location) {

            StringBuilder sb = new StringBuilder();
            String provider = location.getProvider();
            long time = location.getTime();
            double lat = location.getLatitude();
            double alt = location.getAltitude();
            double lon = location.getLongitude();
            float speed = location.getSpeed();
            float bearing = location.getBearing();

            sb.append("[Location");
            sb.append(" time:").append(time);
            sb.append(" lon:").append(lon);
            sb.append(" lat:").append(lat);
            sb.append(" alt:").append(alt);
            sb.append(']');

            mStatus.time = time;
            mStatus.altitude = alt;
            mStatus.latitude = lat;
            mStatus.longitude = lon;
            mStatus.speed = speed;
            mStatus.bearing = bearing;
            mStatus.provider = provider;
            mStatus.count++;

            Log.i("Location", sb.toString());
        }
    }
}
