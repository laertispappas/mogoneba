package com.allpolls.mogoneba.activities;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.allpolls.mogoneba.R;
import com.allpolls.mogoneba.dialogs.ChangePasswordDialog;
import com.allpolls.mogoneba.services.Contacts;
import com.allpolls.mogoneba.services.entities.UserDetails;
import com.allpolls.mogoneba.views.UserDetailsAdapter;
import com.squareup.otto.Subscribe;

public class SelectContactActivity extends BaseAuthenticatedActivity implements AdapterView.OnItemClickListener {
    public static final String RESULT_CONTACT = "RESULT_CONTACT";

    private static final int REQUEST_ADD_FRIEND = 1;
    private UserDetailsAdapter adapter;


    @Override
    protected void onMogonebaCreate(Bundle savedState) {
        setContentView(R.layout.activity_select_contact);

        getSupportActionBar().setTitle("Select contact");

        toolbar.setNavigationIcon(R.drawable.abc_switch_track_mtrl_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        adapter = new UserDetailsAdapter(this);
        ListView listView = (ListView) findViewById(R.id.activity_select_contact_listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        bus.post(new Contacts.GetContactsRequests(true));
    }

    @Subscribe
    public void onContactsReceived(Contacts.GetContactsResponse response) {
        response.showErrorToast(this);
        adapter.clear();
        adapter.addAll(response.Contacts);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        UserDetails selectedUser = adapter.getItem(position);

        Intent intent = new Intent();
        intent.putExtra(RESULT_CONTACT, selectedUser);
        setResult(RESULT_OK, intent);

        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_select_contact, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.activity_select_contact_addContact) {
            Intent intent = new Intent(this, AddContactActivity.class);
            startActivityForResult(intent, REQUEST_ADD_FRIEND);
            return true;
        }

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ADD_FRIEND && resultCode == RESULT_OK) {

            UserDetails addedContact = data.getParcelableExtra(AddContactActivity.RESULT_CONTACT);

            Intent intent = new Intent();
            intent.putExtra(RESULT_CONTACT, addedContact);
            setResult(RESULT_OK, intent);

            finish();
        }
    }
}
