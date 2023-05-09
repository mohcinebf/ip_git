package main;

import DataClasses.Information;
import DataClasses.TextAdversiting;
import core.Core;

/**
 * Main class of the program.
 * Starts up the handlers and the core.
 * Also adds some static test data to the information handler.
 * TODO: Starts the console input loop for dynamic events. (FIS-43)
 *
 * @author Fabian Ferrari
 */
public class FIS
{
    private final static String lineName = "45";
    private final static String lineDestination = "Uniklinikum";

    public static void main(String[] args)
    {
        System.out.println("Starting up...");

        // Create handlers
        EmergencyHandler emergencyHandler = new EmergencyHandler();
        InformationHandler informationHandler = new InformationHandler();
        NotificationHandler notificationHandler = new NotificationHandler();
        DrivingStopHandler drivingStopHandler = new DrivingStopHandler();

        // Create core
        Core core = new Core(informationHandler, notificationHandler, emergencyHandler, drivingStopHandler, lineName, lineDestination);

        // Start handlers
        emergencyHandler.start();
        informationHandler.start();
        notificationHandler.start();
        drivingStopHandler.start();

        // Add some test data to the information handler
        addTestData(informationHandler);

        // Wait for user input. When user enters "exit", the program will shut down. Else, ask for a new input.
        DynamicEventsInput.run(notificationHandler, emergencyHandler, drivingStopHandler);

        // Stop handlers
        emergencyHandler.stopMyThreads();
        informationHandler.stopMyThreads();
        notificationHandler.stopMyThreads();
        drivingStopHandler.stopThread();

        try {
            emergencyHandler.join();
            informationHandler.join();
            notificationHandler.join();
            drivingStopHandler.join();
        } catch (InterruptedException e) {
            System.out.println("Interrupted while waiting for threads to stop.");
        }

        // Stop core
        core.stop();


        System.out.println("Shutting down...");
    }

    /**
     * Adds some test data to the information handler.
     * @param informationHandler The information handler to add the test data to.
     */
    public static void addTestData(InformationHandler informationHandler)
    {
        Information info1 = new TextAdversiting("Lidl ist toll",5,1.3, 1.0,5);
        info1.header = "Kauft bei Lidl!";
        Information info2 = new TextAdversiting("Aldi ist toll",4,1.0, 1.0,10);
        info2.header = "Kauft bei Aldi!";
        Information info3 = new TextAdversiting("Rewe ist toll",1,0.9, 1.0,5);
        info3.header = "Kauft bei Rewe!";
        Information info5 = new TextAdversiting("Edeka ist toll",1,0.9, 0.0,10);
        info5.header = "Kauft bei Edeka!";
/*        Information info6 = new TextAdversiting("Penny ist toll",1,0.3, 1.0,7);
        info6.header = "Kauft bei Penny!";
        Information info7 = new TextAdversiting("Netto ist toll",1,0.2, 1.0,14);
        info7.header = "Kauft bei Netto!";
        Information info8 = new TextAdversiting("Kaufland ist toll",1,1.2, 1.0,19);
        info8.header = "Kauft bei Kaufland!";
        Information info9 = new TextAdversiting("Globus ist toll",1,0.066, 1.0,22);
        info9.header = "Kauft bei Globus!";
        Information info10 = new TextAdversiting("Kaisers ist toll",1,5.5, 0.0,11);
        info10.header = "Kauft bei Kaisers!";*/

        informationHandler.addInfoNewToScheduler(info1);
        informationHandler.addInfoNewToScheduler(info2);
        informationHandler.addInfoNewToScheduler(info3);
        informationHandler.addInfoNewToScheduler(info5);
    }
}
