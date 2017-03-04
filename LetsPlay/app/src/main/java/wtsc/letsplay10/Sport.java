public class Sport
{
   private String name;
   private int sportID;
   public Sport(String name, int sportID)
   {
      setID(sportID);
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