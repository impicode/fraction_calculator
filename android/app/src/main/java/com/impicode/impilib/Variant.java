package com.impicode.impilib;

/**
 * (c) impicode
 * Stamp: PLE 2017-09-18
 */

public class Variant {

    private String name;
    private Object o;

    public Variant(String name) {
        this.name = name;
    }

    public Variant(String name, Object o) {
        this.name = name;
        this.o = o;
    }

    public boolean hasValue() {
        return o != null;
    }


    public boolean is(String name) {
        return this.name.equals(name);
    }

    public <T> T as(String name) {
        Impi.checkNull(o);
        if (!this.name.equals(name)) {
            Impi.die(this.name);
        }
        return (T)o;
    }
}
