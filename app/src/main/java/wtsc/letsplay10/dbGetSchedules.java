package wtsc.letsplay10;



import android.os.AsyncTask;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ricky Stambach on 2/21/2017.
 */

public class dbGetSchedules extends AsyncTask<String,String,List<Schedule>> {

    private dbConnectionClass connectionClass;
    private OnScheduleDataLoaded dataLoaded;
    private List<Schedule> schedulesList;

    public dbGetSchedules(OnScheduleDataLoaded activityContext){this.dataLoaded = activityContext;}

    @Override
    protected void onPreExecute() {
        connectionClass = new dbConnectionClass();
    }

    @Override
    protected void onPostExecute(List<Schedule> list){
        dataLoaded.onScheduleDataLoaded(list);
    }

    @Override
    protected List<Schedule> doInBackground(String... params) {

        String z = "";
        Boolean isSuccess = false;
        List<Schedule> sl = new ArrayList<Schedule>();

        try {
            Connection con = connectionClass.CONN();
            if (con == null) {
                z = "Error in connection with SQL server ";
            } else {
                String query;
                if (params.length > 0) {
                    query = "select * from [Schedule]"; //"select * from [Schedule] WHERE ID = '" + (String) params[0] + "'";
                } else {
                    //query = "select * from [Schedule]";
                    return null;
                }
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                Schedule sh;

                while (rs.next()) {
                    sh = new Schedule();
                    sh.setScheduleID(rs.getInt("Schedule_ID"));
                    sh.setSportID(rs.getInt("SportsType_ID"));
                    sh.setFacilityID(rs.getInt("Facility_ID"));
                    sh.setScheduleDateTime(rs.getDate("Schedule_DateTime"));
                    sl.add(sh);
                }

            }
        } catch (Exception ex) {
            isSuccess = false;
            z = "Exceptions";
        }

        return sl;
    }

}
