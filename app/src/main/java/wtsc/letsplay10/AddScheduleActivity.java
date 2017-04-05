package wtsc.letsplay10;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static wtsc.letsplay10.R.id.findOnMapRBTN;
import static wtsc.letsplay10.R.id.fromDatabaseRBTN;
import static wtsc.letsplay10.R.id.useCurrentRBTN;

/**
 * Created by Ricky Stambach on 3/30/2017.
 */

public class AddScheduleActivity extends AppCompatActivity implements
                        OnSportsDataLoaded,
                        AdapterView.OnItemSelectedListener,
                        OnFacilitiesDataLoaded,
                        OnNewScheduleUserScheduleAdded{

    private List<Sport> sportTypes;
    private Spinner spSportTypes;
    private Spinner spFacilities;
    private List<String> sportTypesNames = new ArrayList<String>();
    private EditText timeText, dateText;
    private List<Facility> facilitiesList;
    private List<String> facilitiesNames = new ArrayList<String>();
    private Button addButton;
    private RadioGroup facilitiesGroup;
    private Location mLastLocation;
    private Schedule newSchedule;
    private UserSchedule newUserSchedule;
    private SharedPreferences preferences;
    private User currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_schedule );

        Intent intent = getIntent();
        mLastLocation = intent.getParcelableExtra("LAST_LOCATION");
        TextView tv = (TextView)findViewById(R.id.currentLocationTxt);
        tv.setText("Lat: "+String.valueOf(mLastLocation.getLatitude())+"  Lng: "+ String.valueOf(mLastLocation.getLongitude()));

        timeText = (EditText)findViewById(R.id.timeText);
        timeText.addTextChangedListener(onTimeTextChanged);
        dateText = (EditText)findViewById(R.id.dateText);
        dateText.addTextChangedListener(onDateTextChanged);
        addButton = (Button)findViewById(R.id.addScheduleBTN);
        addButton.setOnClickListener(onAddButtonClicked);
        facilitiesGroup = (RadioGroup)findViewById(R.id.facilitiesGroupBtns);
        facilitiesGroup.check(R.id.useCurrentRBTN);
        facilitiesGroup.setOnClickListener(facilitiesGroupClicked);
        newSchedule = new Schedule();
        newUserSchedule = new UserSchedule();

        spSportTypes = (Spinner) findViewById(R.id.sports_type_spinner );
        spFacilities = (Spinner)findViewById(R.id.facility_spinner);
        spFacilities.setOnItemSelectedListener(facilitiesSpinnerOnItemSelected);

        Gson gson = new Gson();
        preferences = getSharedPreferences("userSettings", MODE_PRIVATE);
        String json = preferences.getString("User", "");
        currentUser = gson.fromJson(json,User.class);

        dbGetSportsList getSportsList = new dbGetSportsList(AddScheduleActivity.this);
        getSportsList.execute();


    }

    View.OnClickListener facilitiesGroupClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Location newLocation = new Location("");
            switch (facilitiesGroup.getCheckedRadioButtonId()){
                case useCurrentRBTN:

                    break;

                case fromDatabaseRBTN:

                    break;

                case findOnMapRBTN:

                    break;
            }
        }
    };




    View.OnClickListener onAddButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            long time = System.currentTimeMillis();
            Date dateTime = new Date();

            Facility newFacility = new Facility();
            switch (facilitiesGroup.getCheckedRadioButtonId()){
                case useCurrentRBTN:

                    break;

                case fromDatabaseRBTN:
                    newFacility = facilitiesList.get(spFacilities.getSelectedItemPosition());
                    break;

                case findOnMapRBTN:

                    break;
            }
            SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy hh:mmaa");
            String s1 = new String(dateText.getText().toString());
            String s2 = new String(timeText.getText().toString());
            s2.replace(" ","");
            s1 = s1+" "+s2;

            try {
                Date parsed = df.parse(s1);
                dateTime = new Date(parsed.getTime() );

            } catch (ParseException e) {
                e.printStackTrace();
            }
            newSchedule.setSportID(sportTypes.get(spSportTypes.getSelectedItemPosition()).getID());
            newSchedule.setFacilityID(spFacilities.getSelectedItemPosition());
            newSchedule.setScheduleDateTime(dateTime);
            newUserSchedule.setUserID(currentUser.getID());
            ScheduleUserSchedule newSUS = new ScheduleUserSchedule(newSchedule,newUserSchedule);
            dbAddNewSchedule addNewScheduleDB = new dbAddNewSchedule(AddScheduleActivity.this);
            addNewScheduleDB.execute(newSUS);

        }
    };

    @Override
    public void onNewScheduleUserScheduleAdded(ScheduleUserSchedule scheduleUserSchedule) {

    }


    TextWatcher onDateTextChanged = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
    };

    TextWatcher onTimeTextChanged = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
    };

    @Override
    public void onSportsDataLoaded(List<Sport> sports) {
        sportTypes = sports;
        sportTypesNames.clear();
        for(Sport s: sportTypes ){
            sportTypesNames.add(s.getName());
        }
        ArrayAdapter<String> sportsAdapter = new ArrayAdapter<String>
                      (this,R.layout.support_simple_spinner_dropdown_item, sportTypesNames);
        spSportTypes.setAdapter(sportsAdapter);
        spSportTypes.setOnItemSelectedListener(this);

        dbGetFacilitiesList getFacilitiesListList = new dbGetFacilitiesList(AddScheduleActivity.this);
        getFacilitiesListList.execute();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            int sel = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    @Override
    public void onFacilitiesDataLoaded(List<Facility> facilities) {
        facilitiesList = facilities;
        facilitiesNames.clear();
        for(Facility f: facilitiesList){
            facilitiesNames.add(f.getName());
        }
        ArrayAdapter<String> facilityAdapter = new ArrayAdapter<String>
                (this,R.layout.support_simple_spinner_dropdown_item, facilitiesNames);
        spFacilities.setAdapter(facilityAdapter);

    }

        private boolean facilitiesGruopIsLoaded = false;
    AdapterView.OnItemSelectedListener facilitiesSpinnerOnItemSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if(facilitiesGruopIsLoaded) {
                facilitiesGroup.check(R.id.fromDatabaseRBTN);
            }
            else
            {facilitiesGruopIsLoaded = true;}
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };


}
