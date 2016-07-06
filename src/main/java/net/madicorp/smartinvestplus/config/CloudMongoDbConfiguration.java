package net.madicorp.smartinvestplus.config;

import com.mongodb.Mongo;
import net.madicorp.smartinvestplus.domain.util.JSR310DateConverters.*;
import org.jongo.Jongo;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static net.madicorp.smartinvestplus.config.CommonDbConfiguration.jongoMapper;

@Configuration
@EnableMongoRepositories("net.madicorp.smartinvestplus.repository")
@Import(value = {MongoAutoConfiguration.class, CommonDbConfiguration.class})
@Profile(Constants.SPRING_PROFILE_CLOUD)
public class CloudMongoDbConfiguration extends AbstractMongoConfiguration {

    @Inject
    private MongoDbFactory mongoDbFactory;

    @Inject
    private CommonDbConfiguration commonDbConfiguration;

    @Bean
    public CustomConversions customConversions() {
        return commonDbConfiguration.customConversions();
    }

    @Bean
    public Jongo jongo() throws Exception {
        return commonDbConfiguration.jongo(mongo().getDB(getDatabaseName()));
    }

    @Override
    protected String getDatabaseName() {
        return mongoDbFactory.getDb().getName();
    }

    @Override
    public Mongo mongo() throws Exception {
        return mongoDbFactory().getDb().getMongo();
    }
}
