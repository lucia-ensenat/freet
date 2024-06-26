package com.lucia.Freet.models;

public class User {
    private final String email;
    private final String nickname;
    private final String password;

    public User(final String email, final String nickname, final String password) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPassword() {
        return password;
    }
}
