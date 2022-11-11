package com.infolitz.commwithc.shared;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import com.infolitz.commwithc.shared.SpeedometerActivity;

public class MainActivity extends AppCompatActivity {

    private Button btn,btn2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initId();
        initClick();

    }

    void initId(){
        btn=findViewById(R.id.btnn);
        btn2=findViewById(R.id.btn2);
    }
    void initClick(){
        btn.setOnClickListener(view -> {
            Intent myIntent = new Intent(MainActivity.this, BluetoothActivity.class);
            MainActivity.this.startActivity(myIntent);
        });

        btn2.setOnClickListener(view -> {
            Intent myIntent = new Intent(MainActivity.this, SpeedometerActivity.class);
            MainActivity.this.startActivity(myIntent);
        });
    }
}