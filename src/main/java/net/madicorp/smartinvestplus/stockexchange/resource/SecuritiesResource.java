package net.madicorp.smartinvestplus.stockexchange.resource;

import net.madicorp.smartinvestplus.date.URIDate;
import net.madicorp.smartinvestplus.domain.JSONHyperlinkBuilder;
import net.madicorp.smartinvestplus.stockexchange.domain.*;
import net.madicorp.smartinvestplus.stockexchange.service.CloseRateService;
import net.madicorp.smartinvestplus.stockexchange.service.DivisionAlreadyExistsException;
import net.madicorp.smartinvestplus.stockexchange.service.IncompleteDataHistoryException;
import net.madicorp.smartinvestplus.stockexchange.service.StockExchangeService;
import net.madicorp.smartinvestplus.web.rest.ResourceUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

@Path("/api/stock-exchanges/{stock-exchange-symbol}/securities/")
public class SecuritiesResource {
    private static final DateTimeFormatter URI_DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;
    private final Logger log = LoggerFactory.getLogger(SecuritiesResource.class);

    @Context
    private UriInfo uriInfo;

    @Inject
    private ResourceUtil resourceUtil;

    @Inject
    private StockExchangeService stockExchService;

    @Inject
    private CloseRateService closeRateService;

    /**
     * GET  /api/stock-exchanges/{stock-exchange-symbol}/securities : Get all securities in a particular stock exchange
     * place.
     *
     * @return JSONArray containing each stock exchange and the links to its securities
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JSONArray getSecurities(@PathParam("stock-exchange-symbol") @Symbol String stockExchangeSymbol) {
        log.debug("REST request to get securities in stock exchange '{}'", stockExchangeSymbol);
        final String upperStockExchangeSymbol = stockExchangeSymbol.toUpperCase();
        StockExchangeWithSecurities stockExchange =
            stockExchService.getStockExchange(upperStockExchangeSymbol)
                            .orElseThrow(() -> new StockExchangeNotFoundException(upperStockExchangeSymbol));
        UriBuilder uriBuilder = resourceUtil.getUriBuilder(uriInfo).path("{securitySymbol}");
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
    @Produces(MediaType.APPLICATION_JSON)
    public SecurityWithStockExchange getSecurity(@PathParam("stock-exchange-symbol") @Symbol String stockExchangeSymbol,
                                                 @PathParam("security-symbol") @Symbol String securitySymbol) {
        log.debug("REST request to get security '{}' in stock exchange '{}'", securitySymbol, stockExchangeSymbol);
        return getSecurityWithStockExchange(stockExchangeSymbol.toUpperCase(), securitySymbol.toUpperCase());
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
    @Produces(MediaType.APPLICATION_JSON)
    public Iterator<CloseRate> getStockExchange(@PathParam("stock-exchange-symbol") @Symbol String stockExchangeSymbol,
                                                @PathParam("security-symbol") @Symbol String securitySymbol) {

        stockExchangeSymbol = stockExchangeSymbol.toUpperCase();
        securitySymbol = securitySymbol.toUpperCase();
        log.debug("REST request to get close rates one month to date for security '{}' in stock exchange '{}'",
                  securitySymbol, stockExchangeSymbol);
        try {
            Iterator<CloseRate> oneMonthToDateCloseRates =
                closeRateService.getOneMonthToDateCloseRates(stockExchangeSymbol, securitySymbol, LocalDate.now());
            closeRateService.saveGenerated(oneMonthToDateCloseRates);
            return oneMonthToDateCloseRates;
        } catch (IncompleteDataHistoryException e) {
            throw new BadRequestException(e);
        }
    }

    @Path("/{security-symbol}/divisions/")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@PathParam("stock-exchange-symbol") @Symbol String stockExchangeSymbol,
                           @PathParam("security-symbol") @Symbol String securitySymbol,
                           Division division) {
        stockExchangeSymbol = stockExchangeSymbol.toUpperCase();
        securitySymbol = securitySymbol.toUpperCase();
        LocalDate divisionDate = division.getDate();
        log.debug(
            "REST request to create close rates division for security '{}' in stock exchange '{}' at '{}' to apply {} rate",
            securitySymbol, stockExchangeSymbol, divisionDate.format(DateTimeFormatter.ISO_DATE),
            division.getRate());
        SecurityWithStockExchange security = getSecurityWithStockExchange(stockExchangeSymbol, securitySymbol);
        try {
            URI divisionUri = resourceUtil.getUriBuilder(uriInfo)
                                          .path("{division-date}")
                                          .build(divisionDate.format(URI_DATE_FORMATTER));
            return Response.created(divisionUri)
                           .entity(stockExchService.addDivision(security, division))
                           .build();
        } catch (DivisionAlreadyExistsException e) {
            throw new BadRequestException("Should not add a division that already exists");
        }
    }

    @Path("/{security-symbol}/divisions/{division-date}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Division create(@PathParam("stock-exchange-symbol") @Symbol String stockExchangeSymbol,
                           @PathParam("security-symbol") @Symbol String securitySymbol,
                           @PathParam("division-date") @URIDate String formattedDivisionDate) {
        stockExchangeSymbol = stockExchangeSymbol.toUpperCase();
        securitySymbol = securitySymbol.toUpperCase();
        LocalDate divisionDate = LocalDate.parse(formattedDivisionDate, URI_DATE_FORMATTER);
        return stockExchService.getDivision(stockExchangeSymbol, securitySymbol, divisionDate)
                               .orElseThrow(NotFoundException::new);
    }

    private SecurityWithStockExchange getSecurityWithStockExchange(String stockExchangeSymbol, String securitySymbol) {
        return stockExchService.getSecurity(stockExchangeSymbol, securitySymbol)
                               .orElseThrow(() -> new SecurityNotFoundException(stockExchangeSymbol, securitySymbol));
    }
}
