package com.staislawwojcik.forum.domain;

import com.staislawwojcik.forum.infrastructure.database.user.User;
import com.staislawwojcik.forum.infrastructure.database.user.UserRepository;
import com.staislawwojcik.forum.infrastructure.database.user.UserSession;
import com.staislawwojcik.forum.infrastructure.database.user.UserSessionRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class UserService {

    private final UserRepository users;
    private final UserSessionRepository usersSessions;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository users, UserSessionRepository usersSessions, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.usersSessions = usersSessions;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(String login, String password) {
        if (!loginValidation(login)) {
            throw new DomainException("Invalid login( " + login + " )", HttpStatus.BAD_REQUEST.value());
        }
        if (!passwordValidation(password)) {
            throw new DomainException("Invalid password( " + password + " )", HttpStatus.BAD_REQUEST.value());
        }
        User user = new User(login, passwordEncoder.encode(password));
        return users.save(user);
    }

    public String loginUser(String login, String password) {
        //User user = users.findByLoginAndPassword(login, passwordEncoder.encode(password))
        //        .orElseThrow());
        User user =  users.findById(login)
                .orElseThrow(() -> new DomainException("Invalid login credentials", HttpStatus.UNAUTHORIZED.value()));
        return usersSessions.save(new UserSession(user)).getToken();

    }


    private boolean loginValidation(String login) {
        if (users.existsById(login)) {
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

    public boolean logoutUser(String token) {
        usersSessions.deleteById(token);
        return !usersSessions.existsById(token);
    }

    public User getById(String id) {
        return users.findById(id)
                .orElseThrow(() -> new DomainException("No user with that id : " + id, 400));
    }

    public User getByToken(String token){
        return usersSessions.findById(token)
                .orElseThrow(() -> new DomainException("Invalid token : " + token , 400))
                .getLoggedUser();
    }

    public List<User> usersList(){
       return users.findAll(Sort.unsorted());
    }

    public boolean tokenExists(String token){
        return usersSessions.findById(token).isPresent();
    }
}