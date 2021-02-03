package sample;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.net.URL;

/*
* STVARI ZA POPRAVITI / SREDITI
*
* centrirati poruku textError
* srediti gui da lijepo izgleda sve
*
* */



public class ControllerLogin extends HashMethode implements ErrorMessages {

    //Deklaracija promjenljivih sa login.fxml
    @FXML private JFXTextField usernameField;
    @FXML private JFXPasswordField passwordField;
    //@FXML private JFXButton loginButton; ne koristi se mislim
    @FXML private JFXButton regLink;
    @FXML private Label textError;

    //metoda za prijavljivanje na file system
    public void login(){

        User user = new User(); //kreiramo korisnika
        user.setUsername(usernameField.getText());
        user.setPassword(passwordField.getText());

        textError.setText(""); //ocisti

        if (user.getUsername().isEmpty() || user.getPassword().isEmpty()){
            textError.setText(inputError);
            return;
        }

        String pass = null;
        try{
            File file = new File("korisnici/"+user.getUsername()+".txt");
            Scanner reader = new Scanner(file);
            while(reader.hasNextLine()){
                pass = reader.nextLine();
            }
        }catch (FileNotFoundException e){
            textError.setText(errorUserPass);
            return;
        }

        if(!passwordValidation(user.getPassword(), pass)){
            textError.setText(errorUserPass);
            return;
        }

        //Napraviti redirekciju na board.fxml
        //dodati provjeru digital certification-a
        System.out.println("Uspjesno ste se prijavili!");
    }

    public void toRegistration(){
        regLink.setOnAction(new EventHandler<>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try{
                    URL url = getClass().getResource("fxml/register.fxml");
                    Parent reg = FXMLLoader.load(url);
                    Scene scene = new Scene(reg);
                    Stage window = new Stage();
                    window.setScene(scene);
                    window.setTitle("SEF System | Login");
                    window.setResizable(false);
                    window.show();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }


}//kraj controller klase
