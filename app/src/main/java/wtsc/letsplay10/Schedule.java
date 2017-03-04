package wtsc.letsplay10;
// @author Alexander Samuel

import net.sourceforge.jtds.jdbc.DateTime;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

public class Schedule
{
	private int scheduleID, facilityID, sportsTypeID;
	private Date scheduleDate;
    private Time scheduleTime;

	public Schedule(int scheduleID, int facilityID, int sportsTypeID, Date scheduleDate, Time scheduleTime)
	{
		setScheduleID(scheduleID);
		setFacilityID(facilityID);
		setSportID(sportsTypeID);
		setScheduleDate(scheduleDate);
        setScheduleTime(scheduleTime);
	}

	public Schedule(){}
	
	public int getScheduleID()
	{
		return this.scheduleID;
	}
	
	public void setScheduleID(int scheduleID)
	{
		this.scheduleID = scheduleID;
	}
	
	public int getFacilityID()
	{
		return this.facilityID;
	}
	
	public void setFacilityID(int facilityID)
	{
		this.facilityID = facilityID;
	}
	
	public int getSportID()
	{
		return this.sportsTypeID;
	}
	
	public void setSportID(int sportID)
	{
		this.sportsTypeID = sportID;
	}
	
	public Date getScheduleDate()
	{
		return this.scheduleDate;
	}
	
	public void setScheduleDate(Date scheduleDate){this.scheduleDate = scheduleDate;}

    public Time getScheduleTime() { return this.scheduleTime;}

    public void setScheduleTime(Time scheduleTime){this.scheduleTime = scheduleTime;}
	
}
