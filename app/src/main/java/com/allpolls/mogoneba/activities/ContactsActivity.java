package com.allpolls.mogoneba.activities;

import android.os.Bundle;

import com.allpolls.mogoneba.R;
import com.allpolls.mogoneba.views.MainNavDrawer;

public class ContactsActivity extends BaseAuthenticatedActivity {
    @Override
    protected void onMogonebaCreate(Bundle savedState) {
        setContentView(R.layout.activity_contacts);
        setNavDrawer(new MainNavDrawer(this));
    }
}
