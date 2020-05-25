import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

public class ATM {

    /**
     * User Commandline login
     * @param theBank the Bank
     * @param sc the Scanner
     * @return the authorized User
     */
     static User mainMenuPrompt(Bank theBank, Scanner sc){
        //inits
        String userID;
        String pin;
        User authUser;

        // prompt the user for user ID/pin combo until a correct one is reached
         do {
             System.out.println("\nLogin Details For Testing: 08915741:4321\n");
             System.out.println("\nWelcome to " + theBank.getName() + "\n\n");
            System.out.println("Enter user ID:");
            userID = sc.nextLine();
            System.out.println("Enter your pin:");
            pin = sc.nextLine();

            //try to get the user object corresponding to the ID and pin combo
            authUser = theBank.userLogin(userID,pin);
            if(authUser == null){
                System.out.println("Incorrect user ID/pin combination. Please try again.");
            }
        }
        while(authUser == null); //continue looping until successful login
        return authUser;
    }

    /**
     * User Commandline Interface
     * @param theUser the authorized user
     * @param sc the scanner
     *
     */
     static void printUserMenu(User theUser, Scanner sc){
        //print a summary of the user's accounts
        theUser.printAccountsSummary();

        //init
        int choice ;

        // user menu
        do{
            System.out.printf("Welcome %s, what would you like to do?\n",
                    theUser.getFirstName());
            System.out.println("  1) Show account transaction history");
            System.out.println("  2) Withdraw");
            System.out.println("  3) Deposit");
            System.out.println("  4) Transfer");
            System.out.println("  5) Quit");
            System.out.println();
            System.out.println("Enter Choice :");
            choice = sc.nextInt();

            if(choice <1 || choice >5 ){
                System.out.println("Invalid choice. Please choose 1-5");
            }
        }
        while(choice <1 || choice >5); //continue looping until get a correct choice
        // process the choice
        switch (choice)
        {
            case 1:
                ATM.showTransHistory(theUser,sc);
                break;
            case  2:
                ATM.withdrawlFunds(theUser,sc);
                break;
            case 3:
                ATM.depositFunds(theUser,sc);
                break;
            case 4:
                ATM.transferFunds(theUser,sc);
                break;
            case 5:
                sc.nextLine();
                break;

        }
        //redisplay this menu unless the user wants to quit
        if(choice != 5){
            ATM.printUserMenu(theUser,sc);

        }
    }


    /**
     *  Show the transaction history for an account
     * @param theUser the logged-in User object
     * @param sc the Scanner object used for user input
     */
    private static void showTransHistory(User theUser, Scanner sc){

        String theAcct;
        ArrayList<String> Accounts=theUser.getAcctNames();
        //TODO:Handle case the user has no account yet
        //get the account whose transaction history to look at
        do{
            System.out.println("Enter the name of the account whose transaction you " +
                    "want to see:");
            for(int i=0;i<Accounts.size();i++)
                System.out.println("  "+i+")"+Accounts.get(i));
                    theAcct = sc.next();
                    if(!Accounts.contains(theAcct)){
                        System.out.println("Invalid account. Please try again.");
                    }

        }while(!Accounts.contains(theAcct));

        //print the transaction history
        theUser.printAcctTransHistory(theAcct);
    }

    /**
     *  Process transerring funds from one account to another
     * @param theUser the logged-in User object
     * @param sc the Scanner object used for user input
     */

