package com.lucia.Freet.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.lucia.Freet.Home;
import com.lucia.Freet.R;
import com.lucia.Freet.services.ConnectionService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class fragment_signup_tab extends Fragment {


    private ConnectionService connectionService;


    TextView emailEditText;
    TextView nicknameEditText;
    TextView passwordEditText;
    TextView passwordEditText2;
    CheckBox isLogged;
    Button signUp;

    public fragment_signup_tab() {
        // Required empty public constructor
    }

    public static fragment_signup_tab newInstance(String param1, String param2) {
        fragment_signup_tab fragment = new fragment_signup_tab();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectionService = new ConnectionService();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup_tab, container, false);
        emailEditText = view.findViewById(R.id.signup_email);
        nicknameEditText = view.findViewById(R.id.userEditText);
        passwordEditText = view.findViewById(R.id.signup_password);
        passwordEditText2 = view.findViewById(R.id.signup_confirm);
        isLogged = view.findViewById(R.id.checkBox);
        signUp = view.findViewById(R.id.signup_button);
        signUp.setEnabled(false);

        passwordEditText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No necesitamos hacer nada en este método
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Comprobamos que la confrimacion de la contraseña es igual a la contraseña
                if (!passwordEditText.getText().toString().equals(passwordEditText2.getText().toString())) {
                    // Si no coinciden, mostramos un mensaje de error
                    Toast.makeText(getContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                } else {
                    signUp.setEnabled(true);
                }
            }
        });

        return view;
    }

    public void iniciarSesion(View view) {
        try (Connection connection = connectionService.createConnection()) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO `usuarios`(`nickname`, `email`, `password`) VALUES ('?','?','?'); ");
            statement.setString(2, emailEditText.getText().toString());
            statement.setString(1, nicknameEditText.getText().toString());
            statement.setString(3, passwordEditText.getText().toString());
            int raw = statement.executeUpdate();
            if (raw == 1) {

                SharedPreferences sharedPrefernces = getContext().getSharedPreferences("Prefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPrefernces.edit();

                editor.putBoolean("isLogged", isLogged.isChecked());
                editor.putString("email", emailEditText.getText().toString());
                editor.putString("nickname", nicknameEditText.getText().toString());
                editor.commit();
                Intent intent = new Intent(getContext(), Home.class);
                startActivity(intent);
            }
            if (raw == 0) {
                Toast.makeText(getContext(), "Ese nickname no está disponible, intenta con otro", Toast.LENGTH_SHORT).show();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}