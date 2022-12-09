package com.infolitz.commwithc.shared;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.TextView;

import com.github.anastr.speedviewlib.Speedometer;
import com.github.anastr.speedviewlib.components.indicators.Indicator;
import com.infolitz.commwithc.shared.wrapper.PortflagWrapper;
import com.infolitz.mycarspeed.shared.CommunicateWithC;

import java.util.Locale;

import kotlin.jvm.functions.Function2;

public class SpeedRpmActivity extends AppCompatActivity {

    Speedometer speedometer, rpmMeter;
    //... for uart
    CommunicateWithC communicateWithC = new CommunicateWithC();//for kotlin communication with cpp_code
    boolean stop = false;
    TextView textView_c;
    //uart communication close
    //for thread
    Handler handler = new Handler();
    PortflagWrapper portflagWrapper = new PortflagWrapper(/*-1*/);
    //for thread...close...


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed_rpm);

        speedometer = findViewById(R.id.speedometer);
        rpmMeter = findViewById(R.id.imageSpeedometer);
        textView_c = findViewById(R.id.text_c_1);

       /* withRotation = findViewById(R.id.cb_withRotation);
        seekBarTickNumbers = findViewById(R.id.seekBarStartDegree);
        seekBarTickPadding = findViewById(R.id.seekBarTickPadding);
        textTicks = findViewById(R.id.textTickNumber);
        textTickPadding = findViewById(R.id.textTickPadding);*/

