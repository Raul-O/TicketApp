package org.example;


import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.Initializable;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.stage.Stage;
import javafx.util.Callback;

public class TicketUpdateController implements Initializable{

    @FXML private Label connectedUserLbl;
    @FXML private Label ticketDetailsLbl;
    @FXML private Label ticketObs1Lbl;
    @FXML private Label ticketObs2Lbl;
    @FXML private CheckBox confirmedCheckBox;
    @FXML private CheckBox priorityCheckBox;
    @FXML private CheckBox resolvedCheckBox;
    @FXML private Button closeBtn;
    @FXML private Button updateBtn;
    @FXML private Button attachmentBtn;
    @FXML private ComboBox priorityBtn;
    @FXML private ComboBox statusBtn;
    @FXML private TextArea updateObs2;
    @FXML private ListView usersList;
    @FXML private TextField hoursField;
    @FXML private TextField minutesField;

    private String userType;
    private String ticketID;
    private List ticketDetailsList;
    private String resolved = "";
    private String confirmed = "";
    private String status;
    private String priority="";
    ArrayList<String> maintenanceTeam = new ArrayList<>();

    String newLine = System.getProperty("line.separator");//line separator

    //get date and time
    SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm");
    Date date = new Date(System.currentTimeMillis());
    String dateString = formatter.format(date);


