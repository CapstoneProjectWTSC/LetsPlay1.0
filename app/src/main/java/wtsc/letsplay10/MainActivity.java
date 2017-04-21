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
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
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
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static wtsc.letsplay10.R.id.map;


public class MainActivity extends AppCompatActivity implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapClickListener,
        OnMapReadyCallback,
        OnCameraIdleListener,
        PlaceSelectionListener,
        OnScheduleDataLoaded{

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private Place selectedPlace;
    private Marker mCurrLocationMarker;
    private SharedPreferences preferences;
    private Toolbar toolbar;
    private LatLngBounds curBounds;
    private Marker lastMarkerClicked;
    private List<MarkerOptions> showMarkerOpList;
    private final static LatLng WTSC_POS = new LatLng(35.651143, -78.704099);
    private int currentZoomLevel;
    private boolean isHybrid;
    private boolean selectedPlaceMarkerShowing;
    private String markerFiltersType;
    private User currentUser;
    private Sport selectedSportType;
    private boolean isDialogReturn;
    private String bDateText, eDateText;
    private Date bDateTime, eDateTime;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {


            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            preferences = getSharedPreferences("userSettings", MODE_PRIVATE);
            // Load user preferences

            // ---------------------------for testing -----------------------------------------------
            //      SharedPreferences.Editor editor = preferences.edit();
            //      editor.clear();
            //       editor.commit();
//------------------------------------------------------------------------------------------


            String json = preferences.getString("User", "");
            //          json="";
            // Check if user is signed in - if not start sign activity
            if (json.equals("")) {
                startActivityForResult(new Intent(getApplicationContext(), SignIn.class), 1);
            }
            // Get saved user class
            currentUser = new User();
            Gson gson = new Gson();
            currentUser = gson.fromJson(json, User.class);
            // Set map tpye preference
            isHybrid = preferences.getBoolean("IS_HYBRID", false);


            // Load class variables
            currentZoomLevel = 10;
            isHybrid = false;
            selectedPlaceMarkerShowing = false;
            buildGoogleApiClient();
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(1000);
            mLocationRequest.setFastestInterval(1000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            isDialogReturn = false;
            bDateTime = new Date();
            eDateTime = new Date();
            markerFiltersType = "MY_SCHEDULES";

            // Set up toolbar
            toolbar = (Toolbar) findViewById(R.id.menu_bar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setLogo(R.drawable.lets_play_icon6);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

            // Declare & setup Google Places fragment
            PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                    getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
            autocompleteFragment.setOnPlaceSelectedListener(this);
            autocompleteFragment.setHint("Find Location");

            // Declare main activity's map fragment
            SupportMapFragment mapFragment =
                    (SupportMapFragment) getSupportFragmentManager().findFragmentById(map);
            mapFragment.getMapAsync(this);
        }
        catch (Exception ex){
            String message = ex.getMessage().toString();
            Snackbar invalidbDate = Snackbar.make(findViewById(R.id.snackbarCoordinatorLayout), message, Snackbar.LENGTH_LONG);
            invalidbDate.show();
        }

    }


        @Override
    public void onPlaceSelected(Place place) {
        // TODO: Get info about the selected place.
       // String s = (String)place.getName();
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
//        autocompleteFragment.
        selectedPlace = place;
        LatLng selectedLatLng = selectedPlace.getLatLng();
        Marker selectedMarker = mMap.addMarker(new MarkerOptions()
                                    .position(selectedLatLng)
                                    .title(selectedPlace.getName().toString()));
        selectedMarker.showInfoWindow();;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng, currentZoomLevel));
        selectedPlaceMarkerShowing = true;
    }

    @Override
    public void onError(Status status) {
        // TODO: Handle the error.
   }



    @Override
    public void onPause() {
        super.onPause();
        if(mLastLocation!=null) {
            SharedPreferences.Editor prefsEditor = preferences.edit();
            prefsEditor.putFloat("Location_LAT", (float) mLastLocation.getLatitude());
            prefsEditor.putFloat("Location_LNG", (float) mLastLocation.getLongitude());
            prefsEditor.putBoolean("IS_HYBRID",isHybrid);
            prefsEditor.commit();
        }
    }

        @Override
    public void onSaveInstanceState(Bundle outState) {
         super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if(mLastLocation==null){
            Location local = new Location("");
            local.setLatitude(savedInstanceState.getFloat("Location_LAT"));
            local.setLongitude(savedInstanceState.getFloat("Location_LNG"));
            mLastLocation.set(local);
        }
        isHybrid = preferences.getBoolean("IS_HYBRID",false);
     }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(lastMarkerClicked != null){lastMarkerClicked.hideInfoWindow();}
        switch (item.getItemId()) {
            case R.id.view_schedules:
                // User chose the "Settings" item, show the app settings UI...
                return true;
//-------------------------------- view menu sub menu ---------------------------------
            case R.id.my_schedules:
                 markerFiltersType = "MY_SCHEDULES";
                 onCameraIdle();
                 return true;

            case R.id.sports_type:
                //TODO create select sports type activity
                Intent sportsTypeIntent = new Intent(getApplicationContext(), SportsFilterActivity.class);
                startActivityForResult(sportsTypeIntent,2);
                return true;

            case R.id.date_n_times:
                Intent dateTimeFilterIntent = new Intent(getApplicationContext(), DateFilterActivity.class);
                startActivityForResult(dateTimeFilterIntent,3);
                return true;

            case R.id.view_all:
                markerFiltersType = "ALL_SCHEDULES";
                onCameraIdle();
                return  true;
// --------------------end sub menu -----------------------------------------------------

            case R.id.add_schedule :
                Intent addSchIntent = new Intent(getApplicationContext(), AddScheduleActivity.class);
                addSchIntent.putExtra("LAST_LOCATION",mLastLocation);
                startActivity(addSchIntent);
                return true;

            case R.id.add_facility:
                Intent addFacIntent = new Intent(getApplicationContext(), AddFacility.class);
                addFacIntent.putExtra("LAST_LOCATION",mLastLocation);
                startActivity(addFacIntent);
                return true;

            case R.id.isHybridCheckBox:
                if(item.isChecked()){
                    item.setChecked(false);
                    isHybrid = false;
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                }else {
                    item.setChecked(true);
                    isHybrid = true;
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID );
                }
                SharedPreferences.Editor prefsEditor = preferences.edit();
                prefsEditor.putBoolean("IS_HYBRID", isHybrid);
                prefsEditor.commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode) {
            case 1:
                String json = preferences.getString("User", "");
                currentUser = new User();
                Gson gson = new Gson();
                currentUser = gson.fromJson(json, User.class);
                break;
            case 2:     // sports type
                if(resultCode == RESULT_OK){
                    selectedSportType = data.getParcelableExtra("SELECTED_SPORT");
                    markerFiltersType = "SPORTS_TYPE";
                    onCameraIdle();
                }
                break;
            case 3:     // date & time
                if(resultCode == RESULT_OK){
                    markerFiltersType = "DATE_TIME";
                    SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy");
                    bDateText = data.getExtras().getString("BEGINNING_DATE_TIME");
                    eDateText = data.getExtras().getString("ENDING_DATE_TIME");

                    try {
                        Date parsed = df.parse(bDateText);
                        bDateTime = new Date(parsed.getTime() );
                    } catch (ParseException e) {
                        String message = "Invalided Start Date or Time";
                        Snackbar invalidbDate = Snackbar.make(findViewById(R.id.snackbarCoordinatorLayout), message, Snackbar.LENGTH_LONG);
                        invalidbDate.show();
                        markerFiltersType = "MY_SCHEDULES";
                    }

                    try {
                        Date parsed = df.parse(eDateText);
                        eDateTime = new Date(parsed.getTime() );
                    } catch (ParseException e) {
                        String message = "Invalided Ending Date or Time";
                        Snackbar invalideDate = Snackbar.make(findViewById(R.id.snackbarCoordinatorLayout), message, Snackbar.LENGTH_LONG);
                        invalideDate.show();
                        markerFiltersType = "MY_SCHEDULES";
                    }
                    onCameraIdle();
                }
                break;

            default:
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu , menu);
        MenuItem item = menu.findItem(R.id.isHybridCheckBox);
        item.setChecked(isHybrid);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setOnCameraIdleListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        if(isHybrid){
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        }else {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkLocationPermission()) {
                mMap.setMyLocationEnabled(true);
            }
        }
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(WTSC_POS, currentZoomLevel));
        map.addMarker(new MarkerOptions().position(WTSC_POS).title("Wake Tech Software Corp"));
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        lastMarkerClicked = marker;
        return false;
    }
