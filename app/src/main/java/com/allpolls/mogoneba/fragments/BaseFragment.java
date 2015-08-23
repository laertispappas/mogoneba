package com.allpolls.mogoneba.fragments;

import android.app.Fragment;
import android.os.Bundle;

import com.allpolls.mogoneba.infrastructure.MogonebaApplication;

public class BaseFragment extends Fragment {
    protected MogonebaApplication application;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        application = (MogonebaApplication) getActivity().getApplication();
    }

}
