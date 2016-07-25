package net.madicorp.smartinvestplus.date;

import net.madicorp.smartinvestplus.stockexchange.service.StockExchangeService;
import net.madicorp.smartinvestplus.web.rest.HttpUtil;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * User: sennen
 * Date: 12/07/2016
 * Time: 20:24
 */
@Component
@Path("/api/stock-exchanges/{stock-exchange-symbol}/holidays/")
public class HolidaysResource {

    private static final DateTimeFormatter URI_DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;
    @Context
    private UriInfo uriInfo;

    @Inject
    private HttpUtil httpUtil;

    @Inject
    private StockExchangeService stockExchService;

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createHoliday(@PathParam("stock-exchange-symbol") String stockExchangeSymbol,
                                  LocalDate holiday) throws UnsupportedEncodingException {
        String formattedHoliday = holiday.format(URI_DATE_FORMATTER);
        StockExchangeHoliday stockExchangeHoliday =
            stockExchService.addHoliday(stockExchangeSymbol, holiday)
                            .orElseThrow(() -> badRequest(stockExchangeSymbol, formattedHoliday));
        URI createdUri = httpUtil.getUriBuilder(uriInfo).path("{holiday}").build(formattedHoliday);
        return Response.created(createdUri).entity(stockExchangeHoliday).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{holiday-date}")
    public StockExchangeHoliday createHoliday(@PathParam("stock-exchange-symbol") String stockExchangeSymbol,
                                              @PathParam("holiday-date") String formattedHoliday) throws UnsupportedEncodingException {
        LocalDate holiday = LocalDate.parse(formattedHoliday, URI_DATE_FORMATTER);
        Optional<StockExchangeHoliday> stockExchangeHoliday = stockExchService.getHoliday(stockExchangeSymbol, holiday);
        return stockExchangeHoliday.orElseThrow(NotFoundException::new);
    }

    private BadRequestException badRequest(String stockExchangeSymbol, String holiday) {
        String errorMsg = String.format("Holiday on '%s' already exists in stock exchange '%s'",
                                        stockExchangeSymbol, holiday);
        return new BadRequestException(errorMsg);
    }
}
