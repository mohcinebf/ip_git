package DataClasses;

import java.util.Objects;

/**
 * Die Notification ist die Klasse aus deren Objekte die Hinweise verwaltet werden und in der Queue des NotificationHanlders liegt.
 * @author Robin
 */
public class Notification
{
    public String msg;
    public int hashValue;
    public boolean activ;
    public Double lifetime; //Wert von null heißt unbegrenzt

    /**
     * Konstruktor für eine Notifikation. Der hashValue wird automatisch aus der msg bestimmt.
     * @param msg Der Text der Nachricht, welche später im Bus angezeigt wird
     * @param activ Activ ist ein bool, das vom Core verarbeitet wird. Is activ auf true gesetzt soll der Core ein type = add an das Frontend schicken, sonst ein Remove.
     * @param lifetime Die Startlebenszeit in Sekunden dieser Nachricht. Der Observer wird diese Zeit immer wieder reduzieren. Wenn sie 0 oder kleiner wird, wird die Nachricht aus dem System entfernt. Falls der Wert 'null' ist, lebt die Noti ewig und muss durch die Zentrale gesondert entfernt werden.
     */
    public  Notification(String msg, boolean activ, Double lifetime)
    {
        this.msg = msg;
        this.activ = activ;
        this.lifetime = lifetime;
        this.hashValue = hashCode();
    }

    /**
     * Vergleichsmethode zwischen zwei Notifications - dies geschieht immer über den hashvalue
     * @param o Das andere Objekt mit dem Verglichen wird
     * @return Bool der sagt ob die Objekte gleich sin.
     */
    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Notification that = (Notification) o;
        return hashValue == that.hashValue;
    }

    /**
     * Methode die den hashValue einer Notification aus ihrer msg bestimmt.
     * @return
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(msg);
    }

    /**
     * Methode um den Hash ohne Objekt, sondern nur mit der Nachricht zu bestimmen. Dies wird später beim Löschen einer Noti benötigt
     * @param msg Die Nachricht
     * @return Der hash den ein Noti mit dieser Nachricht hätte.
     */
    public static int hashCode(String msg)
    {
        return Objects.hash(msg);
    }

    /**
     * Methode um einfacher die Lebenszeit einer Noti zu reduzieren. Funktioniert auch mit ewiger Lebenszeit
     * @param reduceValue Um welchen Wert die Lebenszeit reduziert werden soll.
     * @return True falls der Wert 0 oder <0 erreicht wurde.
     */
    public boolean reduceMyLifetime(double reduceValue)
    {
        if(lifetime == null)
            return  false;
        lifetime = lifetime - reduceValue;
        if(lifetime <= 0)
            return true;
        return false;
    }

    private static final boolean PRINT_MSG_IN_DEBUGSTRING_FUNCTION = true;

    /**
     * Methode die aus dem Objekt einen String für's lesbare Debugging (mit Worten) macht.
     * @return der String für log oder console
     */
    public String debugString()
    {
        String b;
        if(activ)
            b = "1";
        else
            b = "0";
        String l;
        if(lifetime == null)
            l = "ewig";
        else
            l = Double.toString(lifetime) + "s";
        String m;
        if(PRINT_MSG_IN_DEBUGSTRING_FUNCTION)
            m = "\n--->MSG: \"" + msg + '"';
        else
            m = "";
        return new String ("Meldung mit dem Hash \"" + Integer.toString(hashValue) + "\" auf dem Status '" + b + "' und der Lebenszeit \"" + l + "\"." + m);
    }
}
