package com.lucia.Freet.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.lucia.Freet.R;

import java.util.ArrayList;

public class AdaptadorEvento extends ArrayAdapter<Event> {
    private ArrayList<Event> events;

    public AdaptadorEvento(Context context, ArrayList<Event> events)  {
        super(context, R.layout.layout_event, events);
        this.events = events;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        final Context contexto = parent.getContext();
        final LayoutInflater hinchador = LayoutInflater.from(contexto);
        View filacolor = hinchador.inflate(R.layout.layout_event, parent, false);

        final TextView nombre = filacolor.findViewById(R.id.nombreText);
        nombre.setText(events.get(position).getNombre());

        final TextView hexadecimal = filacolor.findViewById(R.id.lugarText);
        hexadecimal.setText(events.get(position).getNombre());

        final TextView rgb = filacolor.findViewById(R.id.fechaText);
        rgb.setText(events.get(position).getFechaInicio().toString());

        //devolvemos la vista personalizada
        return filacolor;
    }
}
