package com.multicraft.util;

public abstract class StoppableThread extends Thread {

    private volatile boolean isStopped = false;

    public boolean isStopped() {
        return isStopped;
    }

    public void stopThread() {
        isStopped = true;
    }
}
