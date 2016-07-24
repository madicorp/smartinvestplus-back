package net.madicorp.smartinvestplus.web.rest;

import com.codahale.metrics.annotation.Timed;
import net.madicorp.smartinvestplus.config.Constants;
import net.madicorp.smartinvestplus.domain.Authority;
import net.madicorp.smartinvestplus.domain.User;
import net.madicorp.smartinvestplus.repository.AuthorityRepository;
import net.madicorp.smartinvestplus.repository.UserRepository;
import net.madicorp.smartinvestplus.security.AuthoritiesConstants;
import net.madicorp.smartinvestplus.service.MailService;
import net.madicorp.smartinvestplus.service.UserService;
import net.madicorp.smartinvestplus.web.rest.dto.ManagedUserDTO;
import net.madicorp.smartinvestplus.web.rest.util.HeaderUtil;
import net.madicorp.smartinvestplus.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * REST controller for managing users.
 * <p>
 * <p>This class accesses the User entity, and needs to fetch its collection of authorities.</p>
 * <p>
 * For a normal use-case, it would be better to have an eager relationship between User and Authority,
 * and send everything to the client side: there would be no DTO, a lot less code, and an outer-join
 * which would be good for performance.
 * </p>
 * <p>
 * We use a DTO for 3 reasons:
 * <ul>
 * <li>We want to keep a lazy association between the user and the authorities, because people will
 * quite often do relationships with the user, and we don't want them to get the authorities all
 * the time for nothing (for performance reasons). This is the #1 goal: we should not impact our users'
 * application because of this use-case.</li>
 * <li> Not having an outer join causes n+1 requests to the database. This is not a real issue as
 * we have by default a second-level cache. This means on the first HTTP call we do the n+1 requests,
 * but then all authorities come from the cache, so in fact it's much better than doing an outer join
 * (which will get lots of data from the database, for each HTTP call).</li>
 * <li> As this manages users, for security reasons, we'd rather have a DTO layer.</li>
 * </ul>
 * <p>Another option would be to have a specific JPA entity graph to handle this case.</p>
 */
@Component
@Path("/api/users/")
public class UserResource {

    private final Logger log = LoggerFactory.getLogger(UserResource.class);

    @Context
    private UriInfo uriInfo;

    @Inject
    private HttpUtil httpUtil;

    @Inject
    private UserRepository userRepository;

    @Inject
    private MailService mailService;

    @Inject
    private AuthorityRepository authorityRepository;

    @Inject
    private UserService userService;

