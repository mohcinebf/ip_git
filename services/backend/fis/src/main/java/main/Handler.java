package main;

import java.util.LinkedList;

/*
    Basisklasse für alle Handler. Beinhaltet die Queue sowie die Zugriffsfunktion auf diese für den Core.
 */
public abstract class Handler extends Thread
{
    protected LinkedList<Object> queue; // Die eigentliche Queue - bei unserer Anwendung ist eine LinkedList effizienter - füülen mit queue.add
    private int maxQueueSize; //Wie lang darf die Queue werden? Private, da sich nach dem Erstellen fest sein sollte.
    public String classInQueue; //String des Klassennamens, der in die Queue gepackt wird.

    /**
        Öffentliche Methode über den der Core die Queues leeren darf. Gibt Null zurück falls die Queue leer ist.
        @return Head der Queue oder null
     */
    public Object pop()
    {
        return queue.pollFirst();
    }



    /**
        Konstruktor für einen Handler
        @param classInQueue Hier soll der Classenname der Objekte in der Queue stehen - z.B. Information. Dies macht man am besten durch aufruf der "<CLASS_YOU_PUT_IN_QUEUE>.class.getSimpleName()"-Funktion
        @param maxQueueSize Die maximale Größe der Queue dieses Handlers
     */
    public Handler(String classInQueue, int maxQueueSize) // super(<CLASS_YOU_PUT_IN_QUEUE>.class.getSimpleName(), MAX_QUEUE_SIZE); für die erbende Klasse
    {
        this.classInQueue = classInQueue;
        this.maxQueueSize = maxQueueSize;
        queue = new LinkedList<>();
    }



    /**
        Funktion um die Menge an Daten in der Queue auch außerhalb sichtbar zu machen.
        @return Anzahl der Elemente in der Queue.
     */
    public int getQueueSize()
    {
        return  queue.size();
    }

    /**
        Getter für die MaxQueueSize
        @return MaxQueueSize Wert des Attributes "MaxQueueSize"
     */
    public int getMaxQueueSize()
    {
        return maxQueueSize;
    }
}
