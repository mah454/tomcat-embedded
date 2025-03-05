package ir.moke.ws;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.time.LocalDateTime;

@ServerEndpoint("/ws")
public class SampleWebSocket {
    private Session session;

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
    }

    @OnMessage
    public void onMessage(String s) {
        try {
            System.out.println("Receive : " + s);
            session.getBasicRemote().sendText(LocalDateTime.now() + " Server response");
        } catch (IOException e) {
            throw new RuntimeException(e);
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
}
