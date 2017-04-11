package wtsc.letsplay10;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ricky Stambach on 2/21/2017.
 */

public class dbGetTDSTSchedules extends AsyncTask<UserBounds,String,List<MarkerOptions>> {

    private dbConnectionClass connectionClass;
    private OnScheduleDataLoaded dataLoaded;
    private List<Schedule> schedulesList;

    public dbGetTDSTSchedules(OnScheduleDataLoaded activityContext){this.dataLoaded = activityContext;}

    @Override
    protected void onPreExecute() {
        connectionClass = new dbConnectionClass();
    }

    @Override
    protected void onPostExecute(List<MarkerOptions> list){
        dataLoaded.onScheduleDataLoaded(list);
    }

    @Override
    protected List<MarkerOptions> doInBackground(UserBounds... params) {

        String z = "";
        Boolean isSuccess = false;
        List<MarkerOptions> markersOList = new ArrayList<MarkerOptions>();

        try {
            Connection con = connectionClass.CONN();
            if (con == null) {
                z = "Error in connection with SQL server ";
            } else {
                String query;
                if (params.length == 1) {
                    LatLng sw = params[0].getBounds().southwest;
                    LatLng ne = params[0].getBounds().northeast;

                    query = "select f.[facility_ID], f.[Name] AS fName, f.Lat, f.Lng, f.[Address1],f.[Address2]" +
                            ",f.[City],f.[State],f.[Zip],f.[Lat],f.[Lng],f.[Notes]" +
                            ",s.[schedule_ID]" +
                            " from [facility] f join [schedule] s " + "" +
                            "on f.[facility_ID] = s.[Facility_ID]" +
                            " WHERE " +
                            "[Lat] > " + sw.latitude + " AND [Lat] < " + ne.latitude + " AND " +
                            "[Lng] > " + sw.longitude + " AND [Lng] < " + ne.longitude +
                            " ORDER BY f.[Name]";
                    //TODO change query for date_time_sports_type
                } else {
                    //query = "select * from [Schedule]";
                    return null;
                }
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                MarkerOptions mo;

                while (rs.next()) {
                    String n = rs.getString("fName") + rs.getString("Sports_Name");
                    LatLng LL = new LatLng(rs.getDouble("Lat"), rs.getDouble("Lng"));
                    Blob bl = rs.getBlob("Sports_Icon");
                    int i = (int) bl.length();
                    byte[] blAsBytes = bl.getBytes(1, i);
                    Bitmap bm = BitmapFactory.decodeByteArray(blAsBytes, 0, blAsBytes.length);
                    mo = new MarkerOptions();
                    mo.title(n);
                    mo.position(LL);
                    if (bl != null) {
                        mo.icon(BitmapDescriptorFactory.fromBitmap(bm));
                    }
                    markersOList.add(mo);
                }

            }
        } catch (Exception ex) {
            isSuccess = false;
            z = "Exceptions";
        }

        return markersOList;
    }

}
