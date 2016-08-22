package org.kashmirworldfoundation.seaturtle;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class LoadDataActivity extends ListActivity {
    private static final String TAG = "***" + LoadDataActivity.class.getSimpleName();

    // root directory
    private final String ROOT = "/";

    // home directory
    private final String HOME =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();

    // stack representing current path. each node is a directory
    private Stack<String> path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_data);

        initGUI();
        initList(HOME);

        // register with AppManager
        AppManager.initLoadData(this);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(TAG, "onListItemClick");

        String filename = (String) getListAdapter().getItem(position);

        final String pathName = getPathName() + filename;

        if (new File(pathName).isDirectory()) {
            path.push(filename + "/");
            populateList();
        } else {

            // create a click listener for a dialog box
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

                // override the onClick method so that the request is processed
                // only when user clicks yes (BUTTON_POSITIVE)
                @Override
                public void onClick(DialogInterface dialog, int response) {
                    if (response == DialogInterface.BUTTON_POSITIVE) {
                        // TODO
                        AppManager.parseCSVFile(pathName);

                        // close activity
                        onBackPressed();
                    }
                }
            };

            // build the dialog box
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to use " + filename +"?")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("Cancel", dialogClickListener)
                    .show();
        }

    }

    /**
     * initialize the GUI
     */
    private void initGUI() {
        Button goFolderHome = (Button) findViewById(R.id.button_folder_home);
        Button goFolderUp = (Button) findViewById(R.id.button_folder_up);
        //Button goFolderRoot = (Button) findViewById(R.id.button_folder_root);

        // add on click listener for the Home button
        goFolderHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initList(HOME);
            }
        });

        // add on click listener for the Folder Up button
        goFolderUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                path.pop();
                if (path.empty()) {
                    initList(HOME);
                }
                populateList();
            }
        });

        /*
        // add on click listener for the Root button
        goFolderRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initList(ROOT);
            }
        });
        */
    }

    /**
     * initialize the list
     * @param pathName
     */
    private void initList(String pathName) {
        Log.d(TAG, "initList(" + pathName +")");

        String[] pathNodes = pathName.split("/");

        // reinitialize stack
        path = new Stack<String>();

        // split pathName into individual nodes (seperated by "/"
        for (String node : pathNodes) {
            path.push(node + "/");
        }

        populateList();
    }

    private void populateList() {
        String pathName = getPathName();

        setTitle(pathName);

        // Read all files sorted into the values-array
        List values = new ArrayList();
        File dir = new File(pathName);
        if (!dir.canRead()) {
            setTitle(getTitle() + " (inaccessible)");
        }
        String[] list = dir.list();
        if (list != null) {
            for (String file : list) {
                // ignore hidden files
                if (!file.startsWith(".")) {
                    values.add(file);
                }
            }
        }
        Collections.sort(values);

        // Put the data into the list
        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_2, android.R.id.text1, values);
        setListAdapter(adapter);

        //Toast.makeText(this, "Opening " + pathName, Toast.LENGTH_LONG).show();
    }

    /**
     * traverse the path stack and retrun the path as a string
     * @return string - path represented in stack
     */
    private String getPathName() {

        // initialize path name
        String pathName = "";
        // traverse stack and append each node to path name
        for (String node : path) {
            pathName = pathName + node;
        }

        return pathName;

    }
}
