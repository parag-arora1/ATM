import com.mysql.cj.jdbc.exceptions.MysqlDataTruncation;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;

public class User {
    /**
     *  The first name of the user.
     */
    private  String firstName;
    /**
     *  The last name of the user.
     */
    private  String lastName;
    /**
     *  The id number of the user.
     */
    private  String uuid;
    /**
     *  The MD5 hash of the user's pin number.
     */
    private  byte pinHash[];


    /**
     * Create User instances from Database
     * @param uuid: UserUUID
     * @param fN : firstname of the user
     * @param lN : lastname of the user
     * @param pin : the user'S account pin number
     *
     */
    User(String uuid, String fN, String lN, String pin)
    {
        // set user's name
        this.firstName=fN;
        this.lastName=lN;

        // store the pin#s MD5 hash for security reasons.
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            this.pinHash = md.digest(pin.getBytes());
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Algorithm not found");
            e.printStackTrace();
            System.exit(1);
        }

        //get a new, unique universal ID for the user
        this.uuid = uuid;


    }

    /**
     * Create a New User instance
     * @param fN : firstname of the user
     * @param lN : lastname of the user
     * @param pin : the user'S account pin number
     * @param b the bank object that the user is a customer of
     */
    User(String fN, String lN, String pin, Bank b)
    {
        // set user's name
        this.firstName=fN;
        this.lastName=lN;

        // store the pin#s MD5 hash for security reasons.
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            this.pinHash = md.digest(pin.getBytes());
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Algorithm not found");
            e.printStackTrace();
            System.exit(1);
        }

        //get a new, unique universal ID for the user
        this.uuid = b.getNewUserUUID();

        //print log message: Just for testing purpose
        System.out.printf("New user %s, %s with Login Details: %s:%s created.\n", this.firstName, this.lastName, this.uuid, pin);

    }

    /**
     *  Get the User ID
     * @return the uuid
     */
    String getUUID() {
        return this.uuid;
    }

    /**
     * get the hashPin as a String
     * @return Hash Pin
     */
    String getPin() {
        return Base64.getEncoder().encodeToString(this.pinHash);
    }

    /**
     *  Get the User first name
     * @return the user's first name
     */
    String getFirstName() {
        return this.firstName;
    }

    /**
     *  Get the User first name
     * @return the user's first name
     */
    String getLastName() {
        return this.lastName;
    }

    /***
     * Get the accounts names of the user
     * @return Arraylist of accounts names
     */
    ArrayList<String> getAcctNames(){
        Connection conn = null;
        Statement stmt;
        ResultSet rs;
        ArrayList<String>AcctNames = new ArrayList<>();
        try {conn = DBsetup.connect();
            stmt = conn.createStatement();

            rs=stmt.executeQuery("SELECT name from Accounts WHERE U_id="+Integer.parseInt(this.uuid)+";");
            while (rs.next()) {
                AcctNames.add(rs.getString("name"));
            }
            return AcctNames;

        } catch (SQLException |NullPointerException e) {
            System.out.println("Error When Getting Accounts Names");
            e.printStackTrace();
        }finally {
            DBsetup.close(conn);
        }
     return null;
    }

    /**
     *  Check whether a given pin matched the Correct User pin
     * @param aPin the pin to check
     * @return whether the pin is valid or not
     */
    boolean validatePin(String aPin){
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return  Base64.getEncoder().encodeToString(this.pinHash).compareTo(aPin)==0;
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Algorithm not found");
            e.printStackTrace();
            System.exit(1);
        } return false;
    }

    /**
     * * Print transaction history for a particular account
     * @param accname the name of the account to use
     */
    void printAcctTransHistory(String accname)
    { Account newAccount = null;
        Connection conn = null;
        Statement stmt;
        ResultSet rs;

        try {  conn = DBsetup.connect();
            stmt = conn.createStatement();
            rs=stmt.executeQuery("SELECT Acc_uuid,balance from Accounts WHERE name='"
                    +accname+"' AND U_id="+Integer.parseInt(this.uuid)+";");
            while (rs.next()) {
                 newAccount=new Account(accname,
                        rs.getString("Acc_uuid"),
                        rs.getDouble("balance"));
            }
            newAccount.printTransHistory();

        } catch (SQLException |NullPointerException e) {
            System.out.println("Error When Getting Accounts Transactions History");
            e.printStackTrace();
        }finally {
            DBsetup.close(conn);
        }
    }

    /**
     *  Get the balance of a particular account
     * @param accName the name of the account to use
     * @return the balance of the account
     */
    double getAcctBalance(String accName){
        Connection conn = null;
        Account newAccount = null;
        Statement stmt;
        ResultSet rs;
        try { conn= DBsetup.connect();
            stmt = conn.createStatement();
            rs=stmt.executeQuery("SELECT Acc_uuid,balance from Accounts " +
                    "WHERE name='"+accName+"'AND U_id="
                    +Integer.parseInt(this.uuid)+";");
            while (rs.next()) {
                newAccount=new Account(accName,
                        rs.getString("Acc_uuid"),
                        rs.getDouble("balance"));
            }
            return newAccount.getBalance();

        } catch (SQLException |NullPointerException e) {
            System.out.println("Error When Getting the Account's Balance");
            e.printStackTrace();
        }finally {
            DBsetup.close(conn);
        }
        return 0;
    }

    /***
     *  Get the UUID of particular account
     * @param accName the name of the account to use
     * @return the UUID of the account
     */
    String getAcctUUID(String accName){
        Connection conn = null;
        Statement stmt;
        ResultSet rs;
        Account newAccount = null;
        try {conn = DBsetup.connect();
            stmt = conn.createStatement();
            rs=stmt.executeQuery("SELECT Acc_uuid,balance from Accounts " +
                    "WHERE name='"+accName+"'AND U_id="
                    +Integer.parseInt(this.uuid)+";");
            while (rs.next()) {
                newAccount=new Account(accName,
                        rs.getString("Acc_uuid"),
                        rs.getDouble("balance"));
            }
            return newAccount.getUUID();

        } catch (SQLException |NullPointerException e) {
            System.out.println("Error When Getting the Account's Balance");
            e.printStackTrace();
        }finally {
            DBsetup.close(conn);
        }
        return null;
    }

    /**
     * Print Summaries for the accounts of the user.
     */
    void printAccountsSummary() {
        Connection conn = null;
        Statement stmt;
        ResultSet rs;
        ArrayList<Account> Accounts = new ArrayList<>();
        try {conn = DBsetup.connect();
             stmt = conn.createStatement();
             rs=stmt.executeQuery("SELECT Acc_uuid,balance,name from Accounts WHERE U_id="
                     +Integer.parseInt(this.uuid)+";");
            while (rs.next()) {
                Accounts.add(new Account(rs.getString("name"),
                        rs.getString("Acc_uuid"),
                        rs.getDouble("balance")));
            }

        } catch (SQLException |NullPointerException e) {
            System.out.println("Error When Getting the Account's Summaries");
            e.printStackTrace();
        }finally {
            DBsetup.close(conn);
        }
        System.out.printf("\n\n%s's accounts summary,\n", this.firstName);
        for (int a =0; a< Accounts.size();a++){
            System.out.printf("  %d) %s\n",a+1,Accounts.get(a).getSummaryLine());

        }
        System.out.println();

    }

    /**
     *  Update the Account's Balance and add the Transaction to the Database
     * @param accName the Account to update
     * @param amount the amount of the transaction
     * @param memo a short description of the transaction
     */
    void addAcctTransaction(String accName, double amount, String memo) {
        Connection conn = null;
        Statement stmt;
        ResultSet rs;
        PreparedStatement preparedStmt = null;
        Account newAccount = null;
        try {conn = DBsetup.connect();
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            conn.setAutoCommit(false);
            stmt = conn.createStatement();
            rs=stmt.executeQuery("SELECT Acc_uuid,balance from Accounts " +
                    "WHERE name='"+accName+"'AND U_id="
                    +Integer.parseInt(this.uuid)+" FOR UPDATE;");
            while (rs.next()) {
                newAccount=new Account(accName,
                        rs.getString("Acc_uuid"),
                        rs.getDouble("balance"));
            }

            //Second Check on Balance: Add Transaction and update Balance
            // only when amount is not exceeding the balance
            if((newAccount.getBalance()+amount)>=0)
            {newAccount.addTransaction(amount,memo,conn);
                newAccount.setBalance(amount);

            }else{
                System.out.println("We are Sorry, The Account's balance is lower Than" +
                        "The Amount you want to Withdraw");
                return;
            }

            //Update Account in Database
                    preparedStmt = conn.prepareStatement("update Accounts set balance ="+
                    newAccount.getBalance()+"where name ='" +
                    newAccount.getAccName()+"' AND U_id="+
                    Integer.parseInt(this.uuid)+";");

           preparedStmt.executeUpdate();
            conn.commit();
        } //3rd Check : In case Check 1 and 2 are bypassed
        catch (MysqlDataTruncation e) {
            System.out.printf("The Amount must not exceed the account's balance "+
                    "(%.02f Euro) \n", newAccount.getBalance());
            if (conn != null) {
               DBsetup.rollback(conn);
            }
            DBsetup.close(conn);
        } catch (SQLException e) {
            e.getStackTrace();
            if (conn != null) {
                DBsetup.rollback(conn);
            }
            System.out.println("Error When Updating Balance to database");
            DBsetup.close(conn);
        }finally {
                DBsetup.close(conn);
            }

    }

}
