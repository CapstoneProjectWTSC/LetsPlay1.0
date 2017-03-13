package wtsc.letsplay10;

/**
 * May not be finished until I explicitly say so. I will commit this file so I can use it from work and from home.
 * Created by samal on 3/9/2017.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

public class StartPage extends AppCompatActivity implements OnClickListener, OnKeyListener{

    private EditText emailField;
    private EditText passwordField;
    private Button signIn;
    static dbGetCurrentUser getUser;
    private String emailFieldString;
    private String passwordFieldString;

    private User user;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_page);

        preferences = getSharedPreferences("userSettings", MODE_PRIVATE);

        String json = preferences.getString("User", "");


        if (!json.equals(""))
        {
            Gson gson = new Gson();
            user = gson.fromJson(json, User.class);
            startActivity(new Intent(getApplicationContext(), Account.class));
        }

        emailField = (EditText) findViewById(R.id.emailField);
        passwordField = (EditText) findViewById(R.id.passwordField);

        emailField.setOnKeyListener(this);
        passwordField.setOnKeyListener(this);

        signIn = (Button) findViewById(R.id.signIn);

        signIn.setOnClickListener(this);
    }


    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {
        if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
                keyCode == EditorInfo.IME_ACTION_DONE ||
                event.getAction() == KeyEvent.ACTION_DOWN &&
                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
        {
            if (!event.isShiftPressed()) {
                switch (view.getId()) {
                    case R.id.emailSubmission:
                        passwordField.requestFocus();
                        break;
                    case R.id. passwordSubmission:
                        signIn.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(
                                INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        break;
                }
                return true;
            }
        }
        return false; // pass on to other listeners.
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signIn:
                passwordField.setError(null);
                emailFieldString = emailField.getText().toString();
                passwordFieldString= passwordField.getText().toString();

                user = getUser.doInBackground("VERIFY", emailFieldString);

                if (user == null)
                {
                    CharSequence message = "Invalid email/password credentials";
                    Snackbar invalidLogin = Snackbar.make(findViewById(R.id.signIn), message, Snackbar.LENGTH_SHORT);
                    invalidLogin.show();
                    break;
                }


                SharedPreferences.Editor prefsEditor = preferences.edit();
                Gson gson = new Gson();
                String json = gson.toJson(user);
                prefsEditor.putString("User", json);
                prefsEditor.commit();

                startActivity(new Intent(getApplicationContext(), Account.class));

                break;
        }
    }
}

