package at.htl.server;

import at.htl.entity.Message;
import at.htl.entity.Pupil;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.slf4j.LoggerFactory;

public class Connection implements Runnable {
    
    static org.slf4j.Logger logger = LoggerFactory.getLogger(Connection.class);
    
    private Pupil pupil;
    private final Socket socket;

    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;

    public Pupil getPupil() {
        return pupil;
    }
 
    public Connection(Socket socket) {
        this.socket = socket;
    }
    
    public void disconnect() {  
        logger.info(pupil.getMatrikelNr() + "_" + pupil.getName() + ";Closing connection...");
        if(socket.isClosed())
            return;
        try {
            if(inputStream != null) {
                inputStream.close();
            }
            if(outputStream != null) {
                outputStream.close();             
            }
            if(socket != null) {
                socket.close();
            }
            logger.info(pupil.getMatrikelNr() + "_" + pupil.getName() + ";Connection closed.");
        } catch (IOException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
            logger.warn(pupil.getMatrikelNr() + "_" + pupil.getName() + ";Failed to disconnect.");
        }
    }

    @Override
    public void run() {
        try {
            inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());    
            
            Message message = (Message)inputStream.readObject();           
            
            if (message.getTag().equals(Message.Tag.CONNECT)) {  
                if(!PupilRepository.getInstance().putSocket(message.getPupil(), socket)) {
                    logger.error(pupil.getMatrikelNr() + "_" + pupil.getName() + ";Connecting failed!");
                    outputStream.writeObject(new Message(Message.Tag.CONNECTING_FAILED));
                    return; //TODO
                }
                else {                  
                    pupil = PupilRepository.getInstance().getPupilWithKey(message.getPupil());
                    logger.info(pupil.getMatrikelNr() + "_" + pupil.getName() + ";Connecting to pupil.");
                    PupilRepository.getInstance().isPupilConnected(pupil);
                    outputStream.writeObject(new Message(Message.Tag.CONNECTING_SUCCESSFUL, pupil));
                    MessageRepository.getInstance().insert(pupil, message);
                    logger.info(pupil.getMatrikelNr() + "_" + pupil.getName() + ";Connecting was successful!");
                }
                while(true) {
                    message = (Message)inputStream.readObject();
                    //System.out.println("!!!!!!!" + message.getTag());
                    logger.info(pupil.getMatrikelNr() + "_" + pupil.getName() + ";Sent a message.");
                    MessageRepository.getInstance().insert(pupil, message);
                    
                }
            }   
            
        } catch (IOException | ClassNotFoundException ex) {
            disconnect();
            MessageRepository.getInstance().insert(pupil, new Message(Message.Tag.DISCONNECT,pupil));
        }
    }
    
    public void writeToStream(Message msg) {
        if(outputStream != null || !socket.isClosed()) {
            try {
                outputStream.writeObject(msg);
            } catch (IOException ex) {
                logger.warn(pupil.getMatrikelNr() + "_" + pupil.getName() + ";Writing a message has failed.");
            }
        } 
    }
}
