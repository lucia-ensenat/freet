package com.lucia.Freet.views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lucia.Freet.R;
import com.lucia.Freet.models.Event;

import java.util.List;

public class AdaptadorEvento extends RecyclerView.Adapter<AdaptadorEvento.ViewHolder> {
    private List<Event> events;

    public AdaptadorEvento(List<Event> events) {
        this.events = events;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Event event = events.get(position);
        holder.nombre.setText(event.getNombre());
        holder.lugar.setText(event.getLugar());
        holder.fecha.setText(event.getFechaInicio().toString());
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombre;
        TextView lugar;
        TextView fecha;


        public ViewHolder(View view) {
            super(view);
            nombre = view.findViewById(R.id.nombreText);
            lugar = view.findViewById(R.id.lugarText);
            fecha = view.findViewById(R.id.fechaText);
        }
    }
}





