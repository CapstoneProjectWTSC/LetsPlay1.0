package wtsc.letsplay10;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ricky Stambach on 4/11/2017.
 */

public class SportsFilterActivity extends AppCompatActivity implements
            OnSportsDataLoaded,
        AdapterView.OnItemSelectedListener {

    private Spinner spSportTypes;
    private List<Sport> sportTypes;
    private List<String> sportTypesNames = new ArrayList<String>();
    private Sport selectedSport;
    private Button sportsButton;
    private Button cancelBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sports_type_filter);

        sportsButton = (Button)findViewById(R.id.sportsFilterBTN);
        sportsButton.setOnClickListener(onSportsButtonClicked);
        cancelBTN = (Button)findViewById(R.id.cancelSportsFilterBTN);
        cancelBTN.setOnClickListener(onCancelButtonClicked);

        spSportTypes = (Spinner) findViewById(R.id.sports_type_Filter_spinner);
        dbGetSportsList getSportsList = new dbGetSportsList(SportsFilterActivity.this);
        getSportsList.execute();

    }

    View.OnClickListener onCancelButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            setResult(RESULT_CANCELED,intent);
            finish();
        }
    };

    View.OnClickListener onSportsButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            selectedSport = sportTypes.get(spSportTypes.getSelectedItemPosition());
            Intent intent = new Intent();
            intent.putExtra("SELECTED_SPORT",selectedSport);
            setResult(RESULT_OK,intent);
            finish();
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
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
