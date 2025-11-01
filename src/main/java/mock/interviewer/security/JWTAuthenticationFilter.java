package mock.interviewer.security;


import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Service
@AllArgsConstructor
public class JWTAuthenticationFilter extends GenericFilter {

    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService userDetailsService;


    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest http = (HttpServletRequest) servletRequest;

        String path = http.getRequestURI();

        // skip only login and register endpoints
        if (path.equals("/api/auth/register") || path.equals("api/auth/login")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        String jwt = resolveToken(http);

    }


    private String resolveToken(HttpServletRequest request) {
        //"Authorization": "Bearer abc123.jwt.token"
        String bearer = request.getHeader("Authorization");
        // hasText checks if null or empty string
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);  //"Bearer abc123.jwt.token"
                                                    // returns "abc123.jwt.token"
        }// return jwt tokwn
        return null;
    }
}
