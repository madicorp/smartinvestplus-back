package net.madicorp.smartinvestplus.web.rest;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.codahale.metrics.annotation.Timed;
import net.madicorp.smartinvestplus.web.rest.dto.LoggerDTO;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for view and managing Log Level at runtime.
 */
@Component
@Path("/management/jhipster")
public class LogsResource {

    @Inject
    private HttpUtil httpUtil;

    @Path("/logs")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    public List<LoggerDTO> getList() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        return context.getLoggerList()
                      .stream()
                      .map(LoggerDTO::new)
                      .collect(Collectors.toList());
    }

    @Path("/logs")
    @PUT
    @Timed
    public Response changeLevel(@RequestBody LoggerDTO jsonLogger) {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.getLogger(jsonLogger.getName()).setLevel(Level.valueOf(jsonLogger.getLevel()));
        return httpUtil.noContent();
    }
}
