package org.example;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CheckCredentials {

    public static String  checkCredentials(String userID, String password) {
        String validCred = null;
        String fullName;
        String email;

        String queryCredentials = "SELECT UserID, FirstName + ' ' + LastName, Email, Type FROM Users WHERE UserID = ? AND Password=?";
        ResultSet rs = JdbcSQLServerConnection.resultSet2(queryCredentials, userID, password);
        try {
            if (rs.next()) {
                validCred = rs.getString(1).trim(); //+ " " + rs.getString(1).trim();
                fullName = rs.getString(2).trim();
                email = rs.getString(3).trim();
                String type = rs.getString(4);
                new ConnectedUser(validCred, fullName, email, type);
            }else{

            }
        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
            LogToFile.LogToFile(throwables.getMessage());
            //throwables.printStackTrace();
        }
        return validCred;
    }
}
