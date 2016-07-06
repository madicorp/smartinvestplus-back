package net.madicorp.smartinvestplus.config;

import com.mongodb.Mongo;
import org.jongo.Jongo;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import javax.inject.Inject;


@Configuration
@Profile("!" + Constants.SPRING_PROFILE_CLOUD)
@EnableMongoRepositories("net.madicorp.smartinvestplus")
@Import(value = {MongoAutoConfiguration.class, CommonDbConfiguration.class})
@EnableMongoAuditing(auditorAwareRef = "springSecurityAuditorAware")
public class DatabaseConfiguration extends AbstractMongoConfiguration {

    @Inject
    private Mongo mongo;

    @Inject
    private MongoProperties mongoProperties;

    @Inject
    private CommonDbConfiguration commonDbConfiguration;

    @Override
    protected String getDatabaseName() {
        return mongoProperties.getDatabase();
    }

    @Override
    public Mongo mongo() throws Exception {
        return mongo;
    }

    @Bean
    public Jongo jongo() throws Exception {
        return commonDbConfiguration.jongo(mongo.getDB(getDatabaseName()));
    }

    @Bean
    public CustomConversions customConversions() {
        return commonDbConfiguration.customConversions();
    }
}
