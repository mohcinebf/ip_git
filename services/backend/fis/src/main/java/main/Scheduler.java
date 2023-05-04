package main;
import java.util.ArrayList;
import java.util.Map;

/*
    Es gibt ein Programm genau ein Objekt dieser Klasse.
 */
public class Scheduler
{
    //private ArrayList<Information> queue = new ArrayList<Information>();
    private ArrayList<DynamicScheduler> secondSchedulers = new ArrayList<>(); // Die DSs
    private StaticScheduler staticscheduler = new StaticScheduler();          // Der SS

    /*
        Öffentliche Methode um eine neue Information hinzuzufügen. Wird das gleiche Typ nochmal hinzugefügt, wird geprüft ob der Typ singulär ist. Falls ja wird eine Exception geworfen und sonst wird die Info an einen DS weiter geleitet. Im letzteren fall wird die Freq von der neuen Info nicht genutzt.
        throws RuntimeException - falls ein Duplikat eingefügt werden soll.
        info - Die neue nformation
     */
    public void addNewInformation(Information info)
    {
        try
        {
            staticscheduler.addNewMainCategory(info);
        }
        catch (StaticScheduler.MainCategoryAlreadyExists e)
        {
            if(info.isSingle)
                throw e;
        }

        if(!info.isSingle) // Hinzufügen zu einem DynmischenScheduler
        {
            /* // in eigene Funktion ausgelagert
            int i = 0;
            while(i != secondSchedulers.size())
            {
                if(secondSchedulers.get(i).forTyp.equals(info.type))
                {
                    secondSchedulers.get(i).addNewInfo(info);
                    return;
                }
            }
            */

            DynamicScheduler foundSecSched = getDynamicSchedulerForThisType(info.type);
            if(foundSecSched == null)
                secondSchedulers.add(new DynamicScheduler(info));
            else
                foundSecSched.addNewInfo(info);
        }
    }

    /*
        Öffentliche Methode um eine zufällige Information zu erhalten.
        throws RuntimeExeption - Falls keine Information vorliegen.
        return - Zufälliges Informations Objekt welches zuvor eingefügt wurde. Es wird eine Referenz und keine Kopie zurückgegegeben
     */
    public Information getRandomInformation()
    {
        Information first = staticscheduler.popHead();
        if(first.isSingle)
            return first;

        DynamicScheduler ds = getDynamicSchedulerForThisType(first.type);
        if(ds == null)
            throw new RuntimeException("CRITICAL ERROR: Cannot find Dynmaic Scheduler fpr the following Type: " + first.type);

        return ds.getRandomInfo();
    }

    /*
    Funktion um boostValue oder Activ eines Subobjekts zu verändern, da diese durch äußeres erreicht werden müssen.
    Throws: RuntimeException - Falls Information nicht gefunden werden konnte
    info - zu welcher Information Attribute upgedated werden sollen
    newBoostValue - auf welchen Wert der boostvalue gesetzt werden soll
    newActivValue - auf welchen Wert der activ gesetzt werden soll
     */
    public void updateValuesForInfo(Information info, int newBoostValue, int newActivValue)
    {
        DynamicScheduler ds = getDynamicSchedulerForThisType(info.type);
        if(ds==null)
            throw new RuntimeException("There is no");
        ds.updateSubobValuesByExtern(info,newBoostValue,newActivValue);
    }

    /*
        Funktion um den DS zu einem Type zu finden
        return Den gefunden DS oder null
     */
    private DynamicScheduler getDynamicSchedulerForThisType(String type)
    {
        for(int i = 0; i != secondSchedulers.size();i++)
            if(secondSchedulers.get(i).forTyp.equals(type))
                return secondSchedulers.get(i);
        return  null;
    }
}
