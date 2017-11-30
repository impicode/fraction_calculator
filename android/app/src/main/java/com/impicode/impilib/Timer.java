package com.impicode.impilib;

import android.util.Log;

import java.util.ArrayList;


/**
 * (c) impicode
 * Stamp: PLE 2017-09-19
 */

public class Timer {

    private static ArrayList<Long> list = new ArrayList<>();

    private Timer() {
        list = new ArrayList<>();
    }

    static public void start() {
        list.add(System.currentTimeMillis());
    }

    static public void stop() {
        Log.v("profile", (getParentFrameDescription() + " Time: " +
                (System.currentTimeMillis() - (double)list.get(list.size() - 1)) / 1000));
        list.remove(list.size() - 1);
    }

    static private String getParentFrameDescription() {
        StackTraceElement frame = new Exception().getStackTrace()[2];
        return frame.getFileName() + ":" + frame.getClassName() + ":" + frame.getMethodName() + ":" + frame.getLineNumber();
    }
}
