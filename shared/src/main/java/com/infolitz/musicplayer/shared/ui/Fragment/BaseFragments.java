package com.infolitz.musicplayer.shared.ui.Fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.google.android.material.snackbar.Snackbar;
import com.infolitz.musicplayer.shared.R;
import com.infolitz.musicplayer.shared.cloud.BaseListener;

public class BaseFragments extends Fragment implements BaseListener {

    final String TAG = getClass().getSimpleName();
    ProgressDialog progressDialog;
    public View view;
    private AlertDialog.Builder builder;
    public long mLastClickTime = 0;


    @Override
    public void onCompleted() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    @Override
    public void onConnectionFailure(int errorCode) {

    }

    @Override
    public void onStarted() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMax(100);
            progressDialog.setMessage("Please wait for a Moment");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        progressDialog.show();
    }

    public boolean doubleClickPrevent() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return false;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        return true;
    }
//    @Override
//    public void onConnectionFailure(int errorCode) {
//        showMessageAlert(getString(R.string.alert), "No Connection", false);
//    }


    public void showSnackBar(String msg) {
        Snackbar snack = Snackbar.make(getActivity().findViewById(android.R.id.content), msg, Snackbar.LENGTH_SHORT);
        snack.show();
    }


    public void showMessageAlert(final String title, final String msg, final boolean flag) {
        assert getActivity() != null;
        builder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme1);
        builder.setCancelable(false);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (flag && getActivity() != null) {
                    getActivity().finish();
                }
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.show();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }


    public void showMessage(final String message) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
                //toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
            }
        });
    }

    public void hideKeyboard(View view) {
        assert getActivity() != null;
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


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

}
