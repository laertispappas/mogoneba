package com.allpolls.mogoneba.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.allpolls.mogoneba.R;
import com.allpolls.mogoneba.services.Contacts;
import com.allpolls.mogoneba.services.Messages;
import com.allpolls.mogoneba.services.entities.ContactRequest;
import com.allpolls.mogoneba.services.entities.Message;
import com.allpolls.mogoneba.services.entities.UserDetails;
import com.allpolls.mogoneba.views.MainActivityAdapter;
import com.allpolls.mogoneba.views.MainNavDrawer;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MainActivity extends BaseAuthenticatedActivity implements View.OnClickListener, MainActivityAdapter.MainActivityListener {
    private static final int REQUEST_SHOW_MESSAGE = 1;
    private MainActivityAdapter adapter;
    private List<Message> messages;
    private List<ContactRequest> contactRequests;

    @Override
    protected void onMogonebaCreate(Bundle savedState) {
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Inbox");
        setNavDrawer(new MainNavDrawer(this));

        findViewById(R.id.activity_main_new_messageButton).setOnClickListener(this);

        adapter = new MainActivityAdapter(this, this);
        messages = adapter.getMessages();
        contactRequests = adapter.getContactRequests();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.activity_main_recyclerView);
        recyclerView.setAdapter(adapter);

        if (isTablet) {
            GridLayoutManager manager = new GridLayoutManager(this, 2);
            recyclerView.setLayoutManager(manager);
            manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (position == 0) {
                        return 2;
                    }

                    if (contactRequests.size() > 0 && position == contactRequests.size() + 1) {
                        return 2;
                    }

                    return 1;
                }
            });
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }

        scheduler.invokeEveryMilliseconds(new Runnable() {
            @Override
            public void run() {
                onRefresh();
            }
        }, 1000 * 60 * 3);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.activity_main_new_messageButton) {
            startActivity(new Intent(this, NewMessageActivity.class));
        }
    }

    @Override
    public void onRefresh() {
        swipeRefresh.setRefreshing(true);
        bus.post(new Messages.SearchMessagesRequest(false, true));
        bus.post(new Contacts.GetContactRequestsRequest(false));
    }

    @Subscribe
    public void onMessagesLoaded(final Messages.SearchMessagesResponse response) {
        scheduler.invokeOnResume(response.getClass(), new Runnable() {
            @Override
            public void run() {
                swipeRefresh.setRefreshing(false);

                if (!response.didSucceed()) {
                    response.showErrorToast(MainActivity.this);
                    return;
                }

                messages.clear();
                messages.addAll(response.Messages);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Subscribe
    public void onContactRequestLoaded(final Contacts.GetContactsRequestsResponse response) {
        scheduler.invokeOnResume(response.getClass(), new Runnable() {
            @Override
            public void run() {
                swipeRefresh.setRefreshing(false);

                if (!response.didSucceed()) {
                    response.showErrorToast(MainActivity.this);
                    return;
                }

                contactRequests.clear();
                contactRequests.addAll(response.Requests);
            }
        });
    }

    @Override
    public void onMessageClicked(Message message) {
        Intent intent = new Intent(this, MessageActivity.class);
        intent.putExtra(MessageActivity.EXTRA_MESSAGE, message);
        startActivityForResult(intent, REQUEST_SHOW_MESSAGE);
    }

    @Override
    public void onContactRequestClicked(final ContactRequest request, final int position) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_user_display, null);
        ImageView avatar = (ImageView) dialogView.findViewById(R.id.dialog_user_display_avatar);
        TextView displayName = (TextView) dialogView.findViewById(R.id.dialog_user_display_displayName);

        UserDetails user = request.getUser();
        displayName.setText(user.getDisplayName());
        Picasso.with(this).load(user.getAvatarUrl()).into(avatar);

        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == Dialog.BUTTON_NEUTRAL) {
                    return;
                }

                boolean doAccept = which == Dialog.BUTTON_POSITIVE;

                contactRequests.remove(request);
                adapter.notifyItemRemoved(position + 1);

                if (contactRequests.size() == 0) {
                    adapter.notifyItemRemoved(0);
                }

                bus.post(new Contacts.RespondToContactRequestRequest(request.getUser().getId(), doAccept));
            }
        };

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Respond to Contact Request")
                .setView(dialogView)
                .setPositiveButton("Accept", onClickListener)
                .setNeutralButton("Cancel", onClickListener)
                .setNegativeButton("Reject", onClickListener)
                .setCancelable(false)
                .create();

        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SHOW_MESSAGE) {
            // Remove Or mark as read
            int messageId = data.getIntExtra(MessageActivity.RESULT_EXTRA_MESSAGE_ID, -1);
            if (messageId == -1) {
                return;
            }

            // TODO: change from linear to other search algorithm
            for (int i = 0; i < messages.size(); i++) {
                Message message = messages.get(i);
                if (message.getId() == messageId) {
                    if (resultCode == MessageActivity.REQUEST_IMAGE_DELETED) {
                        messages.remove(message);
                    } else {
                        message.setIsRead(true);
                    }
                    adapter.notifyDataSetChanged();
                    break;
                }
            }
        }
    }
}