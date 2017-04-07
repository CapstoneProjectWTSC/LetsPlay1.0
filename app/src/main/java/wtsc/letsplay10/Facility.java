package wtsc.letsplay10;

// @author Alexander Samuel

public class Facility
{
   String name, address1, address2, city, state,zip, notes;
   int facilityID;
   double latitude, longitude;

  // public Facility(int facilityID,  )
   public Facility(){}
   public Facility(int facilityID, String name, String address1, String address2, String city,
                   String state, String zip, double latitude, double longitude, String notes)
   {
      setFacilityID(facilityID);
      setName(name);
      setAddress1(address1);
      setAddress2(address2);
      setCity(city);
      setState(state);
      setNotes(notes);
      setZip(zip);
      setLatitude(latitude);
      setLongitude(longitude);
   }

   public String getName()
   {
      return this.name;
   }

   public void setName(String name)
   {
      this.name = name;
   }
   
   public String getAddress1()
   {
      return address1;
   }

   public String getAddress2()
   {
       return address2;
   }

   
   public void setAddress1(String address1)
   {
      this.address1 = address1;
   }
   public void setAddress2(String address2)
   {
       this.address2 = address2;
   }

   public String getCity()
   {
      return this.city;
   }
   
   public void setCity(String city)
   {
      this.city = city;
   }
   
   public String getState()
   {
      return this.state;
   }

   public void setState(String state)
   {
      this.state = state;
   }
   
   public String getZip()
   {
      return this.zip;
   }
   
   public void setZip(String zip)
   {
      this.zip = zip;
   }
   
   public double getLatitude()
   {
      return this.latitude;
   }
   
   public void setLatitude(double latitude)
   {
      this.latitude = latitude;
   }
   
   public double getLongitude()
   {
      return this.longitude;
   }
   
   public void setLongitude(double longitude)
   {
      this.longitude = longitude;
   }
   
   public int getID()
   {
      return this.facilityID;
   }
   
   public void setID(int facilityID)
   {
      this.facilityID = facilityID;
   }
   
   public String getNotes()
   {
	   return this.notes;
   }
   
   public void setNotes(String notes)
   {
	   this.notes = notes;
   }
   
   public int getFacilityID()
   {
	   return this.facilityID;
   }
   
   public void setFacilityID(int facilityID)
   {
	   this.facilityID = facilityID;
   }
}
