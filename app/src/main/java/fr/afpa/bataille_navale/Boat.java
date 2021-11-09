package fr.afpa.bataille_navale;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Boat {
    private int id;
    private String team;
    private String name;
    private int size;
    private int state;
    private ArrayList position;

    public Boat(String team, String name, int size) {
        this.team = team;
        this.name = name;
        this.size = size;
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

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getPosition() {
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
                "id=" + id +
                ", team='" + team + '\'' +
                ", name=" + name + '\'' +
                ", size=" + size + '\'' +
                ", state=" + state + '\'' +
                ", position=" + position + '\'' +
                '}';
    }
}
