package wtsc.letsplay10;

public class UserSchedule
{
	int userScheduleID, userID, scheduleID;
	
	public UserSchedule(int userScheduleID, int userID, int scheduleID)
	{
		setUserScheduleID(userScheduleID);
		setUserID(userID);
		setScheduleID(scheduleID);
	}
	
	public int getUserScheduleID()
	{
		return this.userScheduleID;
	}
	
	public void setUserScheduleID(int userScheduleID)
	{
		this.userScheduleID = userScheduleID;
	}
	
	public int getUserID()
	{
		return this.userID;
	}
	
	public void setUserID(int userID)
	{
		this.userID = userID;
	}
	
	public int getScheduleID()
	{
		return scheduleID;
	}
	
	public void setScheduleID(int scheduleID)
	{
		this.scheduleID = scheduleID;
	}
}
