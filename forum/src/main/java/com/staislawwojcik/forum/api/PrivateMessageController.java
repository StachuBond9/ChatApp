package com.staislawwojcik.forum.api;

import com.staislawwojcik.forum.api.request.PMRequest;
import com.staislawwojcik.forum.domain.PrivateMessageService;
import com.staislawwojcik.forum.infrastructure.database.PrivateMessage;
import com.staislawwojcik.forum.infrastructure.database.User;
import io.swagger.v3.oas.annotations.headers.Header;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/pm")
public class PrivateMessageController {

    private final PrivateMessageService privateMessageService;

    public PrivateMessageController(PrivateMessageService privateMessageService) {
        this.privateMessageService = privateMessageService;
    }

    @PostMapping(value = "/{receiverId}")
    ResponseEntity<PrivateMessage> sendPM(@PathVariable String receiverId, @RequestHeader("token") String senderToken, @RequestBody PMRequest pmRequest) {
        PrivateMessage pm = privateMessageService.createPM(receiverId, senderToken, pmRequest.message());
        return ResponseEntity.status(HttpStatus.CREATED).body(pm);
    }

    @GetMapping(value ="/conversation/{receiverId}" )
    ResponseEntity<List<PrivateMessage>> getMessagesFromConversation(@PathVariable String receiverID, @RequestHeader("token") String ownerToken ){
        List<PrivateMessage> pms = privateMessageService.getMessagesFromConversation(receiverID, ownerToken);
        return ResponseEntity.status(HttpStatus.OK).body(pms);
    }
    @GetMapping(value = "/users")
    ResponseEntity<List<User>> getUsers(@RequestHeader("token") String ownerToken){
        List<User> users = privateMessageService.getUsers(ownerToken);
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }
}
