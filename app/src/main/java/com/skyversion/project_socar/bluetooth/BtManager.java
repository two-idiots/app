package com.skyversion.project_socar.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by ss on 2016-11-28.
 */
public class BtManager {

    private BluetoothAdapter btAdapter;
    private BluetoothManager btManager;
    private BluetoothGatt btGatt;

    private BtLeService btLeService;

    private Toast toast;
    private Context mContext;
    private ArrayList<BluetoothDevice> btDevicesList;

    private static final long SCAN_PERIOD = 10000;

    private final Handler mHandler = new Handler();

    private final static String TAG = BtManager.class.getSimpleName();

    public BtManager(Context mContext, Toast toast) {
        this.mContext = mContext;
        this.toast = toast;
        btDevicesList = new ArrayList<BluetoothDevice>();
        btLeService = new BtLeService();

        if(!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
            showToastMessage("해당 기기에서는 블루투스 기능이 제공되지 않습니다.");
            System.exit(0);
        }

        btManager = (BluetoothManager)mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();

        if(btAdapter == null){
            showToastMessage("해당 기기에서는 블루투스 기능이 제공되지 않습니다.");
            System.exit(0);
        }
    }

    public void showToastMessage(final String msg){
        toast.setText(msg);
        toast.show();
    }

    public boolean isBluetoothEnable(){
        if(btAdapter.isEnabled())
            return false;

        return true;
    }

    public void enable(){
        btAdapter.enable();
    }

    public void disable(){
        btAdapter.disable();
    }

    public void scanLeDevice(final boolean enable){
        btDevicesList.clear();

        if(enable){
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showToastMessage("블루투스 디바이스 스캔이 완료되었습니다.");
                    btAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            btAdapter.startLeScan(mLeScanCallback);
        }else
            btAdapter.stopLeScan(mLeScanCallback);
    }

    public void showDeviceList(){
        int size = btDevicesList.size();

        for (int i=0;i<size;i++){
            Log.d("BtList " + i , btDevicesList.get(i).getName() + " / " + btDevicesList.get(i).getAddress());
        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice btDevice, int rssi, byte[] scanRecord) {
            if(!btDevicesList.contains(btDevice))
                btDevicesList.add(btDevice);
        }
    };

    public boolean connect(final String address){
        if(btAdapter == null || address == null){
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        final BluetoothDevice device = btAdapter.getRemoteDevice(address);

        if(device == null){
            Log.w(TAG, "Device not found. Unable to connect.");
            return false;
        }

        btGatt = device.connectGatt(mContext, false, mGattCallback);
        btGatt.connect();

        return true;
    }

    public void disconnect(){
        if(btAdapter == null || btGatt == null){
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        btGatt.disconnect();
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if(newState == BluetoothProfile.STATE_CONNECTED){

            }else if(newState == BluetoothProfile.STATE_DISCONNECTED){

            }
        }

        @Override
        // New services discovered
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
//            super.onCharacteristicRead(gatt, characteristic, status);

        }
    };

    public void close(){
        if(btGatt == null)
            return;

        btGatt .close();
        btGatt = null;
    }
}