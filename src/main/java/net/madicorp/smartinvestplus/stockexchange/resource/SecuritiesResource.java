package net.madicorp.smartinvestplus.stockexchange.resource;

import net.madicorp.smartinvestplus.domain.JSONHyperlinkBuilder;
import net.madicorp.smartinvestplus.stockexchange.domain.CloseRate;
import net.madicorp.smartinvestplus.stockexchange.domain.Security;
import net.madicorp.smartinvestplus.stockexchange.domain.SecurityWithStockExchange;
import net.madicorp.smartinvestplus.stockexchange.domain.StockExchangeWithSecurities;
import net.madicorp.smartinvestplus.stockexchange.service.CloseRateService;
import net.madicorp.smartinvestplus.stockexchange.service.IncompleteDataHistoryException;
import net.madicorp.smartinvestplus.stockexchange.service.StockExchangeService;
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
import java.time.LocalDate;
import java.util.Iterator;

@Component
@Path("/api/stock-exchanges/{stock-exchange-symbol}/securities/")
public class SecuritiesResource {
    private final Logger log = LoggerFactory.getLogger(SecuritiesResource.class);

    @Context
    private UriInfo uriInfo;

    @Inject
    private StockExchangeService stockExchangeService;

    @Inject
    private CloseRateService closeRateService;

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
            stockExchangeService.getStockExchange(stockExchangeSymbol)
                                .orElseThrow(() -> new StockExchangeNotFoundException(stockExchangeSymbol));
        UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder().path("{securitySymbol}");
        return stockExchange.getSecurities()
                            .stream()
                            .map(security -> this.buildSecurityJSON(security, uriBuilder))
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
        return stockExchangeService.getSecurity(stockExchangeSymbol, securitySymbol)
                                   .orElseThrow(() -> new StockExchangeNotFoundException(stockExchangeSymbol));
    }

    private JSONObject buildSecurityJSON(Security security, UriBuilder uriBuilder) {
        JSONObject jsonSecurity = new JSONObject(security);
        try {
            JSONArray links = new JSONArray();

            String securityHref = uriBuilder.build(security.getSymbol()).getPath();
            links.put(JSONHyperlinkBuilder.init("self", securityHref).build());

            String closeRatesHref = uriBuilder.path("close-rates").build(security.getSymbol()).getPath();
            links.put(JSONHyperlinkBuilder.init("close-rates", closeRatesHref).build());

            jsonSecurity.put("links", links);
        } catch (JSONException e) {
            String linkErrorMessage = String.format("Failed to format links for security %s", security.getSymbol());
            throw new InternalServerErrorException(linkErrorMessage, e);
        }
        return jsonSecurity;
    }

    @Path("/{security-symbol}/close-rates/")
    @GET
    @Produces(MediaType.APPLICATION_JSON_VALUE)
    public Iterator<CloseRate> getStockExchange(@PathParam("stock-exchange-symbol") String stockExchangeSymbol,
                                                @PathParam("security-symbol") String securitySymbol) {
        log.debug("REST request to get close rates one month to date for security '{}' in stock exchange '{}'",
                  securitySymbol, stockExchangeSymbol);
        try {
            Iterator<CloseRate> oneMonthToDateCloseRates =
                closeRateService.getOneMonthToDateCloseRates(stockExchangeSymbol, securitySymbol, LocalDate.now());
            closeRateService.saveGenerated(stockExchangeSymbol, securitySymbol, oneMonthToDateCloseRates);
            return oneMonthToDateCloseRates;
        } catch (IncompleteDataHistoryException e) {
            throw new BadRequestException(e);
        }
    }
}
