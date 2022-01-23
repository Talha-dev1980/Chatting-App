package com.chatting.chatsapp;

public class RequestInfo  {
    String recieve,sent,timeSent;

    public RequestInfo() {
    }

    public RequestInfo(String recieve, String sent, String timeSent) {
        this.recieve = recieve;
        this.sent = sent;
        this.timeSent = timeSent;
    }

    public String getRecieve() {
        return recieve;
    }

    public void setRecieve(String recieve) {
        this.recieve = recieve;
    }

    public String getSent() {
        return sent;
    }

    public void setSent(String sent) {
        this.sent = sent;
    }

    public String getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(String timeSent) {
        this.timeSent = timeSent;
    }
}
