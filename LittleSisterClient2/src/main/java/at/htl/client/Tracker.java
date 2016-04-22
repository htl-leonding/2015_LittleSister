package at.htl.client;

import at.htl.entity.Message;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Tracker implements Runnable {
    List<ITracker> trackers;
    private int interval;

    @Override
    public void run() {
        while (true) {
            Message message = new Message(Message.Tag.LOG_ENTRY);
            for (ITracker tracker : trackers) {
                Object data = tracker.getNextData();
                if (tracker.getClass().equals(ClipboardTracker.class)) {

                }
            }
            try {
                Thread.sleep(interval);
            } catch (InterruptedException ex) {
                Logger.getLogger(Tracker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
