package wtsc.letsplay10;

import android.content.Context;
import android.content.Intent;
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
import java.util.List;
import java.util.Locale;

import com.google.gson.Gson;
/**
 * Created by a1995 on 3/20/2017.
 */

public class AddFacility extends AppCompatActivity implements
        OnClickListener{

    private Facility newFacility;
    private long latitude;
    private long longitude;
    dbAddNewFacility db_AddNewFacility;
    dbGetFacilitiesList db_GetFacilitiesList;

    public void onKey(View view, int keyCode, KeyEvent event){}
    public void onClick (View v){
        switch (v.getId()) {
            case R.id.myLocation:
                //Get location
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(this, Locale.getDefault());

                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String zip = addresses.get(0).getPostalCode();
                String name = addresses.get(0).getFeatureName(); // Only if available else return NULL
                String notes = "";

                db_AddNewFacility = new dbAddNewFacility(AddFacility.this);

                newFacility = db_AddNewFacility.doInBackground(name + address + city + state + zip + Long.toString(latitude) + Long.toString(longitude) + notes);
                break;
            case R.id.viewAll:
                //<code></code>
                break;
            case R.id.fromMap:


                break;

        }
    }
}
