package at.htl.controller;

import at.htl.entity.ClientSettings;
import at.htl.entity.Message;
import at.htl.entity.Pupil;
import at.htl.entity.Session;
import at.htl.server.MessageRepository;
import at.htl.server.PupilRepository;
import at.htl.server.Server;
import at.htl.utils.MyUtils;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcons;
import java.io.File;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.Duration;
import org.slf4j.LoggerFactory;

public class MainViewController implements Initializable, Observer {
    @FXML
    private ToggleButton btnStartServer;
    @FXML
    private Label lblServerPort;
    @FXML
    private Label lblServerIP;
    @FXML
    private ToggleButton btnStartTest;
    @FXML
    private ListView<Pupil> lvPupils;
    @FXML
    private ImageView ivScreenshot;
    @FXML
    private Label lblSelectedPupil;
    @FXML
    private LineChart<?, ?> lcLineCount;
    @FXML
    private GridPane gpScreen;
    @FXML
    private ToggleButton btnTogglePatrolMode;
    @FXML
    private Text latestLog;
    @FXML
    private ChoiceBox<Message.Tag> chobFilter;
    @FXML
    private TextArea taLogs;
    @FXML
    private Slider sldInterval;
    @FXML
    private TextField tfFileEndings;
    @FXML
    private Button btnSavePref;
    
    // =============================
    // --         FIELDS          --  
    // =============================   
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(MainViewController.class);

    private Server server;
    private Session session;

    private Pupil selectedPupil;

    private boolean isSessionStarted = false;

    private Timeline patrolTimeline = null;

    private List<String> loglist;
    private ObservableList<String> logs;
    @FXML
    private Slider sldIntervalUser;
    @FXML
    private CheckBox cbSprachunterricht;
    @FXML
    private CheckBox cbProgrammierunterrricht;
    @FXML
    private CheckBox cbDoc;
    @FXML
    private CheckBox cbDocx;
    @FXML
    private CheckBox cbTxt;
    @FXML
    private CheckBox cbOdt;
    @FXML
    private CheckBox cbOdf;
    @FXML
    private CheckBox cbJava;
    @FXML
    private CheckBox cbCs;
    @FXML
    private CheckBox cbHtml;
    @FXML
    private CheckBox cbCss;
    @FXML
    private CheckBox cbXhtml;
    @FXML
    private CheckBox cbSql;
    @FXML
    private CheckBox cbFxml;

    
    // =============================
    // --     INITIALIZATION      --  
    // ============================= 
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void initializeData(Session session, int port) {
        this.session = session;
        server = new Server(port);
        loglist = new LinkedList<String>();
        initializeUI();
    }

