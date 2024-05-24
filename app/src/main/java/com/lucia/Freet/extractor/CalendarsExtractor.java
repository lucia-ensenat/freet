package com.lucia.Freet.extractor;

import static android.content.Context.MODE_PRIVATE;

import android.content.*;
import android.content.Intent;

import com.lucia.Freet.models.Calendar;
import android.content.SharedPreferences;
import com.lucia.Freet.models.User;
import com.lucia.Freet.services.ConnectionService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CalendarsExtractor {
    ConnectionService connectionService = new ConnectionService();

    public List<Calendar> extractUserCalendars() {
        final List<Calendar> calendars = new ArrayList<>();
        Context context = null;
        SharedPreferences sharedPreferences = context.getSharedPreferences("Prefs", MODE_PRIVATE);
        Optional<String> nickname = Optional.ofNullable(sharedPreferences.getString("nickanme", null));

        try (final Connection connection = connectionService.createConnection();//
             final PreparedStatement preparedStatement = connection.prepareStatement("SELECT nombre from TIENE WHERE nickname = ?");) {
               nickname.ifPresent( x -> {
                   try {
                       preparedStatement.setString(1,x);
                   } catch (SQLException e) {
                       throw new RuntimeException(e);
                   }
               });

               try( final ResultSet resultSet =preparedStatement.executeQuery();){
                   if(!resultSet.next())
                       return Collections.EMPTY_LIST;

                   while(resultSet.next()){
                      final  Calendar calendar = new Calendar(resultSet.getString("nombre"), (List<User>) resultSet.getArray("users"), resultSet.getString("uuid"));
                       calendars.add(calendar);
                   }
                   return  calendars;
               }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
