package net.madicorp.smartinvestplus.config;

import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.springframework.stereotype.Component;

/**
 * User: sennen
 * Date: 03/07/2016
 * Time: 14:45
 */
@Component
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        property(ServerProperties.RESPONSE_SET_STATUS_OVER_SEND_ERROR, "true");
        registerEndpoints();
        registerLogging();
        registerValidationConfig();
    }

    private void registerEndpoints() {
        packages("net.madicorp.smartinvestplus");
    }

    private void registerLogging() {
        register(LoggingFeature.class);
        property(LoggingFeature.LOGGING_FEATURE_VERBOSITY, LoggingFeature.Verbosity.PAYLOAD_ANY);
        property(LoggingFeature.LOGGING_FEATURE_LOGGER_LEVEL, "INFO");
    }

    private void registerValidationConfig() {
        property(ServerProperties.BV_FEATURE_DISABLE, false);
        // Validation errors are not sent to the client.
        property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, false);
        // @ValidateOnExecution annotations on subclasses won't cause errors.
        property(ServerProperties.BV_DISABLE_VALIDATE_ON_EXECUTABLE_OVERRIDE_CHECK, true);
    }
}
