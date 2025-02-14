package com.staislawwojcik.forum.domain;

import com.staislawwojcik.forum.infrastructure.database.User;
import com.staislawwojcik.forum.infrastructure.database.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.regex.Pattern;

@Service
public class UserService {

    private final UserRepository users;

    public UserService(UserRepository users) {
        this.users = users;
    }

    public User createUser(String login, String password) {
        if (!loginValidation(login)) {
            return null;
        }
        if (!passwordValidation(password)) {
            return null;
        }
        User user = new User(login, password);
        return users.save(user);
    }

    private boolean loginValidation(String login) {

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