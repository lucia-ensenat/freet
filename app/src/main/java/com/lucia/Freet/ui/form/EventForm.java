package com.lucia.Freet.ui.form;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.lucia.Freet.Home;
import com.lucia.Freet.Profile;
import com.lucia.Freet.R;
import com.lucia.Freet.extractor.CalendarsExtractor;
import com.lucia.Freet.models.Calendar;
import com.lucia.Freet.models.Intervalo;
import com.lucia.Freet.services.ConnectionService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventForm extends AppCompatActivity {
    private EditText eventName;
    private EditText eventPlace;
    private static Spinner calendars;
    private TextView eventDate;
    private TextView recomendadoText;
    private CalendarsExtractor calendarExtractor;
    private BottomNavigationView navigationView;
    private ConnectionService connectionService;
    static int startHours;
    static int startMinutes;
    static int endHours;
    static int endMinutes;
    static Date startDate;
    static Date endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        calendarExtractor = new CalendarsExtractor();
        connectionService = new ConnectionService();

        setContentView(R.layout.activity_formulario_evento);

        eventName = findViewById(R.id.nameText);
        eventPlace = findViewById(R.id.placeText);
        eventDate = findViewById(R.id.dateText);
        calendars = findViewById(R.id.spinner);
        recomendadoText = findViewById(R.id.recomendadoText);

        final Button buttonDate = findViewById(R.id.buttonDate);
        final Button buttonTime = findViewById(R.id.buttonTime);
        final Button buttonSave = findViewById(R.id.buttonSave);
        final Button RecomandedButton = findViewById(R.id.buttonRecomendarFecha);


        navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setSelectedItemId(R.id.addEvent);
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
                } else if (itemId == R.id.profile) {
                    intent = new Intent(getApplicationContext(), Profile.class);
                    startActivity(intent);
                } else if (itemId == R.id.addCalendar) {
                    intent = new Intent(getApplicationContext(), CalendarForm.class);
                    startActivity(intent);
                }
                return false;
            }
        });
        final List<Calendar> calendarList = new ArrayList<>();

        ObtainCalendarTask obtainCalendarTask = new ObtainCalendarTask(calendarList);
        obtainCalendarTask.execute();


        MaterialDatePicker<Pair<Long, Long>> pickerRange = MaterialDatePicker.Builder.dateRangePicker()//
                .setTitleText("SELECT A DATE")//
                .setCalendarConstraints(//
                        new CalendarConstraints.Builder().setValidator(DateValidatorPointForward.now()).build()//
                ).build();

        MaterialTimePicker pickerTime =
                new MaterialTimePicker.Builder()
                        .setTitleText("SELECT TIME")
                        .setTimeFormat(TimeFormat.CLOCK_24H).build();

        buttonDate.setOnClickListener(v -> {
            pickerRange.show(getSupportFragmentManager(), "Material Date Picker");

            pickerRange.addOnPositiveButtonClickListener(selection -> {
                startDate = new Date(selection.first);
                endDate = new Date(selection.second);


            });

            pickerRange.addOnCancelListener(dialog -> {
                pickerRange.dismiss();
            });


        });


        buttonTime.setOnClickListener(v -> {
            pickerTime.show(getSupportFragmentManager(), "Select Date");

            pickerTime.addOnPositiveButtonClickListener(dialog -> {
                startHours = pickerTime.getHour();
                startMinutes = pickerTime.getMinute();


            });

        });
        RecomandedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchDateTask searchDateTask = new SearchDateTask((Calendar) EventForm.calendars.getSelectedItem());
                searchDateTask.execute();
            }
        });

        buttonSave.setOnClickListener(v -> {
            final java.util.Calendar startTimeStamp;
            final java.util.Calendar endTimeStamp;

            if (startDate != null && endDate != null) {
                startTimeStamp = java.util.Calendar.getInstance();
                startTimeStamp.setTime(startDate);
                startTimeStamp.set(java.util.Calendar.HOUR_OF_DAY, startHours);
                startTimeStamp.set(java.util.Calendar.MINUTE, startMinutes);

                endTimeStamp = java.util.Calendar.getInstance();
                startTimeStamp.setTime(endDate);
                startTimeStamp.set(java.util.Calendar.HOUR_OF_DAY, startHours);
                startTimeStamp.set(java.util.Calendar.MINUTE, startMinutes);
            } else {
                endTimeStamp = null;
                startTimeStamp = null;
            }

            final Calendar selectedCalendar = (Calendar) calendars.getSelectedItem();

            final InsertEventTask insertEventTask = new InsertEventTask(selectedCalendar, startTimeStamp, endTimeStamp);
            insertEventTask.execute();

        });
    }

    private void updateCalendarsSpinner(List<Calendar> calendarList) {
        if (calendarList.isEmpty()) {
            alertNoCalendars();
        } else {
            ArrayAdapter<Calendar> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, calendarList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            calendars.setAdapter(adapter);
        }
    }

    private void alertNoCalendars() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EventForm.this);
        builder.setTitle("Ups!!");
        builder.setMessage("Parece que no tienes ningun calendario. Antes de añadir un evento crea un calendario");
        builder.setCancelable(false);

        builder.setPositiveButton("CREAR AHORA", (DialogInterface.OnClickListener) (dialog, which) -> {
            Intent intent = new Intent(this, CalendarForm.class);
            startActivity(intent);
            finish();
        });

        builder.setNegativeButton("MAS TARDE", (DialogInterface.OnClickListener) (dialog, which) -> {
            finish();
        });

        AlertDialog alertDialog = builder.create();
        // Show the Alert Dialog box
        alertDialog.show();
    }


    private class InsertEventTask extends AsyncTask<String, Void, Boolean> {
        private Calendar calendar;
        private java.util.Calendar startTime;
        private java.util.Calendar endTime;

        public InsertEventTask(Calendar calendar, java.util.Calendar startTime, java.util.Calendar endTime) {

            this.calendar = calendar;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        @Override
        protected Boolean doInBackground(String... params) {

            try (final Connection connection = connectionService.createConnection()) {

                try (final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `evento`(`fecha`, `fechaFin`, `evento`, `lugar`, `idCalendario`) VALUES (?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);//
                     final PreparedStatement obtainCalendarUsers = connection.prepareStatement("SELECT nickname FROM `tiene` WHERE `idCalendario` = ? ");//
                     final PreparedStatement insertToCalendar = connection.prepareStatement("INSERT INTO asiste (`idEvento`,`nickname`) VALUES (?,?)")) {

                    java.sql.Date sqlStartDate = new java.sql.Date(startTime.getTimeInMillis());
                    java.sql.Date sqlEndDate = new java.sql.Date(endTime.getTimeInMillis());

                    preparedStatement.setDate(1, sqlStartDate);
                    preparedStatement.setDate(2, sqlEndDate);
                    preparedStatement.setString(3, eventName.getText().toString());
                    preparedStatement.setString(4, eventPlace.getText().toString());
                    preparedStatement.setString(5, calendar.getUUID());
                    preparedStatement.executeUpdate();

                    ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int eventoId = generatedKeys.getInt(1);


                        obtainCalendarUsers.setString(1, calendar.getUUID());
                        try (final ResultSet rsCalendar = obtainCalendarUsers.executeQuery()) {
                            while (rsCalendar.next()) {
                                insertToCalendar.setInt(1, eventoId);
                                insertToCalendar.setString(2, rsCalendar.getString("nickname"));
                                insertToCalendar.executeUpdate();

                            }

                        }

                    }
                }

                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean success) {
            Toast toast;
            if (success) {
                toast = Toast.makeText(EventForm.this, "Se ha creado el evento", Toast.LENGTH_SHORT);

            } else {
                toast = Toast.makeText(EventForm.this, "No se ha podido crear", Toast.LENGTH_SHORT);
            }
            toast.show();
        }
    }

    private class SearchDateTask extends AsyncTask<String, Void, Boolean> {
        private Calendar selectedCalendar;
        List<Intervalo> intervalos = new ArrayList<>();

        public SearchDateTask(Calendar selectedCalendar) {
            this.selectedCalendar = selectedCalendar;
        }

        private void encontrarHueco() {


            java.util.Calendar fechaActual = java.util.Calendar.getInstance();
            fechaActual.set(java.util.Calendar.DATE, fechaActual.get(java.util.Calendar.DAY_OF_MONTH) + 1);

            java.util.Calendar fechaLibre = null;

            // Itera a través de los días posteriores al día actual
            while (fechaLibre == null) {
                boolean libre = false;
                for (Intervalo horario : intervalos) {
                    if (horario.getInicio().equals(fechaActual) || horario.getInicio().equals(fechaActual)) {
                        libre = false;
                    } else {
                        libre = true;
                        fechaLibre = fechaActual;
                        break;
                    }
                }

                if (libre) {
                    recomendadoText.setText(fechaLibre.getTime().toString());
                } else {

                    fechaActual.set(java.util.Calendar.DATE, fechaActual.get(java.util.Calendar.DAY_OF_MONTH) + 1);
                }
            }

        }

        @Override
        protected Boolean doInBackground(String... params) {


            try (final Connection connection = connectionService.createConnection()) {
                try (final PreparedStatement statement = connection.prepareStatement("SELECT fecha, fechaFin FROM `evento` WHERE idCalendario IN ( SELECT idCalendario FROM `tiene` WHERE nickname IN ( SELECT nickname FROM `tiene` WHERE idCalendario = ? ) )")) {
                    System.out.println(selectedCalendar.getUUID());
                    statement.setString(1, selectedCalendar.getUUID());

                    try (ResultSet rs = statement.executeQuery()) {
                        while (rs.next()) {
                            intervalos.add(new Intervalo(rs.getDate("fecha"), rs.getDate("fechaFin")));
                        }
                    }

                    return true;


                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                encontrarHueco();
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

            calendars.addAll(calendarExtractor.extractUserCalendars(getApplicationContext()));

            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            updateCalendarsSpinner(calendars);

        }
    }
}
