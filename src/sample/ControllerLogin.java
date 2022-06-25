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
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.*;
import java.math.BigInteger;
import java.security.PublicKey;
import java.security.cert.*;
import java.util.Scanner;
import java.net.URL;
import java.security.cert.X509Certificate;

import static java.security.Security.addProvider;

public class ControllerLogin extends HashMethode implements ErrorMessages {

    //Deklaracija promjenljivih sa login.fxml
    @FXML private JFXTextField usernameField;
    @FXML private JFXPasswordField passwordField;
    @FXML private JFXButton regLink;
    @FXML private JFXButton loginButton;
    @FXML private Label textError;

public  X509Certificate getCertificate(File file) {
    X509Certificate certificate = null;
    CertificateFactory factory;
    FileInputStream fis = null;
    try {
        fis = new FileInputStream(file);
        factory = CertificateFactory.getInstance("X.509");
        certificate = (X509Certificate) factory.generateCertificate(fis);
    } catch (Exception ex) {
        //ex.printStackTrace();
        AlertBox.showDialog(Alert.AlertType.ERROR, "ERROR", "Greska pri ucitavanju sertifikata.");
    } finally {
        if (fis != null) {
            try {
                fis.close();
            } catch (Exception ex) {
                //ex.printStackTrace();
                AlertBox.showDialog(Alert.AlertType.ERROR, "ERROR", "Greska pri ucitavanju sertifikata.");
            }
        }
    }
    return certificate;
}

public boolean provjeraSertifikata(String username, int mode) {

    addProvider(new BouncyCastleProvider());
    FileInputStream fis;
    File file = new File("/home/ognjen/IdeaProjects/KriptoProject/DigitalCertification/"+username+".pem");
    //File forPrivateKey = new File("/home/ognjen/IdeaProjects/KriptoProject/DigitalCertification/"+username+".key");

    File crlFile = new File("/home/ognjen/IdeaProjects/KriptoProject/DigitalCertification/"+"lista.pem"); //rootca.pem //caSertifikat.pem -> lista.pem
    File rootCa  = new File("/home/ognjen/IdeaProjects/KriptoProject/DigitalCertification/"+"rootca.pem"); //caSertifikat.pem


    X509Certificate korisnik = getCertificate(file);
    X509Certificate rootCert = getCertificate(rootCa);
    X509CRL crl;
    X509CRLEntry povuceni;

        try{
        fis = new FileInputStream(crlFile);
        CertificateFactory cf = CertificateFactory.getInstance("X509");
        crl = (X509CRL) cf.generateCRL(fis);
        BigInteger serialNumber;
        if(crl != null){
            serialNumber = korisnik.getSerialNumber();
            povuceni = crl.getRevokedCertificate(serialNumber);
            if(povuceni != null){
                if (mode == 1)
                    AlertBox.showDialog(Alert.AlertType.ERROR, "ERROR", "Sertifikat je povucen.");
                return false;
            }
        }
        fis.close();
        PublicKey caPublicKey = rootCert.getPublicKey();
        korisnik.verify(caPublicKey);
        korisnik.checkValidity();
      //  provjera isteka vremenski
    } catch (CertificateExpiredException dateExpire) {
            //dateExpire.printStackTrace();
            if (mode == 1)
                AlertBox.showDialog(Alert.AlertType.ERROR, "Digitalni certifikat", "Vas certifikat je istekao.");
        return false;
    } catch (CertificateNotYetValidException notYetValid){
            //notYetValid.printStackTrace();
            if (mode == 1)
                AlertBox.showDialog(Alert.AlertType.ERROR, "Digitalni certifikat", "Vas certifikat jos nije validan. Pokusajte ponovo kasnije.");
        return false;
    } catch (Exception e){
        //e.printStackTrace();
       if(mode == 1)
            AlertBox.showDialog(Alert.AlertType.ERROR, "Digitalni certifikat", "Vas certifiakt nije validan.");
        return false;
    }
    return true;
}
    //metoda za prijavljivanje na file system
    public void login(){
        User user = new User(); //kreiramo korisnika
        user.setUsername(usernameField.getText());
        user.setPassword(passwordField.getText());

        textError.setText("");

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

        //napravi lijep ispis poruke
        if (!provjeraSertifikata(user.getUsername(), 1)){
            return;
            //System.out.println("Digital Certification je validan.\nUspjesno ste se prijavili!");
        }/*else {
            System.out.println("Digitalni Certificat nije validan.\nNiste se uspjesno prijavili");
            return;
        }*/

        //zatvaranje login.fxml scene
        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.close();

        try{
            URL url;
            url = getClass().getResource("fxml/board.fxml");
            FXMLLoader loader = new FXMLLoader(url);
            ControllerBoard con = new ControllerBoard();
            con.initData(user);
            Parent board = loader.load();
            Scene scene = new Scene(board);
            Stage window = new Stage();
            window.setScene(scene);
            window.setTitle("SEF System | Board");
            window.setResizable(false);
            window.show();
            window.setOnCloseRequest(event -> {
                String str = closeBoard();
                if (str.equals("No"))
                    event.consume();
            });
        }catch (Exception e){
            //e.printStackTrace();
            AlertBox.showDialog(Alert.AlertType.ERROR, "ERROR", ErrorMessages.errorOpen);
        }
    }

    public String closeBoard() {
        return AlertBox.showDialogYesNo(Alert.AlertType.WARNING, "Izlaz", "Da li ste sigurni da zelite izaci?\nOdjava ce se automatski izvrsiti.");
    }

    public String closeRegistration(){
        return AlertBox.showDialogYesNo(Alert.AlertType.WARNING, "Izlaz", "Da li ste sigurni da zelite izaci?");

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
                    window.setTitle("SEF System | Register");
                    window.setResizable(false);
                    window.show();
                    window.setOnCloseRequest(event -> {
                        String str = closeRegistration();
                        if (str.equals("No"))
                            event.consume();
                    });
                }catch (Exception e){
                    AlertBox.showDialog(Alert.AlertType.ERROR, "ERROR", ErrorMessages.errorOpen);
                }
            }
        });
    }
}