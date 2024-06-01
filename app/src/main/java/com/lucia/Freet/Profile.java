package com.lucia.Freet;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lucia.Freet.forms.EventForm;
import com.lucia.Freet.services.ConnectionService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Profile extends AppCompatActivity {

    private BottomNavigationView navigationView;
    private TextView emailText;
    private TextView nicknameText;
    private Button passwordButton;
    private ConnectionService connectionService;
    private Button historicEventsButton;
    private Button calendarEventsButton;
    private String email;
    private String nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        connectionService = new ConnectionService();

        navigationView = findViewById(R.id.bottom_navigation);
        emailText = findViewById(R.id.emailText);
        nicknameText = findViewById(R.id.nicknameText);
        passwordButton = findViewById(R.id.passwordButton);
        historicEventsButton = findViewById(R.id.buttonHistoric);
        calendarEventsButton = findViewById(R.id.buttonCalendar);


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
                try (final PreparedStatement statement = connection.prepareStatement("UPDATE `usuarios` SET `password`=? WHERE `nickname`=?;")) {
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
}