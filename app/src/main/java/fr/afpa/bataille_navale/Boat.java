package fr.afpa.bataille_navale;

public class Boat {
    private int size;
    private int state;

    public Boat(int size) {
        this.size = size;
        // Set default state to 0 when boat created
        this.state = 0;
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
}
