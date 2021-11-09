package fr.afpa.bataille_navale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BoatMgr {
    private static final int DTB_VERSION = 1;
    private static final String DTB_NAME = "bataille_navale";
    private static final String TABLE_BOAT = "table_boat";
    private static final String COL_ID = "ID";
    private static final int NUM_COL_ID = 0;
    private static final String COL_TEAM = "team";
    private static final int NUM_COL_TEAM = 1;
    private static final String COL_NAME = "Name";
    private static final int NUM_COL_NAME = 2;
    private static final String COL_SIZE = "Size";
    private static final int NUM_COL_SIZE = 3;
    private static final String COL_STATE = "State";
    private static final int NUM_COL_STATE = 4;
    private static final String COL_POSITION = "Position";
    private static final int NUM_COL_POSITION = 5;

    private SQLiteDatabase dtb;
    private ConnexionSQLite connexionSQLite;

    public BoatMgr(Context context) {
        connexionSQLite = new ConnexionSQLite(context, DTB_NAME, null, DTB_VERSION);
    }

    public void open() { dtb = connexionSQLite.getWritableDatabase(); }

    public void close() { dtb.close(); }

    public SQLiteDatabase getDtb() { return dtb; }

    public void insertBoat(Boat boat) {
        open();

        ContentValues values = new ContentValues();
        values.put(COL_TEAM, boat.getTeam());
        values.put(COL_NAME, boat.getName());
        values.put(COL_SIZE, boat.getSize());
        values.put(COL_STATE, boat.getState());
        values.put(COL_POSITION, boat.getPosition());
        dtb.insert(TABLE_BOAT, null, values);

        close();
    }

    public int updateMonnaie(int id, Boat boat) {
        ContentValues values = new ContentValues();
        values.put(COL_TEAM, boat.getTeam());
        values.put(COL_NAME, boat.getName());
        values.put(COL_SIZE, boat.getSize());
        values.put(COL_STATE, boat.getState());
        values.put(COL_POSITION, boat.getPosition());

        return dtb.update(TABLE_BOAT, values, COL_ID + " = " + id, null);
    }

    public ArrayList<Boat> getAll(String team) {
        Log.i("team =", team);
        ArrayList<Boat> boatItems = new ArrayList<Boat>();
        open();
        Cursor c = dtb.query(TABLE_BOAT, new String[]{COL_NAME, COL_SIZE, COL_STATE, COL_POSITION}, COL_TEAM + " = ?", new String[]{"player"}, null, null, null);
        if(c.getCount() == 0) {
            return null;
        }

        if(c.moveToFirst()) {
            do {
                String name = c.getString(0);
                int size = c.getInt(1);
                int state = c.getInt(2);
                String position = c.getString(3);

                // Convert JSON String position to ArrayList
                JSONObject json = null;
                try {
                    json = new JSONObject(position);
                } catch (JSONException e) {
                    Log.i("Error", "JSONObject error");
                }
                JSONArray jsonArray = json.optJSONArray("uniqueArrays");
                ArrayList<String> listOfBits = new ArrayList<String>();

                if(jsonArray != null) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            listOfBits.add(jsonArray.getString(i));
                        } catch (JSONException e) {
                            Log.i("Error", "JSONObject error");
                        }
                    }
                }

                // Create the Boat
                Boat b = new Boat(team, name, size);
                b.setState(state);
                b.setPosition(listOfBits);
                boatItems.add(b);
            } while (c.moveToNext());
        }
        close();
        return boatItems;
    }
}
