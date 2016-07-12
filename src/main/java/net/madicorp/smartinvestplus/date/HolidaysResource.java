package net.madicorp.smartinvestplus.date;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * User: sennen
 * Date: 12/07/2016
 * Time: 20:24
 */
@Component
@Path("/api/holidays/")
public class HolidaysResource {
    @POST
    @Consumes(MediaType.APPLICATION_JSON_VALUE)
    @Produces(MediaType.APPLICATION_JSON_VALUE)
    public void createHoliday() {

    }
}
