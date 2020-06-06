package WorkManager;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionManager {

    // connection manager is a singleton class, for every query to be executed, the same instance of connection is used
    // as reconnecting to database takes alot of time. So it is better to connect once
    private static String url = "";
    private static String driver = "net.sourceforge.jtds.jdbc.Driver";
    private static String userName = "";
    private static String password = "";
    private static Connection conn = null;

    public static Connection setConnectionDetails(String url, String userName, String password) {
        ConnectionManager.url = url;
        ConnectionManager.userName = userName;
        ConnectionManager.password = password;
        return getConnection();

    }

    public static Connection getConnection() {
        if (conn == null) {
//            System.out.println("GETTING CONNECTION");
            try {
                Class.forName(driver);
                conn = DriverManager.getConnection(url, userName, password);
//                System.out.println("CONNECTION ESTABLISHED");

            } catch (Exception e) {
            }
        }

        return conn;
    }
}
