package com.allpolls.mogoneba.services;

import com.allpolls.mogoneba.infrastructure.ServiceResponse;
import com.allpolls.mogoneba.services.entities.ContactRequest;
import com.allpolls.mogoneba.services.entities.UserDetails;

import java.util.List;

public final class Contacts {
    private Contacts(){

    }

    public static class GetContactRequestsRequest {
        public boolean FromUs;

        public GetContactRequestsRequest(boolean fromUs) {
            FromUs = fromUs;
        }
    }

    public static class GetContactsRequestsResponse extends ServiceResponse{
        public List<ContactRequest> Requests;
    }

    public static class GetContactsRequests {
        public boolean IncludePendingContacts;

        public GetContactsRequests(boolean includePendingContacts) {
            IncludePendingContacts = includePendingContacts;
        }
    }

    public static class GetContactsResponse extends ServiceResponse {
        public List<UserDetails> Contacts;
    }

    public static class SendContactRequestRequest {
        public int UserId;

        public SendContactRequestRequest(int userId) {
            UserId = userId;
        }
    }

    public static class SendContactRequestResponse extends ServiceResponse {
    }

    public static class RespondToContactRequestRequest {
        public int ContactRequestId;
        public boolean Accept;

        public RespondToContactRequestRequest(int contactRequestId, boolean accept) {
            ContactRequestId = contactRequestId;
            Accept = accept;
        }
    }

    public static class RespondToContactRequestResponse extends ServiceResponse{
    }

    public static class RemoveContactRequest {
        public int ContactId;

        public RemoveContactRequest(int contactId) {
            ContactId = contactId;
        }
    }

    public static class RemoveContactResponse extends ServiceResponse {
        public int RemoveContactId;
    }

    public static class SearchUsersRequest {
        public String Query;

        public SearchUsersRequest(String query) {
            Query = query;
        }
    }

    public static class SearchUsersResponse extends ServiceResponse {
        public List<UserDetails> Users;
        public String Query;
    }
}
