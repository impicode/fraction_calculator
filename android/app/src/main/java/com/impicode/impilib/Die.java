package com.impicode.impilib;

/**
 * (c) impicode
 * Stamp: PLE 2017-09-07
 */

public class Die extends RuntimeException {
    public Die() {
        super();
    }
    public Die(String s) {
        super(s);
    }
    public Die(String s, Throwable throwable) {
        super(s, throwable);
    }
    public Die(Throwable throwable) {
        super(throwable);
    }
}
