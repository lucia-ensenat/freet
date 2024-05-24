package com.lucia.Freet.forms;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.lucia.Freet.R;

import java.util.UUID;

public class CalendarForm extends AppCompatActivity {
    private EditText nameEditText;
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


    }
}