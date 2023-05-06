package main;
import DataClasses.Information;

import java.util.ArrayList;
import  java.util.Random;

/**
    Ein dynamischer Scheduler sucht für einen bestimmten Typ (z.B. Werbung) einen Bestimmte Subinformation. Der DS wird für jede Information erstellt, die isSingle == false ist.
 */
public class DynamicScheduler
{
    private static final double CHANGE_PERCENT_VALUE = 0.1; //Veränderung des varivalues in Prozent in einem Schritt
    //private static final boolean KNOWN_NEW_INFO_IS_AN_UPDATE = false; //Wird erstmal strengstens verboten und Duplikat werden Exceptions erzeugen
    private static final boolean PRINT_PROBABILITIES_FOR_NEXT_TURN_TO_CONSOLE_FOR_DEBUG_REASONS = false; // ACHTUNG FALLS TRUE WIRD DAS DIE KONSOLE RICHTIG VOLLMÜLLEN! NUR FÜR TESTZWECKE MIT DEM SCHEDULER

    /**
        Subklasse um die Mathematik für die Wahrscheinlichkeit einer Information besser zu verwalten.
     */
    private class Subobject
    {
        //Konstanten
        private static final double STARTVALUE_FOR_VARIVALUE = 1.0;
        private static final double STARTVALUE_FOR_BOOSTVALUE = 1.0;
        private static final double ROUNDING_ACCURACY_FOR_VARIINT = 10000; // In zehner Schritten - also 10^x

        //Attribute
        public double varivalue;    // Variable Wahrscheinlichkeit - verändert sich bei jeder Ziehung
        public double basevalue;    // Basiswahrscheinlichkeit - fest nach der Setzung
        public double boostvalue;   // Wahrscheinlichkeit durch äußere EInflüsse (z.B. Entfernung zu Ort X)
        public double activ;        // Wird zurzeit verwendet? Quasie ein bool - wird jedoch als double gespeichert, damit man es verrechnen kann. Falls 0 wird die Wahrscheinlichkeit mit 0 multipliziert; sonst mit 1. Direkte Verrechnung ist schneller als eine If Verzweigung und 0-Summen stören den Algorythmus nicht.
        public int variint; //Gesamtwahrscheinlichkeits Produkt als Integer. Java kennt kein unsigned; daher mit Vorzeichen
        public int id; //Damit wird später das konkrete Element identifiziert - könnte z.B. der Pfad auf ein bestimmte Werbung sein.

        /**
            Test Konstruktor - nicht mehr aktiv in Benutzung
         */
        public Subobject(int id, double basevalue, double activ) // für Dirtytesting
        {
            this.basevalue = basevalue;             // Basis Chance
            this.activ = activ;                     // Aktiv (eigentlich ein Bool wird aber aus technischen Gründen mit 0.0 oder 1.0 verwaltet, da der Wert einfach verrechnet wird)
            variint = 0;                            // Variable Chance (technisch) - wird im Prozess gesetzt
            varivalue = STARTVALUE_FOR_VARIVALUE;   // Variable Chance (mathematisch)
            boostvalue = STARTVALUE_FOR_BOOSTVALUE; // Einfluss durch Äußeres - z.B. Abstand zu X
            this.id = id;                           // Siehe oben
        }

        /**
         * Konstruktor der aus einer Information ein Subob macht
         * @param info Die Info zu der dieses Subob erstellt werden soll.
         */
        public Subobject(Information info) // Reguläre Nutzung
        {
            this.basevalue = info.startingBaseValue;             // Basis Chance
            this.activ = info.startingActiv;                     // Aktiv (eigentlich ein Bool wird aber aus technischen Gründen mit 0.0 oder 1.0 verwaltet, da der Wert einfach verrechnet wird)
            variint = 0;                            // Variable Chance (technisch) - wird im Prozess gesetzt
            varivalue = STARTVALUE_FOR_VARIVALUE;   // Variable Chance (mathematisch)
            boostvalue = STARTVALUE_FOR_BOOSTVALUE; // Einfluss durch Äußeres - z.B. Abstand zu X
            this.id = info.id;                           // Siehe oben
        }

        /**
            Einfache Fuktion um das variprod zu bestimmen und dieses dann auf einen Integer abzubilden.
         */
        public void calcMyVariint()
        {
            double variprod = basevalue * varivalue * boostvalue * activ;
            variint = (int)Math.round(ROUNDING_ACCURACY_FOR_VARIINT * variprod);
        }

