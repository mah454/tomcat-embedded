package ir.moke;

import ir.moke.filter.CORSFilter;
import ir.moke.filter.TestFilter;
import ir.moke.servlet.TestServlet;
import ir.moke.ws.testWebSocket;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.apache.tomcat.websocket.server.WsSci;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HttpContainer {
    private static final String contextPath = "";
    private static final String baseDir = "/tmp/tomcat";

    static {
        try {
            Files.createDirectory(Path.of(baseDir));
        } catch (IOException ignore) {
        }
    }

    public static void start() {
        try {
            var tomcat = new Tomcat();
            tomcat.setConnector(createHttpConnector());
            tomcat.setConnector(createHttpsConnector());

            var context = tomcat.addWebapp(contextPath, baseDir);
            addSecurityConstraint(context);

            // add websockets
            context.addServletContainerInitializer(new WsSci(), new HashSet<>(List.of(testWebSocket.class)));

            // add servlets
            context.addServletContainerInitializer(new EmbeddedServletContainerInitializer(), Set.of(TestServlet.class));

            /*
             * add filters
             * Note : Filter could be ordered by @WebFilter#filterName method
             * */
            context.addServletContainerInitializer(new EmbeddedFilterContainerInitializer(), Set.of(TestFilter.class, CORSFilter.class));

            tomcat.start();
            tomcat.getServer().await();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Connector createHttpConnector() {
        System.out.println("HTTP connector is ready, listening to port 8080");
        Connector connector = new Connector();
        connector.setProperty("address", "0.0.0.0");
        connector.setPort(8080);
        connector.setRedirectPort(8443);
        return connector;
    }

    public static Connector createHttpsConnector() {
        System.out.println("HTTPS connector is ready, listening to port 8443");
        SSLHostConfig sslHostConfig = getSslHostConfig();
        Connector connector = new Connector();
        connector.setPort(8443);
        connector.setSecure(true);
        connector.setScheme("https");
        connector.setProperty("SSLEnabled", "true");
        connector.addSslHostConfig(sslHostConfig);

        return connector;
    }

    private static SSLHostConfig getSslHostConfig() {
        String keystorePassword = "tompass";
        String keystoreAliasName = "tomcat-embedded";

        // by default generated with keytool (see README.md)
        Path keystoreFile = Path.of("/tmp/application.keystore");

        // Generate pkcs12 keystore programmatically if jks does not exist
        if (!Files.exists(keystoreFile)) {
            KeystoreUtils.createPKCS12(keystoreFile, keystorePassword, keystoreAliasName, null);
        }
        SSLHostConfig sslHostConfig = new SSLHostConfig();
        SSLHostConfigCertificate certificate = new SSLHostConfigCertificate(sslHostConfig, SSLHostConfigCertificate.Type.RSA);
        certificate.setCertificateKeystoreFile(keystoreFile.toFile().getAbsolutePath());
        certificate.setCertificateKeystorePassword(keystorePassword);
        certificate.setCertificateKeyAlias(keystoreAliasName);
        sslHostConfig.addCertificate(certificate);
        return sslHostConfig;
    }

    private static void addSecurityConstraint(Context context) {
        SecurityConstraint securityConstraint = new SecurityConstraint();
        SecurityCollection collection = new SecurityCollection();
        collection.addPattern("/*");
        securityConstraint.addCollection(collection);

        // Enforce HTTPS
        securityConstraint.setUserConstraint("CONFIDENTIAL");
        context.addConstraint(securityConstraint);
    }
}
