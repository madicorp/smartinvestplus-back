package net.madicorp.smartinvestplus.web.rest;

import com.codahale.metrics.annotation.Timed;
import net.madicorp.smartinvestplus.config.JHipsterProperties;
import net.madicorp.smartinvestplus.domain.User;
import net.madicorp.smartinvestplus.security.jwt.TokenProvider;
import net.madicorp.smartinvestplus.service.UserService;
import net.madicorp.smartinvestplus.web.rest.dto.LoginDTO;
import net.madicorp.smartinvestplus.web.rest.dto.UserDTO;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.Collections;

import static javax.ws.rs.core.Cookie.DEFAULT_VERSION;
import static net.madicorp.smartinvestplus.security.jwt.TokenProvider.JWT_COOKIE_NAME;

@Path("/api")
public class UserJWTResource {

    @Inject
    private TokenProvider tokenProvider;

    @Inject
    private AuthenticationManager authenticationManager;

    @Inject
    private ResourceUtil resourceUtil;

    @Inject
    private UserService userService;

    @Inject
    private JHipsterProperties jHipsterProperties;

    private int tokenValidityInSeconds;

    @PostConstruct
    public void init() {
        this.tokenValidityInSeconds =
            jHipsterProperties.getSecurity().getAuthentication().getJwt().getTokenValidityInSeconds();
    }

    @Path("/authenticate")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @Produces(MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public Response authorize(@FormParam("j_username") String username, @FormParam("j_password") String password) {
        LoginDTO loginDTO = login(username, password);

        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());

        try {
            Authentication authentication = this.authenticationManager.authenticate(authenticationToken);
            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(authentication);
            String jwt = tokenProvider.createToken(authentication);
            return tryToCreateResponseWithUserInfo(username, jwt);
        } catch (AuthenticationException exception) {
            return Response.status(401)
                           .entity(Collections.singletonMap("AuthenticationException", exception.getLocalizedMessage()))
                           .build();
        }
    }

    @Path("/logout")
    @POST
    public Response logout() {
        NewCookie newCookie = cookie("", 0);
        return Response.noContent().cookie(newCookie).build();
    }

    private NewCookie cookie(String value, int maxAge) {
        return new NewCookie(JWT_COOKIE_NAME, value, "/", null, DEFAULT_VERSION, null, maxAge, false);
    }

    private Response tryToCreateResponseWithUserInfo(String username, String jwt) {
        return userService.getUserWithAuthorities(username)
                          .map(user -> successfulAuthResponse(user, jwt))
                          .orElse(resourceUtil.notFound());
    }

    private Response successfulAuthResponse(User user, String jwt) {
        return Response.ok(new UserDTO(user))
                       .cookie(cookie(jwt, tokenValidityInSeconds))
                       .build();
    }

    private LoginDTO login(String username, String password) {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername(username);
        loginDTO.setPassword(password);
        return loginDTO;
    }
}
