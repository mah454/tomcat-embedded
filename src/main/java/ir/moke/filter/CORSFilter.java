package ir.moke.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import org.apache.catalina.filters.CorsFilter;

import java.io.IOException;

@WebFilter("/*")
public class CORSFilter extends CorsFilter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("CORS Filter Executed");
        super.doFilter(servletRequest, servletResponse, filterChain);
    }
}
