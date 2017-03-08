package wtsc.letsplay10;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MainActivity extends AppCompatActivity implements
        OnCameraIdleListener,
        OnSportsDataLoaded,
        OnScheduleDataLoaded,
        OnFacitiliesDataLoaded,
        OnMapReadyCallback{

    // instance of the GetCurrentUser utility functions to get the user data from the database
    static dbGetCurrentUser getUser;
    private User currentUser;       // stores the current user object
    private GetSportsList getSportsList;
    private List<Sport> allSportsList;
    private List<Facility> facilitiesList;
    private GoogleMap mMap;
    private dbGetFacilitiesList getFacils;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main );

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
        mMap = map;
        mMap.setOnCameraIdleListener(this);
        map.getUiSettings().setZoomControlsEnabled(true);
        LatLng wtscPos = new LatLng(35.651143, -78.704099);
        map.addMarker(new MarkerOptions().position(wtscPos).title("Wake Tech Software Corp"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(wtscPos,10));
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
        if(facilities.size()>0){
            mMap.clear();
            for(Facility f:facilities)
            {
                LatLng markerPos = new LatLng(f.getLatitude(), f.getLongitude());
                mMap.addMarker(new MarkerOptions().position(markerPos).title(f.getName()));
            }
        }
    }
}
