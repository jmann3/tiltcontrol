package com.mikedg.android.tiltcontrolcontroller;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;

import com.viewpagerindicator.IconPageIndicator;
import com.viewpagerindicator.LinePageIndicator;
import com.viewpagerindicator.UnderlinePageIndicator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends DisplayActivity {

    private final static String TAG = MainActivity.class.getSimpleName();
    private Button mConfigureGlassButton;
    private Button mEnableGlassControlButton;
    private ViewPager mViewPager;
    private ArrayList<HashMap> mTutorialPages;


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
        mViewPager = (ViewPager)findViewById(R.id.main_view_pager);

        mViewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {

            @Override
            public Fragment getItem(int position) {
                int imageId = ((int[])TutorialData.getImages())[position];
                String title = ((String[])TutorialData.getTitles())[position];
                String description = ((String[])TutorialData.getDescriptions())[position];
                return TutorialScreenFragment.newInstance(imageId, title, description);
            }

            @Override
            public int getCount() {
                return TutorialData.getTitles().length;
            }
        });

        // set up the pager indicator
        UnderlinePageIndicator pagerIndicator = (UnderlinePageIndicator)findViewById(R.id.pager_indicator);

        pagerIndicator.setViewPager(mViewPager);
        pagerIndicator.setSelectedColor(getResources().getColor(R.color.indicator_foreground));
        pagerIndicator.setBackgroundColor(getResources().getColor(R.color.indicator_background));
        pagerIndicator.setFades(false);


    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected String getActionBarTitle() {
        return "Tilt Controller";
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mIsWaitingForForceStop == true) {
            mIsWaitingForForceStop = tryConnecting();

            if (mIsWaitingForForceStop == false)
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
            mIsWaitingForForceStop = tryConnecting();

            if (mIsWaitingForForceStop == false)
                mEnableGlassControlButton.setText("Disable Tilt Control");

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