        /**
            Funktion um das double activ in ein bool umzuwandeln.
            @throws RuntimeException Falls Wert weder 1 noch 0.
            @return false falls activ == 0.0 und true falls activ == 1.0
         */
        public boolean isActiv()
        {
            if (activ == 0.0)
                return false;
            else
                if(activ == 1.0)
                    return true;
                else
                    throw new RuntimeException("Illegal Value for Activ found; only 0.0 and 1.0 are allow");
        }
    }

    public ArrayList<Subobject> subobs = new ArrayList<>(); //Sammlung aller subobs
    public ArrayList<Information> myInfos = new ArrayList<>();//Sammlung aller bekannter Informationen. Je Information hier gibt es ein Subobjekt
    private double changeValue = 0.0; //Wird evt als Attribut entfernt und als lok variabel in updateVarivalues verlegt
    public Subobject lastObj = null;//Letztes Obket
    private Random random = new Random();// Die aktuelle Zufallsfolge
    public String forTyp;// Für welchen Informationtypen dieser DS arbeitet

    /**
        Kann von den Testern später benutzt werden, um Unittests zu erstellen
     */
    public void setSeed(long newSeed)
    {
        random.setSeed(newSeed);
    }

    /**
        Hauptfuktion die aus allen Subobs ein zufälliges auswählt.
        @return Ein zufälliges Subobjekt aus allen für diesen DS existierend Subobjekten
     */
    private Subobject getRandomSubobj()
    {
        //Schritt I - berechne die upperBorder
        int upperBorder = 0;
        for(int i = 0; i != subobs.size(); i++)
        {
            Subobject subob = subobs.get(i);
            subob.calcMyVariint();
            upperBorder = upperBorder + subob.variint;
        }
        //Schritt II - Abbildung auf ein Integerarreal
        int randi = random.nextInt(upperBorder);
        int at = 0;
        //Schritt III - Ermittelung des Zufälligen Objekts
        while(true) // abbruchbedingung muss nach der Rechnung geprüft werden
        {
            randi = randi - subobs.get(at).variint;
            if(randi < 0 && subobs.get(at).isActiv()) // Falls die Zufallszahl '0' ist und das erste Element ist inaktiv
                break;
            at++;
        }

        return subobs.get(at);
    }

    /**
        Funktion um die varivalues aller subobs nach einer erfolgreichen Ziehung anzupassen.
        @param toReduce Das Objekt, welches gezogen wurde, und entsprechen in seiner Wahrscheinlichkeit verringert wird, während alle anderen eine entsprechende gewichtete Erhöhung erhalten.
     */
    private void updateVarivalues(Subobject toReduce)
    {
        //changeValue = toReduce.varivalue - toReduce.varivalue * CHANGE_PERCENT_VALUE;
        changeValue = toReduce.varivalue * CHANGE_PERCENT_VALUE;
        double incValue = changeValue / ((double)(subobs.size() - 1));
        for(int i = 0; i != subobs.size(); i++)
            if(subobs.get(i) != toReduce)
                subobs.get(i).varivalue += incValue;
            else
                toReduce.varivalue = toReduce.varivalue - changeValue;
        if(PRINT_PROBABILITIES_FOR_NEXT_TURN_TO_CONSOLE_FOR_DEBUG_REASONS)
            printProbabilities();
    }

    /**
     * Methode die überprüft ob die Information schon bekannt ist.
     * @param info Die Information nach der gesucht wird
     * @return true falls die Information bekannt ist, sonst false
     */
    public boolean infoIsKnown(Information info)
    {
        if (findInfoIfKnown(info.id)==null)
            return false;
        else
            return  true;
    }

    /**
        Billige Suchmethode um eine Info von einer ID zu finden: Meistens für die Zuordnung Subob-->Info
        @param infoID ID nach der gesucht wird
        @return Gefunde Indexposition für die Information oder null falls nicht gefunden.
     */
    private Integer findInfoIfKnown(int infoID)
    {
        for (int i = 0; i != myInfos.size(); i++)
            if (myInfos.get(i).id == infoID)
                return i;
        return  null;
    }

