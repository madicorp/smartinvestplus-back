package net.madicorp.smartinvestplus.date;

import net.madicorp.smartinvestplus.stockexchange.service.StockExchangeService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * User: sennen
 * Date: 12/07/2016
 * Time: 20:24
 */
@Component
@Path("/api/stock-exchanges/{stock-exchange-symbol}/holidays/")
public class HolidaysResource {

    @Context
    private UriInfo uriInfo;

    @Inject
    private StockExchangeService stockExchService;

    @PUT
    @Consumes(MediaType.APPLICATION_JSON_VALUE)
    @Produces(MediaType.APPLICATION_JSON_VALUE)
    public Response createHoliday(@PathParam("stock-exchange-symbol") String stockExchangeSymbol,
                                  LocalDate holiday) throws UnsupportedEncodingException {
        String formattedHoliday = holiday.format(DateTimeFormatter.BASIC_ISO_DATE);
        StockExchangeHoliday stockExchangeHoliday =
            stockExchService.addHoliday(stockExchangeSymbol, holiday)
                            .orElseThrow(() -> badRequest(stockExchangeSymbol, formattedHoliday));
        URI createdUri = uriInfo.getAbsolutePathBuilder().path("{holiday}")
                                .build(formattedHoliday);
        return Response.created(createdUri).entity(stockExchangeHoliday).build();
    }

    private BadRequestException badRequest(String stockExchangeSymbol, String holiday) {
        String errorMsg = String.format("Holiday on '%s' already exists in stock exchange '%s'",
                                        stockExchangeSymbol, holiday);
        return new BadRequestException(errorMsg);
    }
}