    public void initializeUI() {
        //TOP Anchorpane
        ivScreenshot.fitWidthProperty().bind(gpScreen.widthProperty());
        ivScreenshot.fitHeightProperty().bind(gpScreen.heightProperty());
        btnStartTest.setDisable(true);
        lblServerPort.setText("" + server.getPort());
        try {
            lblServerIP.setText(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException ex) {
            Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        btnStartTest.setDisable(true);

        //LEFT Anchorpane
        lvPupils.setItems((ObservableList<Pupil>) FXCollections.observableArrayList(PupilRepository.getInstance().getPupils()));
        lvPupils.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Pupil>() {
            @Override
            public void changed(ObservableValue<? extends Pupil> observable, Pupil oldValue, Pupil newValue) {
                ClientSettings newSettingsForSelectedPupil = new ClientSettings(null, 2000);
                ClientSettings newSettingsForUnselectedPupil = new ClientSettings(null, session.getClientSettings().getLogInterval());
                server.writeToPupil(oldValue, new Message(Message.Tag.SETTINGS_CHANGED, newSettingsForUnselectedPupil));
                server.writeToPupil(newValue, new Message(Message.Tag.SETTINGS_CHANGED, newSettingsForSelectedPupil));

                //btnForceScreenshot.setDisable(!PupilRepository.getInstance().isPupilConnected(newValue));
                updateUI();
            }
        });

        lvPupils.setCellFactory(new Callback<ListView<Pupil>, ListCell<Pupil>>() {
            @Override
            public ListCell<Pupil> call(ListView<Pupil> param) {
                return new PupilCell();
            }
        });

        ivScreenshot.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
            //TODO
            /*
             Pupil selectedPupil = (Pupil) lvPupils.getSelectionModel().getSelectedItem();
             Message latestScreenshot = MessageRepository.getInstance().getLatestMessageForTag(selectedPupil, Message.Tag.LOG_ENTRY);
             byte[] screenshot = latestScreenshot.getLogEntry().getScreenshot();
             try {
             if (screenshot != null) {
             Desktop.getDesktop().open(new File(MyUtils.getAbsJpgPath(System.getProperty("user.dir"), latestScreenshot, false)));
             }
             } catch (IOException ex) {
             Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
             }*/
            event.consume();
        });
        chobFilter.getItems().setAll(Message.Tag.values());
        chobFilter.getItems().add(0, null);
        chobFilter.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                refreshLogList(chobFilter.getItems().get(number2.intValue()));
            }
        });
        
        //Einstellungen
        sldInterval.setValue(session.getClientSettings().getLogInterval()/1000);
        tfFileEndings.setText(session.getClientSettings().getFileEndings().toString().substring(1, session.getClientSettings().getFileEndings().toString().length() - 1));
    }
    
    // =============================
    // --         UPDATES         --  
    // =============================
    
    @Override
    public void update(Observable o, Object arg) {
        Pupil pupil = (Pupil) arg;
        Message msg = MessageRepository.getInstance().getLatestMessageForPupil(pupil);
        switch (msg.getTag()) {
            case CONNECT:
                logger.info(pupil.getMatrikelNr() + "_" + pupil.getName() + ";Connected.");
                server.writeToPupil(pupil, new Message(Message.Tag.SETTINGS_CHANGED, session.getClientSettings()));
                forceListRefresh(lvPupils);
                if (isSessionStarted) {
                    server.writeToPupil(pupil, new Message(Message.Tag.TEST_STARTED));
                }
                addLog(pupil.getMatrikelNr() + ": " + pupil.getName() + " has connected.", Color.GREEN);
                break;
            case DISCONNECT:
                logger.info(pupil.getMatrikelNr() + "_" + pupil.getName() + ";Disconnected.");
                PupilRepository.getInstance().putSocket(msg.getPupil(), null);
                forceListRefresh(lvPupils);
                addLog(pupil.getMatrikelNr() + ": " + pupil.getName() + " has disconnected.", Color.RED);
                break;
            case LOG_ENTRY:
                /*int logEntryCount = LogEntryRepository.getInstance().getAllLogEntries().size();
                
                 if (logEntryCount > 1) {
                 LogEntryRepository.getInstance().getAllLogEntries().get(logEntryCount - 2).setImg(null);
                 }*/
                logger.info(pupil.getMatrikelNr() + "_" + pupil.getName() + ";Screenshot received");
                logger.info(pupil.getMatrikelNr() + "_" + pupil.getName() + ";Clipboard data received: " + msg.getLogEntry().getClipboardData());
                if (pupil.equals(selectedPupil)) {
                    //Image screenshot = new Image("file:///" + MyUtils.getAbsJpgPath(System.getProperty("user.dir"), latestMessage, false));
                    //ivScreenshot.setImage(screenshot);
                }
                MyUtils.writeScreenshotForMessage(session.getLogDirectory(), msg);
                updateUI();
                break;
            case TESTFILES:
                byte[] data = MyUtils.readBytesFromAFile(new File(session.getRelevantFilesPath()));
                if (data != null) {
                    server.writeToPupil(pupil, new Message(Message.Tag.TESTFILES, data));
                    logger.info(pupil.getMatrikelNr() + "_" + pupil.getName() + ";sent testfiles");
                }
                addLog(pupil.getMatrikelNr() + ": " + pupil.getName() + " has downloaded the testfiles", Color.GREEN);

            default:
                logger.info(pupil.getMatrikelNr() + "_" + pupil.getName() + ";Unidentifiable message");
                break;
        }
    }

    public void updateUI() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                selectedPupil = null;
                for (Pupil pup : PupilRepository.getInstance().getPupils()) {
                    if (pup.equals(lvPupils.getSelectionModel().getSelectedItem())) {
                        selectedPupil = pup;
                        break;
                    }
                }
                if (selectedPupil != null) {
                    lblSelectedPupil.setText(selectedPupil.getKatNr() + " - " + selectedPupil.getName());
                    Message latestMessage = MessageRepository.getInstance().getLatestMessageForTag(selectedPupil, Message.Tag.LOG_ENTRY);
                    Image screenshot = null;
                    if (latestMessage != null) {
                        if (session.getLogDirectory().equals("")) {
                            logger.info(latestMessage.getPupil().getMatrikelNr() + "_" + latestMessage.getPupil().getName() + ";file:///" + MyUtils.getAbsJpgPath(System.getProperty("user.dir"), latestMessage, false));
                            screenshot = new Image("file:///" + MyUtils.getAbsJpgPath(System.getProperty("user.dir"), latestMessage, false));
                        } else {
                            logger.info(latestMessage.getPupil().getMatrikelNr() + "_" + latestMessage.getPupil().getName() + ";file:///" + MyUtils.getAbsJpgPath(session.getLogDirectory(), latestMessage, false));
                            screenshot = new Image("file:///" + MyUtils.getAbsJpgPath(session.getLogDirectory(), latestMessage, false));
                        }
                    }
                    ivScreenshot.setImage(screenshot);

                    refreshLogList(chobFilter.getSelectionModel().getSelectedItem());

                } else {
                    lblSelectedPupil.setText("");
                    ivScreenshot.setImage(null);
                }
            }
        });
    }

    private void refreshLogList(Message.Tag tag) {
        taLogs.setText("");
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        if (selectedPupil != null) {
            for (Message m : MessageRepository.getInstance().getMessageForPupil(selectedPupil)) {
                if (m.getTag() == tag || tag == null) {
                    String message = "[Tag: " + m.getTag() + "] [Timestamp: " + dateFormat.format(m.getTimestamp()) + "]";
                    if (m.getTag().equals(Message.Tag.LOG_ENTRY)) {
                        taLogs.appendText(message + " a screenshot was received");
                    } else {
                        taLogs.appendText(message);
                    }
                    taLogs.appendText("\n");
                }
            }
        }

    }
    
    
    public void addLog(String log, Color c) {
        latestLog.setFill(c);
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        latestLog.setText(dateFormat.format(cal.getTime()) + " - " + log);
    }

    @FXML
    private void btnStartServer_Clicked(MouseEvent event) {
        if (!server.isRunning()) {
            MessageRepository.getInstance().addObserver(this);
            Thread thread = new Thread(server);
            thread.setDaemon(true);
            thread.start();
            btnStartServer.setText("Stoppe Server");
            btnStartTest.setDisable(false);
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Stopping server");
            alert.setHeaderText("Warnung!");
            alert.setContentText("Sie sind dabei den Server zu stoppen!");
            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == ButtonType.OK) {

                btnStartServer.setText("Starte Server");
                server.stop();
                forceListRefresh(lvPupils);
                btnStartTest.setDisable(true);
                //timer.cancel();
                //timer = new Timer();
            }
        }
    }

    @FXML
    private void btnStartTest_Clicked(MouseEvent event) {
        if (!isSessionStarted) {
            server.writeToAllPupils(new Message(Message.Tag.TEST_STARTED));
            isSessionStarted = true;
            btnStartTest.setText("Stoppe Test");
        } else if (isSessionStarted) {
            btnStartTest.setText("Starte Test");
            server.writeToAllPupils(new Message(Message.Tag.TEST_ENDED));
            isSessionStarted = false;
        }
    }

    @FXML
    private void btnTogglePatrolMode(MouseEvent event) {
        if (btnTogglePatrolMode.isSelected()) {
            patrolTimeline = new Timeline(new KeyFrame(
                    Duration.seconds(3), new EventHandler<ActionEvent>() {
                public void handle(ActionEvent event1) {
                    lvPupils.getSelectionModel().select(getNextOnlinePupilIndex());
                }
            }));
            patrolTimeline.setCycleCount(Timeline.INDEFINITE);
            patrolTimeline.play();
        } else if (patrolTimeline != null) {
            patrolTimeline.stop();
        }
    }

    private int getNextOnlinePupilIndex() {
        int ind = 0;
        int cnt = 1;
        int size = lvPupils.getItems().size();
        for (int i = 0; i < size; i++, cnt++) {
            if (lvPupils.getSelectionModel().getSelectedIndex() + cnt >= size) {
                cnt -= size;
            }
            if (PupilRepository.getInstance().isPupilConnected(lvPupils.getItems().get(lvPupils.getSelectionModel().getSelectedIndex() + cnt))) {
                ind = lvPupils.getSelectionModel().getSelectedIndex() + cnt;
                break;
            }
        }
        return ind;
    }

    @FXML
    private void updatePref(ActionEvent event) {

    }

    @FXML
    private void btnSavePref_Clicked(MouseEvent event) {
        List<String> newFileEndings = Arrays.asList(tfFileEndings.getText().split(","));
        session.setClientSettings(new ClientSettings(newFileEndings, (int)sldInterval.getValue() * 1000));
        ClientSettings newSettings = session.getClientSettings();
        server.writeToAllPupils(new Message(Message.Tag.SETTINGS_CHANGED, newSettings));
    }

    static class PupilCell extends ListCell<Pupil> {

        Label label = new Label();
        Label iconLabel = new Label();
        GridPane grid = new GridPane();

        public PupilCell() {
            super();
            configureIcon();
            configureGrid();
            addControlsToGrid();
        }

        private void configureGrid() {
            grid.setHgap(10);
            //grid.setPadding(new Insets(0, 10, 0, 10));
        }

        private void addControlsToGrid() {
            grid.add(label, 1, 0);
            grid.add(iconLabel, 0, 0);
        }

        public void configureIcon() {
            iconLabel = GlyphsDude.createIconLabel(FontAwesomeIcons.CHILD, null, "22px", "22px", ContentDisplay.RIGHT);
        }

        @Override
        public void updateItem(Pupil item, boolean empty) {
            super.updateItem(item, empty);
            Platform.runLater(new Runnable() {

                @Override
                public void run() {
                    label.setText(item != null ? item.getName() : "<null>");
                    iconLabel.getStyleClass().clear();
                    if (item != null) {
                        if (PupilRepository.getInstance().isPupilConnected(item)) {
                            iconLabel.getStyleClass().add("online-label");
                        } else {
                            iconLabel.getStyleClass().add("offline-label");
                        }
                        setGraphic(grid);
                    }
                }
            });
        }
    }

    private <T> void forceListRefresh(ListView<T> lsv) {
        ObservableList<T> items = lsv.<T>getItems();
        lsv.<T>setItems(null);
        lsv.<T>setItems(items);
    }
}
