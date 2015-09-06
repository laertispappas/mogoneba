package com.allpolls.mogoneba.services.entities;

import java.util.Calendar;

public class ContactRequest {
    private int id;
    private boolean isFromUs;
    private UserDetails user;
    private Calendar createdAt;

    public ContactRequest(int id, boolean isFromUs, UserDetails user, Calendar createdAt) {

        this.id = id;
        this.isFromUs = isFromUs;
        this.user = user;
        this.createdAt = createdAt;
    }


    public int getId() {
        return id;
    }

    public boolean isFromUs() {
        return isFromUs;
    }

    public UserDetails getUser() {
        return user;
    }

    public Calendar getCreatedAt() {
        return createdAt;
    }
}
