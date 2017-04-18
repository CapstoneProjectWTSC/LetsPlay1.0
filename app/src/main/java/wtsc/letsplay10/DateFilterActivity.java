package wtsc.letsplay10;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ricky Stambach on 4/11/2017.
 */

public class DateFilterActivity extends AppCompatActivity {

    private Button dateTimeSelectedBTN;
    private Button cancelBTN;
    private EditText bDateText,eDateText;
    private Date bDateTime, eDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.date_time_filter);

        dateTimeSelectedBTN = (Button)findViewById(R.id.dateTimeBTN);
        dateTimeSelectedBTN.setOnClickListener(onDateTimeButtonClicked);
        cancelBTN = (Button)findViewById(R.id.cancelBTN);
        cancelBTN.setOnClickListener(onCancelButtonClicked);
        bDateText = (EditText)findViewById(R.id.begin_dateText);
        eDateText = (EditText)findViewById(R.id.end_dateText);
    }

    View.OnClickListener onCancelButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            setResult(RESULT_CANCELED,intent);
            finish();
        }
    };

    View.OnClickListener onDateTimeButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy");
            String s1 = new String(bDateText.getText().toString());

            try {
                Date parsed = df.parse(s1);
                bDateTime = new Date(parsed.getTime() );

            } catch (ParseException e) {
                String message = "Invalided Start Date or Time";
                Snackbar invalidLogin = Snackbar.make(findViewById(R.id.snackbarCoordinatorLayout), message, Snackbar.LENGTH_LONG);
                invalidLogin.show();
            }

            s1 = new String(eDateText.getText().toString());

            try {
                Date parsed = df.parse(s1);
                eDateTime = new Date(parsed.getTime() );

            } catch (ParseException e) {
                String message = "Invalided Ending Date or Time";
                Snackbar invalidLogin = Snackbar.make(findViewById(R.id.snackbarCoordinatorLayout), message, Snackbar.LENGTH_LONG);
                invalidLogin.show();
            }

            Intent intent = new Intent();
            intent.putExtra("BEGINNING_DATE_TIME",bDateText.getText().toString());
            intent.putExtra("ENDING_DATE_TIME",eDateText.getText().toString());
            setResult(RESULT_OK,intent);
            finish();
        }
    };




}
