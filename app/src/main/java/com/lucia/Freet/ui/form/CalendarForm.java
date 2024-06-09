package com.lucia.Freet.ui.form;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lucia.Freet.Home;
import com.lucia.Freet.Profile;
import com.lucia.Freet.R;
import com.lucia.Freet.services.ConnectionService;
import com.lucia.Freet.utils.SharedPreferencesUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import java.util.UUID;

public class CalendarForm extends AppCompatActivity {
    private EditText nameEditText;
    private ConnectionService connectionService;
    private BottomNavigationView navigationView;
    private Switch share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_form);

        nameEditText = findViewById(R.id.nameEditText);
        share = findViewById(R.id.switchShare);
        navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setSelectedItemId(R.id.addCalendar);
        final Button saveButton = findViewById(R.id.button);
        saveButton.setEnabled(false);
        final Button buttonCalendar = findViewById(R.id.buttonCalenario);
        connectionService = new ConnectionService();
        Random rand = new Random();

        final String id =  String.valueOf(rand.nextInt((9999 - 0) + 1) + 0);

        share.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CalendarForm.this);
                    builder.setTitle(String.format("Comparte el código : %s", id));
                    builder.setMessage("Comparte el código con tus amigos \n para que tengan acceso al calendario \n No lo podrás volver a consultar, asegurate de que lo guardas bine ");
                    builder.setCancelable(true);
                    builder.show();

                }


            }
        });

        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                saveButton.setEnabled(!s.toString().trim().isEmpty());
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


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveCalendarTask saveCalendarTask = new SaveCalendarTask(nameEditText.getText().toString(), id);
                saveCalendarTask.execute();
            }
        });


        buttonCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CalendarForm.this);
                builder.setTitle("Introduce el código del calendario");
                final EditText calendarCode = new EditText(CalendarForm.this);
                calendarCode.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(calendarCode);

                builder.setPositiveButton("UNIRTE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NewUserToCalendarTask newUserToCalendarTask = new NewUserToCalendarTask(calendarCode.getText().toString());
                        newUserToCalendarTask.execute();
                    }
                });

                builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.show();


            }
        });

    }

    private class NewUserToCalendarTask extends AsyncTask<String, Void, Boolean> {
        private String uuid;

        public NewUserToCalendarTask(String uuid) {

            this.uuid = uuid;
        }

        @Override
        protected Boolean doInBackground(String... params) {

            try (final Connection connection = connectionService.createConnection()) {
                try (final PreparedStatement statement = connection.prepareStatement("INSERT INTO `tiene`(`nickname`, `idCalendario`) VALUES (?,?)")) {
                    statement.setString(1, SharedPreferencesUtils.get("nickname", CalendarForm.this));
                    statement.setString(2, this.uuid);
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
                Toast toast = Toast.makeText(CalendarForm.this, "Ya formas parte del calendario", Toast.LENGTH_SHORT);
                toast.show();

            } else {
                Toast toast = Toast.makeText(CalendarForm.this, "No se ha podido llevar a cabo", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    private class SaveCalendarTask extends AsyncTask<String, Void, Boolean> {
        private String uuid;
        private String name;

        public SaveCalendarTask(String name, String uuid) {

            this.uuid = uuid;
            this.name = name;
        }

        @Override
        protected Boolean doInBackground(String... params) {

            try (final Connection connection = connectionService.createConnection()) {
                try (final PreparedStatement statement = connection.prepareStatement("INSERT INTO `calendarios`(`id`, `calendario`) VALUES (?,?)")) {
                    statement.setString(1, this.uuid);
                    statement.setString(2, this.name);
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
                NewUserToCalendarTask newUserToCalendarTask = new NewUserToCalendarTask(this.uuid);
                newUserToCalendarTask.execute();

                Toast toast = Toast.makeText(CalendarForm.this, "Se ha creado el nuevo calendario", Toast.LENGTH_SHORT);
                toast.show();


            } else {
                Toast toast = Toast.makeText(CalendarForm.this, "No se ha podido llevar a cabo", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }


}