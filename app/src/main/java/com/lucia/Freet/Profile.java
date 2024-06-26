package com.lucia.Freet;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lucia.Freet.extractor.CalendarsExtractor;
import com.lucia.Freet.models.AdaptadorEvento;
import com.lucia.Freet.models.Calendar;
import com.lucia.Freet.models.Event;
import com.lucia.Freet.services.ConnectionService;
import com.lucia.Freet.ui.form.CalendarForm;
import com.lucia.Freet.ui.form.EventForm;
import com.lucia.Freet.utils.SharedPreferencesUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Profile extends AppCompatActivity {

    private BottomNavigationView navigationView;
    private TextView emailText;
    private TextView nicknameText;
    private Button passwordButton;
    private ConnectionService connectionService;
    private CalendarsExtractor calendarsExtractor;
    private Button historicEventsButton;
    private Button calendarEventsButton;
    private ListView listaEventos;
    private String email;
    private String nickname;
    private List events = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        connectionService = new ConnectionService();
        calendarsExtractor = new CalendarsExtractor();

        navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setSelectedItemId(R.id.profile);

        listaEventos = findViewById(R.id.eventsListView);
        emailText = findViewById(R.id.emailText);
        nicknameText = findViewById(R.id.nicknameText);
        passwordButton = findViewById(R.id.passwordButton);
        historicEventsButton = findViewById(R.id.buttonHistoric);
        calendarEventsButton = findViewById(R.id.buttonCalendar);
        List<Calendar> calendars = new ArrayList<>();


        email = SharedPreferencesUtils.get("email", getApplicationContext());
        nickname = SharedPreferencesUtils.get("nickname", getApplicationContext());

        emailText.setText(email);
        nicknameText.setText(nickname);
        passwordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
                builder.setTitle("Cambiar Contraseña");

                // Agrega un EditText al diálogo
                final EditText newPasswordEditText = new EditText(Profile.this);
                newPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                builder.setView(newPasswordEditText);

                builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Aquí obtienes la nueva contraseña ingresada por el usuario
                        String newPassword = newPasswordEditText.getText().toString();
                        PasswordTask passwordTask = new PasswordTask(newPassword);
                        passwordTask.execute();
                    }
                });

                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.show();
            }

        });
        historicEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExtractAllEvents extractAllEvents = new ExtractAllEvents();
                extractAllEvents.execute();

            }
        });

        calendarEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ObtainCalendarTask obtainCalendarTask = new ObtainCalendarTask(calendars);
                obtainCalendarTask.execute();


            }
        });
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = null;
                int itemId = item.getItemId();
                if (itemId == R.id.home) {
                    intent = new Intent(getApplicationContext(), Home.class);
                    startActivity(intent);
                } else if (itemId == R.id.addEvent) {
                    intent = new Intent(getApplicationContext(), EventForm.class);
                    startActivity(intent);
                } else if (itemId == R.id.addCalendar) {
                    intent = new Intent(getApplicationContext(), CalendarForm.class);
                    startActivity(intent);
                }
                return false;
            }
        });
    }

    private class PasswordTask extends AsyncTask<String, Void, Boolean> {
        public PasswordTask(String password) {
            this.password = password;
        }

        private String password;

        @Override
        protected Boolean doInBackground(String... params) {

            try (final Connection connection = connectionService.createConnection()) {
                try (final PreparedStatement statement = connection.prepareStatement("UPDATE `usuarios` SET `password`=? WHERE `nickname`=?")) {
                    statement.setString(1, password);
                    statement.setString(2, nickname);
                    int update = statement.executeUpdate();
                    if (update > 0) {
                        return true;
                    }

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast toast = Toast.makeText(Profile.this, "Se ha actualizado la contraseña", Toast.LENGTH_SHORT);
                toast.show();

            } else {
                Toast toast = Toast.makeText(Profile.this, "No se ha podido cambiar", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    private class ExtractCalendarEvents extends AsyncTask<String, Void, Boolean> {
        private String uuid;

        public ExtractCalendarEvents(Calendar calendar) {
            this.uuid = calendar.getUUID();
        }

        @Override
        protected Boolean doInBackground(String... params) {

            try (final Connection connection = connectionService.createConnection()) {
                try (final PreparedStatement statement = connection.prepareStatement("SELECT * from evento where calendario = ?")) {
                    statement.setString(1, uuid);
                    try (ResultSet rs = statement.executeQuery()) {
                        while (rs.next()) {
                            final Event event = new Event(rs.getString("evento"), rs.getString("lugar"), rs.getTimestamp("fecha"),
                                    rs.getTimestamp("fechaFin"));

                            events.add(event);

                        }
                        return true;
                    }

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                final AdaptadorEvento adapter = new AdaptadorEvento(getApplicationContext(), (ArrayList<Event>) events);

                listaEventos.setAdapter(adapter);

            } else {
                Toast toast = Toast.makeText(Profile.this, "No hay ningun evento", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    private class ExtractAllEvents extends AsyncTask<String, Void, Boolean> {


        @Override
        protected Boolean doInBackground(String... params) {

            try (final Connection connection = connectionService.createConnection()) {
                try (final PreparedStatement statement = connection.prepareStatement("SELECT * from evento where idEvento IN (SELECT idEvento FROM asiste WHERE nickname = ? )")) {
                    statement.setString(1, SharedPreferencesUtils.get(nickname, Profile.this));
                    try (ResultSet rs = statement.executeQuery()) {
                        while (rs.next()) {
                            final Event event = new Event(rs.getString("evento"), rs.getString("lugar"), rs.getTimestamp("fecha"),
                                    rs.getTimestamp("fechaFin"));

                            events.add(event);

                        }
                        return true;
                    }

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                final AdaptadorEvento adapter = new AdaptadorEvento(getApplicationContext(), (ArrayList<Event>) events);

                listaEventos.setAdapter(adapter);

            } else {
                Toast toast = Toast.makeText(Profile.this, "No hay ningun evento", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    private class ObtainCalendarTask extends AsyncTask<String, Void, Boolean> {
        private List<Calendar> calendars;


        public ObtainCalendarTask(List<Calendar> calendars) {

            this.calendars = calendars;

        }

        @Override
        protected Boolean doInBackground(String... params) {

            calendars.addAll(calendarsExtractor.extractUserCalendars(getApplicationContext()));

            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
            builder.setTitle("Elige un calendario");

            final ArrayAdapter<Calendar> adapter = new ArrayAdapter<>(Profile.this,
                    android.R.layout.simple_spinner_item, calendars);
            final Spinner spinner = new Spinner(Profile.this);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            builder.setView(spinner);

            builder.setPositiveButton("Mostrar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ExtractCalendarEvents extractCalendarEvents = new ExtractCalendarEvents((Calendar) spinner.getSelectedItem());
                    extractCalendarEvents.execute();
                }
            });

            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.show();


        }
    }
}