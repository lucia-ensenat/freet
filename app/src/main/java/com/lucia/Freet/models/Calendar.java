package com.lucia.Freet.models;

import java.util.List;

public class Calendar {
    private final String nombre;
    private final String uuid;
    private final List<User> users;

    public Calendar(final String nombre, final List<User> users, final String uuid) {
        this.nombre = nombre;
        this.uuid = uuid;
        this.users = users;
    }

    public String getNombre() {
        return nombre;
    }
    public String getUUID(){
        return uuid;
    }

    public List<User> getUsuarios() {
        return users;
    }
}
