package com.mikedg.android.tiltcontrolcontroller;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import com.google.glass.companion.GlassProtocol;
import com.google.glass.companion.Proto;
import com.mikedg.android.tiltcontrolcontroller.threads.NotifyingThread;
import com.mikedg.android.tiltcontrolcontroller.threads.ThreadCompleteListener;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import hugo.weaving.DebugLog;

/**
 * Created by Michael on 6/23/2014.
 */
public class GlassController {
    public static final UUID SECURE_UUID = UUID.fromString("F15CC914-E4BC-45CE-9930-CB7695385850");


    //Move subs here
    private OutputStream outputStream;
    private BluetoothSocket socket;
    public BluetoothDevice device;

    private static final String[] authorizedNames = new String[]{"mike digiovanni's glass", "andy lin's glass", "geoff cubitt's glass"};


    @DebugLog
    public GlassController(ThreadCompleteListener threadCompleteListener) {

        NotifyingThread thread1 = new NotifyingThread() {

            @Override
            public void doRun() {

                final BluetoothAdapter mBtAdapter;

                mBtAdapter = BluetoothAdapter.getDefaultAdapter();

                Set<BluetoothDevice> devices = mBtAdapter.getBondedDevices();
                for (BluetoothDevice foundDevice : devices) {
                    Log.d("BTD", "type:" + foundDevice.getType() + " " + foundDevice.getName() + "remove dev is not null for self address");

                    // TODO: use another device and check name
                    String deviceName = foundDevice.getName().toLowerCase();

                    //if (Arrays.asList(authorizedNames).contains((device.getName()).toLowerCase())) {

                    // check if deviceName ends with "glass" or ends with glass a space and 4 digits
                    if (deviceName.endsWith("glass") || deviceName.matches(".*glass \\d{4}$")) {

                        device = foundDevice;

                        Log.d("DEVICE", "device is " + device.getName());

                        try {
                            socket = device.createRfcommSocketToServiceRecord(SECURE_UUID);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        try {
                            socket.connect();
                        } catch (IOException connectException) {
                            // Unable to connect; close the socket and get out
                            try {
                                socket.close();
                            } catch (IOException closeException) {
                                closeException.printStackTrace();
                                return;
                            }
                        }

                        try {
                            outputStream = socket.getOutputStream();

        //                    Thread t1 = new Thread() {
        //                        public void run() {
        //                            try {
        //                                writeMessages(socket);
        //                            } catch (IOException e) {
        //                                e.printStackTrace();
        //                            }
        //
        //                        }
        //
        //                    };
        //                    t1.start();

                            Thread t2 = new Thread() {
                                public void run() {
                                    try {
                                        readMessages(socket);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                }
                            };
                            t2.start();
                            break;
                        } catch (IOException e) {

                            e.printStackTrace();
                        }

                    }
                }
            }
        };
        thread1.addListener(threadCompleteListener);
        thread1.start();





//See for better connection detalis https://github.com/wearscript/wearscript-android/blob/master/WearScript/src/main/java/com/dappervision/wearscript/glassbt/GlassDevice.java
//        https://github.com/wearscript/wearscript-android/blob/master/WearScript/src/main/java/com/dappervision/wearscript/glassbt/GlassMessagingUtil.java

    }

    public void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readMessages(BluetoothSocket socket) throws IOException {
//        Read incoming message as following. (You might want to do it in the dedicated thread.)

        InputStream inputStream = socket.getInputStream();
        while (inputStream.available() > -1) {
            Proto.Envelope envelope = (Proto.Envelope) GlassProtocol.readMessage(new Proto.Envelope(), inputStream);
            if (envelope.screenshot != null) {
                // screenshot response includes screenshot field in envelope
                // …do something…
            }
            Log.d("BTD", envelope.toString());
            //        envelope.motionC2G = GlassMessagingUtil.getSwipeRightEvents() ;
        }
    }


    private void writeMessage(List<Proto.Envelope> events) throws IOException {
        // Envelope is the root of the message hierarchy.
//        Proto.Envelope envelope = CompanionMessagingUtil.newEnvelope();
//// This example is for obtaining screenshot.
//        Proto.ScreenShot screenShot = new Proto.ScreenShot();
//        screenShot.startScreenshotRequestC2G = true;
//        envelope.screenshot = screenShot;
//        GlassProtocol.writeMessage(envelope, outputStream);
//        List<Proto.Envelope> evemts = GlassMessagingUtil.getTapEvents();
//        List<Proto.Envelope> evemts = GlassMessagingUtil.getSwipeLeftEvents();

        if (outputStream != null) {
            for (Proto.Envelope event : events) {

                GlassProtocol.writeMessage(event, outputStream);
            }
//        GlassProtocol.writeMessage(GlassMessagingUtil.createTimelineMessage("hell"), outputStream); //works
            outputStream.flush();
        } else {
            throw new IOException("outputStream is null");
        }
    }

    @Subscribe
    public void leftCommand(ControlEvents.LeftEvent event) {
        Log.d("BTD", "left");
        List<Proto.Envelope> evemts = GlassMessagingUtil.getSwipeLeftEvents();
        try {
            writeMessage(evemts);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void rightCommand(ControlEvents.RightEvent event) {
        Log.d("BTD", "right");
        List<Proto.Envelope> evemts = GlassMessagingUtil.getSwipeRightEvents();
        try {
            writeMessage(evemts);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void downCommand(ControlEvents.DownEvent event) {
        Log.d("BTD", "down");
        List<Proto.Envelope> evemts = GlassMessagingUtil.getSwipeDownEvents();
        try {
            writeMessage(evemts);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void tapCommand(ControlEvents.TapEvent event) {
        Log.d("BTD", "tap");
        List<Proto.Envelope> evemts = GlassMessagingUtil.getTapEvents();
        try {
            writeMessage(evemts);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
