package com.staislawwojcik.forum.api;

import com.staislawwojcik.forum.api.request.PMRequest;
import com.staislawwojcik.forum.api.request.UserRequest;
import com.staislawwojcik.forum.domain.UserService;
import com.staislawwojcik.forum.infrastructure.database.pm.PrivateMessage;
import com.staislawwojcik.forum.infrastructure.database.user.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class PrivateMessageControllerTest {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private UserService userService;

    @Test
    void getMessagesFromConversationsTest() {
        //given
        String login1 = "login123";
        String login2 = "login234";
        String login3 = "login345";

        String password = "password1123333";

        String msg1 ="msg1";
        String msg2 ="msg2";
        String msg3 ="msg3";
        String msg4 ="msg4";

        //when

        User user1 = createUser(login1, password);
        User user2 = createUser(login2, password);
        User user3 = createUser(login3, password);

        String tokenUser1 = login(user1);
        String tokenUser2 = login(user2);

        PrivateMessage pm1 = createPM(tokenUser1, user2.getLogin(), msg1);
        PrivateMessage pm2 = createPM(tokenUser1, user2.getLogin(), msg2);
        PrivateMessage pm3 = createPM(tokenUser2, user1.getLogin(), msg4);
        PrivateMessage pm4 = createPM(tokenUser2, user3.getLogin(), msg3);


        List<PrivateMessage> user1_2 = getConversationByUsers(user2.getLogin(), tokenUser1);
        List<PrivateMessage> user2_1 = getConversationByUsers(user1.getLogin(), tokenUser2);
        List<PrivateMessage> user2_3 = getConversationByUsers(user3.getLogin(), tokenUser2);

        //then
        Assertions.assertEquals(user1_2.getFirst(), user2_1.getFirst());
        Assertions.assertEquals(pm1 , user2_1.getFirst());
        Assertions.assertEquals(user1_2.get(1), user2_1.get(1));
        Assertions.assertEquals(pm2 , user2_1.get(1));
        Assertions.assertEquals(user1_2.get(2), user2_1.get(2));
        Assertions.assertEquals(pm3, user2_1.get(2));
        Assertions.assertEquals(pm4, user2_3.getFirst());
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

    private PrivateMessage createPM(String senderToken, String receiverId, String message) {
        User sender = userService.getByToken(senderToken);
        User receiver = userService.getById(receiverId);
        return webTestClient.post()
                .uri("/pm/{receiverId}", receiverId)
                .header("token", senderToken)
                .bodyValue(new PMRequest(message))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(PrivateMessage.class)
                .value(privateMessage -> {
                    Assertions.assertEquals(receiver, privateMessage.getReceiver());
                    Assertions.assertEquals(sender, privateMessage.getSender());
                    Assertions.assertEquals(message, privateMessage.getMessage());
                    //Assertions.assertFalse(privateMessage.getSendAt().isBefore(LocalDateTime.now()));
                }).returnResult()
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

    private List<PrivateMessage> getConversationByUsers(String receiverId, String ownerToken) {
        return webTestClient.get()
                .uri("/pm/conversation/{receiverId}" , receiverId)
                .header("token", ownerToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<PrivateMessage>>() {
                }).returnResult()
                .getResponseBody();
    }


}