package net.madicorp.smartinvestplus.stockexchange.resource;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * User: sennen
 * Date: 04/07/2016
 * Time: 23:47
 */
public class SecurityNotFoundException extends WebApplicationException {
    public SecurityNotFoundException(String stockExchangeSymbol, String securitySymbol) {
        super(SecurityNotFoundException.stockExchangeNotFound(stockExchangeSymbol, securitySymbol));
    }

    private static Response stockExchangeNotFound(String stockExchangeSymbol, String securitySymbol) {
        String notFoundMessage = String.format("Security '%s' in stock exchange '%s' has not been found",
                                               securitySymbol, stockExchangeSymbol);
        return Response.status(Response.Status.NOT_FOUND).entity(notFoundMessage).build();
    }
}
