package core;

import DataClasses.*;
import main.*;

import java.net.InetSocketAddress;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static java.lang.Thread.sleep;

/**
 * Diese Klasse bildet den Kern des Bus-Backends.
 * Sie überprüft, ob Informationen in den Queues der verschiedenen Handler sind, entnimmt diese und leitet sie an den Websocket-Server weiter, der sie an das Frontend weiterleitet.
 *
 * Im Konstruktor wird die Linien-Information (Linienname und Zielstation) an den Websocket-Server weitergeleitet. Danach wird die main loop gestartet.
 * Die main loop ist dabei wie folgt aufgebaut:
 * 1. Überprüfe, ob ein Notfall vorliegt.
 *      - Falls ja und der Notfall "active = true" hat, leite Notfall an Websocket-Server weiter. Setze "emergencyActive = true".
 *      - Falls ja und der Notfall "active = false" hat, setze "emergencyActive = false". Sende letzte "normale" Nachricht an Websocket-Server.
 * 2. Überprüfe, ob ein neuer Fahrstopp vorliegt.
 *      - Falls ja, leite Fahrstopp an Websocket-Server weiter.
 * 3. Überprüfe, ob ein neuer Hinweis vorliegt.
 *      - Falls ja und der Hinweis "active = true" hat, leite Hinweis an Websocket-Server weiter.
 *      - Falls ja und der Hinweis "active = false" hat, entferne Hinweis aus Websocket-Server.
 * 4. Überprüfe, ob die aktuell angezeigte Information ihre Anzeigezeit überschritten hat oder noch gar keine Information angezeigt wird.
 *      - Falls ja, nimm die nächste Information aus der Queue des InformationHandlers und leite sie an den Websocket-Server weiter.
 *        Setze "lastInformation" auf die Information, die gerade angezeigt wird.
 *        Merke dir die aktuelle Zeit.
 * 5. Warte THREAD_SLEEPING_TIME_IN_SECONDS Sekunden und springe zu Schritt 1.
 *
 * @author Fabian Ferrari
 */
public class Core {
    private final int THREAD_SLEEPING_TIME_IN_SECONDS = 2;  // Es sollte reichen, wenn der Core alle 2 Sekunden überprüft, ob neue Informationen in den Queues sind.
    private final String WEBSOCKET_SERVER_ADDRESS = "localhost"; // Adresse des Websocket-Servers
    private final int WEBSOCKET_SERVER_PORT = 8080; // Port des Websocket-Servers

    private final InformationHandler informationHandler;
    private final DrivingStopHandler drivingStopHandler;
    private final NotificationHandler notificationHandler;
    private final EmergencyHandler emergencyHandler;

    private final WebsocketServer websocketServer;
    private boolean emergencyActive = false;    // Gibt an, ob ein Notfall aktiv ist.
    private boolean drivingStopActive = false;  // Gibt an, ob ein Fahrstopp aktiv ist, d.h. ob sich das Fahrzeug gerade einer Haltestelle nähert.
    private Position missedDrivingStop = null;  // Speichert das Event, wenn ein Fahrstopp verpasst wurde, d.h. wenn sich das Fahrzeug einer Haltestelle nähert, während ein Notfall aktiv ist.
    private InfoSection2 lastInformation = null;    // Die letzte Information, die angezeigt wurde.
    private boolean isRunning = true;   // Gibt an, ob die main loop noch laufen soll.
    private final Thread mainLoopThread;

    /**
     * Konstruktor der Klasse Core.
     * Erstellt die Handler und startet main loop.
     */
    public Core(
            InformationHandler informationHandler,
            NotificationHandler notificationHandler,
            EmergencyHandler emergencyHandler,
            DrivingStopHandler drivingStopHandler,
            String lineName,
            String destination
    ) {
        this.informationHandler = informationHandler;
        this.notificationHandler = notificationHandler;
        this.emergencyHandler = emergencyHandler;
        this.drivingStopHandler = drivingStopHandler;

        this.websocketServer = new WebsocketServer(new InetSocketAddress(WEBSOCKET_SERVER_ADDRESS, WEBSOCKET_SERVER_PORT));
        this.websocketServer.sendLineInfo(lineName, destination);

        // Startet die main loop in einem eigenen Thread.
        mainLoopThread = new Thread(this::mainLoop);
        mainLoopThread.start();
    }

