package wtsc.letsplay10;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;

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
        OnItemClickListener{

    private Facility newFacility;
    private double latitude;
    private double longitude;
    dbAddNewFacility db_AddNewFacility;
    dbGetFacilitiesList db_GetFacilitiesList;
    private Location mLastLocation;
    private Boolean addressButtonChecked;
    private Boolean clButtonChecked;

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

        //preferences = getSharedPreferences("userSettings", MODE_PRIVATE);
        //String json = preferences.getString("User", "");
        Intent intent = getIntent();
        mLastLocation = intent.getParcelableExtra("LAST_LOCATION");
        TextView tv = (TextView)findViewById(R.id.currentLocationTxt);
        tv.setText("Lat: "+String.valueOf(mLastLocation.getLatitude())+"  Lng: "+ String.valueOf(mLastLocation.getLongitude()));

        AutoCompleteTextView autoCompView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);

        autoCompView.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.list_item));
        autoCompView.setOnItemClickListener(this);

  //      FacilityNameText2 = (EditText) findViewById(R.id.FacilityNameText2);
   //     FacilityNameText = (EditText) findViewById(R.id.FacilityNameText);
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

    class GooglePlacesAutocompleteAdapter extends ArrayAdapter<String> implements Filterable {
        private ArrayList<String> resultList;

        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index);
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }








    @Override
    public void onLocationChanged(Location location)
    {
        mLastLocation = location;

    }

    //run this if user selects to add facility based on current location

    public void findLocationInformation() throws IOException {
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
        String name = FacilityNameText.getText().toString();
 //       String name = FacilityNameText2.getText().toString();
        String notes = "";

        db_AddNewFacility = new dbAddNewFacility(AddFacility.this);

        db_AddNewFacility.execute(name, address1, address2,city, state, zip, Double.toString(latitude),
                Double.toString(longitude), notes);

    }


    //run this if add by address is selected.  Adds info using data gathered from user

    public void addLocationInformation() throws IOException {

        String address1 = AddressText.getText().toString();
        String address2 = "";
        String city = CityText.getText().toString();
        String state = StateText.getText().toString();
        String zip = ZipText.getText().toString();
        String name = FacilityNameText.getText().toString();
        String notes = "";

        db_AddNewFacility = new dbAddNewFacility(AddFacility.this);

        db_AddNewFacility.execute(name, address1, address2,city, state, zip, Double.toString(latitude),
                 Double.toString(longitude), notes);


    }


    public void onKey(View view, int keyCode, KeyEvent event){}

    /*              //could possibly add something like this to disable button until user enters something into field.
                    if(FacilityNameText2.length() > 0) //if user has entered something in the fields, the function will work
                {
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
                            findLocationInformation();
                        } catch (IOException IOE) {
                            IOE.printStackTrace();
                        }
                    }

                    else if (selectedId == R.id.addressButton)
                    {
                        try {
                            addLocationInformation();
                        } catch (IOException IOE) {
                            IOE.printStackTrace();
                        }
                    }


                break;
            //case R.id.fromMap:
              //  break;
        }
    }

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

    @Override
    public void onDBNewFacilityAdded(Facility NewFacility) {
        String name = NewFacility.getName();
        String message = "The facility " + name + " successfully added!";
        Snackbar facilityAddedSnackbar = Snackbar.make(findViewById(R.id.snackbarCoordinatorLayout), message, Snackbar.LENGTH_LONG);
        facilityAddedSnackbar.show();
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
    }
}