package wtsc.letsplay10;

/**
 * OnUserDataLoaded interface - remote procedure calls (RPC) event handling function
 * for the MainActivity class implements the interface and receives the event callback
 * from the GetCurrentUser Class notifying when the GetCurrentUser class has finished
 * retrieving data from the database and returns the user object.
 * Created by Ricky Stambach on 2/16/2017.
 *
 */

public interface OnUserDataLoaded {
    void onCurrentUserDataLoaded(User user);
}
