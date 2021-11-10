package fr.afpa.bataille_navale;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Boat {
    private String team;
    private String name;
    private int size;
    private int life;
    private int state;
    private String orientation;
    private ArrayList position;

    public Boat(String team, String name, int size, int life) {
        this.team = team;
        this.name = name;
        this.size = size;
        this.life = life;
        // Set default state to 0 when boat created
        this.state = 0;
        this.position = new ArrayList();
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String name) {
        this.team = team;
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

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public ArrayList getPosition() { return position; }

    public String getPositionJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("uniqueArrays", new JSONArray(position));
        } catch (JSONException e) {
            Log.i("Error", "Pb on json boatPos");
        }
        String arrayList = json.toString();
        return arrayList;
    }

    public void setPosition(ArrayList position) { this.position = position; }

    @Override
    public String toString() {
        return "Boat{" +
                "team='" + team + '\'' +
                ", name=" + name + '\'' +
                ", size=" + size + '\'' +
                ", life=" + life + '\'' +
                ", state=" + state + '\'' +
                ", orientation=" + orientation + '\'' +
                ", position=" + position + '\'' +
                '}';
    }
}
