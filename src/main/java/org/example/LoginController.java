package org.example;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    public TextField username;
    public PasswordField password;
    public Label loginMsg;

    @FXML
    private void validateCredentials() {
        if(username.getText().isEmpty() || password.getText().isEmpty()){
            System.out.println("Introduceti user si parola");
        }else {
            if(CheckCredentials.checkCredentials(username.getText(), password.getText())==null){
                loginMsg.setText("User/parola incorecte");
                //App.setRoot("options");
            }else if (CheckCredentials.checkCredentials(username.getText(), password.getText())=="0") {
                loginMsg.setText("Baza de date este offline");
            }else{
                try {
                    App.setRoot("options");
                } catch (IOException e) {
                    LogToFile.LogToFile(e.getMessage());
                    //e.printStackTrace();
                }
            }
        }
    }

    @FXML
    private void clearLoginMessage(){
        loginMsg.setText("");
    }

    @FXML
    private void switchToOptions() throws IOException {
        App.setRoot("options");
    }
}
