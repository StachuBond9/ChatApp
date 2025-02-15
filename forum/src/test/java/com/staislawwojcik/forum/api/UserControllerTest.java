package com.staislawwojcik.forum.api;

import com.staislawwojcik.forum.api.request.UserRequest;
import com.staislawwojcik.forum.infrastructure.database.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class UserControllerIntegrationTest {
    @Autowired
    private WebTestClient webTestClient;

    @Test
    void addUserToDatabase(){
        String login ="login1234";
        String password ="password1";

        webTestClient.post()
                .uri("/user/registration")
                .bodyValue(new UserRequest(login, password))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(User.class)
                .value(user -> {
                    Assertions.assertEquals(login, user.getLogin());
                    Assertions.assertEquals(password, user.getPassword());
                });
    }

    @Test
    void badLoginInRegistration(){
        String login ="login  1234";
        String password ="password1";

        webTestClient.post()
                .uri("/user/registration")
                .bodyValue(new UserRequest(login, password))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(User.class)
                .value(Assertions::assertNull);

        String login1 ="logiąó1234";
        webTestClient.post()
                .uri("/user/registration")
                .bodyValue(new UserRequest(login1, password))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(User.class)
                .value(Assertions::assertNull);

        String login2 ="log";
        webTestClient.post()
                .uri("/user/registration")
                .bodyValue(new UserRequest(login2, password))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(User.class)
                .value(Assertions::assertNull);
    }

    @Test
    void badPasswordInRegistration(){
        String login ="login1234";
        String password ="pass word123";

        webTestClient.post()
                .uri("/user/registration")
                .bodyValue(new UserRequest(login, password))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(User.class)
                .value(Assertions::assertNull);

        String password1 = "pass";
        webTestClient.post()
                .uri("/user/registration")
                .bodyValue(new UserRequest(login, password1))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(User.class)
                .value(Assertions::assertNull);


    }

}