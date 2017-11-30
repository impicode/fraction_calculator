package com.impicode.impilib;

import android.util.Log;

/**
 * (c) impicode
 * Stamp: PLE 2017-09-08
 */

public class Impi {
    public static void die() {
        Log.v("impi", "die");
        throw new Die();
    }

    public static void die(String s) {
        Log.v("impi", "die");
        Log.v("impi", s);
        throw new Die(s);
    }

    public static void die(Exception e) {
        Log.v("impi", "die");
        e.printStackTrace();
        throw new Die();
    }

    public static void log(String s) {
        Log.v("impi", s);
    }

    public static void checkNull(Object o) {
        if (o == null) {
            Impi.die();
        }
    }
}