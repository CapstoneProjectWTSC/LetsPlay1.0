package wtsc.letsplay10;

import android.os.AsyncTask;
import android.util.Log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Ricky Stambach on 3/8/2017.
 */

public class dbAddNewUser extends AsyncTask<String,String,User> {

    private dbConnectionClass connectionClass;
    private OnNewUserAdded dataLoaded;
    private User newUser;
    private String queryParam;

    public dbAddNewUser(OnNewUserAdded activityContext){this.dataLoaded = activityContext;}

    @Override
    protected void onPreExecute() {
        connectionClass = new dbConnectionClass();
    }

    @Override
    protected User doInBackground(String... params) {

        newUser = null;
        try {
            Connection con = connectionClass.CONN();
            if (con != null){
                String query;

                if (params.length == 5) {
                    query = "INSERT INTO [User] ([First_Name],[Last_Name],[GameName],[Password],[Email])" +
                            "  VALUES ('" + params[0] + "','" + params[1] + "','" + params[2] + "','" + params[3] + "','" + params[4] + "')";
                    newUser = new User();
                    newUser.setFirstName(params[0]);
                    newUser.setLastName(params[1]);
                    newUser.setGameName(params[2]);
                    newUser.setPassword(params[3]);
                    newUser.setEmail(params[4]);

                    Statement stmt = con.createStatement();
                    ResultSet rs;

                    int c = stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
                    if (c > 0) {
                        rs = stmt.getGeneratedKeys();
                        if (rs.next()) {
                            newUser.setID(rs.getInt("ID"));
                        }
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        catch (Exception ex)
        {
            Log.e("ERRO", ex.getMessage());
        }

        return newUser;

    }

    @Override
    protected void onPostExecute(User newUser){
        dataLoaded.onDBNewUserAdded(newUser);
    }
}
