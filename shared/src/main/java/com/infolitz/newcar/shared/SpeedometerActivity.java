package com.infolitz.newcar.shared;

import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

public class SpeedometerActivity extends AppCompatActivity {

    //... for uart
/*
    private Handler mInputHandler;
    private HandlerThread mInputThread;*/
    CommunicateWithC communicateWithC = new CommunicateWithC();

    //....close uart
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speedometer);

        RecyclerView recyclerView = findViewById(R.id.speedometer_recyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);


        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        List<Integer> speeds = new ArrayList<>();
        for (int i = 0; i < 1; i++)
//            //speeds.add(new Random().nextInt(99)+1);
            speeds.add(50);
        recyclerView.setAdapter(new RVAdapter(speeds));

        //... for uart......

        TextView textView=findViewById(R.id.text_c);
//        communicateWithC= new CommunicateWithC();
        String abcd= communicateWithC.Method();
        Log.e("jnekj",abcd);
//        textView.setText(communicateWithC.Method());


/*
        Log.e("in ","oncreate");
        // Create a background looper thread for I/O
        mInputThread = new HandlerThread("InputThread");
        mInputThread.start();
        mInputHandler = new Handler(mInputThread.getLooper());
        try {
            PeripheralManager pm = PeripheralManager.getInstance();
            List<String> li= pm.getUartDeviceList();
            Log.e("hsi",li.get(0));

            UartDevice uart = PeripheralManager.getInstance().openUartDevice("UART0"); // /dev/ttyS0
            uart.setBaudrate(115200);
            uart.setDataSize(8);
            uart.setParity(UartDevice.PARITY_NONE);
            uart.setStopBits(1);
            uart.registerUartDeviceCallback(mInputHandler, mCallback);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        //...for uart end

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
/*
    private UartDeviceCallback mCallback = new UartDeviceCallback() {
        @Override
        public boolean onUartDeviceDataAvailable(UartDevice uart) {
            // Read the received data
            byte[] buffer = new byte[512];
            Log.e("in ","mCallback");
            int read;
            while (true) {
                Log.e("in ","true");
                try {
                    if (!((read = uart.read(buffer, buffer.length)) > 0)) {
                        // And send it back to the other device, as a loopback.
                        Log.e("haii string is:", String.valueOf(read));
                        uart.write(buffer, read);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            // Continue listening for more interrupts.
        }

        @Override
        public void onUartDeviceError(UartDevice uart, int error) {
            Log.w("TAG", uart + ": Error event " + error);
        }
    };*/

   /* CharSequence Method(){
        System.loadLibrary("cpp_code");
        return ;
    }*/
    //.... for uart end
}

