package wtsc.letsplay10;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

public class Introduction extends AppCompatActivity
        implements OnClickListener,
                    OnKeyListener,
                    OndbFindGameName,
                    OndbFindEmail,
                    OnNewUserAdded{

    private EditText emailSubmission;
    private EditText usernameSubmission;
    private EditText passwordSubmission;
    private EditText passwordConfirmation;
    private Button confirm;

    private String emailSubmissionString;
    private String usernameSubmissionString;
    private String passwordSubmissionString;
    private String passwordConfirmationString;


    private dbFindGameName db_findGameName;
    private dbFindEmail db_findEmail;
    private dbAddNewUser db_addNewUser;
    private User currentUser = new User();
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_page);

        preferences = getSharedPreferences("userSettings", MODE_PRIVATE);
        String json = preferences.getString("User", "");

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

                db_findGameName = new dbFindGameName(Introduction.this);    //check if currentUser is in database
                db_findGameName.execute(usernameSubmissionString);      // returns in onDBFindGameName

                break;
        }
    }

    @Override
    public void onDBFindGameName(boolean isInDatabase) {
        if(isInDatabase){
            // Error GameName already in database.
        }
        else
        {
            db_findEmail = new dbFindEmail(Introduction.this);    //check if email is in database
            db_findEmail.execute(emailSubmissionString);      // returns in onDBFindEmail
        }
    }

    @Override
    public void onDBFindEmail(boolean isInDatabase) {
        if(isInDatabase){
            // Error Email already in database
        }
        else
        {
            // Email and GameName are availble to use add new currentUser to database
            currentUser.setEmail(emailSubmissionString);
            currentUser.setGameName(usernameSubmissionString);
            currentUser.setPassword(passwordSubmissionString);
            currentUser.setFirstName("");
            currentUser.setLastName("");

            db_addNewUser = new dbAddNewUser(Introduction.this);
            db_addNewUser.execute(currentUser.getFirstName(), currentUser.getLastName(),
                    currentUser.getGameName(), currentUser.getPassword(), currentUser.getEmail());
        }
    }

    @Override
    public void onDBNewUserAdded(User NewUser) {
            int id = NewUser.getID();
            SharedPreferences.Editor prefsEditor = preferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson(currentUser);
            prefsEditor.putString("User", json);
            prefsEditor.commit();
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
    }
}
