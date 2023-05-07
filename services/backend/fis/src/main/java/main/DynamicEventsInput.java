package main;

import DataClasses.Position;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Klasse zur Eingabe von dynamischen Events (Notfälle, Benachrichtigungen, Haltestellenevents) über die Konsole.
 * @author Fabian Ferrari
 */
public class DynamicEventsInput {
    /**
     * Statische Methode zur Eingabe von dynamischen Ereignissen.
     * Die möglichen Ereignisse und deren Eingabe sind:
     * - Notfall: "n"
     * - Benachrichtigung (Hinweis): "b"
     * - Haltestellenevent: "h"
     *
     * @param notificationHandler Der Handler für die Benachrichtigungen
     * @param emergencyHandler Der Handler für die Notfälle
     * @param drivingStopHandler Der Handler für die Haltestellenevents
     */
    public static void run(NotificationHandler notificationHandler, EmergencyHandler emergencyHandler, DrivingStopHandler drivingStopHandler) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input = "", title = "", text = "";
        do {
            System.out.println("\n==================================================");
            System.out.println("Geben Sie ein, welches dynamische Ereignis Sie auslösen möchten:");
            System.out.println("n - Notfall");
            System.out.println("b - Benachrichtigung");
            System.out.println("h - Haltestellenevent");
            System.out.println("STRG C - Programm beenden");
            System.out.print("Eingabe: ");
            try {
                input = reader.readLine();
            } catch(Exception e) {
                System.out.println("Fehler bei der Eingabe: " + e.getMessage());
            }
            try {
                switch (input) {
                    case "n" -> {   // Notfall
                        System.out.println("Soll ein neuer Notfall ausgelöst (n) oder ein bestehender beendet werden (e)?");
                        System.out.print("Eingabe: ");
                        String newEmergency = reader.readLine();
                        System.out.print("Text: ");
                        text = reader.readLine();
                        if (newEmergency.equals("n")) {
                            //System.out.print("Überschrift: ");
                            //title = reader.readLine();
                            emergencyHandler.addEmer(text);
                        }
                        else if (newEmergency.equals("e")) {
                            emergencyHandler.removeEmer(text);
                        }
                        else {
                            System.out.println("Ungültige Eingabe!");
                        }
                    }
                    case "b" -> {   // Benachrichtigung (Hinweis)
                        System.out.println("Soll eine neuer Hinweis hinzugefügt (n) oder ein bestehender Hinweis beendet (e) werden?");
                        System.out.print("Eingabe: ");
                        String newNotification = reader.readLine();
                        System.out.print("Text: ");
                        text = reader.readLine();
                        if (newNotification.equals("n")) {
                            notificationHandler.addNoti(text);
                        }
                        else if (newNotification.equals("e")) {
                            notificationHandler.removeNotiOBS(text);
                        }
                        else {
                            System.out.println("Ungültige Eingabe!");
                        }
                    }
                    case "h" -> {   // Haltestellenevent
                        System.out.println("Nähert sich das Fahrzeug einer Haltestelle (n) oder verlässt es diese (e)?");
                        System.out.print("Eingabe: ");
                        String newDrivingStop = reader.readLine();
                        if (newDrivingStop.equals("n")) {
                            drivingStopHandler.addNewPosEvent(new Position("", true));
                        }
                        else if (newDrivingStop.equals("e")) {
                            System.out.print("Name der nächsten Haltestelle: ");
                            text = reader.readLine();
                            drivingStopHandler.addNewPosEvent(new Position(text, false));
                        }
                        else {
                            System.out.println("Ungültige Eingabe!");
                        }
                    }
                    // case "e" -> System.out.println("Programm wird beendet...");
                    default -> System.out.println("Ungültige Eingabe!");
                }
            } catch(IOException e) {
                System.out.println("Fehler bei der Eingabe: " + e.getMessage());
            } catch (RuntimeException e) {
                System.out.println("Fehler beim Verarbeiten des dynamischen Ereignisses: " + e.getMessage());
            }
        // } while(!input.equals("e"));
        } while(true);
    }
}
