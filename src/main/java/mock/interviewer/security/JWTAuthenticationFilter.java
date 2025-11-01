package mock.interviewer.security;


import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
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
        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
            String email = tokenProvider.getEmailFromToken(jwt);
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());

            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(http));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(servletRequest, servletResponse);

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
