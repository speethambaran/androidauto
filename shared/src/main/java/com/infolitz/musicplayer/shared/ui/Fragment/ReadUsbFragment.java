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

public class ReadUsbFragment extends Fragment implements Runnable {

    FragmentReadUsbBinding binding;

    private UsbManager usbManager;
    private UsbDevice usbDevice;

    PendingIntent permissionIntent;
    IntentFilter filter;

    UsbDeviceConnection connection = null;

    UsbEndpoint mEndpointBulkIn;
    UsbEndpoint mEndpointBulkOut;
    UsbInterface usbInterface = null;


    private static int TIMEOUT = 100;
    private boolean forceClaim = true;

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

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


        /*SerialData();*/

    }


   /* private void SerialData() {

        SerialPort serialPort = new SerialPort("/dev/bus/usb/001/005");
        try {
            serialPort.openPort();//Open serial port
            serialPort.setParams(9600, 8, 1, 0);//Set params.
            while (true) {
                byte[] buffer = serialPort.readBytes(10);
                if (buffer != null) {
                    for (byte b : buffer) {
                        Log.d("CD", "Serail port   " + b);
                    }
                }
            }
        } catch (SerialPortException e) {
            Log.d("CD", " exception port   " + e);
            e.printStackTrace();
        }


    }*/


    private void usbManager() {

        if (usbManager != null) {
            HashMap<String, UsbDevice> hostDevice = usbManager.getDeviceList();
            UsbDevice device = null;


            if (hostDevice != null) {
                Iterator<UsbDevice> deviceIterator = hostDevice.values().iterator();
                while (deviceIterator.hasNext()) {
                    device = deviceIterator.next();
                    Log.d("CD", "interface count  " + device.getInterfaceCount());

                }
                Log.d("CD", "Host FOUND " + device);
                usbManager.requestPermission(device, permissionIntent);

                boolean hasPermision = usbManager.hasPermission(device);
                if (hasPermision) {
                    connection = usbManager.openDevice(device);


                    if (connection == null) {
                        return;
                    } else {
                        Log.d("CD", "Got connection " + connection);
                    }
                }
            } else {
                Log.d("CD", "Host EMPTY");
                Toast.makeText(getActivity(), "No device found", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "No device found", Toast.LENGTH_SHORT).show();
        }

    }

    final BroadcastReceiver usbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {

                usbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                    if (usbDevice != null) {
                        //call method to set up accessory communication
                        Log.d("CD", "permission Accepted for device " + usbDevice.getManufacturerName());
                        connectUSB(usbDevice);
                    } else {
                        Toast.makeText(getActivity(), "No Device Found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

    };


    private void connectUSB(UsbDevice device) {

        Log.d("CD", "Connected USb  : " + device.getManufacturerName() + " v ID " + device.getVendorId() + "  p ID " + device.getProductId());
        UsbInterface intf = null;


        int count = device.getInterfaceCount();
        Log.d("CD", "interface count   : " + device.getInterfaceCount());

        for (int i = 0; i < count; i++) {
            intf = device.getInterface(i);
            UsbEndpoint tOut = null;
            UsbEndpoint tIn = null;
            int tEndpointCnt = intf.getEndpointCount();
            Log.d("CD", "End point count   : " + tEndpointCnt);
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
        }


        Log.d("CD", " EIn :" + mEndpointBulkIn.toString());
        Log.d("CD", " EOut:" + mEndpointBulkOut.toString());

        if (usbInterface == null) return;
        Log.d("CD", "Usb interface is  ::" + usbInterface);

        if (device != null) {
//            connection = usbManager.openDevice(device);

            Log.d("CD", "Connection availble  ::" + connection.getFileDescriptor());
            if (connection != null && connection.claimInterface(usbInterface, true)) {
                Log.d("CD", "open SUCCESS");

                Thread thread = new Thread(this);
                thread.start();

            } else {
                Log.d("CD", "open FAIL");
                connection = null;
            }
        }

    }

    @Override
    public void run() {
//        byte[] bytes = new byte[mEndpointBulkOut.getMaxPacketSize()];

        ByteBuffer buffer = ByteBuffer.allocate(mEndpointBulkOut.getMaxPacketSize());
        Log.d("CD", "Usb Buffer   ::" + Arrays.toString(buffer.array()));

        final UsbRequest usbRequest = new UsbRequest();
        boolean data = usbRequest.initialize(connection, mEndpointBulkOut);
        Log.d("CD", "Usb Intailize   ::" + data);

        boolean permission = usbRequest.queue(buffer, mEndpointBulkOut.getMaxPacketSize());
        Log.d("CD", "usbRequest.queue  ::" + permission);

        if (permission) {
            Log.d("CD", "usb request in permission  ::" + permission);
            Log.d("CD", " Device name in permission " + usbDevice.getManufacturerName());

            while (permission) {
                final UsbRequest uRequest = connection.requestWait();
                Log.d("CD", "usb request wait1  ::" + uRequest);
                Log.d("CD", "usb request wait 2 ::" + usbRequest);
                Log.d("CD", "usb interface ::" + usbInterface);

                if (uRequest == usbRequest) {
                    Log.d("CD", "usb request wait Success ");

                    boolean cInterface = connection.claimInterface(usbInterface, forceClaim);


                    if (cInterface) {
                        Log.d("CD", "claim interfaces " + cInterface);

                        int fileDesc = connection.getFileDescriptor();
                        Log.d("CD", "file desc " + fileDesc);
                        byte[] rawDescriptors = connection.getRawDescriptors();
                        Log.d("CD", "raw desc " + Arrays.toString(rawDescriptors));

                        //                            int a = connection.bulkTransfer(mEndpointBulkOut, bytes, mEndpointBulkOut.getMaxPacketSize(), 1000);
                        //
                        ////                        connection.controlTransfer(UsbConstants.USB_ENDPOINT_XFER_INT, 0, 0x00, 0, bytes, 11, 0);
                        ////                        Log.d("CD", "got Data Length Success " + Arrays.toString(bytes));
                        //                            if (a != -1) {
                        //                                Log.d("CD", "got Data Length Success " + a);
                        //                            } else {
                        //                                Log.d("CD", "got Data Length False " + a);
                        //                            }
                        new Thread(this::readData).start();

                    }


                } else {
                    Log.d("CD", "usb request wait does not match ");
                }
            }
        } else {
            Log.d("CD", "usb request in per fail  ::" + permission);
        }
    }

    private byte[] readData() {
        long rStart = System.currentTimeMillis();
        byte[] mReadData = new byte[mEndpointBulkOut.getMaxPacketSize()];
        while (true) {
//            int bytesCount = connection.bulkTransfer(null, mReadData, mReadData.length, 9600);
            int bytesCount = connection.bulkTransfer(mEndpointBulkOut, mReadData, mReadData.length, 9600);
            if (bytesCount >= 0) {
                return mReadData;
            } else if (System.currentTimeMillis() - rStart > 5000) {
                return new byte[]{};
            } else {
                Log.d("CD", String.format("Bulk read  cmd" + bytesCount));
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