    /**
        Billige Suchmethode um eine Info von einer ID zu finden: Meistens für die Zuordnung Info-->Subob
        @param id Nach welcher ID gesucht werden soll
        @return null falls nichts gefunden sonst das entsprechende Subob
     */
    private Subobject getSubobByID(int id)
    {
        for (int i = 0; i != subobs.size(); i++)
            if (subobs.get(i).id == id)
                return subobs.get(i);
        return  null;
    }

    /**
        Billige Suchmethode um eine Info von einer ID zu finden: Meistens für die Zuordnung Subob-->Info
        @param id ID nach der gesucht wird
        @return Gefunde Information oder null falls nichts gefunden.
     */
    private Information getInfoForSpecificSubobID(int id)
    {
        for (int i = 0; i != myInfos.size(); i++)
            if (myInfos.get(i).id == id)
                return myInfos.get(i);
        return  null;
    }

    /**
        Methode um zu einer Information boostvalue und/oder avtiv neu zu setzen.
        @param info Ausgehend von der ID dieser info wird das entsprechende subob ermittelt und angepasst
        @param boostvalue der neue boostvalue auf den das subob gesetzt werden soll
        @param activ der neue activ auf den das subob gesetzt werden soll
     */
    public void updateSubobValuesByExtern(Information info, int boostvalue, int activ)
    {
        Subobject subob = getSubobByID(info.id);
        if(subob == null)
            throw new RuntimeException("No Subobjekt for this Info ID is known.");
        subob.boostvalue = boostvalue;
        subob.activ = activ;
    }

    /**
        Methode um dem DS neue Information hinzuzufügen. Erstellt die Subobs
        @throws RuntimeException Falls Information schon bekannt (Duplikat)
        @info Die neue Information
     */
    public void addNewInfo(Information info)
    {
        Integer temp = findInfoIfKnown(info.id);
        if(temp != null)
        {
            //Evt. könnte man später quasie ein Duplikat erlauben wenn man einfach alles neu haben möchte - daher lasse ich das hier erstmal aus kommentiert stehen
            //if (KNOWN_NEW_INFO_IS_AN_UPDATE) //Für das erste bleibt diese Konstante auf False
            //    ;//updateSubob(getSubobByID(temp));
            //else
                throw new RuntimeException("Dynamic Schedulers don't allow duplicates!");
        }
        subobs.add(new Subobject(info));
        myInfos.add(info);
    }

    /**
        Konstruktor: Setzt die forType Variable
        @param information Von dieser Information wird der String für die forType Variabel kopiert
     */
    public DynamicScheduler(Information information)
    {
        this.forTyp = information.type; // ES MUSS DIREKT DER TYPE UND NICHT DIE GET TYPE SEIN!!!
    }

    /**
        Öffentliche Funktion um ein zufällige Information zu bekommen. Da intern ein zufälliges Subob gesucht wird, wird hier auch vom Subob zurück zur Info konvetiert.
        @throws RuntimeException - falls es keine Subobs gibt
        @return Zufällige Information
     */
    public Information getRandomInfo()
    {
        if(subobs.isEmpty())
            throw new RuntimeException("Critical Error: DS is empty!");
        boolean b = true;
        Subobject subob = null;
        while(b)
        {
            subob = getRandomSubobj();
            if(subobs.size() > 1)
                b = false;
            if(lastObj != subob)
                b = false;
        }
        lastObj = subob;
        updateVarivalues(subob);
        return  getInfoForSpecificSubobID(subob.id);
    }

    /**
        Debugausgabe Funktion - nicht effizient und müllt die Konsole voll. Nur für Debugzwecke gedacht.
     */
    private String printProbabilities()
    {
        String superS = "\nAktuelle Wahrscheinlichkeiten für den Dynmischen Scheduler für den Typ \"" + this.forTyp + "\"\n";
        int variintsum = 0;
        for(int i = 0; i != subobs.size(); i++)
        {
            subobs.get(i).calcMyVariint();
            variintsum = variintsum + subobs.get(i).variint;
        }
        for(int i = 0; i != subobs.size(); i++)
        {
            Subobject subob = subobs.get(i);
            String s = "ID=" + String.valueOf(subob.id) + "|" + String.valueOf((int)Math.round( 100 * (((double)subob.variint) / ((double)variintsum)))) + "%\n";
            superS = superS + s;
        }
        System.out.print(superS);
        return superS;
    }
}
