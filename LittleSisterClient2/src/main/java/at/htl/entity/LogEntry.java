package at.htl.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class LogEntry implements Serializable {
    private final LocalDateTime timeStamp;
    private byte[] screenshot;
    private List<String> clipboardData;
    private int lineCount;

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }
     
    public byte[] getScreenshot() {
        return screenshot;
    }
    public void setScreenshot(byte[] screenshot) {
        this.screenshot = screenshot;
    }

    public List<String> getClipboardData() {
        return clipboardData;
    }
    public void setClipboardData(List<String> clipboard) {
        this.clipboardData = clipboard;
    }

    public int getLineCount() {
        return lineCount;
    }
    public void setLineCount(int lineCount) {
        this.lineCount = lineCount;
    }

    public LogEntry() {
        this.timeStamp = LocalDateTime.now();
        screenshot = null;
        clipboardData = null;
        lineCount = 0;
    }
    
    public LogEntry(byte[] screenshot, List<String> clipboardData, int lineCount) {
        this.timeStamp = LocalDateTime.now();
        this.screenshot = screenshot;
        this.clipboardData = clipboardData;
        this.lineCount = lineCount;
    }
}