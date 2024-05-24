package com.lucia.Freet.models;

import java.util.Date;

public class Event {
    private final String nombre;
    private final String lugar;
    private final Date fechaInicio;
    private final Date fechaFin;

    public Event(final String nombre, final String lugar, final Date fechaInicio, final Date fechaFin ){
        this.nombre = nombre;
        this.lugar = lugar;
        this.fechaInicio= fechaInicio;
        this.fechaFin = fechaFin;
    }

    public String getNombre() {
        return nombre;
    }

    public String getLugar() {
        return lugar;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }
}
