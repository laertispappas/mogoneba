package com.allpolls.mogoneba.infrastructure;

import android.app.Application;

import com.allpolls.mogoneba.services.Module;
import com.squareup.otto.Bus;

public class MogonebaApplication extends Application {
    private Auth auth;
    private Bus bus;

    public MogonebaApplication(){
        bus = new Bus();
    }

    @Override
    public void onCreate(){
        auth = new Auth(this);
        Module.register(this);
    }

    public Auth getAuth(){
        return this.auth;
    }

    public Bus getBus(){
        return bus;
    }
}
