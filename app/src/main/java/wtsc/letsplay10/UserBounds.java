package wtsc.letsplay10;

import com.google.android.gms.maps.model.LatLngBounds;

/**
 * Created by Ricky Stambach on 4/10/2017.
 */

public class UserBounds {
    private User currentUser;
    private LatLngBounds currentBounds;

    public UserBounds(User user, LatLngBounds bounds){
        currentUser = user;
        currentBounds = bounds;
    }
    public LatLngBounds getBounds(){
        return currentBounds;
    }
    public User getUser(){
        return currentUser;
    }
    public void setBounds(LatLngBounds bounds){
        currentBounds = bounds;
    }
    public void setUser(User user){
        currentUser = user;
    }
}
