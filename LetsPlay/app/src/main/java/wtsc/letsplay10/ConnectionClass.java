package wtsc.letsplay10;

import android.os.StrictMode;
import android.util.Log;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Connection Class - this class establishes a connection
 * to the SQL database for the WTSC LetsPlay app
 * Created by Ricky Stambach on 2/15/2017.
 */

public class ConnectionClass {
    String ip = "wtsc.database.windows.net";        // connection variables
    String classs = "net.sourceforge.jtds.jdbc.Driver";
    String db = "LetsPlay";
    String un = "WakeTechSC@wtsc";                  // user name
    String password = "Capstone1";                  // password


    public java.sql.Connection CONN() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder() // set thread policy
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        java.sql.Connection conn = null;
        String ConnURL = null;
        try {
            Class.forName(classs);                  // sets jdbc driver
            ConnURL = "jdbc:jtds:sqlserver://" + ip + ";"       // sets connection URL
                    + "databaseName=" + db + ";user=" + un + ";password="
                    + password + ";";
            conn = DriverManager.getConnection(ConnURL);        // gets connection

            // catch exceptions
        } catch (SQLException se) {
            Log.e("ERRO", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("ERRO", e.getMessage());
        } catch (Exception e) {
            Log.e("ERRO", e.getMessage());
        }
        return conn;        // returns the connection object
    }


}
