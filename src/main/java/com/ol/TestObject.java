package com.ol;

import org.openjdk.jmh.infra.Blackhole;

/**
 * Created by Semernitskaya on 17.10.2017.
 */
class TestObject {

    public static final int TOKENS = 3;

    public static void doSomethingStatic(String s) {
        Blackhole.consumeCPU(TOKENS);
    }

    public void doSomethingPublic(String s) {
        Blackhole.consumeCPU(TOKENS);
    }

    private void doSomethingPrivate(String s) {
        Blackhole.consumeCPU(TOKENS);
    }
}
