package com.allpolls.mogoneba.services;

import com.allpolls.mogoneba.infrastructure.MogonebaApplication;
import com.allpolls.mogoneba.infrastructure.RetrofitCallbackPost;
import com.allpolls.mogoneba.services.entities.Message;
import com.squareup.otto.Subscribe;

import java.io.File;

import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

/**
 * Created by lapis on 8/9/2015.
 */
public class LiveMessagesService extends BaseLiveService {
    public LiveMessagesService(MogonebaApplication application, MogonebaWebService api) {
        super(application, api);
    }

    @Subscribe
    public void sendMessage(Messages.SendMessageRequest request) {
        api.sendMessage(
            new TypedString(request.getMessage()),
            new TypedString(Integer.toString(request.getRecipient().getId())),
            new TypedFile("image/jpeg", new File(request.getImagePath().getPath())),
            new RetrofitCallbackPost<>(Messages.SendMessageResponse.class, bus)
        );
    }

    @Subscribe
    public void searchMessages(Messages.SearchMessagesRequest request) {
        if (request.FromContactId != -1) {
            api.searchMessages(
                    request.FromContactId,
                    request.IncludeSentMessages,
                    request.IncludeReceivedMessages,
                    new RetrofitCallbackPost<Messages.SearchMessagesResponse>(Messages.SearchMessagesResponse.class, bus)
            );
        } else {
            api.searchMessages(
                    request.IncludeSentMessages,
                    request.IncludeReceivedMessages,
                    new RetrofitCallbackPost<Messages.SearchMessagesResponse>(Messages.SearchMessagesResponse.class, bus)
            );
        }
    }

    @Subscribe
    public void deleteMessage(Messages.DeleteMessageRequest request) {
        api.deleteMessage(request.MessageId, new RetrofitCallbackPost<Messages.DeleteMessageResponse>(Messages.DeleteMessageResponse.class, bus));
    }

    @Subscribe
    public void markMessageAsRead(Messages.MarkMessageAsReadRequest request) {
        api.markMessageAsRead(request.MessageId, new RetrofitCallbackPost<Messages.MarkMessageAsReadResponse>(Messages.MarkMessageAsReadResponse.class, bus));
    }

    @Subscribe
    public void getMessageDetails(Messages.GetMessageDetailsRequest request) {
        api.getMessageDetails(request.Id, new RetrofitCallbackPost<Messages.GetMessageDetailsResponse>(Messages.GetMessageDetailsResponse.class, bus));
    }
}
