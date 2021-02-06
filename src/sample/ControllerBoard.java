package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerBoard implements Initializable {

    //deklaracija promjenljivih sa board.fxml
    @FXML private TreeView<String> stabloDir;

    //globalne promjenljive za ControllerBoard klasu

    //ucitaj slike za treeItem
    private final Image imgFolder = new Image(getClass().getResourceAsStream("icons/folder.png"));
    private final Image imgTxt = new Image(getClass().getResourceAsStream("icons/txt.png"));
    private final Image imgPng = new Image(getClass().getResourceAsStream("icons/png.png"));
    private final Image imgJpeg = new Image(getClass().getResourceAsStream("icons/jpeg.png"));
    private final Image imgDocx = new Image(getClass().getResourceAsStream("icons/docx.png"));
    private final Image imgPdf = new Image(getClass().getResourceAsStream("icons/pdf.png"));
    private final Image imgNo = new Image(getClass().getResourceAsStream("icons/close.png"));

    private static User user = new User(); //static zbog ponovnog inicanciranja u memoriji da zadrzi staru vrijednost

    public void initData(User oldUser){
       user = oldUser;
    }

    public void createTreeView() {

        TreeItem<String> root = new TreeItem<>(user.getUsername(), new ImageView(imgFolder));

        getAllDirAndFiles(user.getPath()+user.getUsername(), root);

        root.setExpanded(true);
        stabloDir.setRoot(root);
    }

    private void getAllDirAndFiles(String path, TreeItem<String> root){

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

    public String getPathFromTree(){

        StringBuilder pathBuilder = new StringBuilder();
        for (TreeItem<String> item = stabloDir.getSelectionModel().getSelectedItem(); item != null ; item = item.getParent()) {

            pathBuilder.insert(0, item.getValue());
            pathBuilder.insert(0, "/");
        }
        String path = pathBuilder.toString();
        if(path.equals(""))
            path = null;

        return path;
    }

public void openFile(){
        String fileToOpen = getPathFromTree();
        File file = new File("root"+fileToOpen);
        System.out.println(file.getAbsolutePath());


        if(fileToOpen == null){
            System.out.println("Izaberite file koji zelite da otvorite");
            return;
        }

        String command = "xdg-open "+file.getAbsolutePath();
        try{
            Runtime.getRuntime().exec(command);
        }catch (Exception e){
            e.printStackTrace();
        }

        //File file = new File("root"+fileToOpen);

}

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        createTreeView(); //kreiraj stablo direktorijuma
    }
}