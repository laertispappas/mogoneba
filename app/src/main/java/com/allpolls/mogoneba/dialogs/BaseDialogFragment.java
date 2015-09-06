package com.allpolls.mogoneba.dialogs;

import android.app.DialogFragment;
import android.os.Bundle;

import com.allpolls.mogoneba.infrastructure.ActionScheduler;
import com.allpolls.mogoneba.infrastructure.MogonebaApplication;
import com.squareup.otto.Bus;

public class BaseDialogFragment extends DialogFragment{
    protected MogonebaApplication application;
    protected Bus bus;
    protected ActionScheduler scheduler;


    @Override
    public void onCreate(Bundle savedState){
        super.onCreate(savedState);
        application = (MogonebaApplication) getActivity().getApplication();
        scheduler = new ActionScheduler(application);

        bus = application.getBus();
        bus.register(this);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        bus.unregister(this);
    }

    @Override
    public void onPause(){
        super.onPause();
        scheduler.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();
        scheduler.onResume();
    }
}
