package org.example;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javafx.animation.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.print.Doc;

import static org.example.Print.print;


public class OptionsController implements Initializable {



    @FXML private Button newTicket;
    @FXML private Button history;
    @FXML private Button signOut;
    @FXML private Button openTicketsBtn;
    @FXML private Button resolvedTicketsBtn;
    @FXML private Button confirmedTicketsBtn;
    @FXML private Button administrationBtn;
    @FXML private Button settingsBtn;
    @FXML private Label connectedUserLbl;
    @FXML private Label dbConLabel;
    @FXML private Label printerConLabel;
    @FXML private PieChart ticketsChart;
    @FXML private BarChart barChart;
    @FXML private AnchorPane anchorPane;
    @FXML private Label openTicketsLbl;
    @FXML private Label resolvedTicketsLbl;
    @FXML private Label confirmedTicketsLbl;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        if(App.setProperties().getProperty("dbType").equals("Baza de date test")){
            dbConLabel.setText(App.setProperties().getProperty("dbType"));
        }

        // Procesare fise scanate

        String outputPath = App.setProperties().getProperty("processedFilesPath"); //"C:\\TicketApp\\FiseScanate\\";
        String querySQL = "SELECT TicketID FROM Tickets WHERE TicketID = ? AND TicketID = ? AND Attachments IS NULL";

