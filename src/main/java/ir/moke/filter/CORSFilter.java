package ir.moke.filter;

import jakarta.servlet.annotation.WebFilter;
import org.apache.catalina.filters.CorsFilter;

@WebFilter("/*")
public class CORSFilter extends CorsFilter {
}
