package org.example;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class TicketList implements Initializable {

    @FXML private ComboBox<String> departamentTicketList;
    @FXML private ComboBox<String> echipamentTicketList;
    @FXML private ComboBox<String> statusTicketList;

    @FXML private TableView<ObservableList> ticketTabel;
    @FXML private TableColumn<String, Integer> ticketID;

    private String selectedDepartament = "Toate";
    private String selectedEchipament = "Toate";
    private String selectedStatus;
    private ObservableList<ObservableList> data;
    private String connectedUser;

    @FXML
    private void setEchipament(){

        selectedDepartament = departamentTicketList.getSelectionModel().getSelectedItem();
        String equipmentSQL = "SELECT EquipmentName from Equipments WHERE EquipmentType = '" + selectedDepartament + "'";
        try {
            ObservableList<String> echipamenteList = JdbcSQLServerConnection.getObservableList(equipmentSQL);
            echipamenteList.add("Toate");
            echipamentTicketList.setItems(echipamenteList);

            //echipamentTicketList.getSelectionModel().selectLast();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }


    @FXML
    private void getSelectedEchipament(){

        selectedEchipament = echipamentTicketList.getSelectionModel().getSelectedItem();

    }

    @FXML
    private void getStatus(){

        selectedStatus = statusTicketList.getSelectionModel().getSelectedItem();

    }

    @FXML
    private void searchTickets() throws SQLException {

        ticketTabel.getColumns().clear();
        statusTicketList.getSelectionModel().clearSelection();
        statusTicketList.setPromptText("Status");
        data = FXCollections.observableArrayList();
        String ticketsSQL = "SELECT T0.TicketID, T0.TicketStatus, ISNULL(T0.Priority, 'Fara prioritate') AS 'Prioritate', \n" +
                "       T1.FirstName + ' ' + T1.LastName AS 'Owner', ISNULL(T0.Department, '') AS 'Departament', T0.EquipmentName, T0.IssueDescription, CONVERT(varchar, T0.OpenDate,105) AS 'Open Date',\n" +
                "       ISNULL(CONVERT(varchar,T0.ResolvedDate, 105), '') AS 'Data rezolvare', ISNULL(T0.ResolvedUser, '') AS 'Resolvat De', \n" +
                "       ISNULL(CONVERT(varchar,T0.ConfirmedDate, 105), '') AS 'Data Confirmare', ISNULL(T0.ConfirmedByUser, '') AS 'Confirmat De', \n" +
                "       ISNULL(T0.OpenObs, '') AS 'Observatii', ISNULL(T0.ResolvedObs, '') AS 'Observatii rezolvare', T1.Email \n" +
                "from Tickets T0 INNER JOIN Users T1 ON T0.TicketOwnerID=T1.UserID";
        String finalQuery = "";

        if(!selectedDepartament.matches("Toate")) {
            if (selectedEchipament != "Toate") {
                finalQuery = ticketsSQL + " WHERE T0.Department= '" + selectedDepartament + "' AND T0.EquipmentName = '" + selectedEchipament + "' ";
            } else {
                finalQuery = ticketsSQL + " WHERE T0.Department= '" + selectedDepartament + "'";
            }
        }else{
            finalQuery = ticketsSQL;
        }
        ResultSet rs = JdbcSQLServerConnection.resultSet(finalQuery);
        try {

            for (int i = 1; i <= rs.getMetaData().getColumnCount()-2; i++) {
                //We are using non property style for making dynamic table
                final int j = i-1;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i));
                //col.setStyle();
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });

                ticketTabel.getColumns().addAll(col);
                //System.out.println("Column [" + i + "] ");
            }

            /********************************
             * Data added to ObservableList *
             ********************************/
            while (rs.next()) {
                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    //Iterate Column
                    row.add(rs.getString(i));
                }
                //System.out.println("Row [1] added " + row);
                data.add(row);

            }
            FilteredList<ObservableList> filteredData = new FilteredList<>(data, p -> true);
            statusTicketList.valueProperty().addListener((observableValue, oldValue, newValue) -> {
                filteredData.setPredicate(observableList -> {
                    if(newValue == null || newValue.isEmpty()) {
                        return true;
                    }

                    if (observableList.get(1).equals(newValue)){
                        return true;
                    }
                    return false;
                });
            });

