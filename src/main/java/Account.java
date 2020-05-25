import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Account {
    /**
     *  The name of the account.
     */
    private String name;
    /**
     *  The current balance of the account.
     */
    private double balance;
    /**
     *  The account ID number.
     */
    private String uuid;

    /**
     * DB connection
     */
    private Connection conn;
    /**
     * Statement
     */
    private Statement stmt;

    /**
     * Result set
     */
    private ResultSet rs;

    /**
     * Create a new Account in the database
     * @param name the account's name
     * @param holder the account's holder
     * @param b the bank
     */
    Account(String name, User holder, Bank b)
    {   //set the account name and holder
        this.name=name;

        //get new account UUID
         this.uuid= b.getNewAccountUUID();


        //init balanace
       this.balance =0.f;

       try {conn = DBsetup.connect();
            stmt = conn.createStatement();
            stmt.executeUpdate("INSERT IGNORE INTO Accounts(Acc_uuid,name,balance,U_id,B_id) " +
                    "VALUES ("+this.uuid+",'"+this.name+"',"+this.balance
                    +","+holder.getUUID()+","+b.getId()+");");
           conn.close();
        } catch (SQLException e) {
            System.out.println("Error Adding an account to the Database");
            e.printStackTrace();
        }
    }

    /**
     * Retrieve an account from the database
     * @param name the account's name
     * @param uuid the account's id
     * @param balance the account's balance
     */
    Account(String name, String uuid, double balance)
    {   //set the account name and holder
        this.name=name;

        //get new account UUID
        this.uuid= uuid;

        //init balanace
        this.balance =balance;

    }

    /**
     *  Get the account ID
     * @return the uuid
     */
    String getUUID(){
        return this.uuid;
    }

    /**
     *  Get the account Name
     * @return the uuid
     */
    String getAccName(){
        return this.name;
    }

    /**
     *  Get Summary line for the account
     * @return the string summary
     */
    String getSummaryLine(){
        // get the account's balance
        double balance = this.getBalance();

        //format the summary line, depending on whether the balance is negative or not
        if (balance>=0){
            return String.format("%s: %.02f Euro : %S", this.uuid, balance, this.name);

        }else{
            return String.format("%s : (%.02f) Euro : %s", this.uuid, balance, this.name);

        }
    }

    /**
     * Get the balance of the account
     * @return the balance value
     */
    double getBalance(){
        return balance;
    }

    /**
     * Set the balance of the account
     */
    void setBalance(double b){
         this.balance+=b;
    }

    /**
     * Print the transaction history of a User account
     */
    void printTransHistory(){
        ArrayList<Transaction> transactions = new ArrayList<>();
        try {conn = DBsetup.connect();
            stmt = conn.createStatement();
            rs=stmt.executeQuery("SELECT timestamp,amount,memo from Transactions " +
                    "WHERE account_id="+Integer.parseInt(this.uuid)+";");
            while (rs.next()) {
                transactions.add(new Transaction(rs.getDouble("amount"),
                        rs.getString("memo"),
                        rs.getDate("timestamp").toString()));

            }
        } catch (SQLException |NullPointerException e) {
            System.out.println("Error Loading Transactions the Database");
            e.printStackTrace();
        }finally {
            DBsetup.close(conn);
        }
        System.out.printf("\nTransaction history for account %s\n",this.uuid);
        if(transactions!=null &&transactions.size()>0){
        for(int t =transactions.size()-1;t>=0; t--){
            System.out.println(transactions.get(t).getSummaryLine());}}
            System.out.println();
    }

    /**
     *  Add a new transaction in the account
     * @param amount the amount transacted
     * @param memo the transaction memo
     */
    void addTransaction(double amount, String memo,Connection conn){
        Transaction newTrans = new Transaction(amount, memo,this.uuid,conn);
    }


}
