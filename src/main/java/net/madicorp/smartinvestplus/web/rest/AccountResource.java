package net.madicorp.smartinvestplus.web.rest;

import com.codahale.metrics.annotation.Timed;
import net.madicorp.smartinvestplus.domain.User;
import net.madicorp.smartinvestplus.repository.UserRepository;
import net.madicorp.smartinvestplus.security.AuthoritiesConstants;
import net.madicorp.smartinvestplus.security.SecurityUtils;
import net.madicorp.smartinvestplus.service.MailService;
import net.madicorp.smartinvestplus.service.UserService;
import net.madicorp.smartinvestplus.web.rest.dto.KeyAndPasswordDTO;
import net.madicorp.smartinvestplus.web.rest.dto.ManagedUserDTO;
import net.madicorp.smartinvestplus.web.rest.dto.UserDTO;
import net.madicorp.smartinvestplus.web.rest.util.HeaderUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Optional;

/**
 * REST controller for managing the current user's account.
 */
@Component
@Path("/api")
public class AccountResource {

    private final Logger log = LoggerFactory.getLogger(AccountResource.class);

    @Context
    private UriInfo uriInfo;

    @Inject
    private HttpUtil httpUtil;

    @Inject
    private UserRepository userRepository;

    @Inject
    private UserService userService;

    @Inject
    private MailService mailService;

    @Inject
    private SecurityUtils securityUtils;

    /**
     * POST  /accounts : register the user.
     *
     * @param managedUserDTO the managed user DTO
     * @return the Response with status 201 (Created) if the user is registred or 400 (Bad Request) if the login or e-mail is already in use
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/accounts")
    @Timed
    public Response registerAccount(@Valid ManagedUserDTO managedUserDTO) {
        return userRepository.findOneByLogin(managedUserDTO.getLogin().toLowerCase())
                             .map(user -> httpUtil.badRequestBuilder().entity("login already in use").build())
                             .orElseGet(() -> tryCreateUser(managedUserDTO));
    }

    private Response tryCreateUser(ManagedUserDTO managedUserDTO) {
        return userRepository.findOneByEmail(managedUserDTO.getEmail())
                             .map(user -> httpUtil.badRequestBuilder().entity("email address already in use").build())
                             .orElseGet(() -> doCreateUser(managedUserDTO));
    }

    private Response doCreateUser(ManagedUserDTO managedUserDTO) {
        String login = managedUserDTO.getLogin();
        User user = userService.createUserInformation(login,
                                                      managedUserDTO.getPassword(),
                                                      managedUserDTO.getFirstName(),
                                                      managedUserDTO.getLastName(),
                                                      managedUserDTO.getEmail().toLowerCase(),
                                                      managedUserDTO.getLangKey());
        mailService.sendActivationEmail(user, httpUtil.getBaseUrl(uriInfo));
        URI userAccountURI = httpUtil.getUriBuilder(uriInfo).path(login).build();
        return Response.created(userAccountURI).build();
    }

    /**
     * GET  /activate : activate the registered user.
     *
     * @param key the activation key
     * @return the Response with status 204 (No Content) and the activated user in body, or status 500 (Internal Server Error) if the user couldn't be activated
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/activate")
    @Timed
    public Response activateAccount(@QueryParam("key") String key) {
        return userService.activateRegistration(key)
                          .map(user -> httpUtil.noContent())
                          .orElse(httpUtil.serverError());
    }

    /**
     * GET  /authenticate : check if the user is authenticated, and return its login.
     *
     * @return the login if the user is authenticated
     */
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/authenticate")
    @Timed
    public Response isAuthenticated() {
        if (!securityUtils.isAuthenticated()) {
            return httpUtil.noContent();
        }
        return Response.ok(securityUtils.getCurrentUserLogin()).build();
    }

    /**
     * GET  /accounts/{login} : get user with login in path.
     *
     * @return the Response with status 200 (OK) and the current user in body, or status 500 (Internal Server Error) if the user couldn't be returned
     */
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    @Path("/accounts/{login}")
    @Timed
    public Response getAccount(@PathParam("login") String login) {
        if (isForbiddenToChangeUserResource(login)) {
            return forbidden();
        }
        return userService.getUserWithAuthorities(login)
                          .map(user -> Response.ok(new UserDTO(user)).build())
                          .orElse(httpUtil.notFound());
    }

