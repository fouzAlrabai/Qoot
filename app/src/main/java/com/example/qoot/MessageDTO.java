package com.example.qoot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class MessageDTO {

    private String senderId;
    private String senderName;
    private String requestID;
    private String text;

    @ServerTimestamp
    private Date dateSent;

    public MessageDTO(){}

    public MessageDTO(String senderId, String senderName,  String requestID, String text) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.requestID = requestID;
        this.text = text;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDateSent() {
        return dateSent;
    }

    public void setDateSent(Date dateSent) {
        this.dateSent = dateSent;
    }
}


