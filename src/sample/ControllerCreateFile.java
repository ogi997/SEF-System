package sample;

import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerCreateFile implements Initializable {

    @FXML private Label pathToShow;
    @FXML private JFXTextField nameFile;
    @FXML private JFXTextArea contentFile;
    @FXML private Button create;

    private static String path;
    private static TreeView<String> tree;
    private static User user;
    public static void setData(String setPath){
        path = setPath;
    }
    public static void setTree(TreeView<String> treeView){ tree= treeView; }
    public static void setUser(User setUser) { user = setUser;}

    public void createFile(){

        File file = new File(path);
        String name = nameFile.getText();
        String content = contentFile.getText();

        if(name.isEmpty() || content.isEmpty()){
            AlertBox.showDialog(Alert.AlertType.ERROR, "ERROR", ErrorMessages.errorInputCreate);
            return;
        }

        File f = new File(file.getAbsolutePath()+"/"+name+".txt");
        try {
            if(f.exists()){
                AlertBox.showDialog(Alert.AlertType.INFORMATION, "INFORMATION", "Datoteka: "+name+" vec postoji na fajl sistemu");
                return;
            }
            FileWriter fw = new FileWriter(f);
            fw.write(content);
            fw.close();
            ControllerBoard cb = new ControllerBoard();
            cb.updateTree(tree);
        }catch(IOException e){
            AlertBox.showDialog(Alert.AlertType.ERROR, "ERROR", ErrorMessages.error);
        }
        AlertBox.showDialog(Alert.AlertType.INFORMATION, "SUCCESS", ErrorMessages.successCreated);
        //fajl je kreiran uspjesno samo ga jos enkriptuj
        Crypto crypto = new Crypto();
        //System.out.println("Kreiraj na: "+f.getAbsolutePath());
        crypto.desEncrypt(f, user, 0);

        //zatvori prozor poslije kreiranja fajla
        Stage stage = (Stage) create.getScene().getWindow();
        stage.close();

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        pathToShow.setText(path);
    }
}