package sample;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerEditFile implements Initializable {

    @FXML private Label nameOfFile;
    @FXML private JFXTextArea setTextArea;
    @FXML private JFXButton setEditText;

    private static String path;
    private static User user;

    public static void setData(String setPath){
        path = setPath;
    }
    public static void setUser(User setUser) {user = setUser;}

    public void edit(){
        File file = new File(path);
        FileWriter fw;
        try{
            fw = new FileWriter(file);
            String writeInFile = setTextArea.getText();
            fw.write(writeInFile);
            fw.close();
        }catch (IOException e){
            AlertBox.showDialog(Alert.AlertType.ERROR, "ERROR", ErrorMessages.errorInputFile);
        }
        AlertBox.showDialog(Alert.AlertType.INFORMATION, "SUCCESS", ErrorMessages.successEdit);
        Crypto crypto = new Crypto();
        file = new File(path);
        crypto.desEncrypt(file, user, 0);

        //poslije edita zatvori stage
        Stage stage = (Stage) setEditText.getScene().getWindow();
        stage.close();

    }

    private void readTextToEdit(){
        String textRead;
        String textToShow = new String();
        BufferedReader rd;
        FileReader fr;
        File f;
        try{
            f = new File(path);
            fr = new FileReader(f);
            rd = new BufferedReader(fr);
            nameOfFile.setText(f.getName());
            while( (textRead = rd.readLine()) != null ){
                textToShow+=textRead;
                textToShow+="\n";
            }
            setTextArea.setText(textToShow);
            rd.close();
            fr.close();
        }catch (IOException ex){
            AlertBox.showDialog(Alert.AlertType.ERROR, "ERROR", ErrorMessages.errorOpenFile);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        readTextToEdit();
    }
}