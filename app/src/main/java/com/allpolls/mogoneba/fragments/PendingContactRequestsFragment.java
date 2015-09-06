package com.allpolls.mogoneba.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.allpolls.mogoneba.R;
import com.allpolls.mogoneba.activities.BaseActivity;
import com.allpolls.mogoneba.services.Contacts;
import com.allpolls.mogoneba.views.ContactRequestsAdapter;
import com.squareup.otto.Subscribe;

public class PendingContactRequestsFragment extends BaseFragment {
    private View progressFrame;
    private ContactRequestsAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pending_contact_requests, container, false);

        progressFrame = view.findViewById(R.id.fragment_pending_contact_requests_progressFrame);
        adapter = new ContactRequestsAdapter((BaseActivity) getActivity());

        ListView listView = (ListView) view.findViewById(R.id.fragment_pending_contact_requests_list);
        listView.setAdapter(adapter);

        bus.post(new Contacts.GetContactRequestsRequest(true));

        return view;
    }

    @Subscribe
    public void onGetContactRequests(final Contacts.GetContactsRequestsResponse response) {
        scheduler.invokeOnResume(Contacts.GetContactsRequestsResponse.class, new Runnable() {
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
                adapter.addAll(response.Requests);
            }
        });
    }
}
