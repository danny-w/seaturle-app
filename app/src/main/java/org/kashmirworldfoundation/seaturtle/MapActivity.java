package org.kashmirworldfoundation.seaturtle;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MapActivity extends FragmentActivity
        implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private static final String TAG = "***" + MapActivity.class.getSimpleName();

    private static final float ZOOM_LEVEL = 16; //This goes up to 21

    private GoogleMap mMap;

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
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        AppManager.launchNestForm(marker.getTitle());

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

        /*
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                AppManager.launchNestForm(marker.getTitle());
                return true;
            }
        });
        */
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

            // Create a criteria object to retrieve provider
            Criteria criteria = new Criteria();

            // Get the name of the best provider
            String provider = locationManager.getBestProvider(criteria, true);

            // set map type
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

            Toast.makeText(getBaseContext(), "Location provider: " + provider, Toast.LENGTH_SHORT).show();
            // Get Current Location if possible and zoom in
            Location myLocation = locationManager.getLastKnownLocation(provider);
            if (myLocation != null) {
                Toast.makeText(getBaseContext(), "Location found!", Toast.LENGTH_SHORT).show();
                // Create a LatLng object for the current location
                LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());

                // Show the current location in Google Map
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_LEVEL));
            }


        } else {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(getBaseContext(), "Permission denied!", Toast.LENGTH_SHORT).show();
        }

        addNests();

    }

    private void addNests() {

        Cursor cursor = AppManager.getNestNewLocations();

        // move to first record and confirm non-empty set
        if (cursor.moveToFirst()) {

            // format date is entered in
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yy");

            Calendar today = Calendar.getInstance();
            Calendar nestingDate = Calendar.getInstance();


            float hue;

            // loop thru query results and add them to list
            do {
                String nestID = cursor.getString(0);
                float latitude = Float.valueOf(cursor.getString(1));
                float longitude = Float.valueOf(cursor.getString(2));
                LatLng nestLatLng = new LatLng(latitude, longitude);

                /*
                String dt = "2012-01-04";  // Start date

                try {
                    today.setTime(sdf.parse(dt));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                today.add(Calendar.DATE, 40);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
                SimpleDateFormat sdf1 = new SimpleDateFormat("MM-dd-yyyy");
                String output = sdf1.format(c.getTime());
                */

                hue = BitmapDescriptorFactory.HUE_GREEN;

                mMap.addMarker(new MarkerOptions()
                        .position(nestLatLng)
                        .title(nestID)
                        //.snippet("My Snippet"+"\n"+"1st Line Text"+"\n"+"2nd Line Text"+"\n"+"3rd Line Text")
                        .icon(BitmapDescriptorFactory.defaultMarker(hue)));
                //mMap.addMarker(new MarkerOptions().position(nestLatLng).title(nestID));


                Log.d(TAG, "addNests -> " + nestID + ": " + latitude + ", " + longitude);

            } while (cursor.moveToNext());


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


    //You can use an OnMarkerClickListener to listen for click events on the marker. To set this listener on the map, call GoogleMap.setOnMarkerClickListener(OnMarkerClickListener). When a user clicks on a marker, onMarkerClick(Marker) will be called and the marker will be passed through as an argument. This method returns a boolean that indicates whether you have consumed the event (i.e., you want to suppress the default behavior). If it returns false, then the default behavior will occur in addition to your custom behavior. The default behavior for a marker click event is to show its info window (if available) and move the camera such that the marker is centered on the map.

}
