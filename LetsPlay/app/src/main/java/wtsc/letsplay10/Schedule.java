import java.sql.Date;
import java.sql.Time;

public class Schedule
{
	private int scheduleID, facilityID, sportID;
	private Date scheduleDate;
	private Time scheduleTime;
	
	public Schedule(int scheduleID, int facilityID, int sportID, Date scheduleDate, Time scheduleTime)
	{
		setScheduleID(scheduleID);
		setFacilityID(facilityID);
		setSportID(sportID);
		setScheduleDate(scheduleDate);
		setScheduleTime(scheduleTime);
	}
	
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
		return this.sportID;
	}
	
	public void setSportID(int sportID)
	{
		this.sportID = sportID;
	}
	
	public Date getScheduleDate()
	{
		return this.scheduleDate;
	}
	
	public void setScheduleDate(Date scheduleDate)
	{
		this.scheduleDate = scheduleDate;
	}
	
	public Time getScheduleTime()
	{
		return this.scheduleTime;
	}
	
	public void setScheduleTime(Time scheduleTime)
	{
		this.scheduleTime = scheduleTime;
	}
}
