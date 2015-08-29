package com.allpolls.mogoneba.dialogs;

import android.app.DialogFragment;
import android.os.Bundle;

import com.allpolls.mogoneba.infrastructure.MogonebaApplication;

public class BaseDialogFragment extends DialogFragment{
    protected MogonebaApplication application;

    @Override
    public void onCreate(Bundle savedState){
        super.onCreate(savedState);
        application = (MogonebaApplication) getActivity().getApplication();
    }
}
