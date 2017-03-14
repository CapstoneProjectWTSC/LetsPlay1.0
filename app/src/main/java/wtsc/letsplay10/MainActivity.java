package wtsc.letsplay10;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

//import android.location.Location;

public class MainActivity extends AppCompatActivity implements

        OnSportsDataLoaded,
        OnScheduleDataLoaded,
        LocationListener,
        OnFacitiliesDataLoaded,
        OnUserDataLoaded,
GoogleApiClient.ConnectionCallbacks,
GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback,
        OnCameraIdleListener{

    // instance of the GetCurrentUser utility functions to get the user data from the database
    static dbGetCurrentUser getUser;
    private User currentUser;       // stores the current user object
    private dbGetSportsList dbGetSportsList;
    private List<Sport> allSportsList;
    private List<Facility> facilitiesList;
    private GoogleMap mMap;

  //  private dbGetFacilitiesList getFacils;
  //  private dbGetFacilitiesList getFacils;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private Marker mCurrLocationMarker;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    public static final String TAG = MainActivity.class.getSimpleName();
    private dbGetFacilitiesList getFacils;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences("userSettings", MODE_PRIVATE);

     //   SharedPreferences.Editor prefsEditor = preferences.edit(); // these line are for development
    //    prefsEditor.clear();            // these line are for development
     //   prefsEditor.apply();            // these line are for development

        String json = preferences.getString("User", "");

        if(json.equals(""))
        {
            startActivity(new Intent(getApplicationContext(),Introduction.class));
        }

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    public void onCurrentUserDataLoaded(User user) {

    }

    @Override
    public void onUserVerify(User user) {
        if (user == null) {
            getUser = new dbGetCurrentUser(MainActivity.this);
            getUser.execute("ADD_NEW", "Ricky", "Stambach", "gnameTest1",
                    "123456", "rstambach1@my.waketech.edu");
        }
    }

    @Override
    public void onNewUserAdded(User user) {
        String gn = user.getGameName();
        dbGetSportsList = new dbGetSportsList(MainActivity.this);
        dbGetSportsList.execute();

    }

    @Override
    public void onSportsDataLoaded(List<Sport> sports) {
        int i = sports.size();
    }

    @Override
    public void onScheduleDataLoaded(List<Schedule> schedules) {
        int i = schedules.size();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        /*
        mMap = map;
        mMap.setOnCameraIdleListener(this);
        map.getUiSettings().setZoomControlsEnabled(true);
        LatLng wtscPos = new LatLng(35.651143, -78.704099);
        map.addMarker(new MarkerOptions().position(wtscPos).title("Wake Tech Software Corp"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(wtscPos, 10));
        */
        mMap = map;
        mMap.setOnCameraIdleListener(this);
        map.getUiSettings().setZoomControlsEnabled(true);
 //       mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);     //change map type

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
            else
            {
                LatLng wtscPos = new LatLng(35.651143, -78.704099);
                map.addMarker(new MarkerOptions().position(wtscPos).title("Wake Tech Software Corp"));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(wtscPos, 10));
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onCameraIdle() {
        // returns current bounds
        LatLngBounds curBounds = mMap.getProjection().getVisibleRegion().latLngBounds;
        getFacils = new dbGetFacilitiesList(MainActivity.this);
        getFacils.execute(curBounds);
    }

    @Override
    public void onFacitiliesDataLoaded(List<Facility> facilities) {
        facilitiesList = facilities;
        if (facilities.size() > 0) {
            mMap.clear();
            for (Facility f : facilities) {
                LatLng markerPos = new LatLng(f.getLatitude(), f.getLongitude());
                mMap.addMarker(new MarkerOptions().position(markerPos).title(f.getName()));
            }
        }
    }


    //////////////////////////////////////////////////////////////////////////////////
    /*
    Methods for zooming in on current location
    Larson Young, 3/8/17
     */
    /////////////////////////////////////////////////////////////////////////////////

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,this);      //altered
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //@Override
    public void onLocationChanged(Location location)
    {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Starting Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mCurrLocationMarker = mMap.addMarker(markerOptions);


        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this); //altered
        }
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Permission was granted.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            //You can add here other case statements according to your requirement.
        }
    }
}
