package com.allpolls.mogoneba.activities;

import android.os.Bundle;

import com.allpolls.mogoneba.R;
import com.allpolls.mogoneba.views.MainNavDrawer;

public class SentMessagesActivity extends BaseAuthenticatedActivity {
    @Override
    protected void onMogonebaCreate(Bundle savedState) {
        setContentView(R.layout.activity_sent_messages);
        setNavDrawer(new MainNavDrawer(this));
    }
}
