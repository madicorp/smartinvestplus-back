package net.madicorp.smartinvestplus.web.rest;

import net.madicorp.smartinvestplus.service.AuditEventService;
import net.madicorp.smartinvestplus.web.rest.util.PaginationUtil;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * REST controller for getting the audit events.
 */
@Component
@Path(value = "/management/jhipster/audits")
@Produces(MediaType.APPLICATION_JSON)
public class AuditResource {

    @Inject
    private HttpUtil httpUtil;

    @Inject
    private AuditEventService auditEventService;

    /**
     * GET  /audits : get a page of AuditEvents between the fromDate and toDate.
     *
     * @param from the start of the time period of AuditEvents to get
     * @param to   the end of the time period of AuditEvents to get
     * @param page the page to be retrieved
     * @param size number of elements to be retrieved
     * @return the Response with status 200 (OK) and the list of AuditEvents in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GET
    public Response getAll(@QueryParam("fromDate") String from, @QueryParam("toDate") String to,
                           @QueryParam("page") Integer page,
                           @QueryParam("size") Integer size) throws URISyntaxException {
        TrivialPage pageable = new TrivialPage(page, size);
        Page<AuditEvent> auditEventPage;
        if (from != null && to != null) {
            LocalDate fromDate = LocalDate.parse(from, DateTimeFormatter.BASIC_ISO_DATE);
            LocalDate toDate = LocalDate.parse(to, DateTimeFormatter.BASIC_ISO_DATE);
            auditEventPage = auditEventService.findByDates(fromDate.atTime(0, 0), toDate.atTime(23, 59), pageable);
        } else {
            auditEventPage = auditEventService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(auditEventPage, "/api/audits");
        return httpUtil.addHeaders(Response.ok(auditEventPage.getContent()), headers).build();
    }

    /**
     * GET  /audits/:id : get an AuditEvent by id.
     *
     * @param id the id of the entity to get
     * @return the Response with status 200 (OK) and the AuditEvent in body, or status 404 (Not Found)
     */
    @Path("/{id}")
    @GET
    public Response get(@PathParam("id") String id) {
        return auditEventService.find(id)
                                .map((entity) -> Response.ok(entity).build())
                                .orElse(httpUtil.notFound());
    }
}