    /**
     * PUT  /accounts : update the current user information.
     *
     * @param userDTO the current user information
     * @return the Response with status 200 (OK), or status 400 (Bad Request) or 500 (Internal Server Error) if the user couldn't be updated
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    @Path("/accounts")
    @Timed
    public Response saveAccount(@Valid UserDTO userDTO) {
        if (isForbiddenToChangeUserResource(userDTO.getLogin())) {
            return forbidden();
        }
        Optional<User> existingUser = userRepository.findOneByEmail(userDTO.getEmail());
        if (existingUser.isPresent() && (!existingUser.get().getLogin().equalsIgnoreCase(userDTO.getLogin()))) {
            return cantCreateAccountBecauseEmailExists();
        }
        return tryCreateAccount(userDTO);
    }

    private Response forbidden() {
        return Response.status(Response.Status.FORBIDDEN).build();
    }

    private Response tryCreateAccount(@Valid UserDTO userDTO) {
        return userRepository.findOneByLogin(securityUtils.getCurrentUserLogin())
                             .map(user -> {
                                 userService.updateUserInformation(user.getFirstName(), userDTO.getLastName(),
                                                                   userDTO.getEmail(),
                                                                   userDTO.getLangKey());
                                 return httpUtil.noContent();
                             })
                             .orElseGet(httpUtil::serverError);
    }

    /**
     * POST  /accounts/change-password : changes the current user's password
     *
     * @param password the new password
     * @return the Response with status 200 (OK), or status 400 (Bad Request) if the new password is not strong enough
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/accounts/password/change")
    @Timed
    public Response changePassword(String password) {
        if (!checkPasswordLength(password)) {
            return httpUtil.badRequestBuilder().entity("Incorrect password").build();
        }
        userService.changePassword(password);
        return httpUtil.noContent();
    }

    /**
     * POST   /accounts/password/reset/init : Send an e-mail to reset the password of the user
     *
     * @param mail the mail of the user
     * @return the Response with status 200 (OK) if the e-mail was sent, or status 400 (Bad Request) if the e-mail address is not registred
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/accounts/password/reset/init")
    @Timed
    public Response requestPasswordReset(String mail) {
        return userService.requestPasswordReset(mail)
                          .map(user -> {
                              mailService.sendPasswordResetMail(user, httpUtil.getBaseUrl(uriInfo));
                              return Response.ok("e-mail was sent").build();
                          }).orElse(httpUtil.badRequestBuilder().entity("e-mail address not registered").build());
    }

    /**
     * POST   /accounts/password/reset/finish : Finish to reset the password of the user
     *
     * @param keyAndPassword the generated key and the new password
     * @return the Response with status 200 (OK) if the password has been reset,
     * or status 400 (Bad Request) or 500 (Internal Server Error) if the password could not be reset
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/accounts/password/reset/finish")
    @Timed
    public Response finishPasswordReset(KeyAndPasswordDTO keyAndPassword) {
        if (!checkPasswordLength(keyAndPassword.getNewPassword())) {
            return httpUtil.badRequestBuilder().entity("Incorrect password").build();
        }
        return userService.completePasswordReset(keyAndPassword.getNewPassword(), keyAndPassword.getKey())
                          .map(user -> httpUtil.noContent())
                          .orElse(httpUtil.serverError());
    }

    private Response cantCreateAccountBecauseEmailExists() {
        HttpHeaders httpHeaders =
            HeaderUtil.createFailureAlert("user-management", "emailexists", "Email already in use");
        return httpUtil.addHeaders(httpUtil.badRequestBuilder(), httpHeaders).build();
    }

    private boolean checkPasswordLength(String password) {
        return (!StringUtils.isEmpty(password) &&
                password.length() >= ManagedUserDTO.PASSWORD_MIN_LENGTH &&
                password.length() <= ManagedUserDTO.PASSWORD_MAX_LENGTH);
    }

    private boolean isForbiddenToChangeUserResource(String login) {
        return !securityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN) &&
               !securityUtils.getCurrentUserLogin().equals(login);
    }
}
