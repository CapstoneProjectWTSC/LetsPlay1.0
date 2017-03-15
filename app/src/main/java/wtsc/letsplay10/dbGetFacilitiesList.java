package wtsc.letsplay10;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ricky Stambach on 3/3/2017.
 */

public class dbGetFacilitiesList extends AsyncTask<LatLngBounds,String,List<Facility>> {
    private dbConnectionClass connectionClass;
    private OnFacilitiesDataLoaded dataLoaded;
    private List<Facility> facilitiesList;

    public dbGetFacilitiesList(OnFacilitiesDataLoaded activityContext){this.dataLoaded = activityContext;}

    @Override
    protected void onPreExecute() {
        connectionClass = new dbConnectionClass();
    }

    @Override
    protected List<Facility> doInBackground(LatLngBounds... params) {

        String z = "";
        Boolean isSuccess = false;
        List<Facility> fl = new ArrayList<Facility>();

        try {
            Connection con = connectionClass.CONN();
            if (con == null) {
                z = "Error in connection with SQL server ";
            } else {
                String query;
                if(params.length > 0 )
                {
                    LatLng sw = params[0].southwest;
                    LatLng ne = params[0].northeast;

                    query = "select f.[facility_ID],f.[Name],f.[Address1],f.[Address2]" +
                            ",f.[City],f.[State],f.[Zip],f.[Lat],f.[Lng],f.[Notes]"+
                            ",s.[schedule_ID]"+
                            " from [facility] f join [schedule] s "+"" +
                            "on f.[facility_ID] = s.[Facility_ID]"+
                            " WHERE "+
                            "[Lat] > " + sw.latitude + " AND [Lat] < " + ne.latitude + " AND " +
                            "[Lng] > " + sw.longitude + " AND [Lng] < " + ne.longitude;
                }
                else
                {
                    query = "select * from [Facility]";
                }
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);

                while (rs.next()){
                    int id =  rs.getInt("Facility_ID");
                    String name = rs.getString("Name");
                    String a1 = rs.getString("Address1");
                    String a2 = rs.getString("Address2");
                    String city = rs.getString("City");
                    String state = rs.getString("State");
                    String zip = rs.getString("Zip");
                    Double lat = rs.getDouble("Lat");
                    Double lng = rs.getDouble("Lng");
                    String notes = rs.getString("Notes");
                    fl.add(new Facility(id,name,a1,a2,city,state,zip,lat,lng,notes ));
                }
                facilitiesList = new ArrayList<Facility>(fl);
            }
        }
        catch (Exception ex)
        {
            isSuccess = false;
            z = "Exceptions";
        }

        return fl;
    }

    @Override
    protected void onPostExecute(List<Facility> facilitiesList){
        dataLoaded.onFacilitiesDataLoaded(facilitiesList);
    }
}
