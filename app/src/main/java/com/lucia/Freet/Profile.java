package com.lucia.Freet;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lucia.Freet.forms.EventForm;

public class Profile extends AppCompatActivity {

    private BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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
                }
                return false;
            }
        });
    }
}