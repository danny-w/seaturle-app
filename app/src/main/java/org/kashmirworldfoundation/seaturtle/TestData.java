package org.kashmirworldfoundation.seaturtle;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by dwanna on 8/6/16.
 */
public class TestData {
    private static String TAG = "***" + TestData.class.getSimpleName();

    private static boolean mIsFirstRun = true;


    /**
     * empty constructor
     */
    public TestData() {

    }

    /**
     * load a sample of data for testing
     * @return  true
     */
    public static boolean loadTestData(SQLiteDatabase db, String tableName) {

        // check if this is the first run
        // if not then exit without any action

        if (!mIsFirstRun) {
            Log.d(TAG, "loadTestData -> already run before");
            return false;
        }

        String[] fields = new String[]
                {"nest_id", "date_deposited", "complexity", "obstruction", "sand", "location", "orig_latitude", "orig_longitude", "orig_position", "orig_egg_count", "new_latitude", "new_longitude", "new_position", "new_egg_count", "nest_description"};

        String[][] data = new String[][]{
                {"101", "06/10/16", "Complex", "Obstructed", "Dune", "Backbeach", "31.686489", "-81.135941", "5", "126", "", "", "3", "120", "Complex nest #101"},
                {"102", "06/12/16", "Complex", "Obstructed", "Dune", "Backbeach", "31.619655", "-81.139125", "5", "126", "31.620135", "-81.139045", "3", "120", "Complex nest #102"},
                {"103", "06/15/16", "Complex", "Obstructed", "Dune", "Backbeach", "31.619655", "-81.139125", "5", "126", "31.620625", "-81.138545", "3", "120", "Complex nest #103"},
                {"104", "06/15/16", "Complex", "Obstructed", "Dune", "Backbeach", "31.68364", "-81.136112", "5", "126", "31.68431", "-81.135152", "3", "120", "Complex nest #104"},
                {"105", "06/17/16", "Complex", "Obstructed", "Dune", "Backbeach", "31.594801", "-81.152171", "5", "126", "", "", "3", "120", "Complex nest #105"},
                {"106", "06/19/16", "Complex", "Obstructed", "Dune", "Backbeach", "31.679843", "-81.136628", "5", "126", "", "", "3", "120", "Complex nest #106"},
                {"107", "06/19/16", "Complex", "Obstructed", "Dune", "Backbeach", "31.685247", "-81.135855", "5", "126", "31.685837", "-81.135585", "3", "120", "Complex nest #107"},
                {"108", "06/20/16", "Complex", "Obstructed", "Dune", "Backbeach", "31.623748", "-81.136722", "5", "126", "31.624638", "-81.136002", "3", "120", "Complex nest #108"},
                {"109", "06/22/16", "Complex", "Obstructed", "Dune", "Backbeach", "31.675095", "-81.136714", "5", "126", "31.675445", "-81.135894", "3", "120", "Complex nest #109"},
                {"110", "06/25/16", "Complex", "Obstructed", "Dune", "Backbeach", "31.577253", "-81.161098", "5", "126", "31.577383", "-81.160398", "3", "120", "Complex nest #110"},
                {"111", "06/27/16", "Complex", "Obstructed", "Dune", "Backbeach", "31.578716", "-81.185817", "5", "126", "", "", "3", "120", "Complex nest #111"},
                {"112", "06/28/16", "Complex", "Obstructed", "Dune", "Backbeach", "31.667352", "-81.135512", "5", "126", "31.667802", "-81.134932", "3", "120", "Complex nest #112"},
                {"113", "06/30/16", "Complex", "Obstructed", "Dune", "Backbeach", "31.578716", "-81.185817", "5", "126", "", "", "3", "120", "Complex nest #113"},
                {"114", "07/01/16", "Complex", "Obstructed", "Dune", "Backbeach", "31.583688", "-81.156634", "5", "126", "31.584668", "-81.156244", "3", "120", "Complex nest #114"},
                {"115", "07/02/16", "Complex", "Obstructed", "Dune", "Backbeach", "31.618906", "-81.136713", "5", "126", "31.619126", "-81.135963", "3", "120", "Complex nest #115"},
                {"116", "07/03/16", "Complex", "Obstructed", "Dune", "Backbeach", "31.664794", "-81.134997", "5", "126", "", "", "3", "120", "Complex nest #116"},
                {"117", "07/03/16", "Complex", "Obstructed", "Dune", "Backbeach", "31.644922", "-81.13431", "5", "126", "", "", "3", "120", "Complex nest #117"},
                {"118", "07/05/16", "Complex", "Obstructed", "Dune", "Backbeach", "31.662895", "-81.134825", "5", "126", "31.663075", "-81.134755", "3", "120", "Complex nest #118"},
                {"119", "07/08/16", "Complex", "Obstructed", "Dune", "Backbeach", "31.607503", "-81.14255", "5", "126", "31.608083", "-81.14251", "3", "120", "Complex nest #119"},
                {"120", "07/09/16", "Complex", "Obstructed", "Dune", "Backbeach", "31.666402", "-81.135512", "5", "126", "31.666622", "-81.135252", "3", "120", "Complex nest #120"},
                {"121", "07/09/16", "Complex", "Obstructed", "Dune", "Backbeach", "31.614813", "-81.139117", "5", "126", "", "", "3", "120", "Complex nest #121"},
                {"122", "07/11/16", "Complex", "Obstructed", "Dune", "Backbeach", "31.565974", "-81.165037", "5", "126", "31.565984", "-81.164807", "3", "120", "Complex nest #122"},
                {"123", "07/11/16", "Complex", "Obstructed", "Dune", "Backbeach", "31.640099", "-81.134997", "5", "126", "31.640509", "-81.134077", "3", "120", "Complex nest #123"},
                {"124", "07/11/16", "Complex", "Obstructed", "Dune", "Backbeach", "31.62563", "-81.133623", "5", "126", "", "", "3", "120", "Complex nest #124"},
                {"125", "07/11/16", "Complex", "Obstructed", "Dune", "Backbeach", "31.661178", "-81.133945", "5", "126", "31.661948", "-81.133865", "3", "120", "Complex nest #125"},
        };

        for (String[] record: data) {
            ContentValues nestRecord = new ContentValues();
            for (int j=0; j<fields.length; j++) {
                nestRecord.put(fields[j], record[j]);
            }
            db.insert(tableName, null, nestRecord);
            Log.d("***** -> ", nestRecord.toString());
        }

        // reset first run indicator
        mIsFirstRun = false;

        return true;
    }
}
