package wtsc.letsplay10;
    // @author Alexander Samuel

import android.os.Parcel;
import android.os.Parcelable;

public class Sport implements Parcelable
{
   private String name;
   private int sportID;

   public Sport(Parcel in)
   {
       this.sportID = in.readInt();
       this.name = in.readString();
   }

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

   public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
      public Sport createFromParcel(Parcel in) {
         return new Sport(in);
      }

      public Sport[] newArray(int size) {
         return new Sport[size];
      }
   };


   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeInt(sportID);
      dest.writeString(name);
   }
}