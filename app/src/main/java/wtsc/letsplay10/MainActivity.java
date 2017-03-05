package wtsc.letsplay10;

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

import static wtsc.letsplay10.R.id.map;

public class MainActivity extends AppCompatActivity implements
        OnCameraIdleListener,
        OnUserDataLoaded,
        OnSportsDataLoaded,
        OnScheduleDataLoaded,
        OnFacitiliesDataLoaded,
        OnMapReadyCallback{

    // instance of the GetCurrentUser utility functions to get the user data from the database
    static GetCurrentUser getUser;
    private User currentUser;       // stores the current user object
    private GetSportsList getSportsList;
    private List<Sport> allSportsList;
    private List<Facility> facilitiesList;
    private GoogleMap mMap;
    private GetFacilitiesList getFacils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main );

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(map);
        mapFragment.getMapAsync(this);



    }


    @Override
    public void onCurrentUserDataLoaded(User user) {

    }

    @Override
    public void onUserVerify(User user) {
        if(user == null) {
            getUser = new GetCurrentUser(MainActivity.this);
            getUser.execute("ADD_NEW","Ricky","Stambach","gnameTest1",
                            "123456","rstambach1@my.waketech.edu");
        }
    }

    @Override
    public void onNewUserAdded(User user) {
        String gn = user.getGameName();
        getSportsList = new GetSportsList(MainActivity.this);
        getSportsList.execute();
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
        getFacils = new GetFacilitiesList(MainActivity.this);
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
