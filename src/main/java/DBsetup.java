import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBsetup {
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://";
    private static final String USER = "";
    private static final String PASS = "";
    private static Connection conn = null;

    static Connection connect() {
        //System.out.println("Load MySQL JDBC driver");
        try {
            Class.forName(JDBC_DRIVER);
            //System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            return conn;
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found.");


        } catch (SQLException e) {
            System.err.println("Connection to database failed.");
        }
        return null;
    }
    static void close(Connection c){
        try {
            c.close();
        } catch (SQLException e1) {
            System.err.println("Could not Close the connection.");
        }
    }
    static void rollback(Connection c ){
        try {
            System.err.println("Transaction is being rolled back");
            c.rollback();
        } catch(SQLException excep) {
            System.err.print("Failed rollback");
            excep.getStackTrace();

        }
    }
}
