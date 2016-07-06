package net.madicorp.smartinvestplus.config;

import com.github.mongobee.Mongobee;
import com.mongodb.Mongo;
import com.mongodb.MongoClientOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import javax.inject.Inject;
import java.net.UnknownHostException;

/**
 * User: sennen
 * Date: 06/07/2016
 * Time: 00:41
 */
@Configuration
@Import(value = MongoAutoConfiguration.class)
public class MongobeeConfiguration {
    private final Logger log = LoggerFactory.getLogger(MongobeeConfiguration.class);

    @Inject
    private MongoProperties mongoProperties;

    @Autowired(required = false)
    private MongoClientOptions mongoOptions;

    @Autowired
    private Environment environment;

    @Bean
    public Mongobee mongobee() throws UnknownHostException {
        log.debug("Configuring Mongobee");
        Mongobee mongobee = new Mongobee(jongoMongo());
        mongobee.setDbName(mongoProperties.getDatabase());
        // package to scan for migrations
        mongobee.setChangeLogsScanPackage("net.madicorp.smartinvestplus.config.dbmigrations");
        mongobee.setEnabled(true);
        return mongobee;
    }

    @Bean
    @Qualifier("jongoMongo")
    public Mongo jongoMongo() throws UnknownHostException {
        return this.mongoProperties.createMongoClient(this.mongoOptions, this.environment);
    }
}
