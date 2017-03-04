public class Facility
{
   String name, address1, address2, city, state, notes;
   int facilityID, zip;
   double latitude, longitude;
   
   public Facility(String name, String address1, String address2, String city, String state, String notes, int zip, int facilityID, 
                     double latitude, double longitude)
   {
      setName(name);
      setAddress(address1, address2);
      setCity(city);
      setState(state);
      setNotes(notes);
      setZip(zip);
      setFacilityID(facilityID);
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
   
   public String getAddress()
   {
      if (address2.isEmpty())
      {
         return address1;
      }
      else
      {
         return "Address 1: " + address1 + " Address 2: " + address2;
      }
   }
   
   public void setAddress(String address1, String address2)
   {
      this.address1 = address1;
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
   
   public int getZip()
   {
      return this.zip;
   }
   
   public void setZip(int zip)
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
