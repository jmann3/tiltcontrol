package com.mikedg.android.tiltcontrolcontroller.data;

/**
 * Created by jmann on 11/4/14.
 */
public class HelpData {

    private static final String[] questions = new String[] {"Initial requirement to connect Glass",
                                                            "If user\'s blink does not activate scrolling",
                                                            "Sample operational sequence",
                                                            "To shut down the application"};

    private static final String[] answers = new String[] {"Install the \"My Glass\" app on your phone before starting Glass.  Once Glass " +
                                                            "is started, open the My Glass app.  Select \"Devices\" from the top menu bar " +
                                                            " and tap on the name of your Glass to select it",
                                                          "When the user of Glass winks, tilting his/her head from side to side will " +
                                                            "permit scrolling.  If the user's wink does not activate scrolling, simply " +
                                                            "hit the \"SIM WINK\" button on the Tilt Controller app.",
                                                          "Six quick steps are required enable scrolling on Glass.  First start Glass via " +
                                                            "it's power button behind the frame sidebar.  Say \"OK Glass\" at the prompt. " +
                                                            "Then say \"Tilt Control\" at the next prompt.  When the word \"Finish\" is " +
                                                            "displayed, swipe down on Glass\'s control bar to turn off the screen. " +
                                                            "Start the \"Tilt Controller\" application.  Finally, tap \"Enable Tilt " +
                                                            "Control\" to establish the connection.",
                                                          "To shut down the application, simply hit the \"Disable Tilt Control\" button " +
                                                            "on the Tilt Controller app and power Glass down"};

    public static String[] getQuestions() {
        return questions;
    }

    public static String[] getAnswers() {
        return answers;
    }
}
