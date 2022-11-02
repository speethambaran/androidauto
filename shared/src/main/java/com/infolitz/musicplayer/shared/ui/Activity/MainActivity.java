package com.infolitz.musicplayer.shared.ui.Activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.infolitz.musicplayer.shared.R;
import com.infolitz.musicplayer.shared.databinding.ActivityMainBinding;
import com.infolitz.musicplayer.shared.ui.Fragment.BluetoothFragment;
import com.infolitz.musicplayer.shared.ui.Fragment.List_musicFragment;


public class MainActivity extends BaseActivity {
    ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (savedInstanceState == null) {

            boolean isConnected = checkConnection(this);

            if (!isConnected) {
                showNoConnectionSnackBar("There is no Internet");
                return;
            }
            init();
        }
    }

    private void init() {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        List_musicFragment listMusicFragment = new List_musicFragment();
        fragmentTransaction.add(binding.Container.getId(), listMusicFragment);
        fragmentTransaction.commit();


        binding.ivBluetooth.setOnClickListener(view -> {
            BluetoothFragment bluetoothFragment = new BluetoothFragment();
            loadPasswordFragment(bluetoothFragment, this);
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        int count = getSupportFragmentManager().getBackStackEntryCount();
        Log.d("COUNT_FRAG", "" + count);
//        if (count == 0) {
//            finish();
//        }
    }

    private void loadPasswordFragment(Fragment newFragment, Context context) {
        FragmentTransaction transaction = ((MainActivity) context).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.Container, newFragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_NONE);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}