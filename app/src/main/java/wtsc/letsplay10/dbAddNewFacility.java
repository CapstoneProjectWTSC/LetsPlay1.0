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

public class dbAddNewFacility extends AsyncTask<String,String,Facility> {

    private dbConnectionClass connectionClass;
    private OnNewFacilityAdded dataLoaded;
    private Facility newFacility;
    private String queryParam;

    public dbAddNewFacility(OnNewFacilityAdded activityContext){this.dataLoaded = activityContext;}

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

                if (params.length == 6) {
                    query = "INSERT INTO [Facility] ([First_Name],[Last_Name],[GameName],[Password],[Email])" +
                            "  VALUES ('" + params[0] + "','" + params[1] + "','" + params[2] + "','" + params[3] + "','" + params[4] + "','" + params[5] + "','" + params[6] +
                            "','" + params[7] + "')";
                    newFacility = new Facility();
                    newFacility.setName(params[0]);
                    newFacility.setAddress1(params[1]);
                    newFacility.setCity(params[2]);
                    newFacility.setState(params[3]);
                    newFacility.setZip(params[4]);
                    newFacility.setLatitude(Long.parseLong(params[5]));
                    newFacility.setLongitude(Long.parseLong(params[6]));
                    newFacility.setNotes(params[7]);
                    query = "INSERT INTO [Facility] ([Name],[Address1],[Address2],[City],[State]" +
                            ",[Zip],[Lat],[Lng],[Notes])" +
                            "  VALUES ('" + params[0] + "','" + params[1] + "','" + params[2] + "','" +
                            params[3] + "','" + params[4] + "','" + params[5] + "','" + params[6] +
                            "','" + params[7] + "','" + params[8] + "')";

                    newFacility = new Facility();
                    newFacility.setName(params[0]);
                    newFacility.setAddress1(params[1]);
                    newFacility.setAddress1(params[2]);
                    newFacility.setCity(params[3]);
                    newFacility.setState(params[4]);
                    newFacility.setZip(params[5]);
                    newFacility.setLatitude(Long.parseLong(params[6]));
                    newFacility.setLongitude(Long.parseLong(params[7]));
                    newFacility.setNotes(params[8]);

                    Statement stmt = con.createStatement();
                    ResultSet rs;

                    int c = stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
                    if (c > 0) {
                        rs = stmt.getGeneratedKeys();
                        if (rs.next()) {
                            newFacility.setFacilityID(rs.getInt("ID"));
                        }
                    }
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
    @Override
    protected void onPostExecute(Facility facility){
        dataLoaded.onNewFacilityAdded(newFacility);
    }
}
