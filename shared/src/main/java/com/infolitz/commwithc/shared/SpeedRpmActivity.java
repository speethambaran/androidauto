package com.infolitz.commwithc.shared;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.anastr.speedviewlib.Speedometer;
import com.github.anastr.speedviewlib.components.indicators.Indicator;
import com.infolitz.mycarspeed.shared.CommunicateWithC;

import java.util.Locale;

import kotlin.jvm.functions.Function2;

public class SpeedRpmActivity extends AppCompatActivity {

    Speedometer speedometer,rpmMeter;
    //... for uart
    CommunicateWithC communicateWithC = new CommunicateWithC();//for kotlin communication with cpp_code
    boolean stop = false;
    //uart communication close

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed_rpm);

        speedometer = findViewById(R.id.speedometer);
        rpmMeter = findViewById(R.id.imageSpeedometer);
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

    //for uart communication....
    public class MyThreadToCallC extends Thread {

        TextView textView = findViewById(R.id.text_c);

        @Override
        public void run() {
            while (!stop) {
                try {
//                    Log.e("from java testingg",""+communicateWithC.useWiringLib());
                    String s=communicateWithC.useWiringLib();
                    textView.setText("" + s);
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // poll the USB and dispatch changes to the views with a Handler
            }
        }
    }
    //for uart communication....end...
}
