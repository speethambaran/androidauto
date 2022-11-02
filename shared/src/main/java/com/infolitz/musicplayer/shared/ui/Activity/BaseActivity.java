package com.infolitz.musicplayer.shared.ui.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.SystemClock;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.material.snackbar.Snackbar;

import java.security.SecureRandom;
import java.util.ArrayList;

public class BaseActivity extends AppCompatActivity {

    public Toolbar toolbar;
    public RelativeLayout tvNotFoundAct;
    ProgressDialog progressDialog;
    public final String TAG = getClass().getSimpleName();
    public long mLastClickTime = 0;

    public float BUTTON_ALPHA_VALUE_ENABLE = 1f;
    public float BUTTON_ALPHA_VALUE_DISABLE = 0.6f;

    public static BluetoothGattCharacteristic writeCMDCharacteristic;
    public static BluetoothGattCharacteristic readBroadcastCharacteristic;
    public BluetoothManager mBluetoothManager;
    public BluetoothGattCallback mGattCallback;
    public static BluetoothAdapter mBluetoothAdapter;
    public static BluetoothGatt mBluetoothGatt;


    public static BluetoothGattCharacteristic dataCharacteristic;
    public static ArrayList<String> dataList = new ArrayList<>();
    //    OnResponseRecived onResponseRecived;
    public static int FOTA_ACKStatus;

    public boolean doubleClickPrevent() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return false;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        return true;
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

//    public void initNotFoundWidgets() {
//        tvNotFoundAct = findViewById(R.id.tvNotFoundAct);
//        tvNotFoundAct.setVisibility(View.GONE);
//    }

//    public void toolbarSection(String title, Boolean backNav) {
//        toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        assert getSupportActionBar() != null;
//        getSupportActionBar().setTitle(title);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(backNav);
//        toolbar.inflateMenu(R.menu.menu_list);
//    }

    public static boolean checkConnection(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connMgr != null) {
            NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();

            if (activeNetworkInfo != null) { // connected to the internet
                // connected to the mobile provider's data plan
                if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    // connected to wifi
                    return true;
                } else return activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            }
        }
        return false;
    }

    public void noConnectionAlert() {
        /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.custom_noconnectionalert, null);
        builder.setCancelable(false);
        builder.setView(v);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 20);
        dialog.getWindow().setBackgroundDrawable(inset);
        dialog.show();

        Button btnRetry = v.findViewById(R.id.btnRetry);
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });*/
        showSnackBar("No internet connection");
    }

    public void showMessage(final String message) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
                //toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
            }
        });
    }


    public void showMessageAlert(final String title, final String msg, final boolean flag) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.RED);
        SpannableStringBuilder txtColor = new SpannableStringBuilder(title);
        txtColor.setSpan(colorSpan, 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setTitle(txtColor);
        builder.setMessage(msg);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (flag) {
                    finish();
                }
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.show();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

    }

    public void showSnackBar(String msg) {
        Snackbar snack = Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_SHORT);
        snack.show();
    }

    public void showNoConnectionSnackBar(String msg) {
        Snackbar snack = Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_INDEFINITE);
        snack.setAction("Retry", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
        });
        snack.show();
    }

    public boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                return true;

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 3);
                return false;
            }
        } else {
            return true;
        }
    }

    public String getRandomlyGeneratedOTP(int len) {

//        final String AB = "123456789ABCDEFGHIJKLMNPQRSTUVWXYZabcdefghijklmnpqrstuvwxyz";
        final String AB = "1234567890";
        SecureRandom rnd = new SecureRandom();

        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        }
        return "" + sb.toString();
    }


    public void onCompleted() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }


    public void onStarted() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMax(100);
            progressDialog.setMessage("wait for a moment");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        progressDialog.show();
    }


    public void onConnectionFailure(int errorCode) {
        noConnectionAlert();
    }



}
