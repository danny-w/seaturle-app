package org.kashmirworldfoundation.seaturtle;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "***" + MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        initGui();

        AppManager.initApp(this);
    }

    private void initGui() {

        Button buttonMap = (Button) findViewById(R.id.button_map);
        Button buttonNewNest = (Button) findViewById(R.id.button_new_activity);

        buttonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("org.kashmirworldfoundation.seaturtle.MapActivity");
                startActivity(intent);
            }
        });

        buttonNewNest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppManager.launchNestForm();
            }
        });

        Spinner spinner = (Spinner) findViewById(R.id.spinner_activity_type);

        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(getBaseContext(),
                        R.array.activity_type_options,
                        android.R.layout.simple_spinner_item);

        spinner.setAdapter(adapter);
        spinner.setSelection(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
