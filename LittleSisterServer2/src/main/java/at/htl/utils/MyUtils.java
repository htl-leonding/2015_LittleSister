package at.htl.utils;

import at.htl.entity.Message;
import at.htl.entity.Pupil;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.slf4j.LoggerFactory;

public class MyUtils {

    public static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("HH-mm-ss");
    public static DateFormat logDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    static org.slf4j.Logger logger = LoggerFactory.getLogger(MyUtils.class);

    
    public static List<String> getSchoolClassesFromCsv(String path) {
        List<String> schoolClasses = new LinkedList<>();

        File f = new File(path);
        if (!f.exists()) {
            logger.error("System;Can not read pupils csv in getSchoolClassesFromCsv: File does not exist at " + path + ".");
            return schoolClasses;
        }
        BufferedReader reader = null;
        try {
            if (f.exists()) {
                reader = new BufferedReader(new FileReader(f));
                String line;
                String[] data;
                while ((line = reader.readLine()) != null) {
                    data = line.split(";");
                    if (!schoolClasses.contains(data[2])) {
                        schoolClasses.add(data[2]);
                    }
                }
                logger.info("System;Pupils successfully read.");
            }
        } catch (IOException ex) {
            logger.error("System;IO Exception while reading pupils from csv: " + ex.getMessage());
        } finally {
            try {
                if(reader != null)
                    reader.close();
            } catch (IOException ex) {
                logger.error("System;IO Exception while closing pupils csv reader: " + ex.getMessage());

            }
        }

        return schoolClasses;
    }

    public static List<Pupil> getPupilsFromCsv(String path, String schoolClass) {
        List<Pupil> pupils = new LinkedList<>();
        File f = new File(path);
        if (!f.exists()) {
            logger.error("System;Can not read pupils csv in getPupilsFromCsv: File does not exist at " + path + ".");
            return pupils;
        }
        BufferedReader reader = null;
        int i = 0;
        try {
            if (f.exists()) {
                reader = new BufferedReader(new FileReader(f));
                String line;
                String[] data;
                while ((line = reader.readLine()) != null) {
                    i++;
                    data = line.split(";");
                    if (data[2].equals(schoolClass)) {
                        pupils.add(new Pupil(data[1].toUpperCase(), data[0], i));
                    }
                }
                logger.info("System;Pupils successfully read.");
            }
        } catch (IOException ex) {
            logger.error("System;IO Exception while reading pupils from csv: " + ex.getMessage());
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                logger.error("System;IO Exception while closing pupils csv reader: " + ex.getMessage());
            }
        }
        return pupils;
    }

    public static byte[] readBytesFromAFile(File file) {
        int start = 0;
        int length = 1024;
        int offset = -1;
        byte[] buffer = new byte[length];
        try {
            //convert the file content into a byte array
            FileInputStream fileInuptStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(
                    fileInuptStream);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            while ((offset = bufferedInputStream.read(buffer, start, length)) != -1) {
                byteArrayOutputStream.write(buffer, start, offset);
            }

            bufferedInputStream.close();
            byteArrayOutputStream.flush();
            buffer = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.close();
        } catch (FileNotFoundException fileNotFoundException) {
            logger.error("could not find testfiles");
            buffer = null;
        } catch (IOException ioException) {
            logger.error("an error occured while trying to read the testfiles");
            buffer = null;
        } catch (OutOfMemoryError ex){
            logger.error("selected file was too big");
            buffer = null;
        }

        return buffer;
    }

    public static String getAbsJpgPath(String homePath, Message message, boolean isDirectory) {
        String res;

        if (message == null || message.getLogEntry() == null) {
            return "";
        }
        
        if (isDirectory) {
            res = homePath.equals("") ? homePath.concat(message.getPupil().getName()).concat("_").concat(message.getPupil().getMatrikelNr()).concat(File.separator)
                    : homePath.concat(File.separator).concat(message.getPupil().getName()).concat("_").concat(message.getPupil().getMatrikelNr()).concat(File.separator);
        } else {
            System.out.println(message.getTimestamp().toString());

            res = homePath.equals("") ? homePath.concat(message.getPupil().getName()).concat("_").concat(message.getPupil().getMatrikelNr()).
                    concat(File.separator).concat(message.getTimestamp().format(dateFormat)).concat(".jpg")
                    : homePath.concat(File.separator).concat(message.getPupil().getName()).concat("_").concat(message.getPupil().getMatrikelNr()).
                    concat(File.separator).concat(message.getTimestamp().format(dateFormat)).concat(".jpg");
        }

        return res;
    }

    public static void writeScreenshotForMessage(String homePath,Message message) {
        try {
            BufferedImage bi = ImageIO.read(new ByteArrayInputStream(message.getLogEntry().getScreenshot()));
            File f = new File(MyUtils.getAbsJpgPath(homePath, message, true));
            f.mkdir();
            f = new File(MyUtils.getAbsJpgPath(homePath, message, false));
            ImageIO.write(bi, "jpg", f);
        } catch (IOException ex) {
            Logger.getLogger(MyUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Properties getProperties() {
        try {
            Reader reader = new FileReader("properties.txt");
            Properties p = new Properties();
            p.load(reader);
            return p;
        } catch (FileNotFoundException ex) {
            return null;
        } catch (IOException ex) {
            return null;
        }
    }

    public static void writeProperties(String fileEndings, String testFilesPath, String savePath, String port) {
        try {
            Writer writer = new FileWriter("properties.txt");
            Properties p = new Properties();
            p.setProperty("fileEndings", fileEndings);
            p.setProperty("testFilesPath", testFilesPath);
            p.setProperty("savePath", savePath);
            p.setProperty("port", port);
            p.store(writer, "LittleSister Properties");

        } catch (IOException ex) {
            Logger.getLogger(MyUtils.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
