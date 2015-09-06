package com.allpolls.mogoneba.activities;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.allpolls.mogoneba.R;
import com.allpolls.mogoneba.fragments.ContactsFragment;
import com.allpolls.mogoneba.fragments.PendingContactRequestsFragment;
import com.allpolls.mogoneba.views.MainNavDrawer;

public class ContactsActivity extends BaseAuthenticatedActivity implements AdapterView.OnItemSelectedListener {
    private ObjectAnimator currentAnimation;
    private ArrayAdapter<ContactsSpinnerItem> adapter;

    @Override
    protected void onMogonebaCreate(Bundle savedState) {
        setContentView(R.layout.activity_contacts);
        setNavDrawer(new MainNavDrawer(this));

        adapter = new ArrayAdapter<ContactsSpinnerItem>(this, R.layout.list_item_toolbar_spinner);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);

        adapter.add(new ContactsSpinnerItem("Contact", Color.parseColor("#00BCD4"), ContactsFragment.class));
        adapter.add(new ContactsSpinnerItem(
                "Pending Contact Requests",
                getResources().getColor(R.color.contacts_pending_contact_requests),
                PendingContactRequestsFragment.class));

        Spinner spinner = (Spinner) findViewById(R.id.activity_contacts_spinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        getSupportActionBar().setTitle(null);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ContactsSpinnerItem item = adapter.getItem(position);
        if(item == null)
            return;

        if(currentAnimation != null)
            currentAnimation.end();

        int currentColor = ((ColorDrawable) toolbar.getBackground()).getColor();

        currentAnimation = ObjectAnimator
                .ofObject(toolbar, "backgroundColor", new ArgbEvaluator(), currentColor, item.getColor())
                .setDuration(250);

        currentAnimation.start();

        Fragment fragment;

        try {
            fragment = (Fragment) item.getFragment().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .replace(R.id.activity_contacts_fragment_container, fragment)
                .commit();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private class ContactsSpinnerItem {
        private final String title;
        private final int color;
        private Class fragment;


        private ContactsSpinnerItem(String title, int color, Class fragment) {
            this.title = title;
            this.color = color;
            this.fragment = fragment;
        }

        public String getTitle() {
            return title;
        }

        public int getColor() {
            return color;
        }

        public Class getFragment() {
            return fragment;
        }

        @Override
        public String toString(){
            return getTitle();
        }
    }
}
