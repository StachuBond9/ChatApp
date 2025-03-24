package com.staislawwojcik.forum.api;

import com.staislawwojcik.forum.api.request.UserRequest;
import com.staislawwojcik.forum.domain.UserService;
import com.staislawwojcik.forum.infrastructure.database.chatroom.ChatRoom;
import com.staislawwojcik.forum.infrastructure.database.chatroom.ChatRoomMessage;
import com.staislawwojcik.forum.infrastructure.database.user.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class ChatRoomControllerTest {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private UserService userService;



    @Test
    void chatRooms(){
        //given
        String login1 = "login123";
        String login2 = "login234";
        String login3 = "login345";

        String password = "password1123333";

        String msg1 ="msg1";
        String msg2 ="msg2";
        String msg3 ="msg3";

        String groupName1 = "group1";
        String groupName2 = "group2";
        //when

        User user1 = createUser(login1, password);
        User user2 = createUser(login2, password);
        User user3 = createUser(login3, password);

        String tokenUser1 = login(user1);
        String tokenUser2 = login(user2);
        String tokenUser3 = login(user3);

        ChatRoom chatRoom1 = createChatRoom(groupName1, tokenUser1);
        ChatRoom chatRoom2 = createChatRoom(groupName2, tokenUser2);

        ChatRoomMessage rm1 = sendMessageToGroupChat(chatRoom1.getId(), tokenUser1, msg1);
        ChatRoomMessage rm2 = sendMessageToGroupChat(chatRoom1.getId(), tokenUser2, msg2);
        ChatRoomMessage rm3 = sendMessageToGroupChat(chatRoom1.getId(), tokenUser3, msg3);
        ChatRoomMessage rm4 = sendMessageToGroupChat(chatRoom2.getId(), tokenUser1, msg1);
        ChatRoomMessage rm5 = sendMessageToGroupChat(chatRoom2.getId(), tokenUser2, msg2);
        ChatRoomMessage rm6 = sendMessageToGroupChat(chatRoom2.getId(), tokenUser3, msg3);

        List<ChatRoomMessage> chatRoomMessages1_1 = getMessagesFromGroupChat(chatRoom1.getId(), tokenUser1);
        List<ChatRoomMessage> chatRoomMessages1_2 = getMessagesFromGroupChat(chatRoom1.getId(), tokenUser2);
        List<ChatRoomMessage> chatRoomMessages1_3 = getMessagesFromGroupChat(chatRoom1.getId(), tokenUser3);

        List<ChatRoomMessage> chatRoomMessages2_1 = getMessagesFromGroupChat(chatRoom2.getId(), tokenUser1);
        List<ChatRoomMessage> chatRoomMessages2_2 = getMessagesFromGroupChat(chatRoom2.getId(), tokenUser2);
        List<ChatRoomMessage> chatRoomMessages2_3 = getMessagesFromGroupChat(chatRoom2.getId(), tokenUser3);

        //then
        Assertions.assertEquals(rm1, chatRoomMessages1_1.get(0));
        Assertions.assertEquals(rm2, chatRoomMessages1_1.get(1));
        Assertions.assertEquals(rm3, chatRoomMessages1_1.get(2));

        Assertions.assertEquals(rm1, chatRoomMessages1_2.get(0));
        Assertions.assertEquals(rm2, chatRoomMessages1_2.get(1));
        Assertions.assertEquals(rm3, chatRoomMessages1_2.get(2));

        Assertions.assertEquals(rm1, chatRoomMessages1_3.get(0));
        Assertions.assertEquals(rm2, chatRoomMessages1_3.get(1));
        Assertions.assertEquals(rm3, chatRoomMessages1_3.get(2));

        Assertions.assertEquals(rm4, chatRoomMessages2_1.get(0));
        Assertions.assertEquals(rm5, chatRoomMessages2_1.get(1));
        Assertions.assertEquals(rm6, chatRoomMessages2_1.get(2));

        Assertions.assertEquals(rm4, chatRoomMessages2_2.get(0));
        Assertions.assertEquals(rm5, chatRoomMessages2_2.get(1));
        Assertions.assertEquals(rm6, chatRoomMessages2_2.get(2));

        Assertions.assertEquals(rm4, chatRoomMessages2_3.get(0));
        Assertions.assertEquals(rm5, chatRoomMessages2_3.get(1));
        Assertions.assertEquals(rm6, chatRoomMessages2_3.get(2));
    }

    private User createUser(String login, String password) {
        return webTestClient.post()
                .uri("/user/registration")
                .bodyValue(new UserRequest(login, password))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(User.class)
                .value(user -> {
                    Assertions.assertEquals(login, user.getLogin());
                    Assertions.assertEquals(password, user.getPassword());
                })
                .returnResult()
                .getResponseBody();
    }

    private String login(User user) {
        return webTestClient.post()
                .uri("/user/login")
                .bodyValue(new UserRequest(user.getLogin(), user.getPassword()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(Assertions::assertNotNull)
                .returnResult()
                .getResponseBody();

    }


    private ChatRoom createChatRoom(String name, String token){
        return webTestClient.post()
                .uri("/chat-room/createRoom/{name}", name)
                .header("token", token)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ChatRoom.class)
                .value(chatRoom -> {
                    Assertions.assertEquals(name, chatRoom.getName());

                })
                .returnResult()
                .getResponseBody();
    }

    private ChatRoomMessage sendMessageToGroupChat(String id, String token, String message ){
        User user = userService.getByToken(token);

        return  webTestClient.post()
                .uri("/chat-room/{id}/send-message", id)
                .bodyValue(message)
                .header("token", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ChatRoomMessage.class)
                .value(chatRoomMessage -> {
                    Assertions.assertEquals(message, chatRoomMessage.getMessage());
                    Assertions.assertEquals(user, chatRoomMessage.getSender());
                })
                .returnResult()
                .getResponseBody();

    }

    private List<ChatRoomMessage> getMessagesFromGroupChat(String id, String token){
        return  webTestClient.get()
                .uri("/chat-room/{id}/getMessages", id)
                .header("token", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<ChatRoomMessage>>() {
                })
                .returnResult()
                .getResponseBody();
    }

    private List<ChatRoom> getChatRooms(){
        return webTestClient.get()
                .uri("chat-room/get-rooms")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<ChatRoom>>() {
                })
                .returnResult()
                .getResponseBody();
    }



}