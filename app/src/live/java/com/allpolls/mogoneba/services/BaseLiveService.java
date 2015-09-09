package com.allpolls.mogoneba.services;

import com.allpolls.mogoneba.infrastructure.MogonebaApplication;
import com.squareup.otto.Bus;

public abstract class BaseLiveService {
    protected final Bus bus;
    protected final MogonebaWebService api;
    protected final MogonebaApplication application;

    protected BaseLiveService(MogonebaApplication application, MogonebaWebService api) {
        this.api = api;
        this.application = application;
        this.bus = application.getBus();
        this.bus.register(this);
    }
}
