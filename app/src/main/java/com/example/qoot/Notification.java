package com.example.qoot;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Notification {
    String Comment;
    String Date;
    float Rate;
    String Time;
    String From;
    String Event_Type;
    String Notifiarion_Type;
    String Message;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String from_name;

    public Notification(String comment, String date, float rate, String time, String from, String event_Type, String notifiarion_Type, String message,String from_name) {
        Comment = comment;
        Date = date;
        Rate = rate;
        Time = time;
        From = from;
        Event_Type = event_Type;
        Notifiarion_Type = notifiarion_Type;
        Message = message;
        this.from_name=from_name;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public float getRate() {
        return Rate;
    }

    public void setRate(float rate) {
        Rate = rate;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getFrom() {
        return From;
    }

    public void setFrom(String from) {
        From = from;
    }

    public String getEvent_Type() {
        return Event_Type;
    }

    public void setEvent_Type(String event_Type) {
        Event_Type = event_Type;
    }

    public String getNotifiarion_Type() {
        return Notifiarion_Type;
    }

    public void setNotifiarion_Type(String notifiarion_Type) {
        Notifiarion_Type = notifiarion_Type;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getFrom_name() {

        return from_name;
    }
}
