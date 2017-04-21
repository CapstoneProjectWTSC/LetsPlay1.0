package wtsc.letsplay10;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


/**
 * Created by a1995 on 3/20/2017.
 */

public class AddFacility extends AppCompatActivity implements
        OnClickListener, LocationListener, OnNewFacilityAdded, OnCheckedChangeListener {

    private double latitude;
    private double longitude;
    public static LatLng addFromMapLatLng;
    dbAddNewFacility db_AddNewFacility;
    private Location mLastLocation;

    private EditText FacilityNameText2;     //top one for current location
    private EditText FacilityNameText;      //bottom one for find by address
    private EditText AddressText;
    private EditText CityText;
    private EditText StateText;
    private EditText ZipText;

    private RadioButton clButton;           //current location radio button
    private RadioButton addressButton;      //find by address button
    private RadioGroup rBGroup;
    private Button createFacilityButton;    //button at bottom to create facility
    private Button addFromMapButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_facility);

        //preferences = getSharedPreferences("userSettings", MODE_PRIVATE);
        //String json = preferences.getString("User", "");
        Intent intent = getIntent();
        mLastLocation = intent.getParcelableExtra("LAST_LOCATION");
        TextView tv = (TextView)findViewById(R.id.currentLocationTxt);
        tv.setText("Lat: "+String.valueOf(mLastLocation.getLatitude())+"  Lng: "+ String.valueOf(mLastLocation.getLongitude()));

        FacilityNameText2 = (EditText) findViewById(R.id.FacilityNameText2);
        FacilityNameText = (EditText) findViewById(R.id.FacilityNameText);
        AddressText = (EditText) findViewById(R.id.AddressText);
        CityText = (EditText) findViewById(R.id.CityText);
        StateText = (EditText) findViewById(R.id.StateText);
        ZipText = (EditText) findViewById(R.id.ZipText);

        rBGroup = (RadioGroup) findViewById(R.id.RGroup);
        rBGroup.setOnCheckedChangeListener(this);

        clButton = (RadioButton) findViewById(R.id.clButton);

        addressButton = (RadioButton) findViewById(R.id.addressButton);

        createFacilityButton = (Button) findViewById(R.id.submitButton);
        createFacilityButton.setOnClickListener(this);

        addFromMapButton = (Button) findViewById(R.id.addFromMap);
        addFromMapButton.setOnClickListener(this);
    }

    @Override
    public void onLocationChanged(Location location)
    {
        mLastLocation = location;

    }

    /**
     * Finds the location information of a potential new facility using the latitude and longitude,
     * then calls the addToDatabase method.
     */

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
        String name = FacilityNameText2.getText().toString();
        String notes = "";

        addToDatabase(name, address1, address2, city, state, zip, notes);

    }


    /**
     * Reads the information from the EditText fields into variables and then passes them to the addToDatabase method.
     */

    public void readUserInput() throws IOException {

        String name = FacilityNameText.getText().toString();
        String address1 = AddressText.getText().toString();
        String address2 = "";
        String city = CityText.getText().toString();
        String state = StateText.getText().toString();
        String zip = ZipText.getText().toString();
        String notes = "";

        addToDatabase(name, address1, address2, city, state, zip, notes);
    }

    public void addToDatabase(String name, String address1, String address2, String city, String state, String zip, String notes)
    {
        db_AddNewFacility = new dbAddNewFacility(AddFacility.this);

        db_AddNewFacility.execute(name, address1, address2, city, state, zip, Double.toString(latitude),
                Double.toString(longitude), notes);
    }


    public void onKey(View view, int keyCode, KeyEvent event){}

    /*              //could possibly add something like this to disable button until user enters something into field.
                    if(FacilityNameText2.length() > 0) //if user has entered something in the fields, the function will work
                {
     */

    /**
     * On click listener that listens fro a click on the submit button or the select from map button;
     * the relevant procedure is then executed.
     */
    public void onClick (View v) {
        int selectedId = rBGroup.getCheckedRadioButtonId();

        switch (v.getId())
        {
            case R.id.submitButton:

                    if (selectedId == R.id.clButton)
                    {
                        this.latitude = mLastLocation.getLatitude();
                        this.longitude = mLastLocation.getLongitude();
                        try {
                            findLocationInfo();
                        } catch (IOException IOE) {
                            IOE.printStackTrace();
                        }
                    }

                    else if (selectedId == R.id.addressButton)
                    {
                        try {
                            readUserInput();
                        } catch (IOException IOE) {
                            IOE.printStackTrace();
                        }
                    }

                break;

            case R.id.addFromMap:
                startActivity(new Intent(getApplicationContext(), AddFromMap.class));
                break;
        }
    }

    /**
     * Listener that determines which radio button was checked. A message is then displayed for
     * the corresponding button.
     */

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        if (checkedId == R.id.clButton)
        {
            String message = "Use current location";
            Snackbar facilityAddedSnackbar = Snackbar.make(findViewById(R.id.snackbarCoordinatorLayout), message, Snackbar.LENGTH_LONG);
            facilityAddedSnackbar.show();
        }
        else
        {
            String message = "Enter in an address";
            Snackbar facilityAddedSnackbar = Snackbar.make(findViewById(R.id.snackbarCoordinatorLayout), message, Snackbar.LENGTH_LONG);
            facilityAddedSnackbar.show();
        }
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