//            echipamentTicketList.valueProperty().addListener((observableValue, oldValue, newValue) -> {
//                filteredData.setPredicate(observableList -> {
//                    if(newValue == null || newValue.isEmpty()) {
//                        return true;
//                    }
//
//                    if (observableList.get(1).equals(newValue)){
//                        return true;
//                    }
//                    return false;
//                });
//            });

            SortedList<ObservableList> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(ticketTabel.comparatorProperty());
            ticketTabel.setItems(sortedData);

            //FINALLY ADDED TO TableView
            //ticketTabel.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
    }



    @FXML
    public void backToOptions() throws IOException { App.setRoot("options"); }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String ticketsSQL = "SELECT T0.TicketID, T0.TicketStatus, ISNULL(T0.Priority, 'Fara prioritate') AS 'Prioritate', \n" +
                "       T1.FirstName + ' ' + T1.LastName AS 'Owner', ISNULL(T0.Department, '') AS 'Departament', T0.EquipmentName, T0.IssueDescription, CONVERT(varchar, T0.OpenDate,105) AS 'Open Date',\n" +
                "       ISNULL(CONVERT(varchar,T0.ResolvedDate, 105), '') AS 'Data rezolvare', ISNULL(T0.ResolvedUser, '') AS 'Resolvat De', \n" +
                "       ISNULL(CONVERT(varchar,T0.ConfirmedDate, 105), '') AS 'Data Confirmare', ISNULL(T0.ConfirmedByUser, '') AS 'Confirmat De', \n" +
                "       ISNULL(T0.OpenObs, '') AS 'Observatii', ISNULL(T0.ResolvedObs, '') AS 'Observatii rezolvare', T1.Email \n" +
                "from Tickets T0 INNER JOIN Users T1 ON T0.TicketOwnerID=T1.UserID";




        ticketTabel.setRowFactory( tv -> {
            TableRow<ObservableList> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    ObservableList rowData = row.getItem();
                    //System.out.println(rowData.get(1));
                    ArrayList<String> ticketDetailsList = new ArrayList<>();
                    ticketDetailsList.add(rowData.get(0).toString());
                    ticketDetailsList.add(rowData.get(1).toString());
                    ticketDetailsList.add(rowData.get(2).toString());
                    ticketDetailsList.add(rowData.get(3).toString());
                    ticketDetailsList.add(rowData.get(4).toString());
                    ticketDetailsList.add(rowData.get(5).toString());
                    ticketDetailsList.add(rowData.get(12).toString());
                    ticketDetailsList.add(rowData.get(11).toString());
                    ticketDetailsList.add(rowData.get(10).toString());
                    ticketDetailsList.add(rowData.get(13).toString());
                    ticketDetailsList.add(rowData.get(14).toString());

                    try {

                        FXMLLoader loader = new FXMLLoader(App.class.getResource("ticketUpdate.fxml"));
                        Parent root1 = loader.load();

                        TicketUpdateController ticketUpdateController = loader.getController();
                        ticketUpdateController.getTicketID(rowData.get(0).toString());
                        ticketUpdateController.getTicketDetailsList(ticketDetailsList);
                        ticketUpdateController.getTicketDetails(
                                "Ticket ID:   " + rowData.get(0) + "\n" +
                                "Status:      " + rowData.get(1) + "\n" +
                                "Prioritate:  " + rowData.get(2) + "\n" +
                                "Owner:       " + rowData.get(3) + "\n" +
                                "Departament: " + rowData.get(4) + "\n" +
                                "Echipament:  " + rowData.get(5)+ "\n" +
                                "Defectiune:  " + rowData.get(6)+ "\n" +
                                "Deschis in:        " + rowData.get(7));

                        Stage stage = new Stage();
                        stage.setTitle(rowData.get(0).toString());
                        stage.setScene(new Scene(root1));
                        stage.show();
                    } catch (IOException ex) {
                        System.err.println(ex.getCause().toString());
                    }
                }
            });
            return row ;
        });
    }


}
