package com.allpolls.mogoneba.services;

import com.allpolls.mogoneba.infrastructure.MogonebaApplication;
import com.allpolls.mogoneba.infrastructure.RetrofitCallback;
import com.allpolls.mogoneba.infrastructure.RetrofitCallbackPost;
import com.squareup.otto.Subscribe;

public class LiveContactsService extends BaseLiveService {
    public LiveContactsService(MogonebaApplication application, MogonebaWebService api) {
        super(application, api);
    }

    @Subscribe
    public void searchUsers(Contacts.SearchUsersRequest request) {
        api.searchUsers(request.Query, new RetrofitCallbackPost<Contacts.SearchUsersResponse>(Contacts.SearchUsersResponse.class, bus));
    }

    @Subscribe
    public void sendContactRequest(Contacts.SendContactRequestRequest request) {
        api.sendContactRequest(request.UserId, new RetrofitCallbackPost<Contacts.SendContactRequestResponse>(Contacts.SendContactRequestResponse.class, bus));
    }

    @Subscribe
    public void getContactRequests(Contacts.GetContactRequestsRequest request) {
        if (request.FromUs) {
            api.getContactRequestsFromUs(new RetrofitCallbackPost<Contacts.GetContactsRequestsResponse>(Contacts.GetContactsRequestsResponse.class, bus));
        } else {
            api.getContactRequestsToUs(new RetrofitCallbackPost<Contacts.GetContactsRequestsResponse>(Contacts.GetContactsRequestsResponse.class, bus));
        }

    }

    @Subscribe
    public void respondToContactRequest(Contacts.RespondToContactRequestRequest request) {
        String response;
        if (request.Accept) {
            response = "accept";
        } else {
            response = "reject";
        }

        api.respondToContctRequest(
                request.ContactRequestId,
                new MogonebaWebService.RespondToContactRequest(response),
                new RetrofitCallbackPost<Contacts.RespondToContactRequestResponse>(Contacts.RespondToContactRequestResponse.class, bus));
    }

    @Subscribe
    public void getContacts(Contacts.GetContactsRequests request) {
        api.getContacts(new RetrofitCallbackPost<Contacts.GetContactsResponse>(Contacts.GetContactsResponse.class, bus));
    }

    @Subscribe
    public void removeContact(Contacts.RemoveContactRequest request) {
        api.removeContact(request.ContactId, new RetrofitCallbackPost<Contacts.RemoveContactResponse>(Contacts.RemoveContactResponse.class, bus));
    }
}