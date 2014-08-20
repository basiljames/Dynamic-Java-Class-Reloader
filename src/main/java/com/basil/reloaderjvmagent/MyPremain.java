package com.basil.reloaderjvmagent;

import java.lang.instrument.Instrumentation;

/**
 * This the the agent class loaded by the JVM
 * @author Basil
 */
public class MyPremain {
    public static void premain(String agentArgs, Instrumentation inst) {
        UpdateClassLoader.getInstance().setInstrumentation(inst);
    }
}




