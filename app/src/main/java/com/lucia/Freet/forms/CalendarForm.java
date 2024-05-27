package com.lucia.Freet.forms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.lucia.Freet.Home;
import com.lucia.Freet.Profile;
import com.lucia.Freet.R;

import java.util.UUID;

public class CalendarForm extends AppCompatActivity {
    private EditText nameEditText;
    private BottomNavigationView navigationView;
    private Switch share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_form);

        nameEditText = findViewById(R.id.nameEditText);
        share = findViewById(R.id.switchShare);
        final String uuid = UUID.randomUUID().toString();

        share.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CalendarForm.this);
                    builder.setTitle(String.format("Comparte el código : %s", uuid));
                    builder.setMessage("Comparte el código con tus amigos \n para que tengan acceso al calendario \n Luego podrás volver a consultarlo, no te preocupes");
                    builder.setCancelable(true);

                }


            }
        });

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
                    intent = new Intent(getApplicationContext(), Profile.class);
                    startActivity(intent);
                }
                return false;
            }
        });


    }
}