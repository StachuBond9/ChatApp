package com.staislawwojcik.forum.infrastructure.database;

import jakarta.persistence.*;

import java.util.UUID;


@Table(name = "user-session")
@Entity
public class UserSession {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String token;
    @ManyToOne
    private User loggedUser;

    public UserSession(){
    }

    public UserSession(User loggedUser) {

        this.loggedUser = loggedUser;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
    }
}
