package fr.afpa.bataille_navale;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ConnexionSQLite extends SQLiteOpenHelper {

    private static final String TABLE_BOAT = "table_boat";
    private static final String COL_ID = "ID";
    private static final String COL_TEAM = "team";
    private static final String COL_NAME = "Name";
    private static final String COL_SIZE = "Size";
    private static final String COL_LIFE = "Life";
    private static final String COL_STATE = "State";
    private static final String COL_ORIENTATION = "Orientation";
    private static final String COL_POSITION = "Position";

    private static final String CREATE_DTB = "CREATE TABLE " + TABLE_BOAT + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_TEAM + " TEXT NOT NULL, "
            + COL_NAME + " TEXT NOT NULL, " + COL_SIZE + " INT NOT NULL, " + COL_LIFE + " INT NOT NULL, "
            + COL_STATE + " INT NOT NULL, " + COL_ORIENTATION + " TEXT NOT NULL, " + COL_POSITION + " TEXT NOT NULL);";

    public ConnexionSQLite(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DTB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + TABLE_BOAT + ";");
        onCreate(db);
    }

}