package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL url = getClass().getResource("fxml/login.fxml");
        Parent root= FXMLLoader.load(url);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("SEF System | Login");
        primaryStage.setResizable(false);
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
            String str = closeLogin();
            if (str.equals("No"))
                event.consume();
        });
    }
    public String closeLogin(){
        return AlertBox.showDialogYesNo(Alert.AlertType.WARNING, "Izlaz", "Da li ste sigurni da zelite izaci?");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
