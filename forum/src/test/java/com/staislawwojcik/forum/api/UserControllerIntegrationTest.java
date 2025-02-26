package com.staislawwojcik.forum.api;

import com.staislawwojcik.forum.api.request.UserRequest;
import com.staislawwojcik.forum.api.response.ErrorResponse;
import com.staislawwojcik.forum.infrastructure.database.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class UserControllerIntegrationTest {
    @Autowired
    private WebTestClient webTestClient;

    @Test
    void addUserToDatabase() {
        String login = "login1234";
        String password = "password1";

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
    void badLoginInRegistration() {
        String login = "login  1234";
        String password = "password1";

        webTestClient.post()
                .uri("/user/registration")
                .bodyValue(new UserRequest(login, password))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .value(errorResponse -> {
                    Assertions.assertTrue(errorResponse.message().contains(login));
                    Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.status());
                });

        String login1 = "logiąó1234";
        webTestClient.post()
                .uri("/user/registration")
                .bodyValue(new UserRequest(login1, password))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .value(errorResponse -> {
                    Assertions.assertTrue(errorResponse.message().contains(login1));
                    Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.status());
                });

        String login2 = "log";
        webTestClient.post()
                .uri("/user/registration")
                .bodyValue(new UserRequest(login2, password))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .value(errorResponse -> {
                    Assertions.assertTrue(errorResponse.message().contains(login2));
                    Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.status());
                });
    }

    @Test
    void badPasswordInRegistration() {
        String login = "login1234";
        String password = "pass word123";

        webTestClient.post()
                .uri("/user/registration")
                .bodyValue(new UserRequest(login, password))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .value(errorResponse -> {
                    Assertions.assertTrue(errorResponse.message().contains(password));
                    Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.status());
                });

        String password1 = "pass";
        webTestClient.post()
                .uri("/user/registration")
                .bodyValue(new UserRequest(login, password1))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .value(errorResponse -> {
                    Assertions.assertTrue(errorResponse.message().contains(password1));
                    Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.status());
                });
    }

    @Test
    void shouldLoginUser() {
        //given
        String login = "login1234";
        String password = "password1";

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

        //when then
        webTestClient.post()
                .uri("/user/login")
                .bodyValue(new UserRequest(login, password))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(Assertions::assertNotNull);

    }

    @Test
    void shouldNotLoginUser() {
        //given
        String login = "login1234";
        String password = "password1";


        //when then
        webTestClient.post()
                .uri("/user/login")
                .bodyValue(new UserRequest(login, password))
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody(ErrorResponse.class)
                .value(errorResponse -> {
                    Assertions.assertEquals("Invalid login credientals", errorResponse.message());
                    Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), errorResponse.status());
                });

    }
}