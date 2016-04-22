package at.htl.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Message implements Serializable {

    public enum Tag {
        CONNECT, DISCONNECT, CONNECTING_SUCCESSFUL, CONNECTING_FAILED, CONNECTION_LOST, TEST_STARTED, TEST_ENDED, SETTINGS_CHANGED, LOG_ENTRY, TESTFILES, TESTDIRECTORY
    }

    private Tag tag;
    private Pupil pupil;
    private byte[] file;
    private LogEntry logEntry;
    private ClientSettings settings;
    private LocalDateTime timestamp;

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public Pupil getPupil() {
        return pupil;
    }

    public void setPupil(Pupil pupil) {
        this.pupil = pupil;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public LogEntry getLogEntry() {
        return logEntry;
    }

    public void setLogEntry(LogEntry logEntry) {
        this.logEntry = logEntry;
    }

    public ClientSettings getSettings() {
        return settings;
    }

    public void setSettings(ClientSettings settings) {
        this.settings = settings;
    }

    public Message(Tag tag) {
        this.timestamp = LocalDateTime.now();
        this.tag = tag;
    }

    public Message(Tag tag, Pupil pupil) {
        this.timestamp = LocalDateTime.now();
        this.tag = tag;
        this.pupil = pupil;
    }

    public Message(Tag tag, byte[] file) {
        this.timestamp = LocalDateTime.now();
        this.tag = tag;
        this.file = file;
    }

    public Message(Tag tag, ClientSettings settings) {
        this.timestamp = LocalDateTime.now();
        this.tag = tag;
        this.settings = settings;
    }

    public Message(Tag tag, LogEntry logEntry) {
        this.timestamp = LocalDateTime.now();
        this.tag = tag;
        this.logEntry = logEntry;
    }
}
