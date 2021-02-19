package org.example;

public class ConnectedUser {

    private static String ConnectedUser;
    private static String fullName;
    private static String emailAddress;
    private static String type;
    ConnectedUser(String userId, String fullName, String emailAddress, String type){
        ConnectedUser = userId;
        this.fullName = fullName;
        this.emailAddress = emailAddress;
        this.type = type;
    }

    public static String getConnectedUser() {
        return ConnectedUser;
    }
    public static String getConnectedUserFullName() {
        return fullName;
    }
    public static String getEmailAddress() {  return emailAddress; }
}
