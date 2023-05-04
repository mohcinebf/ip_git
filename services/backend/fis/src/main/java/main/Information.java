package main;
/*
    Hauptklasse für alle Arten von Informationen. Informationen sind alles, was nach dem Anforderungsblatt mit geringster (3) Priorität angezeigt werden kann.
    Diese Oberklasse ist bewusst abstrakt, sodass man z.B. im Scheduler alle Information gleich behandeln kann.
    Jede Information hat ein type Attribut, welche immer auf die erste Stufe in der Hierarchie der Abstraktion der Informationsklassen zeigt (Werbung, Fahrstrecke, Dyn.-Daten, ...). Man kann darüber oder über die direkte Klassenabfrage von Java dann zwischen den Klassen einfach
    unterscheiden, wenn man schlicht eine Information hat.
    Für jeden Eintrag (z.B. ein Werbungsobjekt - also z.B. ein Werbungstext) gibt es genau ein Information-Objekt. Es werden, während das Programm läuft, niemals Informationen gelöscht. Jede Information ist einmal (oder fall nicht singulär) zweimal im Scheduler als Referenz hinterlegt und werden
    durch den Zufall für die weitere Verarbeitung mittels Datenübertragung an das Frontend an den Core übermittelt.

    Die Logik um aus einer Information dann die .json Struktur zu für die Überreichung an das Frontend zu machen könnte man entweder im Core machen, welcher
    Information-Objekte zugeschickt bekommt, oder man macht hier eine abstrakte Methode und die jeweils unterste Klasse überschreibt diese Methode entsprechend so, wie ihre Daten überschrieben werden müssen.
    Eleganter und wahrscheinlich effizienter wäre letzteres, jedoch gibt es gerade bei den dynamischen Informationen (UpcommingHalts, Live-Stream, Dynmische Daten) die frage, wie man mit einer Funktion aus dieser Klasse an die entsprechenden Daten kommen sollt, die wahrscheinlich nur der Core kennt.
 */
public abstract class Information
{
    public int id; // unique und const nach dem setzten


    // Folgende Attribute sind für den Scheduler
    public boolean isSingle; //Gbit an, ob es mehrere Typen dieser Klasse geben darf - Nur für den Scheduler relevant. Wird immer durch die niedrigste Klasse in dieser Abstraktenklassenstruktur durch den Konstruktor gesetzt, anschließend niemehr verändert und ist für die entsprechende unterste Klasse eindeutig!
    public String type; //Dient für den Scheduler als Ausgabe und eine Type ID. Darf nach dem Setzen durch den Konstruktor nicht mehr verändert werden.
    public int frequency;// Gibt die Häufigkeit im statischen Scheduler an. Eine Frequenz von 3 entspricht drei Einträgen in einem Array das aus allen Typen besteht. Gibt es also 3 Typen mit Frequenzen von je 2, 5 und 8 hat der Typ mit 2 also eine Wahrscheinlichkeit von 2/(2+5+8) = 13,333%
                         // Es wäre daher sinnvoll den unwichtigsten Anzeigetypus zu bestimmen (warscheinlich Live-Stream oder dynmische Daten) und auf eins zu setzen und die Freq aller anderen als relative Häufigkeit dieses Typus gegen diese Klasse zu verstehen
    public Double startingBaseValue = null; // Wird nur durch Erbendeklasse gesetzt wenn nicht Single. Bei nicht Single irrelevant bzw. nicht definiert
    public Double startingActiv = null;     // Wird nur durch Erbendeklasse gesetzt wenn nicht Single. Bei nicht Single irrelevant bzw. nicht definiert

    private static int lastUsedID = 0; // Um die ID unique zu machen

    /*
        Funktion um eine eindeutige ID bei der Erstellung zuzuweisen.
        return ID für ein neues Objekt
     */
    protected static int giveNewId()
    {
        int i = lastUsedID;
        lastUsedID++;
        return  i;
    }

    /*
        Funktion die den Typen als String zurück gibt. Bei den Meistenklasse wird das identisch zu dem Attribut sein, aber nicht bei Werbung. Bei Werbung gibt diese Funktion zusätzlich noch den Subtypen an, wäre Type nur 'Werbung' liefern wird.
        return this.type + (:subtype)?
     */
    public String getType() // Dient als Ausgabe für den Scheduler Prototypen.
    {
        return type;
    }

    /*
        Funktion die einen debugString erstellt, der Aussage über dieses Objekt macht.
        return String ID + getType
     */
    public String debugString()
    {
        return "Ich bin \"" + String.valueOf(type) + "\" und vom Typus: \"" + getType() + '"';
    }
}
