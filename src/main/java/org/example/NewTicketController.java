package org.example;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class NewTicketController implements Initializable {

    @FXML private ComboBox<String> departament;
    @FXML private ComboBox<String> echipament;
    @FXML private ComboBox<String> defectiune;
    @FXML private ComboBox<String> prioritate;
    @FXML private TextArea obs;
    @FXML private Button okButton;
    @FXML private Label newTicketInfoLbl;

    private String selectedDepartament;
    private String selectedEquipment;
    private String selectedIssue;
    private String priority;


    String newLine = System.getProperty("line.separator");//line separator

    //get date and time
    SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm");
    Date date = new Date(System.currentTimeMillis());
    String dateString = formatter.format(date);

    Properties prop = null;


    @FXML
    private void setEchipament(){


        selectedDepartament = departament.getSelectionModel().getSelectedItem();
        if(selectedDepartament.equals("Altul")){
            obs.setDisable(false);
        }else {
            echipament.setDisable(false);
            String equipmentSQL = "SELECT EquipmentName from Equipments WHERE EquipmentType = '" + selectedDepartament + "'";
            try {
                ObservableList<String> echipamenteList = JdbcSQLServerConnection.getObservableList(equipmentSQL);
                echipamenteList.add("Altul");
                echipament.setItems(echipamenteList);

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

    }

    @FXML
    private void setDefectiune(){

        defectiune.setDisable(false);
        selectedEquipment = String.valueOf(echipament.getSelectionModel().getSelectedItem()).replaceAll("(^\\[|\\]$)", "");
        String issueSQL2 = "SELECT IssueName from Issues T0 INNER JOIN Equipments T1 ON T0.EquipmentID = T1.EquipmentID  WHERE T1.EquipmentName = '" + selectedEquipment + "'";

        try {
            ObservableList<String> defectiuneList = JdbcSQLServerConnection.getObservableList(issueSQL2);
            defectiuneList.add("ALTA PROBLEMA");
            defectiune.setItems(defectiuneList);
            departament.setDisable(true);
            echipament.setDisable(true);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    @FXML
    private void setPrioritate(){

        prioritate.setDisable(false);
        selectedIssue = String.valueOf(defectiune.getSelectionModel().getSelectedItem()).replaceAll("(^\\[|\\]$)", "");
        defectiune.setDisable(true);
    }

    @FXML
    private void setObs(){

        priority = prioritate.getSelectionModel().getSelectedItem();
        obs.setDisable(false);
        prioritate.setDisable(true);

    }

    @FXML
    private void activateOK(){
        okButton.setDisable(false);
    }

    @FXML
    private void goBack() throws IOException { App.setRoot("options"); }


    @FXML
    public void addTicket(ActionEvent event) {

//        try (
//                InputStream input = new FileInputStream("\\\\TRD57L5Z43\\TicketApp\\config.properties")) {
//
//            prop = new Properties();
//            prop.load(input);
//
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
        String ticketId = UUID.randomUUID().toString().replace("-", "");
        String addTicketSQL = "INSERT INTO Tickets (TicketID, TicketStatus, TicketOwnerID, Department, EquipmentName, IssueDescription, OpenDate, OpenObs, Priority) VALUES ('" +
                ticketId +
                "', 'DESCHIS', '" +
                ConnectedUser.getConnectedUser() + "', '" +
                selectedDepartament + "', '" +
                selectedEquipment + "', '" +
                selectedIssue +
                "', GETDATE(), '" +
                obs.getText() + "', '" + priority + "')";
        System.out.println(addTicketSQL);

        //System.out.println(addTicketSQL);
        String checkSimilarTicket = "SELECT TicketID FROM Tickets WHERE Department = '" + selectedDepartament + "' AND EquipmentName = '" + selectedEquipment + "' AND IssueDescription = '" + selectedIssue + "' AND TicketStatus IN ('DESCHIS') AND Department NOT IN ('Mentenanta', 'Altul') AND EquipmentName<>'Altul' AND IssueDescription<>'ALTA PROBLEMA'";
        System.out.println(checkSimilarTicket);
        ResultSet similarTicket =  JdbcSQLServerConnection.resultSet(checkSimilarTicket);
        try {
            if (similarTicket.next()==true){
                newTicketInfoLbl.setText("Este deschis un ticket similar!");
                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), evt -> newTicketInfoLbl.setTextFill(Color.RED)),
                        new KeyFrame(Duration.seconds(0.5), evt -> newTicketInfoLbl.setTextFill(Color.GREENYELLOW)));
                timeline.setCycleCount(Animation.INDEFINITE);
                timeline.play();
                okButton.setDisable(true);
            }else {


                if (JdbcSQLServerConnection.insertSQL(addTicketSQL)) {
                    newTicketInfoLbl.setText("Ticket adaugat cu succes.");
                    SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date startDate = new Date(System.currentTimeMillis());
                    String startD = formatter1.format(startDate);
                    String subject = "Incident " + selectedEquipment + " " + dateString;
                    String mailBody = "<table border='2' cellpadding='2' cellspacing='0' style='color:Black;font-family:arial,helvetica,sans-serif;text-align:left;'>" +
                            "<tr style ='font-size:18px;font-weight: normal;background: #68EAB6'> " +
                            "<th align=left><b>Autor</b></th> " +
                            "<th align=left><b>Data</b></th> " +
                            "<th align=left><b>Sector</b></th> " +
                            "<th align=left><b>Echipament</b></th> " +
                            "<th align=left><b>Defectiune</b></th>" +
                            "<th align=left><b>Observatii</b></th></tr>" +

                            "<tr style='font-size:18px;background-color:#68EAB6'> " +
                            "<td>" + ConnectedUser.getConnectedUserFullName() + "</td> " +
                            "<td>" + dateString + "</td> " +
                            "<td>" + selectedDepartament + "</td> " +
                            "<td>" + selectedEquipment + "</td> " +
                            "<td>" + selectedIssue + "</td>" +
                            "<td>" + obs.getText() + "</td>" +
                            "</tr>";

                    List<String> info = new ArrayList<>();
                    info.add("Incident");
                    info.add("Creat de");
                    info.add("Data");
                    info.add("Departament");
                    info.add("Echipament");
                    info.add("Defectiune");
                    info.add(ticketId);
                    info.add(ConnectedUser.getConnectedUserFullName());
                    info.add(dateString);
                    info.add(selectedDepartament);
                    info.add(selectedEquipment);
                    info.add(selectedIssue);
                    CreatePDF.createPDF(info, obs.getText(), ticketId);
                    String toAddress = App.setProperties().getProperty("toAddress");
                    String ccList = App.setProperties().getProperty("ccAddress") + "," + ConnectedUser.getEmailAddress();
                    System.out.println(selectedDepartament);
//                    if(selectedDepartament.equals("Mentenanta")){
//                        toAddress = prop.getProperty("ccAddress");
//                    }else{
//                        toAddress = prop.getProperty("toAddress");
//                    }
                    new SendEmail(toAddress, ccList, App.setProperties().getProperty("from"), App.setProperties().getProperty("host"), App.setProperties().getProperty("port"), subject, mailBody);

                    if (Print.print(ticketId)) {
                        String updatePrintSQL = "UPDATE Tickets SET Printed = 'Yes' WHERE TicketId = '" + ticketId + "'";
                        if (JdbcSQLServerConnection.insertSQL(updatePrintSQL)) {
                            System.out.println("Ticket actualizat");
                        } else {
                            System.out.println("Nu a fost actualizatt");
                        }
                    } else {
                        System.out.println("nu a fost printat");
                    }
                    try {
                        App.setRoot("options");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    newTicketInfoLbl.setText("Eroare adaugare ticket!");

                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if(ConnectedUser.getConnectedUser().equals("mentenanta")){
            departament.getSelectionModel().select("Mentenanta");
            selectedDepartament = departament.getSelectionModel().getSelectedItem();
            echipament.setDisable(true);
            defectiune.setDisable(true);
            prioritate.setDisable(true);
            obs.setDisable(false);
        }else{
            departament.getItems().remove("Mentenanta");
        }
    }
}
