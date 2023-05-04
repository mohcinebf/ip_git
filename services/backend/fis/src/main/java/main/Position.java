package main;
/**
 * @author David Bockstegers
 * Die Klasse Position gibt an ob sich der Bus gerade einer Haltestelle nähert oder diese verlässt und wie die Haltestelle heißt.
 */
public class Position {
    private String haltestelle;
    private boolean Haltnaehern;

    /**
     * Konstruktor der Position
     * @param haltestelle Name der Haltestelle
     * @param Haltnaehern nähert sich einer Haltestelle(True)/ verlässt Haltestelle(False)
     */
    public Position(String haltestelle, boolean Haltnaehern){
        this.haltestelle = haltestelle;
        this.Haltnaehern = Haltnaehern;
    }

    /**
     * Gibt Auskunft darüber ob sich einer Haltestelle genähert wird oder ob sie verlassen wird.
     * @return True, wenn man sich einer Haltestelle nähert. False, wenn man diese verlässt.
     */
    public boolean getHaltnaehern(){ return this.Haltnaehern; }

    /**
     * Gibt Auskunft darüber wie die aktuelle Haltestelle heißt.
     * @return Name der Haltestelle
     */
    public String getHaltestelle() { return this.haltestelle;}

}
