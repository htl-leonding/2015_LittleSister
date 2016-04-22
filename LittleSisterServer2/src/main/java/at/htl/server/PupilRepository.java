package at.htl.server;

import at.htl.entity.Pupil;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class PupilRepository {

    private static PupilRepository instance;

    private HashMap<Pupil, Socket> pupils = new HashMap<>();
    
    public HashMap<Pupil, Socket> getPupilsHash() {
        return pupils;
    }

    public static PupilRepository getInstance() {
        if (instance == null) {
            instance = new PupilRepository();
        }
        return instance;
    }

    public void insertAll(List<Pupil> pupils) {
        for (Pupil pupil : pupils) {
            this.pupils.put(pupil, null);
        }
    }

    public boolean insert(Pupil pupil, Socket s) {
        if (!pupils.containsKey(pupil)) {
            return false; //TODO?
        }

        pupils.put(pupil, s);

        return true;
    }

    public boolean insert(Pupil pupil) {
        if (!pupils.containsKey(pupil)) {
            return false; //TODO?
        }

        pupils.put(pupil, null);

        return true;
    }

    public boolean putSocket(Pupil p, Socket s) {
        if(!pupils.containsKey(p))
            return false;
        return pupils.put(p, s) == null;
    }

    public List<Pupil> getPupils() {
        List<Pupil> list = new LinkedList<>();
        for (Pupil p : pupils.keySet()) {
            list.add(p);
        }
        Collections.sort(list);
        return list;
    }

    public boolean isPupilConnected(Pupil p){
        if(pupils.get(p) == null) 
            return false;
        return !pupils.get(p).isClosed();
    }

    /**
     *
     * @param keyPupil
     * @return the correct pupil from the Repository, null if no pupil found
     */
    public Pupil getPupilWithKey(Pupil keyPupil) {
        for (Pupil p : pupils.keySet()) {
            if (p.equals(keyPupil)) {
                return p;
            }
        }
        return null;
    }
}
