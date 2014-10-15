package com.mikedg.android.tiltcontrolcontroller.threads;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by jmann on 10/15/14.
 */
public abstract class NotifyingThread extends Thread {

    private final Set<ThreadCompleteListener> listeners
            = new CopyOnWriteArraySet<ThreadCompleteListener>();
    public final void addListener(final ThreadCompleteListener listener) {
        listeners.add(listener);
    }
    public final void removeListener(final ThreadCompleteListener listener) {
        listeners.remove(listener);
    }
    private final void notifyListeners() {
        for (ThreadCompleteListener listener : listeners) {
            listener.notifyOfThreadComplete(this);
        }
    }
    @Override
    public final void run() {
        try {
            doRun();
        } finally {
            notifyListeners();
        }
    }
    public abstract void doRun();
}


