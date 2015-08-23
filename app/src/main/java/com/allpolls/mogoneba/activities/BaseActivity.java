package com.allpolls.mogoneba.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.allpolls.mogoneba.infrastructure.MogonebaApplication;

public abstract class BaseActivity extends ActionBarActivity{
    protected MogonebaApplication application;

    @Override
    protected void onCreate(Bundle savedState){
        super.onCreate(savedState);

        application = (MogonebaApplication) getApplication();
    }
}
