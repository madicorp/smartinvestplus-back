package net.madicorp.smartinvestplus.web.rest;

import net.madicorp.smartinvestplus.config.JHipsterProperties;
import net.madicorp.smartinvestplus.config.audit.AuditEventConverter;
import net.madicorp.smartinvestplus.repository.AuthorityRepository;
import net.madicorp.smartinvestplus.repository.PersistenceAuditEventRepository;
import net.madicorp.smartinvestplus.repository.UserRepository;
import net.madicorp.smartinvestplus.security.SecurityUtils;
import net.madicorp.smartinvestplus.service.AuditEventService;
import net.madicorp.smartinvestplus.service.MailService;
import net.madicorp.smartinvestplus.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.thymeleaf.spring4.SpringTemplateEngine;

import static org.mockito.Mockito.mock;

/**
 * User: sennen
 * Date: 23/07/2016
 * Time: 17:14
 */
public class IntTestConfiguration {
    @Bean
    public AuditResource auditResource() {
        return new AuditResource();
    }

    @Bean
    public AuditEventService auditEventService() {
        return new AuditEventService();
    }

    @Bean
    public AuditEventConverter auditEventConverter() {
        return new AuditEventConverter();
    }

    @Bean
    public AccountResource accountResource() {
        return new AccountResource();
    }

    @Bean
    public UserService userService() {
        return new UserService();
    }

    @Bean
    public UserResource userResource() {
        return new UserResource();
    }

    @Bean
    public ResourceUtil httpUtil() {
        return new ResourceUtil();
    }

    @Bean
    public SecurityUtils mockSecurityUtils() {
        return mock(SecurityUtils.class);
    }

    @Bean
    public PersistenceAuditEventRepository persistenceAuditEventRepository() {
        return mock(PersistenceAuditEventRepository.class);
    }

    @Bean
    public UserRepository mockUserRepository() {
        return mock(UserRepository.class);
    }

    @Bean
    public MailService mockMailService() {
        return mock(MailService.class);
    }

    @Bean
    public AuthorityRepository mockAuthorityRepository() {
        return mock(AuthorityRepository.class);
    }

    @Bean
    public JHipsterProperties mockJHipsterProperties() {
        return mock(JHipsterProperties.class);
    }

    @Bean
    public JavaMailSenderImpl mockJavaMailSender() {
        return mock(JavaMailSenderImpl.class);
    }

    @Bean
    public SpringTemplateEngine mockSpringTemplateEngine() {
        return mock(SpringTemplateEngine.class);
    }

    @Bean
    public PasswordEncoder mockPasswordEncoder() {
        return mock(PasswordEncoder.class);
    }
}
