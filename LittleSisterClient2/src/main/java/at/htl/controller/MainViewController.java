package at.htl.controller;

import at.htl.entity.Pupil;
import at.htl.client.Client;
import at.htl.entity.Message;
import java.io.File;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class MainViewController implements Initializable, Observer {

    @FXML
    private TextField tfPupilMatrikelNr;
    @FXML
    private TextField tfServerIp;
    @FXML
    private TextField tfServerPort;
    @FXML
    private TextField tfProjectDirectory;
    @FXML
    private Button btnConnect;
    @FXML
    private Button btnGetTestfiles;

    private Client client;
    public Pupil pupil;

    List<String> files = new LinkedList<>();
    List<String> endings = new LinkedList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tfPupilMatrikelNr.setText("in110084");
        tfServerIp.setText("localhost");
    }

    @FXML
    private void btnProjectDirectoryChooser_Clicked(MouseEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Pfad des Test-Projektverzeichnisses");
        File file = chooser.showDialog(new Stage());
        if (file != null) {
            tfProjectDirectory.setText(file.getAbsolutePath());
        }
    }

    @FXML
    private void btnConnectToServer_Clicked(MouseEvent event) {
        if (client == null || !client.isConnected()) {
            //Pupil
            String matrikelNr = tfPupilMatrikelNr.getText().trim().toUpperCase();
            String lastName = "";
            int katNr = 0;
            pupil = new Pupil(matrikelNr, lastName, katNr);
            client = new Client(pupil);

            String workingDir = tfProjectDirectory.getText();

            client.setWorkingDirectory(workingDir);
            client.addObserver(this);

            InetSocketAddress serverAddress = new InetSocketAddress(tfServerIp.getText(), Integer.parseInt(tfServerPort.getText()));

            if (client.connect(serverAddress)) {
                //connectedButton.getStyleClass().add("stopButton");
                //updateButton();
            } else {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        //showErrorAlreadyConnectedOrServerNotRunning();
                    }
                });
            }
        } else {
            client.disconnect();
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        Alert alert = new Alert(AlertType.INFORMATION);

        Message.Tag tag = (Message.Tag) arg;
        switch (tag) {
            case CONNECTING_FAILED: {
                btnConnect.setText("Verbinden");
                alert.setTitle("Anmeldung fehlgeschlagen!");
                alert.setHeaderText("Die Anmeldung beim Server ist fehlgeschlagen");
                alert.setContentText("Mögliche Gründe: \n 1. Matrikelnummer existiert nicht.\n 2. Ein User mit dieser Matrikelnummer ist bereits eingeloggt");
                alert.showAndWait();
            }
            break;
            case CONNECTING_SUCCESSFUL: {
                btnConnect.setText("Abmelden");
            }
            break;
            case TEST_STARTED: {
                alert.setTitle("Test gestartet!");
                alert.setHeaderText("Der Test wurde gestartet");
                alert.setContentText("Viel erfolg " + pupil.getName());
                alert.showAndWait();
            }
            break;
            case TEST_ENDED: {
                alert.setTitle("Test beendet!");
                alert.setHeaderText("Der Test wurde beendet");
                alert.setContentText("");
                alert.showAndWait();
            }
            break;

        }
        updateConnectButton();
    }

    @FXML
    private void btnGetTestfiles_Clicked(MouseEvent event) {
        client.sendTestfileRequest();
    }

    private void updateConnectButton() {
        if (client.isConnected()) {
            btnConnect.setText("Abmelden");
        } else {
            btnConnect.setText("Verbinden");
        }
    }
}
