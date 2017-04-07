package wtsc.letsplay10;

/**
 * Created by Ricky Stambach on 4/2/2017.
 */

public class ScheduleUserSchedule {
    Schedule sSchedule;
    UserSchedule sUserSchedule;

    public ScheduleUserSchedule (){
    }
    public ScheduleUserSchedule (Schedule schedule, UserSchedule userSchedule){
        sSchedule = schedule;
        sUserSchedule = userSchedule;
    }

    public Schedule getSSchedule(){return sSchedule;}
    public UserSchedule getSUserSchedule(){return sUserSchedule;}
    public void setSSchedule(Schedule schedule){sSchedule = schedule;}
    public void setSUserSchedule(UserSchedule userSchedule){sUserSchedule = userSchedule;}

}
