package com.staislawwojcik.forum.domain;

import com.staislawwojcik.forum.infrastructure.database.User;
import com.staislawwojcik.forum.infrastructure.database.UserRepository;
import com.staislawwojcik.forum.infrastructure.database.UserSession;
import com.staislawwojcik.forum.infrastructure.database.UserSessionRepository;
import jdk.jshell.Snippet;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class UserService {

    private final UserRepository users;
    private final UserSessionRepository usersSessions;

    public UserService(UserRepository users, UserSessionRepository usersSessions) {
        this.users = users;
        this.usersSessions = usersSessions;
    }

    public User createUser(String login, String password) {
        if (!loginValidation(login)) {
            throw new DomainException("Invalid login( " + login + " )", HttpStatus.BAD_REQUEST.value());
        }
        if (!passwordValidation(password)) {
            throw new DomainException("Invalid password( " + password + " )", HttpStatus.BAD_REQUEST.value());
        }
        User user = new User(login, password);
        return users.save(user);
    }

    public String loginUser(String login, String password) {
        User user = users.findByLoginAndPassword(login, password)
                .orElseThrow(() -> new DomainException("Invalid login credientals", HttpStatus.UNAUTHORIZED.value()));

        return usersSessions.save(new UserSession(user)).getToken();

    }


    private boolean loginValidation(String login) {
        if(users.existsById(login)){
            return false;
        }
        if (users.findAll().contains(new User(login, null))) {
            return false;
        }
        if (login.length() < 8) {
            return false;
        }
        return isAlphanumericWithUnderscore(login);


    }

    private boolean passwordValidation(String password) {
        if (password.length() < 9) {
            return false;
        }
        return !password.contains(" ");
    }

    private static final Pattern ALPHANUMERIC_UNDERSCORE_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+$");

    public static boolean isAlphanumericWithUnderscore(String str) {
        return ALPHANUMERIC_UNDERSCORE_PATTERN.matcher(str).matches();
    }
}