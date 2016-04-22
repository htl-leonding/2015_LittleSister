package at.htl.server;

import at.htl.controller.MainViewController;
import at.htl.entity.Session;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainApp extends Application {
    
    static Logger logger = LoggerFactory.getLogger(MainApp.class);
    
    @Override
    public void start(Stage stage) throws Exception {
        logger.info("System;Application started.");
        Parent root; 
        FXMLLoader loader = new FXMLLoader();
        loader.setResources(ResourceBundle.getBundle("fa.fontawesome"));
        loader.setLocation(getClass().getResource("/fxml/SettingsView.fxml"));
        root = loader.load();
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(true);
        stage.setTitle("LittleSister Server");
        stage.show();   
    }
    
    public void showMainApplication(Session session, int port) throws IOException {
        Stage stage = new Stage(StageStyle.DECORATED);
        Parent root;
        FXMLLoader loader = new FXMLLoader();
        loader.setResources(ResourceBundle.getBundle("fa.fontawesome"));
        loader.setLocation(getClass().getResource("/fxml/MainView.fxml"));

        root = loader.load();
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add("styles/Styles.css");
        stage.setScene(scene);
        stage.setResizable(true);
        MainViewController controller = loader.<MainViewController>getController();
        controller.initializeData(session, port);     
        stage.setTitle("LittleSister Server");
        
        stage.show();
        
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                event.consume();
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Exiting application");
                alert.setHeaderText("Warning!");
                alert.setContentText("Warning you are about to exit this application!");
                Optional<ButtonType> result = alert.showAndWait();
                if(result.get() == ButtonType.OK){
                    stage.close();
                    logger.info("System;Application closed.");
                }
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
