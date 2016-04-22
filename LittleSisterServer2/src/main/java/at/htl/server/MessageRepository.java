package at.htl.server;

import at.htl.entity.LogEntry;
import at.htl.entity.Message;
import at.htl.entity.Pupil;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

public class MessageRepository extends Observable {
    private static MessageRepository instance;
    public static MessageRepository getInstance() {
        if (instance == null) {
            instance = new MessageRepository();
        }
        return instance;
    }

    private final HashMap<Pupil, List<Message>> messages;

    private MessageRepository() {
        messages = new HashMap<>();
    }

    public synchronized void insert(Pupil pupil, Message message) {
        if (messages.get(pupil) == null) {
            messages.put(pupil, new LinkedList<>());
        }
        messages.get(pupil).add(message);

        setChanged();
        notifyObservers(pupil);
    }

    public synchronized List<Message> getMessageForPupil(Pupil pupil) {
        return messages.get(pupil);
    }

    public synchronized Message getLatestMessageForPupil(Pupil pupil) {
        List<Message> entries = messages.get(pupil);
        return !entries.isEmpty() ? entries.get(entries.size() - 1) : null;
    }

    public synchronized Message getLatestMessageForTag(Pupil pupil, Message.Tag tag) {
        Message message = null;
        if(messages.get(pupil)!=null){
            for(Message msg : messages.get(pupil)){
                if(msg.getTag().equals(tag)){
                if(message == null)
                    message = msg;
                else if(msg.getTimestamp().compareTo(message.getTimestamp()) > 0)
                    message = msg;
                }
            }
        }
        return message;
    }
}
