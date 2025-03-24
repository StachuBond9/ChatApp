package com.staislawwojcik.forum.infrastructure.database.chatroom;

import com.staislawwojcik.forum.infrastructure.database.user.User;
import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;


import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Embeddable
public class ChatRoomMessage {
    private String message;
    @ManyToOne
    private User sender;
    private LocalDateTime sendTime;

    public ChatRoomMessage(LocalDateTime sendTime, User sender, String message) {
        this.sendTime = sendTime;
        this.sender = sender;
        this.message = message;
    }

    public ChatRoomMessage() {

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public LocalDateTime getSendTime() {
        return sendTime;
    }

    public void setSendTime(LocalDateTime sendTime) {
        this.sendTime = sendTime.truncatedTo(ChronoUnit.MILLIS).withNano((sendTime.getNano()/10_000_000)*10_000_000);;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatRoomMessage that = (ChatRoomMessage) o;
        return Objects.equals(message, that.message) && Objects.equals(sender, that.sender) && Objects.equals(sendTime, that.sendTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, sender, sendTime);
    }

    @Override
    public String toString() {
        return "ChatRoomMessage{" +
                "message='" + message + '\'' +
                ", sender=" + sender +
                ", sendTime=" + sendTime +
                '}';
    }
}
