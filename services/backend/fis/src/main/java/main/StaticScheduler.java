package main;
import java.util.ArrayList;
import java.util.Collections;

/*
    Klasse, welche dazu dient, die Hauptinformationsklasse zu bestimmen. Von jeder Informationsklasse wird genau ein Objekt in mehrfacher Kopie (entsprechend der Freq) in eine ArrayList gelegt und durchlaufen. Nach einmaligen Durchlaufen wird das Array neu gemischt (daher keine echte Queue).
    Eine Hauptinformationsklasse ist gleich dem type einer Information, also z.B. Fahrstrecke, Dyn.-Data, oder Werbung (aber nicht Text-Werbung, Videowerbung! - siehe Information.type)
 */
public class StaticScheduler
{
    /*
        Wird geschmissen, falls man versucht dem Scheduler eine nicht singuläre Information zugeben, welcher dieser bereits kennt.
     */
    public static class MainCategoryAlreadyExists extends RuntimeException
    {
        MainCategoryAlreadyExists(String s)
        {
            super(s);
        }
    }
    //Attribute
    private ArrayList<Information> queue = new ArrayList<>();   //queue-like array - es wird nicht wirklich was entfernt
    private ArrayList<String> knownTypes = new ArrayList<>();   //sammelt welche typen bekannt sind um Duplikate zu verhindern (die Verantwortung für sauberes arbeiten liegt hier!).
    int pos = 0; // Wo in der ArrayList
    int myHeighestFrequency = 0; // Höchste Frquenz aller Typen
    Information myLastResult = null;

    //Methoden

    /*
        Methode um von außen eine neue Information zum Scheduler hinzuzufügen.
        throws MainCategoryAlreadyExists - Dieses Objekt liegt bereits in der Queue
        info - Information die neu Hinzugefügt werden soll.
     */
    public void addNewMainCategory(Information info)
    {
        if (knownTypes.contains(info.type))
            throw new MainCategoryAlreadyExists("This Category was already know to the Scheduler and cannot be added anew. T");

        knownTypes.add(info.type);
        for(int i = 0; i != info.frequency; i++)
            queue.add(info);
        if(info.frequency > myHeighestFrequency)
            myHeighestFrequency = info.frequency;
        shuffleQueue();
    }

    /*
        Methode um das Array neu zu mischen. Wird ausgeführt wenn pos die Arraygrenze erreicht oder wenn sich das Array generell verändern (neue Info).
     */
    private void shuffleQueue()
    {
        Collections.shuffle(queue);
        pos = 0;
        findAndKillArtifacts();
    }

    /*
        Öffentliche Methode um das vorderste Element der Queue zu erhalten, wobei dieses auch entfernt wird.
        throws RuntimeException - queue ist leer
        Return: ID Type einer Hauptkategorie - z.B. Werbung
     */
    public Information popHead()
    {
        if(queue.isEmpty())
            throw new RuntimeException("Fehler: Keine Information sind bekannt.");
        Information i = queue.get(pos);
        pos++;
        if(pos == queue.size())
            shuffleQueue();
        myLastResult = i;
        return i;
    }

    /*
        Methode um nach dem shuffel Artefakte zu töten. Artefakte sind Unschönheiten. Einziges bisher behandeltes Artefakt wäre eine direkte Wiederholung derselben Information im Array. Dies ist nur möglich falls myHeighestFrequency < queue.size()/2.
     */
    private void findAndKillArtifacts()
    {
        if (myHeighestFrequency >= queue.size()/2) // Falls eine Kategorie so häufig vorkommt, dass sie mehr als die Hälfte eines Queue-Durchlaufes ausmacht, wird es unmöglich sein, dass die Kategorie nicht mit sich selbst benachbart ist und entsprechend muss man dann auch nichts versuchen
            return;
        int i = 0;
        boolean lastWasDouble = false;
        while (i != queue.size()-1)
        {
            int j = i+1;
            if(queue.get(i).type.equals(queue.get(j).type))
                lastWasDouble = true;
            else
            {
                if(lastWasDouble)
                {
                    Information temp = queue.get(i);
                    queue.set(i,queue.get(j));
                    queue.set(j,temp);
                    //noinspection ReassignedVariable
                    i = 0;
                    lastWasDouble = false;
                }
            }
        }
    }
}
