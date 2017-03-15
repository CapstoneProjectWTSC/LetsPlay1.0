package wtsc.letsplay10;

import android.os.AsyncTask;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by Ricky Stambach on 3/14/2017.
 */

public class dbValidateUser extends AsyncTask<String,String,User> {

    private dbConnectionClass connectionClass;
    private OndbVerifyPassword dataLoaded;
    private boolean isValidEmail, isValidUser;
    private  User user;

    public dbValidateUser(OndbVerifyPassword activityContext){this.dataLoaded = activityContext;}

    @Override
    protected void onPreExecute() {
        connectionClass = new dbConnectionClass();
    }

    @Override
    protected User doInBackground(String... params) {

        isValidEmail = false;
        isValidUser = false;
        user = new User();

        try {
            Connection con = connectionClass.CONN();
            if (con == null) {
            } else {
                String query = "select * from [User] WHERE [Email] = '"+ (String) params[0] + "'";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                if(rs.next()){
                    isValidEmail = true;
                    user.setID(rs.getInt("User_ID"));
                    user.setFirstName(rs.getString("First_Name"));
                    user.setLastName(rs.getString("Last_Name"));
                    user.setGameName(rs.getString("GameName"));
                    user.setPassword(rs.getString("Password"));
                    user.setEmail(rs.getString("Email"));
                    if(user.getPassword().equals(params[1])){
                        isValidUser = true;
                    }
                    else{user=new User();}
                }
            }
        } catch (Exception ex) {
        }
       return user;
    }

    public boolean getIsValidEmail(){return isValidEmail;}
    public boolean getIsValidUser(){return isValidUser;}

    @Override
    protected void onPostExecute(User user){
        dataLoaded.onDBPasswordVerified(user);
    }
}
