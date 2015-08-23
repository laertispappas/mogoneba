package com.allpolls.mogoneba.infrastructure;

import android.app.Application;

public class MogonebaApplication extends Application {
    private Auth auth;

    @Override
    public void onCreate(){
        auth = new Auth(this);
    }

    public Auth getAuth(){
        return this.auth;
    }
}
