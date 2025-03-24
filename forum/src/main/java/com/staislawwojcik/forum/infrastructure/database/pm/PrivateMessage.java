package com.staislawwojcik.forum.infrastructure.database.pm;

import com.staislawwojcik.forum.infrastructure.database.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Entity
public class PrivateMessage {
    @Id
    private String id;
    @ManyToOne
    private User receiver;
    @ManyToOne
    private User sender;

    private String message;
    private LocalDateTime sendAt;


    public PrivateMessage(String id, User receiver, User sender, String message, LocalDateTime sendAt) {
        this.id = id;
        this.receiver = receiver;
        this.sender = sender;
        this.message = message;
        this.sendAt = sendAt;
    }


    public PrivateMessage() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getSendAt() {
        return sendAt.truncatedTo(ChronoUnit.MILLIS).withNano((sendAt.getNano()/10_000_000)*10_000_000);
    }

    public void setSendAt(LocalDateTime sendAt) {
        this.sendAt = sendAt;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrivateMessage that = (PrivateMessage) o;
        return Objects.equals(id, that.id) && Objects.equals(receiver, that.receiver) && Objects.equals(sender, that.sender) && Objects.equals(message, that.message) && Objects.equals(sendAt, that.sendAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, receiver, sender, message, sendAt);
    }

    @Override
    public String toString() {
        return "PrivateMessage{" +
                "id='" + id + '\'' +
                ", receiver=" + receiver +
                ", sender=" + sender +
                ", message='" + message + '\'' +
                ", sendAt=" + sendAt +
                '}';
    }
}
