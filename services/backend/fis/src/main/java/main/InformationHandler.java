// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
package main;

/**
    Der InformationHandler (IH oder ih) ist die Zentralelogik für die Verwaltung der Informationen.
    Der IH nutzt den Scheduler, um die nächsten Informationstypen zu bestimmen, besitzt die Handler Klassenstruktur für die Queue Verbindung zum Core,
    und startet außerdem den InformationOberserver; dies ist der Thread, welcher die Ein-/Ausgänge des Handlers überwacht und entsprechend aud Veränderungen reagiert.

    Da der Thread leider nicht als Methode des InformationHandlers in java erstellt werden kann, müssen mehr Methoden public sein, als von mir gewollt.
    Die folgenden Methoden sind für die Benutzung durch den Core oder die Main freigegeben
    -Alle ererbten öffentlichen Funktion der Handler-Klasse
    -stopMyObserver - Methode um den Observer sauber zu beenden - Aufzurufen, wenn das Programm enden soll.
    -popInfo - macht das Gleiche wie Handler.pop nur ist die Rpckgabe schon gecastet.
 @author Robin
 */
public class InformationHandler extends Handler
{
    private Scheduler scheduler;
    private static final int MAX_QUEUE_SIZE = 10;
    private static final int TEMP_INT_UPCOMMING_FREQ = 7; // soll später durch eine config eingelesen werden.
    private static final int TEMP_INT_UPCOMMING_DURA = 15; // soll später durch eine config eingelesen werden.

    /**
        Eine billige Methode die einfache Informationsobjekte für schnelle Wegwerftests erstellt.
     */
    private void dirtyTestingDataFilling()
    {
        Information info1 = new TextAdversiting("Lidl",5,1.3, 1.0,13);
        Information info2 = new TextAdversiting("Aldi",4,1.0, 1.0,12);
        Information info3 = new TextAdversiting("Rewe",1,0.9, 1.0,19);
        Information info5 = new TextAdversiting("Edeka",1,0.9, 0.0,26);
        Information info6 = new TextAdversiting("Penny",1,0.3, 1.0,7);
        Information info7 = new TextAdversiting("Netto",1,0.2, 1.0,14);
        Information info8 = new TextAdversiting("Kaufland",1,1.2, 1.0,19);
        Information info9 = new TextAdversiting("Globus",1,0.066, 1.0,22);
        Information info10 = new TextAdversiting("Kaisers",1,5.5, 0.0,11);
        //Die Freq wird ignoriert, falls der Typ schon einmal eingefügt wurde.

        scheduler.addNewInformation(info1);
        scheduler.addNewInformation(info2);
        scheduler.addNewInformation(info3);
        scheduler.addNewInformation(info5);
        scheduler.addNewInformation(info6);
        scheduler.addNewInformation(info7);
        scheduler.addNewInformation(info8);
        scheduler.addNewInformation(info9);
        scheduler.addNewInformation(info10);
    }

    /**
        Methode zum Füllen des Scheduler mit den nicht singulären Information, die aus technischen Gründen immer existieren. Wird für deren Frequenz eine null Referenz gegeben, wird dieses Objekt nicht dem Scheduler zugefügt werden!
     @param ih Der genutzte Handler
     @param UpcommingHaltsFreq - Frequenz für die Fahrstreckenanzeige
     @param DynmaicDataFreq - Frequenz für die dynmischen Daten
     @param LivestreamFreq - Frequenz für den Livestream
     @param UpcommingHaltsDura - Anzeigezeit für Fahrstreckenanzeige
     @param DynmaicDataDura - Anzeigezeit für die dynmischen Daten
     @param LivestreamDura - Anzeigezeit für den Livestream
     */
    private static void basicInfoFiller(InformationHandler ih, Integer UpcommingHaltsFreq, Integer DynmaicDataFreq, Integer LivestreamFreq, Integer UpcommingHaltsDura, Integer DynmaicDataDura, Integer LivestreamDura)
    {
        if(UpcommingHaltsFreq != null)
            ih.addInfoNewToScheduler(new UpcommingHalts(UpcommingHaltsFreq,UpcommingHaltsDura));
        if(DynmaicDataFreq != null)
            ;//Existiert noch nicht
        if(LivestreamFreq != null)
            ;//Existiert noch nicht
    }

    /**
        Konstruktor für den InformationHandler. Startet das ganze System für die Verarbeitung von Informationen. Muss einmal von der main aufgerufen werden und läuft dann bis zum Programmende.
     */
    public InformationHandler()
    {
        super(Information.class.getSimpleName(), MAX_QUEUE_SIZE);
        scheduler = new Scheduler();
        ini();
    }

    /**
        Initialisiert den InformationHandler mit den Startdaten
     */
    private void ini()
    {
        basicInfoFiller(this,TEMP_INT_UPCOMMING_FREQ,null,null,TEMP_INT_UPCOMMING_DURA,null,null);
        dirtyTestingDataFilling(); // Für's erste so
    }

    /**
        Alternative für den Core - dann muss er nicht selber Casten, kann den Handler aber auch nicht so einfach mit anderen Handlern zusammen mixen.
        @return Kopfinformation oder null
     */
    public Information popInfo()
    {
        return (Information) this.pop();
        //
    }

    /**
        Methode um ein Objekt in die Queue zu legen. Oberserver überbleibsel.
     */
    private void fillQueueSlot()
    {
        queue.add(scheduler.getRandomInformation());
        //
    }

    /**
        Information um ein neues Element zum Scheduler hinzuzufügen. Sollte nur InformationHandler oder InformationObserver aufgerufen werden!
     */
    public void addInfoNewToScheduler(Information info)
    {
        scheduler.addNewInformation(info);
        //
    }

    /**
        Öffentliche Methode um den Thread zu beenden. Für ein sauberes clean-up.
     */
    public void stopMyThreads()
    {
        this.keepRunning = false;
    }


    // Ehemalige InformationObserverklasse

    private static final int THREAD_SLEEPING_TIME_IN_SECONDS = 2; // Es gibt keinen Grund, warum dieser Thread die ganze Zeit auf voller Power laufen sollte.
    private static final boolean ALLOW_DEBUGING_TO_CONSOL = false;
    public boolean keepRunning = true;

    /**
     * Startet den Thread zu überwachen, füllen und lesen der eigenen Websocket sowie der eigenen Queue.
     */
    public void run()
    {
        if(ALLOW_DEBUGING_TO_CONSOL)
            System.out.println("Infoobserver has started!");
        while(keepRunning)
        {
            if(ALLOW_DEBUGING_TO_CONSOL)
                System.out.println("Infoobserver is running!");
            checkNewIncomingInfos();
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

    /**
        Methode die überprüft, ob die Queue eine Leerstellen hat. Falls ja wir die Queue um ein Objekt erweitert.
     */
    private void checkQueue()
    {
        if(this.getMaxQueueSize() > this.getQueueSize())
            this.fillQueueSlot();
    }

    /**
        Diese Methode wird langfristig den Kontakt mit der Zentrale regeln.
     */
    private void checkNewIncomingInfos()
    {

    }

    /**
        Diese Methode wird langfristig den Kontakt mit der Sensorik regeln.
     */
    private void checkSensors()
    {

    }
}