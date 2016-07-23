package net.madicorp.smartinvestplus.web.rest;

import net.madicorp.smartinvestplus.domain.Authority;
import net.madicorp.smartinvestplus.domain.User;
import net.madicorp.smartinvestplus.security.AuthoritiesConstants;
import net.madicorp.smartinvestplus.web.rest.dto.ManagedUserDTO;
import net.madicorp.smartinvestplus.web.rest.dto.UserDTO;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * User: sennen
 * Date: 23/07/2016
 * Time: 21:23
 */
public class MockData {

    static User johnDoe() {
        Set<Authority> authorities = new HashSet<>();
        Authority authority = new Authority();
        authority.setName(AuthoritiesConstants.USER);
        authorities.add(authority);

        User user = new User();
        user.setLogin("johndoe");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@jhipster.com");
        user.setAuthorities(authorities);
        return user;
    }

    static ManagedUserDTO johnDoeDTO() {
        return johnDoeDTO(AuthoritiesConstants.USER);
    }

    static ManagedUserDTO johnDoeDTO(String authority) {
        return new ManagedUserDTO(
            null,                   // id
            "johndoe",                  // login
            "password",             // password
            "John",                  // firstName
            "Doe",                // lastName
            "john.doe@jhipster.com",      // e-mail
            true,                   // activated
            "fr",               // langKey
            new HashSet<>(Collections.singletonList(authority)),
            null,                   // createdDate
            null,                   // lastModifiedBy
            null                    // lastModifiedDate
        );
    }

    static User admin() {
        Set<Authority> authorities = new HashSet<>();
        Authority authority = new Authority();
        authority.setName(AuthoritiesConstants.ADMIN);
        authorities.add(authority);

        User user = new User();
        user.setLogin("admin");
        user.setFirstName("admin");
        user.setLastName("super");
        user.setEmail("admin.super@jhipster.com");
        user.setAuthorities(authorities);
        return user;
    }

    static ManagedUserDTO invalidLoginDTO() {
        return new ManagedUserDTO(
            null,                   // id
            "funky-log!n",          // login <-- invalid
            "password",             // password
            "Funky",                // firstName
            "One",                  // lastName
            "funky@example.com",    // e-mail
            true,                   // activated
            "fr",               // langKey
            new HashSet<>(Collections.singletonList(AuthoritiesConstants.USER)),
            null,                   // createdDate
            null,                   // lastModifiedBy
            null                    // lastModifiedDate
        );
    }

    static ManagedUserDTO invalidEmailDTO() {
        return new ManagedUserDTO(
            null,                   // id
            "funky",          // login
            "password",             // password
            "Funky",                // firstName
            "One",                  // lastName
            "funky",    // e-mail <-- invalid
            true,                   // activated
            "fr",               // langKey
            new HashSet<>(Collections.singletonList(AuthoritiesConstants.USER)),
            null,                   // createdDate
            null,                   // lastModifiedBy
            null                    // lastModifiedDate
        );
    }

    static ManagedUserDTO invalidPasswordDTO() {
        return new ManagedUserDTO(
            null,                   // id
            "funky",          // login
            "123",             // password <-- invalid
            "Funky",                // firstName
            "One",                  // lastName
            "funky@gmail.com",    // e-mail
            true,                   // activated
            "fr",               // langKey
            new HashSet<>(Collections.singletonList(AuthoritiesConstants.USER)),
            null,                   // createdDate
            null,                   // lastModifiedBy
            null                    // lastModifiedDate
        );
    }

    static UserDTO invalidUserDTO() {
        return new UserDTO(
            "johndoe",
            "John",
            "Doe",
            "johndoe",
            false,
            "fr",
            new HashSet<>()
        );
    }
}
