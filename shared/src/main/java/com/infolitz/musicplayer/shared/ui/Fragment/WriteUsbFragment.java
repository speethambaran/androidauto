package com.infolitz.musicplayer.shared.ui.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.infolitz.musicplayer.shared.databinding.FragmentWriteUsbBinding;

import java.nio.ByteBuffer;
import java.util.Arrays;


public class WriteUsbFragment extends Fragment {

    FragmentWriteUsbBinding binding;

    private UsbInterface usbInterfaceFound = null;
    private UsbEndpoint endpointOut = null;
    private UsbEndpoint endpointIn = null;
    private UsbManager mUsbManager;
    private boolean LoopSerial = true;
    private UsbDeviceConnection mUsbDeviceConnection;
    private Thread mSerialInThread;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentWriteUsbBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        getActivity().registerReceiver(MyUsbBoradCastReceiver, intentFilter);
    }

    private BroadcastReceiver MyUsbBoradCastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {

                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (null != device) {

                    operateDevice(device);
                    Toast.makeText(getActivity(), "attached", Toast.LENGTH_LONG).show();
                }

            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {

                Toast.makeText(getActivity(), "USBtest", Toast.LENGTH_LONG).show();
            }
        }
    };


    private void operateDevice(UsbDevice device) {

        mUsbManager = (UsbManager) getActivity().getSystemService(Context.USB_SERVICE);

        //HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();

        int interfaceCount = device.getInterfaceCount();
        for (int i = 0; i < interfaceCount; i++) {
            UsbInterface usbif = device.getInterface(i);
            UsbEndpoint tOut = null;
            UsbEndpoint tIn = null;

            int tEndpointCnt = usbif.getEndpointCount();
            if (tEndpointCnt >= 2) {
                for (int j = 0; j < tEndpointCnt; j++) {
                    if (usbif.getEndpoint(j).getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                        //Bulk endpoint type ??
                        if (usbif.getEndpoint(j).getDirection() == UsbConstants.USB_DIR_OUT)
                            tOut = usbif.getEndpoint(j);
                        else if (usbif.getEndpoint(j).getDirection() == UsbConstants.USB_DIR_IN)
                            tIn = usbif.getEndpoint(j);
                    }
                }
                if (tOut != null && tIn != null) {

                    usbInterfaceFound = usbif;
                    endpointIn = tIn;
                    endpointOut = tOut;
                }
            }
            if (null == usbInterfaceFound)
                return;

            if (null != device) {

                if (mUsbManager.hasPermission(device)) {
                    mUsbDeviceConnection = mUsbManager.openDevice(device);
                    if (null != mUsbDeviceConnection
                            && mUsbDeviceConnection.claimInterface(usbInterfaceFound, true)) {

                        sendCommand(25);

                        final int inMax = tIn.getMaxPacketSize();
                        final ByteBuffer buffer = ByteBuffer.allocate(inMax);
                        final UsbRequest usbRequest = new UsbRequest();
                        usbRequest.initialize(mUsbDeviceConnection, endpointIn);


                        mSerialInThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (LoopSerial) {
                                    usbRequest.queue(buffer, inMax);
                                    if (mUsbDeviceConnection.requestWait() == usbRequest) {
                                        byte[] array = buffer.array();
                                        if (array.length != 0) {

                                            binding.mTextView.setText(Arrays.toString(array));

                                        }
                                        SystemClock.sleep(100);
                                    } else {
                                        break;
                                    }
                                }
                            }
                        });
                    } else {
                        Toast.makeText(getActivity(), "test??", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "testUSBtest", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    private void sendCommand(int control) {
        synchronized (this) {
            if (null != mUsbDeviceConnection) {
                byte[] message = new byte[1];
                message[0] = (byte) control;
                Toast.makeText(getActivity(), "" + message[0], Toast.LENGTH_SHORT).show();
                mUsbDeviceConnection.bulkTransfer(endpointOut, message, message.length, 0);
            }

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mSerialInThread) {
            LoopSerial = false;
            mSerialInThread.isInterrupted();
            mSerialInThread = null;
        }
    }


}