    /**
     * 1. Überprüfe, ob ein Notfall vorliegt.
     *      - Falls ja und der Notfall "active = true" hat, leite Notfall an Websocket-Server weiter. Setze "emergencyActive = true".
     *      - Falls ja und der Notfall "active = false" hat, setze "emergencyActive = false". Sende letzte "normale" Nachricht an Websocket-Server.
     * 2. Überprüfe, ob ein neuer Fahrstopp vorliegt.
     *      - Falls ja, leite Fahrstopp an Websocket-Server weiter.
     * 3. Überprüfe, ob ein neuer Hinweis vorliegt.
     *      - Falls ja und der Hinweis "active = true" hat, leite Hinweis an Websocket-Server weiter.
     *      - Falls ja und der Hinweis "active = false" hat, entferne Hinweis aus Websocket-Server.
     * 4. Überprüfe, ob die aktuell angezeigte Information ihre Anzeigezeit überschritten hat oder noch gar keine Information angezeigt wird.
     *      - Falls ja, nimm die nächste Information aus der Queue des InformationHandlers und leite sie an den Websocket-Server weiter.
     *        Setze "lastInformation" auf die Information, die gerade angezeigt wird.
     *        Merke dir die aktuelle Zeit.
     * 5. Warte THREAD_SLEEPING_TIME_IN_SECONDS Sekunden und springe zu Schritt 1.
     */
    private void mainLoop() {
        int startTimeInSeconds = (int) (System.currentTimeMillis() / 1000);

        while (isRunning) {
            // 1. Überprüfe, ob ein Notfall vorliegt.
            Emergency emergency = emergencyHandler.popEmer();
            if (emergency != null) {
                if (emergency.activ) {
                    websocketServer.sendInformationText(emergency.header, emergency.msg);
                    emergencyActive = true;
                } else {
                    emergencyActive = false;
                    if (lastInformation != null) {
                        websocketServer.sendInformationText(lastInformation.header, lastInformation.msg);
                        startTimeInSeconds = (int) (System.currentTimeMillis() / 1000);
                    }
                }
            }

            // 2. Überprüfe, ob ein neuer Fahrstopp vorliegt
            Position drivingStop = drivingStopHandler.popPosition();
            if (drivingStop != null) {
                if (drivingStop.getHaltnaehern()) {
                    if (emergencyActive) {
                        // Wenn sich das Fahrzeug einem Halt nähert und ein Notfall aktiv ist, werden die Anschlüsse nicht angezeigt.
                        // Es wird sich gemerkt, dass ein Fahrstopp verpasst wurde, damit die Anschlüsse nach dem Notfall wieder angezeigt werden können.
                        missedDrivingStop = drivingStop;
                    }
                    else {
                        // Wenn sich das Fahrzeug einem Halt nähert, werden dem Frontend die Anschlüsse an der kommenden Haltestelle mitgeteilt.
                        websocketServer.sendInformationTable("Anschlüsse", this.getUpcomingHalts());
                        drivingStopActive = true;
                    }
                }
                else {
                    // Wenn sich das Fahrzeug von einem Halt entfernt, wird dem Frontend die neue nächste Haltestelle mitgeteilt und die zuletzt angezeigte Information wird wieder angezeigt.
                    websocketServer.sendNextStop(drivingStop.getHaltestelle(), LocalTime.now().plusMinutes(5).format(DateTimeFormatter.ofPattern("HH:mm")));
                    drivingStopActive = false;
                    missedDrivingStop = null; // Falls ein Fahrstopp verpasst wurde, wird dieser hier wieder zurückgesetzt, da er nicht mehr relevant ist (da das Fahrzeug sich von der Haltestelle entfernt hat).
                    if (lastInformation != null) {
                        websocketServer.sendInformationText(lastInformation.header, lastInformation.msg);
                        startTimeInSeconds = (int) (System.currentTimeMillis() / 1000);
                    }
                }
            } else if (missedDrivingStop != null && !emergencyActive) {
                // Wenn ein Fahrstopp verpasst wurde und kein Notfall aktiv ist, werden die Anschlüsse an der kommenden Haltestelle angezeigt.
                websocketServer.sendInformationTable("Anschlüsse", this.getUpcomingHalts());
                drivingStopActive = true;
                missedDrivingStop = null;
            }

            // 3. Überprüfe, ob ein neuer Hinweis vorliegt.
            Notification notification = notificationHandler.popNoti();
            if (notification != null) {
                if (notification.activ) {
                    websocketServer.sendNotification(String.valueOf(notification.hashValue), WebsocketServer.Notification.Type.ADD, notification.msg);
                } else {
                    websocketServer.sendNotification(String.valueOf(notification.hashValue), WebsocketServer.Notification.Type.REMOVE, "");
                }
            }

            // Falls ein Notfall oder ein Fahrstopp aktiv ist (d.h. gerade die Anschlüsse an der nächsten Haltestelle angezeigt werden), überspringe alle anderen Anzeigen.
            if (emergencyActive || drivingStopActive) {
                try {
                    sleep(THREAD_SLEEPING_TIME_IN_SECONDS * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }

            // 4. Überprüfe, ob die aktuell angezeigte Information ihre Anzeigezeit überschritten hat oder noch gar keine Information angezeigt wird.
            if (lastInformation == null || (System.currentTimeMillis() / 1000) - startTimeInSeconds >= ((Information)lastInformation).duration) {
                Information information = informationHandler.popInfo();
                if (information != null) {
                    websocketServer.sendInformationText(information.header, information.msg);
                    lastInformation = information;
                    startTimeInSeconds = (int) (System.currentTimeMillis() / 1000);
                }
            }

            // 5. Warte THREAD_SLEEPING_TIME_IN_SECONDS Sekunden und springe zu Schritt 1.
            try {
                sleep(THREAD_SLEEPING_TIME_IN_SECONDS * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Gibt die nächsten Haltestellen der Linie zurück.
     * TODO: Anschlüsse an der kommenden Haltestelle ermitteln. Für den ersten Prototypen werden die Anschlüsse hart kodiert.
     * @return Die nächsten Haltestellen der Linie als WebsocketServer.Table (also inklusive der Tabellenüberschriften).
     */
    public WebsocketServer.Table getUpcomingHalts() {
        // Aktuelle Zeit
        LocalTime now = LocalTime.now();
        String[] tableHeaders = {"Abfahrt", "Linie", "Ziel", "Gleis"};
        String[][] tableData = {
                {now.plusMinutes(5).format(DateTimeFormatter.ofPattern("HH:mm")), "RE1", "Aachen", "1"},
                {now.plusMinutes(10).format(DateTimeFormatter.ofPattern("HH:mm")), "RE2", "Köln", "2"},
                {now.plusMinutes(15).format(DateTimeFormatter.ofPattern("HH:mm")), "RE3", "Düsseldorf", "3"},
                {now.plusMinutes(20).format(DateTimeFormatter.ofPattern("HH:mm")), "RE4", "Dortmund", "4"},
        };
        return new WebsocketServer.Table(tableHeaders, tableData);
    }

    /**
     * Beendet den Core und wartet, bis der dazugehörige Thread beendet wurde.
     */
    public void stop() {
        isRunning = false;
        try {
            mainLoopThread.join();
        } catch (InterruptedException e) {
            System.out.println("Core: Error while stopping main loop thread.");
        }
    }
}
