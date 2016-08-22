package org.kashmirworldfoundation.seaturtle;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends FragmentActivity implements
        OnMapReadyCallback, GoogleMap.OnMarkerClickListener, LocationListener {
    private static final String TAG = "***" + MapActivity.class.getSimpleName();


    private float mZoomLevel = 19; //This goes up to 21

    private GoogleMap mMap;

    // filter check boxes
    private CheckBox mCheckBoxDefault;
    private CheckBox mCheckBoxEmergeLessThan5;
    private CheckBox mCheckBoxEmergeMoreThan5;
    private CheckBox mCheckBoxMoreThan70;
    private CheckBox mCheckBoxMoreThan55;
    private CheckBox mCheckBoxMoreThan50;
    private CheckBox mCheckBoxMoreThan45;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //register with AppManager
        AppManager.initMap(this);

        // initialize check boxes
        mCheckBoxDefault = (CheckBox) findViewById(R.id.checkBox_nest_default);
        mCheckBoxEmergeLessThan5 = (CheckBox) findViewById(R.id.checkBox_nest_emerge_less_than_5);
        mCheckBoxEmergeMoreThan5 = (CheckBox) findViewById(R.id.checkBox_nest_emerge_more_than_5);
        mCheckBoxMoreThan70 = (CheckBox) findViewById(R.id.checkBox_nest_more_than_70);
        mCheckBoxMoreThan55 = (CheckBox) findViewById(R.id.checkBox_nest_more_than_55);
        mCheckBoxMoreThan50 = (CheckBox) findViewById(R.id.checkBox_nest_more_than_50);
        mCheckBoxMoreThan45 = (CheckBox) findViewById(R.id.checkBox_nest_more_than_45);

    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        // TODO remove after testing
        //AppManager.launchNestForm(marker.getTitle());
        mMap.clear();

        return false;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // this will maintain the  same zoom level
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                mZoomLevel = cameraPosition.zoom;
                //Toast.makeText(getBaseContext(), "New zoom level: " + mZoomLevel, Toast.LENGTH_SHORT).show();
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker marker) {
                AppManager.launchNestForm(marker.getTitle());
            }
        });

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);

            // Get LocationManager object from System Service LOCATION_SERVICE
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            // get the best location provider
            String provider = locationManager.getBestProvider(new Criteria(), true);

            // request updates every min 1 second and min 1 meter
            locationManager.requestLocationUpdates(provider, 1000L, 1f, this);

            // set map type
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

            // Get Current Location if possible and zoom in
            Location myLocation = locationManager.getLastKnownLocation(provider);
            if (myLocation != null) {
                onLocationChanged(myLocation);
            }

        } else {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(this, "Please enable GPS and check permissions!!!", Toast.LENGTH_LONG).show();
        }

        // add click listener for go button
        ((Button) findViewById(R.id.button_refresh_map)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.clear();
                addMarkers();
            }
        });

        // add click listener for help button
        ((Button) findViewById(R.id.button_show_map_help)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("org.kashmirworldfoundation.seaturtle.HelpActivity");
                startActivity(intent);
            }
        });

        addMarkers();
    }

    /**
     * add color coded markers to the map
     * - ignore nests with inventory date set
     * - if emerge data set
     *      - if less than 5 days -> green
     *      - if 5 or more days -> purle
     * - if no emerge date
     *      - if 70 or more days -> purple
     *      - if 55 or more days -> red
     *      - if 50 or more days -> orange
     *      - if 45 or more days -> yellow
     *      - if less than 45 days -> green
     */
    private void addMarkers() {

        // get records from database
        Cursor cursor = AppManager.getNestLocations();

        // move to first record and confirm non-empty set
        if (cursor.moveToFirst()) {

            // loop thru query results and add them to list
            do {
                // extract record from cursor
                // nest_num, latitude, longitude, new_latitude, new_longitude, activity_date, emerge_date, inventory_date

                String refNum = "";
                float latitude = 0;
                float longitude = 0;

                try {
                    // skip record if inventory date set
                    if (cursor.getString(7).length() > 0) {
                        continue;
                    }

                    // use refNum instead of nestNum
                    refNum = cursor.getString(0);

                    // check if new_latitude and new_Longitude are there then use those values
                    // other wise use original values
                    if (cursor.getString(3).length() > 0  && cursor.getString(4).length() > 0) {
                        latitude = Float.valueOf(cursor.getString(3));
                        longitude = Float.valueOf(cursor.getString(4));
                    }
                    else {
                        latitude = Float.valueOf(cursor.getString(1));
                        longitude = Float.valueOf(cursor.getString(2));
                    }
                    LatLng nestLatLng = new LatLng(latitude, longitude);

                    // calculate days nest has been active
                    int numDaysActivity = AppUtil.calcNumDaysElapsed(cursor.getString(5));

                    // calculate days since nest first emerge date
                    int numDaysEmerge = AppUtil.calcNumDaysElapsed(cursor.getString(6));

                    Log.d(TAG, "nest #" + refNum + " @ " + latitude + "x" + longitude
                            + ", #" + cursor.getString(5) + "# " + numDaysActivity + " days active"
                            + ", #" + cursor.getString(6) + "# " + numDaysEmerge + " days emerged");

                    // color code for marker
                    float hue;

                    // body of marker pop up box
                    String markerSnippet = numDaysActivity + " days";

                    // check if there is an emerge date
                    if (numDaysEmerge > 0) {
                        // add to marker snippet
                        markerSnippet = markerSnippet + "\n" + numDaysEmerge + " days";

                        if (numDaysEmerge < 5) {
                            // check filter, skip if corresponding check box is not checked
                            if (!mCheckBoxEmergeLessThan5.isChecked()) { continue; }
                            hue = AppUtil.colorToHSV(this, R.color.color_nest_emerge_less_than5);
                        } else {
                            // check filter, skip if corresponding check box is not checked
                            if (!mCheckBoxEmergeMoreThan5.isChecked()) { continue; }
                            hue = AppUtil.colorToHSV(this, R.color.color_nest_emerge_more_than5);
                        }
                    } else {
                        if (numDaysActivity >= 70) {
                            // check filter, skip if corresponding check box is not checked
                            if (!mCheckBoxMoreThan70.isChecked()) { continue; }
                            hue = AppUtil.colorToHSV(this, R.color.color_nest_more_than70);

                        } else if (numDaysActivity >= 55) {
                            // check filter, skip if corresponding check box is not checked
                            if (!mCheckBoxMoreThan55.isChecked()) { continue; }
                            hue = AppUtil.colorToHSV(this, R.color.color_nest_more_than55);

                        } else if (numDaysActivity >= 50) {
                            // check filter, skip if corresponding check box is not checked
                            if (!mCheckBoxMoreThan50.isChecked()) { continue; }
                            hue = AppUtil.colorToHSV(this, R.color.color_nest_more_than50);

                        } else if (numDaysActivity >= 45) {
                            // check filter, skip if corresponding check box is not checked
                            if (!mCheckBoxMoreThan45.isChecked()) { continue; }
                            hue = AppUtil.colorToHSV(this, R.color.color_nest_more_than45);

                        } else {
                            // check filter, skip if corresponding check box is not checked
                            if (!mCheckBoxDefault.isChecked()) { continue; }
                            hue = AppUtil.colorToHSV(this, R.color.color_nest_less_than45);
                        }
                    }

                    mMap.addMarker(new MarkerOptions()
                            .position(nestLatLng)
                            .title(refNum)
                            //.snippet("My Snippet"+"\n"+"1st Line Text"+"\n"+"2nd Line Text"+"\n"+"3rd Line Text")
                            .snippet(markerSnippet)
                            .icon(BitmapDescriptorFactory.defaultMarker(hue)));

                    Log.d(TAG, "addNests -> " + refNum + ": " + latitude + ", " + longitude);

                } catch (Exception e) {
                    Toast.makeText(this, "Error parsing Nest ID " + refNum, Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Error\n" + cursor.toString());
                    e.printStackTrace();
                }

            } while (cursor.moveToNext());


            // define window that pops up when marker is clicked
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                @Override
                public View getInfoWindow(Marker arg0) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    Context context = getBaseContext();

                    LinearLayout info = new LinearLayout(context);
                    info.setOrientation(LinearLayout.VERTICAL);

                    TextView title = new TextView(context);
                    title.setTextColor(Color.BLACK);
                    title.setGravity(Gravity.CENTER);
                    title.setTypeface(null, Typeface.BOLD);
                    title.setText(marker.getTitle());


                    TextView snippet = new TextView(context);
                    snippet.setTextColor(Color.GRAY);
                    snippet.setText(marker.getSnippet());

                    info.addView(title);
                    info.addView(snippet);

                    return info;
                }
            });
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        // Create a LatLng object for the current location
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        // Show the current location in Google Map
        //mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(mZoomLevel).build();
        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        // TODO
    }

    @Override
    public void onProviderEnabled(String s) {
        // TODO

    }

    @Override
    public void onProviderDisabled(String s) {
        // TODO

    }


    // You can use an OnMarkerClickListener to listen for click events on the marker.
    // To set this listener on the map, call GoogleMap.setOnMarkerClickListener(OnMarkerClickListener).
    // When a user clicks on a marker, onMarkerClick(Marker) will be called
    // and the marker will be passed through as an argument.
    // This method returns a boolean that indicates whether you have consumed the event
    // (i.e., you want to suppress the default behavior).
    // If it returns false, then the default behavior will occur in addition to your custom behavior.
    // The default behavior for a marker click event is to show its info window (if available)
    // and move the camera such that the marker is centered on the map.

}
