package com.lucia.Freet.services;

import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionService {
    private Connection connection;

   public ConnectionService(){

       final String class_jdbc = "com.mysql.jdbc.Driver";

       StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
               .permitAll().build();
       StrictMode.setThreadPolicy(policy);

       Connection conn = null;
       try {
           Class.forName(class_jdbc);
           connection = DriverManager.getConnection("jdbc:mysql://192.168.0.17:3306/freet2","root", "");
       } catch (SQLException e) {
           throw new RuntimeException("No se ha podido crear la conexión a la base de datos", e);
       } catch (ClassNotFoundException e) {
           throw new RuntimeException("No se ha podido crear la conexión a la base de datos", e);
       } catch (Exception e) {
           throw new RuntimeException("No se ha podido crear la conexión a la base de datos", e);
       }

   }
   public Connection createConnection(){
       return connection;
   }



}
