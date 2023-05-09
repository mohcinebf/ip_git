package DataClasses;
/*
    Informationstypus-Klasse für die Anzeige der Fahrstrecke der eigenen Linie
    z.B.
    ^   Rolandstraße
    |   Alter Tivoli
    |   Sportpark Sörs
    |   Eulershof

    Is singulär mit relativ hoher Frequenz. Die Anzeigeinformationen können aufgrund ihres dynmaischen Charakters hier nur schlecht in der Klasse integriert werden.
 */

import DataClasses.Information;

public class UpcommingHalts extends Information
{
    public UpcommingHalts(int freq, int duration)
    {
        super();
        this.type = "Fahrstrecken_Anzeige";
        this.isSingle = true;
        this.frequency = freq;
        this.duration = duration;
    }
}
