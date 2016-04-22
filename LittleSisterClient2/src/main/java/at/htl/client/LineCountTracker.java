package at.htl.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LineCountTracker {
    private static List<String> _fileEndings;  
    private static List<String> files;

     
    public static int getLines(String rootDirectory, List<String> fileEndings) {
        int lines = 0;
        _fileEndings = fileEndings;
        files = new LinkedList<>();
        getFilesWithEnding(rootDirectory);
        for (String file : files) {
            lines += getLineCountForFile(file);
        }
        return lines;
    }
    
    private static void getFilesWithEnding(String path) {
        File file = new File(path);
        if (file.exists()) {
            for (File listFile : file.listFiles()) {
                String name = listFile.getName();
                if (listFile.isDirectory()) {
                    getFilesWithEnding(listFile.getPath());
                } else {
                    if (name != null) {
                        String[] data = name.split("[.]");
                        if (data.length > 1 && _fileEndings.contains(data[data.length - 1])) {
                            files.add(listFile.getPath());
                        }
                    }
                }
            }
        }
    }
    
    private static int getLineCountForFile(String path) {
        BufferedReader reader;     
        int lines = 0;      
        try {
            reader = new BufferedReader(new FileReader(path));
            while (reader.readLine() != null) {
                lines++;
            }
            reader.close();
        } catch (IOException ex) {
            Logger.getLogger(LineCountTracker.class.getName()).log(Level.SEVERE, null, ex);
        }       
        return lines;
    }
    
}
