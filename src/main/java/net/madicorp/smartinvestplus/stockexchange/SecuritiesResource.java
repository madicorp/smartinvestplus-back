package net.madicorp.smartinvestplus.stockexchange;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

@Component
@Path("/api/stock-exchanges/{stock-exchange-symbol}/securities/")
public class SecuritiesResource {
    private final Logger log = LoggerFactory.getLogger(SecuritiesResource.class);

    @Context
    private UriInfo uriInfo;

    @Inject
    private StockExchangeService service;

    /**
     * GET  /api/stock-exchanges/{stock-exchange-symbol}/securities : Get all securities in a particular stock exchange
     * place.
     *
     * @return JSONArray containing each stock exchange and the links to its securities
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON_VALUE)
    public JSONArray getSecurities(@PathParam("stock-exchange-symbol") String stockExchangeSymbol) {
        log.debug("REST request to get securities in stock exchange '{}'", stockExchangeSymbol);
        StockExchangeWithSecurities stockExchange =
            service.getStockExchange(stockExchangeSymbol)
                   .orElseThrow(() -> new StockExchangeNotFoundException(stockExchangeSymbol));
        UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder().path("{securitySymbol}");
        return stockExchange.getSecurities()
                            .stream()
                            .map(security ->
                                     this.buildSecurityJSON(security, uriBuilder))
                            .collect(JSONArray::new,
                                     JSONArray::put,
                                     JSONArray::put);
    }

    /**
     * GET  /api/stock-exchanges/{stock-exchange-symbol}/securities/{security-symbol} : Get specific security.
     *
     * @return SecurityWithStockExchange security and the stock exchange it is located in.
     */
    @GET
    @Path("/{security-symbol}/")
    @Produces(MediaType.APPLICATION_JSON_VALUE)
    public SecurityWithStockExchange getSecurity(@PathParam("stock-exchange-symbol") String stockExchangeSymbol,
                                                 @PathParam("security-symbol") String securitySymbol) {
        log.debug("REST request to get security '{}' in stock exchange '{}'", securitySymbol, stockExchangeSymbol);
        return service.getSecurity(stockExchangeSymbol, securitySymbol)
                  .orElseThrow(() -> new StockExchangeNotFoundException(stockExchangeSymbol));
    }

    private JSONObject buildSecurityJSON(Security security, UriBuilder uriBuilder) {
        JSONObject jsonSecurity = new JSONObject(security);
        try {
            jsonSecurity.put("link", uriBuilder.build(security.getSymbol()).getPath());
        } catch (JSONException e) {
            String linkErrorMessage = String.format("Failed to parse link for security %s", security.getSymbol());
            throw new InternalServerErrorException(linkErrorMessage, e);
        }
        return jsonSecurity;
    }

//    @Path("/securities/{security-symbol}")
//    @GET
//    @Produces(MediaType.APPLICATION_JSON_VALUE)
//    @Secured(AuthoritiesConstants.ANONYMOUS)
//    public JSONObject getStockExchange(@PathParam("stock-exchange-symbol") String stockExchangeSymbol,
//                                       @PathParam("security-symbol") String securitySymbol) {
//        log.debug("REST request to get security '{}' in stock exchange '{}'", securitySymbol, stockExchangeSymbol);
//    }
}
