package com.lucia.Freet.ui.login;


import android.content.Intent;
import android.os.AsyncTask;
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
import com.lucia.Freet.utils.SharedPreferencesUtils;
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


    private TextView emailEditText;
    private TextView passwordEditText;
    private ConnectionService connectionService;
    private CheckBox isLogged;

    public fragment_login_tab() {
    }

    public static fragment_login_tab newInstance() {
        return new fragment_login_tab();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        connectionService = new ConnectionService();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_tab, container, false);
        emailEditText = view.findViewById(R.id.login_email);
        passwordEditText = view.findViewById(R.id.login_password);
        isLogged = view.findViewById(R.id.checkBox2);
        Button loginButton = view.findViewById(R.id.login_button);
        loginButton.setOnClickListener(this::iniciarSesion);
        return view;
    }


    public void iniciarSesion(View view) {
        final LoginTask loginTask = new LoginTask();
        loginTask.execute();

    }

    private void alertaUsuarioIncorrecto() {
    }

    private class LoginTask extends AsyncTask<String, Void, Boolean> {
        private String email;
        private String nickname;

        @Override
        protected Boolean doInBackground(String... params) {
            email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            try (final Connection connection = connectionService.createConnection()) {
                try (final PreparedStatement statement = connection.prepareStatement("SELECT * FROM `usuarios` WHERE `email` = ? AND `password` = ?")) {
                    statement.setString(1, email);
                    statement.setString(2, password);

                    try (final ResultSet rs = statement.executeQuery()) {
                        if (rs.next()) {
                            nickname = rs.getString("nickname");
                            return true;
                        }
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
                final SharedPreferencesUtils sharedPreferencesUtils = new SharedPreferencesUtils();
                sharedPreferencesUtils.writeUsersPrefs(requireContext(), isLogged.isChecked(), email, nickname);

                Intent intent = new Intent(getContext(), Home.class);
                startActivity(intent);
            } else {
                alertaUsuarioIncorrecto();
            }
        }
    }
}