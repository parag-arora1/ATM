import java.util.Scanner;

public class Test {


    public static void main(String []args){

        /*_*******************************************************_*/
                               /* Test Bank  */
        /*_*******************************************************_*/

        //init Scanner
        Scanner sc = new Scanner(System.in);

        // init bank
        Bank theBank = new Bank(" Test Bank ");
        //add a user, and creates a savings account
        // User aUser1 = theBank.addUser("Gini","Manasse","1234","Saving");
        // add a checking account for the user
        //theBank.addAccount("Checking",aUser1);


        /*_*******************************************************_*/
                          /* Test ATM */
        /*_*******************************************************_*/

        User curUser;
        while (true){
            // stay in the login prompt until successful login
            curUser = ATM.mainMenuPrompt(theBank, sc);
            // stay in the main menu until user quits;
            ATM.printUserMenu(curUser,sc);
        }
        /*_*******************************************************_*/
                            /* To test With Threads */
        /*_*******************************************************_*/
/*
        //add a user, and creates a savings account
        User aUser2 = theBank.addUser("Mathias","Krueger","3124","Saving");
        // add a checking account for the user
        theBank.addAccount("Checking",aUser2);

       Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {

                aUser2.printAccountsSummary();
                aUser2.addAcctTransaction("Saving",-500,"Thread 1");
                aUser2.addAcctTransaction("Saving",750,"Thread 1");
                aUser2.printAccountsSummary();
                aUser2.addAcctTransaction("Saving",-250,"Thread 1");
                aUser2.printAccountsSummary();
            }
        });

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {

                aUser2.printAccountsSummary();
                aUser2.addAcctTransaction("Saving",500,"Thread 2");
                aUser2.printAccountsSummary();
                aUser2.printAccountsSummary();
                aUser2.addAcctTransaction("Saving",-500,"Thread 2");
                aUser2.printAccountsSummary();
                aUser2.addAcctTransaction("Saving",-500,"Thread 2");
            }
        });

        try {t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t1.start();
        t2.start();*/

    }
}
