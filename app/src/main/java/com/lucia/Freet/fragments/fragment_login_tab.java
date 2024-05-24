package com.lucia.Freet.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.lucia.Freet.Home;
import com.lucia.Freet.R;
import com.lucia.Freet.services.ConnectionService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_login_tab#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_login_tab extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private TextView emailEditText;
    private TextView passwordEditText;
    private Button loginButton;
    private ConnectionService connectionService;
    private CheckBox isLogged;

    public fragment_login_tab() {
        // Required empty public constructor
    }

    public static fragment_login_tab newInstance(String param1, String param2) {
        fragment_login_tab fragment = new fragment_login_tab();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        connectionService = new ConnectionService();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_tab, container, false);
        emailEditText = view.findViewById(R.id.login_email);
        passwordEditText = view.findViewById(R.id.login_password);
        isLogged = view.findViewById(R.id.checkBox2);
        loginButton = view.findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarSesion(v);
            }
        });
        return view;
    }


    public void iniciarSesion(View view) {
        final String email = emailEditText.getText().toString();

        try (Connection connection = connectionService.createConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM `usuarios` WHERE `email` = ? AND `password` = ?")) {
                statement.setString(1, email);
                statement.setString(2, passwordEditText.getText().toString());

                ResultSet rs = statement.executeQuery();
                if (rs.next()) {

                    SharedPreferences sharedPrefernces = getContext().getSharedPreferences("Prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPrefernces.edit();

                    editor.putBoolean("isLogged", isLogged.isChecked());
                    editor.putString("email", email);
                    editor.putString("nickname", rs.getString("nickname"));
                    editor.commit();
                    Intent intent = new Intent(getContext(), Home.class);
                    startActivity(intent);
                } else {
                    alertaUsuarioIncorrecto();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private void alertaUsuarioIncorrecto() {
    }
}