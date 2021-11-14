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
    private static final String COL_TEAM = "team";
    private static final String COL_NAME = "Name";
    private static final String COL_SIZE = "Size";
    private static final String COL_LIFE = "Life";
    private static final String COL_STATE = "State";
    private static final String COL_ORIENTATION = "Orientation";
    private static final String COL_POSITION = "Position";

    private SQLiteDatabase dtb;
    private ConnexionSQLite connexionSQLite;

    /**
     * Create new instance of Boat Mgr and connect to the database
     */
    public BoatMgr(Context context) {
        connexionSQLite = new ConnexionSQLite(context, DTB_NAME, null, DTB_VERSION);
    }

    /**
     * Open database access
     */
    public void open() { dtb = connexionSQLite.getWritableDatabase(); }

    /**
     * Close database access
     */
    public void close() { dtb.close(); }

    /**
     * Insert boat into table_boat
     */
    public void insertBoat(Boat boat) {
        open();

        ContentValues values = new ContentValues();
        values.put(COL_TEAM, boat.getTeam());
        values.put(COL_NAME, boat.getName());
        values.put(COL_SIZE, boat.getSize());
        values.put(COL_LIFE, boat.getLife());
        values.put(COL_STATE, boat.getState());
        values.put(COL_ORIENTATION, boat.getOrientation());
        values.put(COL_POSITION, boat.getPositionJson());
        dtb.insert(TABLE_BOAT, null, values);

        close();
    }

    /**
     * Modify boat into table_boat
     */
    // For "continue game" functionality
    public int updateBoat(int id, Boat boat) {
        ContentValues values = new ContentValues();
        values.put(COL_TEAM, boat.getTeam());
        values.put(COL_NAME, boat.getName());
        values.put(COL_SIZE, boat.getSize());
        values.put(COL_LIFE, boat.getLife());
        values.put(COL_STATE, boat.getState());
        values.put(COL_ORIENTATION, boat.getOrientation());
        values.put(COL_POSITION, boat.getPositionJson());

        return dtb.update(TABLE_BOAT, values, COL_ID + " = " + id, null);
    }

    /**
     * Delete table_boat
     */
    public int deleteBoat() {
        return dtb.delete(TABLE_BOAT,null,null);
    }

    /**
     * Get all boats where team equals given team
     */
    public ArrayList<Boat> getAll(String team) {
        Log.i("team =", team);
        ArrayList<Boat> boatItems = new ArrayList<Boat>();
        open();
        Cursor c = dtb.query(TABLE_BOAT, new String[]{COL_NAME, COL_SIZE, COL_LIFE, COL_STATE, COL_ORIENTATION, COL_POSITION}, COL_TEAM + " = ?", new String[]{"player"}, null, null, null);
        if(c.getCount() == 0) {
            return null;
        }

        if(c.moveToFirst()) {
            do {
                String name = c.getString(0);
                int size = c.getInt(1);
                int life = c.getInt(2);
                int state = c.getInt(3);
                String orientation = c.getString(4);
                String position = c.getString(5);

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
                Boat b = new Boat(team, name, size, life);
                b.setState(state);
                b.setOrientation(orientation);
                b.setPosition(listOfBits);
                boatItems.add(b);
            } while (c.moveToNext());
        }
        close();
        return boatItems;
    }
}
