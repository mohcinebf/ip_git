package main;

import java.util.Objects;

public class Emergency
{
    public String msg;
    public int hashValue;
    public boolean activ;
    private static final boolean PRINT_MSG_IN_DEBUGSTRING_FUNCTION = true;

    /**
     * Konstruktor für einen Notfall. Der hashValue wird automatisch aus der msg bestimmt.
     * @param msg Der Text der Nachricht, welche später im Bus angezeigt wird
     * @param activ Activ ist ein bool, das vom Core verarbeitet wird. Is activ auf true gesetzt soll der Core ein type = add an das Frontend schicken, sonst ein Remove.
     */
    public  Emergency(String msg, boolean activ)
    {
        this.msg = msg;
        this.activ = activ;
        this.hashValue = hashCode();
    }

    /**
     * Vergleichsmethode zwischen zwei Notfällen - dies geschieht immer über den hashvalue
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
        Emergency that = (Emergency) o;
        return hashValue == that.hashValue;
    }

    /**
     * Methode die den hashValue einer Emergency aus ihrer msg bestimmt.
     * @return Hashcode aus dem msg
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(msg);
        //
    }

    /**
     * Methode um den Hash ohne Objekt, sondern nur mit der Nachricht zu bestimmen. Dies wird später beim Löschen einer Emer benötigt werden, sobald das WebSocketsystem steht.
     * @param msg Die Nachricht
     * @return Der hash den ein Emer mit dieser Nachricht hätte.
     */
    public static int hashCode(String msg)
    {
        return Objects.hash(msg);
        //
    }

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
        String m;
        if(PRINT_MSG_IN_DEBUGSTRING_FUNCTION)
            m = "\n--->MSG: \"" + msg + '"';
        else
            m = "";
        return new String ("Notfall mit dem Hash \"" + Integer.toString(hashValue) + "\" auf dem Status '" + b  + m);
    }
}