//==================================================================================================================
    @Override
    public void onCameraIdle() {
        if(lastMarkerClicked != null) {
            if (lastMarkerClicked.isInfoWindowShown()) {
                return;
            }
        }
        curBounds = mMap.getProjection().getVisibleRegion().latLngBounds;
        if(!selectedPlaceMarkerShowing) {
            switch (markerFiltersType){
                case "MY_SCHEDULES":
                    dbGetMySchedulesMarkers mySchedules = new dbGetMySchedulesMarkers(MainActivity.this, this);
                    mySchedules.execute(new UserBounds(currentUser,curBounds));
                    break;
                case "ALL_SCHEDULES":
                    dbGetAllSchedulesMarkers allSchedules = new dbGetAllSchedulesMarkers(MainActivity.this, this );
                    allSchedules.execute(new UserBounds(currentUser,curBounds));
                    break;
                case "DATE_TIME":
                    isDialogReturn = true;
                    dbGetDateScheduleMarkers dateSchedules = new dbGetDateScheduleMarkers(MainActivity.this, this);
                    dateSchedules.execute(new DateTimeBounds(bDateTime,eDateTime,curBounds));
                    break;
                case "SPORTS_TYPE":
                    isDialogReturn = true;
                    dbGetSportTypeScheduleMarkers sportsTypeSchedules = new dbGetSportTypeScheduleMarkers(MainActivity.this, this);
                    sportsTypeSchedules.execute(new SportsBounds(selectedSportType,curBounds));
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onScheduleDataLoaded(List<MarkerOptions> scheduleMarkers) {
        showMarkerOpList = scheduleMarkers;
        mMap.clear();
        if(scheduleMarkers != null && scheduleMarkers.size() > 0){
            for(MarkerOptions sM : scheduleMarkers ){
                Marker marker = mMap.addMarker(sM);
            }
        }
        mMap.addMarker(new MarkerOptions().position(WTSC_POS).title("Wake Tech Software Corp"));
        setCurrentLocation(mLastLocation);
    }

//==================================================================================================================
    @Override
    public void onMapClick(LatLng latLng) {
        selectedPlaceMarkerShowing = false;
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .enableAutoManage(this, 0, this)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (!isDialogReturn && checkLocationPermission()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);      //altered
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if(mLastLocation != null){
                setCurrentLocation(mLastLocation);
                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude())));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(currentZoomLevel));
            }
        }
        isDialogReturn = false;
    }

    @Override
    public void onConnectionSuspended(int i) {
        int a =i;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        String s = connectionResult.getErrorMessage();
    }

    //@Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        setCurrentLocation(location);
        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),location.getLongitude())));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(currentZoomLevel));
        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this); //altered
        }
    }


    private void setCurrentLocation(Location location){
        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("My Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mCurrLocationMarker = mMap.addMarker(markerOptions);
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
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
                    String message = "Permission Denied";
                    Snackbar invalidLogin = Snackbar.make(findViewById(R.id.snackbarCoordinatorLayout), message, Snackbar.LENGTH_LONG);
                    invalidLogin.show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            //You can add here other case statements according to your requirement.
        }
    }
}
