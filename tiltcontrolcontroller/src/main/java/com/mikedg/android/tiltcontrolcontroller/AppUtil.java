package com.mikedg.android.tiltcontrolcontroller;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;

import java.util.List;

/**
 * Created by Michael on 7/28/2014.
 */
public class AppUtil {
    // utility method used to start sub activity
    private static final String SCHEME = "package";
    private static final String PACKAGE_MY_GLASS = "com.google.glass.companion";

    private static ProgressDialog mProgressDialog = null;
    private static boolean mProgressDisplayed = false;

    public static void startMyGlassAppInfo(Context context) {
        // Create intent to start new activity
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts(SCHEME, PACKAGE_MY_GLASS, null);
        intent.setData(uri);
        context.startActivity(intent);
    }

    public static final boolean isMyGlassRunning(Context context) {
        ActivityManager mgr = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processList = mgr.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo process: processList) {
            for (String pkg: process.pkgList) {
                if (PACKAGE_MY_GLASS.equals(pkg)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void startMyGlassApp(Context context) {
        // open "MyGlass" app
        try {
            Intent i;
            PackageManager manager = context.getPackageManager();
            try {
                i = manager.getLaunchIntentForPackage(PACKAGE_MY_GLASS);

                if (i == null)
                    throw new PackageManager.NameNotFoundException();

                i.addCategory(Intent.CATEGORY_LAUNCHER);
                context.startActivity(i);

            } catch (PackageManager.NameNotFoundException e) {

            }

        } catch (ActivityNotFoundException e ) {

        }
    }

    public static void startActivityIndicator(final Context context, final String message) {

        if (context != null && !mProgressDisplayed) {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setMessage(message);
            mProgressDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            mProgressDialog.setCanceledOnTouchOutside(true);
//            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//                @Override
//                public void onCancel(DialogInterface dialogInterface) {
//                    stopActivityIndicator();
//                    if (context instanceof Activity) {
//                        ((Activity) context).onBackPressed();
//                    }
//                }
//            });

            mProgressDialog.show();
            mProgressDisplayed = true;
        }
    }

    public static void stopActivityIndicator() {
        if (mProgressDisplayed) {
            mProgressDisplayed = false;
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        }
    }

    public static AlertDialog showGlobalAlertDialog(Context context, final String title, final String message, DialogInterface.OnClickListener onClick) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context, android.R.style.Theme_Holo_Light_Dialog);
        AlertDialog dialog = alertBuilder
                .setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .setNeutralButton("Okay", onClick)
                .create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        return dialog;
    }

}
