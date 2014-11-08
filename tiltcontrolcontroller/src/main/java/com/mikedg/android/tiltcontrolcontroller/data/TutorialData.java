package com.mikedg.android.tiltcontrolcontroller.data;

import com.mikedg.android.tiltcontrolcontroller.R;

/**
 * Created by jmann on 10/14/14.
 */
public class TutorialData {

    private static final String[] titles = new String[] {"Connect To Glass", "Create connection with Google Glass", "Return to Tilt Control"};

    private static final String[] descriptions = new String[] {"Tap on the \"Configure MyGlass\" button to open new app.",
                                                               "Tap on the \"MyGlass\" text in the actionbar and select \n" +
                                                                 "\"Devices\".  Ensure that your device is displayed",
                                                               "Hit the back key to return to the Tilt Control application" };

    private static final int[] images = new int[] {R.drawable.ic_launcher, R.drawable.ic_launcher, R.drawable.ic_launcher};

    // Getters for instance variables
    public static String[] getTitles() {
        return titles;
    }

    public static String[] getDescriptions() {
        return descriptions;
    }

    public static int[] getImages() {
        return images;
    }
}
