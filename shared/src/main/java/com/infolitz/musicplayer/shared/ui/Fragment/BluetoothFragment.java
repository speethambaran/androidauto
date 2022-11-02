package com.infolitz.musicplayer.shared.ui.Fragment;

import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.infolitz.musicplayer.shared.databinding.FragmentBluetoothBinding;

import java.util.ArrayList;
import java.util.List;


public class BluetoothFragment extends BaseFragments {
    FragmentBluetoothBinding binding;
    BluetoothAdapter bluetoothAdapter;
    private ArrayAdapter<String> mBTArrayAdapter;
    private String mBluetoothDeviceAddress;


    // #defines for identifying shared types between calling functions
    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    private final static int REQUEST_LOCATION = 4;

    private boolean permissionOk = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentBluetoothBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        setPermissions();
        turnOnBluetooth();
        mBTArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
        binding.deviceList.setAdapter(mBTArrayAdapter); // assign model to view
        binding.deviceList.setOnItemClickListener(mDeviceClickListener);

        binding.btnGet.setOnClickListener(view -> {
            checkPermissionAndDiscover();
        });
    }

    private void checkPermissionAndDiscover() {

        // Check if the device is already discovering
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            isDiscoveringOn12();
            turnOnBluetooth();
            Log.e("qwerty", "On 12");
        } else {
            discover();
            turnOnBluetooth();
            Log.e("qwerty", "below 12");
        }
    }

    private void discover() {

        // Check if the device is already discovering

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {

            if (bluetoothAdapter.isDiscovering()) {
                Toast.makeText(getActivity(), "Discovering...", Toast.LENGTH_SHORT).show();
            } else {
                if (bluetoothAdapter.isEnabled()) {
                    mBTArrayAdapter.clear(); // clear items
                    bluetoothAdapter.startDiscovery();
                    Toast.makeText(getActivity(), "Discovery started", Toast.LENGTH_SHORT).show();
                    getActivity().registerReceiver(blReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
                } else {
                    Toast.makeText(getActivity(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
                }
            }
            return;
        }
    }


    private void isDiscoveringOn12() {

        if ((ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) && (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) && (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH_ADVERTISE) == PackageManager.PERMISSION_GRANTED)) {

            if (bluetoothAdapter.isDiscovering()) {
                Toast.makeText(getActivity(), "Discovering...", Toast.LENGTH_SHORT).show();
            } else {
                if (bluetoothAdapter.isEnabled()) {
                    mBTArrayAdapter.clear(); // clear items
                    bluetoothAdapter.startDiscovery();
                    Toast.makeText(getActivity(), "Discovery started", Toast.LENGTH_SHORT).show();
                    getActivity().registerReceiver(blReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
                } else {
                    Toast.makeText(getActivity(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
                }
            }
            return;
        } else {
            checkSelfPermissionOn12();
        }
    }

    final BroadcastReceiver blReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                // add the name to the list
                if (permissionOk) {
                    mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                    mBTArrayAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(context, "Please allow bluetooth", Toast.LENGTH_SHORT).show();
                    setPermissions();
                }
            }
        }
    };

    private void setPermissions() {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}
                    , REQUEST_LOCATION);
        } else {

            if (bluetoothAdapter == null) {
//
                Toast.makeText(getActivity(), "Device not support bluetooth", Toast.LENGTH_SHORT).show();
            } else {
                permissionOk = true;
            }
        }
    }

    //Setting The Device Selection Action

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {


            if (!bluetoothAdapter.isEnabled()) {
                Toast.makeText(getActivity(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
                return;
            }


            // mBluetoothStatus.setText("Connecting...");
            // Get the device MAC address, which is the last 17 chars in the View

            String info = ((TextView) v).getText().toString();
            final String address = info.substring(info.length() - 17);
            final String name = info.substring(0, info.length() - 17);
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);

            if (device == null) {
                Log.w(TAG, "Device not found.  Unable to connect.");
                return;
            }
            Log.d(TAG, "Trying to create a new connection.");
            mBluetoothDeviceAddress = address;
            Log.d(TAG, "Connecting");

        }
    };


    private void turnOnBluetooth() {
        if (bluetoothAdapter.isEnabled()) {
            Toast.makeText(getActivity(), "Bluetooth always on", Toast.LENGTH_SHORT).show();
            return;
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Intent panelIntent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                startActivity(panelIntent);
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
        checkSelfPermission();
    }

    private Boolean checkSelfPermission() {

        List<String> permissionsList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (permissionsList.size() > 0) {
            ActivityCompat.requestPermissions(getActivity(), permissionsList.toArray(new String[permissionsList.size()]),
                    1);
        }

        turnOnLocation();

        return true;
    }
//Checking android 12 permissions

    private Boolean checkSelfPermissionOn12() {

        List<String> permissionsList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.BLUETOOTH_ADVERTISE);
        }

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.BLUETOOTH_CONNECT);
        }
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.BLUETOOTH_SCAN);
        }


        if (permissionsList.size() > 0) {
            ActivityCompat.requestPermissions(getActivity(), permissionsList.toArray(new String[permissionsList.size()]),
                    1);
        }

        turnOnLocation();
        return true;
    }


    private void displayLocationSettingsRequest(Context context) {
        final int REQUEST_CHECK_SETTINGS = 0x1;
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(PRIORITY_HIGH_ACCURACY)
                .setInterval(10000)
                .setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }

    public boolean isLocationEnabled() {
        // This is new method provided in API 28
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            LocationManager lm = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            return lm.isLocationEnabled();
        } else {
            int mode = Settings.Secure.getInt(getActivity().getApplicationContext().getContentResolver(), Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF);
            return (mode != Settings.Secure.LOCATION_MODE_OFF);
        }
    }

    private void turnOnLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (isLocationEnabled()) {

                LocationManager lman = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
                boolean network_enabled = false;

                try {
                    network_enabled = lman.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                } catch (Exception ignored) {
                }

                if (!network_enabled) {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            } else {
                displayLocationSettingsRequest(getActivity().getApplicationContext());
            }
        }
    }

}