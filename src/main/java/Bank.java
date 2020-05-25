import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;

public class Bank {
    /**
     * The Bank ID
     */
    private int id;

    /**
     *  The Bank Name
     */
    private String name;


    /**
     * DB connection
     */
    private Connection conn;

    /**
     * Statement
     */
    private Statement stmt;

    /**
     * Result set of the query
     */
    private  ResultSet rs;

    /**
     *  Create a new Bank object with empty lists of users and accounts
     * @param name the name of the bank
     */
    Bank(String name)
    {
        try {
            conn = DBsetup.connect();
            stmt = conn.createStatement();
            stmt.executeUpdate("INSERT IGNORE INTO Banks(name)VALUES ('"+name+"');");
            rs = stmt.executeQuery("SELECT id FROM Banks WHERE name='"+name+"';");
            while (rs.next())
                this.id =rs.getInt("id") ;
        } catch (SQLException |NullPointerException e) {
            System.out.println("Connecting to Database Failed");
            e.printStackTrace();
        }finally {
            DBsetup.close(conn);
        }
        this.name = name;
    }

    /**
     *  Generate a new universally unique ID for a user
     * @return the uuid
     */
    synchronized String getNewUserUUID() {
        ArrayList<String> uuids = new ArrayList<>();
        try {
            conn = DBsetup.connect();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT uuid FROM Users;");

            while (rs.next()) {
                uuids.add(rs.getString("uuid")) ;
            }
        } catch (SQLException |NullPointerException e) {
            System.out.println("Connecting to Database Failed");
            e.printStackTrace();
        }finally {
            DBsetup.close(conn);
        }
        // inits

        Random rng = new Random();
        int len = 8;
        boolean nonUnique;

        //loop until getting a unique id
        String uuid = getUniqueID(uuids, rng, len);
        return  uuid;
    }

    /**
     *  Generate a new universally unique ID for a account
     * @return the uuid
     */
    synchronized String getNewAccountUUID() {
        ArrayList<String> uuids = new ArrayList<>();
        try {
            conn = DBsetup.connect();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT Acc_uuid FROM Accounts;");
            while (rs.next())
                uuids.add(rs.getString("Acc_uuid")) ;

        } catch (SQLException |NullPointerException e) {
            System.out.println("Failed Generating a unique Account id");
            e.printStackTrace();
        }finally {
            DBsetup.close(conn);
        }
        // inits
        String uuid;
        Random rng = new Random();
        int len = 8;
        boolean nonUnique;

        //loop until getting a unigue id
        uuid = getUniqueID(uuids, rng, len);
        return  uuid;
    }

    /**
     * Generate a unigue UUID
     * @param uuids existing uuids
     * @param rng range
     * @param len length
     * @return a unique uuid
     */
    private String getUniqueID(ArrayList<String> uuids, Random rng, int len) {
        String uuid;
        boolean nonUnique;
        do{
            //generate the number;
            uuid="";
            for (int c =0;c <len; c++){
                uuid +=((Integer)rng.nextInt(10)).toString();
            }
            nonUnique = false;
            for(String u: uuids){
                if(uuid.compareTo(u) ==0){
                    nonUnique = true;
                    break;
                }
            }
        }
        while (nonUnique);
        return uuid;
    }


    /**
     *  Get the User object associated wit ha particulat userID and pin
     *  if they are valid
     * @param userID the UUID of the user to log in
     * @param pin the pin of the user
     * @return  the User object, if the login is successful, or null if it is not
     */
    User userLogin(String userID, String pin){
        try {conn = DBsetup.connect();
            stmt = conn.createStatement();

           //TODO:handle Input cannot be converted to int
            // TODO:Add a foreign key to the users for bank users management: not mandatory
            rs= stmt.executeQuery("SELECT * from Users Where uuid="+Integer.parseInt(userID)+";");
            while (rs.next()) {
                User u = new User(rs.getString("uuid"),rs.getString("firstName"),
                        rs.getString("lastName"),pin);

                String userHashPin=rs.getString("pinCode");
                if(u.validatePin(userHashPin)) return u;
            }

        } catch (SQLException | NullPointerException e) {
            System.out.println("Error When Fetching the user");
            e.printStackTrace();
        }finally {
            DBsetup.close(conn);
        }


        // if we haven't found the user or have an incorrect pin
        return null;
    }

    /**
     * Get the name of the bank
     * @return the name of the bank
     */
    String getName() {
        return this.name;
    }

    /**
     * get the Bank id
     * @return the bank id
     */
    int getId() {
        return this.id;
    }

    /**
     *  Add an account to the bank
     * @param aUser the User to open the account for
     * @param AccName the account name
     */
    void addAccount(String AccName, User aUser) {
        Account newAccount = new Account(AccName,aUser,this);
    }

    /**
     *  Create a new user of the bank
     * @param firstName the user's first name
     * @param lastName the user's last name
     * @param pin the user's pin
     * @return the new User Object
     */
    User addUser(String firstName, String lastName, String pin, String accName) {


        // create a new User object and add it to the bank list
        User newUser = new User(firstName,lastName, pin, this);
        try {conn = DBsetup.connect();
            stmt = conn.createStatement();
            stmt.executeUpdate("INSERT IGNORE INTO Users(uuid,pinCode, firstName,lastName,bank_id) " +
                    "VALUES ("+newUser.getUUID()+",'"+newUser.getPin()+"','"+newUser.getFirstName()
                    +"','"+newUser.getLastName()+"',"+this.id+");");

        } catch (SQLException |NullPointerException e) {
            System.out.println("Error Adding a user to the Database");
            e.printStackTrace();
        }finally {
            DBsetup.close(conn);
        }
        // create an account for the user.
        Account newAccount = new Account(accName,newUser,this);

        return newUser;

    }

}
