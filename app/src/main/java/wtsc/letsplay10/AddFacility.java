package wtsc.letsplay10;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import org.apache.commons.validator.routines.EmailValidator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.location.Address;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
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

    public void onKey(View view, int keyCode, KeyEvent event){}

    public void onClick (View v) {
        switch (v.getId()) {
            case R.id.myLocation:
                this.latitude = myLastLocation.getLatitude();
                this.longitude = myLastLocation.getLongitude();
                try{
                    findLocationInformation();
                }
                catch(IOException IOE)
                {
                    IOE.printStackTrace();
                }
                break;
            case R.id.viewAll:
                //<code></code>
                break;
            case R.id.fromMap:


                break;

        }
    }

    @Override
    public void onLocationChanged(Location location) {
        myLastLocation = location;
    }

    public void findLocationInformation() throws IOException
    {
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
}
