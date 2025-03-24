package com.staislawwojcik.forum.domain;

import com.staislawwojcik.forum.infrastructure.database.chatroom.ChatRoom;
import com.staislawwojcik.forum.infrastructure.database.chatroom.ChatRoomMessage;
import com.staislawwojcik.forum.infrastructure.database.chatroom.ChatRoomRepository;
import com.staislawwojcik.forum.infrastructure.database.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserService userService;
    public ChatRoomService(ChatRoomRepository chatRoomRepository, UserService userService) {
        this.chatRoomRepository = chatRoomRepository;
        this.userService = userService;
    }

    public ChatRoom createChatRoom(String name, String token){
        User owner = userService.getByToken(token);
        ChatRoom chatRoom = new ChatRoom(UUID.randomUUID().toString(), name, owner , new ArrayList<>());
        return chatRoomRepository.save(chatRoom);

    }

    public ChatRoomMessage sendMessage(String id, String token, String message){
        User sender = userService.getByToken(token);
        ChatRoom chatRoom = chatRoomRepository.findById(id).orElseThrow();
        ChatRoomMessage chatRoomMessage = new ChatRoomMessage(LocalDateTime.now(),sender, message);
        chatRoom.getMessages().add(chatRoomMessage);
        chatRoomRepository.save(chatRoom);
        return chatRoomMessage;
    }

    public List<ChatRoomMessage> getMessages(String id, String token){
        if(!userService.tokenExists(token)){
            throw new DomainException("User not logged in", HttpStatus.UNAUTHORIZED.value());
        }
        ChatRoom chatRoom = chatRoomRepository.findById(id).orElseThrow();
        return chatRoom.getMessages();
    }

    public List<ChatRoom> getChatRooms() {
        return chatRoomRepository.findAll();
    }
}
