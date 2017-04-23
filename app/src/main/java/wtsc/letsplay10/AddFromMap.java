package wtsc.letsplay10;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.DialogInterface;
import android.app.AlertDialog;
import android.location.Address;
import android.location.Geocoder;
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
import android.widget.EditText;

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

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static wtsc.letsplay10.R.id.map;


public class AddFromMap extends AppCompatActivity implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapClickListener,
        OnMapReadyCallback,
        OnCameraIdleListener,
        PlaceSelectionListener,
        OnNewFacilityAdded {

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
    private final static LatLng WTSC_POS = new LatLng(35.651143, -78.704099);
    private int currentZoomLevel;
    private boolean selectedPlaceMarkerShowing;
    private String markerFiltersType;
    private String nameFromUser;
    private dbAddNewFacility db_AddNewFacility;
    private User currentUser;
    private boolean isDialogReturn;
    private double latitude;
    private double longitude;


    /**
     * This class is made to complement the AddFacility class. When the "Select from map" button is clicked on that page, this page is displayed. It's
     * functionally the same as the MainActivity Class, but with some changes to support the AddFacility class.
     * We could probably implement this class into that one, but for now, this is what I've created.
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_from_map);

        currentZoomLevel = 12;
        selectedPlaceMarkerShowing = false;

        buildGoogleApiClient();
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        toolbar = (Toolbar) findViewById(R.id.menu_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setLogo(R.drawable.lets_play_icon6);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        isDialogReturn = false;

        markerFiltersType = "MY_SCHEDULES";
        preferences = getSharedPreferences("userSettings", MODE_PRIVATE);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(this);
        autocompleteFragment.setHint("Find Location");

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(map);
        mapFragment.getMapAsync(this);

// ---------------------------for testing -----------------------------------------------
  //      SharedPreferences.Editor editor = preferences.edit();
  //      editor.clear();
 //       editor.commit();
//------------------------------------------------------------------------------------------


    }


        @Override
    public void onPlaceSelected(Place place) {
        // TODO: Get info about the selected place.
       // String s = (String)place.getName();
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
//        autocompleteFragment.
        selectedPlace = place;
        final LatLng selectedLatLng = selectedPlace.getLatLng();
            this.latitude = selectedLatLng.latitude;
            this.longitude = selectedLatLng.longitude;

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
            default:
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setOnCameraIdleListener(this);
        mMap.setOnMarkerClickListener(this);
        map.getUiSettings().setZoomControlsEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
        /**
         * This is the code that is different from the MainActivity class.
         * It is a simple dialogue box that will be sure that a user wants to add this facility
         * We could probably alter this later, if we don't want to use a dialogue box, but it'll
         * work for now.
         */

        final AlertDialog.Builder locationNameBuilder = new AlertDialog.Builder(this);
        locationNameBuilder.setTitle("Please enter in a name for this facility:");

        final EditText input = new EditText(this);

        locationNameBuilder.setView(input);

        locationNameBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                nameFromUser = input.getText().toString();
                try {
                    findLocationInfo();
                } catch (IOException IOE)
                {
                    IOE.printStackTrace();
                }
            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Would you like to add this facility?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        locationNameBuilder.show();
                        dialog.dismiss();



                    }
                });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.dismiss();
            }
        });

        AlertDialog addFacilityAlert = builder.create();
        addFacilityAlert.show();

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

    public void findLocationInfo() throws IOException {
        Geocoder geocoder;
        List<Address> addressInformation;
        geocoder = new Geocoder(this, Locale.getDefault());

        addressInformation = geocoder.getFromLocation(this.latitude, this.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        String address1 = addressInformation.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String address2 = "";
        String city = addressInformation.get(0).getLocality();
        String[] spState = addressInformation.get(0).getAddressLine(1).split(" ");
        String state = spState[1];
        String zip = addressInformation.get(0).getPostalCode();
        String name = nameFromUser;
        String notes = "";

        addToDatabase(name, address1, address2, city, state, zip, notes);

    }

    public void addToDatabase(String name, String address1, String address2, String city, String state, String zip, String notes)
    {
        db_AddNewFacility = new dbAddNewFacility(AddFromMap.this);

        db_AddNewFacility.execute(name, address1, address2, city, state, zip, Double.toString(latitude),
                Double.toString(longitude), notes);
    }

    /**
     * When a new facility is added, a message is displayed, and the MainActivity class is loaded.
     */
    @Override
    public void onDBNewFacilityAdded(Facility NewFacility) {
        String name = NewFacility.getName();
        String message = "The facility " + name + " successfully added!";
        Snackbar facilityAddedSnackbar = Snackbar.make(findViewById(R.id.snackbarCoordinatorLayout), message, Snackbar.LENGTH_LONG);
        facilityAddedSnackbar.show();
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
    }
}
