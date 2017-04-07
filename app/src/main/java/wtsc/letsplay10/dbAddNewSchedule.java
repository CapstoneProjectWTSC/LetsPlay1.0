package wtsc.letsplay10;

import android.os.AsyncTask;
import android.util.Log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Ricky Stambach on 4/2/2017.
 */

public class dbAddNewSchedule extends AsyncTask<ScheduleUserSchedule,String,ScheduleUserSchedule> {
    private dbConnectionClass connectionClass;
    private OnNewScheduleUserScheduleAdded dataLoaded;
    private Schedule newSchedule;
    private UserSchedule newUserSchedule;
    private ScheduleUserSchedule newScheduleUserSchedule;

    public dbAddNewSchedule(OnNewScheduleUserScheduleAdded activityContext){this.dataLoaded = activityContext;}

    @Override
    protected void onPreExecute() {
        connectionClass = new dbConnectionClass();
    }

    @Override
    protected ScheduleUserSchedule doInBackground(ScheduleUserSchedule... params) {
        try {
            Connection con = connectionClass.CONN();
            if (con != null){
                String query;

                if (params[0] != null) {
                    newSchedule = new Schedule(params[0].getSSchedule());
                    newUserSchedule = new UserSchedule(params[0].getSUserSchedule());
                    newScheduleUserSchedule = new ScheduleUserSchedule();
// --------------------------- inserts new schedule record into the schedule datatable -------------------------------------------------
                    query = "INSERT INTO [Schedule] ([SportsType_ID],[Facility_ID],[Schedule_Date],[Schedule_Time])" +
                            "  VALUES ('" + String.valueOf(newSchedule.getSportID()) + "','" + String.valueOf(newSchedule.getFacilityID()) +
                            "','" + String.valueOf(newSchedule.getScheduleDateTime()) + "')";

                    Statement stmt = con.createStatement();
                    ResultSet rs;

                    int c = stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
                    if (c > 0) {
                        rs = stmt.getGeneratedKeys();
                        if (rs.next()) {
                            newSchedule.setScheduleID(rs.getInt("Schedule_ID"));
                        }
                    }
// --------------------------- inserts new userSchedule record into the userSchedule datatable -------------------------------------------------
                    query = "INSERT INTO [UserSchedule] ([User_ID],[Schedule_ID])" +
                            "  VALUES ('" + String.valueOf(newUserSchedule.getUserID()) + "','" + String.valueOf(newSchedule.getScheduleID()) + "')";

                    stmt = con.createStatement();
                    c = stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
                    if (c > 0) {
                        rs = stmt.getGeneratedKeys();
                        if (rs.next()) {
                            newUserSchedule.setUserScheduleID(rs.getInt("UserSchedule_ID"));
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
        return newScheduleUserSchedule;
    }

    @Override
    protected void onPostExecute(ScheduleUserSchedule scheduleUserSchedule){
        dataLoaded.onNewScheduleUserScheduleAdded(newScheduleUserSchedule);
    }
}
