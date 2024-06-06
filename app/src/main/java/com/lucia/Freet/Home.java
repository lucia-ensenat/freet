package com.lucia.Freet;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lucia.Freet.models.Event;
import com.lucia.Freet.services.ConnectionService;
import com.lucia.Freet.ui.form.CalendarForm;
import com.lucia.Freet.ui.form.EventForm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Home extends AppCompatActivity {
    private ListView listaEventos;
    private BottomNavigationView navigationView;
    private ConnectionService connectionService;
    private List events = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        connectionService = new ConnectionService();

        listaEventos = findViewById(R.id.listaEventos);
        navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected( MenuItem item) {
                Intent intent = null;
                int itemId = item.getItemId();
                if (itemId == R.id.addEvent) {
                    intent = new Intent(getApplicationContext(), EventForm.class);
                    startActivity(intent);
                } else if (itemId == R.id.profile) {
                    intent = new Intent(getApplicationContext(), Profile.class);
                    startActivity(intent);
                }else if (itemId == R.id.addCalendar) {
                    intent = new Intent(getApplicationContext(), CalendarForm.class);
                    startActivity(intent);
                } else if (itemId == R.id.addCalendar) {
                    intent = new Intent(getApplicationContext(), CalendarForm.class);
                    startActivity(intent);
                }
                return false;
            }
        });
        EventsTask eventsTask = new EventsTask();
        eventsTask.execute();

    }

    private class AdaptadorEvento extends ArrayAdapter<Event> {
        private ArrayList<Event> events;

        public AdaptadorEvento(Context context, ArrayList<Event> events) {
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


    private class EventsTask extends AsyncTask<String, Void, Boolean> {


        @Override
        protected Boolean doInBackground(String... params) {
            final Calendar calendar = Calendar.getInstance();

            try (final Connection connection = connectionService.createConnection(); //
                 final PreparedStatement statement = connection.prepareStatement("SELECT * FROM evento WHERE YEAR(fecha) = ?  AND MONTH(fecha) = ?")) {
                statement.setInt(1, calendar.get(Calendar.YEAR));
                statement.setInt(2, calendar.get(Calendar.MONTH) + 1);
                ;
                try (final ResultSet rs = statement.executeQuery()) {
                    System.out.println("result set 1 guay");
                    while (rs.next()) {
                        final Event event = new Event(rs.getString("evento"), rs.getString("lugar"), rs.getTimestamp("fecha"),
                                rs.getTimestamp("fechaFin"));
                        System.out.println(event);
                        events.add(event);

                    }
                    System.out.println(events);
                    return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                final AdaptadorEvento adapter = new AdaptadorEvento(getApplicationContext(), (ArrayList<Event>) events);

                listaEventos.setAdapter(adapter);
                System.out.println("listo");
            } else {
                System.out.println("No se encontraron eventos.");
            }
        }
    }
}


