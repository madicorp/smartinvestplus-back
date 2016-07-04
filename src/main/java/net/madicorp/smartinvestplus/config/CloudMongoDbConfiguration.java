package net.madicorp.smartinvestplus.config;

import com.github.mongobee.Mongobee;
import com.mongodb.Mongo;
import com.mongodb.MongoClientOptions;
import net.madicorp.smartinvestplus.domain.util.JSR310DateConverters.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.inject.Inject;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableMongoRepositories("net.madicorp.smartinvestplus.repository")
@Import(value = MongoAutoConfiguration.class)
@Profile(Constants.SPRING_PROFILE_CLOUD)
public class CloudMongoDbConfiguration extends AbstractMongoConfiguration  {

    private final Logger log = LoggerFactory.getLogger(CloudDatabaseConfiguration.class);

    @Inject
    private MongoDbFactory mongoDbFactory;

    @Inject
    private MongoProperties mongoProperties;

    @Autowired(required = false)
    private MongoClientOptions mongoOptions;

    @Autowired
    private Environment environment;

    @Bean
    public ValidatingMongoEventListener validatingMongoEventListener() {
        return new ValidatingMongoEventListener(validator());
    }

    @Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    public CustomConversions customConversions() {
        List<Converter<?, ?>> converterList = new ArrayList<>();;
        converterList.add(DateToZonedDateTimeConverter.INSTANCE);
        converterList.add(ZonedDateTimeToDateConverter.INSTANCE);
        converterList.add(DateToLocalDateConverter.INSTANCE);
        converterList.add(LocalDateToDateConverter.INSTANCE);
        converterList.add(DateToLocalDateTimeConverter.INSTANCE);
        converterList.add(LocalDateTimeToDateConverter.INSTANCE);
        return new CustomConversions(converterList);
    }

    @Bean
    public Mongobee mongobee() throws Exception {
        log.debug("Configuring Mongobee");
        Mongobee mongobee = new Mongobee(jongoMongo());
        mongobee.setDbName(getDatabaseName());
        // package to scan for migrations
        mongobee.setChangeLogsScanPackage("net.madicorp.smartinvestplus.config.dbmigrations");
        mongobee.setEnabled(true);
        return mongobee;
    }

    @Override
    protected String getDatabaseName() {
        return mongoDbFactory.getDb().getName();
    }

    @Override
    public Mongo mongo() throws Exception {
        return mongoDbFactory().getDb().getMongo();
    }

    private Mongo jongoMongo() throws UnknownHostException {
        return this.mongoProperties.createMongoClient(this.mongoOptions, this.environment);
    }
}
