package at.htl.client;

import at.htl.entity.LogEntry;
import at.htl.entity.Message;
import static at.htl.entity.Message.Tag.LOG_ENTRY;
import at.htl.entity.Pupil;
import at.htl.entity.ClientSettings;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;

public class Client extends Observable {

    public static int TIMEOUT = 5000;

    private final Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;

    private Thread inputThread;
    private Thread logEntryThread;

    private Pupil pupil;
    private String workingDirectory;
    private List<String> fileEndings;
    private int logInterval;

    private boolean connected = false;
    private boolean sessionRunning = false;

    public ClipboardTracker clipboardTracker;
    public ScreenshotTracker screenshotTracker;
    public LineCountTracker lineCountTracker;

    public boolean isConnected() {
        return connected;
    }

    public boolean isSessionRunning() {
        return sessionRunning;
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    public Client(Pupil pupil) {
        this.pupil = pupil;
        socket = new Socket();
    }

    public boolean connect(InetSocketAddress endpoint) {
        try {
            socket.connect(endpoint, TIMEOUT);

            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush();
            inputStream = new ObjectInputStream(socket.getInputStream());

            System.out.println("[CLIENT][connect][   ] sending CONNECTION message...");
            outputStream.writeObject(new Message(Message.Tag.CONNECT, pupil));
            System.out.println("[CLIENT][connect][SCS] CONNECTION message sent!");

            inputThread = new Thread(new InputReader());
            inputThread.setDaemon(true);
            System.out.println("[CLIENT][connect][   ] starting InputReader...");
            inputThread.start();

        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            connected = false;
            setChanged();
            notifyObservers(Message.Tag.CONNECTING_FAILED);
            return false;
        }

        return true;
    }

    public void disconnect() {
        inputThread.interrupt();
        if (logEntryThread != null) {
            logEntryThread.interrupt();
        }

        try {
            System.out.println("[INPUT_READER][DISCONNECT][   ] sending DISCONNECT message...");
            outputStream.writeObject(new Message(Message.Tag.DISCONNECT, pupil));
            System.out.println("[INPUT_READER][DISCONNECT][SCS] DISCONNECT message sent!");
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

        connected = false;
        setChanged();
        notifyObservers(Message.Tag.DISCONNECT);
    }

    public void sendTestfileRequest() {
        if (isConnected()) {
            try {
                System.out.println("[INPUT_READER][SEND_TESTFILE_REQUEST][   ] sending TESTFILE message...");
                outputStream.writeObject(new Message(Message.Tag.TESTFILES));
                System.out.println("[INPUT_READER][SEND_TESTFILE_REQUEST][SCS] TESTFILE message sent!");
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private class InputReader implements Runnable {

        @Override
        public void run() {
            System.out.println("[INPUT_READER][run][SCS] InputReader started!");
            while (socket.isConnected()) {
                try {
                    System.out.println("[INPUT_READER][run][   ] waiting for incoming Message...");
                    Message message = (Message) inputStream.readObject();
                    System.out.println("[INPUT_READER][run][SCS] message received - {" + message.getTag() + "}");
                    switch (message.getTag()) {
                        case CONNECTING_FAILED: {
                            //TODO
                            break;
                        }
                        case CONNECTING_SUCCESSFUL: {
                            pupil = message.getPupil();
                            connected = true;
                            break;
                        }
                        case TEST_STARTED: {
                            logEntryThread = new Thread(new LogEntrySender());
                            logEntryThread.setDaemon(true);
                            logEntryThread.start();
                            sessionRunning = true;
                            //Notify observers
                            break;
                        }
                        case TEST_ENDED: {
                            sessionRunning = false;
                            break;
                        }
                        case SETTINGS_CHANGED: {
                            ClientSettings settings = message.getSettings();
                            System.out.println("new loginterval: " + message.getSettings().getLogInterval());
                            //TODO auf null prüfen
                            if (settings.getFileEndings() != null) {
                                fileEndings = settings.getFileEndings();
                            }
                            logInterval = settings.getLogInterval();
                            break;
                        }
                        case TESTFILES: {
                            byte[] file = message.getFile();
                            FileOutputStream fos = new FileOutputStream(System.getProperty("user.home") + File.separator +"Desktop"+File.separator+"Unterlagen.zip");
                            fos.write(file);
                            fos.close();
                            break;
                        }
                    }
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            setChanged();
                            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + message.getTag());
                            notifyObservers(message.getTag());
                        }
                    });
                } catch (IOException ex) {
                    connected = false;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            setChanged();
                            notifyObservers(Message.Tag.CONNECTION_LOST);
                        }
                    });
                    System.out.println("[INPUT_READER][run][ERR] lost connection to server!!!");
                    break;
                } catch (ClassNotFoundException ex) {
                    System.out.println("[INPUT_READER][run][ERR] serializeable classes are not equal!!!"); //TODO bessere exception
                }
            }

            connected = false;
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    setChanged();
                    notifyObservers(Message.Tag.CONNECTION_LOST);
                }
            });
        }
    }

    private class LogEntrySender implements Runnable {

        @Override
        public void run() {
            ScreenshotTracker screenshot = new ScreenshotTracker();
            ClipboardTracker clipboardTracker = new ClipboardTracker(logInterval);
            clipboardTracker.start();

            System.out.println("[LOG_ENTRY_SENDER][run][SCS] initialized Trackers!");
            while (isConnected() && isSessionRunning()) {
                System.out.println(clipboardTracker.getClipboardData());
                LogEntry logEntry = new LogEntry(screenshot.getScreenShot(), clipboardTracker.getClipboardData(), LineCountTracker.getLines(workingDirectory, fileEndings));
                try {
                    System.out.println("[LOG_ENTRY_SENDER][run][   ] sending logEntry...");
                    Message message = new Message(LOG_ENTRY, logEntry);
                    message.setPupil(pupil);
                    outputStream.writeObject(message);
                    System.out.println("[LOG_ENTRY_SENDER][run][SCS] logEntry sent!");
                    System.out.println("[LOG_ENTRY_SENDER][run][   ] sleeping for " + logInterval + "ms");
                    Thread.sleep(logInterval);
                } catch (IOException | InterruptedException ex) {
                    System.out.println("[LOG_ENTRY_SENDER][run][   ] sleep interupted!");
                }
            }
            System.out.println("[LOG_ENTRY_SENDER][run][   ] aborted!");
        }
    }
}
