package com.mikedg.android.tiltcontrolcontroller;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;

import java.lang.reflect.Field;

public class MainActivity extends DisplayActivity {

    private final static String TAG = MainActivity.class.getSimpleName();
    private Button mConfigureGlassButton;
    private Button mEnableGlassControlButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));

        // disable options button so can see overflow icon
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class
                    .getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mConfigureGlassButton = (Button)findViewById(R.id.button_MyGlass);
        mEnableGlassControlButton = (Button)findViewById(R.id.button_EnableTilt);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onResume() {
        super.onResume();

        mConfigureGlassButton.setEnabled(mShouldStartMyGlass);
        mEnableGlassControlButton.setEnabled(!mShouldStartMyGlass);

        if (mShouldStartMyGlass) {

            // blur background

            // after short delay show AlertDialog asking if want to connect to Glass

        }

        if (mIsWaitingForForceStop == true) {
            mIsWaitingForForceStop = tryConnecting(this);
            mEnableGlassControlButton.setText("Disable Tilt Control");

        } else {
            mEnableGlassControlButton.setText("Enable Tilt Control");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        com.mikedg.android.btcomm.Configuration.bus.unregister(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.wink_item:

                // post wink to bus
                onClick_simWink(null);

                return true;

            case R.id.diagnotic_item:

                // show diagnostic screen
                Intent intent = new Intent(this, DiagnosticActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onClick_start(View view) {

        if (((Button)view).getText().equals("Enable Tilt Control")) {

            // button is "Start" button
            mIsWaitingForForceStop = tryConnecting(this);

        } else {

            // button is "Stop" button
            onClick_stop(null);
            mEnableGlassControlButton.setText("Enable Tilt Control");
        }
    }

    public void onClick_startMyGlass(View view) {

        // start MyGlass application
        AppUtil.startMyGlassApp(this);

        // toggle state to enable correct button
        mShouldStartMyGlass = false;
    }

}