    /**
     * POST  /users  : Creates a new user.
     * <p>
     * Creates a new user if the login and email are not already used, and sends an
     * mail with an activation link.
     * The user needs to be activated on creation.
     * </p>
     *
     * @param managedUserDTO the user to create
     * @return the Response with status 201 (Created) and with body the new user, or with status 400 (Bad Request) if the login or email is already in use
     * @throws URISyntaxException if the Location URI syntaxt is incorrect
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public Response createUser(ManagedUserDTO managedUserDTO) throws URISyntaxException {
        log.debug("REST request to save User : {}", managedUserDTO);

        //Lowercase the user login before comparing with database
        if (userRepository.findOneByLogin(managedUserDTO.getLogin().toLowerCase()).isPresent()) {
            HttpHeaders headers =
                HeaderUtil.createFailureAlert("userManagement", "userexists", "Login already in use");
            return httpUtil.addHeaders(httpUtil.badRequestBuilder(), headers).build();
        } else if (userRepository.findOneByEmail(managedUserDTO.getEmail()).isPresent()) {
            HttpHeaders headers =
                HeaderUtil.createFailureAlert("userManagement", "emailexists", "Email already in use");
            return httpUtil.addHeaders(httpUtil.badRequestBuilder(), headers).build();
        } else {
            User newUser = userService.createUser(managedUserDTO);
            mailService.sendCreationEmail(newUser, httpUtil.getBaseUrl(uriInfo));
            HttpHeaders headers = HeaderUtil.createAlert("userManagement.created", newUser.getLogin());
            return httpUtil.addHeaders(Response.created(new URI("/api/users/" + newUser.getLogin())), headers)
                           .entity(newUser)
                           .build();
        }
    }

    /**
     * PUT  /users : Updates an existing User.
     *
     * @param managedUserDTO the user to update
     * @return the Response with status 200 (OK) and with body the updated user,
     * or with status 400 (Bad Request) if the login or email is already in use,
     * or with status 500 (Internal Server Error) if the user couldnt be updated
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public Response updateUser(ManagedUserDTO managedUserDTO) {
        log.debug("REST request to update User : {}", managedUserDTO);
        Optional<User> existingUser = userRepository.findOneByEmail(managedUserDTO.getEmail());
        if (existingUser.isPresent() && (!existingUser.get().getId().equals(managedUserDTO.getId()))) {
            HttpHeaders headers =
                HeaderUtil.createFailureAlert("userManagement", "emailexists", "E-mail already in use");
            return httpUtil.addHeaders(httpUtil.badRequestBuilder(), headers).build();

        }
        existingUser = userRepository.findOneByLogin(managedUserDTO.getLogin().toLowerCase());
        if (existingUser.isPresent() && (!existingUser.get().getId().equals(managedUserDTO.getId()))) {
            HttpHeaders headers =
                HeaderUtil.createFailureAlert("userManagement", "userexists", "Login already in use");
            return httpUtil.addHeaders(httpUtil.badRequestBuilder(), headers).build();
        }
        return userRepository.findOneById(managedUserDTO.getId())
                             .map(user -> {
                                 updateUser(managedUserDTO, user);
                                 HttpHeaders headers =
                                     HeaderUtil.createAlert("userManagement.updated", managedUserDTO.getLogin());
                                 return httpUtil.addHeaders(Response.ok(), headers)
                                                .entity(
                                                    new ManagedUserDTO(userRepository.findOne(managedUserDTO.getId())))
                                                .build();

                             })
                             .orElseGet(() -> httpUtil.serverError());

    }

    private void updateUser(ManagedUserDTO managedUserDTO, User user) {
        user.setLogin(managedUserDTO.getLogin());
        user.setFirstName(managedUserDTO.getFirstName());
        user.setLastName(managedUserDTO.getLastName());
        user.setEmail(managedUserDTO.getEmail());
        user.setActivated(managedUserDTO.isActivated());
        user.setLangKey(managedUserDTO.getLangKey());
        Set<Authority> authorities = user.getAuthorities();
        authorities.clear();
        managedUserDTO.getAuthorities().forEach(
            authority -> authorities.add(authorityRepository.findOne(authority))
        );
        userRepository.save(user);
    }

    /**
     * GET  /users : get all users.
     *
     * @param page the page to be retrieved
     * @param size number of elements to be retrieved
     * @return the Response with status 200 (OK) and with body all users
     * @throws URISyntaxException if the pagination headers couldnt be generated
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    public Response getAllUsers(@QueryParam("page") int page, @QueryParam("size") int size)
        throws URISyntaxException {
        Page<User> userPage = userRepository.findAll(new PageRequest(page, size));
        List<ManagedUserDTO> managedUserDTOs = userPage.getContent().stream()
                                                       .map(ManagedUserDTO::new)
                                                       .collect(Collectors.toList());
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(userPage, httpUtil.getBaseUrl(uriInfo));
        return httpUtil.addHeaders(Response.ok(), headers)
                       .entity(managedUserDTOs)
                       .build();

    }

    /**
     * GET  /users/:login : get the "login" user.
     *
     * @param login the login of the user to find
     * @return the Response with status 200 (OK) and with body the "login" user, or with status 404 (Not Found)
     */
    @GET
    @Path("/{login: " + Constants.LOGIN_REGEX + "}")
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    public Response getUser(@PathParam("login") String login) {
        log.debug("REST request to get User : {}", login);
        return userService.getUserWithAuthoritiesByLogin(login)
                          .map(ManagedUserDTO::new)
                          .map(managedUserDTO -> Response.ok().entity(managedUserDTO).build())
                          .orElse(httpUtil.notFound());
    }

    /**
     * DELETE  USER :login : delete the "login" User.
     *
     * @param login the login of the user to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DELETE
    @Path("/{login: " + Constants.LOGIN_REGEX + "}")
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Void> deleteUser(@PathParam("login") String login) {
        log.debug("REST request to delete User: {}", login);
        userService.deleteUserInformation(login);
        return ResponseEntity.ok().headers(HeaderUtil.createAlert("userManagement.deleted", login)).build();
    }
}
