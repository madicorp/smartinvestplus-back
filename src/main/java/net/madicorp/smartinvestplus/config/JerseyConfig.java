package net.madicorp.smartinvestplus.config;

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
    }
}
