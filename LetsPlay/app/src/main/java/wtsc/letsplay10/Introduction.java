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

public class Introduction extends AppCompatActivity implements OnClickListener, OnKeyListener{

    private EditText emailSubmission;
    private EditText usernameSubmission;
    private EditText passwordSubmission;
    private EditText passwordConfirmation;
    private Button confirm;

    private String emailSubmissionString;
    private String usernameSubmissionString;
    private String passwordSubmissionString;
    private String passwordConfirmationString;

    private User user = new User();

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_page);

        preferences = getSharedPreferences("userSettings", MODE_PRIVATE);

        String json = preferences.getString("User", "");


        if (json != "")
        {
            Gson gson = new Gson();
            user = gson.fromJson(json, User.class);
            startActivity(new Intent(getApplicationContext(), Settings.class));
        }

        emailSubmission = (EditText) findViewById(R.id.emailSubmission);
        usernameSubmission = (EditText) findViewById(R.id.usernameSubmission);
        passwordSubmission = (EditText) findViewById(R.id.passwordSubmission);
        passwordConfirmation = (EditText) findViewById(R.id.passwordConfirmation);

        emailSubmission.setOnKeyListener(this);
        usernameSubmission.setOnKeyListener(this);
        passwordSubmission.setOnKeyListener(this);
        passwordConfirmation.setOnKeyListener(this);

        confirm = (Button) findViewById(R.id.confirm);

        confirm.setOnClickListener(this);
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
                        usernameSubmission.requestFocus();
                        break;
                    case R.id. usernameSubmission:
                        passwordSubmission.requestFocus();
                        break;
                    case R.id. passwordSubmission:
                        passwordConfirmation.requestFocus();
                        break;
                    case R.id. passwordConfirmation:
                        confirm.requestFocus();
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
            case R.id.confirm:
                passwordSubmission.setError(null);
                emailSubmissionString = emailSubmission.getText().toString();
                usernameSubmissionString= usernameSubmission.getText().toString();
                passwordSubmissionString= passwordSubmission.getText().toString();
                passwordConfirmationString= passwordConfirmation.getText().toString();

                /*if (!EmailValidator.getInstance().isValid(emailSubmissionString)){
                    emailSubmission.setError("Please enter a valid e-mail.");
                    break;
                }

                PasswordValidation validate = new PasswordValidation();

                String validationResult = validate.validateNewPass(passwordSubmissionString, passwordConfirmationString);

                if(!validationResult.equals("Success!")){
                    passwordSubmission.setError(validationResult);
                    break;
                }*/

                //if(usernname is in database){
                // usernameSubmission.setError("That username is already in use.");
                //break;
                //}

                //if(email is in database){
                // emailSubmission.setError("That e-mail is already in use.");
                //break;
                //}

                //send username email, password combo to database

                //setContentView(R.layout.'name of the map page');
                user.setEmail(emailSubmissionString);
                user.setGamename(usernameSubmissionString);
                user.setPassword(passwordSubmissionString);

                SharedPreferences.Editor prefsEditor = preferences.edit();
                Gson gson = new Gson();
                String json = gson.toJson(user);
                prefsEditor.putString("User", json);
                prefsEditor.commit();

               startActivity(new Intent(getApplicationContext(), Settings.class));

                break;
        }
    }
}
