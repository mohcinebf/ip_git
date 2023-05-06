package main;

import DataClasses.Notification;

import java.util.ArrayList;

/**
 * Allgemeiner Handlerklasse für das Notificationsystem
 */
public class NotificationHandler extends Handler
{
    private ArrayList<Notification> notis; // Hier werden alle zurzeit aktiven Notis gespeichert.
    private static final double refreshTime = 0.3; // Angabe in Sekunden sollte später durch eine config gesetzt werden
    private static final int MAX_QUEUE_SIZE = 32; // sollte später durch eine config gesetzt werden

    /**
     * Konstruktor für den NotificationHandler. Wird von der Main oder Core aufgerufen, um das NotificationSystem zu starten.
     */
    public NotificationHandler()
    {
        super(Notification.class.getSimpleName(), MAX_QUEUE_SIZE);
        ini();
    }

    /**
     * Initialisierungsfunktion
     */
    private void ini()
    {
        notis = new ArrayList<>();
    }

    /**
     * Methode die den Head der Queue pop und den Cast übernimmt
     * @return null fall die Queue leer ist und sonst den Head von Typ Notification
     */
    public Notification popNoti()
    {
        return (Notification) pop();
        //
    }

    /**
     * Methode die nur durch den Observer genutzt werden sollte.
     * Iteriert über alle Elemente der ArrayList und erniedrigt ihre Lebenszeit.
     * Falls 0 erreicht oder unterschritten wird, wird das Element auf inactiv gesetzt und aus Notis entfernt und der queue zugewiesen.
     * Sobald der Core das Objekt übernimmt, ist es für das Notification-System nicht mehr existent.
     * @param t Das Zeitdekrement für die Notis
     */
    private void timeUpdate(double t)
    {
        int i = 0;
        while(i < notis.size())
        {
            Notification noti = notis.get(i);
            //System.out.print("Noti:" + noti.hashValue+"\tWas:" + noti.lifetime.toString());
            if(noti.reduceMyLifetime(t))
            {
                //System.out.println("Noti wird entfernt...");
                noti.activ = false;
                queue.add(noti);
                notis.remove(i);
                return; // der index darf hier nicht verschoben werden - daher auch eine while Schleife!
            }
            //System.out.println("\tNow:" + noti.lifetime.toString());
            i++;
        }
    }

    /**
     * Sucht ein Noti-Objekt aus der Notis Liste
     * @param hash Der hashwert der noti nach der gesucht werden soll
     * @return Null falls nicht existent sonst das gefundene Objekt
     */
    private Notification findNoti(int hash)
    {
        for(int i = 0; i != notis.size(); i++)
            if(notis.get(i).hashValue == hash)
                return  notis.get(i);
        return null;
    }

    /**
     * Methode zum Finden einer Position zu einer Noti
     * @param noti Die Noti von der ausgehend gesucht wird.
     * @return Die gefundene Position oder null
     */
    private Integer findNotiPos(Notification noti)
    {
        for(int i = 0; i != notis.size(); i++)
            if(notis.get(i).equals(noti))
                return  i;
        return null;
    }

    /**
     * Methode zum Hinzufügen einer Notification in der List und überführung in die Queue als aktive Noti
     * @param newNoti Die neue Meldung
     * @throws RuntimeException Falls die neue Notification inaktiv ist oder bereits in der Liste existiert.
     */
    private void addNewNoti(Notification newNoti)
    {
        if(!newNoti.activ)
            throw new RuntimeException("Error: An inactiv Notification cannot be added a new! Maybe you wanted to use 'deleteNoti'?");
        if(findNotiPos(newNoti) != null)
            throw new RuntimeException("Error: This Notification already exists in this list. Duplicates are not allowed!");
        notis.add(newNoti);
        queue.add(newNoti);
    }

    /**
     * Methode zum Löschen einer Notification in der List und überführung in die Queue als inaktive Noti
     * @param hash Der hash an dem die gesuchte Notification identifiziert wird
     * @throws RuntimeException Falls die Notification nicht gefunden werden konnte
     */
    private void removeNoti(int hash)
    {
        Notification noti = findNoti(hash);
        if(noti == null)
            throw new RuntimeException("Error: This Notification is not known to the system (anymore)!");
        notis.remove(noti);
        noti.activ = false;
        queue.add(noti);
    }

    /**
     * Öffentliche Methode zum Beenden des Observers.
     */
    public void stopMyThreads()
    {
        this.keepRunning=false;
        //
    }

    //-----------------------------------------------------------------------------------------
    //Ehemaliger NotificationObserver

    public boolean keepRunning = true;
    private long myTime;

    /**
     * Methode zum Starten des Threads
     */
    public void run()
    {
        while (keepRunning)
        {
            checkUpdates();
            checkLifetimes();
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
        //Websocket kram
    }

    /**
     * Methode um die Lebenszeit von Objekten anzupassen.
     */
    private void checkLifetimes()
    {
        long now = System.currentTimeMillis();
        long diff = now - myTime;
        double d = (double)diff / 1000.0;
        this.timeUpdate(d);
        myTime = now;
    }

    /**
     * Mit dieser Methode kann eine Nachricht später gelöscht werden und wird von checkUpdates aufgerufen werden. Bis auf Weiteres public, damit das System von der main aus gesteuert werden kann.
     * @param msg Die Nachricht die erhalten wurde.
     */
    public void removeNotiOBS(String msg)
    {
        this.removeNoti(Notification.hashCode(msg));
        //Wird entfernt, wenn kein Zugriff über main mehr gewünscht sein sollte
    }

    /**
     * Mit dieser Methode kann eine Nachricht später erstellt werden und wird von checkUpdates aufgerufen werden. Bis auf Weiteres public, damit das System von der main aus gesteuert werden kann.
     * @param msg Die Nachricht die erhalten wurde.
     * @param lifetime Die Lebenszeit der Nachricht
     */
    public void addNoti(String msg, Double lifetime)
    {
        this.addNewNoti(new Notification(msg,true,lifetime));
        //Wird entfernt, wenn kein Zugriff über main mehr gewünscht sein sollte
    }

    /**
     * Mit dieser Methode kann eine Nachricht mit ewiger Lebensdauer später erstellt werden und wird von checkUpdates aufgerufen werden. Bis auf Weiteres public, damit das System von der main aus gesteuert werden kann.
     * @param msg Die Nachricht die erhalten wurde.
     */
    public void addNoti(String msg)
    {
        this.addNewNoti(new Notification(msg,true,null));
        //Wird entfernt, wenn kein Zugriff über main mehr gewünscht sein sollte
    }


}
