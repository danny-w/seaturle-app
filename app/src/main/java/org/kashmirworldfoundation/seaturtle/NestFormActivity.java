package org.kashmirworldfoundation.seaturtle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class NestFormActivity extends AppCompatActivity {
    private static final String TAG = "***" + NestFormActivity.class.getSimpleName();

    /**
     * code used with requestPermissions method for location service
     */
    private static final int mLocationPremissionGrantedCode = 10;

    /**
     * spinners for values stored in database
     */
    private Spinner mComplexitySpinner, mObstructionSpinner,
            mSandSpinner, mLocationSpinner;

    /**
     * EditText's for location values
     */
    private EditText
            mNestID, mDateDeposited,
            mOriginalLatitude, mOriginalLongitude, mOriginalPosition, mOriginalEggCount,
            mNewLatitude, mNewLongitude, mNewPosition, mNewEggCount,
            mNestDescription;

    /**
     * generic EditText's that get updated with GPS locations
     */
    EditText mLatitude, mLongitude;

    /**
     * Location manager used to get GPS location
     */
    private static LocationManager mLocationManager;

    /**
     * Location listener used to get GPS location
     */
    private static LocationListener mLocationListener;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nest_form);

        initializeUI();
        initializeListeners();

        // register with app manager
        AppManager.initNestForm(this);
    }

    private void initializeListeners() {
        Log.d(TAG, "initializeListeners()");

        // add an on-click listner for all buttons
        View.OnClickListener onClickListener = new View.OnClickListener() {
            public void onClick(View v) {
                switch(v.getId()) {
                    case R.id.get_date_deposited:
                        //showDiaglog(R.layout.location_dialog);
                        break;
                    case R.id.get_original_location:
                        //Log.d(TAG, "get_new_location clicked");
                        requestLocation(mOriginalLatitude, mOriginalLongitude);
                        break;
                    case R.id.get_new_location:
                        //Log.d(TAG, "get_new_location clicked");
                        requestLocation(mNewLatitude, mNewLongitude);
                        break;
                    case R.id.save_nest:
                        Log.d(TAG, "Save pressed");
                        if (saveNestRecord()) {
                            NestFormActivity.this.finish();
                        }
                        break;
                    case R.id.cancel_nest:
                        Log.d(TAG, "Cancel pressed");
                        NestFormActivity.this.finish();
                        break;
                    default:
                        break;
                }
            }
        };

        // add listener to buttons
        ((Button) findViewById(R.id.get_date_deposited)).setOnClickListener(onClickListener);
        ((Button) findViewById(R.id.get_original_location)).setOnClickListener(onClickListener);
        ((Button) findViewById(R.id.get_new_location)).setOnClickListener(onClickListener);
        ((Button) findViewById(R.id.save_nest)).setOnClickListener(onClickListener);
        ((Button) findViewById(R.id.cancel_nest)).setOnClickListener(onClickListener);

    }

    private void initializeUI() {
        Log.d(TAG, "initializeUI()");

        // inialize the spinners using initializeSpinner method
        mComplexitySpinner = initializeSpinner(R.id.complexity_spinner, R.array.complexity_options);
        mObstructionSpinner = initializeSpinner(R.id.obstruction_spinner, R.array.obstruction_options);
        mSandSpinner = initializeSpinner(R.id.sand_spinner, R.array.sand_options);
        mLocationSpinner = initializeSpinner(R.id.location_spinner, R.array.location_options);

        // initialize all EditText's
        mNestID = (EditText) findViewById(R.id.nest_id);
        mDateDeposited = (EditText) findViewById(R.id.date_deposited);
        mOriginalLatitude = (EditText) findViewById(R.id.original_latitude);
        mOriginalLongitude = (EditText) findViewById(R.id.original_longitude);
        mOriginalPosition = (EditText) findViewById(R.id.original_position);
        mOriginalEggCount = (EditText) findViewById(R.id.original_egg_count);
        mNewLatitude = (EditText) findViewById(R.id.new_latitude);
        mNewLongitude = (EditText) findViewById(R.id.new_longitude);
        mNewPosition = (EditText) findViewById(R.id.new_position);
        mNewEggCount = (EditText) findViewById(R.id.new_egg_count);
        mNestDescription = (EditText) findViewById(R.id.nest_description);

        // nest ID cannot be changed for existing records
        // enable it only for new records
        //mNestID.setEnabled(mIsNewRecord);
    }


    /**
     * initialize a spinner using it's ID and that
     * of the string array containing it's options
     * @param spinnerId
     * @param optionsId
     * @return Spinner
     */
    private Spinner initializeSpinner(int spinnerId, int optionsId) {

        Spinner spinner = (Spinner) findViewById(spinnerId);

        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(getBaseContext(),
                        optionsId,
                        android.R.layout.simple_spinner_item);

        spinner.setAdapter(adapter);
        spinner.setSelection(0);

        return spinner;
    }



    private boolean saveNestRecord() {
        return true;

    }

    /**
     * process the request to get GPS location for original/new location
     * check if fields have values and prompt user for override action
     *
     * @param latitude
     * @param longitude
     */
    private void requestLocation(EditText latitude, EditText longitude) {
        Log.d(TAG, "requestLocation()");

        // set generic EditText's
        // this is needed because the parameters cannot be passed
        // to the dialog onClick listener
        mLatitude = latitude;
        mLongitude = longitude;

        // check if there already is a value in the fields and
        // prompt user accordingly. If not, then proceed to check permissions
        if (mLatitude.getText().toString().length() > 0 ||
                mLongitude.getText().toString().length() > 0) {

            // create a click listener for a dialog box
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

                // override the onClick method so that the request is processed
                // only when user clicks yes (BUTTON_POSITIVE)
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        requestLocation();
                    }
                }
            };

            // build the dialog box
            AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());
            builder.setMessage("Are you sure you want to override?")
                    .setPositiveButton("Override", dialogClickListener)
                    .setNegativeButton("Cancel", dialogClickListener)
                    .show();
        } else {
            requestLocation();
        }
    }

    /**
     * lock getLocation buttons to avoid clobbering
     */
    protected void lockLocationControls() {

        // display message
        mLatitude.setText("working");
        mLongitude.setText("working");

        // lock controlls buttons
        ((Button) findViewById(R.id.get_original_location)).setEnabled(false);
        ((Button) findViewById(R.id.get_new_location)).setEnabled(false);
    }

    /**
     * sets the text in corresponding latitude and longitude
     * if null is passed, clears the fields and displays error message
     * @param location
     */
    protected void setLocation(Location location) {

        // if location is null then permission was denied
        if (location == null) {
            mLatitude.setText("");
            mLongitude.setText("");
            Toast.makeText(this, "Unable to use GPS! Please enter manually.", Toast.LENGTH_SHORT).show();

        } else {
            mLatitude.setText(Double.toString(location.getLatitude()));
            mLongitude.setText(Double.toString(location.getLongitude()));
        }

        // re-enable buttons
        ((Button) findViewById(R.id.get_original_location)).setEnabled(true);
        ((Button) findViewById(R.id.get_new_location)).setEnabled(true);
    }


    /**
     * check if app has permission to access location service
     * this is needed for newer versions where user
     * can disable permissions despite menifests file settings
     */
    private void requestLocation() {
        Log.d(TAG, "requestLocation()");

        // if build >= 23, then check permissions during runtime
        // for build < 23, no need since permissions are in manifests
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ActivityCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {

            // if permissions are not granted then request them
            // the result of the request is handled by onRequestPermissionsResult
            // that is defined in class MainActivity
            ActivityCompat.requestPermissions(this, new String[]{
                            android.Manifest.permission.INTERNET,
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    mLocationPremissionGrantedCode);
        } else {
            getLocation();
        }
    }

    /**
     * gets location by requesting a single update from location manager
     * is called only after the permissions have been verified
     */
    public void getLocation() {
        Log.d(TAG, "getLocation()");

        // lock the controlls on NestForm to avoid clobbering
        lockLocationControls();

        // initialize location service
        mLocationManager =
                (LocationManager) getSystemService(MainActivity.LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "onLocationChanged()");

                setLocation(location);
                mLocationManager = null;
                mLocationListener = null;
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {}

            @Override
            public void onProviderEnabled(String s) {}

            @Override
            public void onProviderDisabled(String s) {

                // if gps is disabled, prompt user to enable it by starting a new activity
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };

        // TODO find better way to improve performance
        // TODO takes about 30 sec to get location
        // request one time location
        // permissions would have been already checked
        mLocationManager.requestSingleUpdate("gps", mLocationListener, null);

    }


}
