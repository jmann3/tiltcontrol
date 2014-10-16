package com.mikedg.android.tiltcontrolcontroller.events;

/**
 * Created by jmann on 10/16/14.
 */
public class ConnectionState {

    private int mState;

    public ConnectionState(int state) {
        mState = state;
    }

    public int getState() {
        return mState;
    }
}
