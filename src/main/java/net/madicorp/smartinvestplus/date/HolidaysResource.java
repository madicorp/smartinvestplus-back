package net.madicorp.smartinvestplus.date;

import net.madicorp.smartinvestplus.stockexchange.resource.StockExchangeNotFoundException;
import net.madicorp.smartinvestplus.stockexchange.service.StockExchangeService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.*;
import java.time.LocalDate;

/**
 * User: sennen
 * Date: 12/07/2016
 * Time: 20:24
 */
@Component
@Path("/api/{stock-exchange-symbol}/holidays/")
public class HolidaysResource {

    @Inject
    private StockExchangeService stockExchService;

    @PUT
    @Consumes(MediaType.APPLICATION_JSON_VALUE)
    @Produces(MediaType.APPLICATION_JSON_VALUE)
    public StockExchangeHoliday createHoliday(@PathParam("stock-exchange-symbol") String stockExchangeSymbol,
                                              LocalDate holiday) {
        return stockExchService.addHoliday(stockExchangeSymbol, holiday)
                               .orElseThrow(() -> new StockExchangeNotFoundException(stockExchangeSymbol));
    }
}
