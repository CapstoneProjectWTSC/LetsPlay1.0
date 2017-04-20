package wtsc.letsplay10;

import android.content.Context;
import android.content.res.Resources;
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
 * Created by Ricky Stambach on 4/12/2017.
 */

public class dbGetSportTypeScheduleMarkers extends AsyncTask<SportsBounds,String,List<MarkerOptions>> {
    private dbConnectionClass connectionClass;
    private OnScheduleDataLoaded dataLoaded;
    private List<Schedule> schedulesList;
    private Context mContext;

    public dbGetSportTypeScheduleMarkers(OnScheduleDataLoaded activityContext, Context context){
        this.dataLoaded = activityContext;
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        connectionClass = new dbConnectionClass();
    }

    @Override
    protected void onPostExecute(List<MarkerOptions> list){
        dataLoaded.onScheduleDataLoaded(list);
    }

    @Override
    protected List<MarkerOptions> doInBackground(SportsBounds... params) {

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

                    query = "select f.[facility_ID], f.[Name] AS fName, f.Lat, f.Lng, f.[Address1],f.[Address2]"+
                            ",f.[City],f.[State],f.[Zip],f.[Lat],f.[Lng],f.[Notes],s.[schedule_ID], "+
                            "t.[Sports_Name],t.[Sports_Icon] "+
                            "FROM [schedule] s "+
                            "JOIN facility as f on f.Facility_ID = s.Facility_ID "+
                            "JOIN SportsType as t on t.SportsType_ID = s.SportsType_ID "+
                            "JOIN UserSchedule as u on u.Schedule_ID = s.Schedule_ID "+
                            "WHERE "+
                            "[Lat] > " + sw.latitude + " AND [Lat] < " + ne.latitude + " AND " +
                            "[Lng] > " + sw.longitude + " AND [Lng] < " + ne.longitude + " AND " +
                            "t.SportsType_ID = " + params[0].getSport().getID() +
                            " ORDER BY f.[Name]";
                } else {
                    //query = "select * from [Schedule]";
                    return null;
                }
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                MarkerOptions mo;

                while (rs.next()) {
                    String n = rs.getString("fName") + " - " + rs.getString("Sports_Name");
                    LatLng LL = new LatLng(rs.getDouble("Lat"),rs.getDouble("Lng"));
                    Blob bl = rs.getBlob("Sports_Icon");
                    mo = new MarkerOptions();
                    mo.title(n);
                    mo.position(LL);

                    Resources resources = mContext.getResources();
                    final int resourcesID = resources.getIdentifier(rs.getString("Sports_Name").toLowerCase(),"drawable",
                            mContext.getPackageName());
                    if(resourcesID > 0){
                        Bitmap bitmap = BitmapFactory.decodeResource(resources,resourcesID);
                        mo.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
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
