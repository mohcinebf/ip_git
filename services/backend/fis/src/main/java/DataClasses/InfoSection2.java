package DataClasses;

/**
 * Oberklasse für alle Klassen, die später auf Bereich 2 (also dem "großen Hauptbereich") angezeigt werden sollen.
 *
 * @author Fabian Ferrari
 */
public abstract class InfoSection2 {
    /**
     * Eindeutige ID der Information. Wird automatisch gesetzt.
     */
    public final int id;
    /**
     * Die Überschrift der Information. Dieser wird später im Frontend angezeigt.
     */
    public String header;
    /**
     * Die Nachricht der Information. Dieser wird später im Frontend angezeigt.
     */
    public String msg;

    private static int nextId = 0;

    /**
     * Konstruktor für eine InfoSection2. Die ID wird automatisch gesetzt.
     */
    public InfoSection2() {
        this.id = nextId++;
    }
}
