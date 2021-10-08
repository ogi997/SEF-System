package sample;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.crypto.BadPaddingException;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.ResourceBundle;

public class ControllerBoard implements Initializable {
    @FXML public TreeView<String> stabloDir;
    @FXML private JFXButton logout;
    @FXML private TreeView<String> sharedTree;
    @FXML private ChoiceBox<String> choiceUser;


    //ucitaj slike za treeItem
    private final Image imgFolder = new Image(getClass().getResourceAsStream("icons/folder.png"));
    private final Image imgTxt = new Image(getClass().getResourceAsStream("icons/txt.png"));
    private final Image imgPng = new Image(getClass().getResourceAsStream("icons/png.png"));
    private final Image imgJpeg = new Image(getClass().getResourceAsStream("icons/jpeg.png"));
    private final Image imgDocx = new Image(getClass().getResourceAsStream("icons/docx.png"));
    private final Image imgPdf = new Image(getClass().getResourceAsStream("icons/pdf.png"));
    private final Image imgNo = new Image(getClass().getResourceAsStream("icons/close.png"));

    public static User user = new User(); //static zbog ponovnog inicanciranja u memoriji da zadrzi staru vrijednost

    public void initData(User oldUser){
       user = oldUser;
    }

    public void createTreeView() {
        TreeItem<String> root = new TreeItem<>(user.getUsername(), new ImageView(imgFolder));
        getAllDirAndFiles(user.getPath()+user.getUsername(), root);

        root.setExpanded(true);
        stabloDir.setRoot(root);
    }

    public void updateTree(TreeView<String> tree){
        tree.getRoot().getChildren().clear();
        getAllDirAndFiles(user.getPath()+user.getUsername(), tree.getRoot());
    }

    public void getAllUser(ChoiceBox<String> root){
        File file = new File("/home/ognjen/IdeaProjects/KriptoProject/korisnici");
        File[] users = file.listFiles();

        /*
        NAPRAVITI PROVJERU U SLUCAJU DA NA SISTEMU POSTOJI SAMO JEDAN KORISNIK REGISTROVAN
         */
        if(users == null) {
            root.setValue("Nema registrovanih korisnika osim vas!");
            return;
        }
        
        for(File files : users){
            String str;
            str = files.getName().substring(0, files.getName().length()-4); //kako je ime korisnikn.txt trazi substring bez .txt odnosno od 0 (pocetni indeks) do duzina stringa - 4 (.txt se oduzima) i ostaje samo ime korisnika
            //ne dodajemo logovanog korisnika koji pokrece program nema potrebe da on bude na spisku
            if(!str.equals(user.getUsername()))
                root.getItems().add(str);
        }
    }

    public void getAllDirAndFiles(String path, TreeItem<String> root){
        File f = new File(path);
        File[] files = f.listFiles();

        if(files != null)
            for(File file : files){
                TreeItem<String> node;
                if(file.isDirectory()) {
                    node = new TreeItem<>(file.getName(), new ImageView(imgFolder));
                }else if(file.getName().endsWith(".txt")){
                    node = new TreeItem<>(file.getName(), new ImageView(imgTxt));
                }else if(file.getName().endsWith(".docx")){
                    node = new TreeItem<>(file.getName(), new ImageView(imgDocx));
                }else if(file.getName().endsWith(".png")){
                    node = new TreeItem<>(file.getName(), new ImageView(imgPng));
                }else if(file.getName().endsWith(".jpeg")){
                    node = new TreeItem<>(file.getName(), new ImageView(imgJpeg));
                }else if(file.getName().endsWith(".pdf")){
                    node = new TreeItem<>(file.getName(), new ImageView(imgPdf));
                }else {
                    node = new TreeItem<>(file.getName(), new ImageView(imgNo));
                }
                if(file.isDirectory()){
                    getAllDirAndFiles(file.getPath(), node);
                }
                root.getChildren().add(node);
            }
    }

    public String getPathFromTree() {
        StringBuilder pathBuilder = new StringBuilder();
        for (TreeItem<String> item = stabloDir.getSelectionModel().getSelectedItem(); item != null; item = item.getParent()) {
            pathBuilder.insert(0, item.getValue());
            pathBuilder.insert(0, "/");
        }
        String path = pathBuilder.toString();

        if(path.equals("")) {
            path = null;
        }
        return path;
    }

