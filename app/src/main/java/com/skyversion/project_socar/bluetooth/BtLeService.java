package com.skyversion.project_socar.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by ss on 2016-12-01.
 */
public class BtLeService extends Service {
    private final static String TAG = BtLeService.class.getSimpleName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter btAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt btGatt;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
