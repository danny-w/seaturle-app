package org.kashmirworldfoundation.seaturtle;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;

/**
 * Created by dwanna on 8/6/16.
 */
public class AppManager {
    private static final String TAG = "***" + AppManager.class.getSimpleName();

    /**
     * current DBHelper class
     */
    private static DBHelper mDBHelper;

    /**
     * current MainActivity class
     */
    private static MainActivity mMainActivity;

    /**
     * current LoadDataForm class
     */
    private static LoadDataActivity mLoadDataActivity;

    /**
     * current MapActivity class
     */
    private static MapActivity mMapActivity;

    /**
     * current nest form class
     */
    private static NestFormActivity mNestFormActivity;

    /**
     * nest id of current record being edited
     * set to null to indicate new record
     */
    private static String mNestID = null;

    /**
     * called by MainActivity when it starts
     * @param mainActivity
     */
    protected static void initApp(MainActivity mainActivity) {
        mMainActivity = mainActivity;
        mDBHelper = new DBHelper(mainActivity.getBaseContext());

    }

    protected static void initLoadData(LoadDataActivity loadDataActivity) {

        mLoadDataActivity = loadDataActivity;
    }

    /**
     * called by MapActivity when is starts
     * @param mapActivity
     */
    protected static void initMap(MapActivity mapActivity) {
        mMapActivity = mapActivity;

    }

    /**
     * called by NestFormActivity when it starts
     * @param nestFormActivity
     */
    protected static void initNestForm(NestFormActivity nestFormActivity) {
        mNestFormActivity = nestFormActivity;

        // check if mNestID is defined, then this is an exisiting record
        if (mNestID != null) {
            // populate nest form from database
        }
    }

    protected static void launchNestForm() {
        // assume new record, set nest id to null
        mNestID = null;

        // launch new nest form activity
        Intent intent = new Intent("org.kashmirworldfoundation.seaturtle.NestFormActivity");
        mMainActivity.startActivity(intent);
    }

    protected static void launchNestForm(String nestID) {
        launchNestForm();

        // set nest id
        mNestID = nestID;
    }

    /**
     * call the DBHelper initialize database
     * @return
     */
    protected static boolean initializeDB() {

        return true;
    }

    protected static Cursor getNestLocations() {

        return mDBHelper.getNestLocations();
    }

    protected static boolean saveNestRecord(ContentValues record) throws Exception {



        return true;
    }

    protected static boolean parseCSVFile(String pathName) {

        mDBHelper.parseCSVFile(pathName);
        return true;
    }


}
