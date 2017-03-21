package wtsc.letsplay10;


import android.os.AsyncTask;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by Ricky Stambach on 3/7/2017.
 */

public class dbFindEmail extends AsyncTask<String,String,Boolean> {
    private dbConnectionClass connectionClass;
    private OndbFindEmail dataLoaded;
    private boolean isInDatabase;

    public dbFindEmail(OndbFindEmail activityContext){this.dataLoaded = activityContext;}

    @Override
    protected void onPreExecute() {
        connectionClass = new dbConnectionClass();
    }

    @Override
    protected Boolean doInBackground(String... params) {
        isInDatabase = false;
        try {
            Connection con = connectionClass.CONN();
            if (con == null) {
            } else {
                String query = "select * from [User] WHERE [GameName] = '"+ params[0] + "'";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                if(rs.next()){isInDatabase = true;}
            }
        } catch (Exception ex) {
        }
        return isInDatabase;
    }

    @Override
    protected void onPostExecute(Boolean isInDB){
        dataLoaded.onDBFindEmail(isInDB);
    }
}
