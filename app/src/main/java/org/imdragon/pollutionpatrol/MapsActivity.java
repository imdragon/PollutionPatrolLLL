
package org.imdragon.pollutionpatrol;

import android.content.IntentSender;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, LocationListener, GoogleApiClient.OnConnectionFailedListener {

//    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = ;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Location location;
    private LatLng currentll;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationRequest myLocationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(2 * 1000)        // 2 seconds, in milliseconds
            .setFastestInterval(1 * 1000);
    private Location baseLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maplayout);
        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }
        @Override
        protected void onResume(){
            super.onResume();
            setUpMapIfNeeded();
        }

        /**
         * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
         * installed) and the map has not already been instantiated.. This will ensure that we only ever
         * call {@link #setUpMap()} once when {@link #mMap} is not null.
         * <p/>
         * If it isn't installed {@link SupportMapFragment} (and
         * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
         * install/update the Google Play services APK on their device.
         * <p/>
         * A user can return to this FragmentActivity after following the prompt and correctly
         * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
         * have been completely destroyed during this process (it is likely that it would only be
         * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
         * method in {@link #onResume()} to guarantee that it will be called.
         */

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        LatLng here = new LatLng(location.getLatitude(), location.getLongitude());
                CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(here).zoom(17) // Sets the zoom
                .bearing(90) // Sets the orientation of the camera to east
                .tilt(30) // Sets the tilt of the camera to 30 degrees
                .build(); // Creates a CameraPosition from the builder

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));    }

    // Location Awareness Below

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        TextView mLatitudeText = (TextView) findViewById(R.id.latitude);
        TextView mLongitudeText = (TextView) findViewById(R.id.longitude);
        if (mLastLocation != null) {
            mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
            mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
        }
        if (location == null) {
            //do something here
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, myLocationRequest, (com.google.android.gms.location.LocationListener) this);
        } else {
            handleNewLocation(location);
            baseLocation = location;
            setUpMap();
        }
    }

    private void handleNewLocation(Location location) {
        LatLng here = new LatLng(location.getLatitude(), location.getLongitude());

//        myMap.addMarker(new MarkerOptions().position(here).title(String.valueOf(i++)));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(here)
//                .zoom(17) // Sets the zoom
//                .bearing(90) // Sets the orientation of the camera to east
//                .tilt(30) // Sets the tilt of the camera to 30 degrees
                .build(); // Creates a CameraPosition from the builder

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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
//            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//        handleNewLocation(location);
//
//    }

    protected void createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    // Location Awareness Above

}

