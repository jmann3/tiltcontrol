package com.mikedg.android.btcomm.messages;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jmann on 10/22/14.
 */
public class StopMessage extends PTGCMessage {

    public static final String VAL_TYPE = "tiltController_disabled";
    public StopMessage() {

    }
    public StopMessage(JSONObject object) throws JSONException {
        this();
    }

    public String getType() {
        return VAL_TYPE;
    }

    @Override
    public JSONObject getPayload() {
        JSONObject object = new JSONObject();
        //No payload
        return object;
    }
}
