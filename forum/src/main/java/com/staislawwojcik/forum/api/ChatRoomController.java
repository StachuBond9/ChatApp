package com.staislawwojcik.forum.api;

import com.staislawwojcik.forum.domain.ChatRoomService;
import com.staislawwojcik.forum.infrastructure.database.chatroom.ChatRoom;
import com.staislawwojcik.forum.infrastructure.database.chatroom.ChatRoomMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/chat-room")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    public ChatRoomController(ChatRoomService chatRoomService) {
        this.chatRoomService = chatRoomService;
    }

    @PostMapping("/createRoom/{name}")
    public ResponseEntity<ChatRoom> createChatRoom(@PathVariable String name, @RequestHeader String token) {
        ChatRoom chatRoom = chatRoomService.createChatRoom(name, token);
        return ResponseEntity.status(HttpStatus.CREATED).body(chatRoom);
    }

    @PostMapping("/{id}/send-message")
    public ResponseEntity<ChatRoomMessage> sendMessage(@PathVariable String id, @RequestHeader String token, @RequestBody String message) {
        ChatRoomMessage chatRoomMessage = chatRoomService.sendMessage(id, token, message);
        return ResponseEntity.status(HttpStatus.OK).body(chatRoomMessage);
    }

    @GetMapping("/{id}/getMessages")
    public ResponseEntity<List<ChatRoomMessage>> getMessages(@PathVariable String id, @RequestHeader String token) {
        List<ChatRoomMessage> messages = chatRoomService.getMessages(id, token);
        return ResponseEntity.status(HttpStatus.OK).body(messages);

    }

    @GetMapping("/get-rooms")
    public  ResponseEntity<List<ChatRoom>> getChatRooms(){
        List<ChatRoom> chatRooms = chatRoomService.getChatRooms();
        return ResponseEntity.status(HttpStatus.OK).body(chatRooms);
    }

}
