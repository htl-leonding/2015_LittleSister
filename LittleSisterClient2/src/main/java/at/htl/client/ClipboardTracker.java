package at.htl.client;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ClipboardTracker implements Runnable {

    private final List<String> clipboardData;
    private boolean deleteList = false;
    private Thread thread;

    private int interval;

    public ClipboardTracker(int interval) {
        clipboardData = new LinkedList<>();
        this.interval = interval;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public List<String> getClipboardData() {
        deleteList = true;
        return clipboardData;
    }

    public void start() {
        thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }

    public void stop() {
        thread.interrupt();
    }

    @Override
    public void run() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        String currentData;

        while (true) {
            try {
                Transferable data = clipboard.getContents(null);
                if (data != null && data.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                    currentData = (String) data.getTransferData(DataFlavor.stringFlavor);
                    if (currentData.length() == 0 || !clipboardData.contains(currentData)) {
                        if (deleteList) {
                            clipboardData.clear();
                            deleteList = false;
                        }
                        clipboardData.add(currentData);
                    }
                }
                Thread.sleep(interval);
            } catch (UnsupportedFlavorException | IOException | InterruptedException ex) {
                System.out.println("Error: ClipboardTracker");
            }
        }
    }
}
