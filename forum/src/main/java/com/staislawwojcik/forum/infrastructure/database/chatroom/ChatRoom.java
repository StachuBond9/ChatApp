package com.staislawwojcik.forum.infrastructure.database.chatroom;

import com.staislawwojcik.forum.infrastructure.database.user.User;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.util.List;
import java.util.Objects;


@Entity
public class ChatRoom {
    @Id
    private String id;

    private String name;

    @ManyToOne
    private User owner;

    @ElementCollection
    private List<ChatRoomMessage> messages;

    public ChatRoom() {
    }

    public ChatRoom(String id, String name, User owner, List<ChatRoomMessage> messages) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.messages = messages;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<ChatRoomMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatRoomMessage> messages) {
        this.messages = messages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatRoom chatRoom = (ChatRoom) o;
        return Objects.equals(id, chatRoom.id) && Objects.equals(name, chatRoom.name) && Objects.equals(owner, chatRoom.owner) && Objects.equals(messages, chatRoom.messages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, owner, messages);
    }

    @Override
    public String toString() {
        return "ChatRoom{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", owner=" + owner +
                ", messages=" + messages +
                '}';
    }
}
