package at.htl.entity;

import java.io.Serializable;
import java.util.List;

public class ClientSettings implements Serializable {
    private List<String> fileEndings;
    private int logInterval;
    
    public List<String> getFileEndings() {
        return fileEndings;
    }
    public void setFileEndings(List<String> fileEndings) {
        this.fileEndings = fileEndings;
    }

    public int getLogInterval() {
        return logInterval;
    }
    public void setLogInterval(int logInterval) {
        this.logInterval = logInterval;
    }  

    public ClientSettings() {
    }

    public ClientSettings(List<String> fileEndings, int logInterval) {
        this.fileEndings = fileEndings;
        this.logInterval = logInterval;
    } 
}