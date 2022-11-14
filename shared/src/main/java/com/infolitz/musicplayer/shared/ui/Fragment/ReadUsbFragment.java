package com.infolitz.musicplayer.shared.ui.Fragment;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.infolitz.musicplayer.shared.databinding.FragmentReadUsbBinding;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import me.jahnen.libaums.core.UsbMassStorageDevice;

public class ReadUsbFragment extends Fragment {

    FragmentReadUsbBinding binding;

    private UsbManager usbManager;
    private UsbDevice usbDevice;

    PendingIntent permissionIntent;
    IntentFilter filter, filter1, filter2;
    UsbRequest usbRequest;
    UsbDeviceConnection connection = null;
    UsbMassStorageDevice device;

    UsbEndpoint mEndpointBulkIn;
    UsbEndpoint mEndpointBulkOut;
    UsbInterface usbInterface = null;

    private final byte[] bytes = new byte[64];
    private static int TIMEOUT = 0;
    private boolean forceClaim = true;

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION" ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentReadUsbBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
//        checkSelfPermission();
        usbManager = (UsbManager) getActivity().getSystemService(Context.USB_SERVICE);

        permissionIntent = PendingIntent.getBroadcast(getActivity(), 0, new Intent(ACTION_USB_PERMISSION), 0);


        filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        getActivity().registerReceiver(usbReceiver, filter);


        usbManager();

    }
    private void usbManager() {

        if (usbManager != null) {
            HashMap<String, UsbDevice> hostDevice = usbManager.getDeviceList();
            UsbDevice device = null;
            if (hostDevice != null) {
                Iterator<UsbDevice> deviceIterator = hostDevice.values().iterator();
                while (deviceIterator.hasNext()) {
                    device = deviceIterator.next();
                }
                Log.d("CDEVICE", "Host FOUND " + hostDevice);

                usbManager.requestPermission(device, permissionIntent);
                boolean hasPermision = usbManager.hasPermission(device);
                if (hasPermision) {
                    connection = usbManager.openDevice(device);
                    if (connection == null) {
                        return;
                    } else {
                        Log.d("CDEVICE", "Got connection ");
                    }
                }
            } else {
                Log.d("CDEVICE", "Host EMPTY");
                Toast.makeText(getActivity(), "No device found", Toast.LENGTH_SHORT).show();
            }
        }

    }

    final BroadcastReceiver usbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("CDEVICE", "Action is : " + action);
            if (ACTION_USB_PERMISSION.equals(action)) {

                usbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                    if (usbDevice != null) {
                        //call method to set up accessory communication
                        Log.d("CDEVICE", "permission Accepted for device " + usbDevice.getManufacturerName());
                        connectUSB(usbDevice);
                    }
                }
            } /*else if (UsbManager.ACTION_USB_DEVICE_ATTACHED == action) {
                usbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                Log.d("CDEVICE", "USB device attached" + usbDevice.getManufacturerName());
                if (usbDevice != null) {
                    connectUSB(usbDevice);
                }
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED == action) {
                usbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                Log.d("CDEVICE", "USB device Detached" + usbDevice.getManufacturerName());
                if (usbDevice != null) {

                }
            }*/
        }

    };
    private void connectUSB(UsbDevice device) {

        Log.d("CDEVICE", "Connect USb  : " + device.getManufacturerName());

        UsbInterface intf = null;


//        int packetSize = endpoint.getMaxPacketSize();
//        Log.d("CDEVICE", "Packet size  : " + packetSize);
        int count = device.getInterfaceCount();
        Log.d("CDEVICE", "interface count   : " + count);

        for (int i = 0; i < count; i++) {
            intf = device.getInterface(i);
            UsbEndpoint tOut = null;
            UsbEndpoint tIn = null;
            int tEndpointCnt = intf.getEndpointCount();

            if (tEndpointCnt >= 2) {
                for (int j = 0; j < tEndpointCnt; j++) {
                    if (intf.getEndpoint(j).getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                        //Bulk endpoint type ??
                        if (intf.getEndpoint(j).getDirection() == UsbConstants.USB_DIR_OUT)
                            tOut = intf.getEndpoint(j);
                        else if (intf.getEndpoint(j).getDirection() == UsbConstants.USB_DIR_IN)
                            tIn = intf.getEndpoint(j);
                    }
                }
                if (tOut != null && tIn != null) {

                    usbInterface = intf;
                    mEndpointBulkIn = tIn;
                    mEndpointBulkOut = tOut;
                }
            }

            Log.d("CDEVICE", "USB  UI, EIN ,EOUT  :" + usbInterface + mEndpointBulkIn + mEndpointBulkOut);

            if (usbInterface == null) return;
            Log.d("CDEVICE", "Usb interface is  ::" + usbInterface);

            if (device != null) {
                if (usbManager.hasPermission(device)) {
                    Log.d("CDEVICE", "Usb has permission ::" + usbManager.hasPermission(device));
                    connection = usbManager.openDevice(device);

                    Log.d("CDEVICE", "Connection availble  ::" + connection);
                    if (connection != null && connection.claimInterface(usbInterface, true)) {

                        final int inMax = tIn.getMaxPacketSize();
                        final ByteBuffer buffer = ByteBuffer.allocate(inMax);
                        final UsbRequest usbRequest = new UsbRequest();
                        boolean data = usbRequest.initialize(connection, mEndpointBulkIn);


                        Log.d("CDEVICE", "Usb Intailize   ::" + data);


                        Boolean permission = usbRequest.queue(buffer, inMax);
                        Log.d("CDEVICE", "usbRequest.queue  ::" + permission);

                        if (permission == true) {
                            Log.d("CDEVICE", "usb request in permission  ::" + permission);
                            Log.d("CDEVICE", " DEvice name in permission " + device.getManufacturerName());

                            while (true) {
                                final UsbRequest uRequest = connection.requestWait();
                                Log.d("CDEVICE", "usb request wait  ::" + uRequest);
                                if (uRequest != null) {
                                    byte[] array = buffer.array();

                                    Log.d("CDEVICE", "usb request wait Success " + Arrays.toString(array));
                                } else {
                                    Log.d("CDEVICE", "usb request wait does not match ");
                                }
                            }
                        } else {
                            Log.d("CDEVICE", "usb request in per   ::" + permission);
                        }
                    }


                } else {
                    Toast.makeText(getActivity(), "test??", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getActivity(), "testUSBtest", Toast.LENGTH_LONG).show();
            }
        }
    }

    private Boolean checkSelfPermission() {

        List<String> permissionsList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE);
        }

        if (permissionsList.size() > 0) {
            ActivityCompat.requestPermissions(getActivity(), permissionsList.toArray(new String[permissionsList.size()]), 1);
        }


        return true;
    }

}