package com.staislawwojcik.forum.domain;

import com.staislawwojcik.forum.infrastructure.database.pm.PrivateMessage;
import com.staislawwojcik.forum.infrastructure.database.pm.PrivateMessageRepository;
import com.staislawwojcik.forum.infrastructure.database.user.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class PrivateMessageService {
    private final UserService users;
    private final PrivateMessageRepository privateMessageRepository;


    public PrivateMessageService(UserService users, PrivateMessageRepository privateMessageRepository) {
        this.users = users;
        this.privateMessageRepository = privateMessageRepository;
    }


    public PrivateMessage createPM(String receiverId, String senderToken, String message) {
        User receiver = users.getById(receiverId);
        User sender = users.getByToken(senderToken);

        PrivateMessage pm = new PrivateMessage(UUID.randomUUID().toString(), receiver, sender, message, LocalDateTime.now());
        return privateMessageRepository.save(pm);
    }

    public List<PrivateMessage> getMessagesFromConversation(String receiverID, String ownerToken) {
        User receiver = users.getById(receiverID);
        User sender = users.getByToken(ownerToken);
        List<PrivateMessage> privateMessages = privateMessageRepository.findAll();

        List<PrivateMessage> messagesFromSender = new ArrayList<>(privateMessages.stream()
                .filter(privateMessage -> privateMessage.getReceiver().equals(receiver) && privateMessage.getSender().equals(sender)).toList());
        List<PrivateMessage> messagesFromReceiver = privateMessages.stream()
                .filter(privateMessage -> privateMessage.getReceiver().equals(sender) && privateMessage.getSender().equals(receiver)).toList();

        messagesFromSender.addAll(messagesFromReceiver);
        messagesFromSender.sort(Comparator.comparing(PrivateMessage::getSendAt));
        return messagesFromSender;
    }

    public List<User> getUsers(String ownerToken) {
        User owner = users.getByToken(ownerToken);
        List<User> userList =  users.usersList();
        userList.remove(owner);
        return userList;
    }
}
