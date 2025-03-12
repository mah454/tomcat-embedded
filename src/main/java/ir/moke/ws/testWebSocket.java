package ir.moke.ws;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.apache.tomcat.websocket.WsSession;

@ServerEndpoint("/ws/test")
public class testWebSocket {
    private Session session;

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
    }

    @OnMessage
    public void onMessage(String s) {
        System.out.println("Receive : " + s.trim());
        if (s.trim().equals("quit") || s.trim().equals("exit")) {
            fullCloseConnection();
        }
    }

    @OnClose
    public void onClose(Session session) {
        System.out.printf("SessionID: %s closed%n", session.getId());
    }

    @OnError
    public void onError(Throwable throwable) {
        System.out.println("Error : " + throwable.getMessage());
    }

    private void fullCloseConnection() {
        /* If you want socket completely closed */
        WsSession wsSession = (WsSession) session;
        CloseReason reason = new CloseReason(CloseReason.CloseCodes.GOING_AWAY, "Close Socket");
        wsSession.doClose(reason, reason, true);
    }
}
