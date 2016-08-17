package org.kashmirworldfoundation.seaturtle;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

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

        // set nest id to passed id
        mNestID = nestID;

    }

    protected static Cursor getNestNewLocations() {
        return mDBHelper.getNestNewLocations();
    }

    protected static Cursor getNestOriginalLocations() {
        return mDBHelper.getNestOriginalLocations();
    }

}
