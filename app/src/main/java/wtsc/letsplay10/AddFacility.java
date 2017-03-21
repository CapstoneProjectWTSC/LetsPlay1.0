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

import com.google.gson.Gson;
/**
 * Created by a1995 on 3/20/2017.
 */

public class AddFacility {
Facility newFacility;
    public void onClick (View v){
        switch (v.getId()) {
            case R.id.byAddress:
                //<code></code>
                break;
            case R.id.myLocation:
                //<code></code>
                break;
            case R.id.placePin:
                //<code></code>
                break;

        }
    }
}
