package wtsc.letsplay10;
// @author Alexander Samuel

//import net.sourceforge.jtds.jdbc.DateTime;


import java.util.Date;

public class Schedule
{
	private int scheduleID, facilityID, sportsTypeID;
	private Date scheduleDateTime;

	public Schedule (Schedule schedule){
		setScheduleID(schedule.getScheduleID());
		setFacilityID(schedule.getFacilityID());
		setSportID(schedule.getSportID());
		setScheduleDateTime(schedule.getScheduleDateTime());
	}

	public Schedule( int facilityID, int sportsTypeID, Date scheduleDateTime)
	{
		setFacilityID(facilityID);
		setSportID(sportsTypeID);
		setScheduleDateTime(scheduleDateTime);
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
	
	public Date getScheduleDateTime()
	{
		return this.scheduleDateTime;
	}
	
	public void setScheduleDateTime(Date scheduleDateTime){this.scheduleDateTime = scheduleDateTime;}

}
