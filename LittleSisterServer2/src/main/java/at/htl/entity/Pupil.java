package at.htl.entity;

import java.io.Serializable;

public class Pupil implements Serializable, Comparable {      
    private final String matrikelNr;
    private String lastName;
    private int katNr;
    
    public String getMatrikelNr() {
        return matrikelNr.toUpperCase();
    }

    public String getName() {
        return lastName;
    }
    public void setName(String lastName) {
        this.lastName = lastName;
    }

    public int getKatNr() {
        return katNr;
    }
    public void setKatNr(int katNr) {
        this.katNr = katNr;
    }

    public Pupil(String matrikelNr) {
        this.matrikelNr = matrikelNr;
        this.lastName = "";
        this.katNr = 0;
    }

    public Pupil(String matrikelNr, String name, int katNr) {
        this.matrikelNr = matrikelNr;
        this.lastName = name;
        this.katNr = katNr;
    }

    @Override
    public String toString() {
        return matrikelNr + " - " + lastName;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null || obj.getClass() != getClass())
            return false;
       
        Pupil pupil = (Pupil) obj;
        return this.getMatrikelNr().toUpperCase().equals(pupil.getMatrikelNr().toUpperCase());
    }

    @Override
    public int hashCode() {
        return this.matrikelNr.hashCode();
    }  

    @Override
    public int compareTo(Object o) {
        return this.getName().compareTo(((Pupil)o).getName());
    }
}