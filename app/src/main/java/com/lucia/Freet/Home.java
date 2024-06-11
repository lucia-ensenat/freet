package com.lucia.Freet;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Home extends AppCompatActivity {
    private ListView listaEventos;
    private ConnectionService connectionService;
    private final List events = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        connectionService = new ConnectionService();

        listaEventos = findViewById(R.id.listaEventos);
        BottomNavigationView navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setOnNavigationItemSelectedListener(item -> {
            Intent intent = null;
            int itemId = item.getItemId();
            if (itemId == R.id.addEvent) {
                intent = new Intent(getApplicationContext(), EventForm.class);
                startActivity(intent);
            } else if (itemId == R.id.profile) {
                intent = new Intent(getApplicationContext(), Profile.class);
                startActivity(intent);
            } else if (itemId == R.id.addCalendar) {
                intent = new Intent(getApplicationContext(), CalendarForm.class);
                startActivity(intent);
            } else if (itemId == R.id.addCalendar) {
                intent = new Intent(getApplicationContext(), CalendarForm.class);
                startActivity(intent);
            }
            return false;
        });

        listaEventos.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/mm/dd");
                Event event = (Event) parent.getItemAtPosition(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                builder.setTitle("Modifica/Elimina el evento");

                LinearLayout layout = new LinearLayout(Home.this);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText newName = new EditText(Home.this);
                newName.setInputType(InputType.TYPE_CLASS_TEXT);
                newName.setText(event.getNombre());
                layout.addView(newName);

                final EditText newDate1 = new EditText(Home.this);
                newDate1.setText(simpleDateFormat.format(event.getFechaInicio()));
                newDate1.setInputType(InputType.TYPE_DATETIME_VARIATION_NORMAL);
                layout.addView(newDate1);

                final EditText newDate2 = new EditText(Home.this);
                newDate2.setInputType(InputType.TYPE_DATETIME_VARIATION_NORMAL);
                newDate2.setText(simpleDateFormat.format(event.getFechaFin()));
                layout.addView(newDate2);

                builder.setView(layout);

                builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Aquí obtienes la nueva contraseña ingresada por el usuario


                        event.setNombre(newName.getText().toString());
                        try {
                            event.setFechaInicio(simpleDateFormat.parse(newDate1.getText().toString()));
                            event.setFechaFin(simpleDateFormat.parse(newDate2.getText().toString()));
                        } catch (ParseException e) {
                            //todo
                        }
                        Home.UpdateTask updateTask = new UpdateTask(event);
                        updateTask.execute();


                    }
                });


                builder.setNegativeButton("ELIMINAR", (dialog, which) -> {
                    DeleteTask deleteTask = new DeleteTask(event, position);
                    deleteTask.execute();
                    dialog.dismiss();
                });

                builder.show();

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
            hexadecimal.setText(events.get(position).getLugar());

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
                    while (rs.next()) {
                        final Event event = new Event(rs.getString("evento"), rs.getInt("idEvento"), rs.getString("lugar"), rs.getTimestamp("fecha"),
                                rs.getTimestamp("fechaFin"));
                        events.add(event);

                    }
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

            } else {
                System.out.println("No se encontraron eventos.");
            }
        }
    }

    private class DeleteTask extends AsyncTask<String, Void, Boolean> {
        public DeleteTask(Event event, int position) {
            this.event = event;
            this.position = position;
        }

        private Event event;
        private int position;

        @Override
        protected Boolean doInBackground(String... params) {

            try (final Connection connection = connectionService.createConnection(); //
                 final PreparedStatement statement1 = connection.prepareStatement("DELETE FROM `asiste` WHERE `idEvento` = ?");//) {
                 final PreparedStatement statement2 = connection.prepareStatement("DELETE FROM `evento` WHERE `idEvento` = ?")) {
                statement1.setInt(1, event.getId());
                statement2.setInt(1, event.getId());

                if (statement1.executeUpdate() != 0) {
                    if (statement2.executeUpdate() != 0) {
                        return true;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(Home.this, "Se ha eliminado", Toast.LENGTH_SHORT).show();
                events.remove(position);
                final AdaptadorEvento adapter = new AdaptadorEvento(getApplicationContext(), (ArrayList<Event>) events);
                listaEventos.setAdapter(adapter);
            } else {
                Toast.makeText(Home.this, "No se ha eliminado", Toast.LENGTH_SHORT).show();
                System.out.println("No se ha eliminado.");
            }
        }
    }

    private class UpdateTask extends AsyncTask<String, Void, Boolean> {
        public UpdateTask(Event event) {
            this.event = event;

        }

        private Event event;

        @Override
        protected Boolean doInBackground(String... params) {


            try (final Connection connection = connectionService.createConnection(); //
                 final PreparedStatement statement = connection.prepareStatement("UPDATE `evento` SET `fecha`=?,`fechaFin`=?,`evento`=? WHERE`idEvento`=? ")) {

                final java.sql.Date sqlStartDate = new java.sql.Date(event.getFechaFin().getTime());
                final java.sql.Date sqlEndDate = new java.sql.Date(event.getFechaFin().getTime());

                statement.setDate(1, sqlStartDate);
                statement.setDate(2, sqlEndDate);
                statement.setString(3, event.getNombre());
                statement.setInt(4, event.getId());

                if (statement.executeUpdate() != 0) {
                    return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(Home.this, "Se ha modificado", Toast.LENGTH_SHORT).show();

                final AdaptadorEvento adapter = new AdaptadorEvento(getApplicationContext(), (ArrayList<Event>) events);
                listaEventos.setAdapter(adapter);

            } else {
                System.out.println("No se encontraron eventos.");
            }
        }
    }
}


