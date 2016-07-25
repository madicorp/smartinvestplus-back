package net.madicorp.smartinvestplus.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static net.madicorp.smartinvestplus.security.jwt.TokenProvider.JWT_COOKIE_NAME;

/**
 * Filters incoming requests and installs a Spring Security principal if a header corresponding to a valid user is
 * found.
 */
public class JWTFilter extends GenericFilterBean {

    private final Logger log = LoggerFactory.getLogger(JWTFilter.class);

    private final TokenProvider tokenProvider;

    private final RequestMatcher nonAuthenticatedRequestMatcher;

    public JWTFilter(TokenProvider tokenProvider, String[] nonAuthenticatedUrls) {
        this.tokenProvider = tokenProvider;
        this.nonAuthenticatedRequestMatcher = getRequestMatcher(nonAuthenticatedUrls);
    }

    private static RequestMatcher getRequestMatcher(String[] authenticatedURLs) {
        List<RequestMatcher> requestMatchers = Arrays.stream(authenticatedURLs)
                                                     .map(AntPathRequestMatcher::new)
                                                     .collect(Collectors.toList());
        return new OrRequestMatcher(requestMatchers);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
        throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        if (nonAuthenticatedRequestMatcher.matches(httpServletRequest)) {
            filterChain.doFilter(request, response);
            return;
        }
        boolean unauthorized = true;
        try {
            String token = getTokenIfExist(httpServletRequest);
            if (tokenProvider.validateToken(token)) {
                unauthorized = false;
                Authentication authentication = tokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException eje) {
            log.info("Security exception for user {} - {}", eje.getClaims().getSubject(), eje.getMessage());
            unauthorized = true;
        } catch (SignatureException | AuthenticationException e) {
            unauthorized = true;
        } finally {
            if (unauthorized) {
                ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        }
    }

    private String getTokenIfExist(HttpServletRequest httpServletRequest) {
        return Arrays.stream(httpServletRequest.getCookies())
                     .filter(cookie -> JWT_COOKIE_NAME.equals(cookie.getName()))
                     .map(Cookie::getValue)
                     .findFirst()
                     .orElseThrow(this::authCredentialsNotFoundException);
    }

    private AuthenticationCredentialsNotFoundException authCredentialsNotFoundException() {
        return new AuthenticationCredentialsNotFoundException("JWT cookie is not set");
    }
}
