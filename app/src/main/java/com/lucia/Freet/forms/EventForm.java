package com.lucia.Freet.forms;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.lucia.Freet.R;
import com.lucia.Freet.extractor.CalendarsExtractor;
import com.lucia.Freet.models.Calendar;
import com.lucia.Freet.services.ConnectionService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class EventForm extends AppCompatActivity {
    private EditText eventName;
    private EditText eventPlace;
    private Spinner calendars;
    private TextView eventDate;
    private CalendarsExtractor calendarExtractor;
    private ConnectionService connectionService;
    static int hours;
    static int minutes;
    static String startDate;
    static String endDate;

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

        fullfillCalendars(calendars);


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
                startDate = String.valueOf(new Date(selection.first));
                endDate = String.valueOf(new Date(selection.second));
                eventDate.setText(String.format("%s-%s", startDate, endDate));

            });

            pickerRange.addOnCancelListener(dialog -> {
                pickerRange.dismiss();
            });


        });


        buttonTime.setOnClickListener(v -> {
            pickerTime.show(getSupportFragmentManager(), "Select Date");

            pickerTime.addOnPositiveButtonClickListener(dialog -> {
                hours = pickerTime.getHour();
                minutes = pickerTime.getMinute();

            });

        });

        buttonSave.setOnClickListener(v -> {
            eventPlace.getText();
            eventDate.getText();
            eventPlace.getText();
            calendars.getSelectedItem();
            try (final Connection connection = connectionService.createConnection();//
                 final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO event VALUES ('nombre', 'fechaInicio', 'fechaFin', 'lugar'");//
                 final PreparedStatement insertToCalendar = connection.prepareStatement("INSET INTO tiene VALUES ('id','name')");//
            ) {

                preparedStatement.executeUpdate();
                insertToCalendar.executeUpdate();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void fullfillCalendars(final Spinner calendars) {
        List<Calendar> calendarList = calendarExtractor.extractUserCalendars();

        if (calendarList.isEmpty()) {
            alertNoCalendars();
        }

        ArrayAdapter<Calendar> adapter = new ArrayAdapter<Calendar>(this,
                android.R.layout.simple_spinner_item, calendarList);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        calendars.setAdapter(adapter);
    }

    private void alertNoCalendars() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EventForm.this);
        builder.setTitle("Ups!!");
        builder.setMessage("Parece que no tienes ningun calendario. Antes de añadir un evento crea un calendario");
        builder.setCancelable(false);

        builder.setPositiveButton("CREAR AHORA", (DialogInterface.OnClickListener) (dialog, which) -> {
            Intent intent = new Intent(this, CalendarForm.class);
            startActivity(intent);
        });

        builder.setNegativeButton("MAS TARDE", (DialogInterface.OnClickListener) (dialog, which) -> {
            finish();
        });

        AlertDialog alertDialog = builder.create();
        // Show the Alert Dialog box
        alertDialog.show();
    }
}