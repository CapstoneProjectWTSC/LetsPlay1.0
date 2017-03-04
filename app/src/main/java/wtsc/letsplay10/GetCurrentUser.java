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
    private String queryParam;

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

                if(params.length > 1 ){
                    queryParam = params[0];
                    switch (queryParam) {

                        case "VERIFY":
                            query = "select * from [User] WHERE Email = '" + (String) params[1] + "'";
                            break;
                        case "LOAD":
                            query = "select * from [User] WHERE Email = '" + (String) params[1] + "'";
                            break;
                        case "ADD_NEW":
                            query = "INSERT INTO [User] ([First_Name],[Last_Name],[GameName],[Password],[Email])" +
                                    "  VALUES ('"+params[1] +"','"+params[2]+"','"+params[3]+"','"+params[4]+"','"+ params[5]+"')";
                            newUser = new User();
                            newUser.setFirstName(params[1]);
                            newUser.setLastName(params[2]);
                            newUser.setGameName(params[3]);
                            newUser.setPassword(params[4]);
                            newUser.setEmail(params[5]);
                            break;
                        default:
                            query = "select * from [User]";
                    }
                }
                else
                {
                        return newUser;
                }
                Statement stmt = con.createStatement();
                ResultSet rs;

                if (queryParam == "ADD_NEW"){
                    int c = stmt.executeUpdate(query,Statement.RETURN_GENERATED_KEYS);
                    if(c>0){
                        rs = stmt.getGeneratedKeys();
                        if(rs.next()){
                            newUser.setID(rs.getInt("ID"));
                        }
                    }


                }
                else {

                    rs = stmt.executeQuery(query);

                    if (rs.next()) {
                        newUser = new User();
                        newUser.setID(rs.getInt("ID"));
                        newUser.setFirstName(rs.getString("First_Name"));
                        newUser.setLastName(rs.getString("Last_Name"));
                        newUser.setGameName(rs.getString("GameName"));
                        newUser.setPassword(rs.getString("Password"));
                        newUser.setEmail(rs.getString("Email"));
                    }
                }
            }
        }
        catch (Exception ex)
        {
            Log.e("ERRO", ex.getMessage());
        }

        return newUser;
    }

    private void doQuery(){


    }

    @Override
    protected void onPostExecute(User currentUser){

        if(queryParam!=null){
            switch (queryParam){
                case "LOAD":
                    dataLoaded.onCurrentUserDataLoaded(newUser);
                    break;
                case "VERIFY":
                    dataLoaded.onUserVerify(newUser);
                    break;
                case "ADD_NEW":
                    dataLoaded.onNewUserAdded(newUser);
                    break;
            }
        }
    }


}
