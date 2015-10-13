package com.allpolls.mogoneba.services;

import android.util.Log;

import com.allpolls.mogoneba.infrastructure.MogonebaApplication;

public class Module {

    public static void register(MogonebaApplication application){
        new InMemoryAccountService(application);
        new InMemoryContactService(application);
        new InMemoryMessagesService(application);
    }
}