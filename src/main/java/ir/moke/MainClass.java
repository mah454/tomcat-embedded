package ir.moke;

import ir.moke.servlet.SampleServlet;
import ir.moke.ws.SampleWebSocket;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.websocket.server.WsSci;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainClass {
    private static final String contextPath = "";
    private static final String baseDir = "/tmp/tomcat";

    static {
        try {
            Files.createDirectory(Path.of(baseDir));
        } catch (IOException ignore) {
        }
    }

    public static void main(String[] args) throws Exception {

        var tomcat = new Tomcat();
        tomcat.setPort(8080);
        tomcat.getConnector().setProperty("address", "0.0.0.0");

        var context = tomcat.addWebapp(contextPath, baseDir);

        // add websockets
        context.addServletContainerInitializer(new WsSci(), new HashSet<>(List.of(SampleWebSocket.class)));

        // add servlets
        context.addServletContainerInitializer(new ContainerInitializer(), Set.of(SampleServlet.class));

        tomcat.start();
        System.out.println("Tomcat startup is completed, listening to port 8080");
        tomcat.getServer().await();
    }
}