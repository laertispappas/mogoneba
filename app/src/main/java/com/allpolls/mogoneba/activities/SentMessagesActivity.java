package com.allpolls.mogoneba.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.allpolls.mogoneba.R;
import com.allpolls.mogoneba.services.Messages;
import com.allpolls.mogoneba.services.entities.Message;
import com.allpolls.mogoneba.views.MainNavDrawer;
import com.allpolls.mogoneba.views.MessagesAdapter;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

public class SentMessagesActivity extends BaseAuthenticatedActivity implements MessagesAdapter.OnMessageClickedListener {
    private static int REQUEST_VIEW_MESSAGE = 1;

    private MessagesAdapter adapter;
    private ArrayList<Message> messages;
    private View progressFrame;

    @Override
    protected void onMogonebaCreate(Bundle savedState) {
        setContentView(R.layout.activity_sent_messages);
        setNavDrawer(new MainNavDrawer(this));

        getSupportActionBar().setTitle("Sent Messages");

        adapter = new MessagesAdapter(this, this);
        messages = adapter.getMessages();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.activity_sent_messages_messages);
        recyclerView.setAdapter(adapter);

        if (isTablet) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }

        progressFrame = findViewById(R.id.activity_sent_messages_progressFrame);

        // refresh messages every 3 minutes
        scheduler.postEveryMilliseconds(new Messages.SearchMessagesRequest(true, false), 1000 * 60 * 3);


    }

    @Override
    public void onMessageClicked(Message message) {
        Intent intent = new Intent(this, MessageActivity.class);
        intent.putExtra(MessageActivity.EXTRA_MESSAGE, message);
        startActivityForResult(intent, REQUEST_VIEW_MESSAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode != REQUEST_VIEW_MESSAGE || resultCode != MessageActivity.REQUEST_IMAGE_DELETED) {
            return;
        }

        int messageId = data.getIntExtra(MessageActivity.RESULT_EXTRA_MESSAGE_ID, -1);

        if (messageId == -1)
            return;

        // Refactor for more efficient search
        for (int i = 0; i < messages.size(); i++) {
            Message message = messages.get(i);
            if(message.getId() != messageId ){
                continue;
            }

            messages.remove(i);
            adapter.notifyItemRemoved(i);
            break;
        }
    }

    @Subscribe
    public void onMessagesLoader(Messages.SearchMessagesResponse response) {
        response.showErrorToast(this);

        int oldMessagesSize = messages.size();
        messages.clear();
        adapter.notifyItemRangeRemoved(0, oldMessagesSize);

        messages.addAll(response.Messages);
        adapter.notifyItemRangeInserted(0, messages.size());

        progressFrame.setVisibility(View.GONE);
    }
}
