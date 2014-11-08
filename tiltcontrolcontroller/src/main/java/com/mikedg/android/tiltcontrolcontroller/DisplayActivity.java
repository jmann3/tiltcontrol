package com.mikedg.android.tiltcontrolcontroller;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.mikedg.android.tiltcontrolcontroller.events.SimWinkEvent;
import com.mikedg.android.tiltcontrolcontroller.utility.AppUtil;

/**
 * Created by jmann on 10/12/14.
 */
public abstract class DisplayActivity extends FragmentActivity {

    protected static boolean mIsWaitingForForceStop = false;
    protected static boolean mShouldStartMyGlass = false;
    public static int ENABLE_BLUETOOTH = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());

        setTitle(getActionBarTitle());

    }

    @Override
    protected void onPause() {
        super.onPause();

        com.mikedg.android.btcomm.Configuration.bus.unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        com.mikedg.android.btcomm.Configuration.bus.register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // stop the service if it is still running
        onClick_stop(null);
    }

    protected abstract int getLayoutResourceId();

    protected abstract String getActionBarTitle();


    public boolean tryConnecting(Context context) {
        // check if myglass is running
        if (AppUtil.isMyGlassRunning(this)) {

            // show the Application Manager screen for the MyGlass app so the user can kill it
            AppUtil.startMyGlassAppInfo(this);

            // display toast to tell user what to do
            Toast.makeText(this, "Press \"Force Stop\"", Toast.LENGTH_LONG).show();

            return true;
        } else {
            Log.i(this.getClass().getSimpleName(), "MyGlass app is not running");

            if (context instanceof MainActivity)
                AppUtil.startActivityIndicator(context, "Connecting to Glass");

            ControllerService.startService(this);

            return false;
        }
    }

    public abstract void onClick_start(View view);

    public void onClick_simWink(View view) {

        Log.d("Wink Simulated", "Wink Simulated");
        Application.getBus().post(new SimWinkEvent());
    }

    public void onClick_stop(View view) {

        final Context tContext = this;

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {

                // stop the service
                ControllerService.stopService(tContext);

                return null;
            }
        }.execute();
    }

    protected boolean isBluetoothAvailableAndEnabled() {

        // check if wifi is connected
        BluetoothAdapter blueToothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (blueToothAdapter == null) {
            // device does not support bluetooth
            AppUtil.showGlobalAlertDialog(this, "Warning", "Please use a device which supports bluetooth", null);
            return false;
        } else {
            if (!blueToothAdapter.isEnabled()) {
                // bluetooth is not enabled

                // create intent to start bluetooth
                Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBTIntent, ENABLE_BLUETOOTH);

                return false;
            }
        }

        return true;
    }
}
