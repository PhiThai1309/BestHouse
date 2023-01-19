package com.team5.besthouse.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Locale;
import java.util.Objects;

public class TextMessage implements Serializable {
    private String id;
    private String chatId;
    private String senderEmail;
    private String content;
    private Timestamp time;

    public TextMessage() {
    }

    public TextMessage(String chatId, String senderEmail, String content, Timestamp time) {
        this.chatId = chatId;
        this.senderEmail = senderEmail;
        this.content = content;
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TextMessage message = (TextMessage) o;
        return Objects.equals(id, message.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "TextMessage{" +
                "chatId='" + chatId + '\'' +
                ", senderEmail='" + senderEmail + '\'' +
                ", content='" + content + '\'' +
                ", time=" + time +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    @Exclude
    public String getFormattedTime(){
        Locale locale = new Locale.Builder().setLanguage("en").setRegion("US").build();
        DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.DEFAULT, locale);
        return dateFormat.format(getTime().toDate());
    }

    @Exclude
    public String getFormattedDate(){
        Locale locale = new Locale.Builder().setLanguage("en").setRegion("US").build();
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);
        return dateFormat.format(getTime().toDate());
    }

    @Exclude
    public boolean isSender(User user) {
        return this.senderEmail.equals(user.getEmail());
    }
}
