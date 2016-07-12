package net.madicorp.smartinvestplus.config;

import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

/**
 * User: sennen
 * Date: 03/07/2016
 * Time: 14:45
 */
@Component
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        registerEndpoints();
    }

    private void registerEndpoints() {
        packages("net.madicorp.smartinvestplus");
        register(LoggingFeature.class);
        property(LoggingFeature.LOGGING_FEATURE_VERBOSITY, LoggingFeature.Verbosity.PAYLOAD_ANY);
        property(LoggingFeature.LOGGING_FEATURE_LOGGER_LEVEL, "INFO");
    }
}
