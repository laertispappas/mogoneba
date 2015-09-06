package com.allpolls.mogoneba.fragments;

import android.app.Fragment;
import android.os.Bundle;

import com.allpolls.mogoneba.infrastructure.ActionScheduler;
import com.allpolls.mogoneba.infrastructure.MogonebaApplication;
import com.squareup.otto.Bus;

public class BaseFragment extends Fragment {
    protected MogonebaApplication application;
    protected Bus bus;
    protected ActionScheduler scheduler;


    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        application = (MogonebaApplication) getActivity().getApplication();
        bus = application.getBus();
        scheduler = new ActionScheduler(application);

        bus.register(this);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        bus.unregister(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        scheduler.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();
        scheduler.onResume();
    }
}
