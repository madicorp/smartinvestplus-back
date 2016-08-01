package net.madicorp.smartinvestplus.web.rest;

import net.madicorp.smartinvestplus.config.JacksonConfiguration;
import net.madicorp.smartinvestplus.repository.UserRepository;
import net.madicorp.smartinvestplus.service.UserService;
import net.madicorp.smartinvestplus.test.HttpTestRule;
import net.madicorp.smartinvestplus.test.ResponseAssertion;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.Optional;

import static net.madicorp.smartinvestplus.web.rest.MockData.admin;
import static org.mockito.Mockito.when;

/**
 * Test class for the UserResource REST controller.
 *
 * @see UserResource
 */
@SpringApplicationConfiguration({IntTestConfiguration.class, JacksonConfiguration.class})
public class UserResourceIntTest {
    @ClassRule
    public static final HttpTestRule rule = new HttpTestRule();

    @Inject
    private static UserRepository mockUserRepository;

    @Inject
    private static UserService userService;

    private MockMvc restUserMockMvc;

    @Before
    public void setup() {
        Mockito.reset(mockUserRepository);
    }

    @Test
    public void should_get_existing_user() throws Exception {
        // GIVEN
        when(mockUserRepository.findOneByLogin("admin")).thenReturn(Optional.of(admin()));

        // WHEN
        Response actual = rule.get("/api/users/admin");

        // THEN
        ResponseAssertion.assertThat(actual)
                         .ok()
                         .contains("$.lastName", "super");
    }

    @Test
    public void should_return_not_found_for_unknown_user() throws Exception {
        // GIVEN
        when(mockUserRepository.findOneByLogin("unknown")).thenReturn(Optional.empty());

        // WHEN
        Response actual = rule.get("/api/users/unknown");

        // THEN
        ResponseAssertion.assertThat(actual)
                         .notFound();
    }

}
