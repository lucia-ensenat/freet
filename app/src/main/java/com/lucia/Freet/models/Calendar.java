package com.lucia.Freet.models;

import java.util.List;

public class Calendar {
    private final String nombre;
    private final String uuid;



    public Calendar(final String nombre, final String uuid) {
        this.nombre = nombre;
        this.uuid = uuid;

    }

    public String getNombre() {
        return nombre;
    }
    public String getUUID(){
        return uuid;
    }


}
