package org.kashmirworldfoundation.seaturtle;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by dwanna on 8/6/16.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String TAG = "***" + DBHelper.class.getSimpleName();

    /**
     * value to be stored in DB to indicate new record
     * this is needed since SQLite does not support boolean
     */
    public static final int IS_NEW = 1;

    /**
     * database name to be used for entire application
     * MAKE SURE NAME IS *.db (not .DB or anything else)
     * otherwise onCreate will not be called
     */
    private static final String DB_NAME = "SeaTurtle.db";

    /**
     * database version. if this changes onUpgrade will be triggered
     */
    private static final int DB_VERSION = 1;

    /**
     * tag used for tables with "clean" data from server
     */
    private final String NEST = "Nest";

    /**
     * tag used for tables with "dirty" data editted locally
     */
    private final String NEST_HISTORY = "NestHistory";

    /**
     * string representing all updatable fields in Nest table
     */
    private final String NEST_UPDATABLE_FIELDS = ""
            + "  nest_id, date_deposited"
            //      0           1
            + ", complexity, obstruction, sand, location"
            //      2              3        4       5
            + ", orig_latitude, orig_longitude, orig_position, orig_egg_count"
            //      6               7               8               9
            + ", new_latitude, new_longitude, new_position, new_egg_count"
            //      10              11          12              13
            + ", nest_description"
            //      13
            ;

    /**
     * prefix to be added to new nest ID's
     * to distinguish from exisiting ones in DB
     */
    private final String NEW_PREFIX = "NEW";

    /**
     * static number that is used as new nest ID
     * must be incremented after every use
     */
    private static int mNextID = 10001;

    /**
     * constructor create/open database
     * @param context
     */
    public DBHelper(Context context) {
        // call super first
        super(context, DB_NAME, null, DB_VERSION);

        SQLiteDatabase db = getReadableDatabase();

        Log.d(TAG, "Database " + DB_NAME + " created/opened");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate(" + db.toString() + ");");

        // TODO REMOVE - for testing only
        db.execSQL("DROP TABLE IF EXISTS " + NEST);
        db.execSQL("DROP TABLE IF EXISTS " + NEST_HISTORY);
        Log.d(TAG, "deleted tables");

        String sqlString;

        // build the sql statement to create table
        sqlString = "CREATE TABLE IF NOT EXISTS " + NEST + " ("
                + "  id              INTEGER PRIMARY KEY AUTOINCREMENT"
                + ", is_new          INTEGER DEFAULT 0"
                + ", nest_id         TEXT UNIQUE"
                + ", date_deposited  TEXT"
                + ", complexity      TEXT"
                + ", obstruction     TEXT"
                + ", sand            TEXT"
                + ", location        TEXT"
                + ", orig_latitude   TEXT"
                + ", orig_longitude  TEXT"
                + ", orig_position   TEXT"
                + ", orig_egg_count  TEXT"
                + ", new_latitude    TEXT"
                + ", new_longitude   TEXT"
                + ", new_position    TEXT"
                + ", new_egg_count   TEXT"
                + ", nest_description TEXT"
                + ", date_modified   DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ", version         INTEGER DEFAULT 0"
                + ")";

        // execute the sql statement to build the table
        db.execSQL(sqlString);

        // create sql statement to create trigger for last modified
        sqlString = ""
                + "CREATE TRIGGER IF NOT EXISTS last_modified_Nest "
                + " AFTER UPDATE ON Nest FOR EACH ROW "
                + " BEGIN UPDATE Nest "
                + "   SET date_modified = CURRENT_TIMESTAMP"
                + " WHERE id = old.id;"
                + "  END";

        // execute sql statement to create trigger for last modified
        db.execSQL(sqlString);

        // TODO add trigger to increment version

        // TODO add history table

        // TODO REMOVE for testin only
        TestData.loadTestData(db, NEST);

    }

    /**
     * if the database version changes,
     * then need to drop all tables and recreate them
     * @param db database object
     * @param i  int, new version
     * @param i1 int, old version
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        Log.d(TAG, "onUpgrade()");

        // TODO uncomment all lines, commented for testing only
        //db.execSQL("DROP TABLE IF EXISTS " + SERVER);
        Log.d(TAG, "onUpgrade -> dropped table" + NEST);

        //db.execSQL("DROP TABLE IF EXISTS " + LOCAL);
        Log.d(TAG, "onUpgrade -> dropped table" + NEST_HISTORY);

        onCreate(db);
    }

    /**
     * retrieve all original nest locations from database
     * @return Cursor - query results
     */
    public Cursor getNestOriginalLocations() {
        Log.d(TAG, "getNestOriginalLocations()");

        SQLiteDatabase db = getWritableDatabase();

        String sqlString = ""
                + "SELECT nest_id, new_latitude, new_longitude, is_new"
                + "  FROM " + NEST
                ;
        Cursor cursor = db.rawQuery(sqlString, null);
        Log.d(TAG, "getNestOriginalLocations -> " + cursor.getCount() + " records");

        return cursor;
    }

    /**
     * retrieve all original nest locations from database
     * @return Cursor - query results
     */
    public Cursor getNestNewLocations() {
        Log.d(TAG, "getNestNewLocations()");

        SQLiteDatabase db = getWritableDatabase();

        String sqlString = ""
                + "SELECT nest_id, orig_latitude, orig_longitude, is_new "
                + "  FROM " + NEST
                ;
        Cursor cursor = db.rawQuery(sqlString, null);
        Log.d(TAG, "getNestNewLocations -> " + cursor.getCount() + " records");

        return cursor;
    }



    /**
     * retrieve all nest information for a specific nest id
     * @return Cursor - query results
     */
    public Cursor getNestInfo(String nestID) {
        Log.d(TAG, "getData(" + nestID + ")");

        SQLiteDatabase db = getWritableDatabase();

        String sqlString = ""
                + "SELECT * "
                + "  FROM " + NEST
                + " WHERE nest_id = '" + nestID + "'";
                ;

        Cursor cursor = db.rawQuery(sqlString, null);
        Log.d(TAG, "getData -> " + cursor.getCount() + " records");

        return cursor;
    }


    public boolean saveNestRecord(ContentValues record) {
        Log.d(TAG, "saveNestRecord(" + record.toString() +")");

        // open database connection
        SQLiteDatabase db = getWritableDatabase();

        // search if record is in LocalDB
        Cursor cursor = getNestInfo(record.get("nest_id").toString());

        if (cursor.moveToFirst()) {
            // if record exists, archive
            // TODO edit existing record


        } else {
            // add new record
            Log.d(TAG, "saveNestRecord -> inserting");

            // if result of insert is -1, then failed
            if (db.insert(NEST, null, record) == -1) {
                return false;
            } else {
                return true;
            }
        }

        return true;
    }
}