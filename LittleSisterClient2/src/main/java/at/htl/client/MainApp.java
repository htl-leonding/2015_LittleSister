package at.htl.client;

import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {     
        Parent root;
        FXMLLoader loader = new FXMLLoader();       
        loader.setLocation(getClass().getResource("/fxml/MainView.fxml"));
        loader.setResources(ResourceBundle.getBundle("fa.fontawesome"));
        root = loader.load();
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/stylesNew.css");
        
        stage.setTitle("LittleSister Client");
        stage.setResizable(false);
        stage.setScene(scene);    
        stage.show();
        
//        ClipboardTracker cbt = new ClipboardTracker();
//        cbt.start();  
//        for (int i = 0; i < 10; i++) {
//            System.out.println(cbt.getClipboardData());
//            Thread.sleep(10000);
//        }
           
//        List<String> endings = new LinkedList<>();
//        endings.add("txt");
//        endings.add("txt1");
//        for (int i = 0; i < 15; i++) {
//            System.out.println(LineCountTracker.getLines("C:\\Users\\Marc\\Desktop\\test", endings));
//            Thread.sleep(1000);
//        }
        
    }
    
    public static void main(String[] args) {
        launch(args);
    }

}
