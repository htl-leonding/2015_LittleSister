package at.htl.client;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;

public class ScreenshotTracker{
    
    public byte[] getScreenShot() {
        try {
            BufferedImage image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));//Aufnahme
            return convertToJPG(image);
        } catch (AWTException | IOException ex) {
            Logger.getLogger(ScreenshotTracker.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    private byte[] convertToJPG(BufferedImage image) throws IOException{
        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
        ImageWriteParam iwp = writer.getDefaultWriteParam();
        iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        iwp.setCompressionQuality(0.85f);
        
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        writer.setOutput(new MemoryCacheImageOutputStream(bout));
        writer.write(null, new IIOImage(image,null,null),iwp);
        writer.dispose();
        bout.flush();
        return bout.toByteArray();
    }   
}
