package com.allpolls.mogoneba.services;

import com.allpolls.mogoneba.infrastructure.MogonebaApplication;
import com.allpolls.mogoneba.services.entities.Message;
import com.allpolls.mogoneba.services.entities.UserDetails;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class InMemoryMessagesService extends BaseInMemoryService {
    protected InMemoryMessagesService(MogonebaApplication application) {
        super(application);
    }

    @Subscribe
    public void onDeleteMessage(Messages.DeleteMessageRequest request) {
        Messages.DeleteMessageResponse response = new Messages.DeleteMessageResponse();
        postDelayed(response);
    }

    @Subscribe
    public void searchMessages(Messages.SearchMessagesRequest request) {
        Messages.SearchMessagesResponse response = new Messages.SearchMessagesResponse();
        response.Messages = new ArrayList<>();

        UserDetails[] users = new UserDetails[10];
        for (int i = 0; i < users.length; i++){
            String stringId = Integer.toString(i);
            users[i] = new UserDetails(
                    i,
                    true,
                    "User " + stringId,
                    "user_" + stringId,
                    "http://www.gravatar.com/avatar/" + stringId + "?d=identicon");
        }

        Random random = new Random();
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DATE, -100);

        for (int i = i = 0; i < 100; i++) {
            boolean isFromUs;

            if(request.IncludeReceivedMessages && request.IncludeSentMessages) {
                isFromUs = random.nextBoolean();
            } else {
                isFromUs = !request.IncludeReceivedMessages;
            }

            date.set(Calendar.MINUTE, random.nextInt(60 * 24));

            String numberString = Integer.toString(i);
            response.Messages.add(new Message(
                    i,
                    (Calendar) date.clone(),
                    "Short message " + numberString,
                    "Long message " + numberString,
                    "",
                    users[random.nextInt(users.length)],
                    isFromUs,
                    i > 4));

            postDelayed(response, 2000);
        }
    }

    @Subscribe
    public void sendMessage(Messages.SendMessageRequest request) {
        Messages.SendMessageResponse response = new Messages.SendMessageResponse();

        if (request.getMessage().equals("error")) {
            response.setOperationError("Something bad happened!");
        } else if (request.getMessage().equals("error-message")) {
            response.setPropertyError("message", "There was an error");
        }

        postDelayed(response, 1500, 3000);
    }
}


