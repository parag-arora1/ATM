
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


public class Transaction {
    /**
     *  The amount of this transaction.
     */
    private double amount;
    /**
     *  The time and date of this transaction.
     */
    private String timestamp;
    /**
     *  A memo of this transaction.
     */
    private String memo;
    /**
     * DB connection
     */



    /**
     *  Create a new transaction in the Database
     * @param amount the amount transacted
     * @param memo the memo for the transaction
     * @param accID the account ID related to the transaction
     *
     */
    Transaction(double amount, String memo, String accID,Connection conn){
            Statement stmt;
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate("INSERT INTO Transactions(amount,memo,timestamp,account_id) " +
                    "VALUES ("+amount+",'"+memo+"',NOW(),"+accID+");");
        } catch (SQLException | NullPointerException e) {
            System.out.println("Error Adding The Transaction to the Database");
            e.printStackTrace();
        }

    }


    /**
     *  Create a  transaction instance
     * @param amount the amount transacted
     * @param memo the memo for the transaction
     * @param timestamp the date of the transaction
     *
     */
    Transaction(double amount, String memo, String timestamp){
        this.amount = amount;
        this.timestamp =timestamp;
        this.memo=memo;

    }


    /**
     * Get a string summarizing the transaction
     * @return the summary string
     */
    String getSummaryLine(){
        if(this.memo==null)this.memo="No memo";
        if(this.amount>0){
            return String.format("Deposit: %s : %.02f Euro : %s", this.timestamp
            ,this.amount,this.memo);}
        else{
            return String.format("Withdraw: %s : %.02f Euro  : %s", this.timestamp
                    ,this.amount,this.memo);
        }
    }
}
