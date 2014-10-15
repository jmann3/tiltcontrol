package com.mikedg.android.tiltcontrolcontroller;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.mikedg.android.tiltcontrolcontroller.events.SimWinkEvent;

/**
 * Created by jmann on 10/12/14.
 */
public abstract class DisplayActivity extends FragmentActivity {

    protected static boolean mIsWaitingForForceStop = false;
    protected static boolean mShouldStartMyGlass = false;
    protected static final String PACKAGE_MY_GLASS = "com.google.glass.companion";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());

        setTitle(getActionBarTitle());

        com.mikedg.android.btcomm.Configuration.bus.register(this);
    }

    protected abstract int getLayoutResourceId();

    protected abstract String getActionBarTitle();


    public boolean tryConnecting() {
        // check if myglass is running
        if (AppUtil.isMyGlassRunning(this)) {

            // show the Application Manager screen for the MyGlass app so the user can kill it
            AppUtil.startMyGlassAppInfo(this);

            // display toast to tell user what to do
            Toast.makeText(this, "Press \"Force Stop\"", Toast.LENGTH_LONG).show();

            return true;
        } else {
            Log.i(this.getClass().getSimpleName(), "MyGlass app is not running");

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
}
