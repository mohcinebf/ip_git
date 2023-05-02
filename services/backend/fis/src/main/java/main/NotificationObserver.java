package main;

/**
 * Die zum Notification-System gehörige Threadklasse
 */
public class NotificationObserver extends Thread
{
    public boolean keepRunning;
    private double refreshTime;
    private NotificationHandler handler;
    long myTime;

    public NotificationObserver(NotificationHandler handler, double refreshTime)
    {
        this.handler = handler;
        this.refreshTime = refreshTime;
        keepRunning = true;
        myTime = System.currentTimeMillis();
        //WEBSOCKET KRAM....
    }

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
        handler.timeUpdate(d);
        myTime = now;
    }

    /**
     * Mit dieser Methode kann eine Nachricht später gelöscht werden und wird von checkUpdates aufgerufen werden. Bis auf Weiteres public, damit das System von der main aus gesteuert werden kann.
     * @param msg Die Nachricht die erhalten wurde.
     */
    public void removeNoti(String msg)
    {
        handler.removeNoti(Notification.hashCode(msg));
    }

    /**
     * Mit dieser Methode kann eine Nachricht später erstellt werden und wird von checkUpdates aufgerufen werden. Bis auf Weiteres public, damit das System von der main aus gesteuert werden kann.
     * @param msg Die Nachricht die erhalten wurde.
     * @param lifetime Die Lebenszeit der Nachricht
     */
    public void addNoti(String msg, Double lifetime)
    {
        handler.addNewNoti(new Notification(msg,true,lifetime));
    }

    /**
     * Mit dieser Methode kann eine Nachricht mit ewiger Lebensdauer später erstellt werden und wird von checkUpdates aufgerufen werden. Bis auf Weiteres public, damit das System von der main aus gesteuert werden kann.
     * @param msg Die Nachricht die erhalten wurde.
     */
    public void addNoti(String msg)
    {
        handler.addNewNoti(new Notification(msg,true,null));
    }
}
