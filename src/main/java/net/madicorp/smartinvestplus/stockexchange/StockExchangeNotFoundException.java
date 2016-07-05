package net.madicorp.smartinvestplus.stockexchange;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * User: sennen
 * Date: 04/07/2016
 * Time: 23:47
 */
public class StockExchangeNotFoundException extends WebApplicationException {
    public StockExchangeNotFoundException(String symbol) {
        super(StockExchangeNotFoundException.stockExchangeNotFound(symbol));
    }

    private static Response stockExchangeNotFound(String symbol) {
        String notFoundMessage = String.format("Stock exchange '%s' has not been found", symbol);
        return Response.status(Response.Status.NOT_FOUND).entity(notFoundMessage).build();
    }
}