    @FXML
    private void closeWindow() {
        Stage stage = (Stage) closeBtn.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        connectedUserLbl.setText("Utilizator conectat: " + ConnectedUser.getConnectedUserFullName());

        String userTypeSQL = "SELECT Type FROM Users WHERE UserID = '" + ConnectedUser.getConnectedUser() + "'";

        try {
            userType = JdbcSQLServerConnection.getObservableList(userTypeSQL).get(0).toString();
            if(userType.matches("L1")){

                resolvedCheckBox.setDisable(true);
                priorityBtn.setDisable(true);
                statusBtn.setDisable(true);
                usersList.setVisible(false);
                hoursField.setDisable(true);
                minutesField.setDisable(true);
            }
            if(userType.matches("L2")){
                confirmedCheckBox.setDisable(true);
                priorityBtn.setDisable(true);
                statusBtn.setDisable(true);
                hoursField.setDisable(true);
                minutesField.setDisable(true);
            }
            if(userType.matches("L3")){
                resolvedCheckBox.setDisable(true);
                confirmedCheckBox.setDisable(true);
                priorityBtn.setDisable(true);
                statusBtn.setDisable(true);
                usersList.setVisible(false);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            closeWindow();
        }





    String usersListSQL = "SELECT FirstName + ' ' + LastName FROM Users WHERE Department = 'Mentenanta' AND UserID<>'mentenanta'";
        try {
            usersList.setItems(JdbcSQLServerConnection.getObservableList(usersListSQL));
            //usersList.setCellFactory(CheckBoxListCell.forListView(item -> item.onProperty()));
            usersList.setCellFactory(CheckBoxListCell.forListView((Callback<String, ObservableValue<Boolean>>) item -> {
                BooleanProperty observable = new SimpleBooleanProperty();
                observable.addListener((obs, wasSelected, isNowSelected) ->{

                    if(isNowSelected){
                        maintenanceTeam.add(item);
                    }else{
                        maintenanceTeam.remove(item);
                    }
                    //System.out.println("Check box for "+item+" changed from "+wasSelected+" to "+isNowSelected);
                        }

                );
                return observable ;
            }));

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    void getTicketDetails(String ticketDetails) {
        ticketDetailsLbl.setText(ticketDetails);
    }

    void getTicketDetailsList(ArrayList ticketDetails) {
        ticketDetailsList = new ArrayList<>(ticketDetails);
        ticketObs1Lbl.setText(ticketDetailsList.get(6).toString());
        ticketObs2Lbl.setText(ticketDetailsList.get(9).toString());
        status = "'" + ticketDetailsList.get(1).toString() + "'";
        if(ticketDetailsList.get(1).equals("CONFIRMAT") || ticketDetailsList.get(1).equals("INCHIS")){
            confirmedCheckBox.setSelected(true);
            confirmedCheckBox.setDisable(true);
            resolvedCheckBox.setDisable(true);
            priorityBtn.setDisable(true);
            statusBtn.setDisable(true);

        }
        if(ticketDetailsList.get(1).equals("DESCHIS")){
            confirmedCheckBox.setDisable(true);
        }
        if(ticketDetailsList.get(1).equals("REZOLVAT")){
            resolvedCheckBox.setSelected(true);
        }

        if(ticketDetailsList.get(1).equals("INCHIS")){
            updateBtn.setDisable(true);
        }

        if(ticketDetailsList.get(3).toString().trim().equals("Mentenanta") && userType.matches("L1")){
            confirmedCheckBox.setDisable(true);
        }

        //Activate File button if file exists
        File f = new File(App.setProperties().getProperty("processedFilesPath") + ticketDetailsList.get(0).toString() + ".pdf");

        //Check if the specified file exists or not
        if (f.exists()) {
            attachmentBtn.setDisable(false);
        }
        else {
            attachmentBtn.setDisable(true);
        }

    }



    @FXML
    private void checkResolved(){
        if(resolvedCheckBox.isSelected()){
            status = "'REZOLVAT'";
        }else{
            status = "'DESCHIS'";
        }
    }

    @FXML
    private void checkConfirmed(){
        if(confirmedCheckBox.isSelected()){
            status = "'CONFIRMAT'";
        }else{
            status = "'REZOLVAT'";
        }
    }

    @FXML
    private void checkPriority(){
        if(!priorityBtn.getSelectionModel().getSelectedItem().toString().equals(ticketDetailsList.get(2).toString())){
            priority =  ", Priority = '" + priorityBtn.getSelectionModel().getSelectedItem().toString() + "'";
        }else{
            priority = "";
        }
    }

    @FXML
    private void setStatus(){
        if(!statusBtn.getSelectionModel().getSelectedItem().toString().equals(ticketDetailsList.get(1).toString())){
            status = "'" + statusBtn.getSelectionModel().getSelectedItem().toString() +"'";
        }else{
            status = "";
        }
    }

    @FXML
    private void openFile() throws IOException {
        File f = new File(App.setProperties().getProperty("processedFilesPath") + ticketDetailsList.get(0).toString() + ".pdf");
        Desktop.getDesktop().open(f);
    }

    void getTicketID(String ticketDetails) {
        ticketID = ticketDetails;
    }

    @FXML
    private void updateTicket(){

        String updateMaintenanceTeam = "";
        AtomicReference<String> maintenanceTeamString= new AtomicReference<>("'");
        maintenanceTeam.stream().forEach(e-> {
            maintenanceTeamString.set(maintenanceTeamString + "/" + e);
        });
        System.out.println(maintenanceTeamString);


//        Properties prop = null;
//
//        try (
//                InputStream input = new FileInputStream("\\\\TRD57L5Z43\\TicketApp\\config.properties")) {
//
//            prop = new Properties();
//            prop.load(input);
//
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//        System.out.println(!status.replaceAll("'", "").matches(ticketDetailsList.get(1).toString()));
//        System.out.println(!ticketObs2Lbl.getText().isEmpty());
//        System.out.println(!priority.isEmpty());

        if(!status.replaceAll("'", "").matches(ticketDetailsList.get(1).toString()) || !ticketObs2Lbl.getText().isEmpty() || !priority.isEmpty()) {

            if (status.equals("'REZOLVAT'")){
                resolved = ", ResolvedDate = GETDATE(), ResolvedUser ='" + ConnectedUser.getConnectedUser() + "'";
                updateMaintenanceTeam = ", MaintenanceTeam = " + maintenanceTeamString + "'";
            }
            if (status.equals("'CONFIRMAT'")){
                confirmed = ", ConfirmedDate = GETDATE(), ConfirmedByUser ='" + ConnectedUser.getConnectedUser() + "'";
            }
            if (status.equals("'DESCHIS'")){
                resolved = ", ResolvedDate = NULL, ResolvedUser =''";
                confirmed = ", ConfirmedDate = NULL, ConfirmedByUser =''";
            }
            String updateSQL = "UPDATE Tickets SET TicketStatus = " + status +
                    resolved +
                    confirmed +
                    ", ResolvedObs = '" + ticketObs2Lbl.getText() + "\n" + updateObs2.getText() + "' " +
                    priority +
                    updateMaintenanceTeam +
                    " WHERE TicketID = '" + ticketID + "'";
            System.out.println(updateSQL);

            if(JdbcSQLServerConnection.insertSQL(updateSQL)){
                SimpleDateFormat formatter1= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date startDate = new Date(System.currentTimeMillis());
                String startD = formatter1.format(startDate);
                String subject = "Incident " + ticketDetailsList.get(0).toString() +
                        " " + status.replaceAll("'", "");
                String mailBody = "<table border='2' cellpadding='1' cellspacing='0' style='color:Black;font-family:arial,helvetica,sans-serif;text-align:left;'>" +
                        "<tr style ='font-size:18px;font-weight: normal;background: #68EAB6'> " +
                        "<th align=left><b>Ticket</b></th> " +
                        "<th align=left><b>Modificat De</b></th> " +
                        "<th align=left><b>Status</b></th> " +
                        "<th align=left><b>Prioritate</b></th> " +


                        "<tr style='font-size:18px;background-color:#68EAB6'> " +
                        "<td>" + ticketDetailsList.get(0).toString() + "</td> " +
                        "<td>" + ConnectedUser.getConnectedUserFullName() + "</td> " +
                        "<td>" + status.replaceAll("'", "") + "</td> " +
                        "<td>" + ((priority == null) ? "FARA PRIORITATE" : priority.replaceAll("'", "").replaceAll(", Priority =", "") )+ "</td> " +

                        "</tr>";

                new SendEmail(App.setProperties().getProperty("toAddress") + "," + ticketDetailsList.get(10), App.setProperties().getProperty("ccAddressUpdate"), App.setProperties().getProperty("from"), App.setProperties().getProperty("host"), App.setProperties().getProperty("port"), subject, mailBody);
            }
            closeWindow();
        }
    }

}
