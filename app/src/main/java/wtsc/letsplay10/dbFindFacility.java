package wtsc.letsplay10;


import android.os.AsyncTask;
import android.util.Log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Ricky Stambach on 4/23/2017.
 */

public class dbFindFacility extends AsyncTask<String,String,Facility> {


    private dbConnectionClass connectionClass;
    private OnFindFacility dataLoaded;
    private Facility newFacility;

    public dbFindFacility(OnFindFacility activityContext){this.dataLoaded = activityContext;}

    @Override
    protected void onPreExecute() {
        connectionClass = new dbConnectionClass();
    }

    @Override
    protected Facility doInBackground(String... params) {

        newFacility = null;
        try {
            Connection con = connectionClass.CONN();
            if (con != null){
                String query;

                if (params.length == 1) {
                    query = "SELECT [Facility_ID],[Name],[Address1],[Address2]," +
                            "[City],[State],[Zip],[Lat],[Lng],[Notes]" +
                            " FROM [DB_112602_letsplay].[dbo].[Facility]" +
                            " WHERE [Name] = ' " + params[0] + "'";

                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);

                    while (rs.next()) {
                        int id = rs.getInt("Facility_ID");
                        String name = rs.getString("Name");
                        String a1 = rs.getString("Address1");
                        String a2 = rs.getString("Address2");
                        String city = rs.getString("City");
                        String state = rs.getString("State");
                        String zip = rs.getString("Zip");
                        Double lat = rs.getDouble("Lat");
                        Double lng = rs.getDouble("Lng");
                        String notes = rs.getString("Notes");
                    }
                    int c = stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        catch (Exception ex)
        {
            Log.e("ERROR", ex.getMessage());
        }
        return newFacility;
    }

    //@Override
    protected void onPostExecute(Facility newFacility){
        dataLoaded.onDBFindFacility(newFacility);
    }

}
