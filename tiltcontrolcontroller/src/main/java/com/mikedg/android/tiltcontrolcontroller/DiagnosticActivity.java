package com.mikedg.android.tiltcontrolcontroller;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.mikedg.android.btcomm.connector.BluetoothConnector;
import com.mikedg.android.btcomm.connector.ConnectorHelper;
import com.mikedg.android.btcomm.messages.CommandMessage;
import com.mikedg.android.btcomm.messages.PTGCMessage;
import com.mikedg.android.tiltcontrolcontroller.events.StatusMessageEvent;
import com.squareup.otto.Subscribe;

import org.json.JSONException;

public class DiagnosticActivity extends DisplayActivity implements DrawerFragment.OnFragmentInteractionListener {

    private TextView mLogTextView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private final static String TAG = DiagnosticActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLogTextView = (TextView) findViewById(R.id.textView);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getActionBar().setTitle("Tilt Control Controller");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle("Drawer");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_diagnostic;
    }

    @Override
    protected String getActionBarTitle() {
        return "Diagnostic Panel";
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mIsWaitingForForceStop == true) {
            mIsWaitingForForceStop = tryConnecting(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }



//    private void tryConnecting() {
//        // check if myglass is running
//        if (AppUtil.isMyGlassRunning(this)) {
//
//            // show the Application Manager screen for the MyGlass app so the user can kill it
//            AppUtil.startMyGlassAppInfo(this);
//
//            // display toast to tell user what to do
//            Toast.makeText(this, "Press \"Force Stop\"", Toast.LENGTH_LONG).show();
//
//            mIsWaitingForForceStop = true;
//
//            ControllerService.startService(this);
//        } else {
//            Log.i(TAG, "MyGlass app is still running");
//
//            ControllerService.startService(this);
//        }
//    }


    public void onClick_start(View view) {

        mIsWaitingForForceStop = tryConnecting(this);
        Log.d("IsWaitingForForceStop", "isWaitingForForceStop is " + mIsWaitingForForceStop);
    }

    public void onClick_left(View view) {
        Application.getBus().post(new ControlEvents.LeftEvent());
    }
    public void onClick_right(View view) {
        Application.getBus().post(new ControlEvents.RightEvent());
    }
    public void onClick_tap(View view) {
        Application.getBus().post(new ControlEvents.TapEvent());
    }
    public void onClick_down(View view) {
        Application.getBus().post(new ControlEvents.DownEvent());
    }

    public void onClick_loopTap(View view) {
        prepLoop(CommandMessage.Command.TAP.ordinal());

    }

    public void onClick_loopDown(View view) {
        prepLoop(CommandMessage.Command.DOWN.ordinal());

    }

    public void onClick_loopLeft(View view) {
        prepLoop(CommandMessage.Command.LEFT.ordinal());

    }

    public void onClick_loopRight(View view) {
        prepLoop(CommandMessage.Command.RIGHT.ordinal());
    }

    private void prepLoop(int command) {
        //make a new message;
        PTGCMessage message = new CommandMessage(command);

        //get the bytes;
        try {
            byte[] buffer = message.getTypedBytes();

            //recreate from bytes
            message = new ConnectorHelper().getPtgcFromBytes(buffer);

            //publish
            Application.getBus().post(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void onClick_appInfo(View view) {
        AppUtil.startMyGlassAppInfo(this);
    }


    /*
     *  Add status messages to event log
     */
    @Subscribe
    public void gotStatusMessage(StatusMessageEvent event) {
        addToLog(event.getMessage());
    }

    private void addToLog(String message) {
        mLogTextView.setText(message + '\n' + mLogTextView.getText());
    }

    @Subscribe
    public void gotConnectorEvent(BluetoothConnector.ConnectorEvent event) {
        addToLog("BT State: " + event.getState());
    }

    @Override
    public void onFragmentInteraction(String id) {

    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
//        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
//        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }



    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }
}
