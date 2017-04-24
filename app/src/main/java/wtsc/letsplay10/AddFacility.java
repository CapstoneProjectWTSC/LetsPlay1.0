package wtsc.letsplay10;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Created by a1995 on 3/20/2017.
 */

public class AddFacility extends AppCompatActivity implements
        OnClickListener,
        LocationListener,
        OnNewFacilityAdded,
        OnCheckedChangeListener,
        OnItemClickListener,
        OnFindFacility,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Facility newFacility = new Facility();
    private double latitude;
    private double longitude;
    dbAddNewFacility db_AddNewFacility;
    dbGetFacilitiesList db_GetFacilitiesList;
    private Location mLastLocation;
    private Boolean addressButtonChecked;
    private Boolean clButtonChecked;
    private dbFindFacility db_findFacility;
    private Address currentAddress;
    private Geocoder geocoder;
    private GoogleApiClient mGoogleApiClient;

  //  private EditText FacilityNameText2;     //top one for current location
    private EditText FacilityNameText;      //bottom one for find by address
    private EditText AddressText;
    private EditText CityText;
    private EditText StateText;
    private EditText ZipText;

    private RadioButton clButton;           //current location radio button
    private RadioButton addressButton;      //find by address button
    private RadioGroup rBGroup;
    private Button createFacilityButton;    //button at bottom to create facility

    private static final String LOG_TAG = "ExampleApp";

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";

    //------------ make your specific key ------------
    private static final String API_KEY = "AIzaSyA791EE19mAOYygOJnMht8ywW8VgLzfG0k";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_facility);

        Intent intent = getIntent();
        mLastLocation = intent.getParcelableExtra("LAST_LOCATION");
        TextView tv = (TextView)findViewById(R.id.currentLocationTxt);
        tv.setText("Lat: "+String.valueOf(mLastLocation.getLatitude())+"  Lng: "+ String.valueOf(mLastLocation.getLongitude()));

        FacilityNameText = (EditText) findViewById(R.id.FacilityNameText);
        AddressText = (EditText) findViewById(R.id.AddressText);
        CityText = (EditText) findViewById(R.id.CityText);
        StateText = (EditText) findViewById(R.id.StateText);
        ZipText = (EditText) findViewById(R.id.ZipText);

        rBGroup = (RadioGroup) findViewById(R.id.RGroup);
        rBGroup.setOnCheckedChangeListener(this);
        clButton = (RadioButton) findViewById(R.id.useCurrentLocationBTN);
        addressButton = (RadioButton) findViewById(R.id.useAddressBTN);
        createFacilityButton = (Button) findViewById(R.id.submitButton);
        createFacilityButton.setOnClickListener(this);
        buildGoogleApiClient();
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


    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        String str = (String) adapterView.getItemAtPosition(position);
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    public static ArrayList<String> autocomplete(String input) {
        ArrayList<String> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&components=country:us");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());

            System.out.println("URL: "+url);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {

            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<String>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                System.out.println("============================================================");
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }



    private void displaySnackBarMessage (String message, int duration ){
        Snackbar facilityAddedSnackbar = Snackbar.make(findViewById(R.id.snackbarCoordinatorLayout), message, duration);
        facilityAddedSnackbar.show();
    }


    @Override
    public void onLocationChanged(Location location)
    {
        mLastLocation = location;

    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        if (checkedId == R.id.useCurrentLocationBTN)
        {
            displaySnackBarMessage("Use current location",Snackbar.LENGTH_LONG);
        }
        else
        {
            displaySnackBarMessage("Enter in an address",Snackbar.LENGTH_LONG);
        }
    }

    // User clicked submit button - validate facility name from database.
    public void onClick (View v) {

        switch (v.getId())
        {
            case R.id.submitButton:
                db_findFacility = new dbFindFacility(AddFacility.this);
                db_findFacility.execute(FacilityNameText.getText().toString());
                break;
        }
    }

    // Return from dbFindFacility validate if new facility name is not already used
    @Override
    public void onDBFindFacility(Facility NewFacility) {
        if(NewFacility == null){        // facility name is not already used
            int selectedId = rBGroup.getCheckedRadioButtonId();
            newFacility.setLatitude(mLastLocation.getLatitude());
            newFacility.setLongitude(mLastLocation.getLongitude());
            if (selectedId == R.id.useCurrentLocationBTN)   // create new facility from current location
            {
                try {
                    findLocationInformation();
                } catch (IOException IOE) {
                    IOE.printStackTrace();
                }
            }

            else if (selectedId == R.id.useAddressBTN)  // create new facility from address input
            {
                try {
                    addLocationInformation();
                } catch (IOException IOE) {
                    IOE.printStackTrace();
                }
            }
            // add new facility to database
            db_AddNewFacility = new dbAddNewFacility(AddFacility.this);
            db_AddNewFacility.execute(newFacility.getName(), newFacility.getAddress1(), newFacility.getAddress2(),
                    newFacility.getCity(), newFacility.getState(), newFacility.getZip(),
                    Double.toString(newFacility.getLatitude()), Double.toString(newFacility.getLongitude()),
                    newFacility.getNotes());
        } else
        {       // facility name is already used show message
            displaySnackBarMessage("Name Allready In Database",Snackbar.LENGTH_LONG);
        }
    }

    //run this if user selects to add facility based on current location
    public void findLocationInformation() throws IOException {

        Address geoAddress = getGeocoder(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
        Geocoder geocoder;
        List<Address> addressInformation;
        geocoder = new Geocoder(this, Locale.getDefault());

        addressInformation = geocoder.getFromLocation(newFacility.getLatitude(), newFacility.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        if(addressInformation.size() > 0) {
            newFacility.setAddress1(addressInformation.get(0).getAddressLine(0)); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            newFacility.setAddress2("");
            newFacility.setCity(addressInformation.get(0).getLocality());
            String[] spState = addressInformation.get(0).getAddressLine(1).split(" ");
            newFacility.setState(spState[1]);
            newFacility.setZip(addressInformation.get(0).getPostalCode());
   //         if (addressInformation.get(0).getFeatureName() != "") {
     //           newFacility.setName(addressInformation.get(0).getFeatureName());
                //        newFacility.setAddress1(addressInformation.get(0).getAddressLine(0));
       //     } else {
         //       newFacility.setName(FacilityNameText.getText().toString());
       //     }
            newFacility.setName(FacilityNameText.getText().toString());
        }
        newFacility.setNotes("");
    }

    private Address getGeocoder(LatLng latitudeLongitude) throws IOException{
        Address geoAddress = null ;  //new Address(Locale.getDefault())
 //       Geocoder geocoder;
        List<Address> addressInformation;
        geocoder = new Geocoder(this, Locale.getDefault());
        // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        addressInformation = geocoder.getFromLocation(latitudeLongitude.latitude, latitudeLongitude.longitude, 1);
        if(addressInformation.size() > 0) {
            geoAddress = addressInformation.get(0);
        }
        return geoAddress;
    }


    //run this if add by address is selected.  Adds info using data gathered from user

    public void addLocationInformation() throws IOException {

        newFacility.setAddress1(AddressText.getText().toString());
        newFacility.setAddress2("");
        newFacility.setCity(CityText.getText().toString());
        newFacility.setState(StateText.getText().toString());
        newFacility.setZip(ZipText.getText().toString());
        newFacility.setName(FacilityNameText.getText().toString());
        newFacility.setNotes("");
    }

    // return from adding new facility to database
    @Override
    public void onDBNewFacilityAdded(Facility NewFacility) {
        String name = NewFacility.getName();
        String message = "The facility " + name + " successfully added!";
        displaySnackBarMessage(message,Snackbar.LENGTH_LONG); // display confirmation
        // start main activity
 //       startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try{
            currentAddress = getGeocoder(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
            if(currentAddress!=null) {
                if (currentAddress.getAddressLine(0).indexOf(currentAddress.getFeatureName()) > 0) {
                    FacilityNameText.setText(currentAddress.getFeatureName());
                }
            }
        }
        catch (IOException IOE) {
            IOE.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}