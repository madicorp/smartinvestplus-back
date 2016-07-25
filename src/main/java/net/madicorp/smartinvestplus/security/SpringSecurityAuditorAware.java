package net.madicorp.smartinvestplus.security;

import net.madicorp.smartinvestplus.config.Constants;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * Implementation of AuditorAware based on Spring Security.
 */
@Component
public class SpringSecurityAuditorAware implements AuditorAware<String> {

    @Inject
    private SecurityUtils securityUtils;

    @Override
    public String getCurrentAuditor() {
        String userName = securityUtils.getCurrentUserLogin();
        return (userName != null ? userName : Constants.SYSTEM_ACCOUNT);
    }
}
