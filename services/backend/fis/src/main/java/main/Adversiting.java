package main;
/*
    Abstrakte Oberklasse für alle Werbungssubtypen
 */
public abstract class Adversiting extends Information
{
    int customerID; // Damit man später der Zentrale sagen kann, wessen Werbung gespielt wurde. Falls später doch unnötig einfach entfernen.

    public String adType;
    @Override
    public String getType()//Der Hauptgrund für diesen Getter im Vergleich zu dem public type attribut - type muss für jede Werbung gleich bleiben, da der Scheduler sonst statt einem dyn.
                           //Werbungsscheduler für jeden Subtyp einen eigenen machen würde, der Scheduler aber nicht zwischen Informationstypen mitausnahme des isSingle unterscheiden soll, um große switch case blöcke dort zu vermeiden.
                           //Der erste Prototype hat zwar nur eine Werbungsabart, jedoch wird sehr sicher mindestens die Bildwerbung noch nach kommen entsprechend ist sinnvoll diese abstrakte zwischen Stufte schonmal zu machen.
    {
        return type + ':' + adType;
    }
}
