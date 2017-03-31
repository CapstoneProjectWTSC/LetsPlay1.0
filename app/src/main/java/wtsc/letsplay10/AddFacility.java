package wtsc.letsplay10;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.google.android.gms.location.LocationListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by a1995 on 3/20/2017.
 */

public class AddFacility extends AppCompatActivity implements
        OnClickListener, LocationListener {

    private Facility newFacility;
    private double latitude;
    private double longitude;
    dbAddNewFacility db_AddNewFacility;
    dbGetFacilitiesList db_GetFacilitiesList;
    private Location myLastLocation;

    private EditText FacilityNameText2;     //top one for current location
    private EditText FacilityNameText;      //bottom one for find by address
    private EditText AddressText;
    private EditText CityText;
    private EditText StateText;
    private EditText ZipText;

    private RadioButton clButton;           //current location radio button
    private RadioButton addressButton;      //find by address button
    private Button createFacilityButton;    //button at bottom to create facility

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_facility);

        //preferences = getSharedPreferences("userSettings", MODE_PRIVATE);
        //String json = preferences.getString("User", "");

        FacilityNameText2 = (EditText) findViewById(R.id.FacilityNameText2);
        FacilityNameText = (EditText) findViewById(R.id.FacilityNameText);
        AddressText = (EditText) findViewById(R.id.AddressText);
        CityText = (EditText) findViewById(R.id.CityText);
        StateText = (EditText) findViewById(R.id.StateText);
        ZipText = (EditText) findViewById(R.id.ZipText);

        clButton = (RadioButton) findViewById(R.id.clButton);
        clButton.setOnClickListener(this);

        addressButton = (RadioButton) findViewById(R.id.addressButton);
        addressButton.setOnClickListener(this);

        createFacilityButton = (Button) findViewById(R.id.submitButton);
        createFacilityButton.setOnClickListener(this);
    }

    @Override
    public void onLocationChanged(Location location)
    {
        myLastLocation = location;

    }

    //run this if user selects to add facility based on current location

    public void findLocationInformation() throws IOException {
        Geocoder geocoder;
        List<Address> addressInformation;
        geocoder = new Geocoder(this, Locale.getDefault());

        addressInformation = geocoder.getFromLocation(this.latitude, this.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        String address = addressInformation.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addressInformation.get(0).getLocality();
        String state = addressInformation.get(0).getAdminArea();
        String zip = addressInformation.get(0).getPostalCode();
        String name = addressInformation.get(0).getFeatureName(); // Only if available else return NULL
        String notes = "";

        db_AddNewFacility = new dbAddNewFacility(AddFacility.this);

        newFacility = db_AddNewFacility.doInBackground(name + address + city + state + zip + Double.toString(latitude)
                + Double.toString(longitude) + notes);

    }


    //run this if add by address is selected.  Adds info using data gathered from user

    public void addLocationInformation() throws IOException {

        String address = AddressText.getText().toString();
        String city = CityText.getText().toString();
        String state = StateText.getText().toString();
        String zip = ZipText.getText().toString();
        String name = FacilityNameText.getText().toString();
        String notes = "";

        db_AddNewFacility = new dbAddNewFacility(AddFacility.this);

        newFacility = db_AddNewFacility.doInBackground(name + address + city + state + zip + Double.toString(latitude)
                + Double.toString(longitude) + notes);

    }


    public void onKey(View view, int keyCode, KeyEvent event){}

    /*              //could possibly add something like this to disable button until user enters something into field.
                    if(FacilityNameText2.length() > 0) //if user has entered something in the fields, the function will work
                {
     */

    public void onClick (View v) {
        switch (v.getId())
        {
            case R.id.clButton:
                    this.latitude = myLastLocation.getLatitude();
                    this.longitude = myLastLocation.getLongitude();
                    try {
                        findLocationInformation();
                    } catch (IOException IOE) {
                        IOE.printStackTrace();
                    }
                break;
            case R.id.addressButton:
                this.latitude = myLastLocation.getLatitude();
                this.longitude = myLastLocation.getLongitude();
                try{
                    addLocationInformation();
                }
                catch(IOException IOE)
                {
                    IOE.printStackTrace();
                }
                break;
            //case R.id.fromMap:
              //  break;
        }
    }
}