package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main
{
    public static void main(String[] args)
    {
        System.out.println("Hello World!");

        //easyInfoHandlerTesting();
        //easyNotiHandlerTesting();
        easyEmerHandlerTesting();

        System.out.println("Reached end of main");
    }

    /*
        Von Robin:
        Damit kann ich ohne weiteres Scheduler und InformationHandlers schnell testen ohne. Wer sonst probieren will;
        -Setzte 'ALLOW_DEBUGING_TO_CONSOL' im InformationObserver auf true
        -Starte das Programm
        --Beende mit q
        --Printe die Queuefüllstandsmenge mit i
        --Gebe die ersten 3 Objekte der Queue aus (und entferne diese dabei auch)
     */
    static void easyInfoHandlerTesting()
    {
        System.out.println("Easy Info-Handler Test gestartet.");
        InformationHandler ih = new InformationHandler();
        ih.start();
        String s = "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while ( !s.equals("q") )
        {
            //System.out.println("s: '" + s + "'");
            try
            {
                s = reader.readLine();
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
            if(s.equals("l"))
            {
                System.out.println("Queuesize: " + ih.getQueueSize());
                Information info = ih.popInfo();
                if (info != null)
                    System.out.println(info.debugString());
                info = ih.popInfo();
                if (info != null)
                    System.out.println(info.debugString());
                info = ih.popInfo();
                if (info != null)
                    System.out.println(info.debugString());
                System.out.println("Queuesize: " + ih.getQueueSize());
            }
            if(s.equals("i"))
                System.out.println("Queuesize: " + ih.getQueueSize());
        }
        ih.stopMyThreads();
        System.out.println("Easy Info-Handler Test beendet.");
    }

    /**
     * Methode um das Notificationsystem zu testen. Auf der Console gibt es vier Steuerzeichen; 'q','n','t','r'&'c'
     * q Beendet das Programm
     * n ermöglicht das Anlegen einer neuen ewigen Nachricht (simuliere Zentrale)
     * t ermöglicht das Anlegen einer neuen zeitlich begrenzten Nachricht. Die erste Eingabe muss dabei die Zeit in Sekunden sein.
     * r ermöglicht das Entfernen einer existierenden Nachricht (simuliere Zentrale)
     * c popt den Head der Queue (simuliere Core)
     * @author Robin
     */
    static void easyNotiHandlerTesting()
    {
        System.out.println("Easy Noti-Handler Test gestartet.");
        NotificationHandler nh = new NotificationHandler();
        nh.start();



        String s = "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while ( !s.equals("q") )
        {
            //System.out.println("s: '" + s + "'");
            try
            {
                s = reader.readLine();
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
            if(s.equals("n"))
            {
                System.out.print("Neue Meldung: ");
                try
                {
                    s = reader.readLine();
                    System.out.print("\n");
                    nh.addNoti(s);
                }
                catch (RuntimeException e)
                {
                    System.out.print("\n");
                    System.out.println("Exception catched: " + e.getMessage());
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            }
            if(s.equals("t"))
            {
                System.out.print("Lebenszeit [in s]: ");
                try
                {
                    s = reader.readLine();
                    System.out.print("\n");
                    double t = Double.parseDouble(s);
                    System.out.print("Neue Meldung: ");
                    s = reader.readLine();
                    System.out.print("\n");
                    nh.addNoti(s,t);
                }
                catch (RuntimeException e)
                {
                    System.out.print("\n");
                    System.out.println("Exception catched: " + e.getMessage());
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            }
            if(s.equals("c"))
            {
                Notification head = nh.popNoti();
                if(head == null)
                    System.out.println("Es gibt keine Daten in der Queue");
                else
                    System.out.println(head.debugString());
            }
            if(s.equals("r"))
            {
                System.out.print("Lösche Meldung: ");
                try
                {
                    s = reader.readLine();
                    nh.removeNotiOBS(s);
                    System.out.print("\n");
                }
                catch (RuntimeException e)
                {
                    System.out.print("\n");
                    System.out.println("Exception catched: " + e.getMessage());
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            }
            System.out.println("IF-Block-Ende");
        }
        nh.stopMyThreads();
        System.out.println("Easy Info-Handler Test beendet.");
    }

    /**
     * Einfache Testmethode für das Emergency-System.
     * Füllt die PUBLIC_TEMP_WEBSOCKET_REPLACMENT_DUMMY-ArrayList direkt mit den Notfallmeldung.
     * q Beendet den Test
     * c Ersetzt den Coreaufruf und popt die Queue
     * s Fügt dem System einen Notfall zu. Falls die Nachricht gleich einer Existeten ist, wird die Nachricht als Remove interpretiert.
     * l Gibt zurück wie viele Elemente in der Queue sind.
     */
    static void easyEmerHandlerTesting()
    {
        System.out.println("Easy Emer-Handler Test gestartet.");
        EmergencyHandler eh = new EmergencyHandler();
        eh.start();
        String s = "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while ( !s.equals("q") )
        {
            //System.out.println("s: '" + s + "'");
            try
            {
                s = reader.readLine();
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
            if(s.equals("s"))
            {
                try
                {
                    System.out.print("MSG des Notfalls: ");
                    s = reader.readLine();
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
                eh.PUBLIC_TEMP_WEBSOCKET_REPLACMENT_DUMMY.add(s);
                System.out.print("\n");
            }
            if(s.equals("l"))
                System.out.println("Queuesize: " + eh.getQueueSize());
            if(s.equals("c"))
            {
                Emergency emer = eh.popEmer();
                if(emer != null)
                    System.out.println("Queuehead gefunden:\tHash: " + emer.hashValue + "\tActiv:" + emer.activ + '.');
                else
                    System.out.println("Die Queue ist leer.");
            }
        }
        eh.stopMyThreads();
        System.out.println("Easy Info-Handler Test beendet.");
    }
}
