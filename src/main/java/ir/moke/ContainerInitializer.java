package ir.moke;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ContainerInitializer implements ServletContainerInitializer {
    @Override
    public void onStartup(Set<Class<?>> classes, ServletContext ctx) {
        for (Class<?> servletClass : classes) {
            WebServlet webServletAnnotation = servletClass.getDeclaredAnnotation(WebServlet.class);
            String name = extractServletName(servletClass, webServletAnnotation);
            String[] urlPatterns = extractUrlPattern(webServletAnnotation);
            Servlet servlet = createServletInstance(servletClass);
            ctx.addServlet(name, servlet).addMapping(urlPatterns);
        }
    }

    private static String[] extractUrlPattern(WebServlet webServletAnnotation) {
        Set<String> urlPatterns = new HashSet<>();
        urlPatterns.addAll(Arrays.asList(webServletAnnotation.value()));
        urlPatterns.addAll(Arrays.asList(webServletAnnotation.urlPatterns()));
        return urlPatterns.toArray(new String[0]);
    }

    private static String extractServletName(Class<?> servletClass, WebServlet webServletAnnotation) {
        return  !webServletAnnotation.name().isEmpty() ? webServletAnnotation.name() : servletClass.getSimpleName();
    }

    private static Servlet createServletInstance(Class<?> servletClass) {
        try {
            return (Servlet) servletClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