    public void createFile() {
        String pathToCreate = getPathFromTree();
        File file = new File("root"+pathToCreate);

        if(!file.isDirectory()){
            AlertBox.showDialog(Alert.AlertType.ERROR, "ERROR", ErrorMessages.errorSelected);
            return;
        }

        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/createFile.fxml"));
            ControllerCreateFile.setData(file.getPath());
            ControllerCreateFile.setTree(stabloDir);
            ControllerCreateFile.setUser(user);
            Parent board = loader.load(); //poslije ove linije kreira se createFile.fxml
            Scene scene = new Scene(board);
            Stage window = new Stage();
            window.setScene(scene);
            window.setTitle("SEF System | Create Text File");
            window.setResizable(false);
            window.show();
            window.setOnCloseRequest(event -> {
                String str = closeCreate();
                if (str.equals("No"))
                    event.consume();
            });
        }catch (Exception e){
            AlertBox.showDialog(Alert.AlertType.ERROR, "ERROR", ErrorMessages.errorOpen);
        }
    }

    public String closeCreate(){
        return AlertBox.showDialogYesNo(Alert.AlertType.WARNING, "Izlaz", "Da li ste sigurni da zelite izaci?");
    }


    CopyOption[] options = new CopyOption[]{
            StandardCopyOption.REPLACE_EXISTING,
            StandardCopyOption.COPY_ATTRIBUTES
    };

    public void downloadFile() {
        String fileFromDownload = "root"+getPathFromTree();
        if(fileFromDownload.endsWith("null")){
            AlertBox.showDialog(Alert.AlertType.ERROR, "ERROR", "Izaberite fajl koji selite da skinete.");
            return;
        }
        File fro = new File(fileFromDownload);
        String pathToDownload = "/home/ognjen/Desktop/";
        File file = new File(fileFromDownload);

        if(file.isDirectory()){
            AlertBox.showDialog(Alert.AlertType.ERROR, "ERROR", ErrorMessages.errorSelectedDownload);
            return;
        }

        //enkriptuj fajl i skini ga sa sistema
        Crypto crypto = new Crypto();
        crypto.desDecrypt(fro/*, user*/);

        Path from = Paths.get(fro.toURI());
        Path to = Paths.get(pathToDownload+"/"+fro.getName());
        try {
            Files.copy(from, to, options);
            AlertBox.showDialog(Alert.AlertType.INFORMATION, "SUCCESS", ErrorMessages.successDownload);
        } catch (IOException e) {
            AlertBox.showDialog(Alert.AlertType.ERROR, "ERROR", ErrorMessages.errorDownload);
        }
        //fajl je skinut sa sistema mozes ga ponovo enkriptovati
        crypto.desEncrypt(fro, user, 0);
    }

    public void uploadFile() {
       try {
           FileChooser fileChooser = new FileChooser();
           fileChooser.setTitle("Upload File ");
           fileChooser.getExtensionFilters().addAll(
                   new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                   new FileChooser.ExtensionFilter("PDF", "*.pdf"),
                   new FileChooser.ExtensionFilter("DOCX", "*.docx"),
                   new FileChooser.ExtensionFilter("PNG", "*.png"),
                   new FileChooser.ExtensionFilter("JPEG", "*.jpeg")
           );

           Crypto crypto = new Crypto();

           File seletedFile = fileChooser.showOpenDialog(null); /*panel.getScene().getWindow()*/
           if (seletedFile == null) {
               AlertBox.showDialog(Alert.AlertType.WARNING, "WARNING", ErrorMessages.notSelectedFile);
                return;
           }

           crypto.desEncrypt(seletedFile, user, 1);

           AlertBox.showDialog(Alert.AlertType.INFORMATION, "SUCCESS", ErrorMessages.successUpload);

           updateTree(stabloDir);
       }catch (Exception e){
           //e.printStackTrace();
           AlertBox.showDialog(Alert.AlertType.WARNING, "WARNING", ErrorMessages.notSelectedFile);
       }
    }

    public void editFile(){
        //dodati jos provjere i za ostale ekstenzije
        String pathToEdit = getPathFromTree();
        File file = new File("root"+pathToEdit);
        if(file.getName().endsWith("null")){
            AlertBox.showDialog(Alert.AlertType.ERROR, "ERROR", ErrorMessages.notSelectedFile);
            return;
        }

        if(file.isDirectory() || file.getName().endsWith(".png") || file.getName().endsWith(".jpeg") || file.getName().endsWith(".docx") || file.getName().endsWith(".pdf")){
            AlertBox.showDialog(Alert.AlertType.ERROR, "ERROR", ErrorMessages.onlyTextFile);
            return;
        }

        Crypto crypto = new Crypto();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/editFile.fxml"));
            //prvo dekriptovati fajl
            crypto.desDecrypt(file/*, user*/);
            ControllerEditFile.setData(file.getAbsolutePath());
            ControllerEditFile.setUser(user);
            Parent board = loader.load();
            Scene scene = new Scene(board);
            Stage window = new Stage();
            window.setScene(scene);
            window.setTitle("SEF System | Edit Text File");
            window.setResizable(false);
            window.show();
            window.setOnCloseRequest( event -> {
                String str = closeEdit();
                if(str.equals("No"))
                    event.consume();
                crypto.desEncrypt(file, user, 0);
            });
        }catch (Exception e){
            AlertBox.showDialog(Alert.AlertType.ERROR, "ERROR", ErrorMessages.errorOpen);
        }
    }

    public String closeEdit(){
        return AlertBox.showDialogYesNo(Alert.AlertType.CONFIRMATION, "Izlaz", "Da li ste sigurni da zelite izaci?");
    }
    public void deleteFile() {
            String fileToDelete = getPathFromTree();
            File file = new File("root"+fileToDelete);
            if(file.isDirectory()){
                AlertBox.showDialog(Alert.AlertType.ERROR, "ERROR", ErrorMessages.errorSelectedDelete);
                return;
            }

        if (file.delete()) {
            AlertBox.showDialog(Alert.AlertType.INFORMATION, "SUCCESS", ErrorMessages.successDelete);
            updateTree(stabloDir);
        } else {
            AlertBox.showDialog(Alert.AlertType.ERROR, "ERROR", ErrorMessages.errorDelete);
        }
    }
    
    public void openFile() {

            String fileToOpen = getPathFromTree();
            File file = new File("root"+fileToOpen);
            if(fileToOpen == null){
                AlertBox.showDialog(Alert.AlertType.ERROR, "ERROR", ErrorMessages.errorChoose);
                return;
            }
            if (file.isDirectory()) {
                AlertBox.showDialog(Alert.AlertType.ERROR, "ERROR", "Ne mozete otvoriti direktorijum.");
                return;
            }
            Crypto crypto = new Crypto();
            crypto.desDecrypt(file/*, user*/);

//            System.out.println("Fajl je dekriptovan i vrsi se otvaranje");
//
//            if (Desktop.isDesktopSupported()) {
//                try {
//                    Desktop.getDesktop().open(/*new File("semaBaze.jpg")*/file);
//
//                    //Runtime.getRuntime().waitFor();
//
//                }catch(Exception e){
//                    System.out.println("Greska kod Desktop klase");
//                }
//            }else {
//                System.out.println("Desktop nije supported");
//            }
//
//            System.out.println("Vrsimo dekripiju");



            Process p;
        try{
                String line;
                p = Runtime.getRuntime().exec("xdg-open "+file.getAbsolutePath());
                BufferedReader input =  new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8));

                while ((line = input.readLine()) != null) {
                }
                input.close();

                while(p.isAlive()){
                   // System.out.println(p.isAlive());
                    Thread.sleep(1500); //100
                }

            }catch (Exception e){
                //e.printStackTrace();
                AlertBox.showDialog(Alert.AlertType.ERROR, "ERROR", ErrorMessages.errorOpenFile);
            }


            //enkriptujmo fajl
            crypto.desEncrypt(file, user, 0);
    }


    //kod za preuzimanje dijeljenog fajla
    public void getSharedFile(){
        String fileToGet;
        try {
            fileToGet = sharedTree.getSelectionModel().getSelectedItem().getValue();
            if (fileToGet.equals("SharedFolder")){
                AlertBox.showDialog(Alert.AlertType.ERROR, "ERROR", "Ne mozete selektovati direktorijum.");
                return;
            }

        }catch (NullPointerException e){
            AlertBox.showDialog(Alert.AlertType.ERROR, "ERROR", "Izaberite fajl koji zelite da preuzmete.");
            return;
        }
        String pathFrom = "/home/ognjen/IdeaProjects/KriptoProject/shareFolder/Folder/";
        File get = new File(pathFrom + fileToGet);

        if(get.isDirectory()){
            AlertBox.showDialog(Alert.AlertType.ERROR, "ERROR", ErrorMessages.errorSelectedDownload);
            return;
        }

        Crypto crypto = new Crypto();
        //ucitavamo svoj privatni kljuc
        File forPrivateKey = new File("/home/ognjen/IdeaProjects/KriptoProject/DigitalCertification/"+user.getUsername()+".key");
        PrivateKey privateKey = crypto.getPrivateKey(forPrivateKey.toString());

        try {
            crypto.symmetricDecrypt(privateKey, get, user);
            AlertBox.showDialog(Alert.AlertType.INFORMATION, "SUCCESS", ErrorMessages.successDownload);
            get.delete();
        }catch (BadPaddingException | InvalidKeyException e){
            AlertBox.showDialog(Alert.AlertType.ERROR, "Fajl nije za vas", "Korisnik nije podijeli ovaj fajl sa vama");
        }

        updateTreeShareFolder(sharedTree);
        updateTree(stabloDir);
    }

    public void shareFile(){
        String fileToShare = getPathFromTree();
        String pathFrom = "/home/ognjen/IdeaProjects/KriptoProject/root";
        File file1 = new File(pathFrom+fileToShare);


        if (file1.isDirectory()) {
            AlertBox.showDialog(Alert.AlertType.ERROR, "ERROR", ErrorMessages.errorSelectedDownload);
            return;
        }

        //Nadji usera kome hocemo da podijelimo file
        String userToShare = choiceUser.getValue();

        if(userToShare == null){
            AlertBox.showDialog(Alert.AlertType.ERROR, "ERROR", "Morate da izaberete korisnika kome zelite da podijelite fajl.");
            return;
        }


        //test
        Crypto crypto = new Crypto();
        File f = new File("/home/ognjen/IdeaProjects/KriptoProject/DigitalCertification/"+userToShare+".pem");
        X509Certificate cert = crypto.getCertificate(f);
        ControllerLogin cl = new ControllerLogin();

        if (!cl.provjeraSertifikata(userToShare, 0)){
           AlertBox.showDialog(Alert.AlertType.ERROR, "ERROR", "Digitalni sertifikat korisnika: "+userToShare+" nije validan.");
           return;
        }

        PublicKey publicKey = cert.getPublicKey();

        try {

            crypto.symmetricEncrypt(publicKey, file1, user);

            AlertBox.showDialog(Alert.AlertType.INFORMATION, "SUCCESS", ErrorMessages.successDownload);
        } catch (Exception e) {
            //System.out.println("Exception kod copy");
            //e.printStackTrace();
            AlertBox.showDialog(Alert.AlertType.ERROR, "ERROR", ErrorMessages.errorDownload);
        }
        updateTreeShareFolder(sharedTree);
    }

    public void allFilesInSharedFolder(TreeItem<String> root){
        File file = new File("/home/ognjen/IdeaProjects/KriptoProject/shareFolder/Folder");
        File[] files = file.listFiles();
        if(files != null)
            for(File i : files){
                TreeItem<String> node;
                if(i.getName().endsWith(".txt")){
                    node = new TreeItem<>(i.getName(), new ImageView(imgTxt));
                }else if(i.getName().endsWith(".docx")){
                    node = new TreeItem<>(i.getName(), new ImageView(imgDocx));
                }else if(i.getName().endsWith(".png")){
                    node = new TreeItem<>(i.getName(), new ImageView(imgPng));
                }else if(i.getName().endsWith(".jpeg")){
                    node = new TreeItem<>(i.getName(), new ImageView(imgJpeg));
                }else if(i.getName().endsWith(".pdf")){
                    node = new TreeItem<>(i.getName(), new ImageView(imgPdf));
                }else {
                    node = new TreeItem<>(i.getName(), new ImageView(imgNo));
                }
                root.getChildren().add(node);
            }
    }

    public void createTreeViewForShareFolder(){
        TreeItem<String> root = new TreeItem<>("SharedFolder", new ImageView(imgFolder));
        allFilesInSharedFolder(root);

        root.setExpanded(true);
        sharedTree.setRoot(root);
    }

    public void updateTreeShareFolder(TreeView<String> tree){
        tree.getRoot().getChildren().clear();
        allFilesInSharedFolder(tree.getRoot());
    }


    public void logOut(){

        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/login.fxml"));
            Parent board = loader.load();
            Scene scene = new Scene(board);
            Stage window = new Stage();
            window.setScene(scene);
            window.setTitle("SEF System | Login File");
            window.setResizable(false);
            window.show();
            window.setOnCloseRequest(event -> {
                String str = closeLogout();
                if (str.equals("No"))
                    event.consume();
            });
        }catch (Exception e){
            AlertBox.showDialog(Alert.AlertType.ERROR, "ERROR", ErrorMessages.errorLogout);
        }finally {
            Stage stage = (Stage) logout.getScene().getWindow();
            stage.close();
        }
    }

    public String closeLogout(){
        return AlertBox.showDialogYesNo(Alert.AlertType.WARNING, "Izlaz", "Da li ste sigurni da zelite izaci?");
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        createTreeViewForShareFolder();
        createTreeView(); //kreiraj stablo direktorijuma
        getAllUser(choiceUser);
    }
}
