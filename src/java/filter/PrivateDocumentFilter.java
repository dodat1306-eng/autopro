package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns={"/uploads/seller-verify/*","/private/*"})
public class PrivateDocumentFilter implements Filter {
    @Override public void doFilter(ServletRequest req,ServletResponse resp,FilterChain chain) throws IOException {
        ((HttpServletResponse)resp).sendError(404);
    }
}
