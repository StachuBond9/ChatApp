package com.staislawwojcik.forum.api;

import com.staislawwojcik.forum.api.request.UserRequest;
import com.staislawwojcik.forum.domain.UserService;
import com.staislawwojcik.forum.infrastructure.database.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/registration")
    public ResponseEntity<User> userRegistration(@RequestBody UserRequest userRequest) {
        User user= userService.createUser(userRequest.login(), userRequest.password());
        if(user == null){
            return ResponseEntity.status((HttpStatus.BAD_REQUEST)).body(null);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

}