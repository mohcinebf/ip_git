package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main
{
    public static void main(String[] args)
    {
        System.out.println("Hello World!");

        easyInfoHandlerTesting();

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
        InformationHandeler ih = new InformationHandeler();
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
        ih.stopMyObserver();
        System.out.println("Easy Info-Handler Test beendet.");
    }
}
