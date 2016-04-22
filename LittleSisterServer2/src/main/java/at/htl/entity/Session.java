package at.htl.entity;

public class Session {

    // ----- FIELDS -----  
    private String name;
    private String schoolClass;
    private ClientSettings clientSettings;
    private String logDirectory;
    private String relevantFilesPath;
    
    private String retrieveData;
    private boolean isCorrect;

    public String getRetrieveData() {
        return retrieveData;
    }

    public void setRetrieveData(String retrieveData) {
        this.retrieveData = retrieveData;
    }

    public boolean isIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(boolean isCorrect) {
        this.isCorrect = isCorrect;
    }
      
    // ----- GETTER && SETTER -----   
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchoolClass() {
        return schoolClass;
    }

    public void setSchoolClass(String schoolClass) {
        this.schoolClass = schoolClass;
    }

    public ClientSettings getClientSettings() {
        return clientSettings;
    }

    public void setClientSettings(ClientSettings clientSettings) {
        this.clientSettings = clientSettings;
    }

    public String getLogDirectory() {
        return logDirectory;
    }

    public void setLogDirectory(String logDirectory) {
        this.logDirectory = logDirectory;
    }

    public String getRelevantFilesPath() {
        return relevantFilesPath;
    }

    public void setRelevantFilesPath(String relevantFilesPath) {
        this.relevantFilesPath = relevantFilesPath;
    }

    // ----- CONSTRUCTORS -----  
    public Session() {
    
    }

    public Session(String name, String schoolClass, ClientSettings settings, String logDirectory) {
        this.name = name;
        this.schoolClass = schoolClass;
        this.clientSettings = settings;
        this.logDirectory = logDirectory;
    }
}
