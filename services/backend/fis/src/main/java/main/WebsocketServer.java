package main;

import com.google.gson.Gson;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

/**
 * Websocket Server fuer die Kommunikation
 */
public class WebsocketServer extends WebSocketServer {
    
    public WebsocketServer(InetSocketAddress addr) {
        super(addr);
    }

    private static final Gson gson = new Gson();
    private WebSocket socket;

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        this.socket = webSocket;
    }

    /**
     * @param name Name der Linie die der Bus befaehrt
     * @param dest Zielstation der Linie
     */
    public record LineInfo(String name, String dest) {
        static String topic = "lineinfo";
    }

    public void sendLineInfo(String name, String dest) {
        this.sendLineInfo(new LineInfo(name, dest));
    }

    public void sendLineInfo(LineInfo info) {
        var data = gson.toJson(info);
        this.socket.send(data);
    }

    /**
     * @param name         Name des der Haltestation.
     * @param planned_time Geplante Ankunftszeit im "Kitchen Clock" Format, z.B. "14:54".
     */
    public record NextStop(String name, String planned_time) {
        static String topic = "nextstop";
    }

    public void sendNextStop(String name, String planned_time) {
        this.sendNextStop(new NextStop(name, planned_time));
    }

    public void sendNextStop(NextStop stop) {
        var data = gson.toJson(stop);
        this.socket.send(data);
    }

    /**
     * @param id      Einzigartige ID fuer diese Notification.
     * @param type    Ob die Notification hinzugefuegt oder entfernt wird.
     * @param message Die Nachricht die angezeigt wird. Leer, wenn Type == REMOVE.
     */
    public record Notification(String id, Type type, String message) {
        static String topic = "notifications";

        public enum Type {
            ADD,
            REMOVE,
        }
    }

    public void sendNotification(String id, Notification.Type type, String message) {
        this.sendNotification(new Notification(id, type, message));
    }

    public void sendNotification(Notification notification) {
        var data = gson.toJson(notification);
        this.socket.send(data);
    }

    public record InformationText(String header, String message) {
        static String topic = "information";
        static String type = "text";
    }

    public void sendInformationText(String header, String message) {
        this.sendInformationText(new InformationText(header, message));
    }

    public void sendInformationText(InformationText info) {
        var data = gson.toJson(info);
        this.socket.send(data);
    }

    public record InformationTable(String header, Table message) {
        static String topic = "information";
        static String type = "table";
    }

    public record Table(String[] headers, String[][] items) {
    }

    public void sendInformationTable(String header, Table message) {
        this.sendInformationTable(new InformationTable(header, message));
    }

    public void sendInformationTable(InformationTable info) {
        var data = gson.toJson(info);
        this.socket.send(data);
    }

    @Override
    public void onClose(WebSocket webSocket, int code, String reason, boolean remote) {
        System.out.println("frontend disconnected: " + reason);
    }

    @Override
    public void onMessage(WebSocket webSocket, String message) {
        System.out.println("frontend sent message: " + message);
    }

    @Override
    public void onError(WebSocket webSocket, Exception error) {
        System.out.println("frontend error: " + error.toString());
    }

    @Override
    public void onStart() {
        System.out.println("started websocket server");
    }
}
