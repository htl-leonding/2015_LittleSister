package at.htl.controller;

import at.htl.entity.ClientSettings;
import at.htl.entity.Session;
import at.htl.server.MainApp;
import at.htl.server.PupilRepository;
import at.htl.utils.MyUtils;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class SettingsViewController implements Initializable {
    @FXML
    private Button btnSave;
    @FXML
    private TextField tfFileEndings;
    @FXML
    private TextField tfRelevantFiles;
    @FXML
    private TextField tfSavePath;
    @FXML
    private TextField tfPort;
    @FXML
    private ComboBox<String> cbSchoolClass;

    private final String regexNotEmpty = ".{1,}";
    private final String regexFileEndings = "([a-zA-Z]*,)*[a-zA-Z]+";
    private final String regexPort = "1[1-9][0-9]{2}|10[3-9][0-9]|102[4-9]|[2-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5]";

    private boolean isValid;
    
    private String pupilCsvPath = System.getProperty("user.dir") + File.separator + "pupils.csv";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        List<String> schoolClasses = MyUtils.getSchoolClassesFromCsv(pupilCsvPath);
             tfFileEndings.setText("txt,java");
        
        if (!schoolClasses.isEmpty()) {
            cbSchoolClass.getItems().addAll(schoolClasses);
            cbSchoolClass.getSelectionModel().selectFirst();
        } else {
            //TODO: Exceptionhandling
        }

        validate();
        
        tfFileEndings.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (!newValue) {
                validateTextField(tfFileEndings, regexFileEndings);
            }
        });

        tfRelevantFiles.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            validateTextField(tfRelevantFiles, regexNotEmpty);
        });

        tfSavePath.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            validateTextField(tfSavePath, regexNotEmpty);
        });

        cbSchoolClass.valueProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            validateComboBox(cbSchoolClass);
        });
        
        Properties props = MyUtils.getProperties();
        if(props != null){
            tfFileEndings.setText(props.getProperty("fileEndings"));
            tfRelevantFiles.setText(props.getProperty("testFilesPath"));
            tfSavePath.setText(props.getProperty("savePath"));
            tfPort.setText(props.getProperty("port"));
        }

    }

    private boolean validate() {
        validateTextField(tfFileEndings, regexFileEndings);
        validateTextField(tfRelevantFiles, ".*");
        validateTextField(tfSavePath, ".*");
        validateTextField(tfPort, regexPort);
        validateComboBox(cbSchoolClass);
        return true;
    }

    private boolean validateTextField(TextField tf, String regex) {
        if (!tf.getText().matches(regex)) {
            tf.getStyleClass().removeAll("text-field-correct");
            tf.getStyleClass().add("text-field-incorrect");
            isValid = false;
            return false;
        } else {
            tf.getStyleClass().removeAll("text-field-incorrect");
            tf.getStyleClass().add("text-field-correct");
            return true;
        }
    }

    private boolean validateComboBox(ComboBox cb) {
        if (cb.getSelectionModel().isEmpty()) {
            cb.getStyleClass().removeAll("combo-box-correct");
            cb.getStyleClass().add("combo-box-incorrect");
            isValid = false;
            return false;
        } else {
            cb.getStyleClass().removeAll("combo-box-incorrect");
            cb.getStyleClass().add("combo-box-correct");
            return true;
        }
    }

    @FXML
    private void btnRelevantFiles_Clicked(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ZIP", "*.zip"));
        fileChooser.setTitle("Pfad der Test-relevanten Dateien auswählen");
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            tfRelevantFiles.setText(file.getAbsolutePath());
        }
    }

    @FXML
    private void btnSavePath_Clicked(MouseEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Pfad für das Speichern der Aufzeichnungen auswählen");
        File file = chooser.showDialog(new Stage());
        if (file != null) {
            tfSavePath.setText(file.getAbsolutePath());
        }
    }

    @FXML
    private void btnSave_Clicked(MouseEvent event) throws IOException {
        isValid = true;
        validate();
        if (!isValid) {

        } else {
            Session session = new Session();
            ClientSettings settings = new ClientSettings();
            session.setName("Test");
            session.setSchoolClass(cbSchoolClass.selectionModelProperty().getValue().getSelectedItem());
            session.setRelevantFilesPath(tfRelevantFiles.getText());     
            settings.setFileEndings(Arrays.asList(tfFileEndings.getText().split(",")));
            settings.setLogInterval(4500);
            session.setClientSettings(settings);
            session.setLogDirectory(tfSavePath.getText());
            
            MyUtils.writeProperties(tfFileEndings.getText(), tfRelevantFiles.getText(), tfSavePath.getText(), tfPort.getText());
            
            PupilRepository.getInstance().insertAll(MyUtils.getPupilsFromCsv(pupilCsvPath, session.getSchoolClass()));
            MainApp main = new MainApp();
            main.showMainApplication(session, Integer.parseInt(tfPort.getText()));
            Stage stage = (Stage) btnSave.getScene().getWindow();
            stage.close();
        }     
    }

    @FXML
    private void btnCancel_Clicked(MouseEvent event) {
        
    }

    @FXML
    private void btnSchoolClass_Clicked(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            List<String> schoolClasses = MyUtils.getSchoolClassesFromCsv(file.getPath());
            pupilCsvPath = file.getPath();
            if (!schoolClasses.isEmpty()) {
                cbSchoolClass.getItems().clear();
                cbSchoolClass.getItems().addAll(schoolClasses);
                cbSchoolClass.getSelectionModel().selectFirst();
            } else {

            }
        }
    }
}
