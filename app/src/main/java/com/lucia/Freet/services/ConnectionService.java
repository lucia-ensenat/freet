package com.lucia.Freet.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionService {

    public Connection createConnection() {

        final String class_jdbc = "com.mysql.jdbc.Driver";

        try {
            Class.forName(class_jdbc);
            return DriverManager.getConnection("jdbc:mysql://192.168.0.17:3306/freet2", "root", "");
        } catch (SQLException e) {
            throw new RuntimeException("No se ha podido crear la conexión a la base de datos", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("No se ha podido crear la conexión a la base de datos", e);
        } catch (Exception e) {
            throw new RuntimeException("No se ha podido crear la conexión a la base de datos", e);
        }

    }


}
