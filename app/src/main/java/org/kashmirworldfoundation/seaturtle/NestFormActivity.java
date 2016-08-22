package org.kashmirworldfoundation.seaturtle;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class NestFormActivity extends AppCompatActivity {
    private static final String TAG = "***" + NestFormActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nest_form);

        // register with app manager
        AppManager.initNestForm(this);
    }

    public void populateFields(ContentValues record) {

        // prevent keyboard from popping up
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        ((EditText) findViewById(R.id.nest_form_ref_num)).setText(record.getAsString("ref_num"));
        ((EditText) findViewById(R.id.nest_form_activity_date)).setText(record.getAsString("activity_date"));
        ((EditText) findViewById(R.id.nest_form_activity_num_days)).setText(record.getAsString("activity_num_days"));
        ((EditText) findViewById(R.id.nest_form_emerge_date)).setText(record.getAsString("emerge_date"));
        ((EditText) findViewById(R.id.nest_form_emerge_num_days)).setText(record.getAsString("emerge_num_days"));
        ((EditText) findViewById(R.id.nest_form_nest_mgmt)).setText(record.getAsString("nest_mgmt"));
        ((EditText) findViewById(R.id.nest_form_activity_comment)).setText(record.getAsString("activity_comment"));
        ((EditText) findViewById(R.id.nest_form_latitude)).setText(record.getAsString("latitude"));
        ((EditText) findViewById(R.id.nest_form_longitude)).setText(record.getAsString("longitude"));
        ((EditText) findViewById(R.id.nest_form_location)).setText(record.getAsString("location"));
        //((EditText) findViewById(R.id.nest_form_)).setText(record.getAsString(""));
        //((EditText) findViewById(R.id.nest_form_)).setText(record.getAsString(""));
        //((EditText) findViewById(R.id.nest_form_)).setText(record.getAsString(""));
        //((EditText) findViewById(R.id.nest_form_)).setText(record.getAsString(""));
        //((EditText) findViewById(R.id.nest_form_)).setText(record.getAsString(""));
        //((EditText) findViewById(R.id.nest_form_)).setText(record.getAsString(""));
        //((EditText) findViewById(R.id.nest_form_)).setText(record.getAsString(""));
        //((EditText) findViewById(R.id.nest_form_)).setText(record.getAsString(""));
        //((EditText) findViewById(R.id.nest_form_)).setText(record.getAsString(""));
        //((EditText) findViewById(R.id.nest_form_)).setText(record.getAsString(""));
    }



}
