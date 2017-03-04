package wtsc.letsplay10;

import android.os.AsyncTask;
import android.util.Log;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * GetCurrentUser class - creates a connection with the database and returns the
 * current User object. Requires the email parameter to query the User table passed
 * in the execute command
 * Created by Ricky Stambach on 2/15/2017.
 */

public class GetCurrentUser extends AsyncTask<String,String,User> {

    private ConnectionClass connectionClass;
    private OnUserDataLoaded dataLoaded;
    private User newUser;

    public GetCurrentUser(OnUserDataLoaded activityContext){this.dataLoaded = activityContext;}

    public User GetCurrentUser(){return new User(newUser);}

    @Override
    protected void onPreExecute() {
        connectionClass = new ConnectionClass();
    }

    @Override
    protected User doInBackground(String... params) {

        newUser = null;
        try {
            Connection con = connectionClass.CONN();
            if (con == null) {
            } else {
                String query;
                if(params.length > 0 )
                {
                    query = "select * from [User] WHERE Email = '"+(String)params[0]+"'";
                }
                else
                {
                    return newUser;
                }
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);

                if(rs.next()){
                    newUser = new User();
                    newUser.setID(rs.getInt("ID"));
                    newUser.setFirstName(rs.getString("First_Name"));
                    newUser.setLastName(rs.getString("Last_Name"));
                    newUser.setGamename(rs.getString("GameName"));
                    newUser.setPassword(rs.getString("Password"));
                    newUser.setEmail(rs.getString("Email"));
                }
            }
        }
        catch (Exception ex)
        {
            Log.e("ERRO", ex.getMessage());
        }

        return newUser;
    }

    @Override
    protected void onPostExecute(User currentUser){
        dataLoaded.onCurrentUserDataLoaded(newUser);
    }
}