        try {
            File f = new File(App.setProperties().getProperty("scannedFilesPath"));

            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File f, String name) {
                    return name.endsWith(".pdf");
                }
            };

            File[] files = f.listFiles(filter);


            for (File file : files) {
                String qrText = QRCodeReaderFromJPG.readQR(file, outputPath);

                ResultSet rs = JdbcSQLServerConnection.resultSet2(querySQL, qrText, qrText);
                if (rs.next()) {
                    //String updateFilePath = "UPDATE Tickets SET Attachments = ? WHERE TicketID = ?";
                    File scannedFile = new File(outputPath + qrText + ".pdf");
                    file.renameTo(scannedFile);

//                    if(JdbcSQLServerConnection.update(updateFilePath, scannedFile.getName(), qrText)){
//
//                    };

                }

            }
        } catch (Exception e) {
            //System.err.println(e.getMessage() + "aaaaa");
        }

        //============================================================

        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<String,Number> bc = new BarChart<>(xAxis, yAxis);
        xAxis.setLabel("Departament");
        yAxis.setLabel("Numar de tichete");
        //yAxis.tickUnitProperty().setValue(10);
        bc.lookup(".chart-plot-background").setStyle("-fx-background-color: transparent;");
        bc.lookup(".chart-title").setStyle("-fx-text-fill: #4682b4; -fx-font-size: 1.6em;");
        bc.lookup(".axis-label").setStyle("-fx-text-fill: #4682b4; -fx-font-size: 1.6em;");
        bc.getXAxis().setTickLabelFill(Color.GREENYELLOW);
        bc.getYAxis().setTickLabelFill(Color.GREENYELLOW);
        final Label caption = new Label("");
        caption.setTextFill(Color.DARKBLUE);
        caption.setStyle("-fx-font: 24 arial;");

        String getTicketsString = "SELECT EquipmentName, COUNT(TicketID) FROM Tickets WHERE TicketStatus=? GROUP BY EquipmentName";
        PreparedStatement getTicketsSQL=null;
        try {
            getTicketsSQL = JdbcSQLServerConnection.connect().prepareStatement(getTicketsString);
            JdbcSQLServerConnection.connect().setAutoCommit(false);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        List<Button> buttonList = new ArrayList<>();
        buttonList.add(openTicketsBtn);
        buttonList.add(resolvedTicketsBtn);
        buttonList.add(confirmedTicketsBtn);

        PreparedStatement finalGetTicketsSQL = getTicketsSQL;
        buttonList.forEach(e->{
            e.addEventHandler(MouseEvent.MOUSE_ENTERED, event->{
                //e.setStyle();
                //System.out.println(e.getText());
                try {
                    finalGetTicketsSQL.setString(1, e.getText());
                    ResultSet rs = finalGetTicketsSQL.executeQuery();
                    JdbcSQLServerConnection.connect().commit();

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            });
            e.addEventHandler(MouseEvent.MOUSE_EXITED, event -> System.out.println("EXIT"));
        });



//        final RotateTransition rotate = new RotateTransition(Duration.seconds(5), settingsBtn);
//        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1.0), evt -> rotate.setByAngle(10)),
//                new KeyFrame(Duration.seconds(2.0), evt -> rotate.setByAngle(-10)));
//        timeline.setCycleCount(Animation.INDEFINITE);
//        timeline.play();
//        rotate.setByAngle(10);
//        rotate.setCycleCount(Animation.INDEFINITE);
//        rotate.setInterpolator(Interpolator.EASE_IN);
//        rotate.play();




        String chartDataSQL = "SELECT Priority, COUNT(TicketID) FROM Tickets WHERE TicketStatus='DESCHIS' GROUP BY Priority";
        ResultSet rs = JdbcSQLServerConnection.resultSet(chartDataSQL);
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        while (true) {
            try {
                if (!rs.next()) break;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            //ObservableList<String> row = FXCollections.observableArrayList();
            try {
                pieChartData.add(new PieChart.Data(rs.getString(1), rs.getInt(2)));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        ticketsChart.setData(pieChartData);
        ticketsChart.setTitle("Situatie Tichete");
        ticketsChart.setLabelLineLength(10);



        for (final PieChart.Data data : ticketsChart.getData()) {
            data.getNode().addEventHandler(MouseEvent.MOUSE_ENTERED_TARGET,
                    e -> {
                        bc.getData().clear();

                        XYChart.Series series = new XYChart.Series();
//                        System.out.println(data.getPieValue());
//                        System.out.println(data.getName());
                        String dataBarChartSQL = "SELECT T1.DepartmentName, COUNT(T2.TicketID) AS 'NumberOfTickets' From Departments T1 LEFT OUTER JOIN Tickets T2 ON T1.DepartmentName=T2.Department AND T2.Priority = '" + data.getName() + "' Group By T1.DepartmentName";

                        ResultSet rsBarChart = JdbcSQLServerConnection.resultSet(dataBarChartSQL);
                        while (true) {
                            try {
                                if (!rsBarChart.next()) break;
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }
                            try {
                                series.getData().add(new XYChart.Data(rsBarChart.getString(1), rsBarChart.getInt(2)));
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }
                        }

                        bc.getData().addAll(series);
                        //bc.setTitle(data.getPieValue() + " " + data.getName());
                        anchorPane.getChildren().add(bc);
                        bc.prefWidthProperty().bind(anchorPane.widthProperty());
                        bc.prefHeightProperty().bind(anchorPane.heightProperty());

                        double catSpace = xAxis.getCategorySpacing();
                        double avilableBarSpace = catSpace - (bc.getCategoryGap() + bc.getBarGap());
                        double barWidth = (avilableBarSpace / bc.getData().size()) - bc.getBarGap();


                        caption.setTranslateX(e.getSceneX());
                        caption.setTranslateY(e.getSceneY());
                        caption.setText(data.getPieValue() + "");
                        caption.setTextFill(Color.DARKBLUE);
                    });
            data.getNode().addEventHandler(MouseEvent.MOUSE_EXITED,
                    e -> {
                        anchorPane.getChildren().clear();
                    });

            data.getNode().addEventHandler(MouseEvent.MOUSE_CLICKED,
                    e -> {
                        anchorPane.getChildren().clear();
                    });
        }

        Timeline timeline2 = new Timeline(new KeyFrame(Duration.seconds(1.0), evt -> connectedUserLbl.setTextFill(Color.RED)),
                new KeyFrame(Duration.seconds(2.0), evt -> connectedUserLbl.setTextFill(Color.GREENYELLOW)));
        timeline2.setCycleCount(Animation.INDEFINITE);
        timeline2.play();

        Timeline timelinePrintLabel = new Timeline(new KeyFrame(Duration.seconds(0.5), evt -> {
            if(checkPrinterStatus.checkPrinterStatus().equals("3")){
                printerConLabel.setText("Imprimanta este online");
                printerConLabel.setTextFill(Color.YELLOWGREEN);
            }else{
                printerConLabel.setText("Imprimanta nu este online!");
                printerConLabel.setTextFill(Color.RED);
            }

        }),
                new KeyFrame(Duration.seconds(5.0), evt -> printerConLabel.setText("")));
        timelinePrintLabel.setCycleCount(Animation.INDEFINITE);
        timelinePrintLabel.play();


        connectedUserLbl.setText("Utilizator conectat: " + ConnectedUser.getConnectedUserFullName());
        String userTypeSQL = "SELECT Type FROM Users WHERE UserID = '" + ConnectedUser.getConnectedUser() + "'";

        try {
            String userType = JdbcSQLServerConnection.getObservableList(userTypeSQL).get(0).toString();
            if(userType.matches("GODLIKE ADMIN")){
                administrationBtn.setDisable(false);
                settingsBtn.setDisable(false);
            }
            if(userType.matches("ADMIN")){
                administrationBtn.setDisable(false);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        //afisare numar tichete

        String queryTickets = "SELECT TicketStatus, COUNT(TicketID)  FROM Tickets GROUP BY TicketStatus";
        ResultSet rsTickets = JdbcSQLServerConnection.resultSet(queryTickets);
        try{
            while (rsTickets.next()) {
                    switch(rsTickets.getString(1)){
                        case "CONFIRMAT":
                            confirmedTicketsLbl.setText("CONFIRMATE " + rsTickets.getString(2) +  " TICHETE");
                            break;
                        case "REZOLVAT":
                            resolvedTicketsLbl.setText("REZOLVATE " + rsTickets.getString(2) +  " TICHETE");
                            break;
                        case "DESCHIS":
                            openTicketsLbl.setText("DESCHISE " + rsTickets.getString(2) +  " TICHETE");
                            break;
                        default:break;
                    }
            }
        }catch(SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @FXML
    private void switchToNewTicket() throws IOException{ App.setRoot("newTicket"); }

    @FXML
    private void switchToTicketList() throws IOException { App.setRoot("ticketList"); }

    @FXML
    private void switchToAdministration() throws IOException{ App.setRoot("administration"); }

    @FXML
    private void switchToOptions() { }

    @FXML
    public void backToLogin() throws IOException { App.setRoot("login"); }

}