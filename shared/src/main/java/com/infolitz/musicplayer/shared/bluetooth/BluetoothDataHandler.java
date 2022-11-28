package com.infolitz.musicplayer.shared.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.infolitz.musicplayer.shared.ui.Activity.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class BluetoothDataHandler extends BaseActivity {

    public Context mContext;

    private Handler mHandler;

    public BluetoothDataHandler(Context mContext) {
        this.mContext = mContext;

    }


    public void initBluetoothAndCallbacks(BluetoothDevice device, Context context) {
        Log.d(TAG, " initialized BluetoothManager. ");
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return;
        }
        initBleGattCallback();
        mBluetoothGatt = device.connectGatt(context, true, mGattCallback);

    }

    private void initBleGattCallback() {
        mHandler = new Handler();


        mGattCallback = new BluetoothGattCallback() {
            List<BluetoothGattCharacteristic> chars = new ArrayList<>();

            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                String intentAction;
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.e(TAG, "STATE_CONNECTED" + newState);
//                    getBluetoothGatt().discoverServices();


                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.e(TAG, "STATE_DISCONNECTED  " + newState);

                    Log.w(TAG, "STATE_DISCONNECTED");

                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                // this worked, but switching to sensor data leads to disconnecton.
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    gatt.requestMtu(247);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                        }
                    }, 1000);


                } else {
                    Log.w(TAG, "onServicesDiscovered received: " + status);
                }
            }


            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {

                }
            }

            @Override
            public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {

                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.d(TAG, String.format("descriptor written"));
                }
            }

            @Override
            public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.d(TAG, String.format("Reliable Write completed"));
                } else {
                    Log.d(TAG, String.format("Reliable Write called, but error"));
                }
            }

            /**
             * This callback is invoked when the client writes to a characteristic
             * @param gatt Bluetooth Gatt variable
             * @param characteristic Characteristic that is subscribed
             * @param status status of the Write operation
             */
            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.d("TAG_USER", "Succssfully written");
                }
            }

            /**
             * This callback is invoked when the data of the characteristic is modified by the Server
             *
             * @param gatt Bluetooth Gatt variable
             * @param characteristic Characteristic that is subscribed
             */
            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

            }
        };
    }

    private BluetoothGatt getBluetoothGatt() {
        return mBluetoothGatt;
    }
}