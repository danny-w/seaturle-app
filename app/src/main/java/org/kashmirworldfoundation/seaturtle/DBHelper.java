package org.kashmirworldfoundation.seaturtle;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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
    public static final String NEST = "Nest";

    /**
     * tag used for tables with "dirty" data editted locally
     */
    private final String NEST_HISTORY = "NestHistory";

    /**
     * prefix to be added to new nest ID's
     * to distinguish from exisiting ones in DB
     */
    private final String NEW_PREFIX = "NEW";

    // the header of the CSV file with the quotes removed
    private final String CSV_HEADER =
            "UID,Beach,County,Activity #,Activity,Nest #,Ref #,Activity Date,Year,Month,Week,Dayofyear,JulianDate,Activity Comments,Encountered?,Species,Latitude,Longitude,Zone,Location,Nest Management,Light Management,Relocation,Total Eggs Laid By Female,Relocations,Relocation Date,Relocation Reason,Relocation Latitude,Relocation Longitude,Relocation Location,Washovers,Loss Reports,Prerelocations,Total Lost Eggs,Total Lost Hatchlings,Lost Nest,Emerge Date,Inventory Date,Incubation (days),Clutch Count,Shells>50%,Unhatched Eggs,Dead Hatchlings,Live Hatchlings,Final Status Unknown,Exclude From Calc,Hatch Success,Emergence Success,DNA ID,DNA Sample,DNA Match,Inventory Comments,Data Entry,Inventorier,Locator,submitted,modified,Program,";



    private Context mContext;


    private SQLiteDatabase mDB;
    /**
     * constructor create/open database
     * @param context
     */
    public DBHelper(Context context) {
        // call super first
        super(context, DB_NAME, null, DB_VERSION);

        mDB = getWritableDatabase();
        mContext = context;

        Log.d(TAG, "Database " + DB_NAME + " created/opened");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate(" + db.toString() + ");");

        String sqlString;

        // build the sql statement to create table
        sqlString = "CREATE TABLE IF NOT EXISTS " + NEST + " ("
                + "  uid                  INTEGER PRIMARY KEY AUTOINCREMENT"
                + ", activity             TEXT"
                + ", nest_num             TEXT UNIQUE"
                + ", ref_num              TEXT UNIQUE"
                + ", activity_date        TEXT"
                + ", activity_comments    TEXT"
                + ", latitude             TEXT"
                + ", longitude            TEXT"
                + ", location             TEXT"
                + ", nest_mgmt            TEXT"
                + ", relocation           TEXT"
                + ", num_eggs_laid        TEXT"
                + ", num_relocations      TEXT"
                + ", relocation_date      TEXT"
                + ", new_latitude         TEXT"
                + ", new_longitude        TEXT"
                + ", new_location         TEXT"
                + ", num_washovers        TEXT"
                + ", num_loss_reports     TEXT"
                + ", num_lost_eggs        TEXT"
                + ", num_lost_hatchlings  TEXT"
                + ", lost_nest            TEXT"
                + ", emerge_date          TEXT"
                + ", inventory_date       TEXT"
                + ", clutch_count         TEXT"
                + ", locator              TEXT"
                + ", date_modified        TEXT"
                + ", version              INTEGER DEFAULT 0"
                + ")";

        // execute the sql statement to build the table
        db.execSQL(sqlString);

        // create sql statement to create trigger for last modified
        sqlString = ""
                + "CREATE TRIGGER IF NOT EXISTS update_version "
                + " AFTER UPDATE ON Nest FOR EACH ROW "
                + " BEGIN UPDATE Nest "
                + "   SET version = old.version + 1"
                + " WHERE id = old.id;"
                + "   END";

        // execute sql statement to create trigger for last modified
        db.execSQL(sqlString);

        // TODO add history table

        // TODO REMOVE for testin only
        //TestData.loadTestData(db, NEST);

    }

    /**
     * if the database version changes,
     * then need to drop all tables and recreate them
     * @param db SQLite database object
     * @param oldVersion int, new version
     * @param newVersion int, old version
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade()");

        initDB(db);
    }

    /**
     * reinitialize the database by deleting info tables
     * this function can be called on ugrade before mDB is initialized
     */
    private void initDB(SQLiteDatabase db) {
        Log.d(TAG, "inititializeDB()");

        // deleted info tables
        db.execSQL("DROP TABLE IF EXISTS " + NEST);
        db.execSQL("DROP TABLE IF EXISTS " + NEST_HISTORY);
        Log.d(TAG, "inititializeDB -> deleted tables");

        // recreate tables
        onCreate(db);
    }

    /**
     * override initDB with no arameters - uses mDB instead
     */
    public void initDB() {
        initDB(mDB);
    }

    /**
     * retrieve location information for all nests
     * @return Cursor - query results
     */
    public Cursor getNestLocations() {
        Log.d(TAG, "getNestLocations()");

        // ref_num, latitude, longitude, new_latitude, new_longitude, activity_date, emerge_date, inventory_date
        String sqlString = ""
                + "SELECT ref_num, latitude, longitude, new_latitude, new_longitude,"
                + "       activity_date, emerge_date, inventory_date"
                + "  FROM " + NEST
                ;
        Cursor cursor = mDB.rawQuery(sqlString, null);
        Log.d(TAG, "getNestNewLocations -> " + cursor.getCount() + " records");

        return cursor;
    }

    /**
     * retrieve all nest information for a specific nest id
     * @return Cursor - query results
     */
    public ContentValues getNestInfo(String refNum) {
        Log.d(TAG, "getNestInfo(" + refNum + ")");

        // nest_num, ref_num, activity_date, activity_comments, latitude, longitude, location,
        // nest_mgmt, relocation, num_eggs_laid,
        // num_relocations, relocation_date, new_latitude, new_longitude, new_location,
        // num_washovers, num_loss_reports, num_lost_eggs, num_lost_hatchlings, lost_nest,
        // emerge_date, inventory_date, clutch_count, locator, date_modified, version

        String sqlString = ""
                + "SELECT ref_num, activity_date, emerge_date, nest_mgmt, num_eggs_laid, "
                + "       activity_comments, latitude, longitude, location,"
                + "       new_latitude, new_longitude, new_location"
                + "       "
                + "       "
                + "       "
                + "  FROM " + NEST
                + " WHERE ref_num = '" + refNum + "'";
                ;



        // run query
        Cursor cursor = mDB.rawQuery(sqlString, null);

        Log.d(TAG, "getNestInfo -> " + cursor.getCount() + " nests");

        // extract values
        if (cursor.moveToFirst()) {
            ContentValues record = new ContentValues();

            record.put("ref_num", cursor.getString(0));
            record.put("activity_date", cursor.getString(1));
            record.put("emerge_date", cursor.getString(2));
            record.put("nest_mgmt", cursor.getString(3));
            record.put("num_eggs_laid", cursor.getString(4));
            record.put("activity_comments", cursor.getString(5));
            record.put("latitude", cursor.getString(6));
            record.put("longitude", cursor.getString(7));
            record.put("location", cursor.getString(8));
            //record.put("", cursor.getString(9));
            //record.put("", cursor.getString(10));
            //record.put("", cursor.getString(11));
            //record.put("", cursor.getString(12));
            //record.put("", cursor.getString(13));
            //record.put("", cursor.getString(14));
            //record.put("", cursor.getString(15));
            //record.put("", cursor.getString(16));
            //record.put("", cursor.getString(17));
            //record.put("", cursor.getString(18));
            //record.put("", cursor.getString(19));
            //record.put("", cursor.getString(20));


            return record;
        }

        return null;
    }


    public boolean saveNestRecord(ContentValues record) {
        Log.d(TAG, "saveNestRecord(" + record.toString() +")");

        String sqlString = ""
                + "SELECT ref_num"
                + "  FROM " + NEST
                + " WHERE ref_num = '" + record.getAsString("ref_num") + "'";

        // search if record is in LocalDB
        Cursor cursor = mDB.rawQuery(sqlString, null);

        if (cursor.moveToFirst()) {
            // if record exists, archive
            // TODO edit existing record


        } else {
            // add new record
            Log.d(TAG, "saveNestRecord -> inserting");

            // if result of insert is -1, then failed
            if (mDB.insert(NEST, null, record) == -1) {
                return false;
            } else {
                return true;
            }
        }

        return true;
    }

    /**
     *
     * @param filepath
     */
    protected void parseCSVFile(String filepath) {

        // try opening the file
        FileReader file = null;
        try {
            file = new FileReader(filepath);
        } catch (Exception e1) {
            e1.printStackTrace();
            Toast.makeText(mContext, "Unable to open file. Try a different one or check permissions.",
                    Toast.LENGTH_SHORT).show();
            // exit if unable to read
            return;
        }

        BufferedReader buffer = new BufferedReader(file);
        String line = "";

        // try reading the first line
        try {
            // make sure the header line is valid
            // exit otherwise
            line = buffer.readLine();
            line = line.replace("\"", "");
            if (!line.equals(CSV_HEADER)) {
                Log.d(TAG, "Error - File header has changed\n" + CSV_HEADER + "\n" + line);
                Toast.makeText(mContext, "Error - File header has changed",
                        Toast.LENGTH_SHORT).show();
                Toast.makeText(mContext, "Contact admin or try older copy.",
                        Toast.LENGTH_LONG).show();

                return;
            }

        } catch (IOException e2) {
            e2.printStackTrace();
            return;
        }

        //start a database transaction to batch all the updates
        mDB.beginTransaction();

        // delete all existing records from info tables
        int numDeletedRecords = mDB.delete(NEST, "1", null);
        Log.d(TAG, "Deleted " + numDeletedRecords + " from " + NEST);
        // TODO delete other tables

        // used do-while instead of while in order to put the try-catch
        // inside the loop. If one record has error, then continue
        // processing the rest
        do {
            String[] fields = null;
            try {
                // read line from file
                line = buffer.readLine();

                // if no more lines, exit loop
                if (line == null) {
                    break;
                }

                Log.d(TAG, "processing CSV line:\n" + line);

                // some of the fields may contain commas, so the line cannot be
                // split using just comma, instead using ","
                // the leading and trailing " must be deleted first
                line = line.substring(1, line.length()-1);

                // split it
                fields = line.split("\",\"", -1);

                // if no nest num then skip
                if (fields[5].length() == 0) {
                    continue;
                }

                // place split values into record
                ContentValues record = new ContentValues(26);
                record.put("activity", fields[4]);
                record.put("nest_num", fields[5]);
                record.put("ref_num", fields[6]);
                record.put("activity_date", fields[7]);
                record.put("activity_comments", fields[13]);
                record.put("latitude", fields[16]);
                record.put("longitude", fields[17]);
                record.put("location", fields[19]);
                record.put("nest_mgmt", fields[20]);
                record.put("relocation", fields[22]);
                record.put("num_eggs_laid", fields[23]);
                record.put("num_relocations", fields[24]);
                record.put("relocation_date", fields[25]);
                record.put("new_latitude", fields[27]);
                record.put("new_longitude", fields[28]);
                record.put("new_location", fields[29]);
                record.put("num_washovers", fields[30]);
                record.put("num_loss_reports", fields[31]);
                record.put("num_lost_eggs", fields[33]);
                record.put("num_lost_hatchlings", fields[34]);
                record.put("lost_nest", fields[35]);
                record.put("emerge_date", fields[36]);
                record.put("inventory_date", fields[37]);
                record.put("clutch_count", fields[39]);
                record.put("locator", fields[54]);
                record.put("date_modified", fields[56]);

                // insert record into database
                mDB.insert(NEST, null, record);

            } catch (Exception e2) {
                e2.printStackTrace();
                Toast.makeText(mContext, "Error processing record:\n" + line, Toast.LENGTH_LONG).show();

                // proceed to next record
                continue;
            }
        } while (true);


        // commit db transaction
        try {
            mDB.setTransactionSuccessful();
        }
        catch(Exception e3) {
            e3.printStackTrace();
            Toast.makeText(mContext, "Error updating database. Contact admin.", Toast.LENGTH_LONG).show();
        }

        // end db transaction
        mDB.endTransaction();

        try {
            buffer.close();
        }
        catch (IOException e4) {
            e4.printStackTrace();
            Toast.makeText(mContext, "Error closing file. Contact admin.", Toast.LENGTH_LONG).show();
        }
        Toast.makeText(mContext, "File " + filepath + " processed succesfuly", Toast.LENGTH_SHORT).show();
    }

}