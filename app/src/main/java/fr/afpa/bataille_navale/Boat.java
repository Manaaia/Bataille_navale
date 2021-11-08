package fr.afpa.bataille_navale;

import java.util.ArrayList;

public class Boat {
    private String name;
    private int size;
    private int state;
    private ArrayList position;

    public Boat(String name, int size) {
        this.name = name;
        this.size = size;
        // Set default state to 0 when boat created
        this.state = 0;
        this.position = new ArrayList();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public ArrayList getPosition() { return position; }

    public void setPosition(ArrayList position) { this.position = position; }
}
