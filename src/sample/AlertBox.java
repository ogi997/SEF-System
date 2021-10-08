package sample;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class AlertBox {

    public static void showDialog(Alert.AlertType alertType, String headerText, String message){
        Alert alert = new Alert(alertType);
        alert.setHeaderText(headerText);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static String showDialogYesNo(Alert.AlertType alertType, String headerText, String message){
        Alert alert = new Alert(alertType, headerText, ButtonType.YES, ButtonType.NO);
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        return result.get().getText();
    }

}