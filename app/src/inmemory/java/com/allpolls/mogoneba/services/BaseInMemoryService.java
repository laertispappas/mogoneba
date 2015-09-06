package com.allpolls.mogoneba.services;

import android.os.Handler;

import com.allpolls.mogoneba.infrastructure.MogonebaApplication;
import com.squareup.otto.Bus;

import java.util.Random;

public abstract class BaseInMemoryService {
    protected final Bus bus;
    protected final MogonebaApplication application;
    protected final Handler handler;
    protected final Random random;

    protected BaseInMemoryService(MogonebaApplication application) {
        this.application = application;
        this.bus = application.getBus();
        handler = new Handler();    // sync access to main UI thread
        random = new Random();      // random timing for inmemmory debug variant
        bus.register(this);
    }

    protected void invokeDelayed(Runnable runnable, long millisecondMin, long millisecondMax) {
        if(millisecondMin > millisecondMax)
            throw new IllegalArgumentException("Min must be smalled than max");

        long delay = (long) (random.nextDouble() * (millisecondMax - millisecondMin)) + millisecondMin;
        handler.postDelayed(runnable, delay);
    }

    protected void postDelayed(final Object event, long millisecondMin, long millisecondMax) {
        invokeDelayed(new Runnable() {
            @Override
            public void run() {
                bus.post(event);
            }
        }, millisecondMin, millisecondMax);
    }

    protected void postDelayed(Object event, long milliseconds) {
        postDelayed(event, milliseconds, milliseconds);
    }

    protected void postDelayed(Object event) {
        postDelayed(event, 600, 1200);
    }
}