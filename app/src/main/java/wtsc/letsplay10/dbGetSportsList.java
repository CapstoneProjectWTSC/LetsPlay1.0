package wtsc.letsplay10;

import android.os.AsyncTask;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by why76_000 on 2/18/2017.
 */

public class dbGetSportsList extends AsyncTask<String,String,List<Sport>> {

    private dbConnectionClass connectionClass;
    private OnSportsDataLoaded dataLoaded;
    private List<Sport> sportsList;

    public dbGetSportsList(OnSportsDataLoaded activityContext){this.dataLoaded = activityContext;}

    @Override
    protected void onPreExecute() {
        connectionClass = new dbConnectionClass();
    }

    @Override
    protected List<Sport> doInBackground(String... params) {

        String z = "";
        Boolean isSuccess = false;
        List<Sport> sp = new ArrayList<Sport>();

        try {
            Connection con = connectionClass.CONN();
            if (con == null) {
                z = "Error in connection with SQL server ";
            } else {
                String query;
                if(params.length > 0 )
                {
                    query = "select * from [SportsType] WHERE ID = '"+ params[0]+"' ORDER BY [Sports_Name]";
                }
                else
                {
                    query = "select * from [SportsType] ORDER BY [Sports_Name]";
                }
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);

                while (rs.next()){
                    int id =  rs.getInt("SportsType_ID");
                    String name = rs.getString("Sports_Name");
                    sp.add(new Sport(id,name));
                }
                sportsList = new ArrayList<Sport>(sp);
            }
        }
        catch (Exception ex)
        {
            isSuccess = false;
            z = "Exceptions";
        }

        return sp;
    }

    @Override
    protected void onPostExecute(List<Sport> sportsList){
        dataLoaded.onSportsDataLoaded(sportsList);
    }
}
