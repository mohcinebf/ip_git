package main;

import DataClasses.Emergency;

import java.util.ArrayList;

public class EmergencyHandler extends Handler
{
    //---------------------------------------------------------------------------------------------------------
    //--------------------------------------Attribute und Konstanten-------------------------------------------
    //---------------------------------------------------------------------------------------------------------
    private ArrayList<Emergency> emers; // Hier werden alle zurzeit aktiven Notis gespeichert.
    private static final double refreshTime = 0.8; // Angabe in Sekunden sollte später durch eine config gesetzt werden
    private static final int MAX_QUEUE_SIZE = 6; // sollte später durch eine config gesetzt werden
    public boolean keepRunning = true;
    private static final boolean ALLOW_PRINTING_TO_CONSOL = true;

    //---------------------------------------------------------------------------------------------------------
    //--------------------------------------Initalisierungs-Kram-----------------------------------------------
    //---------------------------------------------------------------------------------------------------------

    /**
     * Konstruktor für den NotificationHandler. Wird von der main oder Core aufgerufen, um das NotificationSystem zu starten.
     */
    public EmergencyHandler()
    {
        super(Emergency.class.getSimpleName(), MAX_QUEUE_SIZE);
        ini();
    }

    /**
     * Initialisierungsfunktion
     */
    private void ini()
    {
        emers = new ArrayList<>();
        //websocket kram
    }

    //---------------------------------------------------------------------------------------------------------
    //------------------------------Getter+Setter+Zugriffs-Kram------------------------------------------------
    //---------------------------------------------------------------------------------------------------------

    /**
     * Methode die den Head der Queue pop und den Cast übernimmt
     * @return null fall die Queue leer ist und sonst den Head von Typ Emergency
     */
    public Emergency popEmer()
    {
        return (Emergency) pop();
        //
    }

    /**
     * Sucht ein Emer-Objekt aus der Emers Liste
     * @param hash Der hashwert der Emer nach der gesucht werden soll
     * @return Null falls nicht existent sonst das gefundene Objekt
     */
    private Emergency findEmer(int hash)
    {
        for(int i = 0; i != emers.size(); i++)
            if(emers.get(i).hashValue == hash)
                return emers.get(i);
        return null;
    }

    /**
     * Methode zum Finden einer Position zu einer Emer
     * @param emer Die Emer von der ausgehend gesucht wird.
     * @return Die gefundene Position oder null
     */
    private Integer findEmerPos(Emergency emer)
    {
        for(int i = 0; i != emers.size(); i++)
            if(emers.get(i).equals(emer))
                return  i;
        return null;
    }

    /**
     * Methode zum Hinzufügen einer Emergency in der List und überführung in die Queue als aktive Emer
     * @param newEmer Der neue Notfall
     * @throws RuntimeException Falls die neue Notification inaktiv ist.
     * @throws DuplicatNotAllowedException Falls diese Emer bereits im Emers existiert.
     */
    private void addNewEmer(Emergency newEmer)
    {
        if(!newEmer.activ)
            throw new RuntimeException("Error: An inactiv Notification cannot be added a new! Maybe you wanted to use 'deleteNoti'?");
        if(findEmerPos(newEmer) != null)
            throw new DuplicatNotAllowedException("Error: This Notification already exists in this list. Duplicates are not allowed!");
        emers.add(newEmer);
        queue.add(newEmer);
    }

    /**
     * Methode zum Löschen einer Notification in der List und überführung in die Queue als inaktive Noti
     * @param hash Der hash an dem die gesuchte Notification identifiziert wird
     * @throws RuntimeException Falls die Notification nicht gefunden werden konnte
     */
    private void removeEmer(int hash)
    {
        Emergency emer = findEmer(hash);
        if(emer == null)
            throw new RuntimeException("Error: This Notification is not known to the system (anymore)!");
        emers.remove(emer);
        emer.activ = false;
        queue.add(emer);
    }

    //---------------------------------------------------------------------------------------------------------
    //------------------------------Thread-Kram----------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------

    public ArrayList<String> PUBLIC_TEMP_WEBSOCKET_REPLACMENT_DUMMY = new ArrayList<>();
    //Solange es noch keine Websocket gibt, bitte diese ArrayList direkt mit .add befüllen.
    //Ohne so eine Liste wäre der Thread nämlich absolut nutzlos und könnte nicht getestet werden.

    /**
     * Öffentliche Methode zum Beenden des Threads.
     */
    public void stopMyThreads()
    {
        this.keepRunning=false;
        //
    }

    /**
     * Methode zum Starten des Threads
     */
    public void run()
    {
        while (keepRunning)
        {
            checkUpdates();
            try
            {
                sleep((long) refreshTime*1000);
            }
            catch (InterruptedException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     Diese Methode wird dazu dienen die Websocket zu überprüfen. Bisher leer
     */
    private void checkUpdates()
    {
        //Anfang Websocket lese Kram (dies ist zurzeit eine Temp-Struktur)
        if(PUBLIC_TEMP_WEBSOCKET_REPLACMENT_DUMMY.isEmpty())
            return;
        String msg = PUBLIC_TEMP_WEBSOCKET_REPLACMENT_DUMMY.remove(0);
        //Ende Websocket lese Kram

        try
        {
            //Versucht erstmal die Notfallmeldung als Unbekannt zu Interpretiern
            addEmer(msg);
            if(ALLOW_PRINTING_TO_CONSOL)
                System.out.println("Neuer Notfall entdeckt: MSG='" + msg + '\'');
        }
        catch (DuplicatNotAllowedException e)
        {
            //Falls die Nachricht schon bekannt war, dann sollte sie wohl entfernt werden.
            removeEmer(Emergency.hashCode(msg));
            if(ALLOW_PRINTING_TO_CONSOL)
                System.out.println("Notfall wurde entfernt: MSG='" + msg + '\'');
        }
    }

    //---------------------------------------------------------------------------------------------------------
    //-----------------------------------------Add+Remove-Kram-------------------------------------------------
    //---------------------------------------------------------------------------------------------------------

    /**
     * Mit dieser Methode kann ein Notfall später gelöscht werden und wird von checkUpdates aufgerufen werden. Bis auf Weiteres public, damit das System von der main aus gesteuert werden kann.
     * @param msg Die Nachricht die erhalten wurde.
     */
    public void removeEmer(String msg)
    {
        this.removeEmer(Emergency.hashCode(msg));
        //Wird entfernt, wenn kein Zugriff über main mehr gewünscht sein sollte
    }

    /**
     * Mit dieser Methode kann ein Notfall später erstellt werden und wird von checkUpdates aufgerufen werden. Bis auf Weiteres public, damit das System von der main aus gesteuert werden kann.
     * @param msg Die Nachricht die erhalten wurde.
     */
    public void addEmer(String msg)
    {
        this.addNewEmer(new Emergency(msg,true));
        //Wird entfernt, wenn kein Zugriff über main mehr gewünscht sein sollte
    }

    /**
     * Exception falls ein bereits bekanntes Objekt hinzugefügt werden soll.
     * Fall dies in CheckUpdate passiert sollte die Nachricht entfernt werden.
     * Sonst ist es ein Laufzeitfehler!
     */
    private class DuplicatNotAllowedException extends RuntimeException
    {
        public DuplicatNotAllowedException(String s)
        {
            super(s);
        }
    }
}
