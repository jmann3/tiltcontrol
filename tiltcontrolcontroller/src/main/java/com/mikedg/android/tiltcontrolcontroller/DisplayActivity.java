package com.mikedg.android.tiltcontrolcontroller;

import android.content.Context;
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
    protected static boolean mShouldStartMyGlass = true;
    protected static final String PACKAGE_MY_GLASS = "com.google.glass.companion";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());

        com.mikedg.android.btcomm.Configuration.bus.register(this);
    }

    protected abstract int getLayoutResourceId();


    public boolean tryConnecting(Context context) {
        // check if myglass is running
        if (AppUtil.isMyGlassRunning(context)) {

            // show the Application Manager screen for the MyGlass app so the user can kill it
            AppUtil.startMyGlassAppInfo(context);

            // display toast to tell user what to do
            Toast.makeText(context, "Press \"Force Stop\"", Toast.LENGTH_LONG).show();

            //ControllerService.startService(context);

            return true;
        } else {
            Log.i(context.getClass().getSimpleName(), "MyGlass app is not running");

            ControllerService.startService(context);

            return false;
        }
    }

    public abstract void onClick_start(View view);

    public void onClick_simWink(View view) {

        Log.d("Wink Simulated", "Wink Simulated");
        Application.getBus().post(new SimWinkEvent());
    }

    public void onClick_stop(View view) {
        ControllerService.stopService(this);
    }
}
