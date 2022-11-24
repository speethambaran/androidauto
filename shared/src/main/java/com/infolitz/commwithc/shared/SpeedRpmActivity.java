package com.infolitz.commwithc.shared;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.anastr.speedviewlib.Speedometer;
import com.github.anastr.speedviewlib.components.indicators.Indicator;
import com.infolitz.mycarspeed.shared.CommunicateWithC;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

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
        speedometer.speedPercentTo(53);
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
        rpmMeter.speedPercentTo(40);


        //... for uart......
        callStringg();
        //...for uart end


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
        final Runnable r = new Runnable() {
            String s;

            public void run() {
//                textView_c.setText("0" + textView_c.getText().toString());
                s = communicateWithC.useWiringLib();
                Log.e("from java testingg", "" + s);
//                textView_c.setText("" + s);
                handler.postDelayed(this, 5000);
            }
        };

        handler.postDelayed(r, 5000);
    }


    //////////////////////new

    /////////////////////new close
    //for uart communication....end...


}
