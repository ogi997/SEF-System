package sample;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

public class ControllerRegister extends HashMethode implements ErrorMessages {

    //Deklaracija promjenljivih sa register.fxml
    @FXML private JFXTextField usernameFieldReg;
    @FXML private JFXPasswordField passwordFieldReg;
    @FXML private Label textError;

    public void register(){
        User user = new User();

        user.setUsername(usernameFieldReg.getText());
        user.setPassword(passwordFieldReg.getText());

        if(user.getUsername().isEmpty() || user.getPassword().isEmpty()){
            textError.setText(inputError);
            return;
        }

        try{
            File file = new File("korisnici/"+user.getUsername()+".txt");
            if(file.exists()){
                textError.setText(errorUsernameExist);
                return;
            }
            FileWriter writeFile = new FileWriter(file);

            //random broj od 0 - 2
            Random rand = new Random();
            int randomNumber = rand.nextInt(3); //trebalo bi da baci neki random broj 0,1,2
            switch(randomNumber){
                //za slucajnu 0 hash password sa md5
                case 0: writeFile.write(MD5(user.getPassword()));
                    break;
                //za slucajnu 1 hash password sa sha-256
                case 1: writeFile.write(SHA_256(user.getPassword()));
                    break;
                //za slucajnu 2 hash password sa sha-512
                case 2: writeFile.write(SHA_512(user.getPassword()));
                    break;
            }
            writeFile.close();
        }catch (IOException e){
            AlertBox.showDialog(Alert.AlertType.ERROR, "ERROR", ErrorMessages.errorRegistration);
        }

        try{
            Path path = Paths.get(user.getPath()+user.getUsername()); //ovo prepraviti getPath i ostalo
            Files.createDirectory(path);

        }catch (IOException e) {
            AlertBox.showDialog(Alert.AlertType.ERROR, "ERROR", ErrorMessages.errorHomeDir);
        }
        AlertBox.showDialog(Alert.AlertType.INFORMATION, "SUCCESS", ErrorMessages.successRegistration);
    }
}