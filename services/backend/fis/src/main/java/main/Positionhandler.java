package main;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author David Bockstegers
 * Die Klasse kümmert sich darum die eintreffenden Daten über die Position zu empfangen und dem Core in einer Queue zur verfügung zu stellen
 */
public class Positionhandler extends Handler {
    private static final int MAX_QUEUE_SIZE = 10;
    private boolean keepRunning = true;
    //Buffer für die ankommenden Events der Sensorik(damit keins verloren geht falls diese nicht direkt in Queue gelegt werden)
    private LinkedList<Object> queueBuffer;

    /**
     * Konstruktor der Klasse Positionhandler
     */
    public Positionhandler(){
        super(Position.class.getSimpleName(), MAX_QUEUE_SIZE);
        this.queueBuffer = new LinkedList<>();
    }

    /**
     * Fügt dem Buffer eine neue Position zu. Hier werden die Daten des Sensors dem Handler übergeben.
     * @param pos Position
     */
    public void addNewPosEvent(Position pos){
        this.queueBuffer.add(pos);
    }

    /**
     * Entfernt die nächste Position aus der queueBuffer.
     * @return nächste Position
     */
    private Position removePosBuffer(){
        return (Position) this.queueBuffer.pollFirst();
    }

    /**
     * Entnimmt der Queue die Position. Diese Funktion ermöglicht dem Core auf das nächste Event zuzugreifen.
     * @return Position
     */
    public Position removeQueue(){
        if(this.getQueueSize() <= 0){
            throw new RuntimeException("Queue ist bereits leer");
        }else {
            return (Position) pop();
        }
    }
    /**
     * Fügt neue Position der Queue hinzu. Aus dieser Queue entnimmt der Core das Event.
     * @param pos neue Position
     */
    private void addQueue(Position pos) {
        if(this.getQueueSize() >= MAX_QUEUE_SIZE){
            throw new RuntimeException("Queue ist voll");
        } else {
            queue.add(pos);
        }
    }

    //----------------------
    //THREAD
    /**
     * Beenden des Threads
     */
    public void stopThread(){
        this.keepRunning = false;
    }

    /**
     * Startet den Thread und fragt alle 3 sek nach updates
     */
    public void run(){
        while(keepRunning){
            checkUpdates();
            try {
                sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Checkt ob neue Sensordaten vorhanden sind und empfängt diese und übergibt sie an Queue für den Core.
     */
    private void checkUpdates() {
        //Sensor Daten hier empfangen(falls vorhanden) und Daten zur Bufferqueue hinzufügen
        // newData();

        //Data aus Buffer lesen und in Queue für den Handler übergeben
        this.addQueue(removePosBuffer());
        System.out.println("Sensor hat Daten geschickt");

        //entleeren übernimmt eigt Core
        /*Position tmp = this.removeQueue();
        if(tmp.getHaltnaehern()){
            System.out.println("Bus nähert sich Haltestelle: " + tmp.getHaltestelle());
        }else{
            System.out.println("Bus verlässt Haltestelle: " + tmp.getHaltestelle());
        }

        System.out.println("Queue von 'Core' geleert");
        System.out.println("--------------------------");*/
    }

    /**
     * Ist nur dafür da um random Testdaten zu ziehen
     */
    private void newData(){
        //Take random new Stop
        int randomNum = ThreadLocalRandom.current().nextInt(0, 3 + 1);
        if (randomNum == 0){
            Position pos1 = new Position("eBrunnen",true);
            Position pos1_1 = new Position("eBrunnen",false);
            addNewPosEvent(pos1);
            addNewPosEvent(pos1_1);
        }else if(randomNum == 1) {
            Position pos2 = new Position("Drischer",true);
            Position pos2_1 = new Position("Drischer",false);
            addNewPosEvent(pos2);
            addNewPosEvent(pos2_1);
        }else if(randomNum == 2) {
            Position pos3 = new Position("Ponte",true);
            Position pos3_1 = new Position("Ponte",false);
            addNewPosEvent(pos3);
            addNewPosEvent(pos3_1);
        }else if(randomNum == 3) {
            Position pos4 = new Position("Ponte2",true);
            Position pos4_1 = new Position("Ponte2",false);
            addNewPosEvent(pos4);
            addNewPosEvent(pos4_1);
        }
    }
}
