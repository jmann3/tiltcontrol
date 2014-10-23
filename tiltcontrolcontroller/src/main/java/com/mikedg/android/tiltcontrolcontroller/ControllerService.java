package com.mikedg.android.tiltcontrolcontroller;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.mikedg.android.btcomm.Configuration;
import com.mikedg.android.btcomm.connector.BluetoothClientConnector;
import com.mikedg.android.btcomm.connector.BluetoothConnector;
import com.mikedg.android.btcomm.messages.SimWinkMessage;
import com.mikedg.android.btcomm.messages.StopMessage;
import com.mikedg.android.tiltcontrolcontroller.events.SimWinkEvent;
import com.mikedg.android.tiltcontrolcontroller.events.StatusMessageEvent;
import com.mikedg.android.tiltcontrolcontroller.threads.ThreadCompleteListener;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

/**
 * Service that create imposter MyGlass connection to Glass device.
 * Also creates Bluetooth connection to Glass device, to receive commands.
 */

public class ControllerService extends Service implements ThreadCompleteListener {
    private static final int ONGOING_NOTIFICATION_ID = 21345;
    private GlassController mGlassController;
    private CommandReceiver mCommandReceiver;
    private BluetoothClientConnector mBluetoothConnector;
    private String mMessages = "";
    private static int mLastState = 0;

    public static final void startService(Context context) {

        Intent i = new Intent(context, ControllerService.class);
        context.startService(i);
    }

    public static final void stopService(Context context) {
        Intent i = new Intent(context, ControllerService.class);
        context.stopService(i);
    }


    public ControllerService() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // signal that Glass should stop
        if (mBluetoothConnector != null) {
            mBluetoothConnector.write(new StopMessage());

            mBluetoothConnector.stop();
        }

        Application.getBus().unregister(this);

        try {
            Application.getBus().unregister(mGlassController);
            Application.getBus().unregister(mCommandReceiver);
        } catch (Exception e) {
            Log.i(ControllerService.class.getSimpleName(), "trying to unregister class that was not registered");
        }

        mGlassController.disconnect();

        Configuration.bus.post(new StatusMessageEvent("Stopped service."));
        Log.d("Service stopped", "ControllerService stopped");

        mLastState = 0;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onCreate() {
        super.onCreate();

        Application.getBus().register(this);

        makeForeground();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mGlassController = new GlassController(this);

        return START_STICKY;
    }

    @Override
    public void notifyOfThreadComplete(Thread thread) {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {

                if (mGlassController.device != null) {

                    Application.getBus().register(mGlassController);

                    mCommandReceiver = new CommandReceiver();
                    Application.getBus().register(mCommandReceiver);

                    mBluetoothConnector = new BluetoothClientConnector();
                    mBluetoothConnector.connect(mGlassController.device);

                    Configuration.bus.post(new StatusMessageEvent("Started service."));
                } else {
                    Configuration.bus.post(new StatusMessageEvent("No device found"));
                }
            }
        });
    }

    private void makeForeground() {
        NotificationCompat.Builder builder = createNotification();
        startForeground(ONGOING_NOTIFICATION_ID, builder.build());
    }

    private NotificationCompat.Builder createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        createBigNotification(builder);
        createNormalNotification(builder);
        return builder;
    }

    public NotificationCompat.Builder createNormalNotification(NotificationCompat.Builder builder) {
        builder.setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Tilt Control Controller")
                .setContentText("Status"); //FIXME: most recent status
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, DiagnosticActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(DiagnosticActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);
        return builder;

    }

    public NotificationCompat.Builder createBigNotification(NotificationCompat.Builder builder) {
        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();
// Sets a title for the Inbox style big view
        inboxStyle.setBigContentTitle("Tilt Control Controller");

// Moves events into the big view
        String[] events = mMessages.split("\n", 6);
        for (int i = 0; i < events.length; i++) {
            inboxStyle.addLine(i + " " + events[i]);
        }
// Moves the big view style object into the notification object.
        builder.setStyle(inboxStyle);
//        builder.addAction()

        return builder;
    }

    @Subscribe
    public void gotSimWink(SimWinkEvent event) {
        mBluetoothConnector.write(new SimWinkMessage());
    }

    @Subscribe
    public void gotStatusMessage(StatusMessageEvent event) {

        appendStatus(event.getMessage());

    }

    @Subscribe
    public void gotConnectorEvent(BluetoothConnector.ConnectorEvent event) {
        appendStatus("BT State: " + event.getState());
    }

    private void appendStatus(String message) {
        mMessages = message + '\n' + mMessages;

        NotificationCompat.Builder builder = createNotification();
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(ONGOING_NOTIFICATION_ID, builder.build());

    }

    @Subscribe public void stateUpdated(BluetoothConnector.ConnectorEvent connectorEvent) {
        mLastState = connectorEvent.getState();
    }

    @Produce
    public BluetoothConnector.ConnectorEvent GetLatestState() {
        // Assuming 'lastAnswer' exists.
        return new BluetoothConnector.ConnectorEvent(mLastState);
    }

}
