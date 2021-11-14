package fr.afpa.bataille_navale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class ScoreMgr {
    private static final int DTB_VERSION = 1;
    private static final String DTB_NAME = "bataille_navale";
    private static final String TABLE_SCORE = "table_score";
    private static final String COL_ID = "ID";
    private static final String COL_RESULT = "Result";
    private static final String COL_COUNT = "Count";

    private SQLiteDatabase dtb;
    private ConnexionSQLite connexionSQLite;

    /**
     * Create new instance of ScoreMgr and connect to the database
     */
    public ScoreMgr(Context context) {
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
     * Insert score into table_score
     */
    public void insertScore(boolean result, int count) {
        open();

        ContentValues values = new ContentValues();
        values.put(COL_RESULT, result);
        values.put(COL_COUNT, count);
        dtb.insert(TABLE_SCORE, null, values);

        close();
    }

    /**
     * Get all scores
     */
    public ArrayList getAll() {
        ArrayList scores = new ArrayList();
        open();
        Cursor c = dtb.query(TABLE_SCORE, new String[]{COL_ID, COL_RESULT, COL_COUNT}, null, null, null, null, null);
        if(c.getCount() == 0) {
            return null;
        }

        if(c.moveToFirst()) {
            do {
                int id = c.getInt(0);
                boolean result = c.getInt(1) != 0;
                int count = c.getInt(2);

                // Create the Score string
                String sResult;
                if(result) {
                    sResult = "Gagné";
                } else {
                    sResult = "Perdu";
                }
                String score = "Partie " + id + " : " + sResult + " en " + count + " coups";
                scores.add(score);
            } while (c.moveToNext());
        }
        close();
        return scores;
    }
}
