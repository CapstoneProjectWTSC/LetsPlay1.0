package wtsc.letsplay10;
    // @author Alexander Samuel

public class Sport
{
   private String name;
   private int sportID;

   public Sport(){}

   public Sport(int sportID, String name)
   {
      setID(sportID);
      setName(name);
   }

   public int getID()
   {
      return this.sportID;
   }
   
   public void setID(int sportID)
   {
      this.sportID = sportID;
   }
   
   public String getName()
   {
      return this.name;
   }
   
   public void setName(String name)
   {
      this.name = name;
   }
}