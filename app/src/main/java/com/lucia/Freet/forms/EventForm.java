package com.lucia.Freet.forms;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

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
import com.lucia.Freet.SharedPreferencesUtils;
import com.lucia.Freet.extractor.CalendarsExtractor;
import com.lucia.Freet.models.Calendar;
import com.lucia.Freet.services.ConnectionService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class EventForm extends AppCompatActivity {
    private EditText eventName;
    private EditText eventPlace;
    private Spinner calendars;
    private TextView eventDate;
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

        Button buttonDate = findViewById(R.id.buttonDate);
        Button buttonTime = findViewById(R.id.buttonTime);
        Button buttonSave = findViewById(R.id.buttonSave);


        navigationView = findViewById(R.id.bottom_navigation);
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
                    intent = new Intent(getApplicationContext(), Profile.class);
                    startActivity(intent);
                }
                return false;
            }
        });

        List<Calendar> calendarList = calendarExtractor.extractUserCalendars(getApplicationContext());
        updateCalendarsSpinner(calendarList);

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


        buttonSave.setOnClickListener(v -> {
            final java.util.Calendar startTimeStamp;
            final java.util.Calendar endTimeStamp;

            if(startDate != null && endDate != null ) {
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

            Calendar selectedCalendar = (Calendar) calendars.getSelectedItem();
            try (final Connection connection = connectionService.createConnection()) {
                connection.setAutoCommit(false);
                try (final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `evento`(`fecha`, `fechaFin`, `evento`, `lugar`, `idCalendario`) VALUES (?,?,?,?,?)");//
              final PreparedStatement obtainID = connection.prepareStatement("SELECT id FROM `evento` WHERE `fecha` = ? and `fechaFin` = ? AND `evento` = ? and `lugar` = ? and `idCalendario` = ?");//
              final PreparedStatement obtainCalendarUsers = connection.prepareStatement("SELECT nickname FROM `tiene` WHERE `idCalendario` = ? ");//
                     final PreparedStatement insertToCalendar = connection.prepareStatement("INSET INTO asiste (`idEvento`,`nickname`) VALUES (?,?)")) {


                    java.sql.Date sqlStartDate = new java.sql.Date(startTimeStamp.getTimeInMillis());
                    java.sql.Date sqlEndDate = new java.sql.Date(endTimeStamp.getTimeInMillis());

                    preparedStatement.setDate(1, sqlStartDate);
                    preparedStatement.setDate(2, sqlEndDate);
                    preparedStatement.setString(3, eventName.getText().toString());
                    preparedStatement.setString(4, eventPlace.getText().toString());
                    preparedStatement.setString(5, selectedCalendar.getUUID());
                    preparedStatement.executeUpdate();


                    obtainID.setDate(1, sqlStartDate);
                    obtainID.setDate(2, sqlEndDate);
                    obtainID.setString(3, eventName.getText().toString());
                    obtainID.setString(4, eventPlace.getText().toString());
                    obtainID.setString(5, selectedCalendar.getUUID());

                     try(final ResultSet rs = obtainID.executeQuery()) {
                         String idEvento = rs.getString("idEvento");


                         obtainCalendarUsers.setString(1, selectedCalendar.getUUID());
                         try (final ResultSet rsCalendar = obtainCalendarUsers.executeQuery()) {
                             while (rsCalendar.next()) {
                                 insertToCalendar.setString(1, idEvento);
                                 insertToCalendar.setString(2, rs.getString("nickname"));
                                 insertToCalendar.executeUpdate();

                             }

                         }

                     }



                    connection.commit();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
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
}