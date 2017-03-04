package wtsc.letsplay10;

/**
 * This class represents the User entity in the database. This class will store all of the User table columns for easy retrieval
 *
 * Bugs: There will be some methods that are either discarded or heavily altered due to the nature of Android Studio.
 * 		 This class was written in Eclipse, so some things will likely change (specifically the DB connection code).
 *
 * @author Alexander Samuel
 *
 */

public class User {

    private int ID;
    private String fName, lName, password, email, gameName;

    public User()   // default constructor
    {
    }

    public User(User user){
        setID(user.getID());
        setFirstName(user.getFirstName());
        setLastName(user.getLastName());
        setGameName(user.getGameName());
        setPassword(user.getPassword());
        setEmail(user.getEmail());
    }   // constructs new user from another User object

    public User (int id, String fName, String lName, String gameName,   // constructs new User object
                 String password, String email)                         // with parameters for all members
    {
        setID(id);
        setFirstName(fName);
        setLastName(lName);
        setGameName(gameName);
        setPassword(password);
        setEmail(email);
    }

    // getter and setter methods

    public String getFirstName()
    {
        return this.fName;
    }

    public void setFirstName(String fName)
    {
        this.fName = fName;
    }

    public String getLastName()
    {
        return this.lName;
    }

    public void setLastName(String lName)
    {
        this.lName = lName;
    }

    public void setGameName(String gameName)
    {
        this.gameName = gameName;
    }

    public String getGameName()
    {
        return this.gameName;
    }

    public String getEmail()
    {
        return this.email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPassword()
    {
        return this.password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public int getID()
    {
        return this.ID;
    }

    public void setID(int id){this.ID = id;}
}

