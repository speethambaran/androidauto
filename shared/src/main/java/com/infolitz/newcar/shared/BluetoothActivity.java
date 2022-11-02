package com.infolitz.newcar.shared;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Set;
import java.util.UUID;

public class BluetoothActivity extends AppCompatActivity {


    private static String TimeZone;
    private TextView mBluetoothStatus;
    private TextView mReadBuffer;
    //    private Button mOffBtn;
//    private Button mListPairedDevicesBtn;
    private Button mDiscoverBtn;
    private BluetoothAdapter mBTAdapter;
    private Set<BluetoothDevice> mPairedDevices;
    private ArrayAdapter<String> mBTArrayAdapter;
    private ListView mDevicesListView;
    //    private CheckBox mLED1;
    String SSID, Connection;
    Boolean status;
    ImageView imageView;
    LinearLayout sendlayout;


    private Handler mHandler; // Our main handler that will receive callback notifications
    private ConnectedThread mConnectedThread; // bluetooth background worker thread to send and receive data
    private BluetoothSocket mBTSocket = null; // bi-directional client-to-client data path

    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier


    // #defines for identifying shared types between calling functions
    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status

    public static void time(String timeZone) {

        TimeZone = timeZone;

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);


//        mOffBtn = (Button) findViewById(R.id.off);
        mDiscoverBtn = (Button) findViewById(R.id.discover);
//        mListPairedDevicesBtn = (Button) findViewById(R.id.PairedBtn);
//        mLED1 = (CheckBox) findViewById(R.id.checkboxLED1);
        sendlayout = findViewById(R.id.action_send);

        mBTArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        // get a handle on the bluetooth radio
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();

        mDevicesListView = (ListView) findViewById(R.id.devicesListView);
        mDevicesListView.setAdapter(mBTArrayAdapter); // assign model to view
        mDevicesListView.setOnItemClickListener(mDeviceClickListener);

        SSID = getIntent().getStringExtra("SSID");

        //Sending Default timezone is UTC+5:30


        if (TimeZone == null) {

            TimeZone = "UTC+05:30";

        }

        //Setting Credentials Send Alert Box......

        sendlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (status = true) {

                    final AlertDialog.Builder builder = new AlertDialog.Builder(BluetoothActivity.this);
//                    final View customLayout = getLayoutInflater().inflate(R.layout.custom_dialog, null);
//                    final EditText editText = customLayout.findViewById(R.id.et_ssid);
//                    final EditText editText1 = customLayout.findViewById(R.id.et_pass);
//                    Button send = customLayout.findViewById(R.id.btn_send);
//                    Button skip = customLayout.findViewById(R.id.skip);

                    //Setting Wifi SSID in AlertBox

//                    editText.setText(SSID);
//                    builder.setView(customLayout);

                    //Setting Action For Skip Button in Alert Dialog Box....

//                    skip.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            final String Command = "command:skip";
//                            final String data_cmd = "data_cmd:" + 0;
//
//                            //Sending Command:Skip.....
//
//                            mConnectedThread.writeskip(Command, data_cmd);
//                            Toast.makeText(getApplicationContext(), "Credentials Skipped", Toast.LENGTH_LONG).show();
//                            Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
//                            startActivity(intent);
//
//                        }
//                    });

                    //Send Credentials to Device........

//                    send.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//
//                            String password = editText1.getText().toString();
//                            final String wifi = "wifi_ssid:" + SSID;
//                            final String wifi_pass = "wifi_password:" + password;
//                            final String data = "data_exit:" + 0;
//
//
//                            if (mConnectedThread != null) //First check to make sure thread created
//                            {
//                                //Sending Password and timezone......
//
//                                mConnectedThread.write(wifi, wifi_pass, "time_zone:" + TimeZone, data);
//                                Intent intent = new Intent(getApplicationContext(), Activity.class);
//                                startActivity(intent);
//                            } else {
//                                Toast.makeText(BluetoothActivity.this, "Sending Failed..", Toast.LENGTH_SHORT).show();
//                            }
//
//                        }
//                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

        //Managing Handler.......

        mHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == MESSAGE_READ) {
                    String readMessage = null;
                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    mReadBuffer.setText(readMessage);
                }


