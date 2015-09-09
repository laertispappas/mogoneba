package com.allpolls.mogoneba.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.allpolls.mogoneba.R;
import com.allpolls.mogoneba.services.Messages;
import com.allpolls.mogoneba.services.entities.UserDetails;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

public class SendMessageActivity extends BaseAuthenticatedActivity implements View.OnClickListener {
    public static final String EXTRA_IMAGE_PATH = "EXTRA_IMAGE_PATH";
    public static final String EXTRA_CONTACT = "EXTRA_CONTACT";
    public static final String RESULT_MESSAGE = "RESULT_MESSAGE";

    public static final int MAX_IMAGE_HEIGHT = 1000;

    private Messages.SendMessageRequest request;
    private EditText messageEditText;
    private Button recipientButton;
    private View progressFrame;

    private static final String STATE_REQUEST ="STATE_REQUEST";
    private static final int REQUEST_SELECT_RECIPIENT = 1;


    protected void onMogonebaCreate(Bundle savedState) {
        setContentView(R.layout.activity_send_message);
        getSupportActionBar().setTitle("Send Message");

        if (savedState != null) {
            request = savedState.getParcelable(STATE_REQUEST);
        }

        if (request == null) {
            request = new Messages.SendMessageRequest();
            request.setRecipient((UserDetails) getIntent().getParcelableExtra(EXTRA_CONTACT));
        }

        Uri imageUri = getIntent().getParcelableExtra(EXTRA_IMAGE_PATH);

        if (imageUri != null) {
            ImageView image = (ImageView) findViewById(R.id.activity_send_message_image);

            Picasso picasso = Picasso.with(this);
            picasso.invalidate(imageUri);           // do not cache image
            picasso.load(imageUri).into(image);

            request.setImagePath(imageUri);
        }

        if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_PORTRAIT) {
            View optionFrame = findViewById(R.id.activity_send_message_optionsFrame);

            RelativeLayout.LayoutParams params =
                    (RelativeLayout.LayoutParams) optionFrame.getLayoutParams();

            params.addRule(RelativeLayout.ALIGN_PARENT_END);
            params.addRule(RelativeLayout.BELOW, R.id.include_toolbar);
            params.width =
                    (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            300,
                            getResources().getDisplayMetrics());

            optionFrame.setLayoutParams(params);
        }

        progressFrame = findViewById(R.id.activity_send_message_progressFrame);
        recipientButton = (Button) findViewById(R.id.activity_send_message_recipient);
        messageEditText = (EditText) findViewById(R.id.activity_send_message_message);

        progressFrame.setVisibility(View.GONE);

        recipientButton.setOnClickListener(this);
        updateButtons();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_REQUEST, request);
    }

    @Override
    public void onClick(View view) {
        if (view == recipientButton) {
            selectRecipient();
        }
    }

    private void updateButtons() {
        UserDetails recipient = request.getRecipient();
        if (recipient != null) {
            recipientButton.setText("To: " + recipient.getDisplayName());
        } else {
            recipientButton.setText("Choose Recipient");
        }
    }

    private void selectRecipient() {
        startActivityForResult(new Intent(this, SelectContactActivity.class), REQUEST_SELECT_RECIPIENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SELECT_RECIPIENT && resultCode == RESULT_OK) {
            UserDetails selectedContact = data.getParcelableExtra(SelectContactActivity.RESULT_CONTACT);
            request.setRecipient(selectedContact);
            updateButtons();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_send_message, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.activity_send_message_menuSend) {
            sendMessage();
            return true;
        }

        return false;
    }

    private void sendMessage() {
        String message = messageEditText.getText().toString();
        if(message.length() < 2) {
            messageEditText.setError("Please enter a longer message.");
            return;
        }

        messageEditText.setError(null);

        if (request.getRecipient() == null) {
            Toast.makeText(this, "Please select a recipient", Toast.LENGTH_SHORT).show();
            selectRecipient();
            return;
        }

        progressFrame.setVisibility(View.VISIBLE);
        progressFrame.setAlpha(0);
        progressFrame.animate().alpha(1).setDuration(250).start();

        request.setMessage(message);
        bus.post(request);
    }

    @Subscribe
    public void onMessageSend(Messages.SendMessageResponse response) {
        if (!response.didSucceed()) {
            response.showErrorToast(this);
            messageEditText.setError(response.getPropertyError("message"));
            progressFrame
                    .animate()
                    .alpha(0)
                    .setDuration(200)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            progressFrame.setVisibility(View.GONE);
                        }
                    })
                    .start();
            return;
        }

        Intent data = new Intent();
        data.putExtra(RESULT_MESSAGE, response.Message);

        setResult(RESULT_OK, data);
        finish();
    }
}
