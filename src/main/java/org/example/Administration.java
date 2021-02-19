package org.example;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class Administration implements Initializable {

    @FXML private javafx.scene.control.Label firstNameLabel;
    @FXML private javafx.scene.control.Label lastNameLabel;
    @FXML private javafx.scene.control.Label infoLabel;
    @FXML private javafx.scene.control.TextField userID;
    @FXML private javafx.scene.control.TextField firstName;
    @FXML private javafx.scene.control.TextField lastName;
    @FXML private javafx.scene.control.TextField emailAddress;
    @FXML private javafx.scene.control.TextField passwordField;

    @FXML private ComboBox departmentComboBox;
    @FXML private ComboBox userTypeComboBox;

    @FXML private Button deleteUserBtn;
    @FXML private Button addOrUpdateBtn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {



    }

    @FXML
    private void checkUserID(){

        infoLabel.setText("");
        String checkUserIDSQL = "Select UserID, FirstName, LastName, ISNULL(Email, '') AS Email, Type, Department, Password FROM Users WHERE UserID = '" + userID.getText() + "'";
        ResultSet userResultSet = JdbcSQLServerConnection.resultSet(checkUserIDSQL);
        try {
            if(userResultSet.next()){
                if(userResultSet.getString(5).matches("GODLIKE ADMIN")) {
                    firstName.setText(userResultSet.getString(2));
                    lastName.setText(userResultSet.getString(3));
                    emailAddress.setText(userResultSet.getString(4));
                    userTypeComboBox.getSelectionModel().select(userResultSet.getString(5));
                    departmentComboBox.getSelectionModel().select(userResultSet.getString(6));
                    passwordField.setText("Aici nu apare parola");
                    passwordField.setDisable(true);
                    addOrUpdateBtn.setDisable(true);
                    firstName.setDisable(true);
                    lastName.setDisable(true);
                    emailAddress.setDisable(true);
                    departmentComboBox.setDisable(true);
                    userTypeComboBox.setDisable(true);
                    //departmentComboBox.getSelectionModel().select();

                }else{
                    firstName.setText(userResultSet.getString(2));
                    lastName.setText(userResultSet.getString(3));
                    emailAddress.setText(userResultSet.getString(4));
                    userTypeComboBox.getSelectionModel().select(userResultSet.getString(5));
                    departmentComboBox.getSelectionModel().select(userResultSet.getString(6));
                    passwordField.setText(userResultSet.getString(7));
                    deleteUserBtn.setDisable(false);
                    addOrUpdateBtn.setText("Actualizeaza");
                    firstName.setDisable(false);
                    lastName.setDisable(false);
                    emailAddress.setDisable(false);
                    departmentComboBox.setDisable(false);
                    userTypeComboBox.setDisable(false);
                    passwordField.setDisable(false);
                }


            }else{
                firstName.setText("");
                lastName.setText("");
                emailAddress.setText("");
                userTypeComboBox.valueProperty().set(null);
                departmentComboBox.valueProperty().set(null);
                passwordField.setText("");
                deleteUserBtn.setDisable(true);
                firstName.setDisable(false);
                lastName.setDisable(false);
                emailAddress.setDisable(false);
                departmentComboBox.setDisable(false);
                userTypeComboBox.setDisable(false);
                passwordField.setDisable(false);
//                if(userID.getText().trim().length()<4){
//                    addOrUpdateBtn.setDisable(true);
//                }else {
//                    firstName.setDisable(false);
//                    lastName.setDisable(false);
//                    addOrUpdateBtn.setDisable(false);
//                }
                addOrUpdateBtn.setText("Salveaza");
                addOrUpdateBtn.setDisable(false);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @FXML
    private void enableEmailField(){
        if(firstName.getText().trim().length()>2 && lastName.getText().trim().length()>2){
            emailAddress.setDisable(false);
        }
    }

    @FXML
    private void checkEmailAddress(){

        if(emailAddress.getText().trim().length()>0) {
            if (!checkValidEmailaddress.isValidEmailAddress(emailAddress.getText())) {
                emailAddress.setText("");
                emailAddress.requestFocus();
            }
        }
    }

    @FXML
    private void deleteUser(){

    }

    @FXML
    private void addOrUpdate(){
        if(addOrUpdateBtn.getText()=="Actualizeaza"){

            if(userID.getText().trim().length()<4 || firstName.getText().trim().length()<2 || lastName.getText().trim().length()<2 ||
                    departmentComboBox.getSelectionModel().isEmpty() || userTypeComboBox.getSelectionModel().isEmpty() ||
                    passwordField.getText().trim().length()<4){
                infoLabel.setText("Date incomplete");
            }else{
                String updateUserSQL = "UPDATE Users SET FirstName=?, LastName=?, Email=?, Type=?, Department=?, Password=? WHERE UserID = ?";
                        String userIDParam = userID.getText().trim().toLowerCase();
                        String firstNameParam = firstName.getText().trim().substring(0, 1).toUpperCase() + firstName.getText().trim().substring(1).toLowerCase();
                        String lastNameParam = lastName.getText().trim().substring(0, 1).toUpperCase() + lastName.getText().trim().substring(1).toLowerCase();
                        String email = emailAddress.getText().toLowerCase();
                        String userType = userTypeComboBox.getSelectionModel().getSelectedItem().toString();
                        String department = departmentComboBox.getSelectionModel().getSelectedItem().toString();
                        String pass = passwordField.getText().trim();

                if(JdbcSQLServerConnection.updateUser(updateUserSQL, firstNameParam, lastNameParam, email, userType, department, pass, userIDParam)){
                    infoLabel.setText("Utilizator Actualizat");
                    infoLabel.setTextFill(Color.YELLOW);
                    userID.requestFocus();
                    userID.setText("");
                    firstName.setText("");
                    lastName.setText("");
                    emailAddress.setText("");
                    passwordField.setText("");
                    addOrUpdateBtn.setDisable(true);
                }else{
                    infoLabel.setText("Eroare Adaugare utilizator");
                    infoLabel.setTextFill(Color.RED);
                    addOrUpdateBtn.setDisable(true);
                }
            }

            System.out.println("trebuie actualizat");
        }else{
            System.out.println("Trebuie adaugat");
            //System.out.println(departmentComboBox.getSelectionModel().isEmpty());
            if(userID.getText().trim().length()<4 || firstName.getText().trim().length()<2 || lastName.getText().trim().length()<2 ||
                    departmentComboBox.getSelectionModel().isEmpty() || userTypeComboBox.getSelectionModel().isEmpty() ||
            passwordField.getText().trim().length()<4){
                infoLabel.setText("Date incomplete");
            }else{
                String addUserSQL = "INSERT INTO Users(UserID, FirstName, LastName, Email, Type, Department, Password) VALUES ('" +
                        userID.getText().trim().toLowerCase() + "', '" +
                        firstName.getText().trim().substring(0, 1).toUpperCase() + firstName.getText().trim().substring(1).toLowerCase() + "', '" +
                        lastName.getText().trim().substring(0, 1).toUpperCase() + lastName.getText().trim().substring(1).toLowerCase() + "', '" +
                        emailAddress.getText().toLowerCase() + "', '" +
                        userTypeComboBox.getSelectionModel().getSelectedItem().toString() + "', '" +
                        departmentComboBox.getSelectionModel().getSelectedItem().toString() + "', '" +
                        passwordField.getText().trim() + "') ";
                System.out.println(addUserSQL);
                if(JdbcSQLServerConnection.insertSQL(addUserSQL)){
                    infoLabel.setText("Utilizator Adaugat");
                    infoLabel.setTextFill(Color.GREEN);
                    userID.requestFocus();
                    userID.setText("");
                    firstName.setText("");
                    lastName.setText("");
                    emailAddress.setText("");
                    passwordField.setText("");
                    addOrUpdateBtn.setDisable(true);
                }else{
                    infoLabel.setText("Eroare Adaugare utilizator");
                    infoLabel.setTextFill(Color.RED);
                    addOrUpdateBtn.setDisable(true);
                }
            }
        }
    }

    @FXML
    private void switchToOptions() throws IOException { App.setRoot("options"); }
}
