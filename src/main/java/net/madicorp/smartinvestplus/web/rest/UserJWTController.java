package net.madicorp.smartinvestplus.web.rest;

import com.codahale.metrics.annotation.Timed;
import net.madicorp.smartinvestplus.security.jwt.JWTConfigurer;
import net.madicorp.smartinvestplus.security.jwt.TokenProvider;
import net.madicorp.smartinvestplus.web.rest.dto.LoginDTO;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Collections;

@Component
@Path("/api")
public class UserJWTController {

    @Inject
    private TokenProvider tokenProvider;

    @Inject
    private AuthenticationManager authenticationManager;

    @Path("/authenticate")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @Produces(MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public Response authorize(@FormParam("j_username") String username, @FormParam("j_password") String password,
                              @FormParam("remember-me") Boolean rememberMe) {
        LoginDTO loginDTO = login(username, password, rememberMe);

        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());

        try {
            Authentication authentication = this.authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.createToken(authentication, loginDTO.isRememberMe());
            return Response.ok(new JWTToken(jwt)).header(JWTConfigurer.AUTHORIZATION_HEADER, "Bearer " + jwt).build();
        } catch (AuthenticationException exception) {
            return Response.status(401)
                           .entity(Collections.singletonMap("AuthenticationException", exception.getLocalizedMessage()))
                           .build();
        }
    }

    private LoginDTO login(@FormParam("username") String username, @FormParam("password") String password,
                           Boolean rememberMe) {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername(username);
        loginDTO.setPassword(password);
        loginDTO.setRememberMe(rememberMe != null ? rememberMe : false);
        return loginDTO;
    }
}
