package com.allpolls.mogoneba.services.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Message implements Parcelable {
    private int id;
    private Calendar createdAt;
    private String shortMessage;
    private String longMessage;
    private String imageUrl;
    private UserDetails otherUser;
    private boolean isFromUs;
    private boolean isRead;
    private boolean isSelected;


    private Message(Parcel in) {
        id = in.readInt();
        createdAt = new GregorianCalendar();
        createdAt.setTimeInMillis(in.readLong());
        shortMessage = in.readString();
        longMessage = in.readString();
        imageUrl = in.readString();
        otherUser = (UserDetails) in.readParcelable(UserDetails.class.getClassLoader());
        isFromUs = in.readByte() == 1;
        isRead = in.readByte() == 1;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeLong(createdAt.getTimeInMillis());
        out.writeString(shortMessage);
        out.writeString(longMessage);
        out.writeString(imageUrl);
        out.writeParcelable(otherUser, 0);
        out.writeByte((byte) (isFromUs ? 1 : 0));
        out.writeByte((byte) (isRead ? 1: 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Message(int id,
                   Calendar createdAt,
                   String shortMessage,
                   String longMessage,
                   String imageUrl,
                   UserDetails otherUser,
                   boolean isFromUs,
                   boolean isRead) {
        this.id = id;
        this.createdAt = createdAt;
        this.shortMessage = shortMessage;
        this.longMessage = longMessage;
        this.imageUrl = imageUrl;
        this.otherUser = otherUser;
        this.isFromUs = isFromUs;
        this.isRead = isRead;
    }

    public int getId() {
        return id;
    }

    public Calendar getCreatedAt() {
        return createdAt;
    }

    public String getShortMessage() {
        return shortMessage;
    }

    public String getLongMessage() {
        return longMessage;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public UserDetails getOtherUser() {
        return otherUser;
    }

    public boolean isFromUs() {
        return isFromUs;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel source) {
            return new Message(source);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };
}
