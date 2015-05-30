package org.imdragon.pollutionpatrol;

import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class locationTest extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        OnConnectionFailedListener, LocationListener {
    private GoogleMap myMap;
    private GoogleApiClient myGoogleApiClient;
    public static final String TAG = locationTest.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationRequest myLocationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(2 * 1000)        // 2 seconds, in milliseconds
            .setFastestInterval(1 * 1000);
    int i = 0;
    private Location baseLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildGoogleApiClient();
        myGoogleApiClient.connect();
        setContentView(R.layout.activity_location_test);
        setUpMap();
    }

    private void setUpMap() {
        // get map from the fragment
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        //getMapAsync calls onMapReady
        mapFragment.getMapAsync(this);
        //set map fragment as a GoogleMap object to use for later
        myMap = mapFragment.getMap();


                myMap.addMarker(new MarkerOptions().position(new LatLng(34.4313533,-119.8901207)).snippet("Random Point").title("Woohoo"));
        myMap.addMarker(new MarkerOptions().position(new LatLng(34.420557, -119.896901)).snippet("Random Point").title("Woohoo"));
        myMap.addMarker(new MarkerOptions().position(new LatLng(34.420557, -119.896901)).snippet("Random Point").title("Woohoo"));
        myMap.addMarker(new MarkerOptions().position(new LatLng(34.420243, -119.894787)).snippet("Random Point").title("Woohoo"));
        myMap.addMarker(new MarkerOptions().position(new LatLng(34.458619, -120.021280)).snippet("Random Point").title("Woohoo"));

        //whenever the camera changes, cameraBounds will be updated
//        myMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
//            @Override
//            public void onCameraChange(CameraPosition cameraPosition) {
//                cameraBounds = myMap.getProjection().getVisibleRegion().latLngBounds;
//                Log.d("Bounds", cameraBounds.toString());
//            }
//        });
    }

    protected void onStart() {
        super.onStart();
        if (myGoogleApiClient != null) {
            myGoogleApiClient.connect();
        }
    }

    protected void onResume() {
        super.onResume();
        myGoogleApiClient.connect();
    }
@Override
    protected void onPause() {
        super.onPause();
        if (myGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(myGoogleApiClient, this);
            myGoogleApiClient.disconnect();

        }
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


    @Override
    public void onMapReady(final GoogleMap myMap) {
        myMap.setMyLocationEnabled(true);}



    // Location Awareness Below

    protected synchronized void buildGoogleApiClient() {
        myGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(myGoogleApiClient);

        if (location == null) {
            //do something here
            startLocationUpdates();
//            LocationServices.FusedLocationApi.requestLocationUpdates(myGoogleApiClient, myLocationRequest, this);
        } else {
            handleNewLocation(location);
            baseLocation = location;
            setUpMap();
        }
    }
    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                myGoogleApiClient, myLocationRequest, this);
    }

    private void handleNewLocation(Location location) {
        LatLng here = new LatLng(location.getLatitude(), location.getLongitude());

        myMap.addMarker(new MarkerOptions().position(here).title(String.valueOf(i++)));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(here)
                .zoom(17) // Sets the zoom
//                .bearing(90) // Sets the orientation of the camera to east
//                .tilt(30) // Sets the tilt of the camera to 30 degrees
                .build(); // Creates a CameraPosition from the builder

        myMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        Log.d(TAG, location.toString());
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        TextView mLatitudeText = (TextView) findViewById(R.id.latitude);
        mLatitudeText.setText((String.valueOf(location.getLatitude())) + (String.valueOf(location.getLongitude())));

        handleNewLocation(location);


    }

    // Location Awareness Above
}