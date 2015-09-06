package com.allpolls.mogoneba.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.allpolls.mogoneba.R;
import com.allpolls.mogoneba.activities.AddContactActivity;
import com.allpolls.mogoneba.activities.BaseActivity;
import com.allpolls.mogoneba.activities.ContactActivity;
import com.allpolls.mogoneba.activities.ContactsActivity;
import com.allpolls.mogoneba.services.Contacts;
import com.allpolls.mogoneba.services.entities.UserDetails;
import com.allpolls.mogoneba.views.UserDetailsAdapter;
import com.squareup.otto.Subscribe;

public class ContactsFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    private UserDetailsAdapter adapter;
    private View progressFrame;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        adapter = new UserDetailsAdapter((BaseActivity) getActivity());
        progressFrame = view.findViewById(R.id.fragment_contacts_progressFrame);

        ListView listView = (ListView) view.findViewById(R.id.fragment_contacts_list);
        listView.setEmptyView(view.findViewById(R.id.fragment_contacts_emptyList));

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        bus.post(new Contacts.GetContactsRequests(false));

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        UserDetails details = adapter.getItem(position);

        Intent intent = new Intent(getActivity(), ContactActivity.class);
        intent.putExtra(ContactActivity.EXTRA_USER_DETAILS, details);

        startActivity(intent);
    }

    @Subscribe
    public void onContactsResponse(final Contacts.GetContactsResponse response) {
        scheduler.invokeOnResume(Contacts.GetContactsResponse.class, new Runnable() {
            @Override
            public void run() {
                progressFrame.animate()
                        .alpha(0)
                        .setDuration(250)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                progressFrame.setVisibility(View.GONE);
                            }
                        })
                        .start();

                if (!response.didSucceed()) {
                    response.showErrorToast(getActivity());
                    return;
                }

                adapter.clear();
                adapter.addAll(response.Contacts);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_contacts, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.fragment_contacts_menu_addContact) {
            startActivity(new Intent(getActivity(), AddContactActivity.class));
            return  true;
        }

        return super.onOptionsItemSelected(item);
    }
}
