package net.madicorp.smartinvestplus.stockexchange.repository;

import net.madicorp.smartinvestplus.config.DatabaseConfiguration;
import net.madicorp.smartinvestplus.security.SpringSecurityAuditorAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Repository;

/**
 * User: sennen
 * Date: 03/07/2016
 * Time: 15:39
 */
@Configuration
@Import({DatabaseConfiguration.class, EmbeddedMongoAutoConfiguration.class})
@ComponentScan(includeFilters = @ComponentScan.Filter(Repository.class),
    excludeFilters = @ComponentScan.Filter(Configuration.class))
public class RepositoryTestConfig implements InitializingBean {
    public static final int MONGO_PORT = 27217;

    @Autowired
    private MongoProperties mongoProperties;

    @Override
    public void afterPropertiesSet() throws Exception {
        mongoProperties.setPort(MONGO_PORT);
    }

    @Bean
    public SpringSecurityAuditorAware springSecurityAuditorAware() {
        return new SpringSecurityAuditorAware();
    }
}
