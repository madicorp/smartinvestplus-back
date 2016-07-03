package net.madicorp.smartinvestplus.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

/**
 * User: sennen
 * Date: 03/07/2016
 * Time: 23:17
 */
@Provider
public class JerseyMapperProvider implements ContextResolver<ObjectMapper> {
    @Autowired
    private Jackson2ObjectMapperBuilder objectMapperBuilder;

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return objectMapperBuilder.build();
    }
}
