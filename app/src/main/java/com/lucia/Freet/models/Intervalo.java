package com.lucia.Freet.models;

import java.util.Date;

public class Intervalo {
    public Date inicio;
    public Date fin;

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public Date getFin() {
        return fin;
    }

    public void setFin(Date fin) {
        this.fin = fin;
    }

    public Intervalo(Date inicio, Date fin) {
        this.inicio = inicio;
        this.fin = fin;
    }
}
