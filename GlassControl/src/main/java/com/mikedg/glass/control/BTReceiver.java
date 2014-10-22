package com.mikedg.glass.control;

import com.mikedg.android.btcomm.Configuration;
import com.mikedg.android.btcomm.messages.PTGCMessage;
import com.mikedg.android.btcomm.messages.SimWinkMessage;
import com.mikedg.android.btcomm.messages.StopMessage;
import com.squareup.otto.Subscribe;

/**
 * Created by Michael on 8/5/2014.
 */
public class BTReceiver {
    public static class WinkEvent {};

    public static class StopEvent {};
    //Messages we get over the PTGC channel

    @Subscribe
    public void gotCommand(PTGCMessage event) {

    }

    @Subscribe
    public void gotSimWink(SimWinkMessage event) {
        Configuration.bus.post(new WinkEvent());
    }

    @Subscribe
    public void gotStop(StopMessage event) {
        Configuration.bus.post(new StopEvent()); }
}
