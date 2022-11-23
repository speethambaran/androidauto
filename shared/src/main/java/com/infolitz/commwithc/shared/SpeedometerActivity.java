package com.infolitz.commwithc.shared;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.anastr.speedviewlib.Speedometer;
import com.github.anastr.speedviewlib.components.indicators.ImageIndicator;
import com.infolitz.mycarspeed.shared.CommunicateWithC;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class SpeedometerActivity extends AppCompatActivity {

    //... for uart
/*
    private Handler mInputHandler;
    private HandlerThread mInputThread;*/
    CommunicateWithC communicateWithC = new CommunicateWithC();//for kotlin communication with cpp_code
    boolean stop = false;
    RecyclerView recyclerView ;
    GridLayoutManager gridLayoutManager;
    RVAdapter rvAdapter;
    List<Integer> speeds = new ArrayList<>();
    //....close uart
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speedometer);

        initAdapter();



        //... for uart......
        new Thread(new MyThreadToCallC()).start();//ReadUart();
        //...for uart end

    }

    private void initAdapter() {
        recyclerView = findViewById(R.id.speedometer_recyclerView);
        gridLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        rvAdapter =new RVAdapter(speeds);

//        for (int i = 0; i < 1; i++)
//            //speeds.add(new Random().nextInt(99)+1);
            speeds.add(50);
//        rvAdapter =new RVAdapter(speeds);
        recyclerView.setAdapter(rvAdapter);
    }

    private void initSpeedometer(String s) {
        Log.e("string is in initSpeedometer ",s);
//        speeds.add(90);
//        rvAdapter.notifyDataSetChanged();

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


    public class RVAdapter extends RecyclerView.Adapter<RVAdapter.MyViewHolder> {

        List<Integer> speeds;

        RVAdapter(List<Integer> speeds) {
            this.speeds = speeds;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view_speedometer, viewGroup, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            // set speed at 0 without animation (to start from this position).
            holder.speedometer.setSpeedAt(0);
            ImageIndicator imageIndicator = new ImageIndicator(getApplicationContext(), ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.speedometer_image_indicator));
            holder.speedometer.setIndicator(imageIndicator);
            holder.speedometer.speedTo(speeds.get(position));
        }

        @Override
        public int getItemCount() {
            return speeds.size();
        }


        class MyViewHolder extends RecyclerView.ViewHolder {

            Speedometer speedometer;

            MyViewHolder(View itemView) {
                super(itemView);
                speedometer = itemView.findViewById(R.id.speedometer);
                speedometer.setWithTremble(false); // deactivated the trembling or the indicator flickering
            }

        }

    }


    //for uart

   /* CharSequence Method(){
        System.loadLibrary("cpp_code");
        return ;
    }*/
    /*static { //for java communication with cpp_code
        System.loadLibrary("cpp_code");
        System.loadLibrary("test_code");
   }
   public native String ForTestingg();*///for java communication with cpp_code
    public class MyThreadToCallC extends Thread {

        TextView textView = findViewById(R.id.text_c);

        @Override
        public void run() {
            while (!stop) {
                try {
//                    Log.e("from java testingg",""+communicateWithC.useWiringLib());
                    String s=communicateWithC.useWiringLib();
                    textView.setText("" + s);
                    initSpeedometer(s);
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // poll the USB and dispatch changes to the views with a Handler
            }
        }
    }

}

