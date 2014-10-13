package com.mikedg.android.tiltcontrolcontroller;

import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;

import java.util.List;

/**
 * Created by Michael on 7/28/2014.
 */
public class AppUtil {
    // utility method used to start sub activity
    private static final String SCHEME = "package";
    private static final String PACKAGE_MY_GLASS = "com.google.glass.companion";

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

}