    private static void transferFunds(User theUser, Scanner sc) {
        //inits
        String fromAcct;
        String toAcct;
        double amount;
        double acctBal;
        String memo;
        ArrayList<String> Accounts=theUser.getAcctNames();
        //TODO:Handle case the user has no account yet
        // get the account to transfer from
        do{
            System.out.println("Enter the name of the account to transfer from:");
            for(int i=0;i<Accounts.size();i++)
                System.out.println("  "+i+")"+Accounts.get(i));
            fromAcct = sc.next();
            if(!Accounts.contains(fromAcct)){
                System.out.println("Invalid account. Please try again.");
            }

        }while(!Accounts.contains(fromAcct));
        acctBal = theUser.getAcctBalance(fromAcct);

        //get the account to transfer to

        do{
            System.out.println("Enter the name of the account to transfer to:");
            for(int i=0;i<Accounts.size();i++)
                System.out.println("  "+i+")"+Accounts.get(i));
            toAcct = sc.next();
            if(!Accounts.contains(fromAcct)){
                System.out.println("Invalid account. Please try again.");
            }
            if(toAcct.equals(fromAcct)){
                System.out.println("Cannot transfer to the same account");
            }
        }
        while(!Accounts.contains(fromAcct) || toAcct.equals(fromAcct));

         //get the amount to transfer
        do{
            System.out.printf("Enter the amount to transfer (max %.02f Euro):"
            ,acctBal);
            amount=sc.nextInt();
            if(amount<=0){
                System.out.println("Amount must be greater than zero.");
            }else if (amount>acctBal){
                System.out.printf("Amount must not be greater than\n"+
                        "balance of %.02f Euro. \n", acctBal);
            }
        }while(amount<=0 || amount >acctBal);

        //Do the transfer
        memo=theUser.getAcctUUID(toAcct);
        theUser.addAcctTransaction(fromAcct, -1*amount,String.format(
                "Transfer to account %s",memo));
        memo=theUser.getAcctUUID(fromAcct);
        theUser.addAcctTransaction(toAcct,amount,String.format(
                "Transfer from account %s",memo));

    }

    /**
     *  Process a fund withdraw from an account
     * @param theUser the logged-in User object
     * @param sc the Scanner object user for user input
     */

    private static void withdrawlFunds(User theUser, Scanner sc) {
        //inits
        String fromAcct;
        String memo;
        double amount;
        double acctBal;
        ArrayList<String> Accounts=theUser.getAcctNames();
        //TODO:Handle case the user has no account yet
        // get the account to transfer from
        do{
            System.out.println("Enter the name of the account to withdraw from:");
            for(int i=0;i<Accounts.size();i++)
                System.out.println("  "+i+")"+Accounts.get(i));
            fromAcct = sc.next();
            if(!Accounts.contains(fromAcct)){
                System.out.println("Invalid account. Please try again.");
            }

        }while(!Accounts.contains(fromAcct));

        acctBal = theUser.getAcctBalance(fromAcct);

        if(acctBal!=0){
        //get the amount to transfer
        do{
            System.out.printf("Enter the amount to withdraw (max %.02f Euro):"
                    ,acctBal);
            amount=sc.nextInt();
            if(amount<=0){
                System.out.println("Amount must be greater than zero.");
            }
            //First Check on Balance
            else if (amount>acctBal){
                System.out.printf("Amount must not exceed the account's balance "+
                        "(%.02f Euro) \n", acctBal);
            }
        }while(amount<=0 || amount >acctBal);

        // gobble up the rest of the previous input
        sc.nextLine();

        //get a memo
        System.out.println("Enter a memo:");
        memo=sc.nextLine();

        //do the withdrawal
            theUser.addAcctTransaction(fromAcct, -1 * amount, memo);
        } else {
            System.out.println("You can not withdraw your balance is 0.00 Euro ");

        }

    }

    /**
     *  Process a fund deposit to an account
     * @param theUser the logged-in User object
     * @param sc the Scanner object used for user input
     */

    private static void depositFunds(User theUser, Scanner sc)
    { //inits

        String toAcct;
        String memo;
        double amount;
        ArrayList<String> Accounts=theUser.getAcctNames();
        //TODO:Handle case the user has no account yet
        // get the account to deposit in

        do{
            System.out.println("Enter the name of the account to deposit in:");
            for(int i=0;i<Accounts.size();i++)
                System.out.println("  "+i+")"+Accounts.get(i));
            toAcct = sc.next();
            if(!Accounts.contains(toAcct)){
                System.out.println("Invalid account. Please try again.");
            }

        }while(!Accounts.contains(toAcct));

        //get the amount to deposit
        do{
            System.out.println("Enter the amount to deposit:");
            amount=sc.nextDouble();
            if(amount<=0){
                System.out.println("Amount must be greater than zero.");
            }
        }while(amount<=0 );

        // gobble up the rest of the previous input
        sc.nextLine();

        //get a memo
        System.out.println("Enter a memo:");
        memo=sc.nextLine();

        //do the deposit
        theUser.addAcctTransaction(toAcct, amount,memo);

    }


}
