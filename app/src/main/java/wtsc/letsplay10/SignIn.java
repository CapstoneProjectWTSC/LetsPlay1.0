package wtsc.letsplay10;

import android.app.Activity;
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

import static wtsc.letsplay10.R.id.createAccountBTN;
import static wtsc.letsplay10.R.id.signInBTN;


/**
 * Class to allow the user to sign into his/her account.
 */
public class SignIn extends AppCompatActivity implements
        OnClickListener,
        OnKeyListener,
        OndbVerifyPassword{

    private EditText emailField;
    private EditText passwordField;
    private Button signIn_btn;
    private Button createNewAccount_btn;
    private dbValidateUser vUser;
    private String emailFieldString;
    private String passwordFieldString;
    private User currentUser;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);

        preferences = getSharedPreferences("userSettings", MODE_PRIVATE);
        String json = preferences.getString("User", "");
        emailField = (EditText) findViewById(R.id.emailField);
        passwordField = (EditText) findViewById(R.id.passwordField);
        emailField.setOnKeyListener(this);
        passwordField.setOnKeyListener(this);
        signIn_btn = (Button) findViewById(R.id.signInBTN );
        signIn_btn.setOnClickListener(this);
        createNewAccount_btn = (Button)findViewById(createAccountBTN);
        createNewAccount_btn.setOnClickListener(this);
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
                        signIn_btn.requestFocus();
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
            case signInBTN:
                passwordField.setError(null);
                emailFieldString = emailField.getText().toString();
                passwordFieldString= passwordField.getText().toString();
                vUser = new dbValidateUser(SignIn.this);
                vUser.execute(emailFieldString,passwordFieldString);
                break;
            case createAccountBTN:
                startActivityForResult(new Intent(getApplicationContext(), Introduction.class),1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK ){
            finish();
        }
    }

    @Override
    public void onDBPasswordVerified(User user) {
        if(vUser.getIsValidUser()){
            currentUser = user;
            SharedPreferences.Editor prefsEditor = preferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson(currentUser);
            prefsEditor.putString("User", json);
            prefsEditor.commit();
            finish();
        }
        else{
            String message;
            if(vUser.getIsValidEmail()){
                message = "Invalid password error";
            }
            else {message = "Invalid email error";}
            Snackbar invalidLogin = Snackbar.make(findViewById(R.id.snackbarCoordinatorLayout), message, Snackbar.LENGTH_LONG);
            invalidLogin.show();
        }
    }



}