                if (msg.what == CONNECTING_STATUS) {
                    if (msg.arg1 == 1) {
                        Toast.makeText(getApplicationContext(), "Connected to Device", Toast.LENGTH_LONG).show();
                        getTime();
                        status = true;
                    } else {
                        Toast.makeText(getApplicationContext(), "Connection Failed Connect Again..", Toast.LENGTH_LONG).show();
                        status = false;
                    }
                }
            }
        };

        if (mBTArrayAdapter == null) {
            Toast.makeText(getApplicationContext(), "Bluetooth device not found!", Toast.LENGTH_SHORT).show();
        } else {

            mDiscoverBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    discover(v);
                }
            });
        }
    }

    // Enter here after user selects "yes" or "no" to enabling radio

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent Data) {

        // Check which request we're responding to

        super.onActivityResult(requestCode, resultCode, Data);
        if (requestCode == REQUEST_ENABLE_BT) {

            // Make sure the request was successful

            if (resultCode == RESULT_OK) {

                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                mBluetoothStatus.setText("Enabled");
            } else
                mBluetoothStatus.setText("Disabled");
        }
    }

    private void discover(View view) {
        Log.e("control in","inside discover");

        // Check if the device is already discovering
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, 0);
            }
        }else {
            Log.e("control in", "SDK_INT else");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        if (mBTAdapter.isDiscovering()) {
            Log.e("control in", "isDiscovering()");
            Toast.makeText(getApplicationContext(), "Discovering...", Toast.LENGTH_SHORT).show();
        } else {
            Log.e("control in", "isDiscovering() else");
            if (mBTAdapter.isEnabled()) {
                Log.e("control in", "isEnabled()");
                mBTArrayAdapter.clear(); // clear items
                mBTAdapter.startDiscovery();
                Toast.makeText(getApplicationContext(), "Discovery started", Toast.LENGTH_SHORT).show();
                registerReceiver(blReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            } else {
                Log.e("control in", "isEnabled() else");
                Toast.makeText(getApplicationContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
            }
        }
    }

    final BroadcastReceiver blReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("control in","on onReceive");
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Log.e("control in","on ACTION_FOUND");
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                // add the name to the list

                if (ActivityCompat.checkSelfPermission(BluetoothActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    Log.e("control in","on checkSelfPermission for BLUETOOTH_CONNECT");
                }
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                Log.e("devices",device.getName() + "\n" + device.getAddress());
                mBTArrayAdapter.notifyDataSetChanged();
            }
        }
    };

    //Setting The Device Selection Action

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

            if (!mBTAdapter.isEnabled()) {
                Toast.makeText(getBaseContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(getApplicationContext(), "Connecting Please Wait ....", Toast.LENGTH_LONG).show();

            // mBluetoothStatus.setText("Connecting...");
            // Get the device MAC address, which is the last 17 chars in the View

            String info = ((TextView) v).getText().toString();
            final String address = info.substring(info.length() - 17);
            final String name = info.substring(0, info.length() - 17);

            // Spawn a new thread to avoid blocking the GUI one

            new Thread() {
                public void run() {
                    boolean fail = false;

                    BluetoothDevice device = mBTAdapter.getRemoteDevice(address);

                    try {
                        mBTSocket = createBluetoothSocket(device);
                    } catch (IOException e) {
                        fail = true;
                        Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                    }

                    // Establish the Bluetooth socket connection.

                    try {
                        if (ActivityCompat.checkSelfPermission(BluetoothActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        mBTSocket.connect();
                    } catch (IOException e) {
                        try {
                            fail = true;
                            mBTSocket.close();
                            mHandler.obtainMessage(CONNECTING_STATUS, -1, -1)
                                    .sendToTarget();
                        } catch (IOException e2) {
                            //insert code to deal with this
                            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                    if (fail == false) {
                        mConnectedThread = new ConnectedThread(mBTSocket);
                        mConnectedThread.start();

                        mHandler.obtainMessage(CONNECTING_STATUS, 1, -1, name)
                                .sendToTarget();
                    }
                }
            }.start();

        }
    };

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connection with BT device using UUID
    }


    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.available();
                    if (bytes != 0) {
                        SystemClock.sleep(100); //pause and wait for rest of data. Adjust this depending on your sending speed.
                        bytes = mmInStream.available(); // how many bytes are ready to be read?
                        bytes = mmInStream.read(buffer, 0, bytes); // record how many bytes we actually read
                        mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                                .sendToTarget(); // Send the obtained bytes to the UI activity
                    }
                } catch (IOException e) {
                    e.printStackTrace();

                    break;
                }
            }
        }


        public void writeskip(String skipdata, String data0) {

            byte[] bytes = new byte[1024];
            int bufferPosition = 0;
            String seperate = ",";

            String[] myFields = new String[]{skipdata, seperate, data0};

            for (String field : myFields) {
                byte[] stringBytes = field.getBytes();  // get bytes from string
                System.arraycopy(stringBytes, 0, bytes, bufferPosition, stringBytes.length);  // copy src to dest
                bufferPosition += stringBytes.length;  // advance index

            }
//
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
            }

        }


        /* Call this from the main activity to send data to the remote device */
        public void write(String inputssid, String inputpass, String currenttime, String data) {


            byte[] bytes = new byte[1024];
            int bufferPosition = 0;
            String seperate = ",";

            String[] myFields = new String[]{inputssid, seperate, inputpass, seperate, currenttime, seperate, data};

            for (String field : myFields) {
                byte[] stringBytes = field.getBytes();  // get bytes from string
                System.arraycopy(stringBytes, 0, bytes, bufferPosition, stringBytes.length);  // copy src to dest
                bufferPosition += stringBytes.length;  // advance index

            }

            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
            }
        }

        /* Call this from the main activity to send time to the remote device */
        public void writeTime(long timeStamp) {

            String time = " current_time-" + DateFormat.format("hh:mm:ss", timeStamp).toString();
            String date = "date-" + DateFormat.format("MM:dd:yyyy", timeStamp).toString();
            String data = "end_frame+0";
            Log.d("TIMEZZAND", "time  is : " + time);
            Log.d("TIMEZZAND", "Date is : " + date);

            byte[] bytes = new byte[1024];
            int bufferPosition = 0;
            String seperate = ",";

            String[] myFields = new String[]{time, seperate, date, seperate, data};

            for (String field : myFields) {
                byte[] stringBytes = field.getBytes();  // get bytes from string
                System.arraycopy(stringBytes, 0, bytes, bufferPosition, stringBytes.length);  // copy src to dest
                bufferPosition += stringBytes.length;  // advance index

            }

            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
            }
        }


        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }


    private void getTime() {

        long timestamp = System.currentTimeMillis();
        mConnectedThread.writeTime(timestamp);

    }


}