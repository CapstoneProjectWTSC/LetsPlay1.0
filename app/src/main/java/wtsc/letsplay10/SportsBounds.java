package wtsc.letsplay10;

import com.google.android.gms.maps.model.LatLngBounds;

/**
 * Created by Ricky Stambach on 4/12/2017.
 */

public class SportsBounds {
    private Sport sportType;
    private LatLngBounds currentBounds;

    public SportsBounds(Sport sport, LatLngBounds bounds){
        sportType = sport;
        currentBounds = bounds;
    }
    public LatLngBounds getBounds(){
        return currentBounds;
    }
    public Sport getSport(){
        return sportType;
    }
    public void setBounds(LatLngBounds bounds){
        currentBounds = bounds;
    }
    public void setUser(Sport sport){
        sportType = sport;
    }
}
