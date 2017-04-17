package wtsc.letsplay10;

import com.google.android.gms.maps.model.LatLngBounds;

import java.util.Date;

/**
 * Created by Ricky Stambach on 4/17/2017.
 */

public class DateTimeBounds {
    private Date bDateTime, eDateTime;
    private LatLngBounds currentBounds;

    public DateTimeBounds(Date startDateTime, Date endDateTime, LatLngBounds boumds){
        currentBounds = boumds;
        bDateTime = startDateTime;
        eDateTime = endDateTime;
    }

    public LatLngBounds getBounds(){return currentBounds;}
    public void setBounds(LatLngBounds bounds){currentBounds = bounds;}
    public Date getbDateTime(){return bDateTime;}
    public void setbDateTime(Date dateTime){bDateTime = dateTime;}
    public Date geteDateTime(){return eDateTime;}
    public void seteDateTime(Date dateTime){eDateTime = dateTime;}
}
