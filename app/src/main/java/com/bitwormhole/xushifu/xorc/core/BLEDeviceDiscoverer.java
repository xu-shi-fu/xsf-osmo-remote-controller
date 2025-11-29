package com.bitwormhole.xushifu.xorc.core;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.ParcelUuid;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BLEDeviceDiscoverer {

    private final Context mContext;
    private BluetoothLeScanner mScanner;
    private ScanCallback mCallback;

    private Map<String, InnerDeviceHolder> mDevices; // map<address,result>

    public BLEDeviceDiscoverer(Context ctx) {
        this.mContext = ctx;
        this.mDevices = new HashMap<>();
    }

    private BluetoothLeScanner getScanner() {
        BluetoothLeScanner scanner = this.mScanner;
        if (scanner != null) {
            return scanner;
        }
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        scanner = adapter.getBluetoothLeScanner();
        this.mScanner = scanner;
        return scanner;
    }

    private ScanCallback getCallback() {
        ScanCallback callback = this.mCallback;
        if (callback != null) {
            return callback;
        }
        callback = new InnerScanCallback();
        this.mCallback = callback;
        return callback;
    }

    public void start() {

        ScanSettings.Builder ssb = new ScanSettings.Builder();

        List<ScanFilter> filters = new ArrayList<>();
        ScanSettings settings = ssb.build();
        ScanCallback callback = getCallback();

        String[] plist = new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
        };
        int code = 0;
        Activity act = (Activity) this.mContext;


        try {
            act.requestPermissions(plist, code);

            BluetoothLeScanner scanner = getScanner();
            scanner.startScan(filters, settings, callback);

        } catch (SecurityException e) {

            e.printStackTrace();
            act.requestPermissions(plist, code);
        }
    }


    public void stop() {
        try {
            ScanCallback callback = getCallback();
            BluetoothLeScanner scanner = getScanner();
            scanner.stopScan(callback);
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    private static class InnerDeviceHolder {

        final BluetoothDevice device;

        InnerDeviceHolder(BluetoothDevice dev) {
            this.device = dev;
        }

        void fetch() {
            this.device.fetchUuidsWithSdp();
        }

        void logSelf(BluetoothDevice dev, StringBuilder sb) {

            String name = getDeviceName(dev);
            String alias = getDeviceAlias(dev);
            String uuids = getDeviceUUIDs(dev);
            String address = dev.getAddress();

            if (uuids == null) {
                return; // skip
            }

            sb.append(" address:").append(address);
            sb.append(" name:").append(name);
            sb.append(" alias:").append(alias);
            sb.append(" uuids:").append(uuids);
        }
    }

    private class InnerScanCallback extends ScanCallback {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            BluetoothDevice dev = result.getDevice();
            String address = dev.getAddress();
            Map<String, InnerDeviceHolder> table = BLEDeviceDiscoverer.this.mDevices;
            StringBuilder sb = new StringBuilder();
            sb.append("[onScanResult callbackType:").append(callbackType);
            InnerDeviceHolder older = table.get(address);

            if (older == null) {
                InnerDeviceHolder holder = new InnerDeviceHolder(dev);
                table.put(address, holder);
                holder.fetch();
            } else {
                older.logSelf(dev, sb);
            }

            sb.append(']');
            Log.i(getTagOf(this), sb.toString());
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.i(getTagOf(this), "onScanFailed, errorCode=" + errorCode);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            Log.i(getTagOf(this), "onBatchScanResults");
        }
    }

    private static String getDeviceName(BluetoothDevice dev) {
        try {
            return dev.getName();
        } catch (SecurityException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    private static String getDeviceAlias(BluetoothDevice dev) {
        try {
            return "unsupported";  //             dev.getAlias();
        } catch (SecurityException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }


    private static String getDeviceUUIDs(BluetoothDevice dev) {

        ParcelUuid[] list = dev.getUuids();
        if (list == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (ParcelUuid uuid : list) {
            sb.append("|").append(uuid);
        }
        return sb.toString();
    }


    private static String getTagOf(Object obj) {
        if (obj == null) {
            return "null";
        }
        return obj.getClass().getName();
    }

}
