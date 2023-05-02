package main;
/*
    Informationstypus-Klasse für die Anzeige der Fahrstrecke der eigenen Linie
    z.B.
    ^   Rolandstraße
    |   Alter Tivoli
    |   Sportpark Sörs
    |   Eulershof

    Is singulär mit relativ hoher Frequenz. Die Anzeigeinformationen können aufgrund ihres dynmaischen Charakters hier nur schlecht in der Klasse integriert werden.
 */

public class UpcommingHalts extends Information
{
    public UpcommingHalts(int freq)
    {
        this.type = "Fahrstrecken_Anzeige";
        this.id = Information.giveNewId();
        this.isSingle = true;
        this.frequency = freq;
    }
}
