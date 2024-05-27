package com.lucia.Freet.extractor;

import android.content.Context;

import com.lucia.Freet.SharedPreferencesUtils;
import com.lucia.Freet.models.Calendar;
import com.lucia.Freet.services.ConnectionService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CalendarsExtractor {
    ConnectionService connectionService = new ConnectionService();

    public List<Calendar> extractUserCalendars(Context context) {
        final HashMap<String, String> preferencias = SharedPreferencesUtils.getUsersPrefs(context);
        String nickname = preferencias.get("nickname");
        List<Calendar> calendars = new ArrayList<>();

        if (nickname != null) {
            try (final Connection connection = connectionService.createConnection();
                 final PreparedStatement preparedStatement = connection.prepareStatement("SELECT * from calendarios WHERE id IN (select idCalendario from tiene where nickname = ?)")) {

                preparedStatement.setString(1, nickname);

                try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        final Calendar calendar = new Calendar(resultSet.getString("calendario"), resultSet.getString("id"));
                        calendars.add(calendar);
                    }
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return calendars;
    }
}
