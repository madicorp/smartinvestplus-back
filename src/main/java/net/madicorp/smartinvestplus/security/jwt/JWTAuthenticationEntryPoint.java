package net.madicorp.smartinvestplus.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

/**
 * Returns a 401 error code (Unauthorized) to the client.
 */
@Component
public class JWTAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final Logger log = LoggerFactory.getLogger(JWTAuthenticationEntryPoint.class);

    @Inject
    private TokenProvider tokenProvider;

    /**
     * Always returns a 401 error code to the client.
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException arg2)
        throws IOException, ServletException {
        log.debug("Authentication entry point called.");
        boolean unauthorized = true;
        try {
            String token = getToken(request, arg2);
            if (tokenProvider.validateToken(token)) {
                unauthorized = false;
                Authentication authentication = tokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (ExpiredJwtException eje) {
            log.info("Security exception for user {} - {}", eje.getClaims().getSubject(), eje.getMessage());
            unauthorized = true;
        } catch (SignatureException | AuthenticationException e) {
            unauthorized = true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (unauthorized) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        }
    }

    private String getToken(HttpServletRequest request, AuthenticationException arg2) {
        return Arrays.stream(request.getCookies())
                     .filter(cookie -> JWTConfigurer.COOKIE_NAME.equals(cookie.getName()))
                     .map(Cookie::getValue)
                     .findFirst()
                     .orElseThrow(() -> arg2);
    }
}
