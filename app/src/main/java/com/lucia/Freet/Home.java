package com.lucia.Freet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarMenu;
import com.google.android.material.navigation.NavigationView;
import com.lucia.Freet.forms.EventForm;
import com.lucia.Freet.models.Event;
import com.lucia.Freet.services.ConnectionService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Home extends AppCompatActivity {
    private ListView listaEventos;
    private BottomNavigationView navigationView;
    private  ConnectionService connectionService ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        connectionService = new ConnectionService();

        navigationView =  findViewById(R.id.bottom_navigation);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = null;
                int itemId = item.getItemId();
                if (itemId == R.id.algorithm) {
                    intent = new Intent(getApplicationContext(), Home.class);
                    startActivity(intent);
                } else if (itemId == R.id.course) {
                    intent = new Intent(getApplicationContext(), EventForm.class);
                    startActivity(intent);
                } else if (itemId == R.id.profile) {
//                    intent = new Intent(getApplicationContext(), Profile.class);
//                    startActivity(intent);
                }
                return false;
            }
        });


        final List<Event> events = new ArrayList<>();
        try {
            fillEvents( events);
            showEventsList(events);
            listaEventos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                }
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }



    }

    private void showEventsList(final List<Event> events) {
        AdaptadorEvento adapter = new AdaptadorEvento(getApplicationContext(), (ArrayList<Event>) events);
        listaEventos = findViewById(R.id.listaEventos);
        listaEventos.setAdapter(adapter);
    }

    private void fillEvents(final List<Event> events) throws SQLException {

         Date localDate = new Date();

            try (Connection connection = connectionService.createConnection(); //
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM evento WHERE YEAR(fecha) = ?  AND MONTH(fecha) = ? ")) {
                statement.setString(1, "2024");
                statement.setString(2, "05" +
                        "");

                ResultSet rs = statement.executeQuery();
                while (rs.next()) {
                    Event event = new Event(rs.getString("nombre"), rs.getString("lugar"), rs.getTimestamp("fecha"),
                            rs.getTimestamp("fechaFin"));
                    events.add(event);

                }
            }

    }


    private class AdaptadorEvento extends ArrayAdapter<Event>
    {
        private ArrayList<Event> events;

        public AdaptadorEvento(Context context, ArrayList<Event> events)
        {
            super(context, R.layout.layout_event, events);
            this.events = events;
        }

        //sobreescribimos getView para que, al buscar una vista, nos devuelva la personalizada
        public View getView(int position, View convertView, ViewGroup parent)
        {
            //obtenemos el contexto a través del viewgroup parent
            Context contexto = parent.getContext();
            //definimos el hinchador (layoutinflater)
            LayoutInflater hinchador = LayoutInflater.from(contexto);
            //infla una nueva jerarquía de vistas desde el recurso xml
            View filacolor = hinchador.inflate(R.layout.layout_event, parent, false);
            //ponemos cada dato en su sitio correspondiente
            TextView nombre = filacolor.findViewById(R.id.nombreText);
            nombre.setText(events.get(position).getNombre());
            TextView hexadecimal = filacolor.findViewById(R.id.lugarText);
            hexadecimal.setText(events.get(position).getNombre());
            TextView rgb = filacolor.findViewById(R.id.fechaText);
            rgb.setText(events.get(position).getFechaInicio().toString());

            //devolvemos la vista personalizada
            return filacolor;
        }
    }

}
