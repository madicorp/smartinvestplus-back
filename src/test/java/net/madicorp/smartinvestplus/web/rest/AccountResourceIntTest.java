package net.madicorp.smartinvestplus.web.rest;

import net.madicorp.smartinvestplus.config.JacksonConfiguration;
import net.madicorp.smartinvestplus.repository.UserRepository;
import net.madicorp.smartinvestplus.security.AuthoritiesConstants;
import net.madicorp.smartinvestplus.security.SecurityUtils;
import net.madicorp.smartinvestplus.service.MailService;
import net.madicorp.smartinvestplus.test.HttpTestRule;
import net.madicorp.smartinvestplus.test.ResponseAssertion;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.SpringApplicationConfiguration;

import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.util.Optional;

import static net.madicorp.smartinvestplus.web.rest.MockData.*;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@SpringApplicationConfiguration({UserAccountIntTestConfiguration.class, JacksonConfiguration.class})
public class AccountResourceIntTest {
    @ClassRule
    public static final HttpTestRule rule = new HttpTestRule();

    @Inject
    private static UserRepository mockUserRepository;

    @Inject
    private static MailService mockMailService;

    @Inject
    private static SecurityUtils mockSecurityUtils;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        Mockito.reset(mockSecurityUtils, mockUserRepository);
        doNothing().when(mockMailService).sendActivationEmail(anyObject(), anyString());
    }

    @Test
    public void should_send_empty_login_for_not_authenticated_user() throws Exception {
        // GIVEN
        // no authenticated user

        // WHEN
        Response actual = rule.get("/api/authenticate");

        // THEN
        ResponseAssertion.assertThat(actual)
                         .noContent();
    }

    @Test
    public void should_send_login_for_authenticated_user() throws Exception {
        // GIVEN
        when(mockSecurityUtils.isAuthenticated()).thenReturn(true);
        when(mockSecurityUtils.getCurrentUserLogin()).thenReturn("admin");

        // WHEN
        Response actual = rule.get("/api/authenticate");

        // THEN
        ResponseAssertion.assertThat(actual)
                         .ok()
                         .contains("$", "admin");
    }

    @Test
    public void should_be_able_to_retrieve_admin_own_account_upon_admin_query() throws Exception {
        // GIVEN
        when(mockUserRepository.findOneByLogin("admin")).thenReturn(Optional.of(admin()));
        when(mockSecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN)).thenReturn(true);

        // WHEN
        Response actual = rule.get("/api/accounts/admin");


        // THEN
        ResponseAssertion.assertThat(actual)
                         .ok()
                         .contains("$.login", "admin")
                         .contains("$.email", "admin.super@jhipster.com");
    }

    @Test
    public void should_be_able_to_retrieve_john_doe_account_upon_admin_query() throws Exception {
        // GIVEN
        when(mockUserRepository.findOneByLogin("johndoe")).thenReturn(Optional.of(johnDoe()));
        when(mockSecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN)).thenReturn(true);

        // WHEN
        Response actual = rule.get("/api/accounts/johndoe");


        // THEN
        ResponseAssertion.assertThat(actual)
                         .ok()
                         .contains("$.login", "johndoe")
                         .contains("$.email", "john.doe@jhipster.com");
    }

    @Test
    public void should_be_able_to_retrieve_john_doe_account_upon_john_doe_query() throws Exception {
        // GIVEN
        when(mockUserRepository.findOneByLogin("johndoe")).thenReturn(Optional.of(johnDoe()));
        when(mockSecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN)).thenReturn(false);
        when(mockSecurityUtils.getCurrentUserLogin()).thenReturn("johndoe");

        // WHEN
        Response actual = rule.get("/api/accounts/johndoe");


        // THEN
        ResponseAssertion.assertThat(actual)
                         .ok()
                         .contains("$.login", "johndoe")
                         .contains("$.email", "john.doe@jhipster.com");
    }

    @Test
    public void should_return_server_error_if_account_is_unknown() throws Exception {
        // GIVEN
        when(mockUserRepository.findOneByLogin("foo")).thenReturn(Optional.empty());
        when(mockSecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN)).thenReturn(true);

        // WHEN
        Response actual = rule.get("/api/accounts/foo");


        // THEN
        ResponseAssertion.assertThat(actual)
                         .notFound();
    }

    @Test
    public void should_be_able_to_create_john_doe_account() throws Exception {
        // GIVEN
        when(mockUserRepository.findOneByLogin("johndoe")).thenReturn(Optional.empty());
        when(mockUserRepository.findOneByEmail("john.doe@jhipster.com")).thenReturn(Optional.empty());

        // WHEN
        Response actual = rule.post("/api/accounts", Entity.json(johnDoeDTO()));

        // THEN
        ResponseAssertion.assertThat(actual).created().location("/api/accounts/johndoe");
    }

    @Test
    public void should_reject_account_creation_with_invalid_login() throws Exception {
        // GIVEN

        // WHEN
        Response actual = rule.post("/api/accounts", Entity.json(invalidLoginDTO()));

        // THEN
        ResponseAssertion.assertThat(actual)
                         .badRequest();
    }

    @Test
    public void should_reject_account_creation_with_invalid_email() throws Exception {
        // GIVEN

        // WHEN
        Response actual = rule.post("/api/accounts", Entity.json(invalidEmailDTO()));

        // THEN
        ResponseAssertion.assertThat(actual)
                         .badRequest();
    }

    @Test
    public void should_reject_account_creation_with_invalid_password() throws Exception {
        // GIVEN

        // WHEN
        Response actual = rule.post("/api/accounts", Entity.json(invalidPasswordDTO()));

        // THEN
        ResponseAssertion.assertThat(actual)
                         .badRequest();
    }

    @Test
    public void should_reject_account_creation_with_duplicate_login() throws Exception {
        // GIVEN
        when(mockUserRepository.findOneByLogin("johndoe")).thenReturn(Optional.of(johnDoe()));

        // WHEN
        Response actual = rule.post("/api/accounts", Entity.json(johnDoeDTO()));

        // THEN
        ResponseAssertion.assertThat(actual)
                         .badRequest()
                         .contains("$", "login already in use");
    }

    @Test
    public void should_reject_account_creation_with_duplicate_email() throws Exception {
        // GIVEN
        when(mockUserRepository.findOneByLogin("johndoe")).thenReturn(Optional.empty());
        when(mockUserRepository.findOneByEmail("john.doe@jhipster.com")).thenReturn(Optional.of(johnDoe()));

        // WHEN
        Response actual = rule.post("/api/accounts", Entity.json(johnDoeDTO()));

        // THEN
        ResponseAssertion.assertThat(actual)
                         .badRequest()
                         .contains("$", "email address already in use");
    }

    @Test
    public void should_ignore_admin_creation_request() throws Exception {
        // GIVEN
        when(mockUserRepository.findOneByLogin("johndoe")).thenReturn(Optional.empty());
        when(mockUserRepository.findOneByEmail("john.doe@jhipster.com")).thenReturn(Optional.empty());

        // WHEN
        Response actual =
            rule.post("/api/accounts", Entity.json(johnDoeDTO(AuthoritiesConstants.ADMIN)));

        // THEN
        ResponseAssertion.assertThat(actual).created();
        // Only save the user with USER authority not admin one
        verify(mockUserRepository).save(johnDoe());
    }

    @Test
    public void should_not_update_with_invalid_information() throws Exception {
        // GIVEN

        // WHEN
        Response actual = rule.post("/api/accounts", Entity.json(invalidUserDTO()));

        // THEN
        ResponseAssertion.assertThat(actual).badRequest();
    }

    // TODO Tester mise à jour d'un compte qui n'est pas le sien (admin ou pas) et qui est le sien

    // TODO Tester activation

    // TODO Tester reset
}
