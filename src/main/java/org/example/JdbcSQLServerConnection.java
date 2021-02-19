package org.example;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class JdbcSQLServerConnection {

    public static ResultSet resultSet(String SQL) {
        Connection conn;
        ResultSet rs = null;

        try {
            conn = JdbcSQLServerConnection.connect();
            //ResultSet
            rs = conn.createStatement().executeQuery(SQL);
            //conn.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on Result Set");
        }
        return rs;
    }

    public static ResultSet resultSet2(String SQLQuery, String param1, String param2) {
        Connection conn;
        ResultSet rs = null;

        try {
            conn = JdbcSQLServerConnection.connect();

            PreparedStatement checkCredentials = conn.prepareStatement(SQLQuery);
            checkCredentials.setString(1, param1);
            checkCredentials.setString(2, param2);
            conn.setAutoCommit(false);

            rs = checkCredentials.executeQuery();
            conn.commit();


            //rs = conn.prepareStatement().executeQuery(SQL);
            //conn.close();

        } catch (Exception e) {
            LogToFile.LogToFile(e.getMessage());
            //e.printStackTrace();
            //System.out.println("Error on Result Set");
        }
        return rs;
    }

    public static boolean insertSQL(String SQL) {
        Connection conn;

        try {
            conn = JdbcSQLServerConnection.connect();

            conn.createStatement().executeUpdate(SQL);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on insert/update");
            return false;
        }
    }

    public static boolean update(String updateSQL, String param1, String param2) {
        Connection conn;

        try {
            conn = JdbcSQLServerConnection.connect();
            PreparedStatement psUpdate = conn.prepareStatement(updateSQL);
            psUpdate.setString(1, param1);
            psUpdate.setString(2, param2);

            psUpdate.executeUpdate();
            psUpdate.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on insert/update");
            return false;
        }
    }

    public static boolean updateUser(String updateSQL, String param1, String param2, String param3, String param4, String param5, String param6, String param7) {
        Connection conn;

        try {
            conn = JdbcSQLServerConnection.connect();
            PreparedStatement psUpdate = conn.prepareStatement(updateSQL);
            psUpdate.setString(1, param1);
            psUpdate.setString(2, param2);
            psUpdate.setString(3, param3);
            psUpdate.setString(4, param4);
            psUpdate.setString(5, param5);
            psUpdate.setString(6, param6);
            psUpdate.setString(7, param7);

            psUpdate.executeUpdate();
            psUpdate.close();
            return true;
        } catch (Exception e) {
            LogToFile.LogToFile(e.getMessage());
//            e.printStackTrace();
//            System.out.println("Error update User");
            return false;
        }
    }

    public static ObservableList getObservableList(String SQL) throws SQLException {
        ObservableList<String> data;
        data = FXCollections.observableArrayList();
        ResultSet rs = JdbcSQLServerConnection.resultSet(SQL);
        while (rs.next()) {
            //Iterate Row
            String row = "";
            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                //Iterate Column
                row = rs.getString(i).replaceFirst("\\s++$", "");
            }
            data.add(row);
        }
        return data;
    }

    public static ResultSet getResultSet(String SQL) throws SQLException {
        ResultSet rs = JdbcSQLServerConnection.resultSet(SQL);
        return rs;
    }

    public static Connection connect() {

        Connection conn = null;

        try {

            conn = DriverManager.getConnection(App.setProperties().getProperty("dbURL"), App.setProperties().getProperty("dbUser"), App.setProperties().getProperty("pass"));
            if (conn != null) {
                DatabaseMetaData dm = conn.getMetaData();
//                System.out.println("Driver name: " + dm.getDriverName());
//                System.out.println("Driver version: " + dm.getDriverVersion());
//                System.out.println("Product name: " + dm.getDatabaseProductName());
//                System.out.println("Product version: " + dm.getDatabaseProductVersion());

            }

        } catch (SQLException ex) {
            LogToFile.LogToFile(ex.getMessage());
            //ex.printStackTrace();
        }
        return conn;
    }

}