//        speedometer.setBackgroundColor(getResources().getColor(R.color.white)); //getting the background color as white
//        speedometer.setBackgroundCircleColor(getResources().getColor(R.color.white));
        speedometer.speedPercentTo(0);
        speedometer.setTickNumber(11); // getting the distance to be 10,20..
        speedometer.setTickPadding(40); // getting the speedometer marking inside the circle
        speedometer.setTickRotation(false); // text inside speedometer not in rotated way
        speedometer.setSpeedTextColor(Color.WHITE);// setting displayed current speed color
        speedometer.setUnitTextColor(Color.WHITE); // color to Km/h
        speedometer.setTextColor(Color.WHITE); //setting the interior speed color
        speedometer.setWithTremble(false); // deactivated the trembling or the indicator flickering


        rpmMeter.setIndicator(Indicator.Indicators.HalfLineIndicator);
        rpmMeter.getIndicator().setWidth(speedometer.dpTOpx(5f));
        rpmMeter.setSpeedTextColor(Color.WHITE);
        rpmMeter.setUnitTextColor(Color.WHITE);
        rpmMeter.speedPercentTo(0);


        //... for uart......
        portflagWrapper.setPort(-1);
        callStringg();//call uart
        //...for uart end
        //for python
       /* if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
            Log.e("in python"," success");
        }

        *//*Python py = Python.getInstance();
        PyObject module = py.getModule("uart1");*//*
        dataReturned=communicateWithC.pyKot();
        textView_c.setText("" + dataReturned);*/

        //for python...close


        speedometer.setOnPrintTickLabel(new Function2<Integer, Float, CharSequence>() {
            @Override
            public CharSequence invoke(Integer tickPosition, Float tick) {
                if (tick == 0) {
                    SpannableString s = new SpannableString(String.format(Locale.getDefault(), "%.1f", tick));
                    s.setSpan(new ForegroundColorSpan(0xffff1117), 0, 1, 0);
                    return s;
                }
                return null;
            }
        });
    }

    @Override
    protected void onResume() {

        super.onResume();
        stop = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        stop = true;
    }

    @Override
    protected void onPause() {

        super.onPause();
        stop = true;

    }


    //for uart communication....
    private void callStringg() {
        portflagWrapper.setPort(-1);
        portflagWrapper.setSpeed(0.00F);
        portflagWrapper.setRpm(0.00F);
//        communicateWithC.useThreadTestLib(/*portflagWrapper.getPort()*/);
        final Runnable runnable = new Runnable() {
            int flagPort = -1;
            String ourString;// returned string
            Float rpmCount, speedCount;

            public void run() {
//                textView_c.setText("0" + textView_c.getText().toString());
//                while (true) {
                try {
//                        Thread.sleep(3000);
//                    communicateWithC.useThreadTestLib(/*portflagWrapper.getPort()*/);
                    communicateWithC.useShortCommLib(/*portflagWrapper.getPort()*/);
//                    communicateWithC.useThreadTestLib(/*portflagWrapper.getPort()*/);
//                    ourString = communicateWithC.useShortCommLib(/*portflagWrapper.getPort()*/);
                    Thread.sleep(300);
//                    Log.e("java: port value", "" + portflagWrapper.getPort());
//                    Log.e("from java: testing our string", "" + ourString);
//                    speedCount = callValueCalcuSpeed(ourString);
//                    rpmCount = callValueCalcuRpm(ourString);
//                    if (portflagWrapper.getPort() == -1) {
////                        flagPort = callPort(ourString);// extract port number
//                        Log.e("port value from java: is:", "" + flagPort);
//                        portflagWrapper.setPort(flagPort);
//                    }
                    Log.e("java:now our port value is ", "" + portflagWrapper.getPort());
//                    Log.e("from java speed", "" + speedCount);
//                    Log.e("from java rpm", "" + rpmCount);
//                    textView_c.setText("speed= " + speedCount + " rpm=" + rpmCount);
                    textView_c.setText("speed= " + portflagWrapper.getSpeed() + " rpm=" + portflagWrapper.getRpm());
//                    speedometer.speedTo(speedCount);
                    speedometer.speedTo(portflagWrapper.getSpeed());
//                    rpmMeter.speedTo(rpmCount);
                    rpmMeter.speedTo(portflagWrapper.getRpm());
//                        }
                    handler.postDelayed(this, 3000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
               /* bundle.putString("speedString",speed1);
                bundle.putString("rpmString",rpmCount.toString());
                sMessage.setData(bundle);
                sMessage.sendToTarget();*/
                // handler.removeMessages(0);
//                handler.sendMessage(sMessage); //send data to handler
//                }
            }
        };

        handler.postDelayed(runnable, 1000);
       /* Thread rThread =new Thread(runnable);
        rThread.start();*/
    }

    private Float callValueCalcuSpeed(String s) {
        String speed;
        char[] spd1 = new char[5];


        String[] arrOfSpedd = s.split("d=", 2); //(for input speed = 30 rpm = 40)
//        String[] arrOfSpedd = s.split("!", 2); //( for input : 30!40?)!35? ; 67
        speed = arrOfSpedd[1];
        for (int i = 0; i < 2; i++) {
            if (Character.isDigit(speed.charAt(i))) {
                spd1[i] = speed.charAt(i);
            }

        }

        return Float.parseFloat(String.valueOf(spd1));
    }

    private Float callValueCalcuRpm(String s) {
        String rpm;

        char[] rpmcnt1 = new char[5];


        String[] arrOfRpm = s.split("m=", 2); //(for input speed = 30 rpm = 40)
//        String[] arrOfRpm = s.split("!", 2); //(30!40?)
        rpm = arrOfRpm[1];
        for (int i = 0; i < 2; i++) {
            if (Character.isDigit(rpm.charAt(i))) {
                rpmcnt1[i] = rpm.charAt(i);
            }
        }
        return Float.parseFloat(String.valueOf(rpmcnt1));
    }

    private int callPort(String s) {
        String port;

        char[] rpmcnt1 = new char[5];


        String[] arrOfRpm = s.split(" ; ", 2);
        port = arrOfRpm[1];
        return Integer.parseInt(port);
    }


    //////////////////////new
    public static void callToSetVal(int val) { //to set value
        PortflagWrapper portflagWrapper = new PortflagWrapper(/*-1*/);
        portflagWrapper.setPort(val);
    }

    /////////////////////new close
    //for uart communication....end...


}
