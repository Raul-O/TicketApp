package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class App extends Application {

    static Stage primaryStage;
    static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {

        primaryStage = stage;
        scene = new Scene(loadFXML("login"));
        primaryStage.setScene(scene);
        primaryStage.setTitle("Aplicatie Tichete");
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    static Properties setProperties(){
        Properties properties=null;
        try (
             //TEST ENVIRONMENT
               InputStream input = new FileInputStream("C:\\TicketApp\\config.properties")) {

            //PRODUCTION ENVIRONMENT
                //InputStream input = new FileInputStream("\\\\TRD57L5Z43\\TicketApp\\config.properties")) {

            properties = new Properties();
            properties.load(input);


        } catch (IOException ex) {
            LogToFile.LogToFile(ex.getMessage());
            //ex.printStackTrace();
        }
        return  properties;
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }


    static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}