package at.htl.server;

import at.htl.controller.MainViewController;
import at.htl.entity.Message;
import at.htl.entity.Message.Tag;
import at.htl.entity.Pupil;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.slf4j.LoggerFactory;

public class Server implements Runnable {
    
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(Server.class);

    private int port;
    private ServerSocket serverSocket;

    private List<Connection> connectionThreads;
    private boolean running = false;
     
    public int getPort() {
        return port;
    }

    public boolean isRunning() {
        return running;
    }
    
    public Server() {
        port = 50555;
    }

    public Server(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        connectionThreads = new LinkedList<>();
        try {
            serverSocket = new ServerSocket(port);
            running = true;
            while (true) {
                Socket socket = serverSocket.accept();              
                Connection connection = new Connection(socket);
                connectionThreads.add(connection);
                logger.info("System;New Socket (pupil) accepted");
                new Thread(connection).start();
            }
        } catch (Exception e) {
            stop();          
        }

    }

    public void stop() {
        if (serverSocket != null) {
            try {
                for (Connection connectionThread : connectionThreads) {
                   
                    connectionThread.disconnect();
                }
                serverSocket.close();
                running = false;
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    } 
    
    public void writeToPupil(Pupil pupil, Message msg) {
        if(connectionThreads != null && connectionThreads.size() > 0){
            for (Connection connectionThread : connectionThreads) {
                if(connectionThread != null && pupil != null && connectionThread.getPupil() != null)
                    if(connectionThread.getPupil().equals(pupil)) {
                        connectionThread.writeToStream(msg);
                    }
            }   
        }
    }
    
    public void writeToPupils(List<Pupil> pupils, Message msg) {
        if(connectionThreads != null)
            for (Connection connectionThread : connectionThreads) {
                if(connectionThread != null && pupils != null && connectionThread.getPupil() != null)
                    if(pupils.contains(connectionThread.getPupil())) {
                        connectionThread.writeToStream(msg);
                    }
            }
    }
    
    public void writeToAllPupils(Message msg) {
        if(connectionThreads != null)
            for (Connection connectionThread : connectionThreads) {
                connectionThread.writeToStream(msg);
            }
    }   
}
