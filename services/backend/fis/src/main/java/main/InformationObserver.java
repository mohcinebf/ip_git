package main;

public class InformationObserver extends Thread
{
    private static final int THREAD_SLEEPING_TIME_IN_SECONDS = 2; // Es gibt keinen Grund, warum dieser Thread die ganze Zeit auf voller Power laufen sollte.
    private static final boolean ALLOW_DEBUGING_TO_CONSOL = false;
    public boolean keepRunning = true;
    private InformationHandeler myHandler;
    public void run()
    {
        if(ALLOW_DEBUGING_TO_CONSOL)
            System.out.println("Infoobserver has started!");
        while(keepRunning)
        {
            if(ALLOW_DEBUGING_TO_CONSOL)
                System.out.println("Infoobserver is running!");
            checkNewIncommingInfos();
            checkSensors();
            checkQueue();
            try
            {
                sleep(THREAD_SLEEPING_TIME_IN_SECONDS*1000);
            }
            catch (InterruptedException e) // Weil man es mal wieder fangen muss....
            {}
        }
        if(ALLOW_DEBUGING_TO_CONSOL)
            System.out.println("Infoobserver has ended!");
    }

    public InformationObserver(InformationHandeler myHandler)
    {
        this.myHandler = myHandler;
    }

    /*
        Methode die überprüft, ob die Queue eine Leerstellen hat. Falls ja wir die Queue um ein Objekt erweitert.
     */
    private void checkQueue()
    {
        if(myHandler.getMaxQueueSize() > myHandler.getQueueSize())
            myHandler.fillQueueSlot();
    }

    /*
        Diese Methode wird langfristig den Kontakt mit der Zentrale regeln.
     */
    private void checkNewIncommingInfos()
    {

    }

    /*
        Diese Methode wird langfristig den Kontakt mit der Sensorik regeln.
     */
    private void checkSensors()
    {

    }
}
