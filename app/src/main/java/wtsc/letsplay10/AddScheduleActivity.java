package wtsc.letsplay10;

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

import java.util.ArrayList;
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
                        OnFacilitiesDataLoaded{

    private List<Sport> sportTypes;
    private Spinner spSportTypes;
    private Spinner spFacilities;
    private List<String> sportTypesNames = new ArrayList<String>();
    private EditText timeText, dateText;
    private List<Facility> facilitiesList;
    private List<String> facilitiesNames = new ArrayList<String>();
    private Button addButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_schedule );

        timeText = (EditText)findViewById(R.id.timeText);
        timeText.addTextChangedListener(onTimeTextChanged);
        dateText = (EditText)findViewById(R.id.dateText);
        dateText.addTextChangedListener(onDateTextChanged);
        addButton.setOnClickListener(onAddButtonClicked);

        spSportTypes = (Spinner) findViewById(R.id.sports_type_spinner );
        spFacilities = (Spinner)findViewById(R.id.facility_spinner);

        dbGetSportsList getSportsList = new dbGetSportsList(AddScheduleActivity.this);
        getSportsList.execute();


    }

    View.OnClickListener onAddButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RadioGroup fg = (RadioGroup)findViewById(R.id.facilitiesGroupBtns);
            switch (fg.getCheckedRadioButtonId()){
                case useCurrentRBTN:

                    break;

                case fromDatabaseRBTN:

                    break;

                case findOnMapRBTN:

                    break;
            }

        }
    };


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